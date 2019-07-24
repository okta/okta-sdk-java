/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.ds;

import com.okta.commons.http.DefaultRequest;
import com.okta.commons.http.HttpHeaders;
import com.okta.commons.http.HttpMethod;
import com.okta.commons.http.MediaType;
import com.okta.commons.http.QueryString;
import com.okta.commons.http.Request;
import com.okta.commons.http.RequestExecutor;
import com.okta.commons.http.Response;
import com.okta.commons.http.config.BaseUrlResolver;
import com.okta.commons.lang.ApplicationInfo;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.ds.DataStore;
import com.okta.sdk.ds.RequestBuilder;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.impl.cache.DisabledCacheManager;
import com.okta.sdk.impl.ds.cache.CacheResolver;
import com.okta.sdk.impl.ds.cache.DefaultCacheResolver;
import com.okta.sdk.impl.ds.cache.DefaultResourceCacheStrategy;
import com.okta.sdk.impl.ds.cache.ReadCacheFilter;
import com.okta.sdk.impl.ds.cache.ResourceCacheStrategy;
import com.okta.sdk.impl.ds.cache.WriteCacheFilter;
import com.okta.sdk.impl.error.DefaultError;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.http.HttpHeadersHolder;
import com.okta.sdk.impl.http.support.DefaultCanonicalUri;
import com.okta.sdk.impl.resource.AbstractInstanceResource;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.HalResourceHrefResolver;
import com.okta.sdk.impl.resource.ReferenceFactory;
import com.okta.sdk.impl.util.DefaultBaseUrlResolver;
import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.VoidResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.okta.commons.http.HttpHeaders.OKTA_USER_AGENT;
import static com.okta.commons.http.HttpHeaders.USER_AGENT;
import static com.okta.commons.http.HttpHeaders.OKTA_AGENT;
import static com.okta.commons.http.HttpHeaders.OKTA_CLIENT_REQUEST_ID;

/**
 * @since 0.5.0
 */
public class DefaultDataStore implements InternalDataStore {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataStore.class);
    private static final Logger requestLog = LoggerFactory.getLogger(DataStore.class.getName() + "-request");

    private static final String HREF_REQD_MSG = "'save' may only be called on objects that have already been " +
                                               "persisted and have an existing 'href' attribute.";

    private final RequestExecutor requestExecutor;
    private final ResourceFactory resourceFactory;
    private final MapMarshaller mapMarshaller;
    private final CacheManager cacheManager;
    private final ResourceConverter resourceConverter;
    private final List<Filter> filters;
    private final ClientCredentialsResolver clientCredentialsResolver;
    private final BaseUrlResolver baseUrlResolver;

    private static final String USER_AGENT_STRING = ApplicationInfo.get().entrySet().stream()
            .map(e -> e.getKey() + "/" + e.getValue())
            .collect(Collectors.joining(" "));

    public DefaultDataStore(RequestExecutor requestExecutor, String baseUrl, ClientCredentialsResolver clientCredentialsResolver) {
        this(requestExecutor, new DefaultBaseUrlResolver(baseUrl), clientCredentialsResolver, new DisabledCacheManager());
    }

    public DefaultDataStore(RequestExecutor requestExecutor, BaseUrlResolver baseUrlResolver, ClientCredentialsResolver clientCredentialsResolver, CacheManager cacheManager) {
        Assert.notNull(baseUrlResolver, "baseUrlResolver cannot be null");
        Assert.notNull(requestExecutor, "RequestExecutor cannot be null.");
        Assert.notNull(clientCredentialsResolver, "clientCredentialsResolver cannot be null.");
        Assert.notNull(cacheManager, "CacheManager cannot be null.  Use the DisabledCacheManager if you wish to turn off caching.");
        this.requestExecutor = requestExecutor;
        this.baseUrlResolver = baseUrlResolver;
        this.cacheManager = cacheManager;
        this.resourceFactory = new DefaultResourceFactory(this);
        this.mapMarshaller = new JacksonMapMarshaller();
        CacheResolver cacheResolver = new DefaultCacheResolver(this.cacheManager, new DefaultCacheRegionNameResolver());
        this.clientCredentialsResolver = clientCredentialsResolver;

        ReferenceFactory referenceFactory = new ReferenceFactory();
        this.resourceConverter = new DefaultResourceConverter(referenceFactory);

        this.filters = new ArrayList<>();

        if (isCachingEnabled()) {
            ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver);
            this.filters.add(new ReadCacheFilter(cacheStrategy));
            this.filters.add(new WriteCacheFilter(cacheStrategy));
        }
    }

    @Override
    public ClientCredentials getClientCredentials() {
        return this.clientCredentialsResolver.getClientCredentials();
    }

    @Override
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    /* =====================================================================
       Resource Instantiation
       ===================================================================== */

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.resourceFactory.instantiate(clazz);
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Map<String, Object> properties) {
        return this.resourceFactory.instantiate(clazz, properties);
    }

    private <T extends Resource> T instantiate(Class<T> clazz, Map<String, ?> properties, QueryString qs) {
        if (CollectionResource.class.isAssignableFrom(clazz)) {
            //only collections can support a query string constructor argument:
            return this.resourceFactory.instantiate(clazz, properties, qs);
        }
        //otherwise it must be an instance resource, so use the two-arg constructor:
        return this.resourceFactory.instantiate(clazz, properties);
    }

    /* =====================================================================
       Resource Retrieval
       ===================================================================== */

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return getResource(href, clazz, null);
    }

    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters) {
        return getResource(href, clazz, queryParameters, null);
    }

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters, Map<String, List<String>> headerParameters) {
        ResourceDataResult result = getResourceData(href, clazz, queryParameters, headers(headerParameters));
        T resource = instantiate(clazz, result.getData(), result.getUri().getQuery());

        resource.setResourceHref(href);

        return resource;
    }

    @SuppressWarnings("unchecked")
    private ResourceDataResult getResourceData(String href, Class<? extends Resource> clazz, Map<String,?> queryParameters, HttpHeaders httpHeaders) {

        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Resource class argument cannot be null.");

        FilterChain chain = new DefaultFilterChain(this.filters, req -> {

            CanonicalUri uri = req.getUri();

            Request getRequest = new DefaultRequest(HttpMethod.GET, uri.getAbsolutePath(), uri.getQuery(), req.getHttpHeaders());
            Response getResponse = execute(getRequest);
            Map<String,?> body = getBody(getResponse);

            if (Collections.isEmpty(body)) {
                throw new IllegalStateException("Unable to obtain resource data from the API server or from cache.");
            }

            return new DefaultResourceDataResult(req.getAction(), uri, req.getResourceClass(), (Map<String,Object>)body);
        });

        CanonicalUri uri = canonicalize(href, queryParameters);
        ResourceDataRequest req = new DefaultResourceDataRequest(ResourceAction.READ, uri, clazz, new HashMap<>(), httpHeaders);
        return chain.filter(req);
    }

    private ResourceAction getPostAction(ResourceDataRequest request, Response response) {
        int httpStatus = response.getHttpStatus();
        if (httpStatus == 201) {
            return ResourceAction.CREATE;
        }
        // TODO: verify this is an issue with Okta
        //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/403
        if (httpStatus == 200) {
            return ResourceAction.READ;
        }

        return request.getAction();
    }

    /* =====================================================================
       Resource Persistence
       ===================================================================== */

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T create(String parentHref, T resource, T parentResource) {
        return (T)create(parentHref, resource, parentResource, resource.getClass());
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType) {
        return create(parentHref, resource, parentResource, returnType, null);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType, Map<String, Object> queryParameters) {
        return create(parentHref, resource, parentResource, returnType, queryParameters, null);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, T parentResource, Class<? extends R> returnType, Map<String, Object> queryParameters, Map<String, List<String>> headerParameters) {
        return save(parentHref, resource, parentResource, headers(headerParameters), returnType,  new QueryString(queryParameters), true);
    }

    @Override
    public <T extends Resource> void save(T resource) {
        save(resource.getResourceHref(), resource);
    }

    @Override
    public <T extends Resource> void save(String href, T resource, T parentResource) {
        save(href, resource, parentResource, null);
    }

    @Override
    public <T extends Resource> void save(String href, T resource, T parentResource, Map<String, Object> queryParameters) {
        save(href, resource, parentResource, queryParameters, null);
    }

    @Override
    public <T extends Resource> void save(String href, T resource, T parentResource, Map<String, Object> queryParameters, Map<String, List<String>> headerParameters) {
        Assert.hasText(href, HREF_REQD_MSG);
        save(href, resource, parentResource, headers(headerParameters), getResourceClass(resource), new QueryString(queryParameters), false);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, R extends Resource> R save(String href,
                                                            final T resource,
                                                            final T parentResource,
                                                            HttpHeaders requestHeaders,
                                                            final Class<? extends R> returnType,
                                                            final QueryString qs,
                                                            final boolean create) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.isTrue(!CollectionResource.class.isAssignableFrom(resource.getClass()), "Collections cannot be persisted.");

        final CanonicalUri uri = canonicalize(href, qs);
        final AbstractResource abstractResource = (AbstractResource) resource;
        // Most Okta endpoints do not support partial update, we can revisit in the future.
        final Map<String, Object> props = resourceConverter.convert(abstractResource, false);

        FilterChain chain = new DefaultFilterChain(this.filters, req -> {

            CanonicalUri uri1 = req.getUri();
            String href1 = uri1.getAbsolutePath();
            QueryString qs1 = uri1.getQuery();

            HttpHeaders httpHeaders = req.getHttpHeaders();

            // create == POST
            HttpMethod method = HttpMethod.POST;
            if (!create) {
                method = HttpMethod.PUT;
            }

            InputStream body;
            long length = 0;
            if (resource instanceof VoidResource) {
                body = new ByteArrayInputStream(new byte[0]);
            } else {
                ByteArrayOutputStream bodyOut = new ByteArrayOutputStream();
                mapMarshaller.marshal(bodyOut, req.getData());
                body = new ByteArrayInputStream(bodyOut.toByteArray());
                length = bodyOut.size();
            }

            Request request = new DefaultRequest(method, href1, qs1, httpHeaders, body, length);

            Response response = execute(request);
            Map<String, Object> responseBody = getBody(response);

            if (Collections.isEmpty(responseBody)) {
                // Fix for https://github.com/stormpath/stormpath-sdk-java/issues/218
                // Okta response with 200 for deactivate requests (i.e. /api/v1/apps/<id>/lifecycle/deactivate)
                if (response.getHttpStatus() == 202
                        || response.getHttpStatus() == 200
                        || response.getHttpStatus() == 204) {
                    //202 means that the request has been accepted for processing, but the processing has not been completed. Therefore we do not have a response setBody.
                    responseBody = java.util.Collections.emptyMap();
                } else {
                    throw new IllegalStateException("Unable to obtain resource data from the API server.");
                }
            }

            ResourceAction responseAction = getPostAction(req, response);

            return new DefaultResourceDataResult(responseAction, uri1, returnType, responseBody);
        });

        ResourceAction action = create ? ResourceAction.CREATE : ResourceAction.UPDATE;

        ResourceDataRequest request = new DefaultResourceDataRequest(
                action,
                uri,
                canonicalizeParent(parentResource),
                returnType,
                getResourceClass(parentResource),
                props,
                requestHeaders);

        ResourceDataResult result = chain.filter(request);

        Map<String,Object> data = result.getData();

        //ensure the caller's argument is updated with what is returned from the server if the types are the same:
        if (returnType.isAssignableFrom(abstractResource.getClass())) {
            abstractResource.setInternalProperties(data);
        }

        return resourceFactory.instantiate(returnType, data);
    }

    /* =====================================================================
       Resource Deletion
       ===================================================================== */

    @Override
    public void delete(String href) {
        delete(href, (Map<String, Object>) null);
    }

    @Override
    public void delete(String href, Map<String, Object> queryParameters) {
        delete(href, queryParameters, null);
    }

    @Override
    public void delete(String href, Map<String, Object> queryParameters, Map<String, List<String>> headerParameters) {
        doDelete(href, VoidResource.class, new QueryString(queryParameters), headers(headerParameters));
    }

    @Override
    public <T extends Resource> void delete(String href, T resource, Map<String, Object> queryParameters, Map<String, List<String>> headerParameters) {
        doDelete(href != null ? href : resource.getResourceHref(), getResourceClass(resource), new QueryString(queryParameters), headers(headerParameters));
    }

    @Override
    public <T extends Resource> void delete(T resource) {
        doDelete(resource.getResourceHref(), resource);
    }

    @Override
    public <T extends Resource> void delete(String href, T resource) {
        doDelete(href, resource);
    }

    private void doDelete(String resourceHref, Class resourceClass, QueryString qs, HttpHeaders httpHeaders) {

        Assert.hasText(resourceHref, "This resource does not have an href value, therefore it cannot be deleted.");

        // if this URL is a partial, then we MUST add the baseUrl
        final CanonicalUri uri = canonicalize(resourceHref, qs);

        FilterChain chain = new DefaultFilterChain(this.filters, request -> {
            Request deleteRequest = new DefaultRequest(HttpMethod.DELETE, uri.getAbsolutePath(), uri.getQuery(), httpHeaders);
            execute(deleteRequest);
            //delete requests have HTTP 204 (no content), so just create an empty setBody for the result:
            return new DefaultResourceDataResult(request.getAction(), request.getUri(), request.getResourceClass(), new HashMap<>());
        });

        final CanonicalUri resourceUri = canonicalize(resourceHref, null);
        ResourceDataRequest request = new DefaultResourceDataRequest(ResourceAction.DELETE, resourceUri, resourceClass, new HashMap<>());
        chain.filter(request);
    }

    private <T extends Resource> void doDelete(String href, T resource) {

        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource, "Resource argument must be an AbstractResource.");

        doDelete(href != null ? href : resource.getResourceHref(), getResourceClass(resource), null, null);
    }

    /* =====================================================================
       Resource Caching
       ===================================================================== */

    protected boolean isCachingEnabled() {
        return this.cacheManager != null && !(this.cacheManager instanceof DisabledCacheManager);
    }

    /**
     * @return the Base URL (i.e. https://api.stormpaht.com/v1) at which the current SDK instance is connected to.

     */
    @Override
    public String getBaseUrl() {
        return this.baseUrlResolver.getBaseUrl();
    }

    private Response execute(Request request) throws ResourceException {

        applyDefaultRequestHeaders(request);

        Response response = this.requestExecutor.executeRequest(request);
        log.trace("Executed HTTP request.");

        if (requestLog.isTraceEnabled()) {
            requestLog.trace("Executing request: method: '{}', url: {}", request.getMethod(), request.getResourceUrl());
        }

        if (response.isError()) {
            Map<String, Object> body = getBody(response);

            String requestId = response.getHeaders().getOktaRequestId();

            if (Strings.hasText(requestId)) {
                body.put(DefaultError.ERROR_ID.getName(), requestId);
            }

            throw new ResourceException(new DefaultError(body)
                                            .setHeaders(response.getHeaders().getXHeaders())
                                            .setStatus(response.getHttpStatus()));
        }

        return response;
    }

    private Map<String, Object> getBody(Response response) {

        Assert.notNull(response, "response argument cannot be null.");

        Map<String, Object> out = null;

        if (response.hasBody()) {
            out = mapMarshaller.unmarshal(response.getBody(), response.getHeaders().getLinkMap());
        }

        return out;
    }

    protected void applyDefaultRequestHeaders(Request request) {
        request.getHeaders().setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

        // Get runtime headers from http client
        Map<String, List<String>> headerMap = HttpHeadersHolder.get();
        String oktaAgentHeaderName = OKTA_AGENT.toLowerCase(Locale.ENGLISH);
        if (headerMap != null && headerMap.get(oktaAgentHeaderName) != null) {
            List<String> oktaAgents = headerMap.get(oktaAgentHeaderName);
            if (oktaAgents != null && !oktaAgents.isEmpty()) {
                String oktaAgent = Strings.arrayToDelimitedString(oktaAgents.toArray(), " ");
                applyUserAgent(oktaAgent + " " + USER_AGENT_STRING, request);
            }
        } else {
            applyUserAgent(USER_AGENT_STRING, request);
        }

        if (request.getHeaders().getContentType() == null && request.getBody() != null) {
            // We only add the default content type (application/json) if a content type is not already in the request
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }

        List<String> clientRequestId;
        if (headerMap != null && (clientRequestId = headerMap.get(OKTA_CLIENT_REQUEST_ID)) != null) {
            request.getHeaders().put(OKTA_CLIENT_REQUEST_ID, clientRequestId);
        }
    }

    private void applyUserAgent(String userAgentString, Request request) {

        if (request.getHeaders().containsKey(USER_AGENT)) {
            request.getHeaders().set(OKTA_USER_AGENT, userAgentString);
        } else {
            request.getHeaders().set(USER_AGENT, userAgentString);
        }
    }

    private CanonicalUri canonicalize(String href, Map<String,?> queryParams) {
        href = ensureFullyQualified(href);
        return DefaultCanonicalUri.create(href, queryParams);
    }

    private CanonicalUri canonicalizeParent(Resource parentResource) {
        if (parentResource != null && parentResource.getResourceHref() != null) {
            String href = ensureFullyQualified(parentResource.getResourceHref());
            return DefaultCanonicalUri.create(href, null);
        }
        return null;
    }

    private String ensureFullyQualified(String href) {
        String value = href;
        if (!isFullyQualified(href)) {
            value = qualify(href);
        }
        return value;
    }

    private boolean isFullyQualified(String href) {

        if (href == null || href.length() < 5) {
            return false;
        }
        return href.regionMatches(true, 0, "http", 0, 4);
    }

    private String qualify(String href) {
        StringBuilder sb = new StringBuilder(this.baseUrlResolver.getBaseUrl());
        if (!href.startsWith("/")) {
            sb.append("/");
        }
        sb.append(href);
        return sb.toString();
    }

    private Class<? extends Resource> getResourceClass(Resource resource) {
        if (AbstractInstanceResource.class.isInstance(resource)) {
            return ((AbstractInstanceResource)resource).getResourceClass();
        }
        return resource != null ? resource.getClass() : null;
    }

    private HttpHeaders headers(Map<String, List<String>> headerParameters) {
        if (!Collections.isEmpty(headerParameters)) {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(headerParameters);
            return headers;
        }
        return null;
    }

    @Override
    public RequestBuilder http() {
        return new DefaultRequestBuilder(this);
    }
}
/*
 * Copyright 2014 Stormpath, Inc.
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

import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.ds.DataStore;
import com.okta.sdk.http.HttpMethod;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.cache.DisabledCacheManager;
import com.okta.sdk.impl.ds.cache.CacheResolver;
import com.okta.sdk.impl.ds.cache.DefaultCacheResolver;
import com.okta.sdk.impl.ds.cache.ReadCacheFilter;
import com.okta.sdk.impl.ds.cache.WriteCacheFilter;
import com.okta.sdk.impl.error.DefaultError;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.http.HttpHeaders;
import com.okta.sdk.impl.http.HttpHeadersHolder;
import com.okta.sdk.impl.http.MediaType;
import com.okta.sdk.impl.http.QueryString;
import com.okta.sdk.impl.http.QueryStringFactory;
import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.RequestExecutor;
import com.okta.sdk.impl.http.Response;
import com.okta.sdk.impl.http.support.DefaultCanonicalUri;
import com.okta.sdk.impl.http.support.DefaultRequest;
import com.okta.sdk.impl.http.support.UserAgent;
import com.okta.sdk.impl.query.DefaultCriteria;
import com.okta.sdk.impl.query.DefaultOptions;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.ReferenceFactory;
import com.okta.sdk.resource.VoidResource;
import com.okta.sdk.impl.util.BaseUrlResolver;
import com.okta.sdk.impl.util.DefaultBaseUrlResolver;
import com.okta.sdk.impl.util.StringInputStream;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.lang.Strings;
import com.okta.sdk.query.Criteria;
import com.okta.sdk.query.Options;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;
import com.okta.sdk.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import static com.okta.sdk.impl.http.HttpHeaders.OKTA_AGENT;
import static com.okta.sdk.impl.http.HttpHeaders.OKTA_CLIENT_REQUEST_ID;

/**
 * @since 0.5.0
 */
public class DefaultDataStore implements InternalDataStore {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataStore.class);
    private static final Logger requestLog = LoggerFactory.getLogger(DataStore.class.getName() + "-request");

    public static final String DEFAULT_SERVER_HOST = "api.okta.com";

    public static final int DEFAULT_API_VERSION = 1;

    private static final String APPEND_PARAM_CHAR = "&";

    public static final String DEFAULT_CRITERIA_MSG = "The " + DefaultDataStore.class.getName() +
                                                      " implementation only functions with " +
                                                      DefaultCriteria.class.getName() + " instances.";

    public static final String DEFAULT_OPTIONS_MSG = "The " + DefaultDataStore.class.getName() +
                                                     " implementation only functions with " +
                                                     DefaultOptions.class.getName() + " instances.";

    public static final String HREF_REQD_MSG = "'save' may only be called on objects that have already been " +
                                               "persisted and have an existing 'href' attribute.";

    private static final boolean COLLECTION_CACHING_ENABLED = false; //EXPERIMENTAL - set to true only while developing.

    private final RequestExecutor requestExecutor;
    private final ResourceFactory resourceFactory;
    private final MapMarshaller mapMarshaller;
    private final CacheManager cacheManager;
    private final CacheResolver cacheResolver;
    private final ResourceConverter resourceConverter;
    private final QueryStringFactory queryStringFactory;
    private final List<Filter> filters;
    private final ClientCredentialsResolver clientCredentialsResolver;
    private final BaseUrlResolver baseUrlResolver;

    public static final String USER_AGENT_STRING = UserAgent.getUserAgentString();

    public DefaultDataStore(RequestExecutor requestExecutor, ClientCredentialsResolver clientCredentialsResolver) {
        this(requestExecutor, DEFAULT_API_VERSION, clientCredentialsResolver);
    }

    public DefaultDataStore(RequestExecutor requestExecutor, int apiVersion, ClientCredentialsResolver clientCredentialsResolver) {
        this(requestExecutor, "https://" + DEFAULT_SERVER_HOST + "/v" + apiVersion, clientCredentialsResolver);
    }

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
        this.queryStringFactory = new QueryStringFactory();
        this.cacheResolver = new DefaultCacheResolver(this.cacheManager, new DefaultCacheRegionNameResolver());
        this.clientCredentialsResolver = clientCredentialsResolver;

        ReferenceFactory referenceFactory = new ReferenceFactory();
        this.resourceConverter = new DefaultResourceConverter(referenceFactory);

        this.filters = new ArrayList<Filter>();

//        this.filters.add(new EnlistmentFilter()); // FIXME: cannot support this yet

//        if(clientCredentials instanceof ApiKeyCredentials) { // FIXME: add this back in
//            this.filters.add(new DecryptApiKeySecretFilter((ApiKeyCredentials) clientCredentials));
//        }

        if (isCachingEnabled()) {
            this.filters.add(new ReadCacheFilter(this.baseUrlResolver, this.cacheResolver, COLLECTION_CACHING_ENABLED));
            this.filters.add(new WriteCacheFilter(this.baseUrlResolver, this.cacheResolver, COLLECTION_CACHING_ENABLED, referenceFactory));
        }

        this.filters.add(new ProviderAccountResultFilter());
    }

    @Override
    public CacheResolver getCacheResolver() {
        return this.cacheResolver;
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

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Map<String, Object> properties, boolean hrefFragment) {
        if (hrefFragment) {
            Assert.hasText((String) properties.get("href"), "when hrefFragment is set to true the properties map must contain an href key.");
            String hrefValue = (String) properties.get("href");
            hrefValue = qualify(hrefValue);
            properties.put("href", hrefValue);
        }
        return this.instantiate(clazz, properties);
    }

    /* =====================================================================
       Resource Retrieval
       ===================================================================== */

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return getResource(href, clazz, (Map<String, Object>) null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Criteria criteria) {
        Assert.isInstanceOf(DefaultCriteria.class, criteria, DEFAULT_CRITERIA_MSG);
        QueryString qs = queryStringFactory.createQueryString(href, (DefaultCriteria)criteria);
        return (T) getResource(href, clazz, (Map) qs);
    }

    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters) {
        ResourceDataResult result = getResourceData(href, clazz, queryParameters);
        T resource = instantiate(clazz, result.getData(), result.getUri().getQuery());

        resource.setResourceHref(href);

        return resource;

    }

    /**
     * This method provides the ability to instruct the DataStore how to decide which class of a resource hierarchy will
     * be instantiated.
     *
     * @param href            the endpoint where the request will be targeted to.
     * @param parent          the root class of the Resource hierarchy (helps to validate that the idClassMap contains
     *                        subclasses of it).
     * @param childIdProperty the property whose value will be used to identify the specific class in the hierarchy that
     *                        we need to instantiate.
     * @param idClassMap      a mapping to be able to know which class corresponds to each <code>childIdProperty</code>
     *                        value.
     * @param <T>             the root of the hierarchy of the Resource we want to instantiate.
     * @param <R>             the sub-class of the root Resource.
     * @return the retrieved resource
     */
    @Override
    public <T extends Resource, R extends T> R getResource(String href, Class<T> parent, String childIdProperty,
                                                           Map<String, Class<? extends R>> idClassMap) {
        Assert.hasText(childIdProperty, "childIdProperty cannot be null or empty.");
        Assert.notEmpty(idClassMap, "idClassMap cannot be null or empty.");

        ResourceDataResult result = getResourceData(href, parent, null);
        Map<String, ?> data = result.getData();

        if (Collections.isEmpty(data) || !data.containsKey(childIdProperty)) {
            throw new IllegalStateException(childIdProperty + " could not be found in: " + data + ".");
        }

        Object propertyValue = data.get(childIdProperty);
        if (propertyValue == null) {
            throw new IllegalStateException("No Class mapping could be found for " + childIdProperty + ".");
        }

        Class<? extends R> childClass = idClassMap.get(propertyValue.toString());

        return instantiate(childClass, data, result.getUri().getQuery());
    }

    @Override
    public <T extends Resource, O extends Options> T getResource(String href, Class<T> clazz, O options) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Resource class argument cannot be null.");
        Assert.isInstanceOf(DefaultOptions.class, options, "The " + getClass().getName() + " implementation only functions with " +
                DefaultOptions.class.getName() + " instances.");
        DefaultOptions defaultOptions = (DefaultOptions) options;
        QueryString qs = queryStringFactory.createQueryString(defaultOptions);
        return (T) getResource(href, clazz, (Map) qs);
    }

    @SuppressWarnings("unchecked")
    private ResourceDataResult getResourceData(String href, Class<? extends Resource> clazz, Map<String,?> queryParameters) {

        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Resource class argument cannot be null.");

        FilterChain chain = new DefaultFilterChain(this.filters, new FilterChain() {
            @Override
            public ResourceDataResult filter(final ResourceDataRequest req) {

                CanonicalUri uri = req.getUri();

                Request getRequest = new DefaultRequest(HttpMethod.GET, uri.getAbsolutePath(), uri.getQuery());
                Response getResponse = execute(getRequest);
                Map<String,?> body = getBody(getResponse);

                if (Collections.isEmpty(body)) {
                    throw new IllegalStateException("Unable to obtain resource data from the API server or from cache.");
                }

                return new DefaultResourceDataResult(req.getAction(), uri, req.getResourceClass(), (Map<String,Object>)body);
            }
        });

        CanonicalUri uri = canonicalize(href, queryParameters);
        ResourceDataRequest req = new DefaultResourceDataRequest(ResourceAction.READ, uri, clazz, new HashMap<String,Object>());
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
    public <T extends Resource> T create(String parentHref, T resource) {
        return (T)save(parentHref, resource, null, resource.getClass(), null, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T create(String parentHref, T resource, Options options) {
        QueryString qs = toQueryString(parentHref, options);
        return (T)save(parentHref, resource, null, resource.getClass(), qs, true);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType) {
        return save(parentHref, resource, null, returnType, null, true);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType, HttpHeaders requestHeaders) {
        return save(parentHref, resource, requestHeaders, returnType, null, true);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType, Options options) {
        QueryString qs = toQueryString(parentHref, options);
        return save(parentHref, resource, null, returnType, qs, true);
    }

    @Override
    public <T extends Resource> void save(T resource) {
        save(resource.getResourceHref(), resource);
    }

    @Override
    public <T extends Resource> void save(String href, T resource) {
        Assert.hasText(href, HREF_REQD_MSG);
        save(href, resource, null, resource.getClass(), null, false);
    }

    @Override
    public <T extends Resource> void save(T resource, Options options) {
        Assert.notNull(options, "options argument cannot be null.");
        String href = resource.getResourceHref();
        Assert.hasText(href, HREF_REQD_MSG);
        QueryString qs = toQueryString(href, options);
        save(href, resource, null, resource.getClass(), qs, false);
    }

    @Override
    public <T extends Resource, R extends Resource> R save(T resource, Class<? extends R> returnType) {
        Assert.hasText(resource.getResourceHref(), HREF_REQD_MSG);
        return save(resource.getResourceHref(), resource, null, returnType, null, false);
    }

    private QueryString toQueryString(String href, Options options) {
        if (options == null) {
            return null;
        }
        Assert.isInstanceOf(DefaultOptions.class, options, DEFAULT_OPTIONS_MSG);
        DefaultOptions defaultOptions = (DefaultOptions)options;
        return queryStringFactory.createQueryString(href, defaultOptions);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, R extends Resource> R save(String href, final T resource, HttpHeaders requestHeaders, final Class<? extends R> returnType, final QueryString qs, final boolean create) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.isTrue(!CollectionResource.class.isAssignableFrom(resource.getClass()), "Collections cannot be persisted.");

        final CanonicalUri uri = canonicalize(href, qs);
        final AbstractResource abstractResource = (AbstractResource) resource;
        final Map<String, Object> props = resourceConverter.convert(abstractResource, false); // TODO: Okta doesn't have great support for updating just dirty props so always include clean and dirty props

        FilterChain chain = new DefaultFilterChain(this.filters, new FilterChain() {
            @Override
            public ResourceDataResult filter(final ResourceDataRequest req) {

                String bodyString;
                if (req.getHttpHeaders().getContentType() != null && req.getHttpHeaders().getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED)){
                    bodyString = buildCanonicalBodyQueryParams(req.getData());
                } else {

                    bodyString = mapMarshaller.marshal(req.getData());
                }
                StringInputStream body = new StringInputStream(bodyString);
                long length = body.available();

                CanonicalUri uri = req.getUri();
                String href = uri.getAbsolutePath();
                QueryString qs = uri.getQuery();

                HttpHeaders httpHeaders = req.getHttpHeaders();

                // if this is an Okta user, we must use a PUT and not a POST
                HttpMethod method = HttpMethod.POST;
                if (!create) {
                    method = HttpMethod.PUT;
                }
                Request request = new DefaultRequest(method, href, qs, httpHeaders, body, length);

                Response response = execute(request);
                Map<String, Object> responseBody = getBody(response);

                if (Collections.isEmpty(responseBody)) {
                    // Fix for https://github.com/stormpath/stormpath-sdk-java/issues/218
                    // Okta response with 200 for deactivate requests (i.e. /api/v1/apps/<id>/lifecycle/deactivate)
                    if (response.getHttpStatus() == 202 || response.getHttpStatus() == 200) { //202 means that the request has been accepted for processing, but the processing has not been completed. Therefore we do not have a response body.
                        responseBody = java.util.Collections.emptyMap();
                    } else {
                        throw new IllegalStateException("Unable to obtain resource data from the API server.");
                    }
                }

                ResourceAction responseAction = getPostAction(req, response);

                return new DefaultResourceDataResult(responseAction, uri, returnType, responseBody);
            }
        });

        ResourceAction action = create ? ResourceAction.CREATE : ResourceAction.UPDATE;
        ResourceDataRequest request = new DefaultResourceDataRequest(action, uri, abstractResource.getClass(), props, requestHeaders);

        ResourceDataResult result = chain.filter(request);

        Map<String,Object> data = result.getData();

        //ensure the caller's argument is updated with what is returned from the server if the types are the same:
        if (returnType.equals(abstractResource.getClass())) {
            abstractResource.setInternalProperties(data);
        }

        return resourceFactory.instantiate(returnType, data);
    }

    /* =====================================================================
       Resource Deletion
       ===================================================================== */

    @Override
    public void delete(String href) {
        doDelete(href, VoidResource.class, null);
    }

    @Override
    public <T extends Resource> void delete(T resource) {
        doDelete(resource, null);
    }

    @Override
    public <T extends Resource> void deleteResourceProperty(T resource, String propertyName) {
        Assert.hasText(propertyName, "propertyName cannot be null or empty.");
        doDelete(resource, propertyName);
    }

    private String buildCanonicalBodyQueryParams(Map<String, Object> bodyData){
        StringBuilder builder = new StringBuilder();
        Map<String, Object> treeMap = new TreeMap<String, Object>(bodyData);
        try {
            for (Map.Entry<String,Object> entry : treeMap.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    if (builder.length() > 0) {
                        builder.append(APPEND_PARAM_CHAR);
                    }
                    builder.append(String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(value.toString(), "UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e){
            log.trace("Body content could not be properly encoded");
            return null;
        }
        return builder.toString();
    }

    private void doDelete(String resourceHref, Class resourceClass, final String possiblyNullPropertyName) {

        Assert.hasText(resourceHref, "This resource does not have an href value, therefore it cannot be deleted.");

        // if this URL is a partial, then we MUST add the baseUrl
        if (resourceHref.startsWith("/") ) {
            resourceHref = qualify(resourceHref);
        }

        final String requestHref;
        if (Strings.hasText(possiblyNullPropertyName)) { //delete just that property, not the entire resource:
            requestHref = resourceHref + "/" + possiblyNullPropertyName;
        } else {
            requestHref = resourceHref;
        }

        FilterChain chain = new DefaultFilterChain(this.filters, new FilterChain() {

            @Override
            public ResourceDataResult filter(ResourceDataRequest request) {
                Request deleteRequest = new DefaultRequest(HttpMethod.DELETE, requestHref);
                execute(deleteRequest);
                //delete requests have HTTP 204 (no content), so just create an empty body for the result:
                return new DefaultResourceDataResult(request.getAction(), request.getUri(), request.getResourceClass(), new HashMap<String, Object>());
            }
        });

        final CanonicalUri resourceUri = canonicalize(resourceHref, null);
        ResourceDataRequest request = new DefaultResourceDataRequest(ResourceAction.DELETE, resourceUri, resourceClass, new HashMap<String, Object>());
        chain.filter(request);
    }

    private <T extends Resource> void doDelete(T resource, final String possiblyNullPropertyName) {

        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource, "Resource argument must be an AbstractResource.");

        doDelete(resource.getResourceHref(), resource.getClass(), possiblyNullPropertyName);
    }

    /* =====================================================================
       Resource Caching
       ===================================================================== */

    public boolean isCachingEnabled() {
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

            com.okta.sdk.error.Error error = new DefaultError(body);
            throw new ResourceException(error);
        }

        return response;
    }

    private Map<String, Object> getBody(Response response) {

        Assert.notNull(response, "response argument cannot be null.");

        Map<String, Object> out = null;

        if (response.hasBody()) {
            out = mapMarshaller.unmarshall(response.getBody(), response.getHeaders().getLinkMap());
        }

        return out;
    }

    protected void applyDefaultRequestHeaders(Request request) {
        request.getHeaders().setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

        // Get runtime headers from http client
        Map<String, List<String>> headerMap = HttpHeadersHolder.get();
        String oktaAgentHeaderName = OKTA_AGENT.toLowerCase();
        if (headerMap != null && headerMap.get(oktaAgentHeaderName) != null) {
            List<String> oktaAgents = headerMap.get(oktaAgentHeaderName);
            if (oktaAgents != null && oktaAgents.size() > 0) {
                String oktaAgent = Strings.arrayToDelimitedString(oktaAgents.toArray(), " ");
                request.getHeaders().set("User-Agent", oktaAgent + " " + USER_AGENT_STRING);
            }
        } else {
            request.getHeaders().set("User-Agent", USER_AGENT_STRING);
        }
        if (request.getHeaders().getContentType() == null) {
            if (request.getBody() != null) {
                // We only add the default content type (application/json) if a content type is not already in the request
                request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            }
        }

        List<String> clientRequestId;
        if (headerMap != null && ((clientRequestId = headerMap.get(OKTA_CLIENT_REQUEST_ID)) != null)) {
            request.getHeaders().put(OKTA_CLIENT_REQUEST_ID, clientRequestId);
        }
    }

    protected CanonicalUri canonicalize(String href, Map<String,?> queryParams) {
        href = ensureFullyQualified(href);
        return DefaultCanonicalUri.create(href, queryParams);
    }

    protected String ensureFullyQualified(String href) {
        String value = href;
        if (!isFullyQualified(href)) {
            value = qualify(href);
        }
        return value;
    }

    protected boolean isFullyQualified(String href) {

        if (href == null || href.length() < 5) {
            return false;
        }

        char c = href.charAt(0);
        if (c == 'h' || c == 'H') {
            c = href.charAt(1);
            if (c == 't' || c == 'T') {
                c = href.charAt(2);
                if (c == 't' || c == 'T') {
                    c = href.charAt(3);
                    if (c == 'p' || c == 'P') {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected String qualify(String href) {
        StringBuilder sb = new StringBuilder(this.baseUrlResolver.getBaseUrl());
        if (!href.startsWith("/")) {
            sb.append("/");
        }
        sb.append(href);
        return sb.toString();
    }

    private static String toString(InputStream is) {
        try {
            return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            log.trace("Response body input stream did not contain any content.", e);
            return null;
        }
    }
}

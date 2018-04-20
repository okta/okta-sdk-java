package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.*;
import com.okta.sdk.models.identityproviders.IdentityProvider;
import com.okta.sdk.models.identityproviders.Key;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityProviderApiClient extends JsonApiClient {

    private final static String KEY_PATH = "/credentials/keys";

    public IdentityProviderApiClient(ApiClientConfiguration config) {
        super(config);
    }

    public List<IdentityProvider> getIdentityProviders() throws IOException {
        return getIdentityProvidersWithLimit(Utils.getDefaultResultsLimit());
    }

    public List<IdentityProvider> getIdentityProvidersWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<IdentityProvider>>() { });
    }

    public IdentityProvider getIdentityProvider(String ipdId) throws IOException {
        return get(getEncodedPath("/%s", ipdId), new TypeReference<IdentityProvider>() {
        });
    }

    public List<IdentityProvider> getIdentityProvidersWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<IdentityProvider>>() { });
    }

    public List<IdentityProvider> getIdentityProvidersWithQuery(String query) throws IOException {
        return get(getEncodedPath("?q=%s", query), new TypeReference<List<IdentityProvider>>() { });
    }

    public IdentityProvider createIdentityProvider(IdentityProvider identityProvider) throws IOException {
        return post(getEncodedPath("/"), identityProvider, new TypeReference<IdentityProvider>() { });
    }

    public IdentityProvider updateIdentityProvider(IdentityProvider identityProvider) throws IOException {
        return put(getEncodedPath("/%s", identityProvider.getId()), identityProvider, new TypeReference<IdentityProvider>() { });
    }

    public PagedResults<IdentityProvider> getIdentityProvidersPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<IdentityProvider>(getIdentityProvidersApiResponseWithLimit(limit));
    }

    public PagedResults<IdentityProvider> getIdentityProvidersPagedResultsByUrl(String url) throws IOException {
        return new PagedResults<IdentityProvider>(getIdentityProvidersApiResponseByUrl(url));
    }

    protected ApiResponse<List<IdentityProvider>> getIdentityProvidersApiResponseByUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<IdentityProvider> items = unmarshallResponse(new TypeReference<List<IdentityProvider>>() {}, resp);
        return new ApiResponse<List<IdentityProvider>>(resp, items);
    }

    protected ApiResponse<List<IdentityProvider>> getIdentityProvidersApiResponseWithLimit(int limit) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?limit=%s", Integer.toString(limit)));
        List<IdentityProvider> items = unmarshallResponse(new TypeReference<List<IdentityProvider>>() {}, resp);
        return new ApiResponse<List<IdentityProvider>>(resp, items);
    }

    /**
     * Get the XML content of the metadata for the Identity Provider
     *
     * @param id Identity Provider ID
     * @return XML content of the metadata for the Identity Provider
     */
    public String getMetadata(String id) throws IOException {
        HttpResponse httpResponse = getHttpResponse(getEncodedPath("/%s/metadata.xml", id));
        HttpEntity entity = httpResponse.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    public Key getKey(String keyId) throws IOException {
        return get(getEncodedPath(getKeyPath("/%s"), keyId), new TypeReference<Key>() { });
    }

    public Key createKey(Key key) throws IOException {
        return post(getEncodedPath(getKeyPath("/")), key, new TypeReference<Key>() { });
    }

    private String getKeyPath(String relativePath)
    {
        return KEY_PATH + relativePath;
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/idps%s", apiVersion, relativePath);
    }
}

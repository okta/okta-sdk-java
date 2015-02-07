package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.Filter;
import com.okta.sdk.framework.Filters;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.apps.AppInstance;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppInstanceApiClient extends JsonApiClient {
    public AppInstanceApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/apps%s", this.apiVersion, relativePath);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<AppInstance> getAppInstances() throws IOException {
        return getAppInstancesWithLimit(Utils.getDefaultResultsLimit());
    }

    public List<AppInstance> getAppInstancesWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    public List<AppInstance> getAppInstancesWithQuery(String query) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH_QUERY, query);
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    public List<AppInstance> getAppInstancesWithFilter(Filter filter) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filter.toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    public List<AppInstance> getAppInstancesWithGroupId(String... groupId) throws IOException {
        return getAppInstancesWithFilter(getGroupFilter(groupId));
    }

    public List<AppInstance> getAppInstancesWithUserId(String... userId) throws IOException {
        return getAppInstancesWithFilter(getUserFilter(userId));
    }

    public List<AppInstance> getAppInstancesWithStatuses(String... statuses) throws IOException {
        return getAppInstancesWithFilter(getStatusFilter(statuses));
    }

    public List<AppInstance> getAppInstancesAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    public List<AppInstance> getAppInstancesAfterCursorWithLimitAndStatuses(String after, int limit, String... statuses) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        params.put(FILTER, getStatusFilter(statuses).toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    public List<AppInstance> getAppInstancesWithLimitAndStatuses(int limit, String... statuses) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        params.put(FILTER, getStatusFilter(statuses).toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    // CRUD

    public AppInstance createAppInstance(AppInstance instance) throws IOException {
        return post(getEncodedPath("/"), instance, new TypeReference<AppInstance>() { });
    }

    public AppInstance getAppInstance(String appInstanceId) throws IOException {
        return get(getEncodedPath("/%s", appInstanceId), new TypeReference<AppInstance>() { });
    }

    public AppInstance updateAppInstance(String appInstanceId, AppInstance instance) throws IOException {
        return put(getEncodedPath("/%s", appInstanceId), instance, new TypeReference<AppInstance>() { });
    }

    public void deleteAppInstance(String appInstanceId) throws IOException {
        delete(getEncodedPath("/%s", appInstanceId));
    }

    // LIFECYCLE

    public void activateAppInstance(String appInstanceId) throws IOException {
        post(getEncodedPath("/%s/lifecycle/activate", appInstanceId), new TypeReference<Map>() { });
    }

    public void deactivateAppInstance(String appInstanceId) throws IOException {
        post(getEncodedPath("/%s/lifecycle/deactivate", appInstanceId), new TypeReference<Map>() { });
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponse() throws IOException {
        return getAppInstancesApiResponseWithLimit(Utils.getDefaultResultsLimit());
    }

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithFilterAndLimit(Filter filter, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filter.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimitAndGroupId(int limit, String... groupId) throws IOException {
        return getAppInstancesApiResponseWithFilterAndLimit(getGroupFilter(groupId), limit);
    }

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimitAndUserId(int limit, String... userId) throws IOException {
        return getAppInstancesApiResponseWithFilterAndLimit(getUserFilter(userId), limit);
    }

    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    public PagedResults<AppInstance> getAppInstancesPagedResults() throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponse());
    }

    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimit(limit));
    }

    public PagedResults<AppInstance> getAppInstancesPagedResultsWithFilterAndLimit(Filter filter, int limit) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithFilterAndLimit(filter, limit));
    }

    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimitAndGroupId(int limit, String... groupId) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimitAndGroupId(limit, groupId));
    }

    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimitAndUserId(int limit, String... userId) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimitAndUserId(limit, userId));
    }

    public PagedResults<AppInstance> getAppInstancesPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithUrl(url));
    }

    ////////////////////////////////////////////
    // UTILITY METHODS
    ////////////////////////////////////////////

    private Filter getStatusFilter(String... statuses) {
        return Utils.getFilter(Filters.AppInstance.STATUS, statuses);
    }

    private Filter getGroupFilter(String... groups) {
        return Utils.getFilter(Filters.AppInstance.GROUP_ID, groups);
    }

    private Filter getUserFilter(String... users) {
        return Utils.getFilter(Filters.AppInstance.USER_ID, users);
    }
}

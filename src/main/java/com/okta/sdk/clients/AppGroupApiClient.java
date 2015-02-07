package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.apps.AppGroup;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppGroupApiClient extends JsonApiClient {
    public AppGroupApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/apps%s", this.apiVersion, relativePath);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<AppGroup> getAppGroups(String appInstanceId) throws IOException {
        return getAppGroupsWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    public List<AppGroup> getAppGroupsWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId), new TypeReference<List<AppGroup>>() { });
    }

    public List<AppGroup> getAppGroupsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId), new TypeReference<List<AppGroup>>() { });
    }

    // CRUD

    public AppGroup createAppGroup(String appInstanceId, String groupId, AppGroup assignment) throws IOException {
        return put(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), assignment, new TypeReference<AppGroup>() { });
    }

    public AppGroup getAppGroup(String appInstanceId, String groupId) throws IOException {
        return get(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), new TypeReference<AppGroup>() { });
    }

    public AppGroup updateAppGroup(String appInstanceId, String groupId, AppGroup assignment) throws IOException {
        return put(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), assignment, new TypeReference<AppGroup>() { });
    }

    public void deleteAppGroup(String appInstanceId, String groupId) throws IOException {
        delete(getEncodedPath("/%s/groups/%s", appInstanceId, groupId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponse(String appInstanceId) throws IOException {
        return getAppGroupsApiResponseWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId));
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId));
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseWithUrl(String url) throws IOException {
        HttpResponse response = getHttpResponse(url);
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    public PagedResults<AppGroup> getAppGroupsPagedResults(String appInstanceId) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponse(appInstanceId));
    }

    public PagedResults<AppGroup> getAppGroupsPagedResultsWithLimit(String appInstanceId, int limit) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseWithLimit(appInstanceId, limit));
    }

    public PagedResults<AppGroup> getAppGroupsPagedResultsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseAfterCursorWithLimit(appInstanceId, after, limit));
    }

    public PagedResults<AppGroup> getAppGroupsPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseWithUrl(url));
    }
}

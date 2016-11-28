package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.apps.AppUser;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUserApiClient extends JsonApiClient {

    public AppUserApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<AppUser> getAppUsers(String appInstanceId) throws IOException {
        return getAppUsersWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    public List<AppUser> getAppUsersWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId), new TypeReference<List<AppUser>>() { });
    }

    public List<AppUser> getAppUsersAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId), new TypeReference<List<AppUser>>() { });
    }

    // CRUD

    public AppUser createAppUser(String appInstanceId, String userId) throws IOException {
        return put(getEncodedPath("/%s/users/%s", appInstanceId, userId), null, new TypeReference<AppUser>() { });
    }

    public AppUser getAppUser(String appInstanceId, String userId) throws IOException {
        return get(getEncodedPath("/%s/users/%s", appInstanceId, userId), new TypeReference<AppUser>() { });
    }

    public AppUser updateAppUser(String appInstanceId, String userId, AppUser assignment) throws IOException {
        return put(getEncodedPath("/%s/users/%s", appInstanceId, userId), assignment, new TypeReference<AppUser>() { });
    }

    public void deleteAppUser(String appInstanceId, String userId) throws IOException {
        delete(getEncodedPath("/%s/users/%s", appInstanceId, userId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<AppUser>> getAppUsersApiResponse(String appInstanceId) throws IOException {
        return getAppUsersApiResponseWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    protected ApiResponse<List<AppUser>> getAppUsersApiResponseWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId));
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    protected ApiResponse<List<AppUser>> getAppUsersApiResponseAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId));
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    protected ApiResponse<List<AppUser>> getAppUsersApiResponseWithUrl(String url) throws IOException {
        HttpResponse response = getHttpResponse(url);
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    public PagedResults<AppUser> getAppUsersPagedResults(String appInstanceId) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponse(appInstanceId));
    }

    public PagedResults<AppUser> getAppUsersPagedResultsWithLimit(String appInstanceId, int limit) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseWithLimit(appInstanceId, limit));
    }

    public PagedResults<AppUser> getAppUsersPagedResultsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseAfterCursorWithLimit(appInstanceId, after, limit));
    }

    public PagedResults<AppUser> getAppUsersPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseWithUrl(url));
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/apps%s", this.apiVersion, relativePath);
    }
}

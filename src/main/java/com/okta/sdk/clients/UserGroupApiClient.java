package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.users.User;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGroupApiClient extends JsonApiClient {

    public UserGroupApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<UserGroup> getUserGroupsWithQuery(String query) throws IOException {
        return get(getEncodedPath("?" + SEARCH_QUERY + "=%s", query), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsWithQueryAndLimit(String query, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH_QUERY, query);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsWithLimit(int limit) throws IOException {
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    public List<UserGroup> getUserGroupsByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<UserGroup>>() { });
    }

    // CRUD

    public UserGroup createUserGroup(UserGroup userGroup) throws IOException {
        return post(getEncodedPath("/"), userGroup, new TypeReference<UserGroup>() { });
    }

    public UserGroup getUserGroup(String groupId) throws IOException {
        return get(getEncodedPath("/%s", groupId), new TypeReference<UserGroup>() { });
    }

    public UserGroup updateUserGroup(String groupId, UserGroup userGroup) throws IOException {
        return put(getEncodedPath("/%s", groupId), userGroup, new TypeReference<UserGroup>() { });
    }

    public void deleteUserGroup(String groupId) throws IOException {
        delete(getEncodedPath("/%s", groupId));
    }

    // USER OPERATIONS

    public List<User> getUsers(String groupId) throws IOException {
        return get(getEncodedPath("/%s/users", groupId), new TypeReference<List<User>>() { });
    }

    public List<User> getUsersByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<User>>() { });
    }

    public void addUserToGroup(String groupId, String userId) throws IOException {
        put(getEncodedPath("/%s/users/%s", groupId, userId));
    }

    public void removeUserFromGroup(String groupId, String userId) throws IOException {
        delete(getEncodedPath("/%s/users/%s", groupId, userId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    // UserGroup

    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseWithFilter(FilterBuilder filterBuilder) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseByUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    // User

    protected ApiResponse<List<User>> getUsersApiResponseAfterCursorWithLimit(String groupId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, groupId));
        List<User> users = unmarshall(resp, new TypeReference<List<User>>() { });
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<User> users = unmarshall(resp, new TypeReference<List<User>>() { });
        return new ApiResponse<List<User>>(resp, users);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    // UserGroup

    public PagedResults<UserGroup> getUserGroupsPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseWithLimit(limit));
    }

    public PagedResults<UserGroup> getUserGroupsPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseAfterCursorWithLimit(after, limit));
    }

    public PagedResults<UserGroup> getUserGroupsPagedResultsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseWithFilter(filterBuilder));
    }

    public PagedResults<UserGroup> getUserGroupsPagedResultsByUrl(String url) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseByUrl(url));
    }

    // User

    public PagedResults<User> getUsersPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithUrl(url));
    }

    public PagedResults<User> getUsersPagedResultsAfterCursorWithLimit(String groupId, String after, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseAfterCursorWithLimit(groupId, after, limit));
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/groups%s", this.apiVersion, relativePath);
    }

}

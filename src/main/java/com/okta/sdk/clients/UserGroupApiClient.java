/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.users.User;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGroupApiClient extends JsonApiClient {

    /**
     * Constructor for the UserGroupApiClient.
     * @param config {@link ApiClientConfiguration}
     */
    public UserGroupApiClient(ApiClientConfiguration config) {
        super(config);
    }

    /**
     * Returns all User Groups.
     *
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroups() throws IOException {
        return getUserGroupsWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Returns all User Groups matching a search query.
     *
     * @param  query {@link String}                  Search string to locate a user group.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsWithQuery(String query) throws IOException {
        return get(getEncodedPath("?" + SEARCH_QUERY + "=%s", query), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a limited number of User Groups matching a search query.
     *
     * @param  query {@link String}                  Search string to locate a user group.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsWithQueryAndLimit(String query, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH_QUERY, query);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a limited number of User Groups.
     *
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsWithLimit(int limit) throws IOException {
        if (limit == -1) {
            return get(getFullPath("/"), new TypeReference<List<UserGroup>>() { });
        }
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a list of User Groups matching a filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a limited number of User Groups matching a filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a list of User Groups starting and ending at a specific point.
     *
     * @param  after {@link String }                 The cursor that determines which user groups to return after.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("", params), new TypeReference<List<UserGroup>>() { });
    }

    /**
     * Returns a list of User Groups from URL.
     *
     * @param  url {@link String}                    The URL to get user groups from.
     * @return {@link List}                          Users groups containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroupsByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<UserGroup>>() { });
    }


    /**
     * Creates a new User Group.
     *
     * @param  userGroup {@link UserGroup}           User group to create.
     * @return {@link UserGroup}                     User group object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public UserGroup createUserGroup(UserGroup userGroup) throws IOException {
        return post(getEncodedPath("/"), userGroup, new TypeReference<UserGroup>() { });
    }

    /**
     * Returns a  single User Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @return {@link UserGroup}                     User group object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public UserGroup getUserGroup(String groupId) throws IOException {
        return get(getEncodedPath("/%s", groupId), new TypeReference<UserGroup>() { });
    }

    /**
     * Updates a new User Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  userGroup {@link UserGroup}           User group to update.
     * @return {@link UserGroup}                     User group object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public UserGroup updateUserGroup(String groupId, UserGroup userGroup) throws IOException {
        return put(getEncodedPath("/%s", groupId), userGroup, new TypeReference<UserGroup>() { });
    }

    /**
     * Deletes a new User Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void deleteUserGroup(String groupId) throws IOException {
        delete(getEncodedPath("/%s", groupId));
    }

    /**
     * Returns all users assigned to a Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @return {@link List}                          Users containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<User> getUsers(String groupId) throws IOException {
        return get(getEncodedPath("/%s/users", groupId), new TypeReference<List<User>>() { });
    }

    /**
     * Returns all users assigned to a Group
     *
     * @param  url {@link String}                    The URL to get user groups from.
     * @return {@link List}                          Users containing matching results.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<User> getUsersByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<User>>() { });
    }

    /**
     * Add a User to an existing Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  userId {@link String}                 Unique ID of the user.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void addUserToGroup(String groupId, String userId) throws IOException {
        put(getEncodedPath("/%s/users/%s", groupId, userId));
    }

    /**
     * Removes a User from a Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  userId {@link String}                 Unique ID of the user.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void removeUserFromGroup(String groupId, String userId) throws IOException {
        delete(getEncodedPath("/%s/users/%s", groupId, userId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API response containing specified number of User Groups.
     *
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    /**
     * Returns the API response containing filtered User Groups.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseWithFilter(FilterBuilder filterBuilder) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    /**
     * Returns the API response containing User Groups given limit and cursor.
     *
     * @param  after {@link String }                 The cursor that determines which user groups to return after.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }

    /**
     * Returns the API response containing User Groups via URL.
     *
     * @param  url {@link String}                    The URL to get user groups from.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<UserGroup>> getUserGroupsApiResponseByUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<UserGroup> groups = unmarshall(resp, new TypeReference<List<UserGroup>>() { });
        return new ApiResponse<List<UserGroup>>(resp, groups);
    }


    /**
     * Returns the API response containing Users of a Group.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  after {@link String }                 The cursor that determines which user groups to return after.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<User>> getUsersApiResponseAfterCursorWithLimit(String groupId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, groupId));
        List<User> users = unmarshall(resp, new TypeReference<List<User>>() { });
        return new ApiResponse<List<User>>(resp, users);
    }

    /**
     * Returns the API response containing Users of a Group via URL.
     *
     * @param  url {@link String}                    The URL to get user groups from.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<User>> getUsersApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<User> users = unmarshall(resp, new TypeReference<List<User>>() { });
        return new ApiResponse<List<User>>(resp, users);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Returns PagedResults of User Groups.
     *
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<UserGroup> getUserGroupsPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseWithLimit(limit));
    }

    /**
     * Returns PagedResults of User Groups.
     *
     * @param  after {@link String }                 The cursor that determines which user groups to return after.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<UserGroup> getUserGroupsPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseAfterCursorWithLimit(after, limit));
    }

    /**
     * Returns PagedResults of User Groups.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<UserGroup> getUserGroupsPagedResultsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseWithFilter(filterBuilder));
    }

    /**
     * Returns PagedResults of User Groups via URL.
     *
     * @param  url {@link String}                    The URL to get user groups from.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<UserGroup> getUserGroupsPagedResultsByUrl(String url) throws IOException {
        return new PagedResults<UserGroup>(getUserGroupsApiResponseByUrl(url));
    }


    /**
     * Returns PagedResults of Users.
     *
     * @param  url {@link String}                    The URL to get users from.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithUrl(url));
    }

    /**
     * Returns PagedResults of Users.
     *
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  after {@link String }                 The cursor that determines which user groups to return after.
     * @param  limit {@link Integer}                 The max number of results returned.
     * @return {@link PagedResults}                  Paged results of all returned users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsAfterCursorWithLimit(String groupId, String after, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseAfterCursorWithLimit(groupId, after, limit));
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/groups%s", this.apiVersion, relativePath);
    }

}

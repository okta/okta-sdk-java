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

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    /**
     * Return all App Instances.
     *
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstances() throws IOException {
        return getAppInstancesWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Return a maximum number of App Instances.
     *
     * @param  limit {@link Integer}         Number of matching results to return.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    /**
     * Return all App Instances matching a filter.
     *
     * @param  query {@link String}          Query for searching through app instances.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithQuery(String query) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH_QUERY, query);
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    /**
     * Return all App Instances matching a filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link List}                          List of app instances in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithFilter(FilterBuilder filterBuilder) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    /**
     * Return all App Instances corresponding to a Group.
     *
     * @param  groupId {@link String}        Unique ID of the group.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithGroupId(String... groupId) throws IOException {
        return getAppInstancesWithFilter(getGroupFilter(groupId));
    }

    /**
     * Return all App Instances corresponding to a User.
     *
     * @param  userId {@link String}         Unique ID of the user.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithUserId(String... userId) throws IOException {
        return getAppInstancesWithFilter(getUserFilter(userId));
    }

    /**
     * Return all App Instances with status.
     *
     * @param  statuses {@link String}       Status of the app instance.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithStatuses(String... statuses) throws IOException {
        return getAppInstancesWithFilter(getStatusFilter(statuses));
    }

    /**
     * Return a maximum number of App Instances starting at a given index.
     *
     * @param  after {@link String}          Specifies the pagination cursor for the next page of app instances.
     * @param  limit {@link Integer}         Number of matching results to return.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    /**
     * Return a maximum number of App Instances with status starting at a given index.
     *
     * @param  after {@link String}          Specifies the pagination cursor for the next page of app instances.
     * @param  limit {@link Integer}         Number of matching results to return.
     * @param  statuses {@link String}       Status of the app instance.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesAfterCursorWithLimitAndStatuses(String after, int limit, String... statuses) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        params.put(FILTER, getStatusFilter(statuses).toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    /**
     * Return a maximum number of App Instances with status.
     *
     * @param  limit {@link Integer}         Number of matching results to return.
     * @param  statuses {@link String}       Status of the app instance.
     * @return {@link List}                  List of app instances in the search.
     * @throws IOException                   If an input or output exception occurred.
     */
    public List<AppInstance> getAppInstancesWithLimitAndStatuses(int limit, String... statuses) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        params.put(FILTER, getStatusFilter(statuses).toString());
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<AppInstance>>() { });
    }

    // CRUD

    /**
     * Creates an App Instance.
     *
     * @param  instance {@link AppInstance}          The updated app instance object.
     * @return {@link AppInstance}                   Created app instance.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppInstance createAppInstance(AppInstance instance) throws IOException {
        return post(getEncodedPath("/"), instance, new TypeReference<AppInstance>() { });
    }

    /**
     * Returns an App Instance.
     *
     * @param  appInstanceId {@link String}          The unique ID of the app instance.
     * @return {@link AppInstance}                   Returned app instance.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppInstance getAppInstance(String appInstanceId) throws IOException {
        return get(getEncodedPath("/%s", appInstanceId), new TypeReference<AppInstance>() { });
    }

    /**
     * Updates an App Instance.
     *
     * @param  appInstanceId {@link String}          The unique ID of the app instance.
     * @param  instance {@link AppInstance}          The updated app instance object.
     * @return {@link AppInstance}                   Updated app instance..
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppInstance updateAppInstance(String appInstanceId, AppInstance instance) throws IOException {
        return put(getEncodedPath("/%s", appInstanceId), instance, new TypeReference<AppInstance>() { });
    }

    /**
     * Deletes an App Instance.
     *
     * @param  appInstanceId {@link String}          The unique ID of the app instance.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void deleteAppInstance(String appInstanceId) throws IOException {
        delete(getEncodedPath("/%s", appInstanceId));
    }

    // LIFECYCLE

    /**
     * Activates an App Instance.
     *
     * @param  appInstanceId {@link String}          The unique ID of the app instance.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void activateAppInstance(String appInstanceId) throws IOException {
        post(getEncodedPath("/%s/lifecycle/activate", appInstanceId), new TypeReference<Map>() { });
    }

    /**
     * Deactivates an App Instance.
     *
     * @param  appInstanceId {@link String}          The unique ID of the app instance.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void deactivateAppInstance(String appInstanceId) throws IOException {
        post(getEncodedPath("/%s/lifecycle/deactivate", appInstanceId), new TypeReference<Map>() { });
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API response containing all App Instances.
     *
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponse() throws IOException {
        return getAppInstancesApiResponseWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Returns the API response containing a maximum number of App Instances.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimit(int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    /**
     * Returns the API response containing a maximum number of App Instances.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    /**
     * Returns the API response containing a maximum number of App Instances.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  groupId {@link String}                Group's unique ID.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimitAndGroupId(int limit, String... groupId) throws IOException {
        return getAppInstancesApiResponseWithFilterAndLimit(getGroupFilter(groupId), limit);
    }

    /**
     * Returns the API response containing a maximum number of App Instances.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  userId {@link String}                 User's unique ID.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithLimitAndUserId(int limit, String... userId) throws IOException {
        return getAppInstancesApiResponseWithFilterAndLimit(getUserFilter(userId), limit);
    }

    /**
     * Returns the API response containing a List of App Instances via URL.
     *
     * @param  url {@link String}                    Url to retrieve app users.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppInstance>> getAppInstancesApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<AppInstance> appInstances = unmarshall(resp, new TypeReference<List<AppInstance>>() { });
        return new ApiResponse<List<AppInstance>>(resp, appInstances);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Returns all paged AppInstances.
     *
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred
     */
    public PagedResults<AppInstance> getAppInstancesPagedResults() throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponse());
    }

    /**
     * Returns a maximum number of paged results of an AppInstance.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimit(limit));
    }

    /**
     * Returns a maximum number of paged results of an AppInstance via filter.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppInstance> getAppInstancesPagedResultsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithFilterAndLimit(filterBuilder, limit));
    }

    /**
     * Returns a maximum number of paged results of an AppInstance matching a group ID.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  groupId {@link String}                Group's unique ID.
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimitAndGroupId(int limit, String... groupId) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimitAndGroupId(limit, groupId));
    }

    /**
     * Returns maximum number of paged results of an AppInstance matching a user ID.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  userId {@link String}                 User's unique ID.
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppInstance> getAppInstancesPagedResultsWithLimitAndUserId(int limit, String... userId) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithLimitAndUserId(limit, userId));
    }

    /**
     * Returns paged results of AppInstance via URL.
     *
     * @param  url {@link String}                    Url to retrieve app instances.
     * @return {@link PagedResults}                  Paged results of all returned app instances.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppInstance> getAppInstancesPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppInstance>(getAppInstancesApiResponseWithUrl(url));
    }

    ////////////////////////////////////////////
    // UTILITY METHODS
    ////////////////////////////////////////////

    /**
     * Utility method to get statuses from AppInstance.
     *
     * @param statuses {@link String}
     * @return {@link String}
     */
    private FilterBuilder getStatusFilter(String... statuses) {
        return Utils.getFilter(Filters.AppInstance.STATUS, statuses);
    }

    /**
     * Utility method to get groups from AppInstance.
     *
     * @param groups {@link String}
     * @return {@link String}
     */
    private FilterBuilder getGroupFilter(String... groups) {
        return Utils.getFilter(Filters.AppInstance.GROUP_ID, groups);
    }

    /**
     * Utility method to get users from AppInstance.
     *
     * @param users {@link String}
     * @return {@link String}
     */
    private FilterBuilder getUserFilter(String... users) {
        return Utils.getFilter(Filters.AppInstance.USER_ID, users);
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/apps%s", this.apiVersion, relativePath);
    }

}

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

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    /**
     * Return all app groups.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @return {@link List}                          List of app groups in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppGroup> getAppGroups(String appInstanceId) throws IOException {
        return getAppGroupsWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    /**
     * Return all app groups with an upper limit of the number of results.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of app groups in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppGroup> getAppGroupsWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId), new TypeReference<List<AppGroup>>() { });
    }

    /**
     * Return all app groups with an upper limit of the number of results starting at an index.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of app groups.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of app groups in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppGroup> getAppGroupsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId), new TypeReference<List<AppGroup>>() { });
    }

    // CRUD

    /**
     * Creates an app group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  assignment {@link AppGroup}           App group to be updated.
     * @return {@link AppGroup}                      Created app group.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppGroup createAppGroup(String appInstanceId, String groupId, AppGroup assignment) throws IOException {
        return put(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), assignment, new TypeReference<AppGroup>() { });
    }

    /**
     * Returns an app group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  groupId {@link String}                Unique ID of the group.
     * @return {@link AppGroup}                      Returned app group.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppGroup getAppGroup(String appInstanceId, String groupId) throws IOException {
        return get(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), new TypeReference<AppGroup>() { });
    }

    /**
     * Updates an app group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  groupId {@link String}                Unique ID of the group.
     * @param  assignment {@link AppGroup}           App group to be updated.
     * @return {@link AppGroup}                      Updated app group.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppGroup updateAppGroup(String appInstanceId, String groupId, AppGroup assignment) throws IOException {
        return put(getEncodedPath("/%s/groups/%s", appInstanceId, groupId), assignment, new TypeReference<AppGroup>() { });
    }

    /**
     * Deletes an app group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  groupId {@link String}                Unique ID of the group.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void deleteAppGroup(String appInstanceId, String groupId) throws IOException {
        delete(getEncodedPath("/%s/groups/%s", appInstanceId, groupId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API response containing a List of App Groups.
     *
     * @param  appInstanceId {@link String}         Unique ID of the app instance.
     * @return {@link ApiResponse}                  The app group with paging info.
     * @throws IOException                          If an input or output exception occurred.
     */
    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponse(String appInstanceId) throws IOException {
        return getAppGroupsApiResponseWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    /**
     * Returns the API response containing a maximum number of App Groups.
     *
     * @param  appInstanceId {@link String}         Unique ID of the app instance.
     * @param  limit {@link Integer}                The max number of results returned.
     * @return {@link ApiResponse}                  The app group with paging info.
     * @throws IOException                          If an input or output exception occurred.
     */
    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId));
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    /**
     * Returns the API response containing a maximum number of App Groups via index.
     *
     * @param  appInstanceId {@link String}         Unique ID of the app instance.
     * @param  after {@link String }                The cursor that determines which app groups to return after.
     * @param  limit {@link Integer}                The max number of results returned.
     * @return {@link ApiResponse}                  The app group with paging info.
     * @throws IOException                          If an input or output exception occurred.
     */
    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/groups", params, appInstanceId));
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    /**
     * Returns the API response containing a List of App Groups via URL.
     * @param  url {@link String}                   The URL to get app groups from.
     * @return {@link ApiResponse}                  The app group with paging info.
     * @throws IOException                          If an input or output exception occurred.
     */
    protected ApiResponse<List<AppGroup>> getAppGroupsApiResponseWithUrl(String url) throws IOException {
        HttpResponse response = getHttpResponse(url);
        List<AppGroup> assignments = unmarshallResponse(new TypeReference<List<AppGroup>>() { }, response);
        return new ApiResponse<List<AppGroup>>(response, assignments);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Returns all paged results of an App Group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @return {@link PagedResults}                  Paged results of all returned app groups.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppGroup> getAppGroupsPagedResults(String appInstanceId) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponse(appInstanceId));
    }

    /**
     * Returns maximum number of paged results of an App Group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned app groups.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppGroup> getAppGroupsPagedResultsWithLimit(String appInstanceId, int limit) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseWithLimit(appInstanceId, limit));
    }

    /**
     * Returns maximum number of paged results of an App Group.
     *
     * @param  appInstanceId {@link String}          Unique ID of the app instance.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of app groups.
     * @return {@link PagedResults}                  Paged results of all returned app groups.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppGroup> getAppGroupsPagedResultsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseAfterCursorWithLimit(appInstanceId, after, limit));
    }

    /**
     * Returns paged results of App Group via URL.
     *
     * @param  url {@link String}                    Url to retrieve app groups.
     * @return {@link PagedResults}                  Paged results of all returned app groups.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppGroup> getAppGroupsPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppGroup>(getAppGroupsApiResponseWithUrl(url));
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/apps%s", this.apiVersion, relativePath);
    }

}

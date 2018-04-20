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

    /**
     * Return all App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @return {@link List}                          List of app users in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppUser> getAppUsers(String appInstanceId) throws IOException {
        return getAppUsersWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    /**
     * Return a maximum number of App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of app users in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppUser> getAppUsersWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId), new TypeReference<List<AppUser>>() { });
    }
    /**
     * Return a list of App Users with an upper limit starting at a specified index.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of app users.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of app users in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<AppUser> getAppUsersAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId), new TypeReference<List<AppUser>>() { });
    }

    // CRUD

    /**
     * Creates an App User.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @return {@link AppUser}                       Updated app user object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppUser createAppUser(String appInstanceId, AppUser appUser) throws IOException {
        return post(getEncodedPath("/%s/users", appInstanceId), appUser, new TypeReference<AppUser>() { });
    }

    /**
     * Returns an App User.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  userId {@link String}                 User's unique ID.
     * @return {@link AppUser}                       Updated app user object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppUser getAppUser(String appInstanceId, String userId) throws IOException {
        return get(getEncodedPath("/%s/users/%s", appInstanceId, userId), new TypeReference<AppUser>() { });
    }

    /**
     * Update an App User.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  userId {@link String}                 User's unique ID.
     * @param  assignment {@link AppUser}            AppUser profile.
     * @return {@link AppUser}                       Updated app user object.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AppUser updateAppUser(String appInstanceId, String userId, AppUser assignment) throws IOException {
        return put(getEncodedPath("/%s/users/%s", appInstanceId, userId), assignment, new TypeReference<AppUser>() { });
    }

    /**
     * Deletes an App User.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  userId {@link String}                 User's unique ID.
     * @throws IOException                           If an input or output exception occurred.
     */
    public void deleteAppUser(String appInstanceId, String userId) throws IOException {
        delete(getEncodedPath("/%s/users/%s", appInstanceId, userId));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API response containing a List of App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppUser>> getAppUsersApiResponse(String appInstanceId) throws IOException {
        return getAppUsersApiResponseWithLimit(appInstanceId, Utils.getDefaultResultsLimit());
    }

    /**
     * Returns the API response containing a max number of App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppUser>> getAppUsersApiResponseWithLimit(String appInstanceId, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId));
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    /**
     * Returns the API response containing a max number of App Users after index.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of app users.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppUser>> getAppUsersApiResponseAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse response = getHttpResponse(getEncodedPathWithQueryParams("/%s/users", params, appInstanceId));
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    /**
     * Returns the API response containing a List of App Users via URL.
     *
     * @param  url {@link String}                    Url to retrieve app users.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<AppUser>> getAppUsersApiResponseWithUrl(String url) throws IOException {
        HttpResponse response = getHttpResponse(url);
        List<AppUser> assignments = unmarshallResponse(new TypeReference<List<AppUser>>() { }, response);
        return new ApiResponse<List<AppUser>>(response, assignments);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Returns list of App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @return {@link PagedResults}                  Paged results of all returned app users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppUser> getAppUsersPagedResults(String appInstanceId) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponse(appInstanceId));
    }

    /**
     * Returns max number of paged results of App Users.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned app users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppUser> getAppUsersPagedResultsWithLimit(String appInstanceId, int limit) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseWithLimit(appInstanceId, limit));
    }

    /**
     * Returns max number of paged results of App Users starting at an index.
     *
     * @param  appInstanceId {@link String}          ID of the App.
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of app users.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned app users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppUser> getAppUsersPagedResultsAfterCursorWithLimit(String appInstanceId, String after, int limit) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseAfterCursorWithLimit(appInstanceId, after, limit));
    }

    /**
     * Returns paged results of App Users via URL.
     *
     * @param  url {@link String}                    Url to retrieve app users.
     * @return {@link PagedResults}                  Paged results of all returned app users.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<AppUser> getAppUsersPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<AppUser>(getAppUsersApiResponseWithUrl(url));
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

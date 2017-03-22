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
import com.okta.sdk.models.events.Event;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventApiClient extends JsonApiClient {

    public static final String START_DATE = "startDate";

    public EventApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    /**
     * Returns all Events.
     *
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEvents() throws IOException {
        return getEventsWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Returns a maximum number of Events.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithLimit(int limit) throws IOException {
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<Event>>() { });
    }

    /**
     * Returns a list of Events after a start date.
     *
     * @param  startDate {@link DateTime}            Starting date.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithStartDate(DateTime startDate) throws IOException {
        return get(getEncodedPath("?" + START_DATE + "=%s", Utils.convertDateTimeToString(startDate)), new TypeReference<List<Event>>() { });
    }

    /**
     * Return a maximum number of Events starting after a date.
     *
     * @param  startDate {@link DateTime}            Starting date for the events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }

    /**
     * Return a maximum number of Events given a pagination cursor.
     *
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }

    /**
     * Return all Events given a filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<Event>>() { });
    }

    /**
     * Return all Events after the given startDate that match the search, with an upper limit of the number of results.
     *
     * @param  startDate {@link DateTime}            Starting date for the events.
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithFilterAndStartDate(FilterBuilder filterBuilder, DateTime startDate) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }
    /**
     * Return all Events given URL.
     *
     * @param  url {@link String}                    Url to retrieve events.
     * @return {@link List}                          List of events in the search.
     * @throws IOException                           If an input or output exception occurred.
     */
    public List<Event> getEventsWithUrl(String url) throws IOException {
        return get(url, new TypeReference<List<Event>>() { });
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    /**
     * Returns the API response containing a list of Events.
     *
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponse() throws IOException {
        return getEventsApiResponseWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Returns the API response containing a max number of Events.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponseWithLimit(int limit) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    /**
     * Returns the API response containing a max number of Events given a filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponseWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    /**
     * Returns the API response containing a max number of Events starting after a date.
     *
     * @param  startDate {@link DateTime}            Starting date for the events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponseWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    /**
     * Returns the API response containing a max number of Events after index.
     *
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponseAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    /**
     * Returns the API response containing a List of Events via URL.
     *
     * @param  url {@link String}                    Url to retrieve events.
     * @return {@link ApiResponse}                   API response object.
     * @throws IOException                           If an input or output exception occurred.
     */
    protected ApiResponse<List<Event>> getEventsApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Returns paged results of Events.
     *
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResults() throws IOException {
        return new PagedResults<Event>(getEventsApiResponse());
    }

    /**
     * Returns max number of paged results of Events.
     *
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithLimit(limit));
    }

    /**
     * Returns max number of paged results of Events given filter.
     *
     * @param  filterBuilder {@link FilterBuilder}   Filter to perform, null interpreted to mean no filter.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResultsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithFilterAndLimit(filterBuilder, limit));
    }

    /**
     * Returns max number of paged results of Events given start date.
     *
     * @param  startDate {@link DateTime}            Starting date for the events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResultsWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithStartDateAndLimit(startDate, limit));
    }

    /**
     * Returns max number of paged results of Events starting after value.
     *
     * @param  after {@link String}                  Specifies the pagination cursor for the next page of events.
     * @param  limit {@link Integer}                 Number of matching results to return.
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseAfterCursorWithLimit(after, limit));
    }

    /**
     * Returns paged results of Events.
     *
     * @param  url {@link String}                    Url to retrieve events.
     * @return {@link PagedResults}                  Paged results of all returned events.
     * @throws IOException                           If an input or output exception occurred.
     */
    public PagedResults<Event> getEventsPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithUrl(url));
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/events%s", this.apiVersion, relativePath);
    }

}

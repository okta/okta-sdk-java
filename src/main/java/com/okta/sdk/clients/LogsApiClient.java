/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
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
import com.google.common.annotations.VisibleForTesting;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.log.Log;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogsApiClient extends JsonApiClient {

    private static final String START_DATE = "since";

    // This API has a default limit of 100, not Utils.getDefaultResultsLimit()
    private static final int DEFAULT_RESULTS_LIMIT = 100;


    public LogsApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    /**
     * Return all Logs after the given startDate
     *
     * @param startDate     Starting date
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate) throws IOException {
        return getLogs(startDate, null, null, null, DEFAULT_RESULTS_LIMIT);
    }

    /**
     * Return all Logs after the given startDate with an upper limit of the number of results
     *
     * @param startDate     Starting date
     * @param limit         Number of results to return
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, int limit) throws IOException {
        return getLogs(startDate, null, null, null, limit);
    }

    /**
     * Return all Logs after the given startDate that match the SCIM filter
     *
     * @param startDate     Starting date
     * @param filterBuilder Filter to apply
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, FilterBuilder filterBuilder) throws IOException {
        return getLogs(startDate, filterBuilder, null, null, DEFAULT_RESULTS_LIMIT);
    }

    /**
     * Return all Logs after the given startDate that match the SCIM filter, with an upper limit of the number of results
     *
     * @param startDate     Starting date
     * @param filterBuilder Filter to apply
     * @param limit         Number of matching results to return
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, FilterBuilder filterBuilder, int limit) throws IOException {
        return getLogs(startDate, filterBuilder, null, null, limit);
    }

    /**
     * Return all Logs after the given startDate that match the search
     *
     * @param startDate     Starting date
     * @param search        Search to perform
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, String search) throws IOException {
        return getLogs(startDate, null, search, null, DEFAULT_RESULTS_LIMIT);
    }

    /**
     * Return all Logs after the given startDate that match the search, with an upper limit of the number of results
     *
     * @param startDate     Starting date
     * @param search        Search to perform
     * @param limit         Number of matching results to return
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, String search, int limit) throws IOException {
        return getLogs(startDate, null, search, null, limit);
    }

    /**
     * Return Logs at the given url
     *
     * @param url           Url of search
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(String url) throws IOException {
        return get(url, new TypeReference<List<Log>>() { });
    }

    /**
     * Return all Logs after the given startDate that match the SCIM filter and search, with an upper limit of the number of results
     *
     * @param startDate     Starting date
     * @param filterBuilder Filter to perform, null interpreted to mean no filter
     * @param search        Search to perform, null interpreted to mean no search
     * @param after         Specifies the pagination cursor for the next page of events
     * @param limit         Number of matching results to return
     * @return List<Log>    List of logs in the search
     * @throws IOException  If an input or output exception occurred
     */
    public List<Log> getLogs(DateTime startDate, FilterBuilder filterBuilder, String search, String after, int limit) throws IOException {
        return get(getApiUri(startDate, filterBuilder, search, after, limit), new TypeReference<List<Log>>() { });
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    /**
     * Return paging info of all Logs after the given startDate
     *
     * @param startDate          Starting date
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate));
    }

    /**
     * Return paging info of all Logs after the given startDate with an upper limit of the number of results
     *
     * @param startDate          Starting date
     * @param limit              Number of matching results to return
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, int limit) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, limit));
    }

    /**
     * Return paging info of all Logs after the given startDate that match the SCIM filter
     *
     * @param startDate          Starting date
     * @param filterBuilder      Filter to perform, null interpreted to mean no filter
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, filterBuilder, DEFAULT_RESULTS_LIMIT));
    }

    /**
     * Return paging info of all Logs after the given startDate that match the SCIM filter, with an upper limit of the number of results
     *
     * @param startDate          Starting date
     * @param filterBuilder      Filter to perform, null interpreted to mean no filter
     * @param limit              Number of matching results to return
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, filterBuilder, limit));
    }

    /**
     * Return paging info of all Logs after the given startDate that match the search
     *
     * @param startDate          Starting date
     * @param search             Search to perform, null interpreted to mean no search
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, String search) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, search, DEFAULT_RESULTS_LIMIT));
    }

    /**
     * Return paging info of all Logs after the given startDate that match the search, with an upper limit of the number of results
     *
     * @param startDate          Starting date
     * @param search             Search to perform, null interpreted to mean no search
     * @param limit              Number of matching results to return
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, String search, int limit) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, search, limit));
    }

    /**
     * Return paging info of Logs at the given url
     *
     * @param url                Url of search
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(String url) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(url));
    }

    /**
     * Return paging info of all Logs after the given startDate that match the SCIM filter and search, with an upper limit of the number of results
     *
     * @param startDate          Starting date
     * @param filterBuilder      Filter to perform, null interpreted to mean no filter
     * @param search             Search to perform, null interpreted to mean no search
     * @param after              Specifies the pagination cursor for the next page of events
     * @param limit              Number of matching results to return
     * @return PagedResults<Log> List of logs with the paging info
     * @throws IOException       If an input or output exception occurred
     */
    public PagedResults<Log> getLogsPagedResults(DateTime startDate, FilterBuilder filterBuilder, String search, String after, int limit) throws IOException {
        return new PagedResults<Log>(getLogsApiResponse(startDate, filterBuilder, search, after, limit));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate) throws IOException {
        return getLogsApiResponse(startDate, null, null, null, DEFAULT_RESULTS_LIMIT);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, int limit) throws IOException {
        return getLogsApiResponse(startDate, null, null, null, limit);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, FilterBuilder filterBuilder) throws IOException {
        return getLogsApiResponse(startDate, filterBuilder, null, null, DEFAULT_RESULTS_LIMIT);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, FilterBuilder filterBuilder, int limit) throws IOException {
        return getLogsApiResponse(startDate, filterBuilder, null, null, limit);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, String search) throws IOException {
        return getLogsApiResponse(startDate, null, search, null, DEFAULT_RESULTS_LIMIT);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, String search, int limit) throws IOException {
        return getLogsApiResponse(startDate, null, search, null, limit);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<Log> events = unmarshall(resp, new TypeReference<List<Log>>() { });
        return new ApiResponse<List<Log>>(resp, events);
    }

    protected ApiResponse<List<Log>> getLogsApiResponse(DateTime startDate, FilterBuilder filterBuilder, String search, String after, int limit) throws IOException {
        HttpResponse resp = getHttpResponse(getApiUri(startDate, filterBuilder, search, after, limit));
        List<Log> events = unmarshall(resp, new TypeReference<List<Log>>() { });
        return new ApiResponse<List<Log>>(resp, events);
    }

    @VisibleForTesting
    public String getApiUri(DateTime startDate, FilterBuilder filterBuilder, String search, String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();

        if (startDate != null) {
            params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        }

        params.put(LIMIT, Integer.toString(limit));

        if (filterBuilder != null) {
            params.put(FILTER, filterBuilder.toString());
        }

        if (search != null) {
            params.put(SEARCH_QUERY, search);
        }

        if (after != null) {
            params.put(AFTER_CURSOR, after);
        }

        return getEncodedPathWithQueryParams("/", params);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/logs%s", this.apiVersion, relativePath);
    }
}

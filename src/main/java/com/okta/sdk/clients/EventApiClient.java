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

    public List<Event> getEvents() throws IOException {
        return getEventsWithLimit(Utils.getDefaultResultsLimit());
    }

    public List<Event> getEventsWithLimit(int limit) throws IOException {
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsWithStartDate(DateTime startDate) throws IOException {
        return get(getEncodedPath("?" + START_DATE + "=%s", Utils.convertDateTimeToString(startDate)), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsWithFilterAndStartDate(FilterBuilder filterBuilder, DateTime startDate) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        return get(getEncodedPathWithQueryParams("/", params), new TypeReference<List<Event>>() { });
    }

    public List<Event> getEventsWithUrl(String url) throws IOException {
        return get(url, new TypeReference<List<Event>>() { });
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<Event>> getEventsApiResponse() throws IOException {
        return getEventsApiResponseWithLimit(Utils.getDefaultResultsLimit());
    }

    protected ApiResponse<List<Event>> getEventsApiResponseWithLimit(int limit) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    protected ApiResponse<List<Event>> getEventsApiResponseWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    protected ApiResponse<List<Event>> getEventsApiResponseWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(START_DATE, Utils.convertDateTimeToString(startDate));
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    protected ApiResponse<List<Event>> getEventsApiResponseAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    protected ApiResponse<List<Event>> getEventsApiResponseWithUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<Event> events = unmarshall(resp, new TypeReference<List<Event>>() { });
        return new ApiResponse<List<Event>>(resp, events);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    public PagedResults<Event> getEventsPagedResults() throws IOException {
        return new PagedResults<Event>(getEventsApiResponse());
    }

    public PagedResults<Event> getEventsPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithLimit(limit));
    }

    public PagedResults<Event> getEventsPagedResultsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithFilterAndLimit(filterBuilder, limit));
    }

    public PagedResults<Event> getEventsPagedResultsWithStartDateAndLimit(DateTime startDate, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithStartDateAndLimit(startDate, limit));
    }

    public PagedResults<Event> getEventsPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseAfterCursorWithLimit(after, limit));
    }

    public PagedResults<Event> getEventsPagedResultsWithUrl(String url) throws IOException {
        return new PagedResults<Event>(getEventsApiResponseWithUrl(url));
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/events%s", this.apiVersion, relativePath);
    }

}

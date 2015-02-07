package com.okta.sdk.framework;

import java.util.List;

public class PagedResults<T> {

    private ApiResponse<List<T>> apiResponse;

    public PagedResults(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }

    public boolean isLastPage() {
        return !apiResponse.getLinks().containsKey("next");
    }

    public boolean isFirstPage() {
        return apiResponse.getLinks().containsKey("previous");
    }

    public String getNextUrl() {
        return apiResponse.getLinks().get("next").getHref();
    }

    public List<T> getResult() {
        return apiResponse.getResponseObject();
    }

    public ApiResponse<List<T>> getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }
}

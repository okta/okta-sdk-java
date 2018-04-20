package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Algorithms extends ApiObject {
    private Algorithm request;
    private Algorithm response;

    public Algorithm getRequest() {
        return request;
    }

    public void setRequest(Algorithm request) {
        this.request = request;
    }

    public Algorithm getResponse() {
        return response;
    }

    public void setResponse(Algorithm response) {
        this.response = response;
    }
}

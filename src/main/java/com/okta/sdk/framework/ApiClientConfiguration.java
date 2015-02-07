package com.okta.sdk.framework;

public class ApiClientConfiguration {

    private String baseUrl;
    private int apiVersion = 1;
    private String apiToken;

    public ApiClientConfiguration(String baseUrl, String apiToken) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}

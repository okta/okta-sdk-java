package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class Accessibility extends ApiObject {

    /**
     * Enable self-service application assignment
     */
    private Boolean selfService;

    /**
     * Custom error page for this application
     */
    private String errorRedirectUrl;

    /**
     * Gets selfService
     */
    public Boolean getSelfService() {
        return this.selfService;
    }

    /**
     * Sets selfService
     */
    public void setSelfService(Boolean val) {
        this.selfService = val;
    }

    /**
     * Gets errorRedirectUrl
     */
    public String getErrorRedirectUrl() {
        return this.errorRedirectUrl;
    }

    /**
     * Sets errorRedirectUrl
     */
    public void setErrorRedirectUrl(String val) {
        this.errorRedirectUrl = val;
    }
}
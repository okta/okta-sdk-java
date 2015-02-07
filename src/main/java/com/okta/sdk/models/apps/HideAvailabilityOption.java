package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class HideAvailabilityOption extends ApiObject {

    /**
     * Okta Mobile for iOS or Android (pre-dates Android)
     */
    private Boolean iOS;

    /**
     * Okta Web Browser Home Page
     */
    private Boolean web;

    /**
     * Gets iOS
     */
    public Boolean getIOS() {
        return this.iOS;
    }

    /**
     * Sets iOS
     */
    public void setIOS(Boolean val) {
        this.iOS = val;
    }

    /**
     * Gets web
     */
    public Boolean getWeb() {
        return this.web;
    }

    /**
     * Sets web
     */
    public void setWeb(Boolean val) {
        this.web = val;
    }
}
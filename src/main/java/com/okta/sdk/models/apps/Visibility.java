package com.okta.sdk.models.apps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

public class Visibility extends ApiObject {

    /**
     * Automatically log in when user lands on login page
     */
    private Boolean autoSubmitToolbar;

    /**
     * Hides this app for specific end-user apps
     */
    @JsonProperty(value = "hide")
    private HideAvailabilityOption hide;

    /**
     * Displays specific appLinks for the app
     */
    private AppLinks appLinks;

    /**
     * Gets autoSubmitToolbar
     */
    public Boolean getAutoSubmitToolbar() {
        return this.autoSubmitToolbar;
    }

    /**
     * Sets autoSubmitToolbar
     */
    public void setAutoSubmitToolbar(Boolean val) {
        this.autoSubmitToolbar = val;
    }

    /**
     * Gets hide
     */
    public HideAvailabilityOption getHideAvailabilityOption() {
        return this.hide;
    }

    /**
     * Sets hide
     */
    public void setHideAvailabilityOption(HideAvailabilityOption val) {
        this.hide = val;
    }

    /**
     * Gets appLinks
     */
    public AppLinks getAppLinks() {
        return this.appLinks;
    }

    /**
     * Sets appLinks
     */
    public void setAppLinks(AppLinks val) {
        this.appLinks = val;
    }
}
package com.okta.sdk.models.apps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

public class Settings extends ApiObject {

    /**
     * The settings for the app
     */
    @JsonProperty("app")
    private AppSettings appSettings;

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }
}
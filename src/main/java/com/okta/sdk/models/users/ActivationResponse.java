package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class ActivationResponse extends ApiObject {

    private String activationUrl;

    /**
     * Gets activationUrl
     */
    public String getActivationUrl() {
        return this.activationUrl;
    }

    /**
     * Sets activationUrl
     */
    public void setActivationUrl(String val) {
        this.activationUrl = val;
    }
}
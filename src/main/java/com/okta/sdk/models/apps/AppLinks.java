package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class AppLinks extends ApiObject {

    private Boolean login;

    /**
     * Gets login
     */
    public Boolean getLogin() {
        return this.login;
    }

    /**
     * Sets login
     */
    public void setLogin(Boolean val) {
        this.login = val;
    }
}
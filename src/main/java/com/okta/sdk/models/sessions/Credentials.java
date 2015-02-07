package com.okta.sdk.models.sessions;

import com.okta.sdk.framework.ApiObject;

public class Credentials extends ApiObject {

    /**
     * a user's login name
     */
    private String username;

    /**
     * a user's password
     */
    private String password;

    /**
     * Gets username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets username
     */
    public void setUsername(String val) {
        this.username = val;
    }

    /**
     * Gets password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets password
     */
    public void setPassword(String val) {
        this.password = val;
    }
}
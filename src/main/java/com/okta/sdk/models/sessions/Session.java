package com.okta.sdk.models.sessions;

import com.okta.sdk.framework.ApiObject;

public class Session extends ApiObject {

    /**
     * unique key for session
     */
    private String id;

    /**
     * unique key for the user
     */
    private String userId;

    /**
     * indicates whether the user has enrolled a valid MFA credential
     */
    private Boolean mfaActive;

    /**
     * One-time token which can be used to obtain a session cookie for your organization by visiting either an application’s embed link or a session redirect URL.
     */
    private String cookieToken;

    /**
     * URL for a a transparent 1x1 pixel image which contains a one-time token which when visited sets the session cookie in your browser for your organization.
     */
    private String cookieTokenUrl;

    /**
     * Gets id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets id
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Gets userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets userId
     */
    public void setUserId(String val) {
        this.userId = val;
    }

    /**
     * Gets mfaActive
     */
    public Boolean getMfaActive() {
        return this.mfaActive;
    }

    /**
     * Sets mfaActive
     */
    public void setMfaActive(Boolean val) {
        this.mfaActive = val;
    }

    /**
     * Gets cookieToken
     */
    public String getCookieToken() {
        return this.cookieToken;
    }

    /**
     * Sets cookieToken
     */
    public void setCookieToken(String val) {
        this.cookieToken = val;
    }

    /**
     * Gets cookieTokenUrl
     */
    public String getCookieTokenUrl() {
        return this.cookieTokenUrl;
    }

    /**
     * Sets cookieTokenUrl
     */
    public void setCookieTokenUrl(String val) {
        this.cookieTokenUrl = val;
    }
}
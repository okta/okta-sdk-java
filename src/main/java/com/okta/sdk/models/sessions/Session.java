package com.okta.sdk.models.sessions;

import com.fasterxml.jackson.annotation.JsonValue;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.identityproviders.IdpObject;
import org.joda.time.DateTime;

import java.util.List;

public class Session extends ApiObject {

    public enum Status {
        ACTIVE,         //The session is active
        MFA_ENROLL,     //The user credentials are valid, but MFA is required and no factors are set up yet
        MFA_REQUIRED;   //The user credentials are valid, but MFA is required
    }

    // 	Authentication Method Reference
    public enum AMR {
        PWD("pwx"),       // Password authentication
        SWK("swk"),       // Proof-of-possession (PoP) of a software-secured key.
        HWK("hwk"),       // Proof-of-possession (PoP) of a hardware-secured key.
        OTP("otp"),       // One-time password
        SMS("sms"),       // SMS text message
        KBA("kba"),       // Knowlege-based authentication
        MFA("mfa"),       // Multiple factor authentication
        UNKNOWN(null);

        private final String value;

        AMR(final String amr) {
            this.value = amr;
        }

        @Override
        public String toString() { return value; }

        @JsonValue
        final String value() {
            return this.value;
        }

        public static AMR fromValue(String value) {
            if (value != null) {
                for (AMR amr : values()) {
                    if (amr.value.equals(value)) {
                        return amr;
                    }
                }
            }
            // return a default value
            return UNKNOWN;
        }
    }

    /**
     * unique key for session
     */
    private String id;

    /**
     * unique key for the user
     */
    private String userId;

    /**
     * unique identifier for the user
     */
    private String login;

    /**
     * timestamp when session expires
     */
    private DateTime expiresAt;

    /**
     * current status of the session
     */
    private Status status;

    /**
     * timestamp when user last performed primary authentication (with password)
     */
    private DateTime lastPasswordVerification;

    /**
     *  authentication method reference
     */
    private List<String> amr;

    /**
     *  identity provider used to authenticate the user
     */
    private IdpObject idp;

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
     * Gets login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets expiresAt
     */
    public DateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets expiresAt
     */
    public void setExpiresAt(DateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Gets status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets lastPasswordVerification
     */
    public DateTime getLastPasswordVerification() {
        return lastPasswordVerification;
    }

    /**
     * Sets lastPasswordVerification
     */
    public void setLastPasswordVerification(DateTime lastPasswordVerification) {
        this.lastPasswordVerification = lastPasswordVerification;
    }

    /**
     * Gets amr
     */
    public List<String> getAmr() {
        return amr;
    }

    /**
     * Sets amr
     */
    public void setAmr(List<String> amr) {
        this.amr = amr;
    }

    /**
     * Gets idp
     */
    public IdpObject getIdp() {
        return idp;
    }

    /**
     * Sets idp
     */
    public void setIdp(IdpObject idp) {
        this.idp = idp;
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
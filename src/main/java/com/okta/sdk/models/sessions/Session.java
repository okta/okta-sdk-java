/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.sessions;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

import java.util.List;

public class Session extends ApiObject {

    /**
     * unique key for session
     */
    private String id;

    /**
     * unique key for the user
     */
    private String userId;

    private String login;

    private String status;

    private DateTime createdAt;

    private DateTime expiresAt;

    private DateTime lastLogin;

    private DateTime lastPasswordVerification;

    private DateTime lastFactorVerification;

    private List<String> amr;

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
        return this.login;
    }

    /**
     * Sets login
     */
    public void setLogin(String val) {
        this.login = val;
    }

    /**
     * Gets status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets login
     */
    public void setStatus(String val) {
        this.status = val;
    }

    /**
     * Gets createdAt
     */
    public DateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Sets createdAt
     */
    public void setCreatedAt(DateTime val) {
        this.createdAt = val;
    }

    /**
     * Gets expiresAt
     */
    public DateTime getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * Sets expiresAt
     */
    public void setExpiresAt(DateTime val) {
        this.expiresAt = val;
    }

    /**
     * Gets lastLogin
     */
    public DateTime getLastLogin() {
        return this.lastLogin;
    }

    /**
     * Sets lastLogin
     */
    public void setLastLogin(DateTime val) {
        this.lastLogin = val;
    }

    /**
     * Gets lastPasswordVerification
     */
    public DateTime getLastPasswordVerification() {
        return this.lastPasswordVerification;
    }

    /**
     * Sets lastPasswordVerification
     */
    public void setLastPasswordVerification(DateTime val) {
        this.lastPasswordVerification = val;
    }

    /**
     * Gets lastFactorVerification
     */
    public DateTime getLastFactorVerification() {
        return this.lastFactorVerification;
    }

    /**
     * Sets lastFactorVerification
     */
    public void setLastFactorVerification(DateTime val) {
        this.lastFactorVerification = val;
    }

    /**
     * Gets amr
     */
    public List<String> getAmr() {
        return this.amr;
    }

    /**
     * Sets amr
     */
    public void setAmr(List<String> val) {
        this.amr = val;
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
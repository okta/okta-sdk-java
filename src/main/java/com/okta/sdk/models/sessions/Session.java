/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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
     * Unique key for session.
     */
    private String id;

    /**
     * Unique key for the user.
     */
    private String userId;

    /**
     * Login for the user.
     */
    private String login;

    /**
     * Status of the session.
     */
    private String status;

    /**
     * Time the session was created.
     */
    private DateTime createdAt;

    /**
     * Time the session expires.
     */
    private DateTime expiresAt;

    /**
     * Time of the last login.
     */
    private DateTime lastLogin;

    /**
     * Time of the last password verification.
     */
    private DateTime lastPasswordVerification;

    /**
     * Time of the last factor verification.
     */
    private DateTime lastFactorVerification;

    /**
     * List of Authentication Method References.
     */
    private List<String> amr;

    /**
     * Indicates whether the user has enrolled a valid MFA credential.
     */
    private Boolean mfaActive;

    /**
     * One-time token which can be used to obtain a session cookie for your organization by visiting either an
     * application’s embed link or a session redirect URL.
     */
    private String cookieToken;

    /**
     * URL for a a transparent 1x1 pixel image which contains a one-time token which when visited sets the
     * session cookie in your browser for your organization.
     */
    private String cookieTokenUrl;

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the ID.
     * @param val {@link String}
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the userId.
     * @return {@link String}
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the userId.
     * @param val {@link String}
     */
    public void setUserId(String val) {
        this.userId = val;
    }

    /**
     * Returns the login.
     * @return {@link String}
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Sets the login.
     * @param val {@link String}
     */
    public void setLogin(String val) {
        this.login = val;
    }

    /**
     * Returns the status.
     * @return {@link String}
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the login.
     * @param val {@link String}
     */
    public void setStatus(String val) {
        this.status = val;
    }

    /**
     * Returns the createdAt.
     * @return {@link DateTime}
     */
    public DateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Sets the createdAt.
     * @param val {@link DateTime}
     */
    public void setCreatedAt(DateTime val) {
        this.createdAt = val;
    }

    /**
     * Returns the expiresAt.
     * @return {@link DateTime}
     */
    public DateTime getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * Sets the expiresAt.
     * @param val {@link DateTime}
     */
    public void setExpiresAt(DateTime val) {
        this.expiresAt = val;
    }

    /**
     * Returns the lastLogin.
     * @return {@link DateTime}
     */
    public DateTime getLastLogin() {
        return this.lastLogin;
    }

    /**
     * Sets the lastLogin.
     * @param val {@link DateTime}
     */
    public void setLastLogin(DateTime val) {
        this.lastLogin = val;
    }

    /**
     * Returns the lastPasswordVerification.
     * @return {@link DateTime}
     */
    public DateTime getLastPasswordVerification() {
        return this.lastPasswordVerification;
    }

    /**
     * Sets the lastPasswordVerification.
     * @param val {@link DateTime}
     */
    public void setLastPasswordVerification(DateTime val) {
        this.lastPasswordVerification = val;
    }

    /**
     * Returns the lastFactorVerification.
     * @return {@link DateTime}
     */
    public DateTime getLastFactorVerification() {
        return this.lastFactorVerification;
    }

    /**
     * Sets the lastFactorVerification.
     * @param val {@link DateTime}
     */
    public void setLastFactorVerification(DateTime val) {
        this.lastFactorVerification = val;
    }

    /**
     * Returns the amr.
     * @return {@link List}
     */
    public List<String> getAmr() {
        return this.amr;
    }

    /**
     * Sets the amr.
     * @param val {@link List}
     */
    public void setAmr(List<String> val) {
        this.amr = val;
    }

    /**
     * Returns the mfaActive.
     * @return {@link Boolean}
     */
    public Boolean getMfaActive() {
        return this.mfaActive;
    }

    /**
     * Sets the mfaActive.
     * @param val {@link Boolean}
     */
    public void setMfaActive(Boolean val) {
        this.mfaActive = val;
    }

    /**
     * Returns the cookieToken.
     * @return {@link String}
     */
    public String getCookieToken() {
        return this.cookieToken;
    }

    /**
     * Sets the cookieToken.
     * @param val {@link String}
     */
    public void setCookieToken(String val) {
        this.cookieToken = val;
    }

    /**
     * Returns the cookieTokenUrl.
     * @return {@link String}
     */
    public String getCookieTokenUrl() {
        return this.cookieTokenUrl;
    }

    /**
     * Sets the cookieTokenUrl.
     * @param val {@link String}
     */
    public void setCookieTokenUrl(String val) {
        this.cookieTokenUrl = val;
    }
}

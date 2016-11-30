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
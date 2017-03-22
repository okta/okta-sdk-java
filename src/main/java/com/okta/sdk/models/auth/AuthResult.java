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

package com.okta.sdk.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import org.joda.time.DateTime;

import java.util.Map;

public class AuthResult extends ApiObject {

    /**
     * Public status options.
     */
    public static class Status {
        public static final String PASSWORD_WARN = "PASSWORD_WARN";             //The user credentials are valid, but user has to be warned about password expiry
        public static final String PASSWORD_EXPIRED = "PASSWORD_EXPIRED";       //The user credentials are valid but expired; the user must change them
        public static final String RECOVERY = "RECOVERY";                       //The user is in the middle of the forgot-password flow
        public static final String RECOVERY_CHALLENGE = "RECOVERY_CHALLENGE";   //The user has a second factor challenge to fulfil for password recovery
        public static final String PASSWORD_RESET = "PASSWORD_RESET";           //The user has answered their recovery question and needs to set a new password
        public static final String LOCKED_OUT = "LOCKED_OUT";                   //The user account is locked out; self-service unlock or admin unlock is required
        public static final String MFA_ENROLL = "MFA_ENROLL";                   //The user credentials are valid, but MFA is required and no factors are set up yet
        public static final String MFA_ENROLL_ACTIVATE = "MFA_ENROLL_ACTIVATE"; //User enrolled for MFA but needs to be activated using the activation code(s)
        public static final String MFA_REQUIRED = "MFA_REQUIRED";               //The user credentials are valid, but MFA is required
        public static final String MFA_CHALLENGE = "MFA_CHALLENGE";             //The MFA passCode has been sent, and we are waiting for the user to enter it back
        public static final String SUCCESS = "SUCCESS";                         //The user is authenticated
    }

    /**
     * Token for authentication state.
     */
    private String stateToken;

    /**
     * Status of authentication.
     */
    private String status;

    /**
     * Time the authentication expires.
     */
    private DateTime expiresAt;

    /**
     * Relay state of authentication result.
     */
    private String relayState;

    /**
     * Result of MFA.
     */
    private String factorResult;

    /**
     * MFA result message.
     */
    private String factorResultMessage;

    /**
     * Credential recovery token.
     */
    private String recoveryToken;

    /**
     * MFA factor type.
     */
    private String factorType;

    /**
     * Credential recovery type.
     */
    private String recoveryType;

    /**
     * Token for Okta session.
     */
    private String sessionToken;

    /**
     * Identity token (OIDC)
     */
    private String idToken;

    /**
     * HAL links for authentication response.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded for authentication response.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Returns the stateToken.
     * @return {@link String}
     */
    public String getStateToken() {
        return this.stateToken;
    }

    /**
     * Sets the stateToken.
     * @param val {@link String}
     */
    public void setStateToken(String val) {
        this.stateToken = val;
    }

    /**
     * Returns the status.
     * @return {@link String}
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status.
     * @param val {@link String}
     */
    public void setStatus(String val) {
        this.status = val;
    }

    /**
     * Returns the expiresAt time.
     * @return {@link DateTime}
     */
    public DateTime getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * Sets the expiresAt time.
     * @param val {@link DateTime}
     */
    public void setExpiresAt(DateTime val) {
        this.expiresAt = val;
    }

    /**
     * Returns the relayState.
     * @return {@link String}
     */
    public String getRelayState() {
        return this.relayState;
    }

    /**
     * Sets the relayState.
     * @param val {@link String}
     */
    public void setRelayState(String val) {
        this.relayState = val;
    }

    /**
     * Returns the factorResult.
     * @return {@link String}
     */
    public String getFactorResult() {
        return this.factorResult;
    }

    /**
     * Sets the factorResult.
     * @param val {@link String}
     */
    public void setFactorResult(String val) {
        this.factorResult = val;
    }

    /**
     * Returns the factorResultMessage.
     * @return {@link String}
     */
    public String getFactorResultMessage() {
        return this.factorResultMessage;
    }

    /**
     * Sets the factorResultMessage.
     * @param val {@link String}
     */
    public void setFactorResultMessage(String val) {
        this.factorResultMessage = val;
    }

    /**
     * Returns the recoveryToken.
     * @return {@link String}
     */
    public String getRecoveryToken() {
        return this.recoveryToken;
    }

    /**
     * Sets the recoveryToken.
     * @param val {@link String}
     */
    public void setRecoveryToken(String val) {
        this.recoveryToken = val;
    }

    /**
     * Returns the sessionToken.
     * @return {@link String}
     */
    public String getSessionToken() {
        return this.sessionToken;
    }

    /**
     * Sets the sessionToken.
     * @param val {@link String}
     */
    public void setSessionToken(String val) {
        this.sessionToken = val;
    }

    /**
     * Returns the idToken.
     * @return {@link String}
     */
    public String getIdToken() {
        return this.idToken;
    }

    /**
     * Sets the idToken.
     * @param val {@link String}
     */
    public void setIdToken(String val) {
        this.idToken = val;
    }

    /**
     * Returns the links object.
     * @return {@link Map}
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets the links object.
     * @param val {@link Map}
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Returns the embedded object.
     * @return {@link Map}
     */
    public Map<String, Object> getEmbedded() {
        return this.embedded;
    }

    /**
     * Sets the embedded object.
     * @param val {@link Map}
     */
    public void setEmbedded(Map<String, Object> val) {
        this.embedded = val;
    }

    /**
     * Returns the factorType.
     * @return {@link String}
     */
    public String getFactorType() {
        return this.factorType;
    }

    /**
     * Sets the factorType.
     * @param val {@link String}
     */
    public void setFactorType(String val) {
        this.factorType = val;
    }

    /**
     * Returns the recoveryType.
     * @return {@link String}
     */
    public String getRecoveryType() {
        return this.recoveryType;
    }

    /**
     * Sets the recoveryType.
     * @param val {@link String}
     */
    public void setRecoveryType(String val) {
        this.recoveryType = val;
    }
}

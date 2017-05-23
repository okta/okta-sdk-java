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

package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.models.auth.OrgAnonymousInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthApiClient extends JsonApiClient {

    public static final String STATE_TOKEN = "stateToken";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String RELAY_STATE = "relayState";
    public static final String CONTEXT = "context";
    public static final String PASSCODE = "passCode";
    public static final String SECURITY_ANSWER = "answer";
    public static final String FACTOR = "factor";
    public static final String FACTOR_TYPE = "factorType";
    public static final String PROVIDER = "provider";
    public static final String PROFILE = "profile";
    public static final String RECOVERY_TOKEN = "recoveryToken";
    public static final String OLD_PASSWORD = "oldPassword";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String TOKEN = "token";

    public enum FactorType {
        EMAIL,
        SMS
    }

    public AuthApiClient(ApiClientConfiguration config) {
        super(config);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    /**
     * Return anonymous information about organization.
     *
     * @return {@link OrgAnonymousInfo}             Organization's information.
     * @throws IOException                          If an input or output exception occurred.
     */
    public OrgAnonymousInfo getAnonymousInfo() throws IOException {
        return get(getEncodedPath("/info"), new TypeReference<OrgAnonymousInfo>() { });
    }

    // START AUTHENTICATION

    /**
     * Authenticate into organization.
     *
     * @param  username {@link String}               Username of the user attempting authentication.
     * @param  password {@link String}               Password of the user attempting authentication.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticate(String username, String password, String relayState) throws IOException {
        return authenticate(username, password, relayState, "session_token", false);
    }

    /**
     * Authenticate into organization.
     *
     * @param  username {@link String}               Username of the user attempting authentication.
     * @param  password {@link String}               Password of the user attempting authentication.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  responseType {@link String}           How the OIDC or OAuth2 tokens are returned.
     * @param  forceMFA {@link Boolean}              Weather to enforce MFA on login attempt.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticate(String username, String password, String relayState, String responseType, boolean forceMFA) throws IOException {
        return authenticate(username, password, relayState, responseType, forceMFA, null);
    }

    /**
     * Authenticate into organization.
     *
     * @param  username {@link String}               Username of the user attempting authentication.
     * @param  password {@link String}               Password of the user attempting authentication.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  responseType {@link String}           How the OIDC or OAuth2 tokens are returned.
     * @param  forceMFA {@link Boolean}              Weather to enforce MFA on login attempt.
     * @param  context {@link Map}                   Additional info for the authentication transaction.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticate(String username, String password, String relayState, String responseType, boolean forceMFA, Map<String, String> context) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);
        params.put(RELAY_STATE, relayState);
        params.put(CONTEXT, context);
        return post(getEncodedPath("?response_type=%s&force_mfa=%s", responseType, String.valueOf(forceMFA)), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Authenticate with activation token
     *
     * @param  token {@link String}                 activationToken to use for authentication.
     * @return {@link AuthResult}                   Result of the authentication transaction.
     * @throws IOException                          If an input or output exception occurred.
     */
    public AuthResult authenticateWithActivationToken(String token) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(TOKEN, token);
        return post(getEncodedPath("/"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Authenticate with a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  passCode {@link String}               OTP sent to device.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode) throws IOException {
        return authenticateWithFactor(stateToken, factorId, passCode, null);
    }

    /**
     * Authenticate with a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  passCode {@link String}               OTP sent to device.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode, String relayState) throws IOException {
        return authenticateWithFactor(stateToken, factorId, passCode, relayState, false);
    }

    /**
     * Authenticate with a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  passCode {@link String}               OTP sent to device.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  rememberDevice {@link Boolean}        Toggle to remember device information.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode, String relayState, boolean rememberDevice) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(PASSCODE, passCode);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/verify?rememberDevice=%s", factorId, String.valueOf(rememberDevice)), params, new TypeReference<AuthResult>() { });
    }

    // MFA MANAGEMENT

    /**
     * Enroll in a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  factorType {@link String}             Type of factor.
     * @param  provider {@link String}               Name of provider for factor.
     * @param  profile {@link Map}                   Details of the factor.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult enrollInFactor(String stateToken, String factorType, String provider, Map<String, String> profile) throws IOException {
        return enrollInFactor(stateToken, factorType, provider, profile, null);
    }

    /**
     * Enroll in a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  factorType {@link String}             Type of factor.
     * @param  provider {@link String}               Name of provider for factor.
     * @param  profile {@link Map}                   Details of the factor.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult enrollInFactor(String stateToken, String factorType, String provider, Map<String, String> profile, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(FACTOR_TYPE, factorType);
        params.put(PROVIDER, provider);
        params.put(PROFILE, profile);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Activate a factor.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  passCode {@link String}               OTP sent to device.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult activateFactor(String stateToken, String factorId, String passCode) throws IOException {
        return activateFactor(stateToken, factorId, passCode, null);
    }

    /**
     * Activate a factor by ID.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  passCode {@link String}               OTP sent to device.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult activateFactor(String stateToken, String factorId, String passCode, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(PASSCODE, passCode);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/lifecycle/activate", factorId), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Resend factor code.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  factorId {@link String}               ID for the MFA factor.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult resendCode(String stateToken, String relayState, String factorId) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/lifecycle/resend", factorId), params, new TypeReference<AuthResult>() { });
    }

    // CREDENTIAL MANAGEMENT

    /**
     * Change a password.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  oldPassword {@link String}            Previous password of the user.
     * @param  newPassword {@link String}            New password for the user.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult changePassword(String stateToken, String relayState, String oldPassword, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(OLD_PASSWORD, oldPassword);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/credentials/change_password"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Reset the password.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  newPassword {@link String}            New password request.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult resetPassword(String stateToken, String relayState, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/credentials/reset_password"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Start the forgot password flow.
     *
     * @param  username {@link String}               Username of the user.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult forgotPassword(String username, String relayState) throws IOException {
        return forgotPassword(username, null, relayState);
    }

    /**
     * Start the forgot password flow.
     *
     * @param  username {@link String}               Username of the user.
     * @param  factorType {@link String}             Type of factor.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult forgotPassword(String username, FactorType factorType, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(RELAY_STATE, relayState);
        if (factorType != null) {
            params.put(FACTOR_TYPE, factorType);
        }
        return post(getEncodedPath("/recovery/password"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Update account by forgotten password answer.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  securityAnswer {@link String}         User's answer to security question.
     * @param  newPassword {@link String}            New password request.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult forgotPasswordAnswer(String stateToken, String relayState, String securityAnswer, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(SECURITY_ANSWER, securityAnswer);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/recovery/answer"), params, new TypeReference<AuthResult>() { });
    }

    // RECOVERY

    /**
     * Validate the recovery token.
     *
     * @param recoveryToken {@link String}           One-time token for recovery to be distributed to end-user.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult validateRecoveryToken(String recoveryToken) throws IOException {
        return validateRecoveryToken(recoveryToken, null);
    }

    /**
     * Validate the recovery token.
     *
     * @param recoveryToken {@link String}           One-time token for recovery to be distributed to end-user.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult validateRecoveryToken(String recoveryToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RECOVERY_TOKEN, recoveryToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/recovery/token"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Start the unlock account flow.
     *
     * @param  username {@link String}               Username of the user.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult startUnlockAccount(String username, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/recovery/unlock"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Unlock account via security answer.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @param  securityAnswer {@link String}         User's answer to security question.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult unlockAccountAnswer(String stateToken, String relayState, String securityAnswer) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(SECURITY_ANSWER, securityAnswer);
        return post(getEncodedPath("/recovery/answer"), params, new TypeReference<AuthResult>() { });
    }

    // STATE MANAGEMENT

    /**
     * Return the previous state the authentication transaction.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult previousState(String stateToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/previous"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Return the status the authentication transaction.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred.
     */
    public AuthResult getStatus(String stateToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Verify the authentication transaction.
     *
     * @param  factorId {@link String}               ID for the MFA factor.
     * @param  transactionId {@link String}          ID of the authentication transaction.
     * @param  userResponse {@link String}           User's response to the MFA factor.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred
     */
    public AuthResult verifyTransaction(String factorId, String transactionId, String userResponse) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("result", userResponse);
        return post(getEncodedPath("/factors/%s/transactions/%s/verify", factorId, transactionId), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Cancel the authentication transaction.
     *
     * @param  stateToken {@link String}             Encoded current state of transaction.
     * @param  relayState {@link String}             Opaque identifier.
     * @return {@link AuthResult}                    Result of the authentication transaction.
     * @throws IOException                           If an input or output exception occurred
     */
    public AuthResult cancelTransaction(String stateToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/cancel"), params, new TypeReference<AuthResult>() { });
    }

    /**
     * Overriding method to get full path from relative path.
     *
     * @param  relativePath {@link String}
     * @return {@link String}
     */
    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v1/authn%s", relativePath);
    }

}
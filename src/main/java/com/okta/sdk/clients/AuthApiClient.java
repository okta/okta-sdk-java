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

    public AuthApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v1/authn%s", relativePath);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public OrgAnonymousInfo getAnonymousInfo() throws IOException {
        return get(getEncodedPath("/info"), new TypeReference<OrgAnonymousInfo>() { });
    }

    // START AUTHENTICATION

    public AuthResult authenticate(String username, String password, String relayState) throws IOException {
        return authenticate(username, password, relayState, "session_token", false);
    }

    public AuthResult authenticate(String username, String password, String relayState, String responseType, boolean forceMFA) throws IOException {
        return authenticate(username, password, relayState, responseType, forceMFA, null);
    }

    public AuthResult authenticate(String username, String password, String relayState, String responseType, boolean forceMFA, Map<String, String> context) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);
        params.put(RELAY_STATE, relayState);
        params.put(CONTEXT, context);
        return post(getEncodedPath("?response_type=%s&force_mfa=%s", responseType, String.valueOf(forceMFA)), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode) throws IOException {
        return authenticateWithFactor(stateToken, factorId, passCode, null);
    }

    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode, String relayState) throws IOException {
        return authenticateWithFactor(stateToken, factorId, passCode, relayState, false);
    }

    public AuthResult authenticateWithFactor(String stateToken, String factorId, String passCode, String relayState, boolean rememberDevice) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(PASSCODE, passCode);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/verify?rememberDevice=%s", factorId, String.valueOf(rememberDevice)), params, new TypeReference<AuthResult>() { });
    }

    // MFA MANAGEMENT

    public AuthResult enrollInFactor(String stateToken, String factorType, String provider, Map<String, String> profile) throws IOException {
        return enrollInFactor(stateToken, factorType, provider, profile, null);
    }

    public AuthResult enrollInFactor(String stateToken, String factorType, String provider, Map<String, String> profile, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(FACTOR_TYPE, factorType);
        params.put(PROVIDER, provider);
        params.put(PROFILE, profile);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult activateFactor(String stateToken, String factorId, String passCode) throws IOException {
        return activateFactor(stateToken, factorId, passCode, null);
    }

    public AuthResult activateFactor(String stateToken, String factorId, String passCode, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(PASSCODE, passCode);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/lifecycle/activate", factorId), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult resendCode(String stateToken, String relayState, String factorId) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/factors/%s/lifecycle/resend", factorId), params, new TypeReference<AuthResult>() { });
    }

    // CREDENTIAL MANAGEMENT

    public AuthResult changePassword(String stateToken, String relayState, String oldPassword, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(OLD_PASSWORD, oldPassword);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/credentials/change_password"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult resetPassword(String stateToken, String relayState, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/credentials/reset_password"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult forgotPassword(String username, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/recovery/password"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult forgotPasswordAnswer(String stateToken, String relayState, String securityAnswer, String newPassword) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(SECURITY_ANSWER, securityAnswer);
        params.put(NEW_PASSWORD, newPassword);
        return post(getEncodedPath("/recovery/answer"), params, new TypeReference<AuthResult>() { });
    }

    // RECOVERY

    public AuthResult validateRecoveryToken(String recoveryToken) throws IOException {
        return validateRecoveryToken(recoveryToken, null);
    }

    public AuthResult validateRecoveryToken(String recoveryToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RECOVERY_TOKEN, recoveryToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/recovery/token"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult startUnlockAccount(String username, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USERNAME, username);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/recovery/unlock"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult unlockAccountAnswer(String stateToken, String relayState, String securityAnswer) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        params.put(SECURITY_ANSWER, securityAnswer);
        return post(getEncodedPath("/recovery/answer"), params, new TypeReference<AuthResult>() { });
    }

    // STATE MANAGEMENT

    public AuthResult previousState(String stateToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/previous"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult getStatus(String stateToken, String relayState) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(STATE_TOKEN, stateToken);
        params.put(RELAY_STATE, relayState);
        return post(getEncodedPath("/"), params, new TypeReference<AuthResult>() { });
    }

    public AuthResult verifyTransaction(String factorId, String transactionId, String userResponse) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("result", userResponse);
        return post(getEncodedPath("/factors/%s/transactions/%s/verify", factorId, transactionId), params, new TypeReference<AuthResult>() { });
    }
}

package com.okta.sdk.impl.http.authc;

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.support.RequestAuthenticationException;
import com.okta.sdk.lang.Assert;


public class SswsAuthenticator implements RequestAuthenticator {

    public static final String AUTHENTICATION_SCHEME = "SSWS";

    private final ApiKeyCredentials apiKeyCredentials;

    public SswsAuthenticator(ApiKeyCredentials apiKeyCredentials) {
        Assert.notNull(apiKeyCredentials, "apiKeyCredentials must be not be null.");
        this.apiKeyCredentials = apiKeyCredentials;

        ApiKey apiKey = apiKeyCredentials.getApiKey();
        Assert.notNull(apiKey, "apiKeyCredentials argument cannot have a null apiKey");
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + apiKeyCredentials.getSecret());
    }
}

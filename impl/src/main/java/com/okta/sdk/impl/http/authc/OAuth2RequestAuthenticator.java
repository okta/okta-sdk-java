package com.okta.sdk.impl.http.authc;

import com.okta.commons.http.Request;
import com.okta.commons.http.authc.RequestAuthenticationException;
import com.okta.commons.http.authc.RequestAuthenticator;
import com.okta.commons.lang.Assert;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.util.OAuth2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2RequestAuthenticator implements RequestAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(OAuth2RequestAuthenticator.class);

    public static final String AUTHENTICATION_SCHEME = "OAUTH2";

    private final ClientCredentials<String> clientCredentials;

    public OAuth2RequestAuthenticator(ClientCredentials<String> clientCredentials) {
        Assert.notNull(clientCredentials, "clientCredentials must be not be null.");
        this.clientCredentials = clientCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        //request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + clientCredentials.getCredentials());
        log.debug("Setting Bearer token [{}] in AUTH header...", clientCredentials.getCredentials());
        request.getHeaders().set(AUTHORIZATION_HEADER, "Bearer " + clientCredentials.getCredentials());
    }
}

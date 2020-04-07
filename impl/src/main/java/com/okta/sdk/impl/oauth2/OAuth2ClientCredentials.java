package com.okta.sdk.impl.oauth2;

import com.okta.commons.lang.Assert;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.oauth2.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class OAuth2ClientCredentials implements ClientCredentials<String> {
    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientCredentials.class);

    private AccessTokenRetrieverService accessTokenRetrieverService;

    public OAuth2ClientCredentials(AccessTokenRetrieverService accessTokenRetrieverService) {
        Assert.notNull(accessTokenRetrieverService, "accessTokenRetrieverService must not be null.");
        this.accessTokenRetrieverService = accessTokenRetrieverService;
    }

    public String getCredentials() {
        String accessToken;

        try {
            OAuth2AccessToken oAuth2AccessToken = accessTokenRetrieverService.getOAuth2AccessToken();
            accessToken = oAuth2AccessToken.getAccessToken();
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Exception occurred", e);
            throw new RuntimeException(e);
        }

        return accessToken;
    }

    @Override
    public String toString() {
        return "<OAuth2ClientCredentials>"; //never ever print the secret
    }
}

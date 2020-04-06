package com.okta.sdk.authc.credentials;

import com.okta.commons.lang.Assert;
import com.okta.sdk.oauth2.OAuth2AccessToken;

public class OAuth2ClientCredentials implements ClientCredentials<String> {

    private final OAuth2AccessToken oAuth2AccessToken;

    public OAuth2ClientCredentials(OAuth2AccessToken oAuth2AccessToken) {
        Assert.notNull(oAuth2AccessToken, "oAuth2AccessToken must not be null.");
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    public String getCredentials() {
        return oAuth2AccessToken.getAccessToken();
    }

    @Override
    public String toString() {
        return "<OAuth2ClientCredentials>"; //never ever print the secret
    }
}

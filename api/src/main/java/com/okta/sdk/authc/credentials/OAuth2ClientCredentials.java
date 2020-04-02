package com.okta.sdk.authc.credentials;

import com.okta.commons.configcheck.ConfigurationValidator;

public class OAuth2ClientCredentials implements ClientCredentials<String> {

    private final String accessToken;

    public OAuth2ClientCredentials(String accessToken) {
        ConfigurationValidator.assertApiToken(accessToken);
        this.accessToken = accessToken;
    }

    public String getCredentials() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "<OAuth2ClientCredentials>"; //never ever print the secret
    }

    @Override
    public int hashCode() {
        return accessToken != null ? accessToken.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OAuth2ClientCredentials) {
            OAuth2ClientCredentials other = (OAuth2ClientCredentials) o;
            return accessToken != null ? accessToken.equals(other.accessToken) : other.accessToken == null;
        }

        return false;
    }
}

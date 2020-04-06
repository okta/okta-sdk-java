package com.okta.sdk.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth2AccessToken {
    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("access_token")
    private String accessToken;

    private String scope;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "OAuth2AccessToken [tokenType=" + tokenType +
            ", expiresIn=" + expiresIn +
            ", accessToken=" + accessToken +
            ", scope=" + scope + "]";
    }
}

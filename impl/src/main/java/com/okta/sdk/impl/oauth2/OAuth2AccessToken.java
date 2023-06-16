/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Represents the OAuth2 access token returned by Authorization server.
 *
 * @since 1.6.0
 */
public class OAuth2AccessToken {

    /* Token body constants */
    public static final String TOKEN_TYPE_KEY = "token_type";
    public static final String EXPIRES_IN_KEY = "expires_in";

    public static final String ID_TOKEN_KEY = "id_token";

    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String SCOPE_KEY = "scope";

    @JsonProperty(TOKEN_TYPE_KEY)
    private String tokenType;

    @JsonProperty(EXPIRES_IN_KEY)
    private Integer expiresIn;

    @JsonProperty(ACCESS_TOKEN_KEY)
    private String accessToken;

    @JsonProperty(ID_TOKEN_KEY)
    private String idToken;

    @JsonProperty(SCOPE_KEY)
    private String scope;

    private Instant issuedAt = Instant.now();

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
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

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public boolean hasExpired() {
        return getExpiresAt().isBefore(Instant.now());
    }

    public Instant getExpiresAt() {
        return this.getIssuedAt().plusSeconds(this.getExpiresIn());
    }

    // for testing purposes
    void expireNow() {
        this.setExpiresIn(Integer.MIN_VALUE);
    }

    @Override
    public String toString() {
        return "OAuth2AccessToken [tokenType=" + tokenType +
            ", issuedAt=" + issuedAt +
            ", expiresIn=" + expiresIn +
            ", idToken=xxxxx" +
            ", accessToken=xxxxx" +
            ", scope=" + scope + "]";
    }
}

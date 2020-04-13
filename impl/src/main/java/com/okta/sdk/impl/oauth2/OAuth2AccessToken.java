/*
 * Copyright 2020 Okta, Inc.
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

import java.time.Duration;
import java.time.Instant;

public class OAuth2AccessToken {

    /* Token body constants */
    public static final String TOKEN_TYPE_KEY = "token_type";
    public static final String EXPIRES_IN_KEY = "expires_in";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String SCOPE_KEY = "scope";

    /* Token error constants */
    public static final String ERROR_KEY = "error";
    public static final String ERROR_DESCRIPTION = "error_description";

    private String tokenType;

    private Integer expiresIn;

    private String accessToken;

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
        Duration duration = Duration.between(this.getIssuedAt(), Instant.now());
        return duration.getSeconds() >= this.getExpiresIn();
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
            ", accessToken=xxxxx" +
            ", scope=" + scope + "]";
    }
}

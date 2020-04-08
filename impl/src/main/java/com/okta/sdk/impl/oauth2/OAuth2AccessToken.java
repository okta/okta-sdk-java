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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2AccessToken {
    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("access_token")
    private String accessToken;

    private String scope;

    private Instant issuedAt = Instant.now();

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

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public boolean hasExpired() {
        Duration duration = Duration.between(this.getIssuedAt(), Instant.now());
        //System.out.println("++++++++++++++ duration diff between now() and issuedAt +++++++++++++ " + duration.getSeconds());
        //System.out.println("++++++++++++++ duration.getSeconds() >= this.getExpiresIn() +++++++++++++ " + (duration.getSeconds() >= 10));
        //System.out.println("++++++++++++++ duration.getSeconds() >= this.getExpiresIn() +++++++++++++ " + (duration.getSeconds() >= this.getExpiresIn()));
        //return duration.getSeconds() >= 10;
        return duration.getSeconds() >= this.getExpiresIn();
    }

    // for testing purposes
    public void forceExpiryInNextSecs(Long expireInNextSecs) {
        this.setExpiresIn(expireInNextSecs);
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

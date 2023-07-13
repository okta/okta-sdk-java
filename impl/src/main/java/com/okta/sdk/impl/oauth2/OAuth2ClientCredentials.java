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

import com.okta.commons.lang.Assert;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.resource.handler.Pair;
import com.okta.sdk.resource.handler.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * This implementation represents client credentials specific to OAuth2 Authentication scheme.
 *
 * @since 1.6.0
 */
public class OAuth2ClientCredentials extends OAuth implements ClientCredentials<OAuth2AccessToken> {

    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientCredentials.class);

    private OAuth2AccessToken oAuth2AccessToken;
    private final AccessTokenRetrieverService accessTokenRetrieverService;

    public OAuth2ClientCredentials(AccessTokenRetrieverService accessTokenRetrieverService) {
        Assert.notNull(accessTokenRetrieverService, "accessTokenRetrieverService must not be null");
        this.accessTokenRetrieverService = accessTokenRetrieverService;
    }

    @Override
    public synchronized void applyToParams(List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams) {
        if (oAuth2AccessToken != null &&
            // refresh 5 minutes before token expiration
            oAuth2AccessToken.getExpiresAt().minus(5, ChronoUnit.MINUTES).isBefore(Instant.now())) {
            oAuth2AccessToken = null;
            setAccessToken(null);
            refreshOAuth2AccessToken();
        }
        super.applyToParams(queryParams, headerParams, cookieParams);
    }

    public void refreshOAuth2AccessToken() {
        log.debug("Attempting to refresh OAuth2 access token...");

        try {
            oAuth2AccessToken = accessTokenRetrieverService.getOAuth2AccessToken();
        } catch (IOException | InvalidKeyException e) {
            throw new OAuth2TokenRetrieverException("Failed to get OAuth2 access token", e);
        }

        if (oAuth2AccessToken == null) {
            throw new OAuth2TokenRetrieverException("Failed to get OAuth2 access token");
        }
    }

    public OAuth2AccessToken getCredentials() {
        return oAuth2AccessToken;
    }

    public void setCredentials(OAuth2AccessToken oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    @Override
    public String toString() {
        // never ever print the secret
        return "<OAuth2ClientCredentials>";
    }
}

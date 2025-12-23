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
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.client.auth.OAuth;
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
    
    // Flag to prevent recursive refresh attempts during token retrieval
    private volatile boolean isRefreshing = false;

    public OAuth2ClientCredentials(AccessTokenRetrieverService accessTokenRetrieverService) {
        Assert.notNull(accessTokenRetrieverService, "accessTokenRetrieverService must not be null");
        this.accessTokenRetrieverService = accessTokenRetrieverService;
    }

    @Override
    public synchronized void applyToParams(List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams) {
        // Skip refresh check if we're already in the middle of refreshing (prevents recursive calls)
        if (!isRefreshing) {
            // Determine if token refresh is needed:
            // 1. Token exists but is about to expire (within 5 minutes) - original behavior
            // 2. Token is null but was previously set (stuck state after failed refresh) - fix for GFS customer issue
            boolean tokenExpiring = oAuth2AccessToken != null && 
                oAuth2AccessToken.getExpiresAt().minus(5, ChronoUnit.MINUTES).isBefore(Instant.now());
            boolean tokenMissingButWasSet = oAuth2AccessToken == null && getAccessToken() == null;
            
            if (tokenExpiring || tokenMissingButWasSet) {
                // Clear existing token state before refresh attempt
                oAuth2AccessToken = null;
                setAccessToken(null);
                
                try {
                    refreshOAuth2AccessToken();
                } catch (Exception e) {
                    log.error("Failed to refresh OAuth2 access token. Will retry on next API call.", e);
                    // Don't rethrow - let the API call proceed and fail with 401/403
                    // The next API call will trigger another refresh attempt
                }
            }
        }
        super.applyToParams(queryParams, headerParams, cookieParams);
    }

    public void refreshOAuth2AccessToken() {
        log.debug("Attempting to refresh OAuth2 access token...");

        isRefreshing = true;
        try {
            oAuth2AccessToken = accessTokenRetrieverService.getOAuth2AccessToken();
            
            if (oAuth2AccessToken == null) {
                throw new OAuth2TokenRetrieverException("Failed to get OAuth2 access token");
            }
        } catch (IOException | InvalidKeyException e) {
            throw new OAuth2TokenRetrieverException("Failed to get OAuth2 access token", e);
        } finally {
            isRefreshing = false;
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

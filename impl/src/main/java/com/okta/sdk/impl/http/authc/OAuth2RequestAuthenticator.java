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
package com.okta.sdk.impl.http.authc;

import com.okta.commons.http.Request;
import com.okta.commons.http.authc.RequestAuthenticationException;
import com.okta.commons.http.authc.RequestAuthenticator;
import com.okta.commons.lang.Assert;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.oauth2.OAuth2AccessToken;
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials;
import com.okta.sdk.impl.oauth2.OAuth2TokenRetrieverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * This implementation used by OAuth2 flow adds Bearer header with access token as the
 * value in all outgoing requests. This has logic to fetch a new token and store it in
 * {@link OAuth2ClientCredentials} object if the existing one has expired.
 *
 * @since 1.6.0
 */
public class OAuth2RequestAuthenticator implements RequestAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(OAuth2RequestAuthenticator.class);

    private final ClientCredentials<OAuth2AccessToken> clientCredentials;

    public OAuth2RequestAuthenticator(ClientCredentials<OAuth2AccessToken> clientCredentials) {
        Assert.notNull(clientCredentials, "clientCredentials may not be null");
        this.clientCredentials = clientCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        OAuth2AccessToken oAuth2AccessToken = clientCredentials.getCredentials();

        if (oAuth2AccessToken.hasExpired()) {
            log.debug("OAuth2 access token expiry detected. Will fetch a new token from Authorization server");

            synchronized (this) {
                if (oAuth2AccessToken.hasExpired()) {
                    try {
                        OAuth2ClientCredentials oAuth2ClientCredentials = (OAuth2ClientCredentials) clientCredentials;
                        // fetch new token
                        oAuth2AccessToken = oAuth2ClientCredentials.getAccessTokenRetrieverService().getOAuth2AccessToken();
                        // store the new token
                        oAuth2ClientCredentials.setCredentials(oAuth2AccessToken);
                    } catch (IOException | InvalidKeyException e) {
                        throw new OAuth2TokenRetrieverException("Failed to renew expired OAuth2 access token", e);
                    }
                }
            }
        }

        // add Bearer header with token value
        request.getHeaders().set(AUTHORIZATION_HEADER, "Bearer " + oAuth2AccessToken.getAccessToken());
    }

}

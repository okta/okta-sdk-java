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
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.impl.oauth2.OAuth2AccessToken;
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;

public class OAuth2RequestAuthenticator implements RequestAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(OAuth2RequestAuthenticator.class);

    private final ClientCredentials<OAuth2AccessToken> clientCredentials;

    public OAuth2RequestAuthenticator(ClientCredentials<OAuth2AccessToken> clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {

        if (clientCredentials != null &&
            clientCredentials.getCredentials() != null) {
            OAuth2AccessToken oAuth2AccessToken = clientCredentials.getCredentials();

            if (oAuth2AccessToken != null) {
                if (oAuth2AccessToken.hasExpired()) {
                    log.debug("OAuth2 access token expiry detected. Will fetch a new token from Authorization server");

                    try {
                        OAuth2ClientCredentials oAuth2ClientCredentials = (OAuth2ClientCredentials) clientCredentials;
                        // clear old token
                        oAuth2ClientCredentials.reset();
                        // get a new token
                        oAuth2AccessToken = oAuth2ClientCredentials.getAccessTokenRetrieverService().getOAuth2AccessToken();
                        // store the new token
                        oAuth2ClientCredentials.setCredentials(oAuth2AccessToken);

                    } catch (IOException | InvalidKeyException e) {
                        log.error("Exception occurred", e);
                        throw new RuntimeException(e);
                    }
                }

                request.getHeaders()
                    .set(AUTHORIZATION_HEADER, "Bearer " + oAuth2AccessToken.getAccessToken());
            }
        }
    }

}

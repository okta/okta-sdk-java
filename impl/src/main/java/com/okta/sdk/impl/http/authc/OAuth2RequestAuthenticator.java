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
package com.okta.sdk.impl.http.authc;

import com.okta.commons.http.Request;
import com.okta.commons.http.authc.RequestAuthenticationException;
import com.okta.commons.http.authc.RequestAuthenticator;
import com.okta.commons.lang.Strings;
import com.okta.sdk.authc.credentials.ClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2RequestAuthenticator implements RequestAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(OAuth2RequestAuthenticator.class);

    public static final String AUTHENTICATION_SCHEME = "OAUTH2";

    private final ClientCredentials<String> clientCredentials;

    public OAuth2RequestAuthenticator(ClientCredentials<String> clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        if (clientCredentials != null && !Strings.isEmpty(clientCredentials.getCredentials())) {
            log.debug("Setting Bearer token in Authorization Header for {}", AUTHENTICATION_SCHEME);
            request.getHeaders().set(AUTHORIZATION_HEADER, "Bearer " + clientCredentials.getCredentials());
        }
    }
}

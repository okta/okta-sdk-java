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

import com.okta.commons.lang.Assert;
import com.okta.sdk.authc.credentials.ClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2ClientCredentials implements ClientCredentials<String> {
    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientCredentials.class);

    private String accessToken;

    private AccessTokenRetrieverService accessTokenRetrieverService;

    public OAuth2ClientCredentials(String accessToken) {
        Assert.notNull(accessToken, "accessToken must not be null.");
        this.accessToken = accessToken;
    }

    public OAuth2ClientCredentials(AccessTokenRetrieverService accessTokenRetrieverService) {
        Assert.notNull(accessTokenRetrieverService, "accessTokenRetrieverService must not be null.");
        this.accessTokenRetrieverService = accessTokenRetrieverService;
    }

    public String getCredentials() {
        return accessToken;
    }

    @Override
    public String toString() {
        //never ever print the secret
        return "<OAuth2ClientCredentials>";
    }
}

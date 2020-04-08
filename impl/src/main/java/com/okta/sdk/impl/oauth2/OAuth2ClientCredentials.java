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

public class OAuth2ClientCredentials implements ClientCredentials<OAuth2AccessToken> {

    private OAuth2AccessToken oAuth2AccessToken;
    private AccessTokenRetrieverService accessTokenRetrieverService;

    public OAuth2ClientCredentials(OAuth2AccessToken oAuth2AccessToken,
                                   AccessTokenRetrieverService accessTokenRetrieverService) {
        Assert.notNull(oAuth2AccessToken, "oAuth2AccessToken must not be null.");
        Assert.notNull(accessTokenRetrieverService, "accessTokenRetrieverService must not be null.");
        this.oAuth2AccessToken = oAuth2AccessToken;
        this.accessTokenRetrieverService = accessTokenRetrieverService;
    }

    public OAuth2AccessToken getCredentials() {
        return oAuth2AccessToken;
    }

    public void setCredentials(OAuth2AccessToken oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    public AccessTokenRetrieverService getAccessTokenRetrieverService() {
        return accessTokenRetrieverService;
    }

    public void reset() {
        setCredentials(null);
    }

    @Override
    public String toString() {
        // never ever print the secret
        return "<OAuth2ClientCredentials>";
    }
}

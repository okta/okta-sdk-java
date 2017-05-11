/*
 * Copyright 2014 Stormpath, Inc.
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

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.support.RequestAuthenticationException;
import com.okta.sdk.lang.Assert;


public class SswsAuthenticator implements RequestAuthenticator {

    public static final String AUTHENTICATION_SCHEME = "SSWS";

    private final ApiKeyCredentials apiKeyCredentials;

    public SswsAuthenticator(ApiKeyCredentials apiKeyCredentials) {
        Assert.notNull(apiKeyCredentials, "apiKeyCredentials must be not be null.");
        this.apiKeyCredentials = apiKeyCredentials;

        ApiKey apiKey = apiKeyCredentials.getApiKey();
        Assert.notNull(apiKey, "apiKeyCredentials argument cannot have a null apiKey");
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + apiKeyCredentials.getSecret());
    }
}

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
package com.okta.sdk.impl.authc;

import com.okta.sdk.api.ApiAuthenticationResult;
import com.okta.sdk.api.ApiRequestAuthenticator;
import com.okta.sdk.authc.AuthenticationRequest;
import com.okta.sdk.authc.AuthenticationResult;
import com.okta.sdk.http.HttpRequest;
import com.okta.sdk.lang.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0.0
 */
public class DefaultApiRequestAuthenticator implements ApiRequestAuthenticator {

    private static final ApiAuthenticationRequestFactory FACTORY = new ApiAuthenticationRequestFactory();

    private HttpRequest httpRequest;

    private static final Set<Class> HTTP_REQUEST_SUPPORTED_CLASSES;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG =
            "Class [%s] is not one of the supported http requests classes [%s].";

    static {
        Set<Class> supportedClasses = new HashSet<Class>();
        supportedClasses.add(HttpRequest.class);
        HTTP_REQUEST_SUPPORTED_CLASSES = supportedClasses;
    }

    public DefaultApiRequestAuthenticator(HttpRequest httpRequest) {

        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");
        this.httpRequest = httpRequest;
    }

    protected ApiAuthenticationResult execute() {
        AuthenticationRequest request = FACTORY.createFrom(httpRequest);
        AuthenticationResult result = null; // FIXME
        Assert.isInstanceOf(ApiAuthenticationResult.class, result);
        return (ApiAuthenticationResult) result;
    }

    @Override
    public ApiAuthenticationResult authenticate(HttpRequest httpRequest) {

        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");
        this.httpRequest = httpRequest;

        return this.execute();
    }
}

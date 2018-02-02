/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.http;

import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Classes;

import java.lang.reflect.Constructor;

/**
 * Utility factory class for creating {@link HttpRequest} instances for SDK users that do not already depend on the
 * {@code HttpServletRequest} API.
 *
 * <p>Once you obtain either a {@code HttpServletRequest} or construct a {@link HttpRequest}, either may be
 * authenticated (presumably to assert an identity for calls to your REST API)
 * </p>
 *
 * @since 0.5.0
 */
public final class HttpRequests {

    private static final Class<HttpRequestBuilder> HTTP_REQUEST_BUILDER =
        Classes.forName("com.okta.sdk.impl.http.DefaultHttpRequestBuilder");

    /**
     * Creates and returns a new {@link HttpRequestBuilder} that builds request instances that will be used for request
     * authentication.
     *
     * <p>This method is only useful for SDK users that do not depend on the {@code HttpServletRequest} API, as
     * the {@code application.authenticate*} methods accept that type natively.</p>
     *
     * @param method the source request's http method.
     * @return a new HttpRequestBuilder that can be used to construct a new {@link HttpRequest} instance.
     * @throws IllegalArgumentException if the method argument is {@code null}.
     */
    public static HttpRequestBuilder method(HttpMethod method) {
        Assert.notNull(method, "method argument is required.");

        Constructor<HttpRequestBuilder> ctor = Classes.getConstructor(HTTP_REQUEST_BUILDER, HttpMethod.class);
        return Classes.instantiate(ctor, method);
    }
}

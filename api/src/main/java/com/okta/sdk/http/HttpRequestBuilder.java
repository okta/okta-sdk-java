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

import java.util.Map;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link HttpRequest} instances.
 *
 * <p>The {@link HttpRequestBuilder} is useful to build {@link HttpRequest} instances for developers that don't
 * have access to the Servlet container, and therefore, cannot execute operations using implementations
 * of the {@code javax.servlet.HttpServlet} api.</p>
 *
 * @see HttpRequest
 * @see HttpRequests
 * @since 0.5.0
 */
public interface HttpRequestBuilder {

    /**
     * Sets the HTTP Headers that will be present in the resulting request instance.
     *
     * @param headers the HTTP Headers that will be present in the resulting built request instance.
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if the method argument is {@code null}.
     */
    HttpRequestBuilder headers(Map<String, String[]> headers);

    /**
     * Sets the request parameters that will be present in the resulting request instance.
     *
     * @param parameters the request parameters that will be present in the resulting request instance.
     * @return the builder instance for method chaining.
     */
    HttpRequestBuilder parameters(Map<String, String[]> parameters);

    /**
     * Adds a request header that will be part of the headers map in the resulting request instance.
     *
     * @param name the name of the HTTP header that will be present in the resulting HTTP request instance.
     * @param value the value of the HTTP header that will be present in the resulting HTTP request instance.
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if the name or value arguments are {@code null}.
     *
     */
    HttpRequestBuilder addHeader(String name, String[] value);

    /**
     * Adds a request parameter that will be part of the parameters map in the resulting request instance.
     *
     * @param name the name of the HTTP parameter that will be present in the resulting HTTP request instance.
     * @param value the value of the HTTP parameter that will be present in the resulting HTTP request instance.
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if the name or value arguments are {@code null}.
     *
     */
    HttpRequestBuilder addParameter(String name, String[] value);

    /**
     * Sets the query parameters that will be present in the resulting request instance.
     *
     * @param queryParameters the query parameters that will be present in the resulting request instance.
     * @return the builder instance for method chaining.
     */
    HttpRequestBuilder queryParameters(String queryParameters);

    /**
     * Returns a new {@code HttpRequest} instance reflecting the current builder state.
     *
     * @return a new {@code HttpRequest} instance reflecting the current builder state.
     */
    HttpRequest build();
}

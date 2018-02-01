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
package com.okta.sdk.impl.http;

import com.okta.sdk.http.HttpMethod;
import com.okta.sdk.http.HttpRequest;
import com.okta.sdk.http.HttpRequestBuilder;
import com.okta.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * DefaultHttpRequestBuilder
 *
 * @since 0.5.0
 */
public class DefaultHttpRequestBuilder implements HttpRequestBuilder {

    private HttpMethod method;
    private Map<String, String[]> headers;
    private Map<String, String[]> parameters;
    private String queryParameters;

    public DefaultHttpRequestBuilder(HttpMethod method) {
        Assert.notNull(method);
        this.method = method;
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
    }

    @Override
    public HttpRequestBuilder headers(Map<String, String[]> headers) {
        Assert.notNull(headers, "headers cannot be null");
        this.headers = headers;
        return this;
    }

    @Override
    public HttpRequestBuilder parameters(Map<String, String[]> parameters) {
        Assert.notNull(parameters, "parameters cannot be null");
        this.parameters = parameters;
        return this;
    }

    @Override
    public HttpRequestBuilder addHeader(String key, String[] value) {
        Assert.notNull(key, "key argument is required.");
        Assert.notNull(value, "value argument is required.");

        this.headers.put(key, value);
        return this;
    }

    @Override
    public HttpRequestBuilder addParameter(String key, String[] value) {
        Assert.notNull(key, "key argument is required.");
        Assert.notNull(value, "value argument is required.");

        this.parameters.put(key, value);
        return this;
    }

    @Override
    public HttpRequestBuilder queryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    @Override
    public HttpRequest build() {
        return new DefaultHttpRequest(headers, method, parameters, queryParameters);
    }
}

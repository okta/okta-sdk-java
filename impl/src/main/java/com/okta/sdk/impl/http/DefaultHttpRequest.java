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
package com.okta.sdk.impl.http;

import com.okta.sdk.http.HttpMethod;
import com.okta.sdk.http.HttpRequest;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This is the default implementation for {@link HttpRequest} interface.
 *
 * @since 1.0.RC
 */
public class DefaultHttpRequest implements HttpRequest {

    private final Map<String, String[]> headers;
    private final HttpMethod method;
    private final String queryParameters;
    private Map<String, String[]> parameters;

    public DefaultHttpRequest(Map<String, String[]> headers, HttpMethod method, Map<String, String[]> parameters, String queryParameters) {
        Assert.notNull(method, "method cannot be null.");
        Assert.notNull(headers, "headers cannot be null.");

        this.headers = headers;
        this.method = method;
        this.parameters = parameters;
        this.queryParameters = queryParameters;
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String headerName) {
        if (Collections.isEmpty(headers)) {
            return null;
        }

        for (Map.Entry<String, String[]> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(headerName)) {
                String[] values = entry.getValue();
                if (values == null || values.length == 0) {
                    return null;
                }
                return values[0];
            }
        }
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Map<String, String[]> getParameters() {
        if (parameters == null || parameters.isEmpty()) {
            parseParameters();
        }
        return parameters;
    }

    @Override
    public String getParameter(String parameterName) {

        Map<String, String[]> httpParameters = getParameters();

        if (httpParameters == null) {
            return null;
        }

        String[] values = httpParameters.get(parameterName);

        if (values == null || values.length == 0) {
            return null;
        }

        return values[0];
    }

    @Override
    public String getQueryParameters() {
        return queryParameters;
    }

    private void parseParameters() {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return;
        }

        Map<String, List<String>> tempParameters = new HashMap<String, List<String>>();

        for (StringTokenizer tokenizer = new StringTokenizer(queryParameters, "&"); tokenizer.hasMoreElements(); ) {
            String token = tokenizer.nextToken();

            String[] pair = token.split("=");

            Assert.isTrue(pair.length == 2, "this query parameter is invalid.");

            List<String> values;
            if (tempParameters.containsKey(pair[0])) {
                values = tempParameters.get(pair[0]);
            } else {
                values = new ArrayList<String>();
                tempParameters.put(pair[0], values);
            }
            values.add(pair[1]);
        }

        parameters = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : tempParameters.entrySet()) {
            List<String> valuesList = entry.getValue();
            String[] valuesArray = new String[valuesList.size()];
            valuesList.toArray(valuesArray);
            parameters.put(entry.getKey(), valuesArray);
        }
    }

}

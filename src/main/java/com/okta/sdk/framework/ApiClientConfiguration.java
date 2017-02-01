/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.framework;

import java.util.Map;

public class ApiClientConfiguration {

    private String baseUrl;
    private int apiVersion = 1;
    private String apiToken;
    private Map<String, String> headers;

    public ApiClientConfiguration(String baseUrl, String apiToken) {
        this(baseUrl, apiToken, null);
    }

    public ApiClientConfiguration(String baseUrl, String apiToken, Map headers) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.headers = headers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }
}

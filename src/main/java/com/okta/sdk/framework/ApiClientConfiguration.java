/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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

    /**
     * Class variables.
     */
    private String baseUrl;
    private int apiVersion = 1;
    private String apiToken;
    private Map<String, String> headers;

    /**
     * Constructor for the ApiClientConfiguartion object.
     *
     * @param baseUrl {@link String}
     * @param apiToken {@link String}
     */
    public ApiClientConfiguration(String baseUrl, String apiToken) {
        this(baseUrl, apiToken, null);
    }

    /**
     * Constructor for the ApiClientConfiguartion object.
     *
     * @param baseUrl {@link String}
     * @param apiToken {@link String}
     * @param headers {@link Map}
     */
    public ApiClientConfiguration(String baseUrl, String apiToken, Map headers) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.headers = headers;
    }

    /**
     * Returns the url of the organization specified.
     * @return {@link String}
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the url of the organization.
     * @param baseUrl {@link String}
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the current API version.
     * @return {@link Integer}
     */
    public int getApiVersion() {
        return apiVersion;
    }

    /**
     * Returns the API token.
     * @return {@link String}
     */
    public String getApiToken() {
        return apiToken;
    }

    /**
     * Sets the API token.
     * @param apiToken {@link String}
     */
    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    /**
     * Returns all passed headers.
     * @return {@link Map}
     */
    public Map getHeaders() {
        return headers;
    }

    /**
     * Sets the headers.
     * @param headers {@link Map}
     */
    public void setHeaders(Map headers) {
        this.headers = headers;
    }
}

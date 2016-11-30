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

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogUserAgent extends ApiObject {

    /**
     * The raw user agent of the client
     */
    private final String rawUserAgent;

    /**
     * The operating system of the client
     */
    private final String os;

    /**
     * The browser used by the client
     */
    private final String browser;

    @JsonCreator
    public LogUserAgent(
            @JsonProperty("rawUserAgent") String rawUserAgent,
            @JsonProperty("os") String os,
            @JsonProperty("browser") String browser
    ) {
        this.rawUserAgent = rawUserAgent;
        this.os = os;
        this.browser = browser;
    }


    public String getRawUserAgent() {
        return rawUserAgent;
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawUserAgent, os, browser);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogUserAgent)) {
            return false;
        }
        final LogUserAgent other = (LogUserAgent) obj;
        return Objects.equals(this.rawUserAgent, other.rawUserAgent)
                && Objects.equals(this.os, other.os)
                && Objects.equals(this.browser, other.browser);
    }
}

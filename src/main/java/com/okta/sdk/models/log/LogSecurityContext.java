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

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogSecurityContext extends ApiObject {

    /**
     * AS Number.
     */
    private final Integer asNumber;

    /**
     * AS Organization.
     */
    private final String asOrg;

    /**
     * Internet service provider.
     */
    private final String isp;

    /**
     * The domain.
     */
    private final String domain;

    /**
     * If the request is coming from a known proxy.
     */
    private final Boolean isProxy;

    /**
     * Constructor for LogSecurityContext
     * @param asNumber {@link Integer}
     * @param asOrg {@link String}
     * @param isp {@link String}
     * @param domain {@link String}
     * @param isProxy {@link Boolean}
     */
    @JsonCreator
    public LogSecurityContext(
            @JsonProperty("asNumber") Integer asNumber,
            @JsonProperty("asOrg") String asOrg,
            @JsonProperty("isp") String isp,
            @JsonProperty("domain") String domain,
            @JsonProperty("isProxy") Boolean isProxy
    ) {
        this.asNumber = asNumber;
        this.asOrg = asOrg;
        this.isp = isp;
        this.domain = domain;
        this.isProxy = isProxy;
    }

    /**
     * Returns the asNumber.
     * @return {@link Integer}
     */
    public Integer getAsNumber() {
        return asNumber;
    }

    /**
     * Returns the asOrg.
     * @return {@link String}
     */
    public String getAsOrg() {
        return asOrg;
    }

    /**
     * Returns the isp.
     * @return {@link String}
     */
    public String getIsp() {
        return isp;
    }

    /**
     * Returns the domain.
     * @return {@link String}
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the isProxy.
     * @return {@link Boolean}
     */
    public Boolean getIsProxy() {
        return isProxy;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(asNumber, asOrg, isp, domain, isProxy);
    }

    /**
     * Overrides the method equals.
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogSecurityContext)) {
            return false;
        }
        final LogSecurityContext other = (LogSecurityContext) obj;
        return Objects.equals(this.asNumber, other.asNumber)
                && Objects.equals(this.asOrg, other.asOrg)
                && Objects.equals(this.isp, other.isp)
                && Objects.equals(this.domain, other.domain)
                && Objects.equals(this.isProxy, other.isProxy);
    }
}

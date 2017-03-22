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

public final class LogIpAddress extends ApiObject {

    /**
     * The ip address.
     */
    private final String ip;

    /**
     * Geographical context data of the event.
     */
    private final LogGeographicalContext geographicalContext;

    /**
     * Ip address version.
     */
    private final Version version;

    /**
     * Details regarding the source.
     */
    private final String source;

    /**
     * Version enum
     */
    public enum Version {
        V4,
        V6
    }

    /**
     * Constructor for LogIpAddress
     * @param ip {@link String}
     * @param geographicalContext {@link LogGeographicalContext}
     * @param version {@link Version}
     * @param source {@link String}
     */
    @JsonCreator
    public LogIpAddress(
            @JsonProperty("ip") String ip,
            @JsonProperty("geographicalContext") LogGeographicalContext geographicalContext,
            @JsonProperty("version") Version version,
            @JsonProperty("source") String source
    ) {
        this.ip = ip;
        this.geographicalContext = geographicalContext;
        this.version = version;
        this.source = source;
    }

    /**
     * Returns the IP.
     * @return {@link String}
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the geographicalContext.
     * @return {@link LogGeographicalContext}
     */
    public LogGeographicalContext getGeographicalContext() {
        return geographicalContext;
    }

    /**
     * Returns the version.
     * @return {@link Version}
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Returns the source.
     * @return {@link String}
     */
    public String getSource() {
        return source;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(ip, geographicalContext, version, source);
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
        if (obj == null || !(obj instanceof LogIpAddress)) {
            return false;
        }
        final LogIpAddress other = (LogIpAddress) obj;
        return Objects.equals(this.ip, other.ip)
                && Objects.equals(this.geographicalContext, other.geographicalContext)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.source, other.source);
    }
}

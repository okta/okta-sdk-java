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

public final class LogClient extends ApiObject {

    /**
     * The client user agent.
     */
    private final LogUserAgent userAgent;

    /**
     * Zone the client is in.
     */
    private final String zone;

    /**
     * The client device.
     */
    private final String device;

    /**
     * The client id.
     */
    private final String id;

    /**
     * The client ip address.
     */
    private final String ipAddress;

    /**
     * Geographical context data of the event.
     */
    private final LogGeographicalContext geographicalContext;

    /**
     * Constructor for the LogClient.
     *
     * @param userAgent {@link LogUserAgent}
     * @param zone {@link String}
     * @param device {@link String}
     * @param id {@link String}
     * @param ipAddress {@link String}
     * @param geographicalContext {@link LogGeographicalContext}
     */
    @JsonCreator
    public LogClient(
            @JsonProperty("userAgent") LogUserAgent userAgent,
            @JsonProperty("zone") String zone,
            @JsonProperty("device") String device,
            @JsonProperty("id") String id,
            @JsonProperty("ipAddress") String ipAddress,
            @JsonProperty("geographicalContext") LogGeographicalContext geographicalContext
    ) {
        this.userAgent = userAgent;
        this.zone = zone;
        this.device = device;
        this.id = id;
        this.ipAddress = ipAddress;
        this.geographicalContext = geographicalContext;
    }

    /**
     * Returns the userAgent.
     * @return {@link LogUserAgent}
     */
    public LogUserAgent getUserAgent() {
        return userAgent;
    }

    /**
     * Returns the zone.
     * @return {@link String}
     */
    public String getZone() {
        return zone;
    }

    /**
     * Returns the device.
     * @return {@link String}
     */
    public String getDevice() {
        return device;
    }

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the ipAddress.
     * @return {@link String}
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the geographicalContext.
     * @return {@link LogGeographicalContext}
     */
    public LogGeographicalContext getGeographicalContext() {
        return geographicalContext;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(userAgent, zone, device, id, ipAddress, geographicalContext);
    }

    /**
     * Overriding method for equals.
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogClient)) {
            return false;
        }
        final LogClient other = (LogClient) obj;
        return Objects.equals(this.userAgent, other.userAgent)
                && Objects.equals(this.zone, other.zone)
                && Objects.equals(this.device, other.device)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.ipAddress, other.ipAddress)
                && Objects.equals(this.geographicalContext, other.geographicalContext);
    }
}

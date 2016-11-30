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

public final class LogClient extends ApiObject {

    /**
     * The client user agent
     */
    private final LogUserAgent userAgent;

    /**
     * Zone the client is in
     */
    private final String zone;

    /**
     * The client device
     */
    private final String device;

    /**
     * The client id
     */
    private final String id;

    /**
     * The client ip address
     */
    private final String ipAddress;

    /**
     * Geographical context data of the event
     */
    private final LogGeographicalContext geographicalContext;

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

    public LogUserAgent getUserAgent() {
        return userAgent;
    }

    public String getZone() {
        return zone;
    }

    public String getDevice() {
        return device;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LogGeographicalContext getGeographicalContext() {
        return geographicalContext;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAgent, zone, device, id, ipAddress, geographicalContext);
    }

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

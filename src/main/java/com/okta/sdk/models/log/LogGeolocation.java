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

public final class LogGeolocation extends ApiObject {

    /**
     * Latitude of the client.
     */
    private final Double lat;

    /**
     * Longitude of the client.
     */
    private final Double lon;

    /**
     * Constructor for LogGeolocation.
     * @param lat {@link Double}
     * @param lon {@link Double}
     */
    @JsonCreator
    public LogGeolocation(
            @JsonProperty("lat") Double lat,
            @JsonProperty("lon") Double lon
    ) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Returns the lat.
     * @return {@link Double}
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Returns the lon.
     * @return {@link Double}
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
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
        if (obj == null || !(obj instanceof LogGeolocation)) {
            return false;
        }
        final LogGeolocation other = (LogGeolocation) obj;
        return Objects.equals(this.lat, other.lat)
                && Objects.equals(this.lon, other.lon);
    }
}

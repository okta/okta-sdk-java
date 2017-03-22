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

public final class LogGeographicalContext extends ApiObject {

    /**
     * City of the event.
     */
    private final String city;

    /**
     * Zone the client is in.
     */
    private final String state;

    /**
     * Country of the client.
     */
    private final String country;

    /**
     * Postal code of the client.
     */
    private final String postalCode;

    /**
     * Geolocation of the client.
     */
    private final LogGeolocation geolocation;

    /**
     * Constructor for LogGeographicalContext
     * @param city {@link String}
     * @param state {@link String}
     * @param country {@link String}
     * @param postalCode {@link String}
     * @param geolocation {@link LogGeolocation}
     */
    @JsonCreator
    public LogGeographicalContext(
            @JsonProperty("city") String city,
            @JsonProperty("state") String state,
            @JsonProperty("country") String country,
            @JsonProperty("postalCode") String postalCode,
            @JsonProperty("geolocation") LogGeolocation geolocation
    ) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.geolocation = geolocation;
    }

    /**
     * Returns the city.
     * @return {@link String}
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the state.
     * @return {@link String}
     */
    public String getState() {
        return state;
    }

    /**
     * Returns the country.
     * @return {@link String}
     */
    public String getCountry() {
        return country;
    }

    /**
     * Returns the postalCode.
     * @return {@link String}
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns the geolocation.
     * @return {@link LogGeolocation}
     */
    public LogGeolocation getGeolocation() {
        return geolocation;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(city, state, country, postalCode, geolocation);
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
        if (obj == null || !(obj instanceof LogGeographicalContext)) {
            return false;
        }
        final LogGeographicalContext other = (LogGeographicalContext) obj;
        return Objects.equals(this.city, other.city)
                && Objects.equals(this.state, other.state)
                && Objects.equals(this.country, other.country)
                && Objects.equals(this.postalCode, other.postalCode)
                && Objects.equals(this.geolocation, other.geolocation);
    }
}

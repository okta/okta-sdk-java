package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogGeographicalContext extends ApiObject {

    /**
     * City of the event
     */
    private final String city;

    /**
     * Zone the client is in
     */
    private final String state;

    /**
     * Country of the client
     */
    private final String country;

    /**
     * Postal code of the client
     */
    private final String postalCode;

    /**
     * Geolocation of the client
     */
    private final LogGeolocation geolocation;

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

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public LogGeolocation getGeolocation() {
        return geolocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, state, country, postalCode, geolocation);
    }

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

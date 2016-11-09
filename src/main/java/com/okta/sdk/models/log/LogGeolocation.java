package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogGeolocation extends ApiObject {

    /**
     * Latitude of the client
     */
    private final Double lat;

    /**
     * Longitude of the client
     */
    private final Double lon;

    @JsonCreator
    public LogGeolocation(
            @JsonProperty("lat") Double lat,
            @JsonProperty("lon") Double lon
    ) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

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

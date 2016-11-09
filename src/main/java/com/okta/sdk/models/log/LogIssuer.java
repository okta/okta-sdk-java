package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogIssuer extends ApiObject {

    /**
     * An id of the issuer
     */
    private final String id;

    /**
     * The type of the issuer
     */
    private final String type;

    @JsonCreator
    public LogIssuer(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type
    ) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogIssuer)) {
            return false;
        }
        final LogIssuer other = (LogIssuer) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.type, other.type);
    }
}

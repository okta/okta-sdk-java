package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.okta.sdk.framework.ApiObject;

import java.util.Map;
import java.util.Objects;

public final class LogResource extends ApiObject {

    /**
     * The resource id
     */
    private final String id;

    /**
     * The type of the resource
     */
    private final String type;

    /**
     * Alternative id of the resource
     */
    private final String alternateId;

    /**
     * The display name of the resource
     */
    private final String displayName;

    /**
     * Resource details
     */
    private final Map<String, String> detailEntry;

    @JsonCreator
    public LogResource(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("alternateId") String alternateId,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("detailEntry") Map<String, String> detailEntry
    ) {
        this.id = id;
        this.type = type;
        this.alternateId = alternateId;
        this.displayName = displayName;
        if (detailEntry != null) {
            this.detailEntry = ImmutableMap.copyOf(detailEntry);
        } else {
            this.detailEntry = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<String, String> getDetailEntry() {
        return detailEntry;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, alternateId, displayName, detailEntry);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogResource)) {
            return false;
        }
        final LogResource other = (LogResource) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.alternateId, other.alternateId)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.detailEntry, other.detailEntry);
    }
}

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.okta.sdk.framework.ApiObject;

import java.util.Map;
import java.util.Objects;

public final class LogDebugContext extends ApiObject {

    /**
     * Map of debug data keys to debug data values
     */
    private final Map<String, Object> debugData;

    @JsonCreator
    public LogDebugContext(
            @JsonProperty("debugData") Map<String, Object> debugData
    ) {
        if (debugData != null) {
            this.debugData = ImmutableMap.copyOf(debugData);
        } else {
            this.debugData = null;
        }

    }

    public Map<String, Object> getDebugData() {
        return debugData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debugData);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogDebugContext)) {
            return false;
        }
        final LogDebugContext other = (LogDebugContext) obj;
        return Objects.equals(this.debugData, other.debugData);
    }
}

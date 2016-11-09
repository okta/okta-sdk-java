package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogOutcome extends ApiObject {

    /**
     * Result of the action
     */
    private final String result;

    /**
     * The reason for the outcome
     */
    private final String reason;

    @JsonCreator
    public LogOutcome(
            @JsonProperty("result") String result,
            @JsonProperty("reason") String reason
    ) {
        this.result = result;
        this.reason = reason;
    }

    public String getResult() {
        return result;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, reason);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogOutcome)) {
            return false;
        }
        final LogOutcome other = (LogOutcome) obj;
        return Objects.equals(this.result, other.result)
                && Objects.equals(this.reason, other.reason);
    }
}

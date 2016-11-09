package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.okta.sdk.framework.ApiObject;

import java.util.List;
import java.util.Objects;

public final class LogRequest extends ApiObject {

    /**
     * Chain of Ip data
     */
    private final List<LogIpAddress> ipChain;

    @JsonCreator
    public LogRequest(
            @JsonProperty("ipChain") List<LogIpAddress> ipChain
    ) {
        if (ipChain != null) {
            this.ipChain = ImmutableList.copyOf(ipChain);
        } else {
            this.ipChain = null;
        }
    }

    public List<LogIpAddress> getIpChain() {
        return ipChain;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipChain);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogRequest)) {
            return false;
        }
        final LogRequest other = (LogRequest) obj;
        return Objects.equals(this.ipChain, other.ipChain);
    }
}

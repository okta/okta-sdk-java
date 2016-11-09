package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogSecurityContext extends ApiObject {

    /**
     * AS Number
     */
    private final Integer asNumber;

    /**
     * AS Organization
     */
    private final String asOrg;

    /**
     * Internet service provider
     */
    private final String isp;

    /**
     * The domain
     */
    private final String domain;

    /**
     * If the request is coming from a known proxy
     */
    private final Boolean isProxy;

    @JsonCreator
    public LogSecurityContext(
            @JsonProperty("asNumber") Integer asNumber,
            @JsonProperty("asOrg") String asOrg,
            @JsonProperty("isp") String isp,
            @JsonProperty("domain") String domain,
            @JsonProperty("isProxy") Boolean isProxy
    ) {
        this.asNumber = asNumber;
        this.asOrg = asOrg;
        this.isp = isp;
        this.domain = domain;
        this.isProxy = isProxy;
    }

    public Integer getAsNumber() {
        return asNumber;
    }

    public String getAsOrg() {
        return asOrg;
    }

    public String getIsp() {
        return isp;
    }

    public String getDomain() {
        return domain;
    }

    public Boolean getIsProxy() {
        return isProxy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(asNumber, asOrg, isp, domain, isProxy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogSecurityContext)) {
            return false;
        }
        final LogSecurityContext other = (LogSecurityContext) obj;
        return Objects.equals(this.asNumber, other.asNumber)
                && Objects.equals(this.asOrg, other.asOrg)
                && Objects.equals(this.isp, other.isp)
                && Objects.equals(this.domain, other.domain)
                && Objects.equals(this.isProxy, other.isProxy);
    }
}

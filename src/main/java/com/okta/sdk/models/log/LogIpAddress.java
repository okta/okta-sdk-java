package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogIpAddress extends ApiObject {

    /**
     * The ip address
     */
    private final String ip;

    /**
     * Geographical context data of the event
     */
    private final LogGeographicalContext geographicalContext;

    /**
     * Ip address version
     */
    private final Version version;

    /**
     * Details regarding the source
     */
    private final String source;

    public enum Version {
        V4,
        V6
    }

    @JsonCreator
    public LogIpAddress(
            @JsonProperty("ip") String ip,
            @JsonProperty("geographicalContext") LogGeographicalContext geographicalContext,
            @JsonProperty("version") Version version,
            @JsonProperty("source") String source
    ) {
        this.ip = ip;
        this.geographicalContext = geographicalContext;
        this.version = version;
        this.source = source;
    }

    public String getIp() {
        return ip;
    }

    public LogGeographicalContext getGeographicalContext() {
        return geographicalContext;
    }

    public Version getVersion() {
        return version;
    }

    public String getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, geographicalContext, version, source);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogIpAddress)) {
            return false;
        }
        final LogIpAddress other = (LogIpAddress) obj;
        return Objects.equals(this.ip, other.ip)
                && Objects.equals(this.geographicalContext, other.geographicalContext)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.source, other.source);
    }
}

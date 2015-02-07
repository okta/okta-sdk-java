package com.okta.sdk.models.factors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import org.joda.time.DateTime;

import java.util.Map;

public class FactorVerificationResponse extends ApiObject {

    private String factorResult;

    private String state;

    private String factorResultMessage;

    private DateTime expiryTime;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Gets factorResult
     */
    public String getFactorResult() {
        return this.factorResult;
    }

    /**
     * Sets factorResult
     */
    public void setFactorResult(String val) {
        this.factorResult = val;
    }

    /**
     * Gets state
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets state
     */
    public void setState(String val) {
        this.state = val;
    }

    /**
     * Gets factorResultMessage
     */
    public String getFactorResultMessage() {
        return this.factorResultMessage;
    }

    /**
     * Sets factorResultMessage
     */
    public void setFactorResultMessage(String val) {
        this.factorResultMessage = val;
    }

    /**
     * Gets expiryTime
     */
    public DateTime getExpiryTime() {
        return this.expiryTime;
    }

    /**
     * Sets expiryTime
     */
    public void setExpiryTime(DateTime val) {
        this.expiryTime = val;
    }

    /**
     * Gets links
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets links
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Gets embedded
     */
    public Map<String, Object> getEmbedded() {
        return this.embedded;
    }

    /**
     * Sets embedded
     */
    public void setEmbedded(Map<String, Object> val) {
        this.embedded = val;
    }
}
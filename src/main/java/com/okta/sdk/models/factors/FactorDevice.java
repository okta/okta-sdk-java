package com.okta.sdk.models.factors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;

import java.util.Map;

public class FactorDevice extends ApiObject {

    private String id;

    private String status;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Gets id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets id
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Gets status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets status
     */
    public void setStatus(String val) {
        this.status = val;
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
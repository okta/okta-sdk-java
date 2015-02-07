package com.okta.sdk.models.factors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactorDeviceEnrollRequest extends ApiObject {

    private Verification verify;

    private Map<String, Object> profile;

    /**
     * Gets verify
     */
    public Verification getVerify() {
        return this.verify;
    }

    /**
     * Sets verify
     */
    public void setVerify(Verification val) {
        this.verify = val;
    }

    /**
     * Gets profile
     */
    public Map<String, Object> getProfile() {
        return this.profile;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Sets profile
     */
    public void setProfile(Map<String, Object> val) {
        this.profile = val;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
     public List<FactorEnrollRequest> getFactorsToEnroll() {
        return (List<FactorEnrollRequest>) embedded.get("factors");
    }

    @JsonIgnore
    public void setFactorsToEnroll(List<FactorEnrollRequest> factorsToEnroll) {
        if (embedded == null) {
            embedded = new HashMap<String, Object>();
        }
        embedded.put("factors", factorsToEnroll);
    }
}
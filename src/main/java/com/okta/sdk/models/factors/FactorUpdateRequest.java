package com.okta.sdk.models.factors;

import com.okta.sdk.framework.ApiObject;

import java.util.Map;

public class FactorUpdateRequest extends ApiObject {

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

    /**
     * Sets profile
     */
    public void setProfile(Map<String, Object> val) {
        this.profile = val;
    }
}
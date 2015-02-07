package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class Password extends ApiObject {

    private String value;

    /**
     * Gets value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets value
     */
    public void setValue(String val) {
        this.value = val;
    }
}
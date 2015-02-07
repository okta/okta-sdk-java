package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class TempPassword extends ApiObject {

    private String tempPassword;

    /**
     * Gets tempPassword
     */
    public String getTempPassword() {
        return this.tempPassword;
    }

    /**
     * Sets tempPassword
     */
    public void setTempPassword(String val) {
        this.tempPassword = val;
    }
}
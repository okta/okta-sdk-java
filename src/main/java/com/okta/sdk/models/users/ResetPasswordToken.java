package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class ResetPasswordToken extends ApiObject {

    private String resetPasswordUrl;

    /**
     * Gets resetPasswordUrl
     */
    public String getResetPasswordUrl() {
        return this.resetPasswordUrl;
    }

    /**
     * Sets resetPasswordUrl
     */
    public void setResetPasswordUrl(String val) {
        this.resetPasswordUrl = val;
    }
}
package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;

public class UserNameTemplate extends ApiObject {

    /**
     * mapping expression for username
     */
    private String template;

    /**
     * type of mapping expression
     */
    private String type;

    /**
     * suffix for built-in mapping expressions
     */
    private String userSuffix;

    /**
     * Gets template
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * Sets template
     */
    public void setTemplate(String val) {
        this.template = val;
    }

    /**
     * Gets type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets type
     */
    public void setType(String val) {
        this.type = val;
    }

    /**
     * Gets userSuffix
     */
    public String getUserSuffix() {
        return this.userSuffix;
    }

    /**
     * Sets userSuffix
     */
    public void setUserSuffix(String val) {
        this.userSuffix = val;
    }
}
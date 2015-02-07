package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;

public class Target extends ApiObject {

    /**
     * Unique key for actor
     */
    private String id;

    /**
     * Name of actor used for display purposes
     */
    private String displayName;

    /**
     * User, Client, or AppInstance
     */
    private String objectType;

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
     * Gets displayName
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets displayName
     */
    public void setDisplayName(String val) {
        this.displayName = val;
    }

    /**
     * Gets objectType
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Sets objectType
     */
    public void setObjectType(String val) {
        this.objectType = val;
    }
}
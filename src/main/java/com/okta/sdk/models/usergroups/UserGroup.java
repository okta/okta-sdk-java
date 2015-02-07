package com.okta.sdk.models.usergroups;

import com.okta.sdk.framework.ApiObject;

public class UserGroup extends ApiObject {

    /**
     * unique key for group
     */
    private String id;

    /**
     * determines the group’s profile
     */
    private String objectClass;

    /**
     * the group’s profile attributes
     */
    private UserGroupProfile profile;

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
     * Gets objectClass
     */
    public String getObjectClass() {
        return this.objectClass;
    }

    /**
     * Sets objectClass
     */
    public void setObjectClass(String val) {
        this.objectClass = val;
    }

    /**
     * Gets profile
     */
    public UserGroupProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets profile
     */
    public void setProfile(UserGroupProfile val) {
        this.profile = val;
    }
}
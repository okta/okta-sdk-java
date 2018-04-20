package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Provisioning extends ApiObject {
    private Boolean profileMaster;
    private String action;
    private Groups groups;

    public Boolean getProfileMaster() {
        return profileMaster;
    }

    public void setProfileMaster(Boolean profileMaster) {
        this.profileMaster = profileMaster;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Groups getGroups() {
        return groups;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }
}

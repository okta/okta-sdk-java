package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

import java.util.List;

public class Groups extends ApiObject{
    public static final String ACTION_NONE = "NONE";
    public static final String ACTION_ASSIGN = "ASSIGN";

    private String action;
    private List<String> assignments;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }
}

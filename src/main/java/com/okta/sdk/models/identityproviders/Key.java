package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

import java.util.List;

public class Key extends ApiObject {
    private String kid;
    private List<String> x5c;

    public List<String> getX5c() {
        return x5c;
    }

    public void setX5c(List<String> x5c) {
        this.x5c = x5c;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }
}

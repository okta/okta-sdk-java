package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Credentials extends ApiObject {
    private Trust trust;

    public Trust getTrust() {
        return trust;
    }

    public void setTrust(Trust trust) {
        this.trust = trust;
    }
}

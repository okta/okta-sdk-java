package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Algorithm extends ApiObject {
    private Signature signature;

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }
}

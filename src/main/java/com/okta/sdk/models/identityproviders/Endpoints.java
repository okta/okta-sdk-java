package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Endpoints extends ApiObject {
    private SingleSignOnEndpoint sso;
    private AssertionConsumerServiceEndpoint acs;

    public SingleSignOnEndpoint getSso() {
        return sso;
    }

    public void setSso(SingleSignOnEndpoint sso) {
        this.sso = sso;
    }

    public AssertionConsumerServiceEndpoint getAcs() {
        return acs;
    }

    public void setAcs(AssertionConsumerServiceEndpoint acs) {
        this.acs = acs;
    }
}

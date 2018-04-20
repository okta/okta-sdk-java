package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Trust extends ApiObject {
    private String issuer;
    private String audience;
    private String kid;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }
}

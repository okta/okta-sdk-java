package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Policy extends ApiObject {
    private Integer maxClockSkew;
    private Provisioning provisioning;
    private AccountLink accountLink;
    private Subject subject;

    public Integer getMaxClockSkew() {
        return maxClockSkew;
    }

    public void setMaxClockSkew(Integer maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }

    public Provisioning getProvisioning() {
        return provisioning;
    }

    public void setProvisioning(Provisioning provisioning) {
        this.provisioning = provisioning;
    }

    public AccountLink getAccountLink() {
        return accountLink;
    }

    public void setAccountLink(AccountLink accountLink) {
        this.accountLink = accountLink;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}

package com.okta.sdk.models.auth;

import com.okta.sdk.framework.ApiObject;

public class OrgAnonymousInfo extends ApiObject {

    private String name;

    private String supportPhoneNumber;

    private String technicalContact;

    /**
     * Gets name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name
     */
    public void setName(String val) {
        this.name = val;
    }

    /**
     * Gets supportPhoneNumber
     */
    public String getSupportPhoneNumber() {
        return this.supportPhoneNumber;
    }

    /**
     * Sets supportPhoneNumber
     */
    public void setSupportPhoneNumber(String val) {
        this.supportPhoneNumber = val;
    }

    /**
     * Gets technicalContact
     */
    public String getTechnicalContact() {
        return this.technicalContact;
    }

    /**
     * Sets technicalContact
     */
    public void setTechnicalContact(String val) {
        this.technicalContact = val;
    }
}
package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Settings extends ApiObject {
    private String nameFormat;

    public String getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }
}

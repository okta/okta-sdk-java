package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class AssertionConsumerServiceEndpoint extends ApiObject {
    public static final String HTTP_POST_BINDING = "HTTP-POST";
    public static final String INSTANCE_TYPE = "INSTANCE";

    private String binding;
    private String type;

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

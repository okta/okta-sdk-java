package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class SingleSignOnEndpoint extends ApiObject {
    public static final String HTTP_POST_BINDING = "HTTP-POST";

    private String binding;
    private String type;
    private String url;
    private String destination;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}

package com.okta.sdk.models.apps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Signing {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @JsonProperty(value = "kid")
    private String keyId;

    public String getKeyId() {
        return keyId;
    }
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}

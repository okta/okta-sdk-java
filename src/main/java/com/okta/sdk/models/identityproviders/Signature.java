package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Signature extends ApiObject {
    public static final String ALGORITHM_SHA256 = "SHA-256";
    public static final String SCOPE_REQUEST = "REQUEST";
    public static final String SCOPE_ANY = "ANY";

    private String algorithm;
    private String scope;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}

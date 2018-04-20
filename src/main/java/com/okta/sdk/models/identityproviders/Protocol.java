package com.okta.sdk.models.identityproviders;

import com.okta.sdk.framework.ApiObject;

public class Protocol extends ApiObject {
    private String type;
    private Algorithms algorithms;
    private Credentials credentials;
    private Endpoints endpoints;
    private Settings settings;

    public Algorithms getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Algorithms algorithms) {
        this.algorithms = algorithms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}

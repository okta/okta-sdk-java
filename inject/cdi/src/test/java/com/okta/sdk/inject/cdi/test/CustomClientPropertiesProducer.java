package com.okta.sdk.inject.cdi.test;

import com.okta.sdk.client.ClientConfiguration;

import javax.enterprise.inject.Produces;

public class CustomClientPropertiesProducer {

    @Produces
    protected ClientConfiguration createClientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setOrgUrl("https://oktatest.example.com");
        return clientConfiguration;
    }
}

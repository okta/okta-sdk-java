package com.okta.sdk.inject.cdi;

import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientConfiguration;
import com.okta.sdk.client.Clients;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

public class ClientProducer {

    @Produces
    protected Client createClient(Instance<ClientConfiguration> optionalClientConfiguration,
                                  Instance<CacheManager> optionalCacheManager) {

        ClientConfiguration oktaClientProperties = (optionalClientConfiguration.isResolvable())
                ? optionalClientConfiguration.get()
                : new ClientConfiguration();

        return Clients.builder()
                .withConfiguration(oktaClientProperties)
                .setCacheManager(oktaSdkCacheManager(optionalCacheManager))
                .build();
    }

    private CacheManager oktaSdkCacheManager(Instance<CacheManager> optionalCacheManager) {
        return optionalCacheManager.isResolvable() ? optionalCacheManager.get() : null;
    }
}
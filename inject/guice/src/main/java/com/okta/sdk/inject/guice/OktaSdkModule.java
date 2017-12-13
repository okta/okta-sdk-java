package com.okta.sdk.inject.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.util.Providers;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientConfiguration;
import com.okta.sdk.client.Clients;

public class OktaSdkModule extends AbstractModule {
    @Override
    protected void configure() {

        OptionalBinder.newOptionalBinder(binder(), CacheManager.class).setDefault().toProvider(Providers.of(null));

        final Provider<ClientConfiguration> clientPropertiesProvider = getProvider(ClientConfiguration.class);
        final Provider<CacheManager> cacheManagerProvider = getProvider(CacheManager.class);
        bind(Client.class).toProvider(() -> {

            ClientConfiguration clientConfiguration = clientPropertiesProvider.get();
            return Clients.builder()
                    .withConfiguration(clientConfiguration)
                    .setCacheManager(oktaSdkCacheManager(cacheManagerProvider))
                    .build();
        });
    }

    private CacheManager oktaSdkCacheManager(Provider<CacheManager> cacheManagerProvider) {
        return cacheManagerProvider.get();
    }
}
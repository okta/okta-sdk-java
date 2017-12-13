package com.okta.sdk.inject.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.multibindings.OptionalBinder
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.Client
import com.okta.sdk.client.ClientConfiguration
import org.mockito.Mockito
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.sameInstance

class OktaSdkModuleTest {

    @Test
    void basicInjectTest() {
        Injector injector = Guice.createInjector(new OktaSdkModule())
        Client client = injector.getInstance(Client.class)
        assertThat client, notNullValue()
    }

    @Test
    void customCacheManger() {
        CacheManager cacheManager = Mockito.mock(CacheManager)
        Injector injector = Guice.createInjector(new CustomCacheManagerModule(cacheManager), new OktaSdkModule())
        Client client = injector.getInstance(Client.class)
        assertThat client, notNullValue()
        assertThat client.getCacheManager(), sameInstance(cacheManager)
    }

    @Test
    void customClientConfig() {
        Injector injector = Guice.createInjector(new OktaSdkModule(), new CustomClientConfigModule())
        Client client = injector.getInstance(Client.class)
        assertThat client, notNullValue()
        assertThat client.dataStore.baseUrlResolver.getBaseUrl(), equalTo("https://oktatest.example.com")
    }
}

class CustomCacheManagerModule extends AbstractModule {

    final CacheManager cacheManager

    CustomCacheManagerModule(CacheManager cacheManager) {
        this.cacheManager = cacheManager
    }

    @Override
    protected void configure() {
        OptionalBinder.newOptionalBinder(binder(), CacheManager.class).setBinding().toInstance(cacheManager)
    }
}

class CustomClientConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        ClientConfiguration clientProperties = new ClientConfiguration()
        clientProperties.setOrgUrl("https://oktatest.example.com")
        bind(ClientConfiguration).toInstance(clientProperties)
    }
}
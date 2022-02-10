/*
 * Copyright 2022-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.resource

import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Client
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.impl.client.DefaultClient
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.impl.resource.application.DefaultApplication
import com.okta.sdk.impl.resource.application.DefaultProvisioningConnectionProfile
import com.okta.sdk.impl.resource.application.DefaultProvisioningConnectionRequest
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.ProvisioningConnection
import com.okta.sdk.resource.ProvisioningConnectionStatus
import com.okta.sdk.resource.VoidResource
import com.okta.sdk.resource.application.ProvisioningConnectionAuthScheme
import com.okta.sdk.resource.application.ProvisioningConnectionProfile
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

/**
 * Unit tests of
 * @see ProvisioningConnection
 * @see ProvisioningConnectionProfile
 */
class ProvisioningConnectionTest {

    private static String MOCK_APP_ID = "101W_juydrDRByB7fUdRyE2JQ"

    @Test
    void testGetDefaultProvisioningConnectionForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                final InternalDataStore dataStore = mock(InternalDataStore)
                def provConnData = [authScheme:"UNKNOWN", status: "UNKNOWN"] as HashMap
                def provisioningConnection = new DefaultProvisioningConnection(dataStore, provConnData)
                when(dataStore.getResource("/api/v1/apps/${MOCK_APP_ID}/connections/default",
                    ProvisioningConnection, Collections.emptyMap(), Collections.emptyMap())).thenReturn(provisioningConnection)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")

        def provisioningConnection = client.getDefaultProvisioningConnectionForApplication(application.getId())

        assertThat(provisioningConnection, notNullValue())
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.UNKNOWN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.UNKNOWN)

        verify(client.dataStore)
            .getResource(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/connections/default".toString()),
                (Class) eq(ProvisioningConnection.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }

    @Test
    void testSetDefaultProvisioningConnectionForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                final InternalDataStore dataStore = mock(InternalDataStore)
                def provConnData = [authScheme:"TOKEN", status: "ENABLED"] as HashMap
                def provisioningConnection = new DefaultProvisioningConnection(dataStore, provConnData)
                when(dataStore.create(
                    (String) eq("/api/v1/apps/${MOCK_APP_ID}/connections/default".toString()),
                    any(),
                    isNull(),
                    (Class) eq(ProvisioningConnection.class),
                    eq([activate: true] as HashMap),
                    eq(Collections.emptyMap())))
                    .thenReturn(provisioningConnection)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")
        def profile = new DefaultProvisioningConnectionProfile(client.dataStore)
            .setAuthScheme(ProvisioningConnectionAuthScheme.TOKEN)
            .setToken("foo")
        def provisioningRequest = new DefaultProvisioningConnectionRequest(client.dataStore).setProfile(profile)

        def provisioningConnection = client
            .setDefaultProvisioningConnectionForApplication(provisioningRequest, application.getId(),true)

        assertThat provisioningConnection, notNullValue()
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.TOKEN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.ENABLED)

        verify(client.dataStore)
            .create(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/connections/default".toString()),
                any(),
                isNull(),
                (Class) eq(ProvisioningConnection.class),
                eq([activate: true] as HashMap),
                eq(Collections.emptyMap()))
    }

    @Test
    void testActivateDefaultProvisioningConnectionForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                final InternalDataStore dataStore = mock(InternalDataStore)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")

        client.activateDefaultProvisioningConnectionForApplication(application.getId())

        verify(client.dataStore)
            .create(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/connections/default/lifecycle/activate".toString()),
                any(),
                isNull(),
                (Class) eq(VoidResource.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }

    @Test
    void testDeactivateDefaultProvisioningConnectionForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                final InternalDataStore dataStore = mock(InternalDataStore)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")

        client.deactivateDefaultProvisioningConnectionForApplication(application.getId())

        verify(client.dataStore)
            .create(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/connections/default/lifecycle/deactivate".toString()),
                any(),
                isNull(),
                (Class) eq(VoidResource.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }
}

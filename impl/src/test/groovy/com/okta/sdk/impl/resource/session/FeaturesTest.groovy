/*
 * Copyright 2020-Present Okta, Inc.
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
package com.okta.sdk.impl.resource.session

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
import com.okta.sdk.impl.resource.DefaultFeature
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.Feature
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

/**
 * Tests for the Features API.
 *
 * @since 2.0.0
 */
class FeaturesTest {

    @Test
    void toggleFeatureTest() {

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

                DefaultFeature enabledFeature = new DefaultFeature(dataStore, [id: "test_feature_id", name: "test feature", status: "ENABLED"])
                when(dataStore.create(
                    (String) eq("/api/v1/features/test_feature_id/enable"),
                    any(),
                    isNull(),
                    (Class) eq(Feature.class),
                    eq(Collections.emptyMap()),
                    eq(Collections.emptyMap())))
                    .thenReturn(enabledFeature)

                DefaultFeature disabledFeature = new DefaultFeature(dataStore, [id: "test_feature_id", name: "test feature", status: "DISABLED"])
                when(dataStore.create(
                    (String) eq("/api/v1/features/test_feature_id/disable"),
                    any(),
                    isNull(),
                    (Class) eq(Feature.class),
                    eq(Collections.emptyMap()),
                    eq(Collections.emptyMap())))
                    .thenReturn(disabledFeature)
                return dataStore
            }
        }

        // enable
        client.updateFeatureLifecycle("test_feature_id", "enable", "force")

        verify(client.dataStore).create(
            (String) eq("/api/v1/features/test_feature_id/enable".toString()),
            any(),
            any(),
            (Class) eq(Feature.class),
            eq(Collections.singletonMap("mode", "force")),
            eq(Collections.emptyMap()))

        // disable
        client.updateFeatureLifecycle("test_feature_id", "disable", "force")

        verify(client.dataStore).create(
            (String) eq("/api/v1/features/test_feature_id/disable".toString()),
            any(),
            any(),
            (Class) eq(Feature.class),
            eq(Collections.singletonMap("mode", "force")),
            eq(Collections.emptyMap()))
    }
}
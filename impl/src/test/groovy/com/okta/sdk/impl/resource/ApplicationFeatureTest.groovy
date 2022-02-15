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
import com.okta.sdk.impl.ds.JacksonMapMarshaller
import com.okta.sdk.impl.resource.DefaultApplication
import com.okta.sdk.impl.resource.DefaultApplicationFeature
import com.okta.sdk.impl.resource.DefaultApplicationFeatureList
import com.okta.sdk.impl.resource.DefaultCapabilitiesObject
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import com.okta.sdk.resource.ApplicationFeature
import com.okta.sdk.resource.ApplicationFeatureList
import com.okta.sdk.resource.CapabilitiesObject
import com.okta.sdk.resource.EnabledStatus
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

/**
 * Unit tests for Application Feature operations.
 *
 * @since 2.10.0
 */
class ApplicationFeatureTest {

    private static String MOCK_APP_ID = "101W_juydrDRByB7fUdRyE2JQ"
    private static String FEATURE_NAME = "USER_PROVISIONING"

    @Test
    void testListFeaturesForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {
                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultApplicationFeatureList appFeatureList = new DefaultApplicationFeatureList(dataStore, data)
                when(dataStore.getResource("/api/v1/apps/${MOCK_APP_ID}/features",
                    ApplicationFeatureList, Collections.emptyMap(), Collections.emptyMap())).thenReturn(appFeatureList)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")

        def applicationFeatureList = client.listFeaturesForApplication(application.getId())

        assertThat(applicationFeatureList.properties.internalProperties.get("items"), hasSize(1))
        assertThat(applicationFeatureList.properties.internalProperties.get("items")[0].get("name"), is(FEATURE_NAME))
        assertThat(applicationFeatureList.properties.internalProperties.get("items")[0].get("status"), is("ENABLED"))

        verify(client.dataStore)
            .getResource(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/features".toString()),
                (Class) eq(ApplicationFeatureList.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }

    @Test
    void testGetFeatureForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultApplicationFeature appFeature = new DefaultApplicationFeature(dataStore, data.get("items")[0] as HashMap)
                when(dataStore.getResource("/api/v1/apps/${MOCK_APP_ID}/features/${FEATURE_NAME}",
                    ApplicationFeature, Collections.emptyMap(), Collections.emptyMap())).thenReturn(appFeature)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")
        def applicationFeature = client.getFeatureForApplication(application.getId(), FEATURE_NAME)

        assertThat(applicationFeature, notNullValue())
        assertThat(applicationFeature.getName(), is(FEATURE_NAME))
        assertThat(applicationFeature.getStatus(), is(EnabledStatus.ENABLED))

        verify(client.dataStore)
            .getResource(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/features/${FEATURE_NAME}".toString()),
                (Class) eq(ApplicationFeature.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }

    @Test
    void testUpdateFeatureForApplication() {
        ClientConfiguration clientConfig = new ClientConfiguration()
        clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver("https://example.com"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("foobar")))

        Client client = new DefaultClient(clientConfig, new DisabledCacheManager()) {
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor,
                                                        BaseUrlResolver baseUrlResolver,
                                                        ClientCredentialsResolver clientCredentialsResolver,
                                                        CacheManager cacheManager) {

                Map data = dataFromJsonFile()
                final InternalDataStore dataStore = mock(InternalDataStore)
                DefaultApplicationFeature appFeature = new DefaultApplicationFeature(dataStore, data.get("items")[0] as HashMap)
                when(dataStore.save((String) eq("/api/v1/apps/${MOCK_APP_ID}/features/${FEATURE_NAME}".toString()),
                    any(CapabilitiesObject), (Class) eq(ApplicationFeature.class), eq(false))).thenReturn(appFeature)
                return dataStore
            }
        }

        def application = new DefaultApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")
        def applicationFeature = client.updateFeatureForApplication(
            new DefaultCapabilitiesObject(client.dataStore), application.getId(), FEATURE_NAME)

        assertThat(applicationFeature, notNullValue())
        assertThat(applicationFeature.getName(), is(FEATURE_NAME))
        assertThat(applicationFeature.getStatus(), is(EnabledStatus.ENABLED))

        verify(client.dataStore)
            .save(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/features/${FEATURE_NAME}".toString()),
                any(CapabilitiesObject),
                (Class) eq(ApplicationFeature.class),
                eq(false))
    }

    //OKTA-469414
/*
    @Test
    void testUploadApplicationLogo(){
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

        def application = new DefaultOrg2OrgApplication(client.dataStore)
        application.put("id", "${MOCK_APP_ID}")

        client.uploadApplicationLogo(application.getId(), new File("src/test/resources/okta_logo_favicon.png"))

        verify(client.dataStore)
            .create(
                (String) eq("/api/v1/apps/${MOCK_APP_ID}/logo".toString()),
                any(),
                isNull(),
                (Class) eq(VoidResource.class),
                eq(Collections.emptyMap()),
                eq(Collections.emptyMap()))
    }
*/

    private Map dataFromJsonFile(String resourceFile = '/stubs/application-features.json') {

        return new JacksonMapMarshaller().unmarshal(
            this.getClass().getResource(resourceFile).openStream(),
            Collections.emptyMap())
    }
}

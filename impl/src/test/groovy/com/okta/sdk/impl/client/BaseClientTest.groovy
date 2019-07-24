/*
 * Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.client

import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.okta.sdk.resource.Resource
import org.testng.annotations.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions

class BaseClientTest {

    @Test
    void getResourceTest() {
        def client = getClient()
        def dataStore = client.getDataStore()
        def url = "http://example.com/getResourceTest"

        client.getResource(url, Resource)
        verify(dataStore).getResource(url, Resource)
        verifyNoMoreInteractions(dataStore)
    }

    @Test
    void saveTest() {
        def client = getClient()
        def dataStore = client.getDataStore()
        def resource = mock(Resource)
        def url = "http://example.com/saveTest"

        client.save(url, resource)
        verify(dataStore).save(url, resource)
        verifyNoMoreInteractions(dataStore)
    }

    @Test
    void deleteTest() {
        def client = getClient()
        def dataStore = client.getDataStore()
        def resource = mock(Resource)
        def url = "http://example.com/deleteTest"

        client.delete(url, resource)
        verify(dataStore).delete(url, resource)
        verifyNoMoreInteractions(dataStore)
    }

    @Test
    void createTest() {
        def client = getClient()
        def dataStore = client.getDataStore()
        def resource = mock(Resource)
        def url = "http://example.com/createTest"

        client.create(url, resource)
        verify(dataStore).create(url, resource)
        verifyNoMoreInteractions(dataStore)
    }

    private BaseClient getClient() {
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def cacheManager = mock(CacheManager)
        def requestAuthenticatorFactory = mock(RequestAuthenticatorFactory)
        def baseUrlResolver = mock(BaseUrlResolver)

        def clientConfiguration = new ClientConfiguration()
        clientConfiguration.setClientCredentialsResolver(apiKeyResolver)
        clientConfiguration.setRequestAuthenticatorFactory(requestAuthenticatorFactory)
        clientConfiguration.setAuthenticationScheme(AuthenticationScheme.NONE)
        clientConfiguration.setBaseUrlResolver(baseUrlResolver)

        return new BaseClient(clientConfiguration, cacheManager) {
            @Override
            protected InternalDataStore createDataStore(RequestExecutor requestExecutor, BaseUrlResolver baseUrlResolver1, ClientCredentialsResolver clientCredentialsResolver, CacheManager cacheManager1) {
                return mock(InternalDataStore)
            }
        }
    }
}

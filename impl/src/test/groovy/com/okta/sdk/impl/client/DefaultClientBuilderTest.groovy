/*
 * Copyright 2014 Stormpath, Inc.
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

import com.okta.sdk.api.ApiKey
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.api.ClientApiKey
import com.okta.sdk.impl.api.DefaultApiKeyResolver
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials
import com.okta.sdk.impl.authc.credentials.ClientCredentials
import com.okta.sdk.impl.cache.DefaultCache
import com.okta.sdk.impl.util.BaseUrlResolver
import com.okta.sdk.lang.Duration
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.testng.Assert.*

class DefaultClientBuilderTest {

    def builder, client

    @BeforeMethod
    void before() {
        builder = Clients.builder()
        client = builder.build()
    }

    @Test
    void testBuilder() {
        assertTrue(builder instanceof DefaultClientBuilder)
    }

    @Test
    void testConfigureApiKey() {
        // remove key.txt from src/test/resources and this test will fail
        assertEquals client.dataStore.apiKey.id, "12"
        assertEquals client.dataStore.apiKey.secret, "13"
    }

    @Test
    void testConfigureBaseProperties() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.baseUrl, "https://api.okta.com/v42"
        assertEquals clientBuilder.clientConfiguration.connectionTimeout, 10
        assertEquals clientBuilder.clientConfiguration.authenticationScheme, AuthenticationScheme.BASIC
    }

    @Test
    void testConfigureProxy() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.proxyHost, "proxyyaml" // from yaml
        assertEquals clientBuilder.clientConfiguration.proxyPort, 9999 // from json
        assertEquals clientBuilder.clientConfiguration.proxyUsername, "fooyaml" // from yaml
        assertEquals clientBuilder.clientConfiguration.proxyPassword, "bar" // from properties
    }

    @Test
    void testConfigureBaseUrlResolver(){
        BaseUrlResolver baseUrlResolver = new BaseUrlResolver() {
            @Override
            String getBaseUrl() {
                return "test"
            }
        }

        def testClient = new DefaultClientBuilder().setBaseUrlResolver(baseUrlResolver).build()

        assertEquals(testClient.dataStore.baseUrlResolver.getBaseUrl(), "test")
    }

    @Test
    void testDefaultBaseUrlResolver(){
        assertEquals(client.dataStore.baseUrlResolver.getBaseUrl(), "https://api.okta.com/v42")
    }
}

class DefaultClientBuilderTestCustomCredentials{

    def builder, client, clientCredentials, id, secret

    @BeforeMethod
    void before() {

        id = UUID.randomUUID().toString()
        secret = UUID.randomUUID().toString()

        ApiKey apiKey = new ClientApiKey(id, secret)
        clientCredentials = new ApiKeyCredentials(apiKey)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(clientCredentials)
        client = builder.build()
    }

    @Test
    void testConfigureCredentials() {
        assertEquals client.dataStore.apiKey.id, id
        assertEquals client.dataStore.apiKey.secret, secret
    }

    @Test
    void testCustomClientCredentialsRequireApiKeyResolver(){
        def credentialsId = UUID.randomUUID().toString()
        def credentialsSecret = UUID.randomUUID().toString()

        ClientCredentials customCredentials = new ClientCredentials() {
            @Override
            String getId() {
                return credentialsId
            }

            @Override
            String getSecret() {
                return credentialsSecret
            }
        }


        builder = new DefaultClientBuilder()
        builder.setClientCredentials(customCredentials)

        try {
            client = builder.build()
            fail("Builder should require ApiKeyResolver if non-ApiKeyCredentials are supplied")
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().contains("An ApiKeyResolver must be configured for ClientCredentials other than ApiKeyCredentials."))
        }

    }

    @Test
    void testCustomClientCredentialsAllowedWithApiKeyResolver(){
        def credentialsId = UUID.randomUUID().toString()
        def credentialsSecret = UUID.randomUUID().toString()

        ClientCredentials customCredentials = new ClientCredentials() {
            @Override
            String getId() {
                return credentialsId
            }

            @Override
            String getSecret() {
                return credentialsSecret
            }
        }

        def keyId = UUID.randomUUID().toString()
        def keySecret = UUID.randomUUID().toString()

        def apiKey = new ClientApiKey(keyId, keySecret)
        def apiKeyResolver = new DefaultApiKeyResolver(apiKey)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(customCredentials)
        builder.setApiKeyResolver(apiKeyResolver)
        def testClient = builder.build()

        assertEquals testClient.dataStore.apiKey.id, keyId
        assertEquals testClient.dataStore.apiKey.secret, keySecret
    }
}

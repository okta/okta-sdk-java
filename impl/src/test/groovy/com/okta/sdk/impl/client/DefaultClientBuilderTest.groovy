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

import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.io.DefaultResourceFactory
import com.okta.sdk.impl.io.Resource
import com.okta.sdk.impl.io.ResourceFactory
import com.okta.sdk.impl.util.BaseUrlResolver
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.Test

import static org.testng.Assert.*
import static org.mockito.Mockito.*

class DefaultClientBuilderTest {

    @Test
    void testBuilder() {
        assertTrue(Clients.builder() instanceof DefaultClientBuilder)
    }

    @Test
    void testConfigureApiKey() {
        // remove key.txt from src/test/resources and this test will fail
        def client = new DefaultClientBuilder(noDefaultYamlResourceFactory()).build()
        assertEquals client.dataStore.getClientCredentials().getCredentials(), "13"
    }

    @Test
    void testConfigureBaseProperties() {
        def builder = new DefaultClientBuilder(noDefaultYamlResourceFactory())
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.baseUrl, "https://api.okta.com/v42"
        assertEquals clientBuilder.clientConfiguration.connectionTimeout, 10
        assertEquals clientBuilder.clientConfiguration.authenticationScheme, AuthenticationScheme.SSWS
    }

    @Test
    void testConfigureProxy() {
        def builder = Clients.builder()
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.proxyHost, "proxyyaml" // from yaml
        assertEquals clientBuilder.clientConfiguration.proxyPort, 9009 // from yaml
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
        def client = new DefaultClientBuilder(noDefaultYamlResourceFactory()).build()
        assertEquals(client.dataStore.baseUrlResolver.getBaseUrl(), "https://api.okta.com/v42")
    }

    static ResourceFactory noDefaultYamlResourceFactory() {
        def resourceFactory = spy(new DefaultResourceFactory())
        doAnswer(new Answer<Resource>() {
            @Override
            Resource answer(InvocationOnMock invocation) throws Throwable {
                if (invocation.arguments[0].toString().endsWith("/.okta/okta.yaml")) {
                    return mock(Resource)
                }
                else {
                    return invocation.callRealMethod()
                }
            }
        })
        .when(resourceFactory).createResource(anyString())

        return resourceFactory
    }
}

/*
 * Copyright 2017 Okta
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

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.impl.test.RestoreEnvironmentVariables
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

class DefaultClientBuilderTestCustomCredentialsTest {

    def builder, client, clientCredentials, id, secret

    @BeforeMethod
    void before() {
        clearOktaEnvAndSysProps()

        id = UUID.randomUUID().toString()
        secret = UUID.randomUUID().toString()

        clientCredentials = new TokenClientCredentials(secret)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(clientCredentials)
        client = builder.build()
    }

    @Test
    void testConfigureCredentials() {
        assertEquals client.dataStore.getClientCredentials().getCredentials(), secret
    }

    @Test
    void testCustomClientCredentialsAllowedWithApiKeyResolver(){
        clearOktaEnvAndSysProps()

        def credentialsSecret = UUID.randomUUID().toString()

        ClientCredentials customCredentials = new ClientCredentials<String>() {
            @Override
            String getCredentials() {
                return credentialsSecret
            }
        }

        def keySecret = UUID.randomUUID().toString()

        def apiKey = new TokenClientCredentials(keySecret)
        def apiKeyResolver = new DefaultClientCredentialsResolver(apiKey)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(customCredentials)
        builder.setClientCredentialsResolver(apiKeyResolver)

        def testClient = builder.build()

        assertEquals testClient.dataStore.clientCredentials.credentials, keySecret
    }

    void clearOktaEnvAndSysProps() {
        System.clearProperty("okta.client.authorizationMode")
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_AUTHORIZATIONMODE", null)
    }
}

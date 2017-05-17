package com.okta.sdk.impl.client

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.api.TokenClientCredentials
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

class DefaultClientBuilderTestCustomCredentialsTest {

    def builder, client, clientCredentials, id, secret

    @BeforeMethod
    void before() {

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
        builder.setApiKeyResolver(apiKeyResolver)
        def testClient = builder.build()

        assertEquals testClient.dataStore.clientCredentials.credentials, keySecret
    }
}

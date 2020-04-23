/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

import com.okta.commons.http.config.BaseUrlResolver
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.AuthorizationMode
import com.okta.sdk.client.ClientBuilder
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.Util
import com.okta.sdk.impl.io.DefaultResourceFactory
import com.okta.sdk.impl.io.Resource
import com.okta.sdk.impl.io.ResourceFactory
import com.okta.sdk.impl.oauth2.OAuth2TokenRetrieverException
import com.okta.sdk.impl.test.RestoreEnvironmentVariables
import com.okta.sdk.impl.test.RestoreSystemProperties
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.Listeners
import org.testng.annotations.Test

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.nullValue
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

@Listeners([RestoreSystemProperties, RestoreEnvironmentVariables])
class DefaultClientBuilderTest {

    /**
     * This method MUST be called from each test in order to work with with the Listeners defined above.
     * If this method is invoked with an @BeforeMethod annotation the Listener will be invoked before and after this
     * method as well.
     */
    void clearOktaEnvAndSysProps() {
        System.clearProperty("okta.client.token")
        System.clearProperty("okta.client.orgUrl")
        System.clearProperty("okta.client.authorizationMode")
        System.clearProperty("okta.client.clientId")
        System.clearProperty("okta.client.scopes")
        System.clearProperty("okta.client.privateKey")

        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_TOKEN", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_ORGURL", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_AUTHORIZATIONMODE", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_CLIENTID", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_SCOPES", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_PRIVATEKEY", null)
    }

    @Test
    void testBuilder() {
        assertTrue(Clients.builder() instanceof DefaultClientBuilder)
    }

    @Test
    void testConfigureApiKey() {
        clearOktaEnvAndSysProps()
        // remove key.txt from src/test/resources and this test will fail
        def client = new DefaultClientBuilder(noDefaultYamlResourceFactory()).build()
        assertEquals client.dataStore.getClientCredentials().getCredentials(), "13"
    }

    @Test
    void testConfigureBaseProperties() {
        clearOktaEnvAndSysProps()
        def builder = new DefaultClientBuilder(noDefaultYamlResourceFactory())
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.baseUrl, "https://api.okta.com/v42"
        assertEquals clientBuilder.clientConfiguration.connectionTimeout, 10
        assertEquals clientBuilder.clientConfiguration.authenticationScheme, AuthenticationScheme.SSWS
    }

    @Test
    void testConfigureProxy() {
        clearOktaEnvAndSysProps()
        def builder = Clients.builder()
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertThat clientBuilder.clientConfiguration.proxyHost, is("proxyyaml") // from yaml
        assertThat clientBuilder.clientConfiguration.proxyPort, is(9009) // from yaml
        assertThat clientBuilder.clientConfiguration.proxyUsername, is("fooyaml") // from yaml
        assertThat clientBuilder.clientConfiguration.proxyPassword, is("bar") // from properties

        def proxy = clientBuilder.clientConfiguration.getProxy()
        assertThat proxy.host, is("proxyyaml")
        assertThat proxy.port, is(9009)
        assertThat proxy.username, is("fooyaml")
        assertThat proxy.password, is("bar")
    }

    @Test
    void testConfigureProxyWithoutPassword() {
        clearOktaEnvAndSysProps()
        def builder = Clients.builder()
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        clientBuilder.clientConfiguration.setProxyPassword(null) // override config from properties
        clientBuilder.clientConfiguration.setProxyUsername(null)
        assertThat clientBuilder.clientConfiguration.proxyHost, is("proxyyaml") // from yaml
        assertThat clientBuilder.clientConfiguration.proxyPort, is(9009) // from yaml

        def proxy = clientBuilder.clientConfiguration.getProxy()
        assertThat proxy.host, is("proxyyaml")
        assertThat proxy.port, is(9009)
        assertThat proxy.username, nullValue()
        assertThat proxy.password, nullValue()
    }

    @Test
    void testConfigureBaseUrlResolver(){
        clearOktaEnvAndSysProps()
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
        clearOktaEnvAndSysProps()
        def client = new DefaultClientBuilder(noDefaultYamlResourceFactory()).build()
        assertEquals(client.dataStore.baseUrlResolver.getBaseUrl(), "https://api.okta.com/v42")
    }

    @Test
    void testNullBaseUrl() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setClientCredentials(new TokenClientCredentials("some-token"))
                .build()
        }
    }

    @Test
    void testHttpBaseUrlForTesting() {
        clearOktaEnvAndSysProps()
        System.setProperty(ClientBuilder.DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME, "true")
        // shouldn't throw IllegalArgumentException
        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("http://okta.example.com")
            .setClientCredentials(new TokenClientCredentials("some-token"))
            .build()
    }

    @Test
    void testHttpBaseUrlForTestingDisabled() {
        clearOktaEnvAndSysProps()
        System.setProperty(ClientBuilder.DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME, "false")
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                    .setOrgUrl("http://okta.example.com")
                    .setClientCredentials(new TokenClientCredentials("some-token"))
                    .build()
        }
    }

    @Test
    void testNullApiToken() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .build()
        }
    }

    @Test
    void testOAuth2NullClientId() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .build()
        }
    }

    @Test
    void testOAuth2NullScopes() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .build()
        }
    }

    @Test
    void testOAuth2EmptyScopes() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(new HashSet<String>())
                .build()
        }
    }

    @Test
    void testOAuth2NullPrivateKey() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(new HashSet<>(Arrays.asList({"okta.apps.read"})))
                .setPrivateKey(null)
                .build()
        }
    }

    @Test
    void testOAuth2InvalidPrivateKeyPemFilePath() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(new HashSet<>(Arrays.asList({"okta.apps.read"})))
                .setPrivateKey("blahblah.pem")
                .build()
        }
    }

    @Test
    void testOAuth2InvalidPrivateKeyPemFileContent() {
        clearOktaEnvAndSysProps()
        File privateKeyFile = File.createTempFile("tmp",".pem")
        privateKeyFile.write("-----INVALID PEM CONTENT-----")
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(new HashSet<>(Arrays.asList({"okta.apps.read"})))
                .setPrivateKey(privateKeyFile.path)
                .build()
        }

        privateKeyFile.delete()
    }

    @Test
    void testOAuth2UnsupportedPrivateKeyAlgorithm() {
        clearOktaEnvAndSysProps()

        // DSA algorithm is unsupported (we support only RSA & EC)
        File privateKeyFile = generatePrivateKey("DSA", 2048, "privateKey", ".pem")

        Set<String> scopes = new HashSet<>();
        scopes.add("okta.apps.read")
        scopes.add("okta.apps.manage")

        Util.expect(OAuth2TokenRetrieverException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(scopes)
                .setPrivateKey(privateKeyFile.path)
                .build()
        }

        privateKeyFile.delete()
    }

    @Test
    void testOAuth2SemanticallyValidInputParams() {
        clearOktaEnvAndSysProps()

        File privateKeyFile = generatePrivateKey("RSA", 2048, "privateKey", ".pem")

        Set<String> scopes = new HashSet<>();
        scopes.add("okta.apps.read")
        scopes.add("okta.apps.manage")

        // expected because the URL is not an actual endpoint
        Util.expect(OAuth2TokenRetrieverException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(scopes)
                .setPrivateKey(privateKeyFile.path)
                .build()
        }

        privateKeyFile.delete()
    }

    @Test
    void testOAuth2WithEnvVariables() {
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_ORGURL",
            "https://okta.example.com")
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_AUTHORIZATIONMODE",
            AuthorizationMode.PRIVATE_KEY.getLabel()) // "PrivateKey"
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_CLIENTID",
            "client12345")
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_SCOPES",
            "okta.users.read okta.users.manage okta.apps.read okta.apps.manage")

        File privateKeyFile = generatePrivateKey("RSA", 2048, "privateKey", ".pem")

        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_PRIVATEKEY", privateKeyFile.path)

        // expected because the URL is not an actual endpoint
        Util.expect(OAuth2TokenRetrieverException) {
            new DefaultClientBuilder().build()
        }

        privateKeyFile.delete()
    }

    // helper methods

    static generatePrivateKey(String algorithm, int keySize, String fileNamePrefix, String fileNameSuffix) {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm)
        keyGen.initialize(keySize)
        KeyPair key = keyGen.generateKeyPair()
        PrivateKey privateKey = key.getPrivate()
        String encodedString = "-----BEGIN PRIVATE KEY-----\n"
        encodedString = encodedString + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
        encodedString = encodedString + "-----END PRIVATE KEY-----\n"
        File file = File.createTempFile(fileNamePrefix,fileNameSuffix)
        file.write(encodedString)
        return file
    }

    static ResourceFactory noDefaultYamlNoAppYamlResourceFactory() {
        def resourceFactory = spy(new DefaultResourceFactory())
        doAnswer(new Answer<Resource>() {
            @Override
            Resource answer(InvocationOnMock invocation) throws Throwable {
                String arg = invocation.arguments[0].toString();
                if (arg.endsWith("/.okta/okta.yaml") || arg.equals("classpath:okta.yaml")) {
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

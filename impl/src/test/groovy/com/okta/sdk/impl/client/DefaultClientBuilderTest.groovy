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

import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.cache.Caches
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.AuthorizationMode
import com.okta.sdk.client.ClientBuilder
import com.okta.sdk.client.Clients
import com.okta.sdk.impl.Util
import com.okta.sdk.impl.io.DefaultResourceFactory
import com.okta.sdk.impl.io.Resource
import com.okta.sdk.impl.io.ResourceFactory
import com.okta.sdk.impl.oauth2.OAuth2HttpException
import com.okta.sdk.impl.oauth2.OAuth2TokenRetrieverException
import com.okta.sdk.impl.test.RestoreEnvironmentVariables
import com.okta.sdk.impl.test.RestoreSystemProperties
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.client.Configuration
import org.apache.hc.client5.http.HttpRequestRetryStrategy
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.util.TimeValue
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.Listeners
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.nio.file.Path
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

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
    static void clearOktaEnvAndSysProps() {
        System.clearProperty("okta.client.token")
        System.clearProperty("okta.client.orgUrl")
        System.clearProperty("okta.client.authorizationMode")
        System.clearProperty("okta.client.clientId")
        System.clearProperty("okta.client.kid")
        System.clearProperty("okta.client.scopes")
        System.clearProperty("okta.client.privateKey")
        System.clearProperty("okta.client.connectionTimeout")
        System.clearProperty("okta.client.oauth2.accessToken")

        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_TOKEN", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_ORGURL", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_AUTHORIZATIONMODE", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_CLIENTID", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_KID", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_SCOPES", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_PRIVATEKEY", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_CONNECTIONTIMEOUT", null)
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_OAUTH2_ACCESSTOKEN", null)
    }

    @Test
    void testBuilder() {
        assertTrue(Clients.builder() instanceof DefaultClientBuilder)
    }

    @Test
    void testDefaultApiClient() {
        Configuration.getDefaultApiClient()
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    void testIncorrectApiClient_nullHttpClient() {
        new ApiClient(null, Caches.newDisabledCacheManager())
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    void testIncorrectApiClient_nullCacheManager() {
        new ApiClient(HttpClients.createDefault(), null)
    }

    @Test
    void testConfigureBaseProperties() {
        clearOktaEnvAndSysProps()
        def builder = new DefaultClientBuilder(noDefaultYamlResourceFactory())
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.baseUrl, "https://api.okta.com/v42"
        assertEquals 10, (int) clientBuilder.clientConfiguration.connectionTimeout
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
            .setOrgUrl("https://okta.example.com")
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
                .setOrgUrl("http://okta.example.com")
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
    void testOAuth2WithNoPrivateKeyPemAndNoAccessToken() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(new HashSet<>(Arrays.asList("okta.users.manage", "okta.apps.manage", "okta.groups.manage")))
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
                .setScopes([] as Set)
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
                .setScopes(["okta.apps.read"] as Set)
                .setPrivateKey(null as Path)
                .build()
        }
    }

    @Test
    void testOAuth2InvalidPrivateKey_PemFilePath() {
        clearOktaEnvAndSysProps()
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(["okta.apps.read"] as Set)
                .setPrivateKey("/some/invalid/path/privateKey.pem")
                .build()
        }
    }

    @Test
    void testOAuth2InvalidPrivateKey_PemFileContent() {
        clearOktaEnvAndSysProps()
        File privateKeyFile = File.createTempFile("tmp", ".pem")
        privateKeyFile.write("-----INVALID PEM CONTENT-----")
        Util.expect(IllegalArgumentException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(["okta.apps.read"] as Set)
                .setPrivateKey(privateKeyFile.text)
                .build()
        }

        privateKeyFile.delete()
    }

    @Test
    void testOAuth2UnsupportedPrivateKeyAlgorithm() {
        clearOktaEnvAndSysProps()

        // DSA algorithm is unsupported (we support only RSA & EC)
        File privateKeyFile = generatePrivateKey("DSA", 2048, "privateKey", ".pem")

        Util.expect(OAuth2TokenRetrieverException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
                .setPrivateKey(privateKeyFile.path)
                .build()
        }

        privateKeyFile.delete()
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyPemFilePath() {
        clearOktaEnvAndSysProps()

        File privateKeyFile = generatePrivateKey("RSA", 2048, "privateKey", ".pem")

        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("https://okta.example.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("client12345")
            .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
            .setPrivateKey(privateKeyFile.path)
            .build()

        privateKeyFile.delete()
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyPemFileContent() {
        clearOktaEnvAndSysProps()

        File privateKeyFile = generatePrivateKey("RSA", 2048, "anotherPrivateKey", ".pem")

        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("https://okta.example.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("client12345")
            .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
            .setPrivateKey(privateKeyFile.text)
            .build()

        privateKeyFile.delete()
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyPemPath() {
        clearOktaEnvAndSysProps()

        File privateKeyFile = generatePrivateKey("RSA", 2048, "anotherPrivateKey", ".pem")

        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("https://okta.example.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("client12345")
            .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
            .setPrivateKey(privateKeyFile.toPath())
            .build()

        privateKeyFile.delete()
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyPemInputStream() {
        clearOktaEnvAndSysProps()

        File privateKeyFile = generatePrivateKey("RSA", 2048, "anotherPrivateKey", ".pem")
        InputStream privateKeyFileInputStream = new FileInputStream(privateKeyFile)

        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("https://okta.example.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("client12345")
            .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
            .setPrivateKey(privateKeyFileInputStream)
            .build()

        privateKeyFile.delete()
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyRSA() {
        clearOktaEnvAndSysProps()

        PrivateKey privateKey = generatePrivateKey("RSA", 2048)

        new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
            .setOrgUrl("https://okta.example.com")
            .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
            .setClientId("client12345")
            .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
            .setPrivateKey(privateKey)
            .build()
    }

    @Test
    void testOAuth2SemanticallyValidInputParams_withPrivateKeyEC() {
        clearOktaEnvAndSysProps()

        PrivateKey privateKey = generatePrivateKey("EC", 256)

        // expected because the URL is not an actual endpoint
        Util.expect(OAuth2TokenRetrieverException) {
            new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
                .setOrgUrl("https://okta.example.com")
                .setAuthorizationMode(AuthorizationMode.PRIVATE_KEY)
                .setClientId("client12345")
                .setScopes(["okta.apps.read", "okta.apps.manage"] as Set)
                .setPrivateKey(privateKey)
                .build()
        }
    }

    @Test(expectedExceptions = OAuth2HttpException.class)
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

        new DefaultClientBuilder().build()

        privateKeyFile.delete()
    }

    @Test
    void testEnvironmentValueKid() {
        clearOktaEnvAndSysProps()
        RestoreEnvironmentVariables.setEnvironmentVariable("OKTA_CLIENT_KID", "kid-value")
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) Clients.builder()
        assertThat clientBuilder.clientConfiguration.getKid(), is("kid-value")
    }

    @Test
    void testSystemPropertyKid() {
        clearOktaEnvAndSysProps()
        System.setProperty("okta.client.kid", "kid-value")
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) Clients.builder()
        assertThat clientBuilder.clientConfiguration.getKid(), is("kid-value")
    }

    // Regression coverage for OKTA-1235975: an unbounded server-controlled retry sleep on HTTP 429.
    // A malicious/MitM server can return an arbitrarily large x-rate-limit-reset value; the retry
    // strategy built here must always cap the resulting sleep, whether or not retryMaxElapsed was
    // explicitly configured.
    //
    // Unlike OktaHttpRequestRetryStrategyTest (which exercises the capping math directly against a
    // strategy instance), these tests go through the real DefaultClientBuilder.createHttpClientBuilder
    // wiring - so a regression like swapping getRetryMaxAttempts()/getRetryMaxElapsed() arguments at
    // the call site would be caught here even though the strategy's own unit tests would still pass.

    @Test
    void testRetryStrategyDefaultRetryMaxElapsedStillBoundsMalicious429Delay() {
        clearOktaEnvAndSysProps()
        DefaultClientBuilder clientBuilder = new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
        // retryMaxElapsed intentionally left unconfigured (defaults to 0 / "no explicit limit")

        HttpRequestRetryStrategy retryStrategy = buildRetryStrategy(clientBuilder)
        TimeValue retryInterval = retryStrategy.getRetryInterval(maliciousRateLimitResponse(), 1, mock(HttpContext))

        // must never honor the malicious ~1 year delay; a sane client-side ceiling always applies
        assertTrue retryInterval.toMilliseconds() <= TimeUnit.MINUTES.toMillis(1)
    }

    @Test
    void testRetryStrategyHonorsConfiguredRetryMaxElapsedFor429Delay() {
        clearOktaEnvAndSysProps()
        DefaultClientBuilder clientBuilder = new DefaultClientBuilder(noDefaultYamlNoAppYamlResourceFactory())
        clientBuilder.setRetryMaxElapsed(5) // seconds

        HttpRequestRetryStrategy retryStrategy = buildRetryStrategy(clientBuilder)
        TimeValue retryInterval = retryStrategy.getRetryInterval(maliciousRateLimitResponse(), 1, mock(HttpContext))

        assertEquals(TimeUnit.SECONDS.toMillis(5) as long, retryInterval.toMilliseconds() as long)
    }

    // helper methods

    /**
     * Builds the retry strategy exactly as DefaultClientBuilder wires it up in createHttpClientBuilder,
     * so the test exercises the real wiring rather than re-testing OktaHttpRequestRetryStrategy in isolation.
     *
     * HttpClientBuilder doesn't expose a public getter for the configured retry strategy, so this
     * reaches into its private `retryStrategy` field via reflection. That's a known trade-off: it
     * would break if Apache HttpClient renamed/removed that field. Accepted here because it's the
     * only way to catch a wiring regression (e.g. swapped constructor arguments) without it; if
     * client5 ever restructures HttpClientBuilder, delete these two tests rather than fight the
     * reflection - the direct OktaHttpRequestRetryStrategy tests remain the source of truth for the
     * capping behavior itself.
     */
    static HttpRequestRetryStrategy buildRetryStrategy(DefaultClientBuilder clientBuilder) {
        HttpClientBuilder httpClientBuilder =
            clientBuilder.createHttpClientBuilder(clientBuilder.getClientConfiguration())
        Field field = HttpClientBuilder.class.getDeclaredField("retryStrategy")
        field.setAccessible(true)
        return (HttpRequestRetryStrategy) field.get(httpClientBuilder)
    }

    /**
     * A 429 response with an x-rate-limit-reset one year in the future, simulating a malicious or
     * MitM server trying to force an excessively long client-side sleep.
     */
    static HttpResponse maliciousRateLimitResponse() {
        HttpResponse response = mock(HttpResponse)
        when(response.getCode()).thenReturn(429)

        long currentTime = System.currentTimeMillis()
        long maliciousResetTime = currentTime / 1000 + TimeUnit.DAYS.toSeconds(365)

        Header resetHeader = mock(Header)
        when(resetHeader.getValue()).thenReturn(String.valueOf(maliciousResetTime))
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader)

        Header dateHeader = mock(Header)
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)))
        when(response.getHeader("Date")).thenReturn(dateHeader)

        return response
    }

    static generatePrivateKey(String algorithm, int keySize, String fileNamePrefix, String fileNameSuffix) {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm)
        keyGen.initialize(keySize)
        KeyPair key = keyGen.generateKeyPair()
        PrivateKey privateKey = key.getPrivate()
        String encodedString = "-----BEGIN PRIVATE KEY-----\n"
        encodedString = encodedString + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
        encodedString = encodedString + "-----END PRIVATE KEY-----\n"
        File file = File.createTempFile(fileNamePrefix, fileNameSuffix)
        file.write(encodedString)
        return file
    }

    static generatePrivateKey(String algorithm, int keySize) {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm)
        keyGen.initialize(keySize)
        KeyPair key = keyGen.generateKeyPair()
        PrivateKey privateKey = key.getPrivate()
        return privateKey
    }

    static ResourceFactory noDefaultYamlNoAppYamlResourceFactory() {
        def resourceFactory = spy(new DefaultResourceFactory())
        doAnswer(new Answer<Resource>() {
            @Override
            Resource answer(InvocationOnMock invocation) throws Throwable {
                String arg = invocation.arguments[0].toString()
                if (arg.endsWith("/.okta/okta.yaml") || arg == "classpath:okta.yaml") {
                    return mock(Resource)
                } else {
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
                } else {
                    return invocation.callRealMethod()
                }
            }
        })
            .when(resourceFactory).createResource(anyString())

        return resourceFactory
    }
}
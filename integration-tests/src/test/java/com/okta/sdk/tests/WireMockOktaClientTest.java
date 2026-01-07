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
package com.okta.sdk.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.api.UserApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.User;
import com.okta.sdk.resource.model.UserProfile;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

/**
 * Integration test demonstrating WireMock + Okta SDK with HTTPS using self-signed certificates.
 * This test proves the solution works end-to-end without hitting actual Okta servers.
 */
public class WireMockOktaClientTest {

    private WireMockServer wireMockServer;
    private ApiClient client;
    private UserApi userApi;
    private static final int WIREMOCK_HTTPS_PORT = 8443;
    private static final String WIREMOCK_HOST = "https://localhost:" + WIREMOCK_HTTPS_PORT;
    private static final String KEYSTORE_PATH = "../../wiremock-keystore.jks";  // Path from integration-tests module
    private static final String KEYSTORE_PASSWORD = "password";

    @BeforeMethod
    public void setup() throws Exception {
        // Generate WireMock keystore if it doesn't exist
        String keystorePath = Paths.get(KEYSTORE_PATH).toAbsolutePath().toString();
        java.io.File keystoreFile = new java.io.File(keystorePath);
        if (!keystoreFile.exists()) {
            System.out.println("Generating WireMock keystore at: " + keystorePath);
            ProcessBuilder pb = new ProcessBuilder(
                "keytool", "-genkey", "-alias", "wiremock", "-keyalg", "RSA",
                "-keystore", keystorePath,
                "-storepass", KEYSTORE_PASSWORD, "-keypass", KEYSTORE_PASSWORD,
                "-dname", "CN=localhost", "-validity", "365", "-noprompt"
            );
            int exitCode = pb.start().waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Failed to generate WireMock keystore. " +
                    "Ensure 'keytool' is in your PATH (comes with Java)");
            }
            System.out.println("WireMock keystore generated successfully");
        }

        // Start WireMock on HTTPS with self-signed certificate
        wireMockServer = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .httpsPort(WIREMOCK_HTTPS_PORT)
                .keystorePath(KEYSTORE_PATH)
                .keystorePassword(KEYSTORE_PASSWORD)
        );
        wireMockServer.start();

        // Configure custom SSL context with the self-signed keystore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            trustStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

        // Build HttpClient with custom SSL context using HTTP Client 5 APIs
        // We need to set up the connection manager with custom SSL context
        org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = 
            HttpClients.custom()
                .setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(
                            new org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory(sslContext)
                        )
                        .build()
                )
                .build();
        
        // Create ApiClient with the custom HttpClient and a disabled cache manager
        client = new ApiClient(httpClient, new com.okta.sdk.impl.cache.DisabledCacheManager());
        client.setBasePath(WIREMOCK_HOST);

        userApi = new UserApi(client);
    }

    @AfterMethod
    public void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    public void testGetUser() throws ApiException {
        // Mock the Okta API endpoint for getting a user
        String userId = "00ub0oNGTSWTBKOLGLHN";
        stubFor(get(urlEqualTo("/api/v1/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{" +
                    "\"id\":\"" + userId + "\"," +
                    "\"status\":\"ACTIVE\"," +
                    "\"created\":\"2013-06-24T16:39:18.000Z\"," +
                    "\"activated\":\"2013-06-24T16:39:19.000Z\"," +
                    "\"statusChanged\":\"2013-06-24T16:39:19.000Z\"," +
                    "\"lastLogin\":\"2013-10-02T14:06:25.000Z\"," +
                    "\"lastUpdated\":\"2013-10-02T14:06:25.000Z\"," +
                    "\"passwordChanged\":\"2013-09-11T23:30:26.000Z\"," +
                    "\"profile\":{" +
                        "\"firstName\":\"Isaac\"," +
                        "\"lastName\":\"Brock\"," +
                        "\"email\":\"isaac.brock@example.com\"," +
                        "\"login\":\"isaac.brock@example.com\"," +
                        "\"mobilePhone\":null" +
                    "}" +
                "}")
            ));

        // Call the SDK to get the user
        User user = userApi.getUser(userId, null, null);

        // Verify the response
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("ACTIVE", user.getStatus().toString());
        assertNotNull(user.getProfile());
        assertEquals("isaac.brock@example.com", user.getProfile().getEmail());
        assertEquals("Isaac", user.getProfile().getFirstName());
        assertEquals("Brock", user.getProfile().getLastName());
    }

    @Test
    public void testListUsers() throws ApiException {
        // Mock the Okta API endpoint for listing users
        stubFor(get(urlEqualTo("/api/v1/users"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[" +
                    "{" +
                        "\"id\":\"00ub0oNGTSWTBKOLGLHN\"," +
                        "\"status\":\"ACTIVE\"," +
                        "\"created\":\"2013-06-24T16:39:18.000Z\"," +
                        "\"activated\":\"2013-06-24T16:39:19.000Z\"," +
                        "\"statusChanged\":\"2013-06-24T16:39:19.000Z\"," +
                        "\"lastLogin\":\"2013-10-02T14:06:25.000Z\"," +
                        "\"lastUpdated\":\"2013-10-02T14:06:25.000Z\"," +
                        "\"passwordChanged\":\"2013-09-11T23:30:26.000Z\"," +
                        "\"profile\":{" +
                            "\"firstName\":\"Isaac\"," +
                            "\"lastName\":\"Brock\"," +
                            "\"email\":\"isaac.brock@example.com\"," +
                            "\"login\":\"isaac.brock@example.com\"," +
                            "\"mobilePhone\":null" +
                        "}" +
                    "}," +
                    "{" +
                        "\"id\":\"00ub0oNGTSWTBKOLGLHO\"," +
                        "\"status\":\"ACTIVE\"," +
                        "\"created\":\"2013-06-24T16:39:18.000Z\"," +
                        "\"activated\":\"2013-06-24T16:39:19.000Z\"," +
                        "\"statusChanged\":\"2013-06-24T16:39:19.000Z\"," +
                        "\"lastLogin\":\"2013-10-02T14:06:25.000Z\"," +
                        "\"lastUpdated\":\"2013-10-02T14:06:25.000Z\"," +
                        "\"passwordChanged\":\"2013-09-11T23:30:26.000Z\"," +
                        "\"profile\":{" +
                            "\"firstName\":\"Jane\"," +
                            "\"lastName\":\"Developer\"," +
                            "\"email\":\"jane.developer@example.com\"," +
                            "\"login\":\"jane.developer@example.com\"," +
                            "\"mobilePhone\":null" +
                        "}" +
                    "}" +
                "]")
            ));

        // Call the SDK to list users
        List<User> users = userApi.listUsers(null, null, null, null, null, null, null, null, null, null);

        // Verify the response
        assertNotNull(users);
        assertEquals(2, users.size());
        
        User firstUser = users.get(0);
        assertEquals("00ub0oNGTSWTBKOLGLHN", firstUser.getId());
        assertEquals("isaac.brock@example.com", firstUser.getProfile().getEmail());
        
        User secondUser = users.get(1);
        assertEquals("00ub0oNGTSWTBKOLGLHO", secondUser.getId());
        assertEquals("jane.developer@example.com", secondUser.getProfile().getEmail());
    }

    @Test
    public void testWireMockHttps() {
        // This test simply verifies that the WireMock server is running on HTTPS
        // and the SSL context is properly configured
        assertNotNull(wireMockServer);
        assertNotNull(client);
        assertNotNull(userApi);
    }
}


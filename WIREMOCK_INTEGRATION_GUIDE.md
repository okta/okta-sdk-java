# WireMock + Okta Java SDK Integration Guide

## Overview
This guide shows how to configure the Okta Java SDK to work with WireMock for testing, including handling HTTPS with custom certificates.

## Problem Statement
1. Okta SDK requires HTTPS URLs (no HTTP support)
2. WireMock can be configured for HTTPS with a custom keystore
3. The SDK needs to trust the custom WireMock certificate

## Solution

### Step 1: Generate a Self-Signed Certificate for WireMock

```bash
keytool -genkey -alias wiremock -keyalg RSA -keystore wiremock-keystore.jks \
  -storepass password -keypass password -dname "CN=localhost" -validity 365
```

This creates `wiremock-keystore.jks` which is already in your project.

### Step 2: Configure WireMock to Use HTTPS

```java
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

WireMockServer wireMockServer = new WireMockServer(
    WireMockConfiguration.wireMockConfig()
        .httpsPort(8443)
        .keystorePath("wiremock-keystore.jks")
        .keystorePassword("password")
);
wireMockServer.start();
```

### Step 3: Configure Okta SDK to Trust the Custom Certificate

This is the **key solution** to your problem:

```java
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.AuthorizationMode;

// Load the WireMock keystore
KeyStore trustStore = KeyStore.getInstance("JKS");
try (FileInputStream fis = new FileInputStream("wiremock-keystore.jks")) {
    trustStore.load(fis, "password".toCharArray());
}

// Create TrustManagerFactory from the keystore
TrustManagerFactory tmf = TrustManagerFactory.getInstance(
    TrustManagerFactory.getDefaultAlgorithm());
tmf.init(trustStore);

// Create SSLContext with the custom trust manager
SSLContext sslContext = SSLContext.getInstance("TLS");
sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

// Configure the Okta SDK client with the custom SSL context
Client client = new ClientBuilder()
    .setOrgUrl("https://localhost:8443")
    .setAuthorizationMode(AuthorizationMode.SSWS)
    .setClientId("test-client-id")
    .setClientSecret("test-client-secret")
    .setHttpClientBuilder(httpClientBuilder -> {
        httpClientBuilder.setSSLContext(sslContext);
        return httpClientBuilder;
    })
    .build();
```

## Key Points

1. **Custom SSLContext**: The solution uses a custom `SSLContext` that trusts your WireMock keystore
2. **TrustManagerFactory**: Loads the custom keystore and creates trust managers
3. **HttpClientBuilder Integration**: The Okta SDK's `ClientBuilder` accepts a customized HTTP client

## Using the Test Class

The test class `WireMockOktaClientTest.java` demonstrates:

1. **Setup Phase**:
   - Starts WireMock on HTTPS port 8443
   - Loads the custom keystore
   - Configures the Okta SDK client with custom SSL context

2. **Test Phase**:
   - Stubs API endpoints using WireMock
   - Calls the Okta SDK methods
   - Verifies responses

3. **Example Tests**:
   - `testGetUser()` - Mocks a single user endpoint
   - `testListUsers()` - Mocks a list users endpoint

## Running the Tests

Once Maven dependencies are resolved:

```bash
mvn test -Dtest=WireMockOktaClientTest
```

## Troubleshooting

### Maven Certificate Issues
If you get SSL certificate errors from Maven (different from the WireMock setup):

```bash
# Option 1: Use HTTP for Maven repo (not recommended for production)
mvn -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true \
    test -Dtest=WireMockOktaClientTest

# Option 2: Import certificate to Java keystore
keytool -import -alias maven-repo -file /path/to/cert.pem \
  -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt
```

### WireMock Connection Issues

If you get SSL handshake errors when running tests:

1. Verify `wiremock-keystore.jks` exists
2. Check the password is correct (`password`)
3. Ensure the keystore path is absolute or relative to where you're running the test

## Benefits of This Approach

✅ **No rate limits** - Mock all the Okta endpoints
✅ **Full control** - Define exactly what responses you need for testing
✅ **Supports HTTPS** - Same as production Okta URLs
✅ **Custom certificates** - Can test certificate validation logic
✅ **Multiple scenarios** - Easy to set up different test data
✅ **Fast tests** - No network delays, instant responses

## Example Use Cases

### Test User Creation Flow
```java
@Test
public void testCreateUserFlow() {
    stubFor(post(urlEqualTo("/api/v1/users"))
        .willReturn(aResponse()
            .withStatus(201)
            .withBody("{\"id\":\"00u123\",\"status\":\"STAGED\"}")));
    
    User user = client.createUser(...);
    assertNotNull(user.getId());
}
```

### Test Error Handling
```java
@Test
public void testUserNotFound() {
    stubFor(get(urlEqualTo("/api/v1/users/invalid"))
        .willReturn(aResponse()
            .withStatus(404)
            .withBody("{\"errorCode\":\"E0000007\"}")));
    
    assertThrows(OktaAPIException.class, () -> {
        client.getUser("invalid");
    });
}
```

## Files in This Setup

- `wiremock-keystore.jks` - Self-signed certificate
- `integration-tests/src/test/java/com/okta/sdk/tests/WireMockOktaClientTest.java` - Full test class
- `integration-tests/pom.xml` - Updated with wiremock-jre8 dependency
- `StandaloneWireMockTest.java` - Standalone demo of SSL configuration

## Next Steps

1. Review `WireMockOktaClientTest.java` for the full working example
2. Run `StandaloneWireMockTest.java` to verify SSL configuration works
3. Adapt the test class to your specific API mocking needs
4. Integrate into your CI/CD pipeline for automated testing

## References

- [WireMock Documentation](https://wiremock.org/)
- [Okta Java SDK](https://github.com/okta/okta-sdk-java)
- [Java SSL/TLS Configuration](https://docs.oracle.com/javase/tutorial/security/jsse/)

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
package com.okta.sdk.impl.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class DPoPInterceptorTest {

    @Mock
    private ClassicHttpRequest mockRequest;

    @Mock
    private ExecChain mockExecChain;

    @Captor
    private ArgumentCaptor<ClassicHttpRequest> requestCaptor;

    private DPoPInterceptor interceptor;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interceptor = new DPoPInterceptor();

        when(mockRequest.getRequestUri()).thenReturn(AccessTokenRetrieverServiceImpl.TOKEN_URI);
        when(mockRequest.getUri()).thenReturn(new URI(AccessTokenRetrieverServiceImpl.TOKEN_URI));
        when(mockRequest.getMethod()).thenReturn("POST");

        // Allow header modifications for all tests
        doNothing().when(mockRequest).addHeader(anyString(), anyString());
        doNothing().when(mockRequest).setHeader(anyString(), anyString());
    }

    @Test
    public void testInitialState_noDPoP() throws Exception {
        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        ClassicHttpResponse response = interceptor.execute(mockRequest, null, mockExecChain);

        verify(mockExecChain).proceed(eq(mockRequest), any());
        verify(mockRequest, never()).addHeader(eq("DPoP"), anyString());
        assertEquals(response.getCode(), 200);
    }

    @Test
    public void testFirstInvalidDPoPProof() throws Exception {
        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "invalid_dpop_proof");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            assertNotNull(e);
        }

        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);
        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            assertNotNull(e);
        }

        verify(mockRequest, times(1)).addHeader(eq("DPoP"), anyString());
    }

    @Test
    public void testDPoPNonceHandshake() throws Exception {
        initializeJwk();

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "use_dpop_nonce");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        mockResponse.addHeader(new BasicHeader("dpop-nonce", "test-nonce-123"));
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            assertNotNull(e);
        }

        Field nonceField = DPoPInterceptor.class.getDeclaredField("nonce");
        nonceField.setAccessible(true);
        assertEquals(nonceField.get(interceptor), "test-nonce-123");
    }

    @Test
    public void testSuccessfulRequestAfterHandshake() throws Exception {
        initializeJwkAndNonce("test-nonce-123");

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        ClassicHttpResponse response = interceptor.execute(mockRequest, null, mockExecChain);
        assertEquals(response.getCode(), 200);
    }

    @Test
    public void testRequestWithAuthorizationHeader() throws Exception {
        initializeJwkAndNonce("test-nonce-123");

        when(mockRequest.getFirstHeader("Authorization"))
            .thenReturn(new BasicHeader("Authorization", "Bearer test-token"));

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        interceptor.execute(mockRequest, null, mockExecChain);
    }

    @Test
    public void testNonceExpiration() throws Exception {
        initializeJwkAndExpiredNonce();

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        interceptor.execute(mockRequest, null, mockExecChain);

        Field nonceField = DPoPInterceptor.class.getDeclaredField("nonce");
        nonceField.setAccessible(true);
        assertNull(nonceField.get(interceptor));
    }

    @Test
    public void testNonTokenRequest() throws Exception {
        initializeJwkAndNonce("test-nonce-123");

        when(mockRequest.getRequestUri()).thenReturn("/api/v1/users");
        when(mockRequest.getUri()).thenReturn(new URI("/api/v1/users"));

        // Add Authorization header to trigger DPoP processing
        when(mockRequest.getFirstHeader("Authorization"))
            .thenReturn(new BasicHeader("Authorization", "Bearer test-token"));

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        interceptor.execute(mockRequest, null, mockExecChain);
    }

    // Helper methods
    private void initializeJwk() throws Exception {
        Field jwkField = DPoPInterceptor.class.getDeclaredField("jwk");
        jwkField.setAccessible(true);
        jwkField.set(interceptor, Jwks.builder()
            .keyPair(Jwts.SIG.ES256.keyPair().build())
            .build());
    }

    private void initializeJwkAndNonce(String nonceValue) throws Exception {
        initializeJwk();

        Field nonceField = DPoPInterceptor.class.getDeclaredField("nonce");
        nonceField.setAccessible(true);
        nonceField.set(interceptor, nonceValue);

        Field nonceValidField = DPoPInterceptor.class.getDeclaredField("nonceValidUntil");
        nonceValidField.setAccessible(true);
        nonceValidField.set(interceptor, Instant.now().plusSeconds(3600));
    }

    private void initializeJwkAndExpiredNonce() throws Exception {
        initializeJwk();

        Field nonceField = DPoPInterceptor.class.getDeclaredField("nonce");
        nonceField.setAccessible(true);
        nonceField.set(interceptor, "expired-nonce");

        Field nonceValidField = DPoPInterceptor.class.getDeclaredField("nonceValidUntil");
        nonceValidField.setAccessible(true);
        nonceValidField.set(interceptor, Instant.now().minusSeconds(3600));
    }

    @Test
    public void testConstructorAndStaticInitializer() throws Exception {
        // Create a new instance to cover constructor
        DPoPInterceptor newInterceptor = new DPoPInterceptor();
        assertNotNull(newInterceptor);

        // Test static initializer by accessing ThreadLocal SHA256
        Field sha256Field = DPoPInterceptor.class.getDeclaredField("SHA256");
        sha256Field.setAccessible(true);
        ThreadLocal<?> sha256 = (ThreadLocal<?>) sha256Field.get(null);
        assertNotNull(sha256.get());
    }

    @Test
    public void testRepeatedInvalidDPoPProof() throws Exception {
        // Set up jwk first to trigger REPEATED_INVALID_DPOP_PROOF branch
        initializeJwk();

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "invalid_dpop_proof");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            // Verify exception is thrown - can't check state directly
            assertNotNull(e);
            assertTrue(e.getMessage().contains("invalid_dpop_proof"));
        }
    }

    @Test
    public void testRepeatedUseDPoPNonce() throws Exception {
        initializeJwkAndNonce("existing-nonce");

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "use_dpop_nonce");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        mockResponse.addHeader(new BasicHeader("dpop-nonce", "new-nonce"));
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            // Verify exception is thrown with correct error message
            assertNotNull(e);
            assertTrue(e.getMessage().contains("use_dpop_nonce"));
        }
    }

    @Test
    public void testMissingDPoPNonceHeader() throws Exception {
        initializeJwk();

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "use_dpop_nonce");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        // Deliberately NOT adding dpop-nonce header
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            // Verify exception is thrown with correct error message
            assertNotNull(e);
            assertTrue(e.getMessage().contains("use_dpop_nonce"));
        }
    }

    @Test
    public void testUnexpectedErrorState() throws Exception {
        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", "unknown_error_type");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            // Verify exception is thrown with correct error message
            assertNotNull(e);
            assertTrue(e.getMessage().contains("unknown_error_type"));
        }
    }

    @Test
    public void testNonTextualErrorField() throws Exception {
        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(400);
        ObjectNode errorJson = objectMapper.createObjectNode();
        // Create a non-textual error field
        errorJson.putObject("error");
        StringEntity entity = new StringEntity(errorJson.toString());
        mockResponse.setEntity(entity);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        try {
            interceptor.execute(mockRequest, null, mockExecChain);
            fail("Expected DPoPHandshakeException");
        } catch (DPoPHandshakeException e) {
            // Just verify exception is thrown
            assertNotNull(e);
        }
    }

    @Test
    public void testGetUriWithoutQueryString() throws Exception {
        initializeJwk();

        // Use properly encoded URI for testing
        URI uriWithQuery = new URI("https://example.com/path%20with%20spaces?query=value#fragment");
        when(mockRequest.getRequestUri()).thenReturn(uriWithQuery.toString());
        when(mockRequest.getUri()).thenReturn(uriWithQuery);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getFirstHeader("Authorization"))
            .thenReturn(new BasicHeader("Authorization", "Bearer token"));

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        interceptor.execute(mockRequest, null, mockExecChain);
        // Verify header was added, indicating URI processing worked
        verify(mockRequest).addHeader(eq("DPoP"), anyString());
    }

    @Test
    public void testGetUriWithSpecialCharacters() throws Exception {
        initializeJwk();

        // Test URI with encoded characters that should be properly handled
        URI uriWithSpecials = new URI("https://example.com/path/with%22quotes%23hash");
        when(mockRequest.getRequestUri()).thenReturn(uriWithSpecials.toString());
        when(mockRequest.getUri()).thenReturn(uriWithSpecials);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getFirstHeader("Authorization"))
            .thenReturn(new BasicHeader("Authorization", "Bearer token"));

        ClassicHttpResponse mockResponse = new BasicClassicHttpResponse(200);
        when(mockExecChain.proceed(any(), any())).thenReturn(mockResponse);

        interceptor.execute(mockRequest, null, mockExecChain);
        verify(mockRequest).addHeader(eq("DPoP"), anyString());
    }



}

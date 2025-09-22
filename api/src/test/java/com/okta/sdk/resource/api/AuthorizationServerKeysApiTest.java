package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AuthorizationServerJsonWebKey;
import com.okta.sdk.resource.model.JwkUse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerKeysApiTest {

    private ApiClient apiClient;
    private AuthorizationServerKeysApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new AuthorizationServerKeysApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    @Test
    public void testGetAuthorizationServerKey_Success() throws Exception {
        AuthorizationServerJsonWebKey expected = new AuthorizationServerJsonWebKey();
        stubInvoke(expected);

        AuthorizationServerJsonWebKey actual = api.getAuthorizationServerKey("as1", "k1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/credentials/keys/k1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        verify(apiClient).escapeString("as1");
        verify(apiClient).escapeString("k1");
    }

    @Test
    public void testGetAuthorizationServerKey_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerJsonWebKey());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v");
        api.getAuthorizationServerKey("as2","k2", headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-Test"));
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerKey_MissingAuthServerId() throws Exception {
        api.getAuthorizationServerKey(null, "k");
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerKey_MissingKeyId() throws Exception {
        api.getAuthorizationServerKey("as", null);
    }

    @Test
    public void testListAuthorizationServerKeys_Success() throws Exception {
        List<AuthorizationServerJsonWebKey> expected =
            Arrays.asList(new AuthorizationServerJsonWebKey(), new AuthorizationServerJsonWebKey());
        stubInvoke(expected);

        List<AuthorizationServerJsonWebKey> actual = api.listAuthorizationServerKeys("as3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3/credentials/keys"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("as3");
    }

    @Test
    public void testListAuthorizationServerKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAuthorizationServerKeys("as4", Collections.singletonMap("X-H","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-H"));
    }

    @Test(expected = ApiException.class)
    public void testListAuthorizationServerKeys_MissingAuthServerId() throws Exception {
        api.listAuthorizationServerKeys(null);
    }

    @Test
    public void testRotateAuthorizationServerKeys_Success() throws Exception {
        List<AuthorizationServerJsonWebKey> expected =
            Arrays.asList(new AuthorizationServerJsonWebKey());
        stubInvoke(expected);

        // If JwkUse is an enum with a valid constant replace new JwkUse() accordingly.
        JwkUse use = new JwkUse();
        List<AuthorizationServerJsonWebKey> actual =
            api.rotateAuthorizationServerKeys("as5", use);
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as5/credentials/lifecycle/keyRotate"));
        assertSame(use, bodyCap.getValue());
        verify(apiClient).escapeString("as5");
    }

    @Test
    public void testRotateAuthorizationServerKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Rotate","yes");
        api.rotateAuthorizationServerKeys("as6", new JwkUse(), headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("yes", hdrCap.getValue().get("X-Rotate"));
    }

    @Test(expected = ApiException.class)
    public void testRotateAuthorizationServerKeys_MissingAuthServerId() throws Exception {
        api.rotateAuthorizationServerKeys(null, new JwkUse());
    }

    @Test(expected = ApiException.class)
    public void testRotateAuthorizationServerKeys_MissingUse() throws Exception {
        api.rotateAuthorizationServerKeys("as7", null);
    }

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }
}

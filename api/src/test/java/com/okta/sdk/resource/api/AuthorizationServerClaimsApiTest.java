package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OAuth2Claim;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerClaimsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthorizationServerClaimsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthorizationServerClaimsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // createOAuth2Claim
    @Test
    public void testCreateOAuth2Claim_Success() throws Exception {
        OAuth2Claim body = new OAuth2Claim();
        OAuth2Claim expected = new OAuth2Claim();
        stubInvoke(expected);

        OAuth2Claim actual = api.createOAuth2Claim("as1", body);
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
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("POST", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as1/claims"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as1");
    }

    @Test
    public void testCreateOAuth2Claim_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Claim());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v");
        api.createOAuth2Claim("as2", new OAuth2Claim(), headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", hdrCap.getValue().get("X-Test"));
    }

    @Test(expected = ApiException.class)
    public void testCreateOAuth2Claim_MissingAuthServerId() throws Exception {
        api.createOAuth2Claim(null, new OAuth2Claim());
    }

    @Test(expected = ApiException.class)
    public void testCreateOAuth2Claim_MissingBody() throws Exception {
        api.createOAuth2Claim("asX", null);
    }

    // deleteOAuth2Claim
    @Test
    public void testDeleteOAuth2Claim_Success() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2Claim("as3", "c1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3/claims/c1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        verify(apiClient).escapeString("as3");
        verify(apiClient).escapeString("c1");
    }

    @Test
    public void testDeleteOAuth2Claim_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2Claim("as4", "c2", Collections.singletonMap("X-Req","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", hdrCap.getValue().get("X-Req"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2Claim_MissingAuthServerId() throws Exception {
        api.deleteOAuth2Claim(null, "c");
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2Claim_MissingClaimId() throws Exception {
        api.deleteOAuth2Claim("as", null);
    }

    // getOAuth2Claim
    @Test
    public void testGetOAuth2Claim_Success() throws Exception {
        OAuth2Claim expected = new OAuth2Claim();
        stubInvoke(expected);

        OAuth2Claim actual = api.getOAuth2Claim("as5", "c5");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as5/claims/c5"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).escapeString("as5");
        verify(apiClient).escapeString("c5");
    }

    @Test
    public void testGetOAuth2Claim_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Claim());
        api.getOAuth2Claim("as6", "c6", Collections.singletonMap("X-H","Y"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("Y", hdrCap.getValue().get("X-H"));
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2Claim_MissingAuthServerId() throws Exception {
        api.getOAuth2Claim(null, "c");
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2Claim_MissingClaimId() throws Exception {
        api.getOAuth2Claim("as", null);
    }

    // listOAuth2Claims
    @Test
    public void testListOAuth2Claims_Success() throws Exception {
        List<OAuth2Claim> expected = Arrays.asList(new OAuth2Claim(), new OAuth2Claim());
        stubInvoke(expected);

        List<OAuth2Claim> actual = api.listOAuth2Claims("as7");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as7/claims"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).escapeString("as7");
    }

    @Test
    public void testListOAuth2Claims_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listOAuth2Claims("as8", Collections.singletonMap("X-List","L"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("L", hdrCap.getValue().get("X-List"));
    }

    @Test(expected = ApiException.class)
    public void testListOAuth2Claims_MissingAuthServerId() throws Exception {
        api.listOAuth2Claims(null);
    }

    // replaceOAuth2Claim
    @Test
    public void testReplaceOAuth2Claim_Success() throws Exception {
        OAuth2Claim body = new OAuth2Claim();
        OAuth2Claim expected = new OAuth2Claim();
        stubInvoke(expected);

        OAuth2Claim actual = api.replaceOAuth2Claim("as9", "c9", body);
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
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("PUT", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as9/claims/c9"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as9");
        verify(apiClient).escapeString("c9");
    }

    @Test
    public void testReplaceOAuth2Claim_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Claim());
        api.replaceOAuth2Claim("as10", "c10", new OAuth2Claim(), Collections.singletonMap("X-R","Z"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("Z", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Claim_MissingAuthServerId() throws Exception {
        api.replaceOAuth2Claim(null, "c", new OAuth2Claim());
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Claim_MissingClaimId() throws Exception {
        api.replaceOAuth2Claim("as", null, new OAuth2Claim());
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Claim_MissingBody() throws Exception {
        api.replaceOAuth2Claim("as", "c", null);
    }

    // Helpers
    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }
}

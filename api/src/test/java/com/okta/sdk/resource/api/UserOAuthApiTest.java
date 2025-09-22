package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OAuth2RefreshToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserOAuthApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.UserOAuthApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.UserOAuthApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object val = inv.getArgument(1);
            if (val == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(val)));
        });
    }

    // getRefreshTokenForUserAndClient
    @Test
    public void testGetRefreshTokenForUserAndClient_Success() throws Exception {
        OAuth2RefreshToken expected = new OAuth2RefreshToken();
        stubInvoke(expected);

        OAuth2RefreshToken actual = api.getRefreshTokenForUserAndClient("u1","c1","t1","scope");
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            qCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("GET", method.getValue());
        assertEquals("/api/v1/users/u1/clients/c1/tokens/t1", path.getValue());
        assertTrue(qCap.getValue().stream().anyMatch(p -> ((Pair)p).getName().equals("expand") && "scope".equals(((Pair)p).getValue())));
        verify(apiClient).escapeString("u1");
        verify(apiClient).escapeString("c1");
        verify(apiClient).escapeString("t1");
    }

    @Test
    public void testGetRefreshTokenForUserAndClient_WithHeaders() throws Exception {
        stubInvoke(new OAuth2RefreshToken());
        api.getRefreshTokenForUserAndClient("u2","c2","t2", null, Collections.singletonMap("X-H","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headerCap.getValue().get("X-H"));
    }

    @Test(expected = ApiException.class)
    public void testGetRefreshTokenForUserAndClient_MissingUserId() throws Exception {
        api.getRefreshTokenForUserAndClient(null,"c","t", null);
    }

    @Test(expected = ApiException.class)
    public void testGetRefreshTokenForUserAndClient_MissingClientId() throws Exception {
        api.getRefreshTokenForUserAndClient("u",null,"t", null);
    }

    @Test(expected = ApiException.class)
    public void testGetRefreshTokenForUserAndClient_MissingTokenId() throws Exception {
        api.getRefreshTokenForUserAndClient("u","c",null, null);
    }

    // listRefreshTokensForUserAndClient
    @Test
    public void testListRefreshTokensForUserAndClient_Success() throws Exception {
        List<OAuth2RefreshToken> expected = Arrays.asList(new OAuth2RefreshToken(), new OAuth2RefreshToken());
        stubInvoke(expected);

        List<OAuth2RefreshToken> actual = api.listRefreshTokensForUserAndClient("u3","c3","scope","afterCursor",50);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            qCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("GET", method.getValue());
        assertEquals("/api/v1/users/u3/clients/c3/tokens", path.getValue());
        List<Pair> qp = qCap.getValue();
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("expand") && p.getValue().equals("scope")));
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("after") && p.getValue().equals("afterCursor")));
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("limit") && p.getValue().equals("50")));
        verify(apiClient).escapeString("u3");
        verify(apiClient).escapeString("c3");
    }

    @Test
    public void testListRefreshTokensForUserAndClient_NullOptionals() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listRefreshTokensForUserAndClient("u4","c4", null, null, null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            qCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertTrue(qCap.getValue().isEmpty());
    }

    @Test
    public void testListRefreshTokensForUserAndClient_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listRefreshTokensForUserAndClient("u5","c5", null, null, null, Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headerCap.getValue().get("X-L"));
    }

    @Test(expected = ApiException.class)
    public void testListRefreshTokensForUserAndClient_MissingUserId() throws Exception {
        api.listRefreshTokensForUserAndClient(null,"c","e", null, null);
    }

    @Test(expected = ApiException.class)
    public void testListRefreshTokensForUserAndClient_MissingClientId() throws Exception {
        api.listRefreshTokensForUserAndClient("u",null,"e", null, null);
    }

    // revokeTokenForUserAndClient
    @Test
    public void testRevokeTokenForUserAndClient_Success() throws Exception {
        stubVoidInvoke();
        api.revokeTokenForUserAndClient("u6","c6","t6");

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u6/clients/c6/tokens/t6"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("u6");
        verify(apiClient).escapeString("c6");
        verify(apiClient).escapeString("t6");
    }

    @Test
    public void testRevokeTokenForUserAndClient_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeTokenForUserAndClient("u7","c7","t7", Collections.singletonMap("X-R","h"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("h", headerCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeTokenForUserAndClient_MissingUserId() throws Exception {
        api.revokeTokenForUserAndClient(null,"c","t");
    }

    @Test(expected = ApiException.class)
    public void testRevokeTokenForUserAndClient_MissingClientId() throws Exception {
        api.revokeTokenForUserAndClient("u",null,"t");
    }

    @Test(expected = ApiException.class)
    public void testRevokeTokenForUserAndClient_MissingTokenId() throws Exception {
        api.revokeTokenForUserAndClient("u","c",null);
    }

    // revokeTokensForUserAndClient
    @Test
    public void testRevokeTokensForUserAndClient_Success() throws Exception {
        stubVoidInvoke();
        api.revokeTokensForUserAndClient("u8","c8");

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/u8/clients/c8/tokens"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("u8");
        verify(apiClient).escapeString("c8");
    }

    @Test
    public void testRevokeTokensForUserAndClient_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeTokensForUserAndClient("u9","c9", Collections.singletonMap("X-A","z"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("z", headerCap.getValue().get("X-A"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeTokensForUserAndClient_MissingUserId() throws Exception {
        api.revokeTokensForUserAndClient(null,"c");
    }

    @Test(expected = ApiException.class)
    public void testRevokeTokensForUserAndClient_MissingClientId() throws Exception {
        api.revokeTokensForUserAndClient("u",null);
    }

    // Helpers
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

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }
}

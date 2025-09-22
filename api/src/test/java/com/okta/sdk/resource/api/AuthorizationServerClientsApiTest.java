package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OAuth2Client;
import com.okta.sdk.resource.model.OAuth2RefreshToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerClientsApiTest {

    private ApiClient apiClient;
    private AuthorizationServerClientsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new AuthorizationServerClientsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), isNull())).thenReturn(Collections.emptyList());
        when(apiClient.parameterToPair(anyString(), argThat(o -> o != null))).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object value = inv.getArgument(1);
            return Collections.singletonList(new Pair(name, String.valueOf(value)));
        });
    }

    // getRefreshTokenForAuthorizationServerAndClient
    @Test
    public void testGetRefreshToken_Success() throws Exception {
        OAuth2RefreshToken expected = new OAuth2RefreshToken();
        stubInvoke(expected);

        OAuth2RefreshToken actual =
            api.getRefreshTokenForAuthorizationServerAndClient("as1","client1","tok1","scope");
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/clients/client1/tokens/tok1"), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("scope", qp.get("expand"));

        verify(apiClient).escapeString("as1");
        verify(apiClient).escapeString("client1");
        verify(apiClient).escapeString("tok1");
    }

    @Test
    public void testGetRefreshToken_WithHeaders() throws Exception {
        stubInvoke(new OAuth2RefreshToken());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v");

        api.getRefreshTokenForAuthorizationServerAndClient("as2","client2","tok2",null, headers);

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
    public void testGetRefreshToken_MissingAuthServerId() throws Exception {
        api.getRefreshTokenForAuthorizationServerAndClient(null,"c","t",null);
    }

    @Test(expected = ApiException.class)
    public void testGetRefreshToken_MissingClientId() throws Exception {
        api.getRefreshTokenForAuthorizationServerAndClient("as",null,"t",null);
    }

    @Test(expected = ApiException.class)
    public void testGetRefreshToken_MissingTokenId() throws Exception {
        api.getRefreshTokenForAuthorizationServerAndClient("as","c",null,null);
    }

    // listOAuth2ClientsForAuthorizationServer
    @Test
    public void testListOAuth2Clients_Success() throws Exception {
        List<OAuth2Client> expected = Arrays.asList(new OAuth2Client(), new OAuth2Client());
        stubInvoke(expected);

        List<OAuth2Client> actual = api.listOAuth2ClientsForAuthorizationServer("as3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3/clients"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("as3");
    }

    @Test
    public void testListOAuth2Clients_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listOAuth2ClientsForAuthorizationServer("as4", Collections.singletonMap("X-H","1"));

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
    public void testListOAuth2Clients_MissingAuthServerId() throws Exception {
        api.listOAuth2ClientsForAuthorizationServer(null);
    }

    // listRefreshTokensForAuthorizationServerAndClient
    @Test
    public void testListRefreshTokens_AllParams() throws Exception {
        List<OAuth2RefreshToken> expected = Arrays.asList(new OAuth2RefreshToken(), new OAuth2RefreshToken());
        stubInvoke(expected);

        List<OAuth2RefreshToken> actual =
            api.listRefreshTokensForAuthorizationServerAndClient("as5","client5","scope","afterCursor",100);
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as5/clients/client5/tokens"), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("scope", qp.get("expand"));
        assertEquals("afterCursor", qp.get("after"));
        assertEquals("100", qp.get("limit"));
    }

    @Test
    public void testListRefreshTokens_NullOptionals() throws Exception {
        stubInvoke(Collections.emptyList());

        api.listRefreshTokensForAuthorizationServerAndClient("as6","client6",null,null,null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertTrue(qpCap.getValue().isEmpty());
    }

    @Test(expected = ApiException.class)
    public void testListRefreshTokens_MissingAuthServerId() throws Exception {
        api.listRefreshTokensForAuthorizationServerAndClient(null,"c",null,null,null);
    }

    @Test(expected = ApiException.class)
    public void testListRefreshTokens_MissingClientId() throws Exception {
        api.listRefreshTokensForAuthorizationServerAndClient("as",null,null,null,null);
    }

    // revokeRefreshTokenForAuthorizationServerAndClient
    @Test
    public void testRevokeRefreshToken_Success() throws Exception {
        stubVoidInvoke();
        api.revokeRefreshTokenForAuthorizationServerAndClient("as7","client7","tok7");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as7/clients/client7/tokens/tok7"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testRevokeRefreshToken_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeRefreshTokenForAuthorizationServerAndClient("as8","client8","tok8",
            Collections.singletonMap("X-Del","D"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("D", hdrCap.getValue().get("X-Del"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeRefreshToken_MissingAuthServerId() throws Exception {
        api.revokeRefreshTokenForAuthorizationServerAndClient(null,"c","t");
    }

    @Test(expected = ApiException.class)
    public void testRevokeRefreshToken_MissingClientId() throws Exception {
        api.revokeRefreshTokenForAuthorizationServerAndClient("as",null,"t");
    }

    @Test(expected = ApiException.class)
    public void testRevokeRefreshToken_MissingTokenId() throws Exception {
        api.revokeRefreshTokenForAuthorizationServerAndClient("as","c",null);
    }

    // revokeRefreshTokensForAuthorizationServerAndClient
    @Test
    public void testRevokeAllRefreshTokens_Success() throws Exception {
        stubVoidInvoke();
        api.revokeRefreshTokensForAuthorizationServerAndClient("as9","client9");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as9/clients/client9/tokens"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testRevokeAllRefreshTokens_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeRefreshTokensForAuthorizationServerAndClient("as10","client10",
            Collections.singletonMap("X-All","A"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("A", hdrCap.getValue().get("X-All"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeAllRefreshTokens_MissingAuthServerId() throws Exception {
        api.revokeRefreshTokensForAuthorizationServerAndClient(null,"c");
    }

    @Test(expected = ApiException.class)
    public void testRevokeAllRefreshTokens_MissingClientId() throws Exception {
        api.revokeRefreshTokensForAuthorizationServerAndClient("as",null);
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
            any(String[].class), any())
        ).thenReturn(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String,String> flattenPairs(List<?> pairs) {
        if (pairs == null) return Collections.emptyMap();
        Map<String,String> m = new HashMap<>();
        for (Object o : pairs) {
            if (o instanceof Pair) {
                Pair p = (Pair) o;
                m.put(p.getName(), p.getValue());
            }
        }
        return m;
    }
}

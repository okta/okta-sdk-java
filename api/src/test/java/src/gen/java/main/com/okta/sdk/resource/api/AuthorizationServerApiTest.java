package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AuthorizationServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthorizationServerApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthorizationServerApi(apiClient);

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

    // activateAuthorizationServer
    @Test
    public void testActivateAuthorizationServer_Success() throws Exception {
        stubVoidInvoke();
        api.activateAuthorizationServer("as1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("as1");
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServer_MissingId() throws Exception {
        api.activateAuthorizationServer(null);
    }

    // createAuthorizationServer
    @Test
    public void testCreateAuthorizationServer_Success() throws Exception {
        AuthorizationServer body = new AuthorizationServer();
        AuthorizationServer expected = new AuthorizationServer();
        stubInvoke(expected);

        AuthorizationServer actual = api.createAuthorizationServer(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServer_MissingBody() throws Exception {
        api.createAuthorizationServer(null);
    }

    // deactivateAuthorizationServer
    @Test
    public void testDeactivateAuthorizationServer_Success() throws Exception {
        stubVoidInvoke();
        api.deactivateAuthorizationServer("as2");
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("as2");
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServer_MissingId() throws Exception {
        api.deactivateAuthorizationServer(null);
    }

    // deleteAuthorizationServer
    @Test
    public void testDeleteAuthorizationServer_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAuthorizationServer("as3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("as3");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServer_MissingId() throws Exception {
        api.deleteAuthorizationServer(null);
    }

    // getAuthorizationServer
    @Test
    public void testGetAuthorizationServer_Success() throws Exception {
        AuthorizationServer expected = new AuthorizationServer();
        stubInvoke(expected);
        AuthorizationServer actual = api.getAuthorizationServer("as4");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as4"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("as4");
    }

    @Test
    public void testGetAuthorizationServer_WithHeaders() throws Exception {
        AuthorizationServer expected = new AuthorizationServer();
        stubInvoke(expected);
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Custom","v1");
        api.getAuthorizationServer("as5", headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v1", hdrCap.getValue().get("X-Custom"));
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServer_MissingId() throws Exception {
        api.getAuthorizationServer(null);
    }

    // listAuthorizationServers
    @Test
    public void testListAuthorizationServers_AllParams() throws Exception {
        List<AuthorizationServer> expected = Arrays.asList(new AuthorizationServer(), new AuthorizationServer());
        stubInvoke(expected);

        List<AuthorizationServer> actual = api.listAuthorizationServers("search", 123, "cursor1");
        assertSame(expected, actual);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers"), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("search", qp.get("q"));
        assertEquals("123", qp.get("limit"));
        assertEquals("cursor1", qp.get("after"));
    }

    @Test
    public void testListAuthorizationServers_NullParams() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAuthorizationServers(null, null, null);

        @SuppressWarnings("unchecked")
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

    // replaceAuthorizationServer
    @Test
    public void testReplaceAuthorizationServer_Success() throws Exception {
        AuthorizationServer body = new AuthorizationServer();
        AuthorizationServer expected = new AuthorizationServer();
        stubInvoke(expected);

        AuthorizationServer actual = api.replaceAuthorizationServer("as6", body);
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
        assertEquals("PUT", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as6"));
        assertSame(body, bodyCap.getValue());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServer_MissingId() throws Exception {
        api.replaceAuthorizationServer(null, new AuthorizationServer());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServer_MissingBody() throws Exception {
        api.replaceAuthorizationServer("as7", null);
    }

    // Helpers
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

    @SuppressWarnings("unchecked")
    private Map<String,String> flattenPairs(List<Pair> pairs) {
        if (pairs == null) return Collections.emptyMap();
        Map<String,String> m = new HashMap<>();
        for (Pair p : pairs) {
            if (p != null) m.put(p.getName(), p.getValue());
        }
        return m;
    }
}

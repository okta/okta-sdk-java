package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AssociatedServerMediated;
import com.okta.sdk.resource.model.AuthorizationServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerAssocApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthorizationServerAssocApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthorizationServerAssocApi(apiClient);

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

    // createAssociatedServers
    @Test
    public void testCreateAssociatedServers_Success() throws Exception {
        List<AuthorizationServer> expected = Arrays.asList(new AuthorizationServer(), new AuthorizationServer());
        stubInvoke(expected);

        AssociatedServerMediated body = new AssociatedServerMediated();
        List<AuthorizationServer> actual = api.createAssociatedServers("as1", body);
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
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as1/associatedServers"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as1");
    }

    @Test
    public void testCreateAssociatedServers_WithAdditionalHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v");
        api.createAssociatedServers("as2", new AssociatedServerMediated(), headers);

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
    public void testCreateAssociatedServers_MissingAuthServerId() throws Exception {
        api.createAssociatedServers(null, new AssociatedServerMediated());
    }

    @Test(expected = ApiException.class)
    public void testCreateAssociatedServers_MissingBody() throws Exception {
        api.createAssociatedServers("as3", null);
    }

    // deleteAssociatedServer
    @Test
    public void testDeleteAssociatedServer_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAssociatedServer("as4", "assoc1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as4/associatedServers/assoc1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        verify(apiClient).escapeString("as4");
        verify(apiClient).escapeString("assoc1");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAssociatedServer_MissingAuthServerId() throws Exception {
        api.deleteAssociatedServer(null, "x");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAssociatedServer_MissingAssociatedId() throws Exception {
        api.deleteAssociatedServer("as5", null);
    }

    // listAssociatedServersByTrustedType
    @Test
    public void testListAssociatedServersByTrustedType_AllParams() throws Exception {
        List<AuthorizationServer> expected = Arrays.asList(new AuthorizationServer(), new AuthorizationServer());
        stubInvoke(expected);

        List<AuthorizationServer> actual =
            api.listAssociatedServersByTrustedType("as6", true, "search", 50, "cursor");
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as6/associatedServers"), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("true", qp.get("trusted"));
        assertEquals("search", qp.get("q"));
        assertEquals("50", qp.get("limit"));
        assertEquals("cursor", qp.get("after"));
        verify(apiClient).escapeString("as6");
    }

    @Test
    public void testListAssociatedServersByTrustedType_NullOptionals() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAssociatedServersByTrustedType("as7", null, null, null, null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertTrue(qpCap.getValue().isEmpty());
    }

    @Test
    public void testListAssociatedServersByTrustedType_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Extra","1");
        api.listAssociatedServersByTrustedType("as8", false, "qv", 10, "next", headers);

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
        assertEquals("1", hdrCap.getValue().get("X-Extra"));
    }

    @Test(expected = ApiException.class)
    public void testListAssociatedServersByTrustedType_MissingAuthServerId() throws Exception {
        api.listAssociatedServersByTrustedType(null, null, null, null, null);
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
            any(String[].class), any()
        )).thenReturn(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String,String> flattenPairs(List<?> pairs) {
        Map<String,String> m = new HashMap<>();
        if (pairs == null) return m;
        for (Object o : pairs) {
            if (o instanceof Pair) {
                Pair p = (Pair) o;
                m.put(p.getName(), p.getValue());
            }
        }
        return m;
    }
}

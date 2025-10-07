package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.KeepCurrent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserSessionsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.UserSessionsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.UserSessionsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        // Stub query param helpers
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(v)));
        });

        // Generic invokeAPI stub for void endpoints
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }

    // endUserSessions
    @Test
    public void testEndUserSessions_Success() throws Exception {
        KeepCurrent body = new KeepCurrent();
        api.endUserSessions(body);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("/api/v1/users/me/lifecycle/delete_sessions", path.getValue());
        assertEquals("POST", method.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testEndUserSessions_NullBodyAllowed() throws Exception {
        api.endUserSessions((KeepCurrent) null);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertNull(bodyCap.getValue());
    }

    @Test
    public void testEndUserSessions_WithHeaders() throws Exception {
        Map<String,String> hdrs = Collections.singletonMap("X-End","v");
        api.endUserSessions(new KeepCurrent(), hdrs);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", headerCap.getValue().get("X-End"));
    }

    // revokeUserSessions
    @Test
    public void testRevokeUserSessions_Success() throws Exception {
        api.revokeUserSessions("u123", true, false);

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
            any(String[].class), isNull());
        assertEquals("DELETE", method.getValue());
        assertEquals("/api/v1/users/u123/sessions", path.getValue());
        @SuppressWarnings("unchecked")
        List<Pair> qp = qCap.getValue();
        assertTrue(qp.stream().anyMatch(p -> "oauthTokens".equals(p.getName()) && "true".equals(p.getValue())));
        assertTrue(qp.stream().anyMatch(p -> "forgetDevices".equals(p.getName()) && "false".equals(p.getValue())));
        verify(apiClient).escapeString("u123");
    }

    @Test
    public void testRevokeUserSessions_NullOptionals() throws Exception {
        api.revokeUserSessions("uNull", null, null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            qCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        @SuppressWarnings("unchecked")
        List<Pair> qp = qCap.getValue();
        assertTrue(qp.isEmpty()); // no params when both null
    }

    @Test
    public void testRevokeUserSessions_WithHeaders() throws Exception {
        api.revokeUserSessions("uH", true, true, Collections.singletonMap("X-R","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", headerCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testRevokeUserSessions_MissingUserId() throws Exception {
        api.revokeUserSessions(null, true, true);
    }
}

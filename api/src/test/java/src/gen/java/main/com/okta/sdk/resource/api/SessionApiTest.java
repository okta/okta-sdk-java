package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.SessionApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateSessionRequest;
import com.okta.sdk.resource.model.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class SessionApiTest {

    private ApiClient apiClient;
    private SessionApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new SessionApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeVoid(String method) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    private void stubInvokeSession(String method, Session value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* closeCurrentSession */
    @Test
    public void testCloseCurrentSession_Success() throws Exception {
        stubInvokeVoid("DELETE");
        api.closeCurrentSession(null);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/me"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testCloseCurrentSession_WithHeaders() throws Exception {
        stubInvokeVoid("DELETE");
        Map<String,String> hdr = Collections.singletonMap("X-CLOSE","v");
        api.closeCurrentSession(null, hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", cap.getValue().get("X-CLOSE"));
    }

    /* createSession */
    @Test
    public void testCreateSession_Success() throws Exception {
        Session expected = new Session();
        stubInvokeSession("POST", expected);
        Session actual = api.createSession(new CreateSessionRequest());
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), any(CreateSessionRequest.class),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testCreateSession_MissingBody() {
        expect400(() -> api.createSession(null));
    }

    /* getCurrentSession */
    @Test
    public void testGetCurrentSession_Success() throws Exception {
        Session expected = new Session();
        stubInvokeSession("GET", expected);
        Session actual = api.getCurrentSession(null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/me"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* getSession */
    @Test
    public void testGetSession_Success() throws Exception {
        Session expected = new Session();
        stubInvokeSession("GET", expected);
        Session actual = api.getSession("SID123");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/SID123"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetSession_MissingId() {
        expect400(() -> api.getSession(null));
    }

    /* refreshCurrentSession */
    @Test
    public void testRefreshCurrentSession_Success() throws Exception {
        Session expected = new Session();
        stubInvokeSession("POST", expected);
        Session actual = api.refreshCurrentSession(null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/me/lifecycle/refresh"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* refreshSession */
    @Test
    public void testRefreshSession_Success() throws Exception {
        Session expected = new Session();
        stubInvokeSession("POST", expected);
        Session actual = api.refreshSession("SID456");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/SID456/lifecycle/refresh"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testRefreshSession_MissingId() {
        expect400(() -> api.refreshSession(null));
    }

    /* revokeSession */
    @Test
    public void testRevokeSession_Success() throws Exception {
        stubInvokeVoid("DELETE");
        api.revokeSession("SID789");
        verify(apiClient).invokeAPI(
            eq("/api/v1/sessions/SID789"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testRevokeSession_WithHeaders() throws Exception {
        stubInvokeVoid("DELETE");
        api.revokeSession("SID790", Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", cap.getValue().get("X-R"));
    }

    @Test
    public void testRevokeSession_MissingId() {
        expect400(() -> api.revokeSession(null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_GetSession() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getSession("X");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_RevokeSession() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.revokeSession("Z");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_CreateSession() throws Exception {
        stubInvokeSession("POST", new Session());
        api.createSession(new CreateSessionRequest());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/sessions"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_RevokeSession() throws Exception {
        stubInvokeVoid("DELETE");
        api.revokeSession("SID111");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/sessions/SID111"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = SessionApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try {
            r.run();
            fail("Expected 400");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }
}

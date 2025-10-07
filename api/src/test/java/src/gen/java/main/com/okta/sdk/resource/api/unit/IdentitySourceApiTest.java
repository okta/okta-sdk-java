package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.BulkDeleteRequestBody;
import com.okta.sdk.resource.model.BulkUpsertRequestBody;
import com.okta.sdk.resource.model.IdentitySourceSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class IdentitySourceApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.IdentitySourceApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.IdentitySourceApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

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

    /* createIdentitySourceSession */
    @Test
    public void testCreateIdentitySourceSession_Success() throws Exception {
        IdentitySourceSession expected = new IdentitySourceSession();
        stubInvoke(expected);
        IdentitySourceSession actual = api.createIdentitySourceSession("src1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src1/sessions"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("src1");
    }

    @Test
    public void testCreateIdentitySourceSession_WithHeaders() throws Exception {
        stubInvoke(new IdentitySourceSession());
        api.createIdentitySourceSession("srcH", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-H"));
    }

    @Test
    public void testCreateIdentitySourceSession_MissingId_Throws() {
        try {
            api.createIdentitySourceSession(null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    /* getIdentitySourceSession */
    @Test
    public void testGetIdentitySourceSession_Success() throws Exception {
        IdentitySourceSession expected = new IdentitySourceSession();
        stubInvoke(expected);
        IdentitySourceSession actual = api.getIdentitySourceSession("src2","sess2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src2/sessions/sess2"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("src2");
        verify(apiClient).escapeString("sess2");
    }

    @Test
    public void testGetIdentitySourceSession_WithHeaders() throws Exception {
        stubInvoke(new IdentitySourceSession());
        api.getIdentitySourceSession("srcGH","sessGH", Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetIdentitySourceSession_MissingSource() {
        try { api.getIdentitySourceSession(null,"s"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deleteIdentitySourceSession */
    @Test
    public void testDeleteIdentitySourceSession_Success() throws Exception {
        stubVoidInvoke();
        api.deleteIdentitySourceSession("src3","sess3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src3/sessions/sess3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("src3");
        verify(apiClient).escapeString("sess3");
    }

    @Test
    public void testDeleteIdentitySourceSession_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteIdentitySourceSession("srcD","sessD", Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteIdentitySourceSession_MissingSource() {
        try { api.deleteIdentitySourceSession(null,"s"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testDeleteIdentitySourceSession_MissingSession() {
        try { api.deleteIdentitySourceSession("src",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listIdentitySourceSessions */
    @Test
    public void testListIdentitySourceSessions_Success() throws Exception {
        List<IdentitySourceSession> expected = Arrays.asList(new IdentitySourceSession());
        stubInvoke(expected);
        List<IdentitySourceSession> actual = api.listIdentitySourceSessions("src4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src4/sessions"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListIdentitySourceSessions_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listIdentitySourceSessions("srcLH", Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testListIdentitySourceSessions_MissingSource() {
        try { api.listIdentitySourceSessions(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* startImportFromIdentitySource */
    @Test
    public void testStartImportFromIdentitySource_Success() throws Exception {
        IdentitySourceSession expected = new IdentitySourceSession();
        stubInvoke(expected);
        IdentitySourceSession actual = api.startImportFromIdentitySource("src5","sess5");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src5/sessions/sess5/start-import"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testStartImportFromIdentitySource_WithHeaders() throws Exception {
        stubInvoke(new IdentitySourceSession());
        api.startImportFromIdentitySource("srcSI","sessSI", Collections.singletonMap("X-S","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-S"));
    }

    @Test
    public void testStartImportFromIdentitySource_MissingSource() {
        try { api.startImportFromIdentitySource(null,"s"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testStartImportFromIdentitySource_MissingSession() {
        try { api.startImportFromIdentitySource("src",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* uploadIdentitySourceDataForDelete */
    @Test
    public void testUploadIdentitySourceDataForDelete_SuccessBodySent() throws Exception {
        stubVoidInvoke();
        BulkDeleteRequestBody body = new BulkDeleteRequestBody();
        api.uploadIdentitySourceDataForDelete("src6","sess6", body);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src6/sessions/sess6/bulk-delete"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUploadIdentitySourceDataForDelete_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.uploadIdentitySourceDataForDelete("src7","sess7", null, Collections.singletonMap("X-D","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-D"));
    }

    @Test
    public void testUploadIdentitySourceDataForDelete_MissingSource() {
        try { api.uploadIdentitySourceDataForDelete(null,"s",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testUploadIdentitySourceDataForDelete_MissingSession() {
        try { api.uploadIdentitySourceDataForDelete("src",null,null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* uploadIdentitySourceDataForUpsert */
    @Test
    public void testUploadIdentitySourceDataForUpsert_SuccessBodySent() throws Exception {
        stubVoidInvoke();
        BulkUpsertRequestBody body = new BulkUpsertRequestBody();
        api.uploadIdentitySourceDataForUpsert("src8","sess8", body);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/identity-sources/src8/sessions/sess8/bulk-upsert"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUploadIdentitySourceDataForUpsert_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.uploadIdentitySourceDataForUpsert("src9","sess9", null, Collections.singletonMap("X-U","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-U"));
    }

    @Test
    public void testUploadIdentitySourceDataForUpsert_MissingSource() {
        try { api.uploadIdentitySourceDataForUpsert(null,"s",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testUploadIdentitySourceDataForUpsert_MissingSession() {
        try { api.uploadIdentitySourceDataForUpsert("src",null,null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testCreateIdentitySourceSession_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.createIdentitySourceSession("srcX");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("bad"));
        }
    }

    @Test
    public void testStartImportFromIdentitySource_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.startImportFromIdentitySource("srcY","sessY");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.IdentitySourceApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

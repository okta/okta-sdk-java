package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateRealmAssignmentRequest;
import com.okta.sdk.resource.model.OperationRequest;
import com.okta.sdk.resource.model.OperationResponse;
import com.okta.sdk.resource.model.RealmAssignment;
import com.okta.sdk.resource.model.UpdateRealmAssignmentRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RealmAssignmentApiTest {

    private ApiClient apiClient;
    private RealmAssignmentApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RealmAssignmentApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
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

    /* activateRealmAssignment */
    @Test
    public void testActivateRealmAssignment_Success() throws Exception {
        api.activateRealmAssignment("A1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/A1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testActivateRealmAssignment_WithHeaders() throws Exception {
        api.activateRealmAssignment("A2", Collections.singletonMap("X-Act","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-Act"));
    }

    @Test
    public void testActivateRealmAssignment_MissingId() {
        try {
            api.activateRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* deactivateRealmAssignment */
    @Test
    public void testDeactivateRealmAssignment_Success() throws Exception {
        api.deactivateRealmAssignment("D1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/D1/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeactivateRealmAssignment_MissingId() {
        try {
            api.deactivateRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* deleteRealmAssignment */
    @Test
    public void testDeleteRealmAssignment_Success() throws Exception {
        api.deleteRealmAssignment("DEL1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/DEL1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteRealmAssignment_WithHeaders() throws Exception {
        api.deleteRealmAssignment("DEL2", Collections.singletonMap("X-Del","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", headers.getValue().get("X-Del"));
    }

    @Test
    public void testDeleteRealmAssignment_MissingId() {
        try {
            api.deleteRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* createRealmAssignment */
    @Test
    public void testCreateRealmAssignment_Success() throws Exception {
        RealmAssignment expected = new RealmAssignment();
        stubInvoke(expected);
        CreateRealmAssignmentRequest body = new CreateRealmAssignmentRequest();
        RealmAssignment actual = api.createRealmAssignment(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateRealmAssignment_WithHeaders() throws Exception {
        stubInvoke(new RealmAssignment());
        api.createRealmAssignment(new CreateRealmAssignmentRequest(), Collections.singletonMap("X-C","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-C"));
    }

    @Test
    public void testCreateRealmAssignment_MissingBody() {
        try {
            api.createRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* executeRealmAssignment */
    @Test
    public void testExecuteRealmAssignment_Success() throws Exception {
        OperationResponse expected = new OperationResponse();
        stubInvoke(expected);
        OperationRequest body = new OperationRequest();
        OperationResponse actual = api.executeRealmAssignment(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/operations"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testExecuteRealmAssignment_MissingBody() {
        try {
            api.executeRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* getRealmAssignment */
    @Test
    public void testGetRealmAssignment_Success() throws Exception {
        RealmAssignment expected = new RealmAssignment();
        stubInvoke(expected);
        RealmAssignment actual = api.getRealmAssignment("G1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/G1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRealmAssignment_WithHeaders() throws Exception {
        stubInvoke(new RealmAssignment());
        api.getRealmAssignment("G2", Collections.singletonMap("X-H","x"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("x", headers.getValue().get("X-H"));
    }

    @Test
    public void testGetRealmAssignment_MissingId() {
        try {
            api.getRealmAssignment(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* listRealmAssignmentOperations */
    @Test
    public void testListRealmAssignmentOperations_Success() throws Exception {
        List<OperationResponse> expected = new ArrayList<>();
        stubInvoke(expected);
        List<OperationResponse> actual = api.listRealmAssignmentOperations(100, "AFTER1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/operations"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("limit"), eq(100));
        verify(apiClient).parameterToPair(eq("after"), eq("AFTER1"));
    }

    @Test
    public void testListRealmAssignmentOperations_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRealmAssignmentOperations(null, null, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headers.getValue().get("X-L"));
    }

    /* listRealmAssignments */
    @Test
    public void testListRealmAssignments_Success() throws Exception {
        List<RealmAssignment> expected = new ArrayList<>();
        stubInvoke(expected);
        List<RealmAssignment> actual = api.listRealmAssignments(25, "CURSOR1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("limit"), eq(25));
        verify(apiClient).parameterToPair(eq("after"), eq("CURSOR1"));
    }

    @Test
    public void testListRealmAssignments_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRealmAssignments(null, null, Collections.singletonMap("X-A","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-A"));
    }

    /* replaceRealmAssignment */
    @Test
    public void testReplaceRealmAssignment_Success() throws Exception {
        RealmAssignment expected = new RealmAssignment();
        stubInvoke(expected);
        UpdateRealmAssignmentRequest body = new UpdateRealmAssignmentRequest();
        RealmAssignment actual = api.replaceRealmAssignment("R1", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/realm-assignments/R1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRealmAssignment_WithHeaders() throws Exception {
        stubInvoke(new RealmAssignment());
        api.replaceRealmAssignment("R2", new UpdateRealmAssignmentRequest(), Collections.singletonMap("X-R","z"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("z", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceRealmAssignment_MissingArgs() {
        try { api.replaceRealmAssignment(null, new UpdateRealmAssignmentRequest()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.replaceRealmAssignment("RID", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Create() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.createRealmAssignment(new CreateRealmAssignmentRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.getRealmAssignment("X1");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvoke(new RealmAssignment());
        api.getRealmAssignment("H1");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/realm-assignments/H1"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRealmAssignments(null, null);
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/realm-assignments"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RealmAssignmentApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

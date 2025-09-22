package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateIamRoleRequest;
import com.okta.sdk.resource.model.IamRole;
import com.okta.sdk.resource.model.IamRoles;
import com.okta.sdk.resource.model.UpdateIamRoleRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleECustomApiTest {

    private ApiClient apiClient;
    private RoleECustomApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleECustomApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeIamRole(IamRole value, String method) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeIamRoles(IamRoles value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* createRole */
    @Test
    public void testCreateRole_Success() throws Exception {
        IamRole expected = new IamRole();
        stubInvokeIamRole(expected, "POST");
        IamRole actual = api.createRole(new CreateIamRoleRequest());
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles"), eq("POST"),
            anyList(), anyList(),
            anyString(), any(CreateIamRoleRequest.class),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testCreateRole_WithHeaders() throws Exception {
        stubInvokeIamRole(new IamRole(), "POST");
        Map<String,String> hdr = Collections.singletonMap("X-H","v");
        api.createRole(new CreateIamRoleRequest(), hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", cap.getValue().get("X-H"));
    }

    @Test
    public void testCreateRole_MissingParam() {
        expect400(() -> api.createRole(null));
    }

    /* deleteRole */
    @Test
    public void testDeleteRole_Success() throws Exception {
        api.deleteRole("R1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/R1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteRole_WithHeaders() throws Exception {
        Map<String,String> hdr = Collections.singletonMap("X-D","1");
        api.deleteRole("R2", hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteRole_MissingParam() {
        expect400(() -> api.deleteRole(null));
    }

    /* getRole */
    @Test
    public void testGetRole_Success() throws Exception {
        IamRole expected = new IamRole();
        stubInvokeIamRole(expected, "GET");
        IamRole actual = api.getRole("RID");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/RID"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRole_WithHeaders() throws Exception {
        stubInvokeIamRole(new IamRole(), "GET");
        api.getRole("RID2", Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("g", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetRole_MissingParam() {
        expect400(() -> api.getRole(null));
    }

    /* listRoles */
    @Test
    public void testListRoles_Success_WithAfter() throws Exception {
        IamRoles expected = new IamRoles();
        stubInvokeIamRoles(expected);
        IamRoles actual = api.listRoles("CUR123");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("after","CUR123");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListRoles_Success_NoAfter() throws Exception {
        IamRoles expected = new IamRoles();
        stubInvokeIamRoles(expected);
        IamRoles actual = api.listRoles(null);
        assertSame(expected, actual);
    }

    @Test
    public void testListRoles_WithHeaders() throws Exception {
        stubInvokeIamRoles(new IamRoles());
        api.listRoles(null, Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", cap.getValue().get("X-L"));
    }

    /* replaceRole */
    @Test
    public void testReplaceRole_Success() throws Exception {
        IamRole expected = new IamRole();
        stubInvokeIamRole(expected, "PUT");
        IamRole actual = api.replaceRole("RID3", new UpdateIamRoleRequest());
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/RID3"), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(UpdateIamRoleRequest.class),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceRole_WithHeaders() throws Exception {
        stubInvokeIamRole(new IamRole(), "PUT");
        api.replaceRole("RID4", new UpdateIamRoleRequest(), Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceRole_MissingParams() {
        expect400(() -> api.replaceRole(null, new UpdateIamRoleRequest()));
        expect400(() -> api.replaceRole("RID5", null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_GetRole() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"up"));
        try {
            api.getRole("BAD");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_DeleteRole() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.deleteRole("RIDX");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_ListRoles() throws Exception {
        stubInvokeIamRoles(new IamRoles());
        api.listRoles(null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/roles"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_GetRole() throws Exception {
        stubInvokeIamRole(new IamRole(), "GET");
        api.getRole("RRR");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/roles/RRR"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RoleECustomApi.class.getDeclaredMethod("getObjectMapper");
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

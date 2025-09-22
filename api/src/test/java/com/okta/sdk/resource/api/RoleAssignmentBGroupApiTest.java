package com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AssignRoleToGroupRequest;
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleAssignmentBGroupApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RoleAssignmentBGroupApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RoleAssignmentBGroupApi(apiClient);

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
            any(String[].class), any()
        )).thenReturn(value);
    }

    /* assignRoleToGroup */
    @Test
    public void testAssignRoleToGroup_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        AssignRoleToGroupRequest body = new AssignRoleToGroupRequest();
        ListGroupAssignedRoles200ResponseInner actual =
            api.assignRoleToGroup("G1", body, true);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G1/roles"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertSame(body, bodyCap.getValue());
        verify(apiClient).parameterToPair(eq("disableNotifications"), eq(true));
    }

    @Test
    public void testAssignRoleToGroup_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.assignRoleToGroup("G2", new AssignRoleToGroupRequest(), false,
            Collections.singletonMap("X-Custom","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-Custom"));
    }

    @Test
    public void testAssignRoleToGroup_MissingArgs() {
        try { api.assignRoleToGroup(null, new AssignRoleToGroupRequest(), null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("groupId")); }
        try { api.assignRoleToGroup("G3", null, null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("assignRoleRequest")); }
    }

    /* getGroupAssignedRole */
    @Test
    public void testGetGroupAssignedRole_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        ListGroupAssignedRoles200ResponseInner actual = api.getGroupAssignedRole("G4","R1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G4/roles/R1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
    }

    @Test
    public void testGetGroupAssignedRole_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.getGroupAssignedRole("G5","R2", Collections.singletonMap("X-H","h"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("h", headers.getValue().get("X-H"));
    }

    @Test
    public void testGetGroupAssignedRole_MissingArgs() {
        try { api.getGroupAssignedRole(null,"R"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.getGroupAssignedRole("G",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* listGroupAssignedRoles */
    @Test
    public void testListGroupAssignedRoles_Success() throws Exception {
        List<ListGroupAssignedRoles200ResponseInner> expected = new ArrayList<>();
        stubInvoke(expected);
        List<ListGroupAssignedRoles200ResponseInner> actual =
            api.listGroupAssignedRoles("G6", "targets/groups");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G6/roles"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        verify(apiClient).parameterToPair(eq("expand"), eq("targets/groups"));
    }

    @Test
    public void testListGroupAssignedRoles_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listGroupAssignedRoles("G7", null, Collections.singletonMap("X-L","1"));
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

    @Test
    public void testListGroupAssignedRoles_MissingGroup() {
        try { api.listGroupAssignedRoles(null, null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* unassignRoleFromGroup */
    @Test
    public void testUnassignRoleFromGroup_Success() throws Exception {
        stubInvoke(null);
        api.unassignRoleFromGroup("G8","R8");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G8/roles/R8"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testUnassignRoleFromGroup_WithHeaders() throws Exception {
        stubInvoke(null);
        api.unassignRoleFromGroup("G9","R9", Collections.singletonMap("X-U","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headers.getValue().get("X-U"));
    }

    @Test
    public void testUnassignRoleFromGroup_MissingArgs() {
        try { api.unassignRoleFromGroup(null,"R"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignRoleFromGroup("G",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Assign() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.assignRoleToGroup("GX", new AssignRoleToGroupRequest(), null);
            fail("Expected");
        } catch (ApiException e){
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
            any(String[].class), any()
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.getGroupAssignedRole("GZ","RZ");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Assign() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.assignRoleToGroup("G10", new AssignRoleToGroupRequest(), null);
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/groups/G10/roles"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listGroupAssignedRoles("G11", null);
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/groups/G11/roles"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RoleAssignmentBGroupApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

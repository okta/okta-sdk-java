package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.RoleAssignmentAUserApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AssignRoleToUser201Response;
import com.okta.sdk.resource.model.AssignRoleToUserRequest;
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner;
import com.okta.sdk.resource.model.RoleAssignedUsers;
import com.okta.sdk.resource.model.RoleGovernance;
import com.okta.sdk.resource.model.RoleGovernanceResources;
import com.okta.sdk.resource.model.RoleGovernanceSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleAssignmentAUserApiTest {

    private ApiClient apiClient;
    private RoleAssignmentAUserApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleAssignmentAUserApi(apiClient);

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

    /* assignRoleToUser */
    @Test
    public void testAssignRoleToUser_Success() throws Exception {
        AssignRoleToUser201Response expected = new AssignRoleToUser201Response();
        stubInvoke(expected);
        AssignRoleToUserRequest body = new AssignRoleToUserRequest();
        AssignRoleToUser201Response actual = api.assignRoleToUser("U1", body, true);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U1/roles"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
        verify(apiClient).parameterToPair(eq("disableNotifications"), eq(true));
    }

    @Test
    public void testAssignRoleToUser_WithHeaders() throws Exception {
        stubInvoke(new AssignRoleToUser201Response());
        api.assignRoleToUser("U2", new AssignRoleToUserRequest(), false, Collections.singletonMap("X-Custom","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-Custom"));
    }

    @Test
    public void testAssignRoleToUser_MissingArgs() {
        try { api.assignRoleToUser(null, new AssignRoleToUserRequest(), null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("userId")); }
        try { api.assignRoleToUser("U3", null, null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("assignRoleRequest")); }
    }

    /* getRoleAssignmentGovernanceGrant */
    @Test
    public void testGetRoleAssignmentGovernanceGrant_Success() throws Exception {
        RoleGovernanceSource expected = new RoleGovernanceSource();
        stubInvoke(expected);
        RoleGovernanceSource actual = api.getRoleAssignmentGovernanceGrant("U1","RA1","G1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U1/roles/RA1/governance/G1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRoleAssignmentGovernanceGrant_Missing() {
        try { api.getRoleAssignmentGovernanceGrant(null,"RA","G"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.getRoleAssignmentGovernanceGrant("U",null,"G"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.getRoleAssignmentGovernanceGrant("U","RA",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* getRoleAssignmentGovernanceGrantResources */
    @Test
    public void testGetRoleAssignmentGovernanceGrantResources_Success() throws Exception {
        RoleGovernanceResources expected = new RoleGovernanceResources();
        stubInvoke(expected);
        RoleGovernanceResources actual = api.getRoleAssignmentGovernanceGrantResources("U1","RA2","G2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U1/roles/RA2/governance/G2/resources"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* getUserAssignedRole */
    @Test
    public void testGetUserAssignedRole_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        assertSame(expected, api.getUserAssignedRole("U1","RA3"));
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U1/roles/RA3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* getUserAssignedRoleGovernance */
    @Test
    public void testGetUserAssignedRoleGovernance_Success() throws Exception {
        RoleGovernance expected = new RoleGovernance();
        stubInvoke(expected);
        assertSame(expected, api.getUserAssignedRoleGovernance("U2","RA4"));
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U2/roles/RA4/governance"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* listAssignedRolesForUser */
    @Test
    public void testListAssignedRolesForUser_Success() throws Exception {
        List<ListGroupAssignedRoles200ResponseInner> expected = new ArrayList<>();
        stubInvoke(expected);
        List<ListGroupAssignedRoles200ResponseInner> actual = api.listAssignedRolesForUser("U5","targets/groups");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U5/roles"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("expand"), eq("targets/groups"));
    }

    @Test
    public void testListAssignedRolesForUser_MissingUser() {
        try { api.listAssignedRolesForUser(null, null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* listUsersWithRoleAssignments */
    @Test
    public void testListUsersWithRoleAssignments_Success() throws Exception {
        RoleAssignedUsers expected = new RoleAssignedUsers();
        stubInvoke(expected);
        RoleAssignedUsers actual = api.listUsersWithRoleAssignments("CURSOR", 55);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/assignees/users"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("after"), eq("CURSOR"));
        verify(apiClient).parameterToPair(eq("limit"), eq(55));
    }

    /* unassignRoleFromUser */
    @Test
    public void testUnassignRoleFromUser_Success() throws Exception {
        stubInvoke(null);
        api.unassignRoleFromUser("U7","RA7");
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U7/roles/RA7"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testUnassignRoleFromUser_Missing() {
        try { api.unassignRoleFromUser(null,"R"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignRoleFromUser("U",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* Headers propagation example */
    @Test
    public void testGetUserAssignedRole_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.getUserAssignedRole("U9","RA9", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-H"));
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
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.assignRoleToUser("UERR", new AssignRoleToUserRequest(), null);
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_GetGovernanceGrant() throws Exception {
        stubInvoke(new RoleGovernanceSource());
        api.getRoleAssignmentGovernanceGrant("U1","RA1","G1");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/users/U1/roles/RA1/governance/G1"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_ListUsers() throws Exception {
        stubInvoke(new RoleAssignedUsers());
        api.listUsersWithRoleAssignments(null, null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/assignees/users"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RoleAssignmentClientApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true); // bypass protected access
        ObjectMapper mapper = (ObjectMapper) m.invoke(null); // static method, so null target
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

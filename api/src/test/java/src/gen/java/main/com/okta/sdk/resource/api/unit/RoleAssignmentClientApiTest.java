package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
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
public class RoleAssignmentClientApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RoleAssignmentClientApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RoleAssignmentClientApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
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

    /* assignRoleToClient */
    @Test
    public void testAssignRoleToClient_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        AssignRoleToGroupRequest body = new AssignRoleToGroupRequest();
        ListGroupAssignedRoles200ResponseInner actual = api.assignRoleToClient("C1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C1/roles"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testAssignRoleToClient_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        Map<String,String> hdrs = Collections.singletonMap("X-Custom","v");
        api.assignRoleToClient("C2", new AssignRoleToGroupRequest(), hdrs);
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headersCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headersCap.getValue().get("X-Custom"));
    }

    @Test
    public void testAssignRoleToClient_MissingParams() {
        try { api.assignRoleToClient(null, new AssignRoleToGroupRequest()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("clientId")); }
        try { api.assignRoleToClient("CID", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("assignRoleToGroupRequest")); }
    }

    /* deleteRoleFromClient */
    @Test
    public void testDeleteRoleFromClient_Success() throws Exception {
        stubInvoke(null);
        api.deleteRoleFromClient("C3","R1");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C3/roles/R1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteRoleFromClient_WithHeaders() throws Exception {
        stubInvoke(null);
        api.deleteRoleFromClient("C4","R2", Collections.singletonMap("X-Del","1"));
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headersCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", headersCap.getValue().get("X-Del"));
    }

    @Test
    public void testDeleteRoleFromClient_MissingParams() {
        try { api.deleteRoleFromClient(null,"R"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.deleteRoleFromClient("C",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* listRolesForClient */
    @Test
    public void testListRolesForClient_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        assertSame(expected, api.listRolesForClient("C5"));
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C5/roles"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListRolesForClient_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.listRolesForClient("C6", Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headersCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headersCap.getValue().get("X-L"));
    }

    @Test
    public void testListRolesForClient_MissingClient() {
        try { api.listRolesForClient(null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* retrieveClientRole */
    @Test
    public void testRetrieveClientRole_Success() throws Exception {
        ListGroupAssignedRoles200ResponseInner expected = new ListGroupAssignedRoles200ResponseInner();
        stubInvoke(expected);
        assertSame(expected, api.retrieveClientRole("C7","R7"));
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C7/roles/R7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testRetrieveClientRole_WithHeaders() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.retrieveClientRole("C8","R8", Collections.singletonMap("X-H","h"));
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headersCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("h", headersCap.getValue().get("X-H"));
    }

    @Test
    public void testRetrieveClientRole_MissingParams() {
        try { api.retrieveClientRole(null,"R"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.retrieveClientRole("C",null); fail("Expected"); }
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
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.assignRoleToClient("CERR", new AssignRoleToGroupRequest());
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
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.retrieveClientRole("CERR","RERR");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    @Test
    public void testApiExceptionPropagates_Delete() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteRoleFromClient("CERR","RERR");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("unavailable"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Assign() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.assignRoleToClient("C9", new AssignRoleToGroupRequest());
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/oauth2/v1/clients/C9/roles"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.listRolesForClient("C10");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/oauth2/v1/clients/C10/roles"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Retrieve() throws Exception {
        stubInvoke(new ListGroupAssignedRoles200ResponseInner());
        api.retrieveClientRole("C11","R11");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/oauth2/v1/clients/C11/roles/R11"));
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

package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CatalogApplication;
import com.okta.sdk.resource.model.Group;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleBTargetClientApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RoleBTargetClientApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RoleBTargetClientApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private <T> void stubInvokeList(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* assignAppTargetInstanceRoleForClient */
    @Test
    public void testAssignAppInstanceTarget_Success() throws Exception {
        api.assignAppTargetInstanceRoleForClient("C1","R1","appA","A1");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C1/roles/R1/targets/catalog/apps/appA/A1"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testAssignAppInstanceTarget_WithHeaders() throws Exception {
        api.assignAppTargetInstanceRoleForClient("C2","R2","appB","B1",
            Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C2/roles/R2/targets/catalog/apps/appB/B1"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
        assertEquals("v", hdr.getValue().get("X-H"));
    }

    @Test
    public void testAssignAppInstanceTarget_MissingParams() {
        expect400(() -> api.assignAppTargetInstanceRoleForClient(null,"R","A","ID"));
        expect400(() -> api.assignAppTargetInstanceRoleForClient("C",null,"A","ID"));
        expect400(() -> api.assignAppTargetInstanceRoleForClient("C","R",null,"ID"));
        expect400(() -> api.assignAppTargetInstanceRoleForClient("C","R","A",null));
    }

    /* assignAppTargetRoleToClient */
    @Test
    public void testAssignAppTarget_Success() throws Exception {
        api.assignAppTargetRoleToClient("C3","R3","appC");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C3/roles/R3/targets/catalog/apps/appC"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testAssignAppTarget_WithHeaders() throws Exception {
        api.assignAppTargetRoleToClient("C4","R4","appD", Collections.singletonMap("X-A","1"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C4/roles/R4/targets/catalog/apps/appD"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
        assertEquals("1", hdr.getValue().get("X-A"));
    }

    @Test
    public void testAssignAppTarget_MissingParams() {
        expect400(() -> api.assignAppTargetRoleToClient(null,"R","A"));
        expect400(() -> api.assignAppTargetRoleToClient("C",null,"A"));
        expect400(() -> api.assignAppTargetRoleToClient("C","R",null));
    }

    /* assignGroupTargetRoleForClient */
    @Test
    public void testAssignGroupTarget_Success() throws Exception {
        api.assignGroupTargetRoleForClient("C5","R5","G1");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C5/roles/R5/targets/groups/G1"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testAssignGroupTarget_WithHeaders() throws Exception {
        api.assignGroupTargetRoleForClient("C6","R6","G2",
            Collections.singletonMap("X-G","v"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C6/roles/R6/targets/groups/G2"),
            eq("PUT"),
            anyList(), anyList(),
            eq(""), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), isNull()
        );
        assertEquals("v", hdr.getValue().get("X-G"));
    }

    @Test
    public void testAssignGroupTarget_MissingParams() {
        expect400(() -> api.assignGroupTargetRoleForClient(null,"R","G"));
        expect400(() -> api.assignGroupTargetRoleForClient("C",null,"G"));
        expect400(() -> api.assignGroupTargetRoleForClient("C","R",null));
    }

    /* listAppTargetRoleToClient */
    @Test
    public void testListAppTargets_Success() throws Exception {
        List<CatalogApplication> expected = new ArrayList<>();
        stubInvokeList(expected);
        List<CatalogApplication> actual =
            api.listAppTargetRoleToClient("C7","R7","AF", 25);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C7/roles/R7/targets/catalog/apps"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after","AF");
        verify(apiClient).parameterToPair("limit",25);
    }

    @Test
    public void testListAppTargets_WithHeaders() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listAppTargetRoleToClient("C8","R8", null, null,
            Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", hdr.getValue().get("X-H"));
    }

    @Test
    public void testListAppTargets_MissingParams() {
        expect400(() -> api.listAppTargetRoleToClient(null,"R", null,null));
        expect400(() -> api.listAppTargetRoleToClient("C",null, null,null));
    }

    /* listGroupTargetRoleForClient */
    @Test
    public void testListGroupTargets_Success() throws Exception {
        List<Group> expected = new ArrayList<>();
        stubInvokeList(expected);
        List<Group> actual =
            api.listGroupTargetRoleForClient("C9","R9","CUR", 10);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C9/roles/R9/targets/groups"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after","CUR");
        verify(apiClient).parameterToPair("limit",10);
    }

    @Test
    public void testListGroupTargets_WithHeaders() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listGroupTargetRoleForClient("C10","R10", null, null,
            Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", hdr.getValue().get("X-G"));
    }

    @Test
    public void testListGroupTargets_MissingParams() {
        expect400(() -> api.listGroupTargetRoleForClient(null,"R", null,null));
        expect400(() -> api.listGroupTargetRoleForClient("C",null, null,null));
    }

    /* removeAppTargetInstanceRoleForClient */
    @Test
    public void testRemoveAppInstanceTarget_Success() throws Exception {
        api.removeAppTargetInstanceRoleForClient("C11","R11","appZ","Z1");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C11/roles/R11/targets/catalog/apps/appZ/Z1"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testRemoveAppInstanceTarget_WithHeaders() throws Exception {
        api.removeAppTargetInstanceRoleForClient("C12","R12","appY","Y1",
            Collections.singletonMap("X-U","v"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", hdr.getValue().get("X-U"));
    }

    @Test
    public void testRemoveAppInstanceTarget_MissingParams() {
        expect400(() -> api.removeAppTargetInstanceRoleForClient(null,"R","A","ID"));
        expect400(() -> api.removeAppTargetInstanceRoleForClient("C",null,"A","ID"));
        expect400(() -> api.removeAppTargetInstanceRoleForClient("C","R",null,"ID"));
        expect400(() -> api.removeAppTargetInstanceRoleForClient("C","R","A",null));
    }

    /* removeAppTargetRoleFromClient */
    @Test
    public void testRemoveAppTarget_Success() throws Exception {
        api.removeAppTargetRoleFromClient("C13","R13","appQ");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C13/roles/R13/targets/catalog/apps/appQ"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testRemoveAppTarget_WithHeaders() throws Exception {
        api.removeAppTargetRoleFromClient("C14","R14","appW",
            Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", hdr.getValue().get("X-R"));
    }

    @Test
    public void testRemoveAppTarget_MissingParams() {
        expect400(() -> api.removeAppTargetRoleFromClient(null,"R","A"));
        expect400(() -> api.removeAppTargetRoleFromClient("C",null,"A"));
        expect400(() -> api.removeAppTargetRoleFromClient("C","R",null));
    }

    /* removeGroupTargetRoleFromClient */
    @Test
    public void testRemoveGroupTarget_Success() throws Exception {
        api.removeGroupTargetRoleFromClient("C15","R15","G15");
        verify(apiClient).invokeAPI(
            eq("/oauth2/v1/clients/C15/roles/R15/targets/groups/G15"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testRemoveGroupTarget_WithHeaders() throws Exception {
        api.removeGroupTargetRoleFromClient("C16","R16","G16",
            Collections.singletonMap("X-H","h"));
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("h", hdr.getValue().get("X-H"));
    }

    @Test
    public void testRemoveGroupTarget_MissingParams() {
        expect400(() -> api.removeGroupTargetRoleFromClient(null,"R","G"));
        expect400(() -> api.removeGroupTargetRoleFromClient("C",null,"G"));
        expect400(() -> api.removeGroupTargetRoleFromClient("C","R",null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_ListApps() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"down"));
        try {
            api.listAppTargetRoleToClient("C","R", null,null);
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("down"));
        }
    }

    @Test
    public void testApiExceptionPropagates_RemoveGroup() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"svc"));
        try {
            api.removeGroupTargetRoleFromClient("C","R","G");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("svc"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_ListApps() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listAppTargetRoleToClient("CX","RX", null,null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/oauth2/v1/clients/CX/roles/RX/targets/catalog/apps"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_ListGroups() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listGroupTargetRoleForClient("CY","RY", null,null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/oauth2/v1/clients/CY/roles/RY/targets/groups"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RoleBTargetClientApi.class.getDeclaredMethod("getObjectMapper");
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
        try { r.run(); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }
}

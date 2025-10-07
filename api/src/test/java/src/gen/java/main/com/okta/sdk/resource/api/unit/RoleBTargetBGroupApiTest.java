package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.RoleBTargetBGroupApi;
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
public class RoleBTargetBGroupApiTest {

    private ApiClient apiClient;
    private RoleBTargetBGroupApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleBTargetBGroupApi(apiClient);

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

    /* assignAppInstanceTargetToAppAdminRoleForGroup */


    @Test
    public void testAssignAppInstanceTarget_MissingParams() {
        expect400(() -> api.assignAppInstanceTargetToAppAdminRoleForGroup(null,"R","A","ID"));
        expect400(() -> api.assignAppInstanceTargetToAppAdminRoleForGroup("G",null,"A","ID"));
        expect400(() -> api.assignAppInstanceTargetToAppAdminRoleForGroup("G","R",null,"ID"));
        expect400(() -> api.assignAppInstanceTargetToAppAdminRoleForGroup("G","R","A",null));
    }

    /* assignAppTargetToAdminRoleForGroup */

    @Test
    public void testAssignAppTarget_MissingParams() {
        expect400(() -> api.assignAppTargetToAdminRoleForGroup(null,"R","A"));
        expect400(() -> api.assignAppTargetToAdminRoleForGroup("G",null,"A"));
        expect400(() -> api.assignAppTargetToAdminRoleForGroup("G","R",null));
    }



    @Test
    public void testAssignGroupTarget_MissingParams() {
        expect400(() -> api.assignGroupTargetToGroupAdminRole(null,"R","T"));
        expect400(() -> api.assignGroupTargetToGroupAdminRole("G",null,"T"));
        expect400(() -> api.assignGroupTargetToGroupAdminRole("G","R",null));
    }

    /* listApplicationTargetsForApplicationAdministratorRoleForGroup */
    @Test
    public void testListAppTargets_Success() throws Exception {
        List<CatalogApplication> expected = new ArrayList<>();
        stubInvokeList(expected);
        List<CatalogApplication> actual =
            api.listApplicationTargetsForApplicationAdministratorRoleForGroup("G7","RA7","AF", 30);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G7/roles/RA7/targets/catalog/apps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after","AF");
        verify(apiClient).parameterToPair("limit",30);
    }

    @Test
    public void testListAppTargets_WithHeaders() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listApplicationTargetsForApplicationAdministratorRoleForGroup("G8","RA8", null, null,
            Collections.singletonMap("X-H","v"));
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

    @Test
    public void testListAppTargets_MissingParams() {
        expect400(() -> api.listApplicationTargetsForApplicationAdministratorRoleForGroup(null,"R", null,null));
        expect400(() -> api.listApplicationTargetsForApplicationAdministratorRoleForGroup("G",null, null,null));
    }

    /* listGroupTargetsForGroupRole */
    @Test
    public void testListGroupTargets_Success() throws Exception {
        List<Group> expected = new ArrayList<>();
        stubInvokeList(expected);
        List<Group> actual = api.listGroupTargetsForGroupRole("G9","RA9","CUR", 15);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G9/roles/RA9/targets/groups"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after","CUR");
        verify(apiClient).parameterToPair("limit",15);
    }

    @Test
    public void testListGroupTargets_WithHeaders() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listGroupTargetsForGroupRole("G10","RA10", null, null,
            Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headers.getValue().get("X-G"));
    }

    @Test
    public void testListGroupTargets_MissingParams() {
        expect400(() -> api.listGroupTargetsForGroupRole(null,"R", null,null));
        expect400(() -> api.listGroupTargetsForGroupRole("G",null, null,null));
    }

    /* unassignAppInstanceTargetToAppAdminRoleForGroup */
    @Test
    public void testUnassignAppInstanceTarget_Success() throws Exception {
        api.unassignAppInstanceTargetToAppAdminRoleForGroup("G11","RA11","appZ","Z1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G11/roles/RA11/targets/catalog/apps/appZ/Z1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testUnassignAppInstanceTarget_WithHeaders() throws Exception {
        api.unassignAppInstanceTargetToAppAdminRoleForGroup("G12","RA12","appY","Y1",
            Collections.singletonMap("X-U","v"));
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
    public void testUnassignAppInstanceTarget_MissingParams() {
        expect400(() -> api.unassignAppInstanceTargetToAppAdminRoleForGroup(null,"R","A","ID"));
        expect400(() -> api.unassignAppInstanceTargetToAppAdminRoleForGroup("G",null,"A","ID"));
        expect400(() -> api.unassignAppInstanceTargetToAppAdminRoleForGroup("G","R",null,"ID"));
        expect400(() -> api.unassignAppInstanceTargetToAppAdminRoleForGroup("G","R","A",null));
    }

    /* unassignAppTargetToAdminRoleForGroup */
    @Test
    public void testUnassignAppTarget_Success() throws Exception {
        api.unassignAppTargetToAdminRoleForGroup("G13","RA13","appQ");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G13/roles/RA13/targets/catalog/apps/appQ"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testUnassignAppTarget_WithHeaders() throws Exception {
        api.unassignAppTargetToAdminRoleForGroup("G14","RA14","appW",
            Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", headers.getValue().get("X-R"));
    }

    @Test
    public void testUnassignAppTarget_MissingParams() {
        expect400(() -> api.unassignAppTargetToAdminRoleForGroup(null,"R","A"));
        expect400(() -> api.unassignAppTargetToAdminRoleForGroup("G",null,"A"));
        expect400(() -> api.unassignAppTargetToAdminRoleForGroup("G","R",null));
    }

    /* unassignGroupTargetFromGroupAdminRole */
    @Test
    public void testUnassignGroupTarget_Success() throws Exception {
        api.unassignGroupTargetFromGroupAdminRole("G15","RA15","TG15");
        verify(apiClient).invokeAPI(
            eq("/api/v1/groups/G15/roles/RA15/targets/groups/TG15"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testUnassignGroupTarget_WithHeaders() throws Exception {
        api.unassignGroupTargetFromGroupAdminRole("G16","RA16","TG16",
            Collections.singletonMap("X-H","h"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("h", headers.getValue().get("X-H"));
    }

    @Test
    public void testUnassignGroupTarget_MissingParams() {
        expect400(() -> api.unassignGroupTargetFromGroupAdminRole(null,"R","T"));
        expect400(() -> api.unassignGroupTargetFromGroupAdminRole("G",null,"T"));
        expect400(() -> api.unassignGroupTargetFromGroupAdminRole("G","R",null));
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
            api.listApplicationTargetsForApplicationAdministratorRoleForGroup("G","R", null,null);
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("down"));
        }
    }

    @Test
    public void testApiExceptionPropagates_Unassign() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"svc"));
        try {
            api.unassignAppTargetToAdminRoleForGroup("G","R","A");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("svc"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_ListGroupTargets() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listGroupTargetsForGroupRole("GX","RAX", null,null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/groups/GX/roles/RAX/targets/groups"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_ListAppTargets() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listApplicationTargetsForApplicationAdministratorRoleForGroup("GY","RAY", null,null);
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/groups/GY/roles/RAY/targets/catalog/apps"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RoleBTargetBGroupApi.class.getDeclaredMethod("getObjectMapper");
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

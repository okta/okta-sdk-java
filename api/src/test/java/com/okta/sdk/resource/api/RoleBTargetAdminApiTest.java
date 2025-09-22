package com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CatalogApplication;
import com.okta.sdk.resource.model.Group;
import com.okta.sdk.resource.model.RoleTarget;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleBTargetAdminApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RoleBTargetAdminApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RoleBTargetAdminApi(apiClient);

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
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    /* listApplicationTargetsForApplicationAdministratorRoleForUser */
    @Test
    public void testListApplicationTargets_Success() throws Exception {
        List<CatalogApplication> expected = new ArrayList<>();
        stubInvoke(expected);
        List<CatalogApplication> actual = api.listApplicationTargetsForApplicationAdministratorRoleForUser(
            "U1", "RA1", "CUR", 50, Collections.emptyMap());
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U1/roles/RA1/targets/catalog/apps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after", "CUR");
        verify(apiClient).parameterToPair("limit", 50);
    }

    @Test
    public void testListApplicationTargets_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        Map<String,String> hdrs = Collections.singletonMap("X-H","v");
        api.listApplicationTargetsForApplicationAdministratorRoleForUser("U2","RA2", null, null, hdrs);
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
    public void testListApplicationTargets_MissingParams() {
        try {
            api.listApplicationTargetsForApplicationAdministratorRoleForUser(null,"R", null,null, Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e){ assertEquals(400, e.getCode()); }
        try {
            api.listApplicationTargetsForApplicationAdministratorRoleForUser("U",null, null,null, Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* listGroupTargetsForRole */
    @Test
    public void testListGroupTargets_Success() throws Exception {
        List<Group> expected = new ArrayList<>();
        stubInvoke(expected);
        List<Group> actual = api.listGroupTargetsForRole("U3","RA3","AF", 25, Collections.emptyMap());
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U3/roles/RA3/targets/groups"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair("after", "AF");
        verify(apiClient).parameterToPair("limit", 25);
    }

    @Test
    public void testListGroupTargets_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listGroupTargetsForRole("U4","RA4", null, null, Collections.singletonMap("X-G","1"));
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
        try { api.listGroupTargetsForRole(null,"R", null,null, Collections.emptyMap()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.listGroupTargetsForRole("U",null, null,null, Collections.emptyMap()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* unassignAppInstanceTargetFromAdminRoleForUser */
    @Test
    public void testUnassignAppInstanceTarget_Success() throws Exception {
        stubInvoke(null);
        api.unassignAppInstanceTargetFromAdminRoleForUser("U5","RA5","appA","AID",
            Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U5/roles/RA5/targets/catalog/apps/appA/AID"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headers.getValue().get("X-D"));
    }

    @Test
    public void testUnassignAppInstanceTarget_MissingParams() {
        try { api.unassignAppInstanceTargetFromAdminRoleForUser(null,"R","A","ID"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignAppInstanceTargetFromAdminRoleForUser("U",null,"A","ID"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignAppInstanceTargetFromAdminRoleForUser("U","R",null,"ID"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignAppInstanceTargetFromAdminRoleForUser("U","R","A",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* unassignAppTargetFromAppAdminRoleForUser */
    @Test
    public void testUnassignAppTarget_Success() throws Exception {
        stubInvoke(null);
        api.unassignAppTargetFromAppAdminRoleForUser("U6","RA6","appB",
            Collections.singletonMap("X-U","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U6/roles/RA6/targets/catalog/apps/appB"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", headers.getValue().get("X-U"));
    }

    @Test
    public void testUnassignAppTarget_MissingParams() {
        try { api.unassignAppTargetFromAppAdminRoleForUser(null,"R","A"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignAppTargetFromAppAdminRoleForUser("U",null,"A"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignAppTargetFromAppAdminRoleForUser("U","R",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* unassignGroupTargetFromUserAdminRole */
    @Test
    public void testUnassignGroupTarget_Success() throws Exception {
        stubInvoke(null);
        api.unassignGroupTargetFromUserAdminRole("U7","RA7","G1",
            Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/U7/roles/RA7/targets/groups/G1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headers.getValue().get("X-H"));
    }

    @Test
    public void testUnassignGroupTarget_MissingParams() {
        try { api.unassignGroupTargetFromUserAdminRole(null,"R","G"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignGroupTargetFromUserAdminRole("U",null,"G"); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.unassignGroupTargetFromUserAdminRole("U","R",null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
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
            api.listApplicationTargetsForApplicationAdministratorRoleForUser("U","R", null,null, Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("down"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_ListApps() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listApplicationTargetsForApplicationAdministratorRoleForUser("U8","RA8", null,null, Collections.emptyMap());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/users/U8/roles/RA8/targets/catalog/apps"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_ListGroups() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listGroupTargetsForRole("U9","RA9", null,null, Collections.emptyMap());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/users/U9/roles/RA9/targets/groups"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RoleBTargetAdminApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

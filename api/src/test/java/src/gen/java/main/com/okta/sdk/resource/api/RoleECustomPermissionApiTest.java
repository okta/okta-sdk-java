package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.RoleECustomPermissionApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CreateUpdateIamRolePermissionRequest;
import com.okta.sdk.resource.model.Permission;
import com.okta.sdk.resource.model.Permissions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RoleECustomPermissionApiTest {

    private ApiClient apiClient;
    private RoleECustomPermissionApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RoleECustomPermissionApi(apiClient);

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

    /* createRolePermission */
    @Test
    public void testCreateRolePermission_Success() throws Exception {
        stubVoidInvoke();
        CreateUpdateIamRolePermissionRequest body = new CreateUpdateIamRolePermissionRequest();
        api.createRolePermission("role1", "perm1", body);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/role1/permissions/perm1"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("role1");
        verify(apiClient).escapeString("perm1");
    }

    @Test
    public void testCreateRolePermission_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.createRolePermission("role2", "perm2", new CreateUpdateIamRolePermissionRequest(), Collections.singletonMap("X-Test", "true"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    @Test
    public void testCreateRolePermission_MissingRoleId() {
        try {
            api.createRolePermission(null, "perm3", new CreateUpdateIamRolePermissionRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("roleIdOrLabel"));
        }
    }

    @Test
    public void testCreateRolePermission_MissingPermissionType() {
        try {
            api.createRolePermission("role4", null, new CreateUpdateIamRolePermissionRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("permissionType"));
        }
    }

    /* deleteRolePermission */
    @Test
    public void testDeleteRolePermission_Success() throws Exception {
        stubVoidInvoke();
        api.deleteRolePermission("role5", "perm5");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/role5/permissions/perm5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("role5");
        verify(apiClient).escapeString("perm5");
    }

    @Test
    public void testDeleteRolePermission_MissingRoleId() {
        try {
            api.deleteRolePermission(null, "permX");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("roleIdOrLabel"));
        }
    }

    @Test
    public void testDeleteRolePermission_MissingPermissionType() {
        try {
            api.deleteRolePermission("role6", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("permissionType"));
        }
    }

    /* getRolePermission */
    @Test
    public void testGetRolePermission_Success() throws Exception {
        Permission expected = new Permission();
        stubInvoke(expected);
        Permission actual = api.getRolePermission("role7", "perm7");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/role7/permissions/perm7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("role7");
        verify(apiClient).escapeString("perm7");
    }

    /* listRolePermissions */
    @Test
    public void testListRolePermissions_Success() throws Exception {
        Permissions expected = new Permissions();
        stubInvoke(expected);
        Permissions actual = api.listRolePermissions("role8");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/role8/permissions"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("role8");
    }

    @Test
    public void testListRolePermissions_MissingRoleId() {
        try {
            api.listRolePermissions(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("roleIdOrLabel"));
        }
    }

    /* replaceRolePermission */
    @Test
    public void testReplaceRolePermission_Success() throws Exception {
        Permission expected = new Permission();
        stubInvoke(expected);
        CreateUpdateIamRolePermissionRequest body = new CreateUpdateIamRolePermissionRequest();
        Permission actual = api.replaceRolePermission("role9", "perm9", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/roles/role9/permissions/perm9"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("role9");
        verify(apiClient).escapeString("perm9");
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500, "Internal Server Error"));
        try {
            api.getRolePermission("role-err", "perm-err");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertEquals("Internal Server Error", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RoleECustomPermissionApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
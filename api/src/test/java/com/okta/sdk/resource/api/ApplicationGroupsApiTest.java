package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ApplicationGroupAssignment;
import com.okta.sdk.resource.model.JsonPatchOperation;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationGroupsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationGroupsApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(inv.getArgument(0), String.valueOf(v)));
        });

        api = new com.okta.sdk.resource.api.ApplicationGroupsApi(apiClient);
    }

    // assignGroupToApplication success with body
    @Test
    @SuppressWarnings("unchecked")
    public void testAssignGroupToApplication_success_withBody() throws ApiException {
        ApplicationGroupAssignment body = new ApplicationGroupAssignment();
        ApplicationGroupAssignment returned = new ApplicationGroupAssignment();

        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        ApplicationGroupAssignment out = api.assignGroupToApplication("app1", "g1", body);
        assertNotNull(out);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("PUT"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app1/groups/g1");
        assertSame(bodyCap.getValue(), body);
    }

    // assignGroupToApplication success without body
    @Test
    @SuppressWarnings("unchecked")
    public void testAssignGroupToApplication_success_noBody() throws ApiException {
        ApplicationGroupAssignment returned = new ApplicationGroupAssignment();

        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        api.assignGroupToApplication("app2", "g2", null);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertNull(bodyCap.getValue());
    }

    // assignGroupToApplication with additional headers
    @Test
    @SuppressWarnings("unchecked")
    public void testAssignGroupToApplication_withHeaders() throws ApiException {
        ApplicationGroupAssignment body = new ApplicationGroupAssignment();
        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new ApplicationGroupAssignment());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","abc");
        api.assignGroupToApplication("appH", "gH", body, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Test"), "abc");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testAssignGroupToApplication_missingAppId() throws ApiException {
        api.assignGroupToApplication(null, "g1", null);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testAssignGroupToApplication_missingGroupId() throws ApiException {
        api.assignGroupToApplication("app1", null, null);
    }

    // getApplicationGroupAssignment success with expand
    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplicationGroupAssignment_withExpand() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new ApplicationGroupAssignment());

        api.getApplicationGroupAssignment("app3", "g3", "group");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), qCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app3/groups/g3");
        assertTrue(hasPair(qCap.getValue(), "expand", "group"));
    }

    // getApplicationGroupAssignment no expand
    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplicationGroupAssignment_noExpand() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new ApplicationGroupAssignment());

        api.getApplicationGroupAssignment("app4", "g4", null);

        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), qCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(qCap.getValue().isEmpty());
    }

    // getApplicationGroupAssignment with headers
    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplicationGroupAssignment_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new ApplicationGroupAssignment());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","val");
        api.getApplicationGroupAssignment("app5", "g5", null, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Test"), "val");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApplicationGroupAssignment_missingAppId() throws ApiException {
        api.getApplicationGroupAssignment(null, "g1", null);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApplicationGroupAssignment_missingGroupId() throws ApiException {
        api.getApplicationGroupAssignment("app1", null, null);
    }

    // listApplicationGroupAssignments with all query params
    @Test
    @SuppressWarnings("unchecked")
    public void testListApplicationGroupAssignments_withQuery() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new ApplicationGroupAssignment()));

        List<ApplicationGroupAssignment> list = api.listApplicationGroupAssignments("app6","sa","cursor",42,"group");
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), qCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app6/groups");
        assertTrue(hasPair(qCap.getValue(), "q", "sa"));
        assertTrue(hasPair(qCap.getValue(), "after", "cursor"));
        assertTrue(hasPair(qCap.getValue(), "limit", "42"));
        assertTrue(hasPair(qCap.getValue(), "expand", "group"));
    }

    // listApplicationGroupAssignments minimal
    @Test
    @SuppressWarnings("unchecked")
    public void testListApplicationGroupAssignments_minimal() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        List<ApplicationGroupAssignment> list = api.listApplicationGroupAssignments("app7", null, null, null, null);
        assertTrue(list.isEmpty());

        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), qCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(qCap.getValue().isEmpty());
    }

    // listApplicationGroupAssignments with headers
    @Test
    @SuppressWarnings("unchecked")
    public void testListApplicationGroupAssignments_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-List","yes");
        api.listApplicationGroupAssignments("app8", null, null, null, null, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-List"), "yes");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListApplicationGroupAssignments_missingAppId() throws ApiException {
        api.listApplicationGroupAssignments(null, null, null, null, null);
    }

    // unassignApplicationFromGroup success
    @Test
    public void testUnassignApplicationFromGroup_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.unassignApplicationFromGroup("app9","g9");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app9/groups/g9");
    }

    // unassign with headers
    @Test
    public void testUnassignApplicationFromGroup_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Del","1");
        api.unassignApplicationFromGroup("app10","g10", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(headerCap.getValue().get("X-Del"), "1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUnassignApplicationFromGroup_missingAppId() throws ApiException {
        api.unassignApplicationFromGroup(null, "g1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUnassignApplicationFromGroup_missingGroupId() throws ApiException {
        api.unassignApplicationFromGroup("app1", null);
    }

    // updateGroupAssignmentToApplication success
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateGroupAssignmentToApplication_success() throws ApiException {
        List<JsonPatchOperation> patch = new ArrayList<>();
        ApplicationGroupAssignment updated = new ApplicationGroupAssignment();

        when(apiClient.invokeAPI(anyString(), eq("PATCH"), anyList(), anyList(), anyString(), eq(patch),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(updated);

        ApplicationGroupAssignment out = api.updateGroupAssignmentToApplication("app11","g11", patch);
        assertNotNull(out);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("PATCH"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app11/groups/g11");
        assertSame(bodyCap.getValue(), patch);
    }

    // update with headers
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateGroupAssignmentToApplication_withHeaders() throws ApiException {
        List<JsonPatchOperation> patch = new ArrayList<>();
        when(apiClient.invokeAPI(anyString(), eq("PATCH"), anyList(), anyList(), anyString(), eq(patch),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new ApplicationGroupAssignment());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Upd","u");
        api.updateGroupAssignmentToApplication("app12","g12", patch, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PATCH"), anyList(), anyList(), anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Upd"), "u");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateGroupAssignmentToApplication_missingAppId() throws ApiException {
        api.updateGroupAssignmentToApplication(null, "g1", Collections.emptyList());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateGroupAssignmentToApplication_missingGroupId() throws ApiException {
        api.updateGroupAssignmentToApplication("app1", null, Collections.emptyList());
    }

    // helper
    private boolean hasPair(List<?> list, String name, String value) {
        for (Object o : list) {
            Pair p = (Pair) o;
            if (name.equals(p.getName()) && value.equals(String.valueOf(p.getValue()))) return true;
        }
        return false;
    }
}
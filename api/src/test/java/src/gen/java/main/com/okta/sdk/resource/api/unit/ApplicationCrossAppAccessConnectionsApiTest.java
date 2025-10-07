package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OrgCrossAppAccessConnection;
import com.okta.sdk.resource.model.OrgCrossAppAccessConnectionPatchRequest;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationCrossAppAccessConnectionsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationCrossAppAccessConnectionsApi api;

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

        api = new com.okta.sdk.resource.api.ApplicationCrossAppAccessConnectionsApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateCrossAppAccessConnection_success() throws ApiException {
        OrgCrossAppAccessConnection body = new OrgCrossAppAccessConnection();
        OrgCrossAppAccessConnection returned = new OrgCrossAppAccessConnection();
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        OrgCrossAppAccessConnection result = api.createCrossAppAccessConnection("app1", body);
        assertNotNull(result);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app1/cwo/connections");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCreateCrossAppAccessConnection_missingAppId() throws ApiException {
        api.createCrossAppAccessConnection(null, new OrgCrossAppAccessConnection());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCreateCrossAppAccessConnection_missingBody() throws ApiException {
        api.createCrossAppAccessConnection("app1", null);
    }

    @Test
    public void testDeleteCrossAppAccessConnection_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.deleteCrossAppAccessConnection("app2", "connA");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/api/v1/apps/app2/cwo/connections/connA");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeleteCrossAppAccessConnection_missingAppId() throws ApiException {
        api.deleteCrossAppAccessConnection(null, "c1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeleteCrossAppAccessConnection_missingConnectionId() throws ApiException {
        api.deleteCrossAppAccessConnection("app2", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllCrossAppAccessConnections_withQueryParams() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new OrgCrossAppAccessConnection()));

        List<OrgCrossAppAccessConnection> list = api.getAllCrossAppAccessConnections("app3", "cursor123", 50);
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qParams = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), qParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app3/cwo/connections");
        assertTrue(hasPair(qParams.getValue(), "after", "cursor123"));
        assertTrue(hasPair(qParams.getValue(), "limit", "50"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllCrossAppAccessConnections_noOptionalParams() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        List<OrgCrossAppAccessConnection> list = api.getAllCrossAppAccessConnections("app4", null, null);
        assertTrue(list.isEmpty());

        ArgumentCaptor<List> qParams = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), qParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(qParams.getValue().isEmpty());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetAllCrossAppAccessConnections_missingAppId() throws ApiException {
        api.getAllCrossAppAccessConnections(null, null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCrossAppAccessConnection_success() throws ApiException {
        OrgCrossAppAccessConnection conn = new OrgCrossAppAccessConnection();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(conn);

        OrgCrossAppAccessConnection result = api.getCrossAppAccessConnection("app5", "conn5");
        assertNotNull(result);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app5/cwo/connections/conn5");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetCrossAppAccessConnection_missingAppId() throws ApiException {
        api.getCrossAppAccessConnection(null, "c1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetCrossAppAccessConnection_missingConnectionId() throws ApiException {
        api.getCrossAppAccessConnection("app5", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateCrossAppAccessConnection_success() throws ApiException {
        OrgCrossAppAccessConnectionPatchRequest patch = new OrgCrossAppAccessConnectionPatchRequest();
        OrgCrossAppAccessConnection updated = new OrgCrossAppAccessConnection();
        when(apiClient.invokeAPI(anyString(), eq("PATCH"), anyList(), anyList(), anyString(), eq(patch),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(updated);

        OrgCrossAppAccessConnection result = api.updateCrossAppAccessConnection("app6", "conn6", patch);
        assertNotNull(result);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("PATCH"), anyList(), anyList(), anyString(), eq(patch),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app6/cwo/connections/conn6");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateCrossAppAccessConnection_missingAppId() throws ApiException {
        api.updateCrossAppAccessConnection(null, "c1", new OrgCrossAppAccessConnectionPatchRequest());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateCrossAppAccessConnection_missingConnectionId() throws ApiException {
        api.updateCrossAppAccessConnection("app6", null, new OrgCrossAppAccessConnectionPatchRequest());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateCrossAppAccessConnection_missingBody() throws ApiException {
        api.updateCrossAppAccessConnection("app6", "c1", null);
    }

    private boolean hasPair(List<?> list, String name, String value) {
        for (Object o : list) {
            Pair p = (Pair) o;
            if (name.equals(p.getName()) && value.equals(String.valueOf(p.getValue()))) return true;
        }
        return false;
    }
}

package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.Application;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");

        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            Object val = inv.getArgument(1);
            if (val == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(inv.getArgument(0), String.valueOf(val)));
        });

        api = new com.okta.sdk.resource.api.ApplicationApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplication_withExpand() throws ApiException {
        Application app = new Application();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(app);

        Application result = api.getApplication("app123", "user/abc");
        assertNotNull(result);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> queryCap = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), queryCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app123");
        assertTrue(containsPair(queryCap.getValue(), "expand", "user/abc"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApplication_nullAppId() throws ApiException {
        api.getApplication(null, null);
    }

    @Test
    public void testDeleteApplication_success() throws ApiException {
        api.deleteApplication("appXYZ");
        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(pathCap.getValue(), "/api/v1/apps/appXYZ");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeleteApplication_nullAppId() throws ApiException {
        api.deleteApplication(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListApplications_withParams() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new Application()));

        List<Application> apps = api.listApplications("crm", "cursor1", true, 50,
            "status eq \"ACTIVE\"", "user/uid123", true);
        assertEquals(apps.size(), 1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> queryCap = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), queryCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps");
        List<Pair> q = queryCap.getValue();
        assertTrue(containsPair(q, "q", "crm"));
        assertTrue(containsPair(q, "after", "cursor1"));
        assertTrue(containsPair(q, "useOptimization", "true"));
        assertTrue(containsPair(q, "limit", "50"));
        assertTrue(containsPair(q, "filter", "status eq \"ACTIVE\""));
        assertTrue(containsPair(q, "expand", "user/uid123"));
        assertTrue(containsPair(q, "includeNonDeleted", "true"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListApplications_allNullParams() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        List<Application> apps = api.listApplications(null, null, null, null, null, null, null);
        assertTrue(apps.isEmpty());

        ArgumentCaptor<List> queryCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), queryCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        @SuppressWarnings("rawtypes")
        List list = queryCap.getValue();
        // Expect no pairs because all inputs null (our parameterToPair returns empty list for null)
        assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReplaceApplication_success() throws ApiException {
        Application body = new Application();
        Application returned = new Application();
        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        Application result = api.replaceApplication("app999", body);
        assertNotNull(result);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("PUT"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app999");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testReplaceApplication_missingAppId() throws ApiException {
        api.replaceApplication(null, new Application());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testReplaceApplication_missingBody() throws ApiException {
        api.replaceApplication("app111", null);
    }

    private boolean containsPair(List<?> list, String name, String value) {
        for (Object o : list) {
            Pair p = (Pair) o;
            if (name.equals(p.getName()) && value.equals(String.valueOf(p.getValue()))) {
                return true;
            }
        }
        return false;
    }
}

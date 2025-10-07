package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.okta.sdk.resource.api.ApiServiceIntegrationsApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.APIServiceIntegrationInstance;
import com.okta.sdk.resource.model.APIServiceIntegrationInstanceSecret;
import com.okta.sdk.resource.model.PostAPIServiceIntegrationInstance;
import com.okta.sdk.resource.model.PostAPIServiceIntegrationInstanceRequest;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApiServiceIntegrationsApiTest {

    private ApiClient apiClient;
    private ApiServiceIntegrationsApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(i -> {
            Object v = i.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(i.getArgument(0), v.toString()));
        });

        api = new ApiServiceIntegrationsApi(apiClient);
    }

    @Test
    public void testActivateSecret_buildsCorrectPathAndPOST() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(new APIServiceIntegrationInstanceSecret());

        APIServiceIntegrationInstanceSecret r = api.activateApiServiceIntegrationInstanceSecret("api1", "sec1");
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(path.getValue().contains("/integrations/api/v1/api-services/api1/credentials/secrets/sec1/lifecycle/activate"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testActivateSecret_nullApiServiceId() throws ApiException {
        api.activateApiServiceIntegrationInstanceSecret(null, "sec1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testActivateSecret_nullSecretId() throws ApiException {
        api.activateApiServiceIntegrationInstanceSecret("api1", null);
    }

    @Test
    public void testCreateInstance_buildsCorrectPathPOST() throws ApiException {
        PostAPIServiceIntegrationInstanceRequest req = new PostAPIServiceIntegrationInstanceRequest();
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(req),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(new PostAPIServiceIntegrationInstance());

        PostAPIServiceIntegrationInstance r = api.createApiServiceIntegrationInstance(req);
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), eq(req),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/integrations/api/v1/api-services");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCreateInstance_nullRequest() throws ApiException {
        api.createApiServiceIntegrationInstance(null);
    }

    @Test
    public void testCreateSecret_buildsCorrectPathPOST() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(new APIServiceIntegrationInstanceSecret());

        APIServiceIntegrationInstanceSecret r = api.createApiServiceIntegrationInstanceSecret("api1");
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(path.getValue().endsWith("/integrations/api/v1/api-services/api1/credentials/secrets"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCreateSecret_nullApiServiceId() throws ApiException {
        api.createApiServiceIntegrationInstanceSecret(null);
    }

    @Test
    public void testDeactivateSecret_buildsCorrectPathPOST() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(new APIServiceIntegrationInstanceSecret());

        APIServiceIntegrationInstanceSecret r = api.deactivateApiServiceIntegrationInstanceSecret("api1", "sec2");
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(path.getValue().contains("/lifecycle/deactivate"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeactivateSecret_nullSecretId() throws ApiException {
        api.deactivateApiServiceIntegrationInstanceSecret("api1", null);
    }

    @Test
    public void testDeleteInstance_buildsCorrectPathDELETE() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.deleteApiServiceIntegrationInstance("api1");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/integrations/api/v1/api-services/api1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeleteInstance_nullApiServiceId() throws ApiException {
        api.deleteApiServiceIntegrationInstance(null);
    }

    @Test
    public void testDeleteSecret_buildsCorrectPathDELETE() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.deleteApiServiceIntegrationInstanceSecret("api1", "sec9");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertTrue(path.getValue().contains("/credentials/secrets/sec9"));
    }

    @Test
    public void testGetInstance_buildsCorrectPathGET() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(new APIServiceIntegrationInstance());

        APIServiceIntegrationInstance r = api.getApiServiceIntegrationInstance("api1");
        assertNotNull(r);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/integrations/api/v1/api-services/api1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetInstance_nullApiServiceId() throws ApiException {
        api.getApiServiceIntegrationInstance(null);
    }

    @Test
    public void testListSecrets_buildsCorrectPathGET() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(Collections.singletonList(new APIServiceIntegrationInstanceSecret()));

        List<APIServiceIntegrationInstanceSecret> list = api.listApiServiceIntegrationInstanceSecrets("api1");
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertTrue(path.getValue().endsWith("/integrations/api/v1/api-services/api1/credentials/secrets"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListSecrets_nullApiServiceId() throws ApiException {
        api.listApiServiceIntegrationInstanceSecrets(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListInstances_withAfterParam() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(Collections.singletonList(new APIServiceIntegrationInstance()));

        List<APIServiceIntegrationInstance> list = api.listApiServiceIntegrationInstances("cursor123");
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> queryParams = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(path.capture(), eq("GET"), queryParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(path.getValue(), "/integrations/api/v1/api-services");

        boolean hasAfter = false;
        for (Object o : queryParams.getValue()) {
            Pair p = (Pair) o;
            if ("after".equals(p.getName()) && "cursor123".equals(p.getValue())) {
                hasAfter = true;
                break;
            }
        }
        assertTrue(hasAfter, "Expected 'after=cursor123' in query params");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListInstances_nullAfter() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any()))
            .thenReturn(Collections.emptyList());

        List<APIServiceIntegrationInstance> list = api.listApiServiceIntegrationInstances(null);
        assertTrue(list.isEmpty());

        ArgumentCaptor<List> queryParams = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), queryParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        boolean hasAfter = false;
        for (Object o : queryParams.getValue()) {
            Pair p = (Pair) o;
            if ("after".equals(p.getName())) {
                hasAfter = true;
                break;
            }
        }
        assertFalse(hasAfter, "Did not expect 'after' query param when null");
    }
}

package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OAuth2ScopeConsentGrant;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationGrantsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationGrantsApi api;

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

        api = new com.okta.sdk.resource.api.ApplicationGrantsApi(apiClient);
    }

    // getScopeConsentGrant success with expand
    @Test
    @SuppressWarnings("unchecked")
    public void testGetScopeConsentGrant_success_withExpand() throws ApiException {
        OAuth2ScopeConsentGrant grant = new OAuth2ScopeConsentGrant();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(grant);

        OAuth2ScopeConsentGrant out = api.getScopeConsentGrant("app1", "g1", "scope");
        assertNotNull(out);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qParamsCap = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), qParamsCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app1/grants/g1");
        assertTrue(hasPair(qParamsCap.getValue(), "expand", "scope"));
    }

    // getScopeConsentGrant no expand
    @Test
    @SuppressWarnings("unchecked")
    public void testGetScopeConsentGrant_noExpand() throws ApiException {
        OAuth2ScopeConsentGrant grant = new OAuth2ScopeConsentGrant();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(grant);

        api.getScopeConsentGrant("app2", "g2", null);

        ArgumentCaptor<List> qParamsCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), qParamsCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertTrue(qParamsCap.getValue().isEmpty());
    }

    // getScopeConsentGrant with additional headers
    @Test
    @SuppressWarnings("unchecked")
    public void testGetScopeConsentGrant_additionalHeaders() throws ApiException {
        OAuth2ScopeConsentGrant grant = new OAuth2ScopeConsentGrant();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(grant);

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test", "val");
        api.getScopeConsentGrant("appH", "gH", null, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(headerCap.getValue().get("X-Test"), "val");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetScopeConsentGrant_missingAppId() throws ApiException {
        api.getScopeConsentGrant(null, "g1", null);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetScopeConsentGrant_missingGrantId() throws ApiException {
        api.getScopeConsentGrant("app1", null, null);
    }

    // grantConsentToScope success
    @Test
    @SuppressWarnings("unchecked")
    public void testGrantConsentToScope_success() throws ApiException {
        OAuth2ScopeConsentGrant body = new OAuth2ScopeConsentGrant();
        OAuth2ScopeConsentGrant returned = new OAuth2ScopeConsentGrant();

        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        OAuth2ScopeConsentGrant out = api.grantConsentToScope("app3", body);
        assertNotNull(out);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app3/grants");
        assertSame(bodyCap.getValue(), body);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGrantConsentToScope_missingAppId() throws ApiException {
        api.grantConsentToScope(null, new OAuth2ScopeConsentGrant());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGrantConsentToScope_missingBody() throws ApiException {
        api.grantConsentToScope("app3", null);
    }

    // listScopeConsentGrants success with expand
    @Test
    @SuppressWarnings("unchecked")
    public void testListScopeConsentGrants_withExpand() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new OAuth2ScopeConsentGrant()));

        List<OAuth2ScopeConsentGrant> list = api.listScopeConsentGrants("app4", "scope");
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qParamsCap = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), qParamsCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app4/grants");
        assertTrue(hasPair(qParamsCap.getValue(), "expand", "scope"));
    }

    // listScopeConsentGrants no expand
    @Test
    @SuppressWarnings("unchecked")
    public void testListScopeConsentGrants_noExpand() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        List<OAuth2ScopeConsentGrant> list = api.listScopeConsentGrants("app5", null);
        assertTrue(list.isEmpty());

        ArgumentCaptor<List> qParamsCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), qParamsCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertTrue(qParamsCap.getValue().isEmpty());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListScopeConsentGrants_missingAppId() throws ApiException {
        api.listScopeConsentGrants(null, null);
    }

    // revokeScopeConsentGrant success
    @Test
    public void testRevokeScopeConsentGrant_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.revokeScopeConsentGrant("app6", "g6");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app6/grants/g6");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRevokeScopeConsentGrant_missingAppId() throws ApiException {
        api.revokeScopeConsentGrant(null, "g1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRevokeScopeConsentGrant_missingGrantId() throws ApiException {
        api.revokeScopeConsentGrant("app6", null);
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

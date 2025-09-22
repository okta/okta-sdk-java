package com.okta.sdk.resource.api;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ApiToken;
import com.okta.sdk.resource.model.ApiTokenUpdate;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApiTokenApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApiTokenApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");

        api = new com.okta.sdk.resource.api.ApiTokenApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetApiToken_success() throws ApiException {
        ApiToken token = new ApiToken();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(token);

        ApiToken result = api.getApiToken("tok123");
        assertNotNull(result);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/api-tokens/tok123");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApiToken_missingId() throws ApiException {
        api.getApiToken(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListApiTokens_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new ApiToken()));

        List<ApiToken> list = api.listApiTokens();
        assertEquals(list.size(), 1);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/api-tokens");
    }

    @Test
    public void testRevokeApiToken_success() throws ApiException {
        api.revokeApiToken("tokABC");
        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/api/v1/api-tokens/tokABC");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRevokeApiToken_missingId() throws ApiException {
        api.revokeApiToken(null);
    }

    @Test
    public void testRevokeCurrentApiToken_success() throws ApiException {
        api.revokeCurrentApiToken();
        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/api/v1/api-tokens/current");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpsertApiToken_success() throws ApiException {
        ApiTokenUpdate update = new ApiTokenUpdate();
        ApiToken returned = new ApiToken();
        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), eq(update),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(returned);

        ApiToken result = api.upsertApiToken("id789", update);
        assertNotNull(result);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("PUT"), anyList(), anyList(), anyString(), eq(update),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/api-tokens/id789");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpsertApiToken_missingId() throws ApiException {
        api.upsertApiToken(null, new ApiTokenUpdate());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpsertApiToken_missingUpdate() throws ApiException {
        api.upsertApiToken("id999", null);
    }
}

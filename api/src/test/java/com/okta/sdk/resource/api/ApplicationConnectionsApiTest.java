package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AppConnectionUserProvisionJWKResponse;
import com.okta.sdk.resource.model.OAuthProvisioningEnabledApp;
import com.okta.sdk.resource.model.ProvisioningConnectionResponse;
import com.okta.sdk.resource.model.UpdateDefaultProvisioningConnectionForApplicationRequest;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationConnectionsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationConnectionsApi api;

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

        api = new com.okta.sdk.resource.api.ApplicationConnectionsApi(apiClient);
    }

    // activate
    @Test
    public void testActivateDefaultProvisioningConnection_pathAndMethod() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.activateDefaultProvisioningConnectionForApplication("app1");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/api/v1/apps/app1/connections/default/lifecycle/activate");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testActivateDefaultProvisioningConnection_missingAppId() throws ApiException {
        api.activateDefaultProvisioningConnectionForApplication(null);
    }

    // deactivate
    @Test
    public void testDeactivateDefaultProvisioningConnection_pathAndMethod() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.deactivateDefaultProvisioningConnectionForApplication("app2");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(path.getValue(), "/api/v1/apps/app2/connections/default/lifecycle/deactivate");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testDeactivateDefaultProvisioningConnection_missingAppId() throws ApiException {
        api.deactivateDefaultProvisioningConnectionForApplication(null);
    }

    // get default connection
    @Test
    @SuppressWarnings("unchecked")
    public void testGetDefaultProvisioningConnection_success() throws ApiException {
        ProvisioningConnectionResponse resp = new ProvisioningConnectionResponse();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(resp);

        ProvisioningConnectionResponse out = api.getDefaultProvisioningConnectionForApplication("app3");
        assertNotNull(out);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app3/connections/default");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetDefaultProvisioningConnection_missingAppId() throws ApiException {
        api.getDefaultProvisioningConnectionForApplication(null);
    }

    // JWKS
    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserProvisioningConnectionJWKS_success() throws ApiException {
        AppConnectionUserProvisionJWKResponse resp = new AppConnectionUserProvisionJWKResponse();
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(resp);

        AppConnectionUserProvisionJWKResponse out = api.getUserProvisioningConnectionJWKS("app4");
        assertNotNull(out);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(path.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(path.getValue(), "/api/v1/apps/app4/connections/default/jwks");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetUserProvisioningConnectionJWKS_missingAppId() throws ApiException {
        api.getUserProvisioningConnectionJWKS(null);
    }

    // update default connection
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDefaultProvisioningConnection_withActivateTrue() throws ApiException {
        ProvisioningConnectionResponse resp = new ProvisioningConnectionResponse();
        UpdateDefaultProvisioningConnectionForApplicationRequest body =
            new UpdateDefaultProvisioningConnectionForApplicationRequest();

        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(resp);

        ProvisioningConnectionResponse out =
            api.updateDefaultProvisioningConnectionForApplication("app5", body, true);
        assertNotNull(out);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> queryParams = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(path.capture(), eq("POST"), queryParams.capture(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertEquals(path.getValue(), "/api/v1/apps/app5/connections/default");
        assertTrue(hasPair(queryParams.getValue(), "activate", "true"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDefaultProvisioningConnection_activateNull() throws ApiException {
        ProvisioningConnectionResponse resp = new ProvisioningConnectionResponse();
        UpdateDefaultProvisioningConnectionForApplicationRequest body =
            new UpdateDefaultProvisioningConnectionForApplicationRequest();

        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(resp);

        api.updateDefaultProvisioningConnectionForApplication("app6", body, null);

        ArgumentCaptor<List> queryParams = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), queryParams.capture(), anyList(), anyString(), eq(body),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());

        assertFalse(hasPair(queryParams.getValue(), "activate", "true"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateDefaultProvisioningConnection_missingAppId() throws ApiException {
        api.updateDefaultProvisioningConnectionForApplication(null,
            new UpdateDefaultProvisioningConnectionForApplicationRequest(), true);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUpdateDefaultProvisioningConnection_missingBody() throws ApiException {
        api.updateDefaultProvisioningConnectionForApplication("app7", null, true);
    }

    // verify provisioning connection (oauth callback)
    @Test
    public void testVerifyProvisioningConnection_pathAndQuery() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.verifyProvisioningConnectionForApplication(OAuthProvisioningEnabledApp.GOOGLE, "app8", "c123", "s456");

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qParams = ArgumentCaptor.forClass(List.class);

        verify(apiClient).invokeAPI(path.capture(), eq("POST"), qParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());

        String expected = "/api/v1/apps/" + OAuthProvisioningEnabledApp.GOOGLE.name().toLowerCase(Locale.ROOT) + "/app8/oauth2/callback";
        assertEquals(path.getValue(), expected);
        assertTrue(hasPair(qParams.getValue(), "code", "c123"));
        assertTrue(hasPair(qParams.getValue(), "state", "s456"));
    }

    @Test
    public void testVerifyProvisioningConnection_nullOptionalParams() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull())).thenReturn(null);

        api.verifyProvisioningConnectionForApplication(OAuthProvisioningEnabledApp.SLACK, "app9", null, null);

        ArgumentCaptor<List> qParams = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), qParams.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());

        assertFalse(hasPair(qParams.getValue(), "code", "null"));
        assertFalse(hasPair(qParams.getValue(), "state", "null"));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testVerifyProvisioningConnection_missingAppName() throws ApiException {
        api.verifyProvisioningConnectionForApplication(null, "app10", null, null);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testVerifyProvisioningConnection_missingAppId() throws ApiException {
        api.verifyProvisioningConnectionForApplication(OAuthProvisioningEnabledApp.ZOOMUS, null, null, null);
    }

    // helpers
    private boolean hasPair(List<?> list, String name, String value) {
        for (Object o : list) {
            Pair p = (Pair) o;
            if (name.equals(p.getName()) && value.equals(String.valueOf(p.getValue()))) return true;
        }
        return false;
    }
}

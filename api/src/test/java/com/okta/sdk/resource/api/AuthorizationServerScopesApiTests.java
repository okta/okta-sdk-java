package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OAuth2Scope;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerScopesApiTests {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthorizationServerScopesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthorizationServerScopesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // createOAuth2Scope
    @Test
    public void testCreateOAuth2Scope_Success() throws Exception {
        OAuth2Scope body = new OAuth2Scope();
        OAuth2Scope expected = new OAuth2Scope();
        stubInvoke(expected);

        OAuth2Scope actual = api.createOAuth2Scope("as1", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", method.getValue());
        assertTrue(path.getValue().endsWith("/api/v1/authorizationServers/as1/scopes"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as1");
    }

    @Test
    public void testCreateOAuth2Scope_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Scope());
        api.createOAuth2Scope("as2", new OAuth2Scope(), Collections.singletonMap("X-C","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateOAuth2Scope_MissingAuthServerId() throws Exception {
        api.createOAuth2Scope(null, new OAuth2Scope());
    }

    @Test(expected = ApiException.class)
    public void testCreateOAuth2Scope_MissingBody() throws Exception {
        api.createOAuth2Scope("as", null);
    }

    // getOAuth2Scope
    @Test
    public void testGetOAuth2Scope_Success() throws Exception {
        OAuth2Scope expected = new OAuth2Scope();
        stubInvoke(expected);

        OAuth2Scope actual = api.getOAuth2Scope("as3","sc3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3/scopes/sc3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("as3");
        verify(apiClient).escapeString("sc3");
    }

    @Test
    public void testGetOAuth2Scope_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Scope());
        api.getOAuth2Scope("as4","sc4", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", headers.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2Scope_MissingAuthServerId() throws Exception {
        api.getOAuth2Scope(null,"sc");
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2Scope_MissingScopeId() throws Exception {
        api.getOAuth2Scope("as",null);
    }

    // listOAuth2Scopes

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }


    @Test
    public void testListOAuth2Scopes_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listOAuth2Scopes("as6", null, null, null, null, Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headers.getValue().get("X-L"));
    }

    @Test(expected = ApiException.class)
    public void testListOAuth2Scopes_MissingAuthServerId() throws Exception {
        api.listOAuth2Scopes(null, null, null, null, null);
    }

    // replaceOAuth2Scope
    @Test
    public void testReplaceOAuth2Scope_Success() throws Exception {
        OAuth2Scope body = new OAuth2Scope();
        OAuth2Scope expected = new OAuth2Scope();
        stubInvoke(expected);

        OAuth2Scope actual = api.replaceOAuth2Scope("as7","sc7", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("PUT", method.getValue());
        assertTrue(path.getValue().endsWith("/api/v1/authorizationServers/as7/scopes/sc7"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as7");
        verify(apiClient).escapeString("sc7");
    }

    @Test
    public void testReplaceOAuth2Scope_WithHeaders() throws Exception {
        stubInvoke(new OAuth2Scope());
        api.replaceOAuth2Scope("as8","sc8", new OAuth2Scope(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", headers.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Scope_MissingAuthServerId() throws Exception {
        api.replaceOAuth2Scope(null,"sc", new OAuth2Scope());
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Scope_MissingScopeId() throws Exception {
        api.replaceOAuth2Scope("as",null, new OAuth2Scope());
    }

    @Test(expected = ApiException.class)
    public void testReplaceOAuth2Scope_MissingBody() throws Exception {
        api.replaceOAuth2Scope("as","sc", null);
    }

    // deleteOAuth2Scope
    @Test
    public void testDeleteOAuth2Scope_Success() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2Scope("as9","sc9");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as9/scopes/sc9"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("as9");
        verify(apiClient).escapeString("sc9");
    }

    @Test
    public void testDeleteOAuth2Scope_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2Scope("as10","sc10", Collections.singletonMap("X-Del","d"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("d", headers.getValue().get("X-Del"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2Scope_MissingAuthServerId() throws Exception {
        api.deleteOAuth2Scope(null,"sc");
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2Scope_MissingScopeId() throws Exception {
        api.deleteOAuth2Scope("as",null);
    }


}

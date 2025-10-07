package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AuthorizationServerPolicy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerPoliciesApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthorizationServerPoliciesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthorizationServerPoliciesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // activateAuthorizationServerPolicy
    @Test
    public void testActivateAuthorizationServerPolicy_Success() throws Exception {
        stubVoidInvoke();
        api.activateAuthorizationServerPolicy("as1", "p1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/policies/p1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        verify(apiClient).escapeString("as1");
        verify(apiClient).escapeString("p1");
    }

    @Test
    public void testActivateAuthorizationServerPolicy_WithHeaders() throws Exception {
        stubVoidInvoke();
        Map<String,String> headers = new HashMap<>();
        headers.put("X-A","1");
        api.activateAuthorizationServerPolicy("as2","p2", headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", hdrCap.getValue().get("X-A"));
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.activateAuthorizationServerPolicy(null, "p");
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServerPolicy_MissingPolicyId() throws Exception {
        api.activateAuthorizationServerPolicy("as", null);
    }

    // createAuthorizationServerPolicy
    @Test
    public void testCreateAuthorizationServerPolicy_Success() throws Exception {
        AuthorizationServerPolicy body = new AuthorizationServerPolicy();
        AuthorizationServerPolicy expected = new AuthorizationServerPolicy();
        stubInvoke(expected);

        AuthorizationServerPolicy actual = api.createAuthorizationServerPolicy("as3", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("POST", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as3/policies"));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as3");
    }

    @Test
    public void testCreateAuthorizationServerPolicy_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicy());
        api.createAuthorizationServerPolicy("as4", new AuthorizationServerPolicy(),
            Collections.singletonMap("X-C","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.createAuthorizationServerPolicy(null, new AuthorizationServerPolicy());
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServerPolicy_MissingBody() throws Exception {
        api.createAuthorizationServerPolicy("as", null);
    }

    // deactivateAuthorizationServerPolicy
    @Test
    public void testDeactivateAuthorizationServerPolicy_Success() throws Exception {
        stubVoidInvoke();
        api.deactivateAuthorizationServerPolicy("as5","p5");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as5/policies/p5/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.deactivateAuthorizationServerPolicy(null,"p");
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServerPolicy_MissingPolicyId() throws Exception {
        api.deactivateAuthorizationServerPolicy("as",null);
    }

    // deleteAuthorizationServerPolicy
    @Test
    public void testDeleteAuthorizationServerPolicy_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAuthorizationServerPolicy("as6","p6");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as6/policies/p6"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.deleteAuthorizationServerPolicy(null,"p");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServerPolicy_MissingPolicyId() throws Exception {
        api.deleteAuthorizationServerPolicy("as",null);
    }

    // getAuthorizationServerPolicy
    @Test
    public void testGetAuthorizationServerPolicy_Success() throws Exception {
        AuthorizationServerPolicy expected = new AuthorizationServerPolicy();
        stubInvoke(expected);

        AuthorizationServerPolicy actual = api.getAuthorizationServerPolicy("as7","p7");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as7/policies/p7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetAuthorizationServerPolicy_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicy());
        api.getAuthorizationServerPolicy("as8","p8", Collections.singletonMap("X-G","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", hdrCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.getAuthorizationServerPolicy(null,"p");
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerPolicy_MissingPolicyId() throws Exception {
        api.getAuthorizationServerPolicy("as",null);
    }

    // listAuthorizationServerPolicies
    @Test
    public void testListAuthorizationServerPolicies_Success() throws Exception {
        List<AuthorizationServerPolicy> expected =
            Arrays.asList(new AuthorizationServerPolicy(), new AuthorizationServerPolicy());
        stubInvoke(expected);

        List<AuthorizationServerPolicy> actual = api.listAuthorizationServerPolicies("as9");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as9/policies"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListAuthorizationServerPolicies_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAuthorizationServerPolicies("as10", Collections.singletonMap("X-L","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("x", hdrCap.getValue().get("X-L"));
    }

    @Test(expected = ApiException.class)
    public void testListAuthorizationServerPolicies_MissingAuthServerId() throws Exception {
        api.listAuthorizationServerPolicies(null);
    }

    // replaceAuthorizationServerPolicy
    @Test
    public void testReplaceAuthorizationServerPolicy_Success() throws Exception {
        AuthorizationServerPolicy body = new AuthorizationServerPolicy();
        AuthorizationServerPolicy expected = new AuthorizationServerPolicy();
        stubInvoke(expected);

        AuthorizationServerPolicy actual = api.replaceAuthorizationServerPolicy("as11","p11", body);
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("PUT", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as11/policies/p11"));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceAuthorizationServerPolicy_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicy());
        api.replaceAuthorizationServerPolicy("as12","p12", new AuthorizationServerPolicy(),
            Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("r", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicy_MissingAuthServerId() throws Exception {
        api.replaceAuthorizationServerPolicy(null,"p", new AuthorizationServerPolicy());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicy_MissingPolicyId() throws Exception {
        api.replaceAuthorizationServerPolicy("as",null, new AuthorizationServerPolicy());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicy_MissingBody() throws Exception {
        api.replaceAuthorizationServerPolicy("as","p", null);
    }

    // Helpers
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
}

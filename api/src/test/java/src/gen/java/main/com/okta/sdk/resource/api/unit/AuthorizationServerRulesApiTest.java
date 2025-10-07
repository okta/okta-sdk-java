package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.AuthorizationServerRulesApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AuthorizationServerPolicyRule;
import com.okta.sdk.resource.model.AuthorizationServerPolicyRuleRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthorizationServerRulesApiTest {

    private ApiClient apiClient;
    private AuthorizationServerRulesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new AuthorizationServerRulesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // activateAuthorizationServerPolicyRule
    @Test
    public void testActivateAuthorizationServerPolicyRule_Success() throws Exception {
        stubVoidInvoke();
        api.activateAuthorizationServerPolicyRule("as1","p1","r1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/policies/p1/rules/r1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("as1");
        verify(apiClient).escapeString("p1");
        verify(apiClient).escapeString("r1");
    }

    @Test
    public void testActivateAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Act","yes");
        api.activateAuthorizationServerPolicyRule("as2","p2","r2", headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("yes", hdrCap.getValue().get("X-Act"));
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.activateAuthorizationServerPolicyRule(null,"p","r");
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.activateAuthorizationServerPolicyRule("as",null,"r");
    }

    @Test(expected = ApiException.class)
    public void testActivateAuthorizationServerPolicyRule_MissingRuleId() throws Exception {
        api.activateAuthorizationServerPolicyRule("as","p",null);
    }

    // createAuthorizationServerPolicyRule
    @Test
    public void testCreateAuthorizationServerPolicyRule_Success() throws Exception {
        AuthorizationServerPolicyRuleRequest body = new AuthorizationServerPolicyRuleRequest();
        AuthorizationServerPolicyRule expected = new AuthorizationServerPolicyRule();
        stubInvoke(expected);

        AuthorizationServerPolicyRule actual =
            api.createAuthorizationServerPolicyRule("as3","p3", body);
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
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as3/policies/p3/rules"));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicyRule());
        api.createAuthorizationServerPolicyRule("as4","p4", new AuthorizationServerPolicyRuleRequest(),
            Collections.singletonMap("X-C","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.createAuthorizationServerPolicyRule(null,"p", new AuthorizationServerPolicyRuleRequest());
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.createAuthorizationServerPolicyRule("as",null, new AuthorizationServerPolicyRuleRequest());
    }

    @Test(expected = ApiException.class)
    public void testCreateAuthorizationServerPolicyRule_MissingBody() throws Exception {
        api.createAuthorizationServerPolicyRule("as","p", null);
    }

    // deactivateAuthorizationServerPolicyRule
    @Test
    public void testDeactivateAuthorizationServerPolicyRule_Success() throws Exception {
        stubVoidInvoke();
        api.deactivateAuthorizationServerPolicyRule("as5","p5","r5");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as5/policies/p5/rules/r5/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeactivateAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deactivateAuthorizationServerPolicyRule("as6","p6","r6",
            Collections.singletonMap("X-D","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.deactivateAuthorizationServerPolicyRule(null,"p","r");
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.deactivateAuthorizationServerPolicyRule("as",null,"r");
    }

    @Test(expected = ApiException.class)
    public void testDeactivateAuthorizationServerPolicyRule_MissingRuleId() throws Exception {
        api.deactivateAuthorizationServerPolicyRule("as","p",null);
    }

    // deleteAuthorizationServerPolicyRule
    @Test
    public void testDeleteAuthorizationServerPolicyRule_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAuthorizationServerPolicyRule("as7","p7","r7");

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as7/policies/p7/rules/r7"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeleteAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteAuthorizationServerPolicyRule("as8","p8","r8",
            Collections.singletonMap("X-Del","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", hdrCap.getValue().get("X-Del"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.deleteAuthorizationServerPolicyRule(null,"p","r");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.deleteAuthorizationServerPolicyRule("as",null,"r");
    }

    @Test(expected = ApiException.class)
    public void testDeleteAuthorizationServerPolicyRule_MissingRuleId() throws Exception {
        api.deleteAuthorizationServerPolicyRule("as","p",null);
    }

    // getAuthorizationServerPolicyRule
    @Test
    public void testGetAuthorizationServerPolicyRule_Success() throws Exception {
        AuthorizationServerPolicyRule expected = new AuthorizationServerPolicyRule();
        stubInvoke(expected);

        AuthorizationServerPolicyRule actual =
            api.getAuthorizationServerPolicyRule("as9","p9","r9");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as9/policies/p9/rules/r9"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicyRule());
        api.getAuthorizationServerPolicyRule("as10","p10","r10",
            Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdrCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.getAuthorizationServerPolicyRule(null,"p","r");
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.getAuthorizationServerPolicyRule("as",null,"r");
    }

    @Test(expected = ApiException.class)
    public void testGetAuthorizationServerPolicyRule_MissingRuleId() throws Exception {
        api.getAuthorizationServerPolicyRule("as","p",null);
    }

    // listAuthorizationServerPolicyRules
    @Test
    public void testListAuthorizationServerPolicyRules_Success() throws Exception {
        List<AuthorizationServerPolicyRule> expected =
            Arrays.asList(new AuthorizationServerPolicyRule(), new AuthorizationServerPolicyRule());
        stubInvoke(expected);

        List<AuthorizationServerPolicyRule> actual =
            api.listAuthorizationServerPolicyRules("as11","p11");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as11/policies/p11/rules"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListAuthorizationServerPolicyRules_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listAuthorizationServerPolicyRules("as12","p12",
            Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-L"));
    }

    @Test(expected = ApiException.class)
    public void testListAuthorizationServerPolicyRules_MissingAuthServerId() throws Exception {
        api.listAuthorizationServerPolicyRules(null,"p");
    }

    @Test(expected = ApiException.class)
    public void testListAuthorizationServerPolicyRules_MissingPolicyId() throws Exception {
        api.listAuthorizationServerPolicyRules("as",null);
    }

    // replaceAuthorizationServerPolicyRule
    @Test
    public void testReplaceAuthorizationServerPolicyRule_Success() throws Exception {
        AuthorizationServerPolicyRuleRequest body = new AuthorizationServerPolicyRuleRequest();
        AuthorizationServerPolicyRule expected = new AuthorizationServerPolicyRule();
        stubInvoke(expected);

        AuthorizationServerPolicyRule actual =
            api.replaceAuthorizationServerPolicyRule("as13","p13","r13", body);
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
            any(String[].class), any(TypeReference.class));
        assertEquals("PUT", methodCap.getValue());
        assertTrue(pathCap.getValue().endsWith("/api/v1/authorizationServers/as13/policies/p13/rules/r13"));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceAuthorizationServerPolicyRule_WithHeaders() throws Exception {
        stubInvoke(new AuthorizationServerPolicyRule());
        api.replaceAuthorizationServerPolicyRule("as14","p14","r14",
            new AuthorizationServerPolicyRuleRequest(),
            Collections.singletonMap("X-R","y"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("y", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicyRule_MissingAuthServerId() throws Exception {
        api.replaceAuthorizationServerPolicyRule(null,"p","r", new AuthorizationServerPolicyRuleRequest());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicyRule_MissingPolicyId() throws Exception {
        api.replaceAuthorizationServerPolicyRule("as",null,"r", new AuthorizationServerPolicyRuleRequest());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicyRule_MissingRuleId() throws Exception {
        api.replaceAuthorizationServerPolicyRule("as","p",null, new AuthorizationServerPolicyRuleRequest());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthorizationServerPolicyRule_MissingBody() throws Exception {
        api.replaceAuthorizationServerPolicyRule("as","p","r", null);
    }

    // Helpers
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
}

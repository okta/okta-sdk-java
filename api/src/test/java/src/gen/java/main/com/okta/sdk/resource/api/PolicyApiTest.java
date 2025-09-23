package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CreateOrUpdatePolicy;
import com.okta.sdk.resource.model.Policy;
import com.okta.sdk.resource.model.PolicyMapping;
import com.okta.sdk.resource.model.PolicyMappingRequest;
import com.okta.sdk.resource.model.PolicyRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class PolicyApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.PolicyApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.PolicyApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

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

    /* getPolicy */
    @Test
    public void testGetPolicy_Success() throws Exception {
        Policy expected = new Policy();
        stubInvoke(expected);
        Policy actual = api.getPolicy("PID123", null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies/PID123"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetPolicy_MissingPolicyId() {
        try {
            api.getPolicy(null, null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("policyid"));
        }
    }

    /* listPolicies */
    @Test
    public void testListPolicies_Success() throws Exception {
        Policy expected = new Policy();
        stubInvoke(expected);
        Policy actual = api.listPolicies("OKTA_SIGN_ON", null, null, null, null, null, null, null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListPolicies_MissingType() {
        try {
            api.listPolicies(null, null, null, null, null, null, null, null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("type"));
        }
    }

    /* deletePolicyResourceMapping */
    @Test
    public void testDeletePolicyResourceMapping_SuccessWithHeaders() throws Exception {
        stubInvoke(null);
        Map<String,String> headers = Collections.singletonMap("X-Req","v");
        api.deletePolicyResourceMapping("PID1","MID1", headers);
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies/PID1/mappings/MID1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headerCap.getValue().get("X-Req"));
    }

    @Test
    public void testDeletePolicyResourceMapping_MissingPolicyId() {
        try {
            api.deletePolicyResourceMapping(null,"MID", Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testDeletePolicyResourceMapping_MissingMappingId() {
        try {
            api.deletePolicyResourceMapping("PID",null, Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* mapResourceToPolicy */
    @Test
    public void testMapResourceToPolicy_Success() throws Exception {
        PolicyMapping expected = new PolicyMapping();
        stubInvoke(expected);
        PolicyMappingRequest body = new PolicyMappingRequest();
        PolicyMapping actual = api.mapResourceToPolicy("PIDZ", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies/PIDZ/mappings"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testMapResourceToPolicy_MissingPolicyId() {
        try {
            api.mapResourceToPolicy(null, new PolicyMappingRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testMapResourceToPolicy_MissingBody() {
        try {
            api.mapResourceToPolicy("PID", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* replacePolicy */
    @Test
    public void testReplacePolicy_Success() throws Exception {
        Policy expected = new Policy();
        stubInvoke(expected);
        CreateOrUpdatePolicy body = new CreateOrUpdatePolicy();
        Policy actual = api.replacePolicy("PX1", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies/PX1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplacePolicy_MissingArgs() {
        try {
            api.replacePolicy(null, new CreateOrUpdatePolicy());
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
        try {
            api.replacePolicy("PID", null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    /* replacePolicyRule */
    @Test
    public void testReplacePolicyRule_Success() throws Exception {
        PolicyRule expected = new PolicyRule();
        stubInvoke(expected);
        PolicyRule body = new PolicyRule();
        PolicyRule actual = api.replacePolicyRule("P1","R1", body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/policies/P1/rules/R1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplacePolicyRule_MissingArgs() {
        try { api.replacePolicyRule(null,"R", new PolicyRule()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.replacePolicyRule("P",null, new PolicyRule()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.replacePolicyRule("P","R", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }

    /* header negotiation */
    @Test
    public void testSelectHeaderAcceptCalledWithPath() throws Exception {
        stubInvoke(new Policy());
        api.getPolicy("PID123", null);
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/policies/PID123"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* object mapper */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.PolicyApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* ApiException passthrough */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.getPolicy("PIDERR", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }
}

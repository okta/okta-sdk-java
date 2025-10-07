package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.BundleEntitlementsResponse;
import com.okta.sdk.resource.model.EntitlementValuesResponse;
import com.okta.sdk.resource.model.GovernanceBundle;
import com.okta.sdk.resource.model.GovernanceBundleCreateRequest;
import com.okta.sdk.resource.model.GovernanceBundleUpdateRequest;
import com.okta.sdk.resource.model.GovernanceBundlesResponse;
import com.okta.sdk.resource.model.OptInStatusResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class GovernanceBundleApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.GovernanceBundleApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.GovernanceBundleApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
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

    /* createGovernanceBundle */
    @Test
    public void testCreateGovernanceBundle_Success() throws Exception {
        GovernanceBundleCreateRequest body = new GovernanceBundleCreateRequest();
        GovernanceBundle expected = new GovernanceBundle();
        stubInvoke(expected);

        GovernanceBundle actual = api.createGovernanceBundle(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/governance/bundles"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateGovernanceBundle_WithHeaders() throws Exception {
        stubInvoke(new GovernanceBundle());
        Map<String,String> hdrs = Collections.singletonMap("X-C","1");
        api.createGovernanceBundle(new GovernanceBundleCreateRequest(), hdrs);

        ArgumentCaptor<Map> h = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            h.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", h.getValue().get("X-C"));
    }

    @Test
    public void testCreateGovernanceBundle_MissingBody() {
        try {
            api.createGovernanceBundle(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* deleteGovernanceBundle */
    @Test
    public void testDeleteGovernanceBundle_Success() throws Exception {
        stubVoidInvoke();
        api.deleteGovernanceBundle("b1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/governance/bundles/b1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("b1");
    }

    @Test
    public void testDeleteGovernanceBundle_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteGovernanceBundle("b2", Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteGovernanceBundle_MissingId() {
        try {
            api.deleteGovernanceBundle(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getGovernanceBundle */
    @Test
    public void testGetGovernanceBundle_Success() throws Exception {
        GovernanceBundle expected = new GovernanceBundle();
        stubInvoke(expected);
        GovernanceBundle actual = api.getGovernanceBundle("b3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/governance/bundles/b3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("b3");
    }

    @Test
    public void testGetGovernanceBundle_WithHeaders() throws Exception {
        stubInvoke(new GovernanceBundle());
        api.getGovernanceBundle("b4", Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetGovernanceBundle_MissingId() {
        try {
            api.getGovernanceBundle(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getOptInStatus */
    @Test
    public void testGetOptInStatus_Success() throws Exception {
        OptInStatusResponse expected = new OptInStatusResponse();
        stubInvoke(expected);
        OptInStatusResponse actual = api.getOptInStatus();
        assertSame(expected, actual);
    }

    @Test
    public void testGetOptInStatus_WithHeaders() throws Exception {
        stubInvoke(new OptInStatusResponse());
        api.getOptInStatus(Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-H"));
    }

    /* listBundleEntitlementValues */
    @Test
    public void testListBundleEntitlementValues_Success_WithAfterLimit() throws Exception {
        EntitlementValuesResponse expected = new EntitlementValuesResponse();
        stubInvoke(expected);
        EntitlementValuesResponse actual = api.listBundleEntitlementValues("b5","e1","cursor",50);
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("after","cursor");
        verify(apiClient).parameterToPair("limit",50);
        verify(apiClient).escapeString("b5");
        verify(apiClient).escapeString("e1");
    }

    @Test
    public void testListBundleEntitlementValues_WithoutOptional() throws Exception {
        stubInvoke(new EntitlementValuesResponse());
        api.listBundleEntitlementValues("b6","e2", null, null);
        verify(apiClient).escapeString("b6");
        verify(apiClient).escapeString("e2");
    }

    @Test
    public void testListBundleEntitlementValues_WithHeaders() throws Exception {
        stubInvoke(new EntitlementValuesResponse());
        api.listBundleEntitlementValues("b7","e3","a",10, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testListBundleEntitlementValues_MissingBundleId() {
        try {
            api.listBundleEntitlementValues(null,"e","a",1);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testListBundleEntitlementValues_MissingEntitlementId() {
        try {
            api.listBundleEntitlementValues("b",null,"a",1);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listBundleEntitlements */
    @Test
    public void testListBundleEntitlements_Success() throws Exception {
        BundleEntitlementsResponse expected = new BundleEntitlementsResponse();
        stubInvoke(expected);
        BundleEntitlementsResponse actual = api.listBundleEntitlements("b8","c1",25);
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("after","c1");
        verify(apiClient).parameterToPair("limit",25);
        verify(apiClient).escapeString("b8");
    }

    @Test
    public void testListBundleEntitlements_WithHeaders() throws Exception {
        stubInvoke(new BundleEntitlementsResponse());
        api.listBundleEntitlements("b9", null, null, Collections.singletonMap("X-H","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", cap.getValue().get("X-H"));
    }

    @Test
    public void testListBundleEntitlements_MissingBundleId() {
        try {
            api.listBundleEntitlements(null, null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listGovernanceBundles */
    @Test
    public void testListGovernanceBundles_Success() throws Exception {
        GovernanceBundlesResponse expected = new GovernanceBundlesResponse();
        stubInvoke(expected);
        GovernanceBundlesResponse actual = api.listGovernanceBundles(null, null);
        assertSame(expected, actual);
    }

    @Test
    public void testListGovernanceBundles_WithAfterLimit() throws Exception {
        stubInvoke(new GovernanceBundlesResponse());
        api.listGovernanceBundles("c2", 99);
        verify(apiClient).parameterToPair("after","c2");
        verify(apiClient).parameterToPair("limit",99);
    }

    @Test
    public void testListGovernanceBundles_WithHeaders() throws Exception {
        stubInvoke(new GovernanceBundlesResponse());
        api.listGovernanceBundles("c3", 10, Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", cap.getValue().get("X-G"));
    }

    /* optIn / optOut */
    @Test
    public void testOptIn_Success() throws Exception {
        OptInStatusResponse expected = new OptInStatusResponse();
        stubInvoke(expected);
        assertSame(expected, api.optIn());
    }

    @Test
    public void testOptIn_WithHeaders() throws Exception {
        stubInvoke(new OptInStatusResponse());
        api.optIn(Collections.singletonMap("X-O","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-O"));
    }

    @Test
    public void testOptOut_Success() throws Exception {
        OptInStatusResponse expected = new OptInStatusResponse();
        stubInvoke(expected);
        assertSame(expected, api.optOut());
    }

    @Test
    public void testOptOut_WithHeaders() throws Exception {
        stubInvoke(new OptInStatusResponse());
        api.optOut(Collections.singletonMap("X-P","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-P"));
    }

    /* replaceGovernanceBundle */
    @Test
    public void testReplaceGovernanceBundle_Success() throws Exception {
        GovernanceBundleUpdateRequest body = new GovernanceBundleUpdateRequest();
        GovernanceBundle expected = new GovernanceBundle();
        stubInvoke(expected);
        GovernanceBundle actual = api.replaceGovernanceBundle("b10", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/governance/bundles/b10"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b10");
    }

    @Test
    public void testReplaceGovernanceBundle_WithHeaders() throws Exception {
        stubInvoke(new GovernanceBundle());
        api.replaceGovernanceBundle("b11", new GovernanceBundleUpdateRequest(),
            Collections.singletonMap("X-R","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("y", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceGovernanceBundle_MissingBundleId() {
        try {
            api.replaceGovernanceBundle(null, new GovernanceBundleUpdateRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testReplaceGovernanceBundle_MissingBody() {
        try {
            api.replaceGovernanceBundle("b12", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* ApiException propagation example */
    @Test
    public void testCreateGovernanceBundle_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createGovernanceBundle(new GovernanceBundleCreateRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.GovernanceBundleApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

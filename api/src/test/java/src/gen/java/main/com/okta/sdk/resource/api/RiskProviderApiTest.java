package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.RiskProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes","deprecation"})
public class RiskProviderApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RiskProviderApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RiskProviderApi(apiClient);

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

    /* createRiskProvider */
    @Test
    public void testCreateRiskProvider_Success() throws Exception {
        RiskProvider expected = new RiskProvider();
        stubInvoke(expected);
        RiskProvider body = new RiskProvider();
        RiskProvider actual = api.createRiskProvider(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/providers"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateRiskProvider_WithHeaders() throws Exception {
        stubInvoke(new RiskProvider());
        api.createRiskProvider(new RiskProvider(), Collections.singletonMap("X-C","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-C"));
    }

    @Test
    public void testCreateRiskProvider_MissingBody() {
        try {
            api.createRiskProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("instance"));
        }
    }

    /* deleteRiskProvider */
    @Test
    public void testDeleteRiskProvider_Success() throws Exception {
        stubInvoke(null);
        api.deleteRiskProvider("RP1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/providers/RP1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteRiskProvider_WithHeaders() throws Exception {
        stubInvoke(null);
        api.deleteRiskProvider("RP2", Collections.singletonMap("X-D","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("1", headers.getValue().get("X-D"));
    }

    @Test
    public void testDeleteRiskProvider_MissingId() {
        try {
            api.deleteRiskProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("riskProviderId"));
        }
    }

    /* getRiskProvider */
    @Test
    public void testGetRiskProvider_Success() throws Exception {
        RiskProvider expected = new RiskProvider();
        stubInvoke(expected);
        RiskProvider actual = api.getRiskProvider("RP3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/providers/RP3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRiskProvider_WithHeaders() throws Exception {
        stubInvoke(new RiskProvider());
        api.getRiskProvider("RP4", Collections.singletonMap("X-H","h"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("h", headers.getValue().get("X-H"));
    }

    @Test
    public void testGetRiskProvider_MissingId() {
        try {
            api.getRiskProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("riskProviderId"));
        }
    }

    /* listRiskProviders */
    @Test
    public void testListRiskProviders_Success() throws Exception {
        List<RiskProvider> expected = new ArrayList<>();
        stubInvoke(expected);
        List<RiskProvider> actual = api.listRiskProviders();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/providers"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListRiskProviders_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRiskProviders(Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-L"));
    }

    /* replaceRiskProvider */
    @Test
    public void testReplaceRiskProvider_Success() throws Exception {
        RiskProvider expected = new RiskProvider();
        stubInvoke(expected);
        RiskProvider body = new RiskProvider();
        RiskProvider actual = api.replaceRiskProvider("RP5", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/providers/RP5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRiskProvider_WithHeaders() throws Exception {
        stubInvoke(new RiskProvider());
        api.replaceRiskProvider("RP6", new RiskProvider(), Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceRiskProvider_MissingArgs() {
        try { api.replaceRiskProvider(null, new RiskProvider()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("riskProviderId")); }
        try { api.replaceRiskProvider("RP7", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); assertTrue(e.getMessage().contains("instance")); }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Create() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.createRiskProvider(new RiskProvider());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.getRiskProvider("RP_ERR");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvoke(new RiskProvider());
        api.getRiskProvider("RP8");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/risk/providers/RP8"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listRiskProviders();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/risk/providers"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RiskProviderApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

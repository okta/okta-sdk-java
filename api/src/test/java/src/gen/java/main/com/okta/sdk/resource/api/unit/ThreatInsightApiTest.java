package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ThreatInsightConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class ThreatInsightApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ThreatInsightApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ThreatInsightApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(a -> a.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeGet(ThreatInsightConfiguration value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokePost(ThreatInsightConfiguration value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(ThreatInsightConfiguration.class),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* getCurrentConfiguration */
    @Test
    public void testGetCurrentConfiguration_Success() throws Exception {
        ThreatInsightConfiguration expected = new ThreatInsightConfiguration();
        stubInvokeGet(expected);
        ThreatInsightConfiguration actual = api.getCurrentConfiguration();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/threats/configuration"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetCurrentConfiguration_WithHeaders() throws Exception {
        stubInvokeGet(new ThreatInsightConfiguration());
        Map<String,String> hdr = Collections.singletonMap("X-G","v");
        api.getCurrentConfiguration(hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-G"));
    }

    /* updateConfiguration */
    @Test
    public void testUpdateConfiguration_Success() throws Exception {
        ThreatInsightConfiguration body = new ThreatInsightConfiguration();
        ThreatInsightConfiguration expected = new ThreatInsightConfiguration();
        stubInvokePost(expected);
        ThreatInsightConfiguration actual = api.updateConfiguration(body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/threats/configuration"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateConfiguration_WithHeaders() throws Exception {
        stubInvokePost(new ThreatInsightConfiguration());
        ThreatInsightConfiguration body = new ThreatInsightConfiguration();
        api.updateConfiguration(body, Collections.singletonMap("X-U","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-U"));
    }

    @Test
    public void testUpdateConfiguration_MissingParam() {
        expect400(() -> api.updateConfiguration(null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getCurrentConfiguration();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_Post() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.updateConfiguration(new ThreatInsightConfiguration());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvokeGet(new ThreatInsightConfiguration());
        api.getCurrentConfiguration();
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/threats/configuration"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Post() throws Exception {
        stubInvokePost(new ThreatInsightConfiguration());
        api.updateConfiguration(new ThreatInsightConfiguration());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/threats/configuration"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.ThreatInsightApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try {
            r.run();
            fail("Expected 400");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }
}

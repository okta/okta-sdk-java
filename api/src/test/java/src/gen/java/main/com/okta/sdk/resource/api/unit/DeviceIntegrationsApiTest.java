package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.DeviceIntegrationsApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.DeviceIntegrations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DeviceIntegrationsApiTest {

    private ApiClient apiClient;
    private DeviceIntegrationsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new DeviceIntegrationsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    /* Helpers */
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

    /* activateDeviceIntegration */
    @Test
    public void testActivateDeviceIntegration_Success() throws Exception {
        DeviceIntegrations expected = new DeviceIntegrations();
        stubInvoke(expected);

        DeviceIntegrations actual = api.activateDeviceIntegration("dev1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-integrations/dev1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("dev1");
    }

    @Test
    public void testActivateDeviceIntegration_WithHeaders() throws Exception {
        stubInvoke(new DeviceIntegrations());
        Map<String,String> hdrs = Collections.singletonMap("X-A","1");
        api.activateDeviceIntegration("dev2", hdrs);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-A"));
    }

    @Test(expected = ApiException.class)
    public void testActivateDeviceIntegration_MissingId() throws Exception {
        api.activateDeviceIntegration(null);
    }

    /* deactivateDeviceIntegration */
    @Test
    public void testDeactivateDeviceIntegration_Success() throws Exception {
        DeviceIntegrations expected = new DeviceIntegrations();
        stubInvoke(expected);

        DeviceIntegrations actual = api.deactivateDeviceIntegration("dev3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-integrations/dev3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("dev3");
    }

    @Test
    public void testDeactivateDeviceIntegration_WithHeaders() throws Exception {
        stubInvoke(new DeviceIntegrations());
        api.deactivateDeviceIntegration("dev4", Collections.singletonMap("X-D","v"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeactivateDeviceIntegration_MissingId() throws Exception {
        api.deactivateDeviceIntegration(null);
    }

    /* getDeviceIntegration */
    @Test
    public void testGetDeviceIntegration_Success() throws Exception {
        DeviceIntegrations expected = new DeviceIntegrations();
        stubInvoke(expected);

        DeviceIntegrations actual = api.getDeviceIntegration("dev5");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-integrations/dev5"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("dev5");
    }

    @Test
    public void testGetDeviceIntegration_WithHeaders() throws Exception {
        stubInvoke(new DeviceIntegrations());
        api.getDeviceIntegration("dev6", Collections.singletonMap("X-G","g"));

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
    public void testGetDeviceIntegration_MissingId() throws Exception {
        api.getDeviceIntegration(null);
    }

    /* listDeviceIntegrations */
    @Test
    public void testListDeviceIntegrations_Success() throws Exception {
        List<DeviceIntegrations> expected = Arrays.asList(new DeviceIntegrations(), new DeviceIntegrations());
        stubInvoke(expected);

        List<DeviceIntegrations> actual = api.listDeviceIntegrations();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-integrations"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListDeviceIntegrations_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listDeviceIntegrations(Collections.singletonMap("X-L","1"));

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

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method method = DeviceIntegrationsApi.class.getDeclaredMethod("getObjectMapper");
        method.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) method.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* ApiException propagation */
    @Test
    public void testActivateDeviceIntegration_ApiExceptionPropagates() throws Exception {
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenThrow(new ApiException(500, "err"));

        try {
            api.activateDeviceIntegration("devErr");
            fail("Expected ApiException");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }
}

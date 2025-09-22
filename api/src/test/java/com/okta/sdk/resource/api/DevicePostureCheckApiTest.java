package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.DevicePostureCheck;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DevicePostureCheckApiTest {

    private ApiClient apiClient;
    private DevicePostureCheckApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new DevicePostureCheckApi(apiClient);

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

    /* createDevicePostureCheck */
    @Test
    public void testCreateDevicePostureCheck_Success() throws Exception {
        DevicePostureCheck body = new DevicePostureCheck();
        DevicePostureCheck expected = new DevicePostureCheck();
        stubInvoke(expected);

        DevicePostureCheck actual = api.createDevicePostureCheck(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateDevicePostureCheck_WithHeaders() throws Exception {
        stubInvoke(new DevicePostureCheck());
        api.createDevicePostureCheck(new DevicePostureCheck(), Collections.singletonMap("X-C","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateDevicePostureCheck_MissingBody() throws Exception {
        api.createDevicePostureCheck(null);
    }

    /* deleteDevicePostureCheck */
    @Test
    public void testDeleteDevicePostureCheck_Success() throws Exception {
        stubVoidInvoke();
        api.deleteDevicePostureCheck("pc1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks/pc1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("pc1");
    }

    @Test
    public void testDeleteDevicePostureCheck_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteDevicePostureCheck("pc2", Collections.singletonMap("X-D","v"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdr.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteDevicePostureCheck_MissingId() throws Exception {
        api.deleteDevicePostureCheck(null);
    }

    /* getDevicePostureCheck */
    @Test
    public void testGetDevicePostureCheck_Success() throws Exception {
        DevicePostureCheck expected = new DevicePostureCheck();
        stubInvoke(expected);

        DevicePostureCheck actual = api.getDevicePostureCheck("pc3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks/pc3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("pc3");
    }

    @Test
    public void testGetDevicePostureCheck_WithHeaders() throws Exception {
        stubInvoke(new DevicePostureCheck());
        api.getDevicePostureCheck("pc4", Collections.singletonMap("X-G","g"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdr.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetDevicePostureCheck_MissingId() throws Exception {
        api.getDevicePostureCheck(null);
    }

    /* listDefaultDevicePostureChecks */
    @Test
    public void testListDefaultDevicePostureChecks_Success() throws Exception {
        List<DevicePostureCheck> expected = Arrays.asList(new DevicePostureCheck(), new DevicePostureCheck());
        stubInvoke(expected);

        List<DevicePostureCheck> actual = api.listDefaultDevicePostureChecks();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks/default"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListDefaultDevicePostureChecks_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listDefaultDevicePostureChecks(Collections.singletonMap("X-LD","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-LD"));
    }

    /* listDevicePostureChecks */
    @Test
    public void testListDevicePostureChecks_Success() throws Exception {
        List<DevicePostureCheck> expected = Arrays.asList(new DevicePostureCheck());
        stubInvoke(expected);

        List<DevicePostureCheck> actual = api.listDevicePostureChecks();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListDevicePostureChecks_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listDevicePostureChecks(Collections.singletonMap("X-L","x"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", hdr.getValue().get("X-L"));
    }

    /* replaceDevicePostureCheck */
    @Test
    public void testReplaceDevicePostureCheck_Success() throws Exception {
        DevicePostureCheck body = new DevicePostureCheck();
        DevicePostureCheck expected = new DevicePostureCheck();
        stubInvoke(expected);

        DevicePostureCheck actual = api.replaceDevicePostureCheck("pc5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/device-posture-checks/pc5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("pc5");
    }

    @Test
    public void testReplaceDevicePostureCheck_WithHeaders() throws Exception {
        stubInvoke(new DevicePostureCheck());
        api.replaceDevicePostureCheck("pc6", new DevicePostureCheck(), Collections.singletonMap("X-R","r"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdr.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceDevicePostureCheck_MissingId() throws Exception {
        api.replaceDevicePostureCheck(null, new DevicePostureCheck());
    }

    @Test(expected = ApiException.class)
    public void testReplaceDevicePostureCheck_MissingBody() throws Exception {
        api.replaceDevicePostureCheck("pc7", null);
    }

    /* ObjectMapper (reflection) */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = DevicePostureCheckApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* ApiException propagation */
    @Test
    public void testCreateDevicePostureCheck_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));

        try {
            api.createDevicePostureCheck(new DevicePostureCheck());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }
}

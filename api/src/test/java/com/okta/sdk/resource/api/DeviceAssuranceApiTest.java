package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.DeviceAssurance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DeviceAssuranceApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.DeviceAssuranceApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.DeviceAssuranceApi(apiClient);

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

    /* createDeviceAssurancePolicy */
    @Test
    public void testCreateDeviceAssurancePolicy_Success() throws Exception {
        DeviceAssurance body = new DeviceAssurance();
        DeviceAssurance expected = new DeviceAssurance();
        stubInvoke(expected);

        DeviceAssurance actual = api.createDeviceAssurancePolicy(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/device-assurances"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateDeviceAssurancePolicy_WithHeaders() throws Exception {
        stubInvoke(new DeviceAssurance());
        api.createDeviceAssurancePolicy(new DeviceAssurance(), Collections.singletonMap("X-C","1"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateDeviceAssurancePolicy_MissingBody() throws Exception {
        api.createDeviceAssurancePolicy(null);
    }

    /* deleteDeviceAssurancePolicy */
    @Test
    public void testDeleteDeviceAssurancePolicy_Success() throws Exception {
        stubVoidInvoke();
        api.deleteDeviceAssurancePolicy("da1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-assurances/da1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("da1");
    }

    @Test
    public void testDeleteDeviceAssurancePolicy_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteDeviceAssurancePolicy("da2", Collections.singletonMap("X-D","v"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteDeviceAssurancePolicy_MissingId() throws Exception {
        api.deleteDeviceAssurancePolicy(null);
    }

    /* getDeviceAssurancePolicy */
    @Test
    public void testGetDeviceAssurancePolicy_Success() throws Exception {
        DeviceAssurance expected = new DeviceAssurance();
        stubInvoke(expected);

        DeviceAssurance actual = api.getDeviceAssurancePolicy("da3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-assurances/da3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("da3");
    }

    @Test
    public void testGetDeviceAssurancePolicy_WithHeaders() throws Exception {
        stubInvoke(new DeviceAssurance());
        api.getDeviceAssurancePolicy("da4", Collections.singletonMap("X-G","g"));

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
    public void testGetDeviceAssurancePolicy_MissingId() throws Exception {
        api.getDeviceAssurancePolicy(null);
    }

    /* listDeviceAssurancePolicies */
    @Test
    public void testListDeviceAssurancePolicies_Success() throws Exception {
        List<DeviceAssurance> expected = Arrays.asList(new DeviceAssurance(), new DeviceAssurance());
        stubInvoke(expected);

        List<DeviceAssurance> actual = api.listDeviceAssurancePolicies();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/device-assurances"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListDeviceAssurancePolicies_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listDeviceAssurancePolicies(Collections.singletonMap("X-L","1"));

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

    /* replaceDeviceAssurancePolicy */
    @Test
    public void testReplaceDeviceAssurancePolicy_Success() throws Exception {
        DeviceAssurance body = new DeviceAssurance();
        DeviceAssurance expected = new DeviceAssurance();
        stubInvoke(expected);

        DeviceAssurance actual = api.replaceDeviceAssurancePolicy("da5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/device-assurances/da5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("da5");
    }

    @Test
    public void testReplaceDeviceAssurancePolicy_WithHeaders() throws Exception {
        stubInvoke(new DeviceAssurance());
        api.replaceDeviceAssurancePolicy("da6", new DeviceAssurance(), Collections.singletonMap("X-R","r"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceDeviceAssurancePolicy_MissingId() throws Exception {
        api.replaceDeviceAssurancePolicy(null, new DeviceAssurance());
    }

    @Test(expected = ApiException.class)
    public void testReplaceDeviceAssurancePolicy_MissingBody() throws Exception {
        api.replaceDeviceAssurancePolicy("da7", null);
    }
}

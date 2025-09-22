package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SsfTransmitterApiTest {

    private ApiClient apiClient;
    private SsfTransmitterApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new SsfTransmitterApi(apiClient);

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

    /* createSsfStream */
    @Test
    public void testCreateSsfStream_Success() throws Exception {
        StreamConfiguration expected = new StreamConfiguration();
        stubInvoke(expected);
        StreamConfigurationCreateRequest body = new StreamConfigurationCreateRequest();
        StreamConfiguration actual = api.createSsfStream(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateSsfStream_MissingBody() {
        try {
            api.createSsfStream(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("instance"));
        }
    }

    /* deleteSsfStream */
    @Test
    public void testDeleteSsfStream_Success() throws Exception {
        stubVoidInvoke();
        api.deleteSsfStream("stream1");
        verify(apiClient).parameterToPair("stream_id", "stream1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    /* getSsfStreamStatus */
    @Test
    public void testGetSsfStreamStatus_Success() throws Exception {
        StreamStatus expected = new StreamStatus();
        stubInvoke(expected);
        StreamStatus actual = api.getSsfStreamStatus("stream2");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("stream_id", "stream2");
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream/status"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetSsfStreamStatus_MissingId() {
        try {
            api.getSsfStreamStatus(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("streamId"));
        }
    }

    /* getSsfStreams */
    @Test
    public void testGetSsfStreams_Success() throws Exception {
        GetSsfStreams200Response expected = new GetSsfStreams200Response();
        stubInvoke(expected);
        GetSsfStreams200Response actual = api.getSsfStreams("stream3");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("stream_id", "stream3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* getWellknownSsfMetadata */
    @Test
    public void testGetWellknownSsfMetadata_Success() throws Exception {
        WellKnownSSFMetadata expected = new WellKnownSSFMetadata();
        stubInvoke(expected);
        WellKnownSSFMetadata actual = api.getWellknownSsfMetadata();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/.well-known/ssf-configuration"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* replaceSsfStream */
    @Test
    public void testReplaceSsfStream_Success() throws Exception {
        StreamConfiguration expected = new StreamConfiguration();
        stubInvoke(expected);
        StreamConfiguration body = new StreamConfiguration();
        StreamConfiguration actual = api.replaceSsfStream(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    /* updateSsfStream */
    @Test
    public void testUpdateSsfStream_Success() throws Exception {
        StreamConfiguration expected = new StreamConfiguration();
        stubInvoke(expected);
        StreamConfiguration body = new StreamConfiguration();
        StreamConfiguration actual = api.updateSsfStream(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    /* verifySsfStream */
    @Test
    public void testVerifySsfStream_Success() throws Exception {
        stubVoidInvoke();
        StreamVerificationRequest body = new StreamVerificationRequest();
        api.verifySsfStream(body);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/ssf/stream/verification"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testVerifySsfStream_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.verifySsfStream(new StreamVerificationRequest(), Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    @Test
    public void testVerifySsfStream_MissingBody() {
        try {
            api.verifySsfStream(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("instance"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(404, "Not Found"));
        try {
            api.getSsfStreamStatus("non-existent-id");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(404, ex.getCode());
            assertEquals("Not Found", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = SsfTransmitterApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
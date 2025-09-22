package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.LogStream;
import com.okta.sdk.resource.model.LogStreamPutSchema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class LogStreamApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.LogStreamApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.LogStreamApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.singletonList(new Pair("k","v")));
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

    /* activateLogStream */
    @Test
    public void testActivateLogStream_Success() throws Exception {
        LogStream expected = new LogStream();
        stubInvoke(expected);
        LogStream actual = api.activateLogStream("ls1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams/ls1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("ls1");
    }

    @Test
    public void testActivateLogStream_WithHeaders() throws Exception {
        stubInvoke(new LogStream());
        api.activateLogStream("lsH", Collections.singletonMap("X-A","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateLogStream_MissingId() {
        try {
            api.activateLogStream(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* createLogStream */
    @Test
    public void testCreateLogStream_Success() throws Exception {
        LogStream expected = new LogStream();
        stubInvoke(expected);
        LogStream body = new LogStream();
        LogStream actual = api.createLogStream(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateLogStream_WithHeaders() throws Exception {
        stubInvoke(new LogStream());
        api.createLogStream(new LogStream(), Collections.singletonMap("X-C","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateLogStream_MissingBody() {
        try {
            api.createLogStream(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* deactivateLogStream */
    @Test
    public void testDeactivateLogStream_Success() throws Exception {
        LogStream expected = new LogStream();
        stubInvoke(expected);
        LogStream actual = api.deactivateLogStream("ls2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams/ls2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("ls2");
    }

    @Test
    public void testDeactivateLogStream_WithHeaders() throws Exception {
        stubInvoke(new LogStream());
        api.deactivateLogStream("lsDH", Collections.singletonMap("X-D","z"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("z", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeactivateLogStream_MissingId() {
        try {
            api.deactivateLogStream(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* deleteLogStream */
    @Test
    public void testDeleteLogStream_Success() throws Exception {
        stubVoidInvoke();
        api.deleteLogStream("ls3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams/ls3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("ls3");
    }

    @Test
    public void testDeleteLogStream_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteLogStream("lsDel", Collections.singletonMap("X-R","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-R"));
    }

    @Test
    public void testDeleteLogStream_MissingId() {
        try {
            api.deleteLogStream(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* getLogStream */
    @Test
    public void testGetLogStream_Success() throws Exception {
        LogStream expected = new LogStream();
        stubInvoke(expected);
        LogStream actual = api.getLogStream("ls4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams/ls4"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("ls4");
    }

    @Test
    public void testGetLogStream_WithHeaders() throws Exception {
        stubInvoke(new LogStream());
        api.getLogStream("lsGH", Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetLogStream_MissingId() {
        try {
            api.getLogStream(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* listLogStreams */
    @Test
    public void testListLogStreams_AllParams() throws Exception {
        List<LogStream> expected = Arrays.asList(new LogStream());
        stubInvoke(expected);
        List<LogStream> list = api.listLogStreams("cursor1", 50, "status eq \"ACTIVE\"");
        assertSame(expected, list);
        verify(apiClient).parameterToPair("after", "cursor1");
        verify(apiClient).parameterToPair("limit", 50);
        verify(apiClient).parameterToPair("filter", "status eq \"ACTIVE\"");
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListLogStreams_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<LogStream> list = api.listLogStreams(null, null, null);
        assertNotNull(list);
    }

    @Test
    public void testListLogStreams_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listLogStreams(null, null, null, Collections.singletonMap("X-L","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("y", cap.getValue().get("X-L"));
    }

    /* replaceLogStream */
    @Test
    public void testReplaceLogStream_Success() throws Exception {
        LogStream expected = new LogStream();
        stubInvoke(expected);
        LogStreamPutSchema body = new LogStreamPutSchema();
        LogStream actual = api.replaceLogStream("ls5", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/logStreams/ls5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("ls5");
    }

    @Test
    public void testReplaceLogStream_WithHeaders() throws Exception {
        stubInvoke(new LogStream());
        LogStreamPutSchema body = new LogStreamPutSchema();
        api.replaceLogStream("lsRH", body, Collections.singletonMap("X-R","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceLogStream_MissingId() {
        try {
            api.replaceLogStream(null, new LogStreamPutSchema());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testReplaceLogStream_MissingBody() {
        try {
            api.replaceLogStream("lsX", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testReplaceLogStream_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.replaceLogStream("lsErr", new LogStreamPutSchema());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("bad"));
        }
    }

    @Test
    public void testDeleteLogStream_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteLogStream("lsErr2");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.LogStreamApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

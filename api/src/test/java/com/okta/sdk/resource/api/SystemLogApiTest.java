//package com.okta.sdk.resource.api;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.okta.sdk.resource.client.ApiClient;
//import com.okta.sdk.resource.client.ApiException;
//import com.okta.sdk.resource.model.LogEvent;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@SuppressWarnings({"rawtypes", "unchecked"})
//public class SystemLogApiTest {
//
//    private ApiClient apiClient;
//    private SystemLogApi api;
//
//    @Before
//    public void setUp() {
//        apiClient = mock(ApiClient.class);
//        api = new SystemLogApi(apiClient);
//
//        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
//        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
//        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
//        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
//    }
//
//    private <T> void stubInvoke(T value) throws ApiException {
//        when(apiClient.invokeAPI(
//            anyString(), anyString(),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenReturn(value);
//    }
//
//    /* listLogEvents */
//    @Test
//    public void testListLogEvents_Success_AllParams() throws Exception {
//        List<LogEvent> expected = Arrays.asList(new LogEvent());
//        stubInvoke(expected);
//        List<LogEvent> actual = api.listLogEvents("2025-01-01T00:00:00Z", "2025-01-02T00:00:00Z", "next_cursor", "eventType eq \"user.session.start\"", "search", 50, "DESCENDING");
//        assertSame(expected, actual);
//
//        verify(apiClient).parameterToPair("since", "2025-01-01T00:00:00Z");
//        verify(apiClient).parameterToPair("until", "2025-01-02T00:00:00Z");
//        verify(apiClient).parameterToPair("after", "next_cursor");
//        verify(apiClient).parameterToPair("filter", "eventType eq \"user.session.start\"");
//        verify(apiClient).parameterToPair("q", "search");
//        verify(apiClient).parameterToPair("limit", 50);
//        verify(apiClient).parameterToPair("sortOrder", "DESCENDING");
//
//        verify(apiClient).invokeAPI(
//            eq("/api/v1/logs"), eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//    }
//
//    @Test
//    public void testListLogEvents_WithHeaders() throws Exception {
//        stubInvoke(Collections.emptyList());
//        api.listLogEvents(null, null, null, null, null, null, null, Collections.singletonMap("X-Test", "true"));
//        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
//        verify(apiClient).invokeAPI(
//            anyString(), eq("GET"),
//            anyList(), anyList(),
//            anyString(), isNull(),
//            cap.capture(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class));
//        assertEquals("true", cap.getValue().get("X-Test"));
//    }
//
//    /* ApiException propagation */
//    @Test
//    public void testApiExceptionPropagates() throws Exception {
//        when(apiClient.invokeAPI(
//            anyString(), anyString(),
//            anyList(), anyList(),
//            anyString(), any(),
//            anyMap(), anyMap(), anyMap(),
//            anyString(), anyString(),
//            any(String[].class), any(TypeReference.class)
//        )).thenThrow(new ApiException(500, "Internal Server Error"));
//        try {
//            api.listLogEvents(null, null, null, null, null, null, null);
//            fail("Expected ApiException");
//        } catch (ApiException ex) {
//            assertEquals(500, ex.getCode());
//            assertEquals("Internal Server Error", ex.getMessage());
//        }
//    }
//
//    /* ObjectMapper configuration */
//    @Test
//    public void testGetObjectMapperConfiguration() throws Exception {
//        Method m = SystemLogApi.class.getDeclaredMethod("getObjectMapper");
//        m.setAccessible(true);
//        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
//        assertNotNull(mapper);
//        assertFalse(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
//        assertTrue(mapper.getDeserializationConfig()
//            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
//    }
//}
package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.EventHook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class EventHookApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.EventHookApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.EventHookApi(apiClient);

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

    /* activateEventHook */
    @Test
    public void testActivateEventHook_Success() throws Exception {
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.activateEventHook("eh1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("eh1");
    }

    @Test
    public void testActivateEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        Map<String,String> hdrs = Collections.singletonMap("X-A","1");
        api.activateEventHook("eh2", hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateEventHook_MissingId() {
        try {
            api.activateEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    /* createEventHook */
    @Test
    public void testCreateEventHook_Success() throws Exception {
        EventHook body = new EventHook();
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.createEventHook(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        Map<String,String> hdrs = Collections.singletonMap("X-C","v");
        api.createEventHook(new EventHook(), hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateEventHook_MissingBody() {
        try {
            api.createEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHook"));
        }
    }

    /* deactivateEventHook */
    @Test
    public void testDeactivateEventHook_Success() throws Exception {
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.deactivateEventHook("eh3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("eh3");
    }

    @Test
    public void testDeactivateEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        api.deactivateEventHook("eh4", Collections.singletonMap("X-D","1"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeactivateEventHook_MissingId() {
        try {
            api.deactivateEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    /* deleteEventHook */
    @Test
    public void testDeleteEventHook_Success() throws Exception {
        stubVoidInvoke();
        api.deleteEventHook("eh5");
        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("eh5");
    }

    @Test
    public void testDeleteEventHook_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteEventHook("eh6", Collections.singletonMap("X-X","z"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("z", cap.getValue().get("X-X"));
    }

    @Test
    public void testDeleteEventHook_MissingId() {
        try {
            api.deleteEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    /* getEventHook */
    @Test
    public void testGetEventHook_Success() throws Exception {
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.getEventHook("eh7");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("eh7");
    }

    @Test
    public void testGetEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        api.getEventHook("eh8", Collections.singletonMap("X-G","g"));

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

    @Test
    public void testGetEventHook_MissingId() {
        try {
            api.getEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    /* listEventHooks */
    @Test
    public void testListEventHooks_Success() throws Exception {
        List<EventHook> expected = Arrays.asList(new EventHook());
        stubInvoke(expected);

        List<EventHook> actual = api.listEventHooks();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListEventHooks_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listEventHooks(Collections.singletonMap("X-L","1"));

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

    /* replaceEventHook */
    @Test
    public void testReplaceEventHook_Success() throws Exception {
        EventHook body = new EventHook();
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.replaceEventHook("eh9", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh9"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("eh9");
    }

    @Test
    public void testReplaceEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        api.replaceEventHook("eh10", new EventHook(), Collections.singletonMap("X-R","y"));

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
    public void testReplaceEventHook_MissingId() {
        try {
            api.replaceEventHook(null, new EventHook());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    @Test
    public void testReplaceEventHook_MissingBody() {
        try {
            api.replaceEventHook("eh11", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHook"));
        }
    }

    /* verifyEventHook */
    @Test
    public void testVerifyEventHook_Success() throws Exception {
        EventHook expected = new EventHook();
        stubInvoke(expected);

        EventHook actual = api.verifyEventHook("eh12");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/eventHooks/eh12/lifecycle/verify"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("eh12");
    }

    @Test
    public void testVerifyEventHook_WithHeaders() throws Exception {
        stubInvoke(new EventHook());
        api.verifyEventHook("eh13", Collections.singletonMap("X-V","ok"));

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("ok", cap.getValue().get("X-V"));
    }

    @Test
    public void testVerifyEventHook_MissingId() {
        try {
            api.verifyEventHook(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("eventHookId"));
        }
    }

    /* ApiException propagation example */
    @Test
    public void testCreateEventHook_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createEventHook(new EventHook());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.EventHookApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

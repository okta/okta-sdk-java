package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ExecuteInlineHook200Response;
import com.okta.sdk.resource.model.ExecuteInlineHookRequest;
import com.okta.sdk.resource.model.InlineHook;
import com.okta.sdk.resource.model.InlineHookCreate;
import com.okta.sdk.resource.model.InlineHookCreateResponse;
import com.okta.sdk.resource.model.InlineHookReplace;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class InlineHookApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.InlineHookApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.InlineHookApi(apiClient);

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

    /* activateInlineHook */
    @Test
    public void testActivateInlineHook_Success() throws Exception {
        InlineHook expected = new InlineHook();
        stubInvoke(expected);
        InlineHook actual = api.activateInlineHook("id1");
        assertSame(expected, actual);
        verify(apiClient).escapeString("id1");
    }

    @Test
    public void testActivateInlineHook_WithHeaders() throws Exception {
        stubInvoke(new InlineHook());
        api.activateInlineHook("idH", Collections.singletonMap("X-A","1"));
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
    public void testActivateInlineHook_MissingId() {
        try { api.activateInlineHook(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* createInlineHook */
    @Test
    public void testCreateInlineHook_Success() throws Exception {
        InlineHookCreateResponse expected = new InlineHookCreateResponse();
        stubInvoke(expected);
        InlineHookCreate body = new InlineHookCreate();
        InlineHookCreateResponse actual = api.createInlineHook(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateInlineHook_WithHeaders() throws Exception {
        stubInvoke(new InlineHookCreateResponse());
        api.createInlineHook(new InlineHookCreate(), Collections.singletonMap("X-C","v"));
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
    public void testCreateInlineHook_MissingBody() {
        try { api.createInlineHook(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deactivateInlineHook */
    @Test
    public void testDeactivateInlineHook_Success() throws Exception {
        InlineHook expected = new InlineHook();
        stubInvoke(expected);
        InlineHook actual = api.deactivateInlineHook("id2");
        assertSame(expected, actual);
        verify(apiClient).escapeString("id2");
    }

    @Test
    public void testDeactivateInlineHook_MissingId() {
        try { api.deactivateInlineHook(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deleteInlineHook */
    @Test
    public void testDeleteInlineHook_Success() throws Exception {
        stubVoidInvoke();
        api.deleteInlineHook("id3");
        verify(apiClient).escapeString("id3");
    }

    @Test
    public void testDeleteInlineHook_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteInlineHook("id4", Collections.singletonMap("X-D","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("y", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteInlineHook_MissingId() {
        try { api.deleteInlineHook(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* executeInlineHook */
    @Test
    public void testExecuteInlineHook_Success() throws Exception {
        ExecuteInlineHook200Response expected = new ExecuteInlineHook200Response();
        stubInvoke(expected);
        ExecuteInlineHookRequest body = new ExecuteInlineHookRequest();
        ExecuteInlineHook200Response actual = api.executeInlineHook("id5", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testExecuteInlineHook_MissingId() {
        try { api.executeInlineHook(null, new ExecuteInlineHookRequest()); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testExecuteInlineHook_MissingBody() {
        try { api.executeInlineHook("id", null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* getInlineHook */
    @Test
    public void testGetInlineHook_Success() throws Exception {
        InlineHook expected = new InlineHook();
        stubInvoke(expected);
        InlineHook actual = api.getInlineHook("id6");
        assertSame(expected, actual);
        verify(apiClient).escapeString("id6");
    }

    @Test
    public void testGetInlineHook_WithHeaders() throws Exception {
        stubInvoke(new InlineHook());
        api.getInlineHook("idG", Collections.singletonMap("X-G","1"));
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
    public void testGetInlineHook_MissingId() {
        try { api.getInlineHook(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listInlineHooks */
    @Test
    public void testListInlineHooks_WithType() throws Exception {
        List<InlineHook> expected = Arrays.asList(new InlineHook());
        stubInvoke(expected);
        List<InlineHook> list = api.listInlineHooks("com.okta.tokens.transform");
        assertSame(expected, list);
        verify(apiClient).parameterToPair(eq("type"), any());
    }

    @Test
    public void testListInlineHooks_NoType() throws Exception {
        stubInvoke(Collections.emptyList());
        List<InlineHook> list = api.listInlineHooks(null);
        assertNotNull(list);
    }

    @Test
    public void testListInlineHooks_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listInlineHooks(null, Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-L"));
    }

    /* replaceInlineHook */
    @Test
    public void testReplaceInlineHook_Success() throws Exception {
        InlineHook expected = new InlineHook();
        stubInvoke(expected);
        InlineHookReplace body = new InlineHookReplace();
        InlineHook actual = api.replaceInlineHook("id7", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceInlineHook_MissingId() {
        try { api.replaceInlineHook(null, new InlineHookReplace()); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testReplaceInlineHook_MissingBody() {
        try { api.replaceInlineHook("id", null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* updateInlineHook */
    @Test
    public void testUpdateInlineHook_Success() throws Exception {
        InlineHook expected = new InlineHook();
        stubInvoke(expected);
        InlineHookReplace body = new InlineHookReplace();
        InlineHook actual = api.updateInlineHook("id8", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUpdateInlineHook_MissingId() {
        try { api.updateInlineHook(null, new InlineHookReplace()); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testUpdateInlineHook_MissingBody() {
        try { api.updateInlineHook("id", null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testExecuteInlineHook_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.executeInlineHook("idx", new ExecuteInlineHookRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("boom"));
        }
    }

    @Test
    public void testDeleteInlineHook_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteInlineHook("idErr");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.InlineHookApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

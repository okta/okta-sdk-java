package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.LinkedObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class LinkedObjectApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.LinkedObjectApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.LinkedObjectApi(apiClient);
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

    /* createLinkedObjectDefinition */
    @Test
    public void testCreateLinkedObjectDefinition_Success() throws Exception {
        LinkedObject expected = new LinkedObject();
        stubInvoke(expected);
        LinkedObject body = new LinkedObject();
        LinkedObject actual = api.createLinkedObjectDefinition(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/linkedObjects"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateLinkedObjectDefinition_WithHeaders() throws Exception {
        stubInvoke(new LinkedObject());
        api.createLinkedObjectDefinition(new LinkedObject(), Collections.singletonMap("X-C","v"));
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
    public void testCreateLinkedObjectDefinition_MissingBody() {
        try {
            api.createLinkedObjectDefinition(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* getLinkedObjectDefinition */
    @Test
    public void testGetLinkedObjectDefinition_Success() throws Exception {
        LinkedObject expected = new LinkedObject();
        stubInvoke(expected);
        LinkedObject actual = api.getLinkedObjectDefinition("manager");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/linkedObjects/manager"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("manager");
    }

    @Test
    public void testGetLinkedObjectDefinition_WithHeaders() throws Exception {
        stubInvoke(new LinkedObject());
        api.getLinkedObjectDefinition("assistant", Collections.singletonMap("X-G","1"));
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
    public void testGetLinkedObjectDefinition_MissingName() {
        try {
            api.getLinkedObjectDefinition(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* listLinkedObjectDefinitions */
    @Test
    public void testListLinkedObjectDefinitions_Success() throws Exception {
        List<LinkedObject> expected = Arrays.asList(new LinkedObject());
        stubInvoke(expected);
        List<LinkedObject> list = api.listLinkedObjectDefinitions();
        assertSame(expected, list);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/linkedObjects"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListLinkedObjectDefinitions_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listLinkedObjectDefinitions(Collections.singletonMap("X-L","z"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("z", cap.getValue().get("X-L"));
    }

    /* deleteLinkedObjectDefinition */
    @Test
    public void testDeleteLinkedObjectDefinition_Success() throws Exception {
        stubVoidInvoke();
        api.deleteLinkedObjectDefinition("primaryRel");
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/schemas/user/linkedObjects/primaryRel"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("primaryRel");
    }

    @Test
    public void testDeleteLinkedObjectDefinition_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteLinkedObjectDefinition("assocRel", Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteLinkedObjectDefinition_MissingName() {
        try {
            api.deleteLinkedObjectDefinition(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateLinkedObjectDefinition_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createLinkedObjectDefinition(new LinkedObject());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("boom"));
        }
    }

    @Test
    public void testDeleteLinkedObjectDefinition_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteLinkedObjectDefinition("x");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.LinkedObjectApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.CreateUISchema;
import com.okta.sdk.resource.model.UISchemasResponseObject;
import com.okta.sdk.resource.model.UpdateUISchema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class UiSchemaApiTest {

    private ApiClient apiClient;
    private UiSchemaApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UiSchemaApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    /* Stubs */
    private void stubInvokeCreate(UISchemasResponseObject value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(CreateUISchema.class),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeReplace(UISchemasResponseObject value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(UpdateUISchema.class),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeGetSingle(UISchemasResponseObject value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeList(List<UISchemasResponseObject> value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeDelete() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    /* createUISchema */
    @Test
    public void testCreateUISchema_Success() throws Exception {
        UISchemasResponseObject expected = new UISchemasResponseObject();
        stubInvokeCreate(expected);
        CreateUISchema body = new CreateUISchema();
        UISchemasResponseObject actual = api.createUISchema(body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/uischemas"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testCreateUISchema_WithHeaders() throws Exception {
        stubInvokeCreate(new UISchemasResponseObject());
        Map<String,String> hdr = Collections.singletonMap("X-C","v");
        api.createUISchema(new CreateUISchema(), hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateUISchema_MissingParam() {
        expect400(() -> api.createUISchema(null));
    }

    /* getUISchema */
    @Test
    public void testGetUISchema_Success() throws Exception {
        UISchemasResponseObject expected = new UISchemasResponseObject();
        stubInvokeGetSingle(expected);
        UISchemasResponseObject actual = api.getUISchema("ID1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/uischemas/ID1"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetUISchema_WithHeaders() throws Exception {
        stubInvokeGetSingle(new UISchemasResponseObject());
        api.getUISchema("ID2", Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("g", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetUISchema_MissingParam() {
        expect400(() -> api.getUISchema(null));
    }

    /* listUISchemas */
    @Test
    public void testListUISchemas_Success() throws Exception {
        List<UISchemasResponseObject> expected = Arrays.asList(new UISchemasResponseObject(), new UISchemasResponseObject());
        stubInvokeList(expected);
        List<UISchemasResponseObject> actual = api.listUISchemas();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/uischemas"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListUISchemas_WithHeaders() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listUISchemas(Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    /* deleteUISchemas */
    @Test
    public void testDeleteUISchemas_Success() throws Exception {
        stubInvokeDelete();
        api.deleteUISchemas("ID3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/uischemas/ID3"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteUISchemas_WithHeaders() throws Exception {
        stubInvokeDelete();
        api.deleteUISchemas("ID4", Collections.singletonMap("X-D","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteUISchemas_MissingParam() {
        expect400(() -> api.deleteUISchemas(null));
    }

    /* replaceUISchemas */
    @Test
    public void testReplaceUISchemas_Success() throws Exception {
        UISchemasResponseObject expected = new UISchemasResponseObject();
        stubInvokeReplace(expected);
        UpdateUISchema body = new UpdateUISchema();
        UISchemasResponseObject actual = api.replaceUISchemas("ID5", body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/meta/uischemas/ID5"),
            eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceUISchemas_WithHeaders() throws Exception {
        stubInvokeReplace(new UISchemasResponseObject());
        UpdateUISchema body = new UpdateUISchema();
        api.replaceUISchemas("ID6", body, Collections.singletonMap("X-R","h"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("h", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceUISchemas_MissingParams() {
        expect400(() -> api.replaceUISchemas(null, new UpdateUISchema()));
        expect400(() -> api.replaceUISchemas("ID7", null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Create() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.createUISchema(new CreateUISchema());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_Get() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.getUISchema("IDX");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_Create() throws Exception {
        stubInvokeCreate(new UISchemasResponseObject());
        api.createUISchema(new CreateUISchema());
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/meta/uischemas"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Get() throws Exception {
        stubInvokeGetSingle(new UISchemasResponseObject());
        api.getUISchema("ID8");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/meta/uischemas/ID8"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_List() throws Exception {
        stubInvokeList(new ArrayList<>());
        api.listUISchemas();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/meta/uischemas"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Replace() throws Exception {
        stubInvokeReplace(new UISchemasResponseObject());
        api.replaceUISchemas("ID9", new UpdateUISchema());
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/meta/uischemas/ID9"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_Delete() throws Exception {
        stubInvokeDelete();
        api.deleteUISchemas("ID10");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/meta/uischemas/ID10"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = UiSchemaApi.class.getDeclaredMethod("getObjectMapper");
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

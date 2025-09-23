package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.TrustedOriginApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.TrustedOrigin;
import com.okta.sdk.resource.model.TrustedOriginWrite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TrustedOriginApiTest {

    private ApiClient apiClient;
    private TrustedOriginApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new TrustedOriginApi(apiClient);

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

    /* activateTrustedOrigin */
    @Test
    public void testActivateTrustedOrigin_Success() throws Exception {
        TrustedOrigin expected = new TrustedOrigin();
        stubInvoke(expected);
        TrustedOrigin actual = api.activateTrustedOrigin("to1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins/to1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("to1");
    }

    @Test
    public void testActivateTrustedOrigin_MissingId() {
        try {
            api.activateTrustedOrigin(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("trustedOriginId"));
        }
    }

    /* createTrustedOrigin */
    @Test
    public void testCreateTrustedOrigin_Success() throws Exception {
        TrustedOrigin expected = new TrustedOrigin();
        stubInvoke(expected);
        TrustedOriginWrite body = new TrustedOriginWrite();
        TrustedOrigin actual = api.createTrustedOrigin(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateTrustedOrigin_MissingBody() {
        try {
            api.createTrustedOrigin(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("trustedOrigin"));
        }
    }

    /* deactivateTrustedOrigin */
    @Test
    public void testDeactivateTrustedOrigin_Success() throws Exception {
        TrustedOrigin expected = new TrustedOrigin();
        stubInvoke(expected);
        TrustedOrigin actual = api.deactivateTrustedOrigin("to2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins/to2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("to2");
    }

    /* deleteTrustedOrigin */
    @Test
    public void testDeleteTrustedOrigin_Success() throws Exception {
        stubVoidInvoke();
        api.deleteTrustedOrigin("to3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins/to3"), eq("DELETE"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());
        verify(apiClient).escapeString("to3");
    }

    /* getTrustedOrigin */
    @Test
    public void testGetTrustedOrigin_Success() throws Exception {
        TrustedOrigin expected = new TrustedOrigin();
        stubInvoke(expected);
        TrustedOrigin actual = api.getTrustedOrigin("to4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins/to4"), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("to4");
    }

    /* listTrustedOrigins */
    @Test
    public void testListTrustedOrigins_Success_AllParams() throws Exception {
        List<TrustedOrigin> expected = Arrays.asList(new TrustedOrigin());
        stubInvoke(expected);
        List<TrustedOrigin> actual = api.listTrustedOrigins("query", "status eq \"ACTIVE\"", "cursor", 50);
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("q", "query");
        verify(apiClient).parameterToPair("filter", "status eq \"ACTIVE\"");
        verify(apiClient).parameterToPair("after", "cursor");
        verify(apiClient).parameterToPair("limit", 50);

        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins"), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
    }

    /* replaceTrustedOrigin */
    @Test
    public void testReplaceTrustedOrigin_Success() throws Exception {
        TrustedOrigin expected = new TrustedOrigin();
        stubInvoke(expected);
        TrustedOrigin body = new TrustedOrigin();
        TrustedOrigin actual = api.replaceTrustedOrigin("to5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/trustedOrigins/to5"), eq("PUT"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("to5");
    }

    @Test
    public void testReplaceTrustedOrigin_WithHeaders() throws Exception {
        stubInvoke(new TrustedOrigin());
        api.replaceTrustedOrigin("to6", new TrustedOrigin(), Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(), anyString(), any(), cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(), anyList(), anyList(), anyString(), any(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(404, "Not Found"));
        try {
            api.getTrustedOrigin("non-existent-id");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(404, ex.getCode());
            assertEquals("Not Found", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = TrustedOriginApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.DetailedHookKeyInstance;
import com.okta.sdk.resource.model.Embedded;
import com.okta.sdk.resource.model.HookKey;
import com.okta.sdk.resource.model.KeyRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class HookKeyApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.HookKeyApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.HookKeyApi(apiClient);

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

    /* createHookKey */
    @Test
    public void testCreateHookKey_Success() throws Exception {
        DetailedHookKeyInstance expected = new DetailedHookKeyInstance();
        stubInvoke(expected);
        KeyRequest body = new KeyRequest();
        DetailedHookKeyInstance actual = api.createHookKey(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateHookKey_WithHeaders() throws Exception {
        stubInvoke(new DetailedHookKeyInstance());
        api.createHookKey(new KeyRequest(), Collections.singletonMap("X-C","1"));
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
    public void testCreateHookKey_MissingBody() {
        try {
            api.createHookKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("keyrequest"));
        }
    }

    /* deleteHookKey */
    @Test
    public void testDeleteHookKey_Success() throws Exception {
        stubVoidInvoke();
        api.deleteHookKey("k1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys/k1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("k1");
    }

    @Test
    public void testDeleteHookKey_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteHookKey("k2", Collections.singletonMap("X-D","v"));
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
    public void testDeleteHookKey_MissingId() {
        try {
            api.deleteHookKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("id"));
        }
    }

    /* getHookKey */
    @Test
    public void testGetHookKey_Success() throws Exception {
        HookKey expected = new HookKey();
        stubInvoke(expected);
        HookKey actual = api.getHookKey("k3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys/k3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("k3");
    }

    @Test
    public void testGetHookKey_WithHeaders() throws Exception {
        stubInvoke(new HookKey());
        api.getHookKey("k4", Collections.singletonMap("X-G","1"));
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
    public void testGetHookKey_MissingId() {
        try {
            api.getHookKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("id"));
        }
    }

    /* getPublicKey */
    @Test
    public void testGetPublicKey_Success() throws Exception {
        Embedded expected = new Embedded();
        stubInvoke(expected);
        Embedded actual = api.getPublicKey("pk1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys/public/pk1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("pk1");
    }

    @Test
    public void testGetPublicKey_WithHeaders() throws Exception {
        stubInvoke(new Embedded());
        api.getPublicKey("pk2", Collections.singletonMap("X-P","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-P"));
    }

    @Test
    public void testGetPublicKey_MissingKeyId() {
        try {
            api.getPublicKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("keyid"));
        }
    }

    /* listHookKeys */
    @Test
    public void testListHookKeys_Success() throws Exception {
        List<HookKey> expected = Arrays.asList(new HookKey());
        stubInvoke(expected);
        List<HookKey> actual = api.listHookKeys();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListHookKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listHookKeys(Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    /* replaceHookKey */
    @Test
    public void testReplaceHookKey_Success() throws Exception {
        DetailedHookKeyInstance expected = new DetailedHookKeyInstance();
        stubInvoke(expected);
        KeyRequest body = new KeyRequest();
        DetailedHookKeyInstance actual = api.replaceHookKey("k5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/hook-keys/k5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("k5");
    }

    @Test
    public void testReplaceHookKey_WithHeaders() throws Exception {
        stubInvoke(new DetailedHookKeyInstance());
        api.replaceHookKey("k6", new KeyRequest(), Collections.singletonMap("X-R","v"));
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
    public void testReplaceHookKey_MissingId() {
        try {
            api.replaceHookKey(null, new KeyRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("id"));
        }
    }

    @Test
    public void testReplaceHookKey_MissingBody() {
        try {
            api.replaceHookKey("k7", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().toLowerCase().contains("keyrequest"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateHookKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createHookKey(new KeyRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.HookKeyApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

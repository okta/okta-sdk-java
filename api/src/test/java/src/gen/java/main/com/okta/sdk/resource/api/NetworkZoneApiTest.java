package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.NetworkZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class NetworkZoneApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.NetworkZoneApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.NetworkZoneApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        // Safe stub: handles nulls and avoids char[] overload issues
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0, String.class);
            Object valObj = inv.getArgument(1);
            String val = (valObj == null) ? null : String.valueOf(valObj); // forces Object overload
            return Collections.singletonList(new Pair(name, val));
        });
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

    /* activateNetworkZone */
    @Test
    public void testActivateNetworkZone_Success() throws Exception {
        NetworkZone expected = new NetworkZone();
        stubInvoke(expected);
        NetworkZone actual = api.activateNetworkZone("nz1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones/nz1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("nz1");
    }

    @Test
    public void testActivateNetworkZone_WithHeaders() throws Exception {
        stubInvoke(new NetworkZone());
        api.activateNetworkZone("nzH", Collections.singletonMap("X-A","v"));
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
    public void testActivateNetworkZone_MissingId() {
        try { api.activateNetworkZone(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deactivateNetworkZone */
    @Test
    public void testDeactivateNetworkZone_Success() throws Exception {
        NetworkZone expected = new NetworkZone();
        stubInvoke(expected);
        NetworkZone actual = api.deactivateNetworkZone("nz2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones/nz2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("nz2");
    }

    @Test
    public void testDeactivateNetworkZone_MissingId() {
        try { api.deactivateNetworkZone(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* createNetworkZone */
    @Test
    public void testCreateNetworkZone_Success() throws Exception {
        NetworkZone expected = new NetworkZone();
        stubInvoke(expected);
        NetworkZone body = new NetworkZone();
        NetworkZone actual = api.createNetworkZone(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateNetworkZone_WithHeaders() throws Exception {
        stubInvoke(new NetworkZone());
        api.createNetworkZone(new NetworkZone(), Collections.singletonMap("X-C","1"));
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
    public void testCreateNetworkZone_MissingBody() {
        try { api.createNetworkZone(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* getNetworkZone */
    @Test
    public void testGetNetworkZone_Success() throws Exception {
        NetworkZone expected = new NetworkZone();
        stubInvoke(expected);
        NetworkZone actual = api.getNetworkZone("nz3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones/nz3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("nz3");
    }

    @Test
    public void testGetNetworkZone_WithHeaders() throws Exception {
        stubInvoke(new NetworkZone());
        api.getNetworkZone("nzG", Collections.singletonMap("X-G","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetNetworkZone_MissingId() {
        try { api.getNetworkZone(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deleteNetworkZone */
    @Test
    public void testDeleteNetworkZone_Success() throws Exception {
        stubVoidInvoke();
        api.deleteNetworkZone("nz4");
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones/nz4"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("nz4");
    }

    @Test
    public void testDeleteNetworkZone_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteNetworkZone("nzDel", Collections.singletonMap("X-D","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteNetworkZone_MissingId() {
        try { api.deleteNetworkZone(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listNetworkZones */
    @Test
    public void testListNetworkZones_AllParams() throws Exception {
        List<NetworkZone> expected = Arrays.asList(new NetworkZone());
        stubInvoke(expected);
        List<NetworkZone> list = api.listNetworkZones("cursor1", 100, "system eq false");
        assertSame(expected, list);
        verify(apiClient).parameterToPair("after", "cursor1");
        verify(apiClient).parameterToPair("limit", 100);
        verify(apiClient).parameterToPair("filter", "system eq false");
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListNetworkZones_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<NetworkZone> list = api.listNetworkZones(null, null, null);
        assertNotNull(list);
    }

    @Test
    public void testListNetworkZones_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listNetworkZones(null, null, null, Collections.singletonMap("X-L","v"));
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

    /* replaceNetworkZone */
    @Test
    public void testReplaceNetworkZone_Success() throws Exception {
        NetworkZone expected = new NetworkZone();
        stubInvoke(expected);
        NetworkZone body = new NetworkZone();
        NetworkZone actual = api.replaceNetworkZone("nz5", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/zones/nz5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("nz5");
    }

    @Test
    public void testReplaceNetworkZone_WithHeaders() throws Exception {
        stubInvoke(new NetworkZone());
        api.replaceNetworkZone("nzRH", new NetworkZone(), Collections.singletonMap("X-R","v"));
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
    public void testReplaceNetworkZone_MissingId() {
        try { api.replaceNetworkZone(null, new NetworkZone()); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testReplaceNetworkZone_MissingBody() {
        try { api.replaceNetworkZone("nzX", null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testCreateNetworkZone_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createNetworkZone(new NetworkZone());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("boom"));
        }
    }

    @Test
    public void testDeleteNetworkZone_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteNetworkZone("nzErr");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.NetworkZoneApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

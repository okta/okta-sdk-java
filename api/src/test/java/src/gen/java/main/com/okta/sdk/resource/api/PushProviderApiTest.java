package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ProviderType;
import com.okta.sdk.resource.model.PushProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class PushProviderApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.PushProviderApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.PushProviderApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        )).thenReturn(value);
    }

    /* createPushProvider */
    @Test
    public void testCreatePushProvider_Success() throws Exception {
        PushProvider expected = new PushProvider();
        stubInvoke(expected);
        PushProvider body = new PushProvider();
        PushProvider actual = api.createPushProvider(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/push-providers"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreatePushProvider_WithHeaders() throws Exception {
        stubInvoke(new PushProvider());
        Map<String,String> headers = Collections.singletonMap("X-Custom","v");
        api.createPushProvider(new PushProvider(), headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headerCap.getValue().get("X-Custom"));
    }

    @Test
    public void testCreatePushProvider_MissingBody() {
        try {
            api.createPushProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("pushprovider"));
        }
    }

    /* deletePushProvider */
    @Test
    public void testDeletePushProvider_Success() throws Exception {
        stubInvoke(null);
        api.deletePushProvider("PP1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/push-providers/PP1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeletePushProvider_WithHeaders() throws Exception {
        stubInvoke(null);
        api.deletePushProvider("PP2", Collections.singletonMap("X-Del","1"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headerCap.getValue().get("X-Del"));
    }

    @Test
    public void testDeletePushProvider_MissingId() {
        try {
            api.deletePushProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("pushproviderid"));
        }
    }

    /* getPushProvider */
    @Test
    public void testGetPushProvider_Success() throws Exception {
        PushProvider expected = new PushProvider();
        stubInvoke(expected);
        PushProvider actual = api.getPushProvider("PPG1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/push-providers/PPG1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetPushProvider_WithHeaders() throws Exception {
        stubInvoke(new PushProvider());
        api.getPushProvider("PPG2", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headerCap.getValue().get("X-H"));
    }

    @Test
    public void testGetPushProvider_MissingId() {
        try {
            api.getPushProvider(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("pushproviderid"));
        }
    }

    /* listPushProviders */
    @Test
    public void testListPushProviders_Success() throws Exception {
        List<PushProvider> expected = new ArrayList<>();
        stubInvoke(expected);
        List<PushProvider> actual = api.listPushProviders(ProviderType.FCM);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/push-providers"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        verify(apiClient).parameterToPair(eq("type"), eq(ProviderType.FCM));
    }

    @Test
    public void testListPushProviders_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listPushProviders(null, Collections.singletonMap("X-L","y"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("y", headerCap.getValue().get("X-L"));
    }

    /* replacePushProvider */
    @Test
    public void testReplacePushProvider_Success() throws Exception {
        PushProvider expected = new PushProvider();
        stubInvoke(expected);
        PushProvider body = new PushProvider();
        PushProvider actual = api.replacePushProvider("PPX1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/push-providers/PPX1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplacePushProvider_WithHeaders() throws Exception {
        stubInvoke(new PushProvider());
        api.replacePushProvider("PPZ", new PushProvider(), Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headerCap.getValue().get("X-R"));
    }

    @Test
    public void testReplacePushProvider_MissingArgs() {
        try { api.replacePushProvider(null, new PushProvider()); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
        try { api.replacePushProvider("ID", null); fail("Expected"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
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
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.createPushProvider(new PushProvider());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalledWithPath_Get() throws Exception {
        stubInvoke(new PushProvider());
        api.getPushProvider("PPA1");
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/push-providers/PPA1"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.PushProviderApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

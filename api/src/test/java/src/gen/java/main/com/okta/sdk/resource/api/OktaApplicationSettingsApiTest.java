package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AdminConsoleSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OktaApplicationSettingsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OktaApplicationSettingsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OktaApplicationSettingsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        // Safe parameterToPair stub (even if null)
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object v = inv.getArgument(1);
            return Collections.singletonList(new com.okta.sdk.resource.client.Pair(name, v == null ? null : String.valueOf(v)));
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

    /* getFirstPartyAppSettings */
    @Test
    public void testGetFirstPartyAppSettings_Success() throws Exception {
        AdminConsoleSettings expected = new AdminConsoleSettings();
        stubInvoke(expected);
        AdminConsoleSettings actual = api.getFirstPartyAppSettings("admin-console");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/first-party-app-settings/admin-console"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("admin-console");
    }

    @Test
    public void testGetFirstPartyAppSettings_WithHeaders() throws Exception {
        stubInvoke(new AdminConsoleSettings());
        api.getFirstPartyAppSettings("admin-console", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-H"));
    }

    @Test
    public void testGetFirstPartyAppSettings_MissingAppName() {
        try {
            api.getFirstPartyAppSettings(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* replaceFirstPartyAppSettings */
    @Test
    public void testReplaceFirstPartyAppSettings_Success() throws Exception {
        AdminConsoleSettings expected = new AdminConsoleSettings();
        stubInvoke(expected);
        AdminConsoleSettings body = new AdminConsoleSettings();
        AdminConsoleSettings actual = api.replaceFirstPartyAppSettings("admin-console", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/first-party-app-settings/admin-console"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("admin-console");
    }

    @Test
    public void testReplaceFirstPartyAppSettings_WithHeaders() throws Exception {
        stubInvoke(new AdminConsoleSettings());
        AdminConsoleSettings body = new AdminConsoleSettings();
        api.replaceFirstPartyAppSettings("admin-console", body, Collections.singletonMap("X-R","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceFirstPartyAppSettings_MissingAppName() {
        try {
            api.replaceFirstPartyAppSettings(null, new AdminConsoleSettings());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testReplaceFirstPartyAppSettings_MissingBody() {
        try {
            api.replaceFirstPartyAppSettings("admin-console", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testReplaceFirstPartyAppSettings_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.replaceFirstPartyAppSettings("admin-console", new AdminConsoleSettings());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("bad"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OktaApplicationSettingsApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

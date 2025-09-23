package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.RateLimitSettingsApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.PerClientRateLimitSettings;
import com.okta.sdk.resource.model.RateLimitAdminNotifications;
import com.okta.sdk.resource.model.RateLimitWarningThresholdRequest;
import com.okta.sdk.resource.model.RateLimitWarningThresholdResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RateLimitSettingsApiTest {

    private ApiClient apiClient;
    private RateLimitSettingsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new RateLimitSettingsApi(apiClient);

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

    /* getRateLimitSettingsAdminNotifications */
    @Test
    public void testGetRateLimitSettingsAdminNotifications_Success() throws Exception {
        RateLimitAdminNotifications expected = new RateLimitAdminNotifications();
        stubInvoke(expected);
        RateLimitAdminNotifications actual = api.getRateLimitSettingsAdminNotifications();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/admin-notifications"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRateLimitSettingsAdminNotifications_WithHeaders() throws Exception {
        stubInvoke(new RateLimitAdminNotifications());
        api.getRateLimitSettingsAdminNotifications(Collections.singletonMap("X-H","v"));
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

    /* getRateLimitSettingsPerClient */
    @Test
    public void testGetRateLimitSettingsPerClient_Success() throws Exception {
        PerClientRateLimitSettings expected = new PerClientRateLimitSettings();
        stubInvoke(expected);
        PerClientRateLimitSettings actual = api.getRateLimitSettingsPerClient();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/per-client"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetRateLimitSettingsPerClient_WithHeaders() throws Exception {
        stubInvoke(new PerClientRateLimitSettings());
        api.getRateLimitSettingsPerClient(Collections.singletonMap("X-A","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", cap.getValue().get("X-A"));
    }

    /* getRateLimitSettingsWarningThreshold */
    @Test
    public void testGetRateLimitSettingsWarningThreshold_Success() throws Exception {
        RateLimitWarningThresholdResponse expected = new RateLimitWarningThresholdResponse();
        stubInvoke(expected);
        RateLimitWarningThresholdResponse actual = api.getRateLimitSettingsWarningThreshold();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/warning-threshold"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    /* replaceRateLimitSettingsAdminNotifications (required) */
    @Test
    public void testReplaceRateLimitSettingsAdminNotifications_Success() throws Exception {
        RateLimitAdminNotifications expected = new RateLimitAdminNotifications();
        stubInvoke(expected);
        RateLimitAdminNotifications body = new RateLimitAdminNotifications();
        RateLimitAdminNotifications actual = api.replaceRateLimitSettingsAdminNotifications(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/admin-notifications"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRateLimitSettingsAdminNotifications_WithHeaders() throws Exception {
        stubInvoke(new RateLimitAdminNotifications());
        api.replaceRateLimitSettingsAdminNotifications(new RateLimitAdminNotifications(),
            Collections.singletonMap("X-R","h"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("h", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceRateLimitSettingsAdminNotifications_MissingBody() {
        try {
            api.replaceRateLimitSettingsAdminNotifications(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("rateLimitAdminNotifications"));
        }
    }

    /* replaceRateLimitSettingsPerClient (required) */
    @Test
    public void testReplaceRateLimitSettingsPerClient_Success() throws Exception {
        PerClientRateLimitSettings expected = new PerClientRateLimitSettings();
        stubInvoke(expected);
        PerClientRateLimitSettings body = new PerClientRateLimitSettings();
        PerClientRateLimitSettings actual = api.replaceRateLimitSettingsPerClient(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/per-client"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRateLimitSettingsPerClient_MissingBody() {
        try {
            api.replaceRateLimitSettingsPerClient(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("perClientRateLimitSettings"));
        }
    }

    /* replaceRateLimitSettingsWarningThreshold (optional) */
    @Test
    public void testReplaceRateLimitSettingsWarningThreshold_WithBody() throws Exception {
        RateLimitWarningThresholdResponse expected = new RateLimitWarningThresholdResponse();
        stubInvoke(expected);
        RateLimitWarningThresholdRequest body = new RateLimitWarningThresholdRequest();
        RateLimitWarningThresholdResponse actual = api.replaceRateLimitSettingsWarningThreshold(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/rate-limit-settings/warning-threshold"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRateLimitSettingsWarningThreshold_NullBody() throws Exception {
        RateLimitWarningThresholdResponse expected = new RateLimitWarningThresholdResponse();
        stubInvoke(expected);
        RateLimitWarningThresholdResponse actual = api.replaceRateLimitSettingsWarningThreshold((RateLimitWarningThresholdRequest) null);
        assertSame(expected, actual);
    }

    @Test
    public void testReplaceRateLimitSettingsWarningThreshold_WithHeaders() throws Exception {
        stubInvoke(new RateLimitWarningThresholdResponse());
        api.replaceRateLimitSettingsWarningThreshold(new RateLimitWarningThresholdRequest(),
            Collections.singletonMap("X-W","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headers.getValue().get("X-W"));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_FromGet() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.getRateLimitSettingsPerClient();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    @Test
    public void testApiExceptionPropagates_FromPut() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"fail"));
        try {
            api.replaceRateLimitSettingsAdminNotifications(new RateLimitAdminNotifications());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("fail"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptAndContentTypeCalled() throws Exception {
        stubInvoke(new RateLimitAdminNotifications());
        api.getRateLimitSettingsAdminNotifications();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/rate-limit-settings/admin-notifications"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = RateLimitSettingsApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.SsfReceiverApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.SecurityEventsProviderRequest;
import com.okta.sdk.resource.model.SecurityEventsProviderResponse;
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
public class SsfReceiverApiTest {

    private ApiClient apiClient;
    private SsfReceiverApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new SsfReceiverApi(apiClient);

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

    /* activateSecurityEventsProviderInstance */
    @Test
    public void testActivateSecurityEventsProviderInstance_Success() throws Exception {
        SecurityEventsProviderResponse expected = new SecurityEventsProviderResponse();
        stubInvoke(expected);
        SecurityEventsProviderResponse actual = api.activateSecurityEventsProviderInstance("sep1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers/sep1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("sep1");
    }

    @Test
    public void testActivateSecurityEventsProviderInstance_MissingId() {
        try {
            api.activateSecurityEventsProviderInstance(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("securityEventProviderId"));
        }
    }

    /* createSecurityEventsProviderInstance */
    @Test
    public void testCreateSecurityEventsProviderInstance_Success() throws Exception {
        SecurityEventsProviderResponse expected = new SecurityEventsProviderResponse();
        stubInvoke(expected);
        SecurityEventsProviderRequest body = new SecurityEventsProviderRequest();
        SecurityEventsProviderResponse actual = api.createSecurityEventsProviderInstance(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateSecurityEventsProviderInstance_MissingBody() {
        try {
            api.createSecurityEventsProviderInstance(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("instance"));
        }
    }

    /* deactivateSecurityEventsProviderInstance */
    @Test
    public void testDeactivateSecurityEventsProviderInstance_Success() throws Exception {
        SecurityEventsProviderResponse expected = new SecurityEventsProviderResponse();
        stubInvoke(expected);
        SecurityEventsProviderResponse actual = api.deactivateSecurityEventsProviderInstance("sep2");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers/sep2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("sep2");
    }

    /* deleteSecurityEventsProviderInstance */
    @Test
    public void testDeleteSecurityEventsProviderInstance_Success() throws Exception {
        stubVoidInvoke();
        api.deleteSecurityEventsProviderInstance("sep3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers/sep3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("sep3");
    }

    @Test
    public void testDeleteSecurityEventsProviderInstance_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteSecurityEventsProviderInstance("sep4", Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    /* getSecurityEventsProviderInstance */
    @Test
    public void testGetSecurityEventsProviderInstance_Success() throws Exception {
        SecurityEventsProviderResponse expected = new SecurityEventsProviderResponse();
        stubInvoke(expected);
        SecurityEventsProviderResponse actual = api.getSecurityEventsProviderInstance("sep5");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers/sep5"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("sep5");
    }

    /* listSecurityEventsProviderInstances */
    @Test
    public void testListSecurityEventsProviderInstances_Success() throws Exception {
        List<SecurityEventsProviderResponse> expected = Arrays.asList(new SecurityEventsProviderResponse());
        stubInvoke(expected);
        List<SecurityEventsProviderResponse> actual = api.listSecurityEventsProviderInstances();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* replaceSecurityEventsProviderInstance */
    @Test
    public void testReplaceSecurityEventsProviderInstance_Success() throws Exception {
        SecurityEventsProviderResponse expected = new SecurityEventsProviderResponse();
        stubInvoke(expected);
        SecurityEventsProviderRequest body = new SecurityEventsProviderRequest();
        SecurityEventsProviderResponse actual = api.replaceSecurityEventsProviderInstance("sep6", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/security-events-providers/sep6"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("sep6");
    }

    @Test
    public void testReplaceSecurityEventsProviderInstance_MissingBody() {
        try {
            api.replaceSecurityEventsProviderInstance("sep7", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("instance"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(404, "Not Found"));
        try {
            api.getSecurityEventsProviderInstance("non-existent-id");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(404, ex.getCode());
            assertEquals("Not Found", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = SsfReceiverApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
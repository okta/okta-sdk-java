package com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.RiskEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RiskEventApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RiskEventApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RiskEventApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString()))
            .thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class)))
            .thenReturn("application/json");
    }

    /* sendRiskEvents (success) */
    @Test
    public void testSendRiskEvents_Success() throws Exception {
        List<RiskEvent> body = Arrays.asList(new RiskEvent(), new RiskEvent());

        api.sendRiskEvents(body);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/risk/events/ip"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertSame(body, bodyCap.getValue());
    }

    /* sendRiskEvents (headers) */
    @Test
    public void testSendRiskEvents_WithHeaders() throws Exception {
        List<RiskEvent> body = Collections.singletonList(new RiskEvent());
        Map<String,String> headers = Collections.singletonMap("X-Custom","v");

        api.sendRiskEvents(body, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headerCap.getValue().get("X-Custom"));
    }

    /* sendRiskEvents (missing required) */
    @Test
    public void testSendRiskEvents_MissingInstance() {
        try {
            api.sendRiskEvents((List<RiskEvent>) null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().toLowerCase().contains("instance"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testSendRiskEvents_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(502,"downstream"));
        try {
            api.sendRiskEvents(Collections.singletonList(new RiskEvent()));
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("downstream"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderNegotiationCalled() throws Exception {
        api.sendRiskEvents(Collections.singletonList(new RiskEvent()));
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/risk/events/ip"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() {
        ObjectMapper mapper = com.okta.sdk.resource.api.RiskEventApi.getObjectMapper();
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

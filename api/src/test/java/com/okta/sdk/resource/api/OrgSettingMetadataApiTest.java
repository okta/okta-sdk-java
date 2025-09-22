package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.WellKnownOrgMetadata;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class OrgSettingMetadataApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingMetadataApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingMetadataApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString()))
            .thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class)))
            .thenReturn("application/json");
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

    @Test
    public void testGetWellknownOrgMetadata_Success() throws Exception {
        WellKnownOrgMetadata expected = new WellKnownOrgMetadata();
        stubInvoke(expected);
        WellKnownOrgMetadata actual = api.getWellknownOrgMetadata();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/.well-known/okta-organization"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetWellknownOrgMetadata_WithHeaders() throws Exception {
        stubInvoke(new WellKnownOrgMetadata());
        api.getWellknownOrgMetadata(Collections.singletonMap("X-Custom","v"));
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headersCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headersCap.getValue().get("X-Custom"));
    }

    @Test
    public void testHeaderNegotiation() throws Exception {
        stubInvoke(new WellKnownOrgMetadata());
        api.getWellknownOrgMetadata();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/.well-known/okta-organization"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testGetWellknownOrgMetadata_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.getWellknownOrgMetadata();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("unavailable"));
        }
    }

    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingMetadataApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

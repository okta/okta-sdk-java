package com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.BouncesRemoveListObj;
import com.okta.sdk.resource.model.BouncesRemoveListResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class EmailCustomizationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.EmailCustomizationApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.EmailCustomizationApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    private void stubInvoke(BouncesRemoveListResult value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any())
        ).thenReturn(value);
    }

    @Test
    public void testBulkRemoveEmailAddressBounces_Success_WithBody() throws Exception {
        BouncesRemoveListObj body = new BouncesRemoveListObj();
        BouncesRemoveListResult expected = new BouncesRemoveListResult();
        stubInvoke(expected);

        BouncesRemoveListResult actual = api.bulkRemoveEmailAddressBounces(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/email/bounces/remove-list"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testBulkRemoveEmailAddressBounces_Success_NullBody() throws Exception {
        BouncesRemoveListResult expected = new BouncesRemoveListResult();
        stubInvoke(expected);

        BouncesRemoveListResult actual = api.bulkRemoveEmailAddressBounces(null);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/org/email/bounces/remove-list"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any());
    }

    @Test
    public void testBulkRemoveEmailAddressBounces_WithHeaders() throws Exception {
        stubInvoke(new BouncesRemoveListResult());
        Map<String,String> headers = Collections.singletonMap("X-Test","v");
        api.bulkRemoveEmailAddressBounces(new BouncesRemoveListObj(), headers);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any());
        assertEquals("v", hdrCap.getValue().get("X-Test"));
    }

    @Test
    public void testBulkRemoveEmailAddressBounces_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any())
        ).thenThrow(new ApiException(500,"boom"));

        try {
            api.bulkRemoveEmailAddressBounces(new BouncesRemoveListObj());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    @Test
    public void testGetObjectMapperConfiguration() {
        ObjectMapper mapper = com.okta.sdk.resource.api.EmailCustomizationApi.getObjectMapper();
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

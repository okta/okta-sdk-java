package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ResourceSetResource;
import com.okta.sdk.resource.model.ResourceSetResourcePutRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class RoleCResourceSetResourceApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.RoleCResourceSetResourceApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.RoleCResourceSetResourceApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    private void stubInvokeReplace(ResourceSetResource value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* replaceResourceSetResource */
    @Test
    public void testReplaceResourceSetResource_Success() throws Exception {
        ResourceSetResourcePutRequest body = new ResourceSetResourcePutRequest();
        ResourceSetResource expected = new ResourceSetResource();
        stubInvokeReplace(expected);

        ResourceSetResource actual = api.replaceResourceSetResource("RS1","RID1", body, Collections.emptyMap());
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/iam/resource-sets/RS1/resources/RID1"),
            eq("PUT"),
            anyList(), anyList(),
            anyString(),
            eq(body),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class),
            any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceResourceSetResource_WithHeaders() throws Exception {
        ResourceSetResourcePutRequest body = new ResourceSetResourcePutRequest();
        stubInvokeReplace(new ResourceSetResource());
        Map<String,String> hdr = Collections.singletonMap("X-CUSTOM","v1");

        api.replaceResourceSetResource("RS2","RID2", body, hdr);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class),
            any(TypeReference.class)
        );
        assertEquals("v1", headerCap.getValue().get("X-CUSTOM"));
    }

    @Test
    public void testReplaceResourceSetResource_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502, "upstream"));
        try {
            api.replaceResourceSetResource("RSX","RIX", new ResourceSetResourcePutRequest(), Collections.emptyMap());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("upstream"));
        }
    }

    @Test
    public void testReplaceResourceSetResource_HeaderNegotiation() throws Exception {
        stubInvokeReplace(new ResourceSetResource());
        api.replaceResourceSetResource("RS3","RID3", new ResourceSetResourcePutRequest(), Collections.emptyMap());
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/iam/resource-sets/RS3/resources/RID3"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Ignore("Enable after null-check placeholders throw ApiException(400, ...)")
    @Test
    public void testReplaceResourceSetResource_MissingParams() {
        expect400(() -> api.replaceResourceSetResource(null,"RID", new ResourceSetResourcePutRequest(), Collections.emptyMap()));
        expect400(() -> api.replaceResourceSetResource("RS", null, new ResourceSetResourcePutRequest(), Collections.emptyMap()));
        expect400(() -> api.replaceResourceSetResource("RS","RID", null, Collections.emptyMap()));
    }

    /* ObjectMapper */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.RoleCResourceSetResourceApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try { r.run(); fail("Expected 400"); }
        catch (ApiException e){ assertEquals(400, e.getCode()); }
    }
}

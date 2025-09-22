package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OrgContactTypeObj;
import com.okta.sdk.resource.model.OrgContactUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OrgSettingContactApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingContactApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingContactApi(apiClient);

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

    /* getOrgContactUser */
    @Test
    public void testGetOrgContactUser_Success() throws Exception {
        OrgContactUser expected = new OrgContactUser();
        stubInvoke(expected);
        OrgContactUser actual = api.getOrgContactUser("PRIMARY");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/contacts/PRIMARY"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetOrgContactUser_WithHeaders() throws Exception {
        stubInvoke(new OrgContactUser());
        api.getOrgContactUser("PRIMARY", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-H"));
    }

    @Test
    public void testGetOrgContactUser_MissingContactType() {
        try {
            api.getOrgContactUser(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testGetOrgContactUser_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getOrgContactUser("PRIMARY");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    /* listOrgContactTypes */
    @Test
    public void testListOrgContactTypes_Success() throws Exception {
        List<OrgContactTypeObj> expected = new ArrayList<>();
        stubInvoke(expected);
        List<OrgContactTypeObj> actual = api.listOrgContactTypes();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/contacts"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListOrgContactTypes_WithHeaders() throws Exception {
        stubInvoke(new ArrayList<>());
        api.listOrgContactTypes(Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headers.getValue().get("X-L"));
    }

    /* replaceOrgContactUser */
    @Test
    public void testReplaceOrgContactUser_Success() throws Exception {
        OrgContactUser expected = new OrgContactUser();
        stubInvoke(expected);
        OrgContactUser body = new OrgContactUser();
        OrgContactUser actual = api.replaceOrgContactUser("PRIMARY", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/contacts/PRIMARY"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceOrgContactUser_WithHeaders() throws Exception {
        stubInvoke(new OrgContactUser());
        api.replaceOrgContactUser("PRIMARY", new OrgContactUser(), Collections.singletonMap("X-R","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceOrgContactUser_MissingContactType() {
        try {
            api.replaceOrgContactUser(null, new OrgContactUser());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testReplaceOrgContactUser_MissingUser() {
        try {
            api.replaceOrgContactUser("PRIMARY", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testReplaceOrgContactUser_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.replaceOrgContactUser("PRIMARY", new OrgContactUser());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingContactApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

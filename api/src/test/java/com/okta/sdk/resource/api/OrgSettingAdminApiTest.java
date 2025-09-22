package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.AutoAssignAdminAppSetting;
import com.okta.sdk.resource.model.ClientPrivilegesSetting;
import com.okta.sdk.resource.model.ThirdPartyAdminSetting;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OrgSettingAdminApiTest {

    private ApiClient apiClient;
    private OrgSettingAdminApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new OrgSettingAdminApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String n = inv.getArgument(0);
            Object v = inv.getArgument(1);
            return Collections.singletonList(new Pair(n, v == null ? null : String.valueOf(v)));
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

    /* assignClientPrivilegesSetting */
    @Test
    public void testAssignClientPrivilegesSetting_Success() throws Exception {
        ClientPrivilegesSetting expected = new ClientPrivilegesSetting();
        stubInvoke(expected);
        ClientPrivilegesSetting body = new ClientPrivilegesSetting();
        ClientPrivilegesSetting actual = api.assignClientPrivilegesSetting(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/settings/clientPrivilegesSetting"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testAssignClientPrivilegesSetting_WithHeaders() throws Exception {
        stubInvoke(new ClientPrivilegesSetting());
        api.assignClientPrivilegesSetting(new ClientPrivilegesSetting(), Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-H"));
    }

    /* getAutoAssignAdminAppSetting */
    @Test
    public void testGetAutoAssignAdminAppSetting_Success() throws Exception {
        AutoAssignAdminAppSetting expected = new AutoAssignAdminAppSetting();
        stubInvoke(expected);
        AutoAssignAdminAppSetting actual = api.getAutoAssignAdminAppSetting();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/settings/autoAssignAdminAppSetting"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetAutoAssignAdminAppSetting_WithHeaders() throws Exception {
        stubInvoke(new AutoAssignAdminAppSetting());
        api.getAutoAssignAdminAppSetting(Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headers.getValue().get("X-G"));
    }

    /* getClientPrivilegesSetting */
    @Test
    public void testGetClientPrivilegesSetting_Success() throws Exception {
        ClientPrivilegesSetting expected = new ClientPrivilegesSetting();
        stubInvoke(expected);
        ClientPrivilegesSetting actual = api.getClientPrivilegesSetting();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/settings/clientPrivilegesSetting"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetClientPrivilegesSetting_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getClientPrivilegesSetting();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    /* getThirdPartyAdminSetting */
    @Test
    public void testGetThirdPartyAdminSetting_Success() throws Exception {
        ThirdPartyAdminSetting expected = new ThirdPartyAdminSetting();
        stubInvoke(expected);
        ThirdPartyAdminSetting actual = api.getThirdPartyAdminSetting();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/orgSettings/thirdPartyAdminSetting"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* updateAutoAssignAdminAppSetting */
    @Test
    public void testUpdateAutoAssignAdminAppSetting_Success() throws Exception {
        AutoAssignAdminAppSetting expected = new AutoAssignAdminAppSetting();
        stubInvoke(expected);
        AutoAssignAdminAppSetting body = new AutoAssignAdminAppSetting();
        AutoAssignAdminAppSetting actual = api.updateAutoAssignAdminAppSetting(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/settings/autoAssignAdminAppSetting"), anyString(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUpdateAutoAssignAdminAppSetting_WithHeaders() throws Exception {
        stubInvoke(new AutoAssignAdminAppSetting());
        api.updateAutoAssignAdminAppSetting(new AutoAssignAdminAppSetting(), Collections.singletonMap("X-U","z"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("z", headers.getValue().get("X-U"));
    }

    /* updateThirdPartyAdminSetting */
    @Test
    public void testUpdateThirdPartyAdminSetting_Success() throws Exception {
        ThirdPartyAdminSetting expected = new ThirdPartyAdminSetting();
        stubInvoke(expected);
        ThirdPartyAdminSetting body = new ThirdPartyAdminSetting();
        ThirdPartyAdminSetting actual = api.updateThirdPartyAdminSetting(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/orgSettings/thirdPartyAdminSetting"), anyString(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testUpdateThirdPartyAdminSetting_WithHeaders() throws Exception {
        stubInvoke(new ThirdPartyAdminSetting());
        api.updateThirdPartyAdminSetting(new ThirdPartyAdminSetting(), Collections.singletonMap("X-T","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-T"));
    }

    @Test
    public void testUpdateThirdPartyAdminSetting_MissingBody() {
        try {
            api.updateThirdPartyAdminSetting(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testUpdateThirdPartyAdminSetting_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.updateThirdPartyAdminSetting(new ThirdPartyAdminSetting());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = OrgSettingAdminApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

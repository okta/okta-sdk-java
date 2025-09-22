package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AttackProtectionAuthenticatorSettings;
import com.okta.sdk.resource.model.UserLockoutSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AttackProtectionApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AttackProtectionApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AttackProtectionApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // getAuthenticatorSettings
    @Test
    public void testGetAuthenticatorSettings_Success() throws Exception {
        List<AttackProtectionAuthenticatorSettings> expected = Arrays.asList(
            new AttackProtectionAuthenticatorSettings(),
            new AttackProtectionAuthenticatorSettings()
        );
        whenInvokeReturn(expected);

        List<AttackProtectionAuthenticatorSettings> actual = api.getAuthenticatorSettings();
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("GET", methodCap.getValue());
        assertEquals("/attack-protection/api/v1/authenticator-settings", pathCap.getValue());
    }

    @Test
    public void testGetAuthenticatorSettings_WithAdditionalHeaders() throws Exception {
        List<AttackProtectionAuthenticatorSettings> expected = Collections.singletonList(
            new AttackProtectionAuthenticatorSettings()
        );
        whenInvokeReturn(expected);

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test-Header", "123");
        api.getAuthenticatorSettings(headers);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("123", headerCap.getValue().get("X-Test-Header"));
    }

    // getUserLockoutSettings
    @Test
    public void testGetUserLockoutSettings_Success() throws Exception {
        List<UserLockoutSettings> expected = Arrays.asList(new UserLockoutSettings(), new UserLockoutSettings());
        whenInvokeReturn(expected);

        List<UserLockoutSettings> actual = api.getUserLockoutSettings();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/attack-protection/api/v1/user-lockout-settings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetUserLockoutSettings_WithAdditionalHeaders() throws Exception {
        List<UserLockoutSettings> expected = Collections.singletonList(new UserLockoutSettings());
        whenInvokeReturn(expected);

        api.getUserLockoutSettings(Collections.singletonMap("X-Extra", "A"));
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("A", headerCap.getValue().get("X-Extra"));
    }

    // replaceAuthenticatorSettings
    @Test
    public void testReplaceAuthenticatorSettings_Success() throws Exception {
        AttackProtectionAuthenticatorSettings body = new AttackProtectionAuthenticatorSettings();
        AttackProtectionAuthenticatorSettings expected = new AttackProtectionAuthenticatorSettings();
        whenInvokeReturn(expected);

        AttackProtectionAuthenticatorSettings actual = api.replaceAuthenticatorSettings(body);
        assertSame(expected, actual);

        ArgumentCaptor<String> methodCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), methodCap.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("PUT", methodCap.getValue());
        assertEquals("/attack-protection/api/v1/authenticator-settings", pathCap.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthenticatorSettings_MissingParam() throws Exception {
        api.replaceAuthenticatorSettings((AttackProtectionAuthenticatorSettings) null);
    }

    // replaceUserLockoutSettings
    @Test
    public void testReplaceUserLockoutSettings_Success() throws Exception {
        UserLockoutSettings body = new UserLockoutSettings();
        UserLockoutSettings expected = new UserLockoutSettings();
        whenInvokeReturn(expected);

        UserLockoutSettings actual = api.replaceUserLockoutSettings(body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/attack-protection/api/v1/user-lockout-settings"), eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceUserLockoutSettings_WithAdditionalHeaders() throws Exception {
        UserLockoutSettings body = new UserLockoutSettings();
        UserLockoutSettings expected = new UserLockoutSettings();
        whenInvokeReturn(expected);

        api.replaceUserLockoutSettings(body, Collections.singletonMap("X-ReqId", "req-1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("req-1", headerCap.getValue().get("X-ReqId"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceUserLockoutSettings_MissingParam() throws Exception {
        api.replaceUserLockoutSettings((UserLockoutSettings) null);
    }

    // Helpers
    private <T> void whenInvokeReturn(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }
}

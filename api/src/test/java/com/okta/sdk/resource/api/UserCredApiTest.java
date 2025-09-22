package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UserCredApiTest {

    private ApiClient apiClient;
    private UserCredApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserCredApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
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

    /* changePassword */
    @Test
    public void testChangePassword_Success() throws Exception {
        UserCredentials expected = new UserCredentials();
        stubInvoke(expected);
        ChangePasswordRequest body = new ChangePasswordRequest();
        UserCredentials actual = api.changePassword("user1", body, false);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user1/credentials/change_password"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user1");
        verify(apiClient).parameterToPair("strict", false);
    }

    /* changeRecoveryQuestion */
    @Test
    public void testChangeRecoveryQuestion_Success() throws Exception {
        UserCredentials expected = new UserCredentials();
        stubInvoke(expected);
        UserCredentials body = new UserCredentials();
        UserCredentials actual = api.changeRecoveryQuestion("user2", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user2/credentials/change_recovery_question"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user2");
    }

    /* forgotPassword */
    @Test
    public void testForgotPassword_Success() throws Exception {
        ForgotPasswordResponse expected = new ForgotPasswordResponse();
        stubInvoke(expected);
        ForgotPasswordRequest body = new ForgotPasswordRequest();
        ForgotPasswordResponse actual = api.forgotPassword("user3", body, true);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user3/lifecycle/forgot_password"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user3");
        verify(apiClient).parameterToPair("sendEmail", true);
    }

    /* forgotPasswordSetNewPassword */
    @Test
    public void testForgotPasswordSetNewPassword_Success() throws Exception {
        UserCredentials expected = new UserCredentials();
        stubInvoke(expected);
        UserCredentials body = new UserCredentials();
        UserCredentials actual = api.forgotPasswordSetNewPassword("user4", body, false);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user4/credentials/forgot_password"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user4");
        verify(apiClient).parameterToPair("sendEmail", false);
    }

    /* resetPassword */
    @Test
    public void testResetPassword_Success() throws Exception {
        ResetPasswordToken expected = new ResetPasswordToken();
        stubInvoke(expected);
        ResetPasswordToken actual = api.resetPassword("user5", true, true);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user5/lifecycle/reset_password"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("user5");
        verify(apiClient).parameterToPair("sendEmail", true);
        verify(apiClient).parameterToPair("tempPassword", true);
    }

    @Test
    public void testResetPassword_WithHeaders() throws Exception {
        stubInvoke(new ResetPasswordToken());
        api.resetPassword("user5.1", true, true, Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(), anyString(), isNull(), cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    /* updatePassword */
    @Test
    public void testUpdatePassword_Success() throws Exception {
        UserCredentials expected = new UserCredentials();
        stubInvoke(expected);
        PasswordCredential body = new PasswordCredential();
        UserCredentials actual = api.updatePassword("user6", body, true);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user6/credentials/password"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user6");
        verify(apiClient).parameterToPair("strict", true);
    }

    @Test
    public void testUpdatePassword_MissingBody() {
        try {
            api.updatePassword("user7", null, false);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("body"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(anyString(), anyString(), anyList(), anyList(), anyString(), any(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class)))
            .thenThrow(new ApiException(403, "Forbidden"));
        try {
            api.changePassword("forbidden-user", new ChangePasswordRequest(), false);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(403, ex.getCode());
            assertEquals("Forbidden", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = UserCredApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
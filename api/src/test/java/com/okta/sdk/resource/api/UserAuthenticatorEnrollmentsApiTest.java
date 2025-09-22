package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AuthenticatorEnrollment;
import com.okta.sdk.resource.model.AuthenticatorEnrollmentCreateRequest;
import com.okta.sdk.resource.model.AuthenticatorEnrollmentCreateRequestTac;
import com.okta.sdk.resource.model.TacAuthenticatorEnrollment;
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
public class UserAuthenticatorEnrollmentsApiTest {

    private ApiClient apiClient;
    private UserAuthenticatorEnrollmentsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserAuthenticatorEnrollmentsApi(apiClient);

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

    /* createAuthenticatorEnrollment */
    @Test
    public void testCreateAuthenticatorEnrollment_Success() throws Exception {
        AuthenticatorEnrollment expected = new AuthenticatorEnrollment();
        stubInvoke(expected);
        AuthenticatorEnrollmentCreateRequest body = new AuthenticatorEnrollmentCreateRequest();
        AuthenticatorEnrollment actual = api.createAuthenticatorEnrollment("user1", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user1/authenticator-enrollments/phone"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user1");
    }

    @Test
    public void testCreateAuthenticatorEnrollment_MissingUserId() {
        try {
            api.createAuthenticatorEnrollment(null, new AuthenticatorEnrollmentCreateRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("userId"));
        }
    }

    @Test
    public void testCreateAuthenticatorEnrollment_MissingBody() {
        try {
            api.createAuthenticatorEnrollment("user1", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("authenticator"));
        }
    }

    /* createTacAuthenticatorEnrollment */
    @Test
    public void testCreateTacAuthenticatorEnrollment_Success() throws Exception {
        TacAuthenticatorEnrollment expected = new TacAuthenticatorEnrollment();
        stubInvoke(expected);
        AuthenticatorEnrollmentCreateRequestTac body = new AuthenticatorEnrollmentCreateRequestTac();
        TacAuthenticatorEnrollment actual = api.createTacAuthenticatorEnrollment("user2", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user2/authenticator-enrollments/tac"), eq("POST"),
            anyList(), anyList(), anyString(), bodyCap.capture(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("user2");
    }

    /* deleteAuthenticatorEnrollment */
    @Test
    public void testDeleteAuthenticatorEnrollment_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAuthenticatorEnrollment("user3", "enrollment1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user3/authenticator-enrollments/enrollment1"), eq("DELETE"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), isNull());
        verify(apiClient).escapeString("user3");
        verify(apiClient).escapeString("enrollment1");
    }

    @Test
    public void testDeleteAuthenticatorEnrollment_MissingEnrollmentId() {
        try {
            api.deleteAuthenticatorEnrollment("user3", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("enrollmentId"));
        }
    }

    /* getAuthenticatorEnrollment */
    @Test
    public void testGetAuthenticatorEnrollment_Success() throws Exception {
        AuthenticatorEnrollment expected = new AuthenticatorEnrollment();
        stubInvoke(expected);
        AuthenticatorEnrollment actual = api.getAuthenticatorEnrollment("user4", "enrollment2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user4/authenticator-enrollments/enrollment2"), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("user4");
        verify(apiClient).escapeString("enrollment2");
    }

    @Test
    public void testGetAuthenticatorEnrollment_WithHeaders() throws Exception {
        stubInvoke(new AuthenticatorEnrollment());
        api.getAuthenticatorEnrollment("user4", "enrollment2", Collections.singletonMap("X-Test", "true"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("true", cap.getValue().get("X-Test"));
    }

    /* listAuthenticatorEnrollments */
    @Test
    public void testListAuthenticatorEnrollments_Success() throws Exception {
        AuthenticatorEnrollment expected = new AuthenticatorEnrollment();
        stubInvoke(expected);
        AuthenticatorEnrollment actual = api.listAuthenticatorEnrollments("user5");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user5/authenticator-enrollments"), eq("GET"),
            anyList(), anyList(), anyString(), isNull(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("user5");
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(), anyList(), anyList(), anyString(), any(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(404, "Not Found"));
        try {
            api.getAuthenticatorEnrollment("non-existent-user", "non-existent-enrollment");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(404, ex.getCode());
            assertEquals("Not Found", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = UserAuthenticatorEnrollmentsApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
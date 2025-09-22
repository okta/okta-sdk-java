package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.EnrollmentActivationRequest;
import com.okta.sdk.resource.model.EnrollmentActivationResponse;
import com.okta.sdk.resource.model.EnrollmentInitializationRequest;
import com.okta.sdk.resource.model.EnrollmentInitializationResponse;
import com.okta.sdk.resource.model.FulfillmentRequest;
import com.okta.sdk.resource.model.PinRequest;
import com.okta.sdk.resource.model.WebAuthnPreregistrationFactor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebAuthnPreregistrationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.WebAuthnPreregistrationApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.WebAuthnPreregistrationApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // activatePreregistrationEnrollment
    @Test
    public void testActivatePreregistrationEnrollment_Success() throws Exception {
        EnrollmentActivationRequest body = new EnrollmentActivationRequest();
        EnrollmentActivationResponse expected = new EnrollmentActivationResponse();
        stubInvoke(expected);

        EnrollmentActivationResponse actual = api.activatePreregistrationEnrollment(body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", method.getValue());
        assertEquals("/webauthn-registration/api/v1/activate", path.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testActivatePreregistrationEnrollment_WithHeaders() throws Exception {
        stubInvoke(new EnrollmentActivationResponse());
        api.activatePreregistrationEnrollment(new EnrollmentActivationRequest(),
            Collections.singletonMap("X-A","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", headers.getValue().get("X-A"));
    }

    // enrollPreregistrationEnrollment
    @Test
    public void testEnrollPreregistrationEnrollment_Success() throws Exception {
        EnrollmentInitializationRequest body = new EnrollmentInitializationRequest();
        EnrollmentInitializationResponse expected = new EnrollmentInitializationResponse();
        stubInvoke(expected);

        EnrollmentInitializationResponse actual = api.enrollPreregistrationEnrollment(body);
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("POST", method.getValue());
        assertEquals("/webauthn-registration/api/v1/enroll", path.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testEnrollPreregistrationEnrollment_WithHeaders() throws Exception {
        stubInvoke(new EnrollmentInitializationResponse());
        api.enrollPreregistrationEnrollment(new EnrollmentInitializationRequest(),
            Collections.singletonMap("X-E","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", headers.getValue().get("X-E"));
    }

    // generateFulfillmentRequest
    @Test
    public void testGenerateFulfillmentRequest_Success() throws Exception {
        stubVoidInvoke();
        FulfillmentRequest body = new FulfillmentRequest();
        api.generateFulfillmentRequest(body);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("POST", method.getValue());
        assertEquals("/webauthn-registration/api/v1/initiate-fulfillment-request", path.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testGenerateFulfillmentRequest_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.generateFulfillmentRequest(new FulfillmentRequest(), Collections.singletonMap("X-F","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", headers.getValue().get("X-F"));
    }

    // sendPin
    @Test
    public void testSendPin_Success() throws Exception {
        stubVoidInvoke();
        PinRequest body = new PinRequest();
        api.sendPin(body);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("POST", method.getValue());
        assertEquals("/webauthn-registration/api/v1/send-pin", path.getValue());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testSendPin_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.sendPin(new PinRequest(), Collections.singletonMap("X-P","p"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("p", headers.getValue().get("X-P"));
    }

    // listWebAuthnPreregistrationFactors
    @Test
    public void testListWebAuthnPreregistrationFactors_Success() throws Exception {
        List<WebAuthnPreregistrationFactor> expected =
            Arrays.asList(new WebAuthnPreregistrationFactor(), new WebAuthnPreregistrationFactor());
        stubInvoke(expected);

        List<WebAuthnPreregistrationFactor> actual = api.listWebAuthnPreregistrationFactors("u1");
        assertSame(expected, actual);

        ArgumentCaptor<String> path = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> method = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(
            path.capture(), method.capture(),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("GET", method.getValue());
        assertEquals("/webauthn-registration/api/v1/users/u1/enrollments", path.getValue());
    }

    @Test
    public void testListWebAuthnPreregistrationFactors_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listWebAuthnPreregistrationFactors("u2", Collections.singletonMap("X-L","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", headers.getValue().get("X-L"));
    }

    @Test(expected = ApiException.class)
    public void testListWebAuthnPreregistrationFactors_MissingUserId() throws Exception {
        api.listWebAuthnPreregistrationFactors(null);
    }

    // assignFulfillmentErrorWebAuthnPreregistrationFactor
    @Test
    public void testAssignFulfillmentError_Success() throws Exception {
        stubVoidInvoke();
        api.assignFulfillmentErrorWebAuthnPreregistrationFactor("u3","e3");

        verify(apiClient).invokeAPI(
            eq("/webauthn-registration/api/v1/users/u3/enrollments/e3/mark-error"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testAssignFulfillmentError_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.assignFulfillmentErrorWebAuthnPreregistrationFactor("u4","e4",
            Collections.singletonMap("X-H","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", headers.getValue().get("X-H"));
    }

    @Test(expected = ApiException.class)
    public void testAssignFulfillmentError_MissingUserId() throws Exception {
        api.assignFulfillmentErrorWebAuthnPreregistrationFactor(null,"e");
    }

    @Test(expected = ApiException.class)
    public void testAssignFulfillmentError_MissingEnrollmentId() throws Exception {
        api.assignFulfillmentErrorWebAuthnPreregistrationFactor("u",null);
    }

    // deleteWebAuthnPreregistrationFactor
    @Test
    public void testDeleteWebAuthnPreregistrationFactor_Success() throws Exception {
        stubVoidInvoke();
        api.deleteWebAuthnPreregistrationFactor("u5","e5");

        verify(apiClient).invokeAPI(
            eq("/webauthn-registration/api/v1/users/u5/enrollments/e5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeleteWebAuthnPreregistrationFactor_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteWebAuthnPreregistrationFactor("u6","e6",
            Collections.singletonMap("X-D","d"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("d", headers.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteWebAuthnPreregistrationFactor_MissingUserId() throws Exception {
        api.deleteWebAuthnPreregistrationFactor(null,"e");
    }

    @Test(expected = ApiException.class)
    public void testDeleteWebAuthnPreregistrationFactor_MissingEnrollmentId() throws Exception {
        api.deleteWebAuthnPreregistrationFactor("u",null);
    }

    // Helpers
    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }
}

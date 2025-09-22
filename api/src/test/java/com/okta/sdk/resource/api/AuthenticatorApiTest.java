package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.*;
import com.okta.sdk.resource.model.AuthenticatorBase;
import com.okta.sdk.resource.model.AuthenticatorMethodBase;
import com.okta.sdk.resource.model.AuthenticatorMethodType;
import com.okta.sdk.resource.model.WellKnownAppAuthenticatorConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthenticatorApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.AuthenticatorApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.AuthenticatorApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object value = inv.getArgument(1);
            if (value == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(value)));
        });
    }

    // getWellKnownAppAuthenticatorConfiguration
    @Test
    public void testGetWellKnownAppAuthenticatorConfiguration_Success() throws Exception {
        List<WellKnownAppAuthenticatorConfiguration> expected = Arrays.asList(
            new WellKnownAppAuthenticatorConfiguration(), new WellKnownAppAuthenticatorConfiguration());
        stubInvoke(expected);

        List<WellKnownAppAuthenticatorConfiguration> actual =
            api.getWellKnownAppAuthenticatorConfiguration("client123");
        assertSame(expected, actual);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            pathCap.capture(), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        assertEquals("/.well-known/app-authenticator-configuration", pathCap.getValue());
        Map<String,String> qp = flattenPairs(qpCap.getValue());
        assertEquals("client123", qp.get("oauthClientId"));
    }

    @Test
    public void testGetWellKnownAppAuthenticatorConfiguration_WithHeaders() throws Exception {
        List<WellKnownAppAuthenticatorConfiguration> expected = Collections.singletonList(
            new WellKnownAppAuthenticatorConfiguration());
        stubInvoke(expected);

        Map<String,String> hdrs = new HashMap<>();
        hdrs.put("X-Test", "abc");
        api.getWellKnownAppAuthenticatorConfiguration("cid", hdrs);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("abc", headerCap.getValue().get("X-Test"));
    }

    @Test(expected = ApiException.class)
    public void testGetWellKnownAppAuthenticatorConfiguration_MissingParam() throws Exception {
        api.getWellKnownAppAuthenticatorConfiguration(null);
    }

    // listAllCustomAAGUIDs
    @Test
    public void testListAllCustomAAGUIDs_Success() throws Exception {
        List<CustomAAGUIDResponseObject> expected = Arrays.asList(
            new CustomAAGUIDResponseObject(), new CustomAAGUIDResponseObject());
        stubInvoke(expected);

        List<CustomAAGUIDResponseObject> actual = api.listAllCustomAAGUIDs("auth1");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            contains("/api/v1/authenticators/auth1/aaguids"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("auth1");
    }

    @Test(expected = ApiException.class)
    public void testListAllCustomAAGUIDs_MissingParam() throws Exception {
        api.listAllCustomAAGUIDs(null);
    }

    // listAuthenticatorMethods
    @Test
    public void testListAuthenticatorMethods_Success() throws Exception {
        List<AuthenticatorMethodBase> expected = Collections.singletonList(new AuthenticatorMethodBase());
        stubInvoke(expected);

        List<AuthenticatorMethodBase> actual = api.listAuthenticatorMethods("auth2");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            contains("/api/v1/authenticators/auth2/methods"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("auth2");
    }

    @Test(expected = ApiException.class)
    public void testListAuthenticatorMethods_MissingParam() throws Exception {
        api.listAuthenticatorMethods(null);
    }

    // listAuthenticators
    @Test
    public void testListAuthenticators_Success() throws Exception {
        List<AuthenticatorBase> expected = Arrays.asList(new AuthenticatorBase(), new AuthenticatorBase());
        stubInvoke(expected);

        List<AuthenticatorBase> actual = api.listAuthenticators();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/authenticators"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListAuthenticators_WithHeaders() throws Exception {
        List<AuthenticatorBase> expected = Collections.singletonList(new AuthenticatorBase());
        stubInvoke(expected);

        api.listAuthenticators(Collections.singletonMap("X-Req", "1"));
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-Req"));
    }

    // replaceAuthenticator
    @Test
    public void testReplaceAuthenticator_Success() throws Exception {
        AuthenticatorBase body = new AuthenticatorBase();
        AuthenticatorBase expected = new AuthenticatorBase();
        stubInvoke(expected);

        AuthenticatorBase actual = api.replaceAuthenticator("auth3", body);
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
            any(String[].class), any(TypeReference.class));
        assertEquals("PUT", methodCap.getValue());
        assertTrue(pathCap.getValue().contains("/api/v1/authenticators/auth3"));
        assertSame(body, bodyCap.getValue());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthenticator_MissingId() throws Exception {
        api.replaceAuthenticator(null, new AuthenticatorBase());
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthenticator_MissingBody() throws Exception {
        api.replaceAuthenticator("authX", null);
    }

    // replaceAuthenticatorMethod
    @Test
    public void testReplaceAuthenticatorMethod_Success() throws Exception {
        AuthenticatorMethodBase body = new AuthenticatorMethodBase();
        AuthenticatorMethodBase expected = new AuthenticatorMethodBase();
        stubInvoke(expected);

        AuthenticatorMethodBase actual = api.replaceAuthenticatorMethod(
            "auth4", AuthenticatorMethodType.PUSH, body);
        assertSame(expected, actual);

        // Path in implementation uses lower-case method segment ("push")
        verify(apiClient).invokeAPI(
            contains("/api/v1/authenticators/auth4/methods/push"), eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        verify(apiClient).escapeString("auth4");
        verify(apiClient).escapeString("push");
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthenticatorMethod_MissingId() throws Exception {
        api.replaceAuthenticatorMethod(null, AuthenticatorMethodType.SMS, null);
    }

    @Test(expected = ApiException.class)
    public void testReplaceAuthenticatorMethod_MissingMethodType() throws Exception {
        api.replaceAuthenticatorMethod("auth5", null, null);
    }

    // replaceCustomAAGUID
    @Test
    public void testReplaceCustomAAGUID_Success() throws Exception {
        CustomAAGUIDUpdateRequestObject body = new CustomAAGUIDUpdateRequestObject();
        CustomAAGUIDResponseObject expected = new CustomAAGUIDResponseObject();
        stubInvoke(expected);

        CustomAAGUIDResponseObject actual =
            api.replaceCustomAAGUID("auth6", "aag-1", body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            contains("/api/v1/authenticators/auth6/aaguids/aag-1"), eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomAAGUID_MissingAuthenticatorId() throws Exception {
        api.replaceCustomAAGUID(null, "a", null);
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomAAGUID_MissingAaguid() throws Exception {
        api.replaceCustomAAGUID("auth7", null, null);
    }

    // updateCustomAAGUID
    @Test
    public void testUpdateCustomAAGUID_Success() throws Exception {
        CustomAAGUIDUpdateRequestObject body = new CustomAAGUIDUpdateRequestObject();
        CustomAAGUIDResponseObject expected = new CustomAAGUIDResponseObject();
        stubInvoke(expected);

        CustomAAGUIDResponseObject actual =
            api.updateCustomAAGUID("auth8", "aag-2", body);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            contains("/api/v1/authenticators/auth8/aaguids/aag-2"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test(expected = ApiException.class)
    public void testUpdateCustomAAGUID_MissingAuthenticatorId() throws Exception {
        api.updateCustomAAGUID(null, "a", null);
    }

    @Test(expected = ApiException.class)
    public void testUpdateCustomAAGUID_MissingAaguid() throws Exception {
        api.updateCustomAAGUID("auth9", null, null);
    }

    // Helpers
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

    @SuppressWarnings("unchecked")
    private Map<String,String> flattenPairs(List<?> pairs) {
        if (pairs == null) return Collections.emptyMap();
        return (Map<String,String>) pairs.stream()
            .filter(Objects::nonNull)
            .map(p -> (Pair) p)
            .collect(Collectors.toMap(Pair::getName, Pair::getValue, (a,b)->b));
    }
}

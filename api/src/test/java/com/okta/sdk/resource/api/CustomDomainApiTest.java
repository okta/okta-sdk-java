package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.*;
import com.okta.sdk.resource.model.DomainCertificate;
import com.okta.sdk.resource.model.DomainListResponse;
import com.okta.sdk.resource.model.DomainRequest;
import com.okta.sdk.resource.model.DomainResponse;
import com.okta.sdk.resource.model.UpdateDomain;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CustomDomainApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.CustomDomainApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.CustomDomainApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // createCustomDomain
    @Test
    public void testCreateCustomDomain_Success() throws Exception {
        DomainRequest body = new DomainRequest();
        DomainResponse expected = new DomainResponse();
        stubInvoke(expected);

        DomainResponse actual = api.createCustomDomain(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/domains"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateCustomDomain_WithHeaders() throws Exception {
        stubInvoke(new DomainResponse());
        api.createCustomDomain(new DomainRequest(), Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateCustomDomain_MissingBody() throws Exception {
        api.createCustomDomain(null);
    }

    // deleteCustomDomain
    @Test
    public void testDeleteCustomDomain_Success() throws Exception {
        stubVoidInvoke();
        api.deleteCustomDomain("d1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/domains/d1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("d1");
    }

    @Test
    public void testDeleteCustomDomain_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteCustomDomain("d2", Collections.singletonMap("X-D","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteCustomDomain_MissingId() throws Exception {
        api.deleteCustomDomain(null);
    }

    // getCustomDomain
    @Test
    public void testGetCustomDomain_Success() throws Exception {
        DomainResponse expected = new DomainResponse();
        stubInvoke(expected);

        DomainResponse actual = api.getCustomDomain("d3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/domains/d3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("d3");
    }

    @Test
    public void testGetCustomDomain_WithHeaders() throws Exception {
        stubInvoke(new DomainResponse());
        api.getCustomDomain("d4", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdrCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetCustomDomain_MissingId() throws Exception {
        api.getCustomDomain(null);
    }

    // listCustomDomains
    @Test
    public void testListCustomDomains_Success() throws Exception {
        DomainListResponse expected = new DomainListResponse();
        stubInvoke(expected);

        DomainListResponse actual = api.listCustomDomains();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/domains"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListCustomDomains_WithHeaders() throws Exception {
        stubInvoke(new DomainListResponse());
        api.listCustomDomains(Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-L"));
    }

    // replaceCustomDomain
    @Test
    public void testReplaceCustomDomain_Success() throws Exception {
        UpdateDomain body = new UpdateDomain();
        DomainResponse expected = new DomainResponse();
        stubInvoke(expected);

        DomainResponse actual = api.replaceCustomDomain("d5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/domains/d5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("d5");
    }

    @Test
    public void testReplaceCustomDomain_WithHeaders() throws Exception {
        stubInvoke(new DomainResponse());
        api.replaceCustomDomain("d6", new UpdateDomain(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomDomain_MissingId() throws Exception {
        api.replaceCustomDomain(null, new UpdateDomain());
    }

    @Test(expected = ApiException.class)
    public void testReplaceCustomDomain_MissingBody() throws Exception {
        api.replaceCustomDomain("d7", null);
    }

    // upsertCertificate
    @Test
    public void testUpsertCertificate_Success() throws Exception {
        DomainCertificate cert = new DomainCertificate();
        stubVoidInvoke();

        api.upsertCertificate("d8", cert);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/domains/d8/certificate"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(cert, bodyCap.getValue());
        verify(apiClient).escapeString("d8");
    }

    @Test
    public void testUpsertCertificate_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.upsertCertificate("d9", new DomainCertificate(), Collections.singletonMap("X-U","u"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("u", hdrCap.getValue().get("X-U"));
    }

    @Test(expected = ApiException.class)
    public void testUpsertCertificate_MissingId() throws Exception {
        api.upsertCertificate(null, new DomainCertificate());
    }

    @Test(expected = ApiException.class)
    public void testUpsertCertificate_MissingCert() throws Exception {
        api.upsertCertificate("d10", null);
    }

    // verifyDomain
    @Test
    public void testVerifyDomain_Success() throws Exception {
        DomainResponse expected = new DomainResponse();
        stubInvoke(expected);

        DomainResponse actual = api.verifyDomain("d11");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/domains/d11/verify"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("d11");
    }

    @Test
    public void testVerifyDomain_WithHeaders() throws Exception {
        stubInvoke(new DomainResponse());
        api.verifyDomain("d12", Collections.singletonMap("X-V","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-V"));
    }

    @Test(expected = ApiException.class)
    public void testVerifyDomain_MissingId() throws Exception {
        api.verifyDomain(null);
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

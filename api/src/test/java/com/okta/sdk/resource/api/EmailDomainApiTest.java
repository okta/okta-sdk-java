package com.okta.sdk.resource.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.EmailDomain;
import com.okta.sdk.resource.model.EmailDomainResponse;
import com.okta.sdk.resource.model.EmailDomainResponseWithEmbedded;
import com.okta.sdk.resource.model.UpdateEmailDomain;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class EmailDomainApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.EmailDomainApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.EmailDomainApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPairs(anyString(), anyString(), any())).thenReturn(Collections.emptyList());
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

    /* createEmailDomain */
    @Test
    public void testCreateEmailDomain_Success() throws Exception {
        EmailDomain body = new EmailDomain();
        EmailDomainResponse expected = new EmailDomainResponse();
        stubInvoke(expected);

        EmailDomainResponse actual = api.createEmailDomain(body, Arrays.asList("a","b"));
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateEmailDomain_WithHeaders() throws Exception {
        stubInvoke(new EmailDomainResponse());
        Map<String,String> hdrs = Collections.singletonMap("X-H","1");
        api.createEmailDomain(new EmailDomain(), null, hdrs);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-H"));
    }

    @Test
    public void testCreateEmailDomain_MissingBody() {
        try {
            api.createEmailDomain(null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailDomain"));
        }
    }

    /* deleteEmailDomain */
    @Test
    public void testDeleteEmailDomain_Success() throws Exception {
        stubVoidInvoke();
        api.deleteEmailDomain("dom1", null);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains/dom1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("dom1");
    }

    @Test
    public void testDeleteEmailDomain_MissingId() {
        try {
            api.deleteEmailDomain(null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailDomainId"));
        }
    }

    /* getEmailDomain */
    @Test
    public void testGetEmailDomain_Success() throws Exception {
        EmailDomainResponseWithEmbedded expected = new EmailDomainResponseWithEmbedded();
        stubInvoke(expected);

        EmailDomainResponseWithEmbedded actual = api.getEmailDomain("dom2", null);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains/dom2"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("dom2");
    }

    @Test
    public void testGetEmailDomain_MissingId() {
        try {
            api.getEmailDomain(null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailDomainId"));
        }
    }

    /* listEmailDomains */
    @Test
    public void testListEmailDomains_Success() throws Exception {
        List<EmailDomainResponseWithEmbedded> expected = Arrays.asList(new EmailDomainResponseWithEmbedded());
        stubInvoke(expected);

        List<EmailDomainResponseWithEmbedded> actual = api.listEmailDomains(Arrays.asList("x"));
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* replaceEmailDomain */
    @Test
    public void testReplaceEmailDomain_Success() throws Exception {
        UpdateEmailDomain body = new UpdateEmailDomain();
        EmailDomainResponse expected = new EmailDomainResponse();
        stubInvoke(expected);

        EmailDomainResponse actual = api.replaceEmailDomain("dom3", body, null);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains/dom3"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("dom3");
    }

    @Test
    public void testReplaceEmailDomain_MissingId() {
        try {
            api.replaceEmailDomain(null, new UpdateEmailDomain(), null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailDomainId"));
        }
    }

    @Test
    public void testReplaceEmailDomain_MissingBody() {
        try {
            api.replaceEmailDomain("dom4", null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("updateEmailDomain"));
        }
    }

    /* verifyEmailDomain */
    @Test
    public void testVerifyEmailDomain_Success() throws Exception {
        EmailDomainResponse expected = new EmailDomainResponse();
        stubInvoke(expected);

        EmailDomainResponse actual = api.verifyEmailDomain("dom5");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-domains/dom5/verify"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("dom5");
    }

    @Test
    public void testVerifyEmailDomain_MissingId() {
        try {
            api.verifyEmailDomain(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailDomainId"));
        }
    }

    /* Header propagation example for verify */
    @Test
    public void testVerifyEmailDomain_WithHeaders() throws Exception {
        stubInvoke(new EmailDomainResponse());
        Map<String,String> hdrs = Collections.singletonMap("X-V","yes");
        api.verifyEmailDomain("dom6", hdrs);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("yes", hdrCap.getValue().get("X-V"));
    }

    /* ApiException propagation */
    @Test
    public void testCreateEmailDomain_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));

        try {
            api.createEmailDomain(new EmailDomain(), null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("err"));
        }
    }

    /* ObjectMapper configuration (reflection) */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.EmailDomainApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

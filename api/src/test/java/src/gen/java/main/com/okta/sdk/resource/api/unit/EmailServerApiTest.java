package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.EmailServerApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.EmailServerListResponse;
import com.okta.sdk.resource.model.EmailServerPost;
import com.okta.sdk.resource.model.EmailServerRequest;
import com.okta.sdk.resource.model.EmailServerResponse;
import com.okta.sdk.resource.model.EmailTestAddresses;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class EmailServerApiTest {

    private ApiClient apiClient;
    private EmailServerApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new EmailServerApi(apiClient);

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

    /* createEmailServer */
    @Test
    public void testCreateEmailServer_Success_WithBody() throws Exception {
        EmailServerPost body = new EmailServerPost();
        EmailServerResponse expected = new EmailServerResponse();
        stubInvoke(expected);

        EmailServerResponse actual = api.createEmailServer(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateEmailServer_Success_NullBody() throws Exception {
        EmailServerResponse expected = new EmailServerResponse();
        stubInvoke(expected);

        EmailServerResponse actual = api.createEmailServer(null);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testCreateEmailServer_WithHeaders() throws Exception {
        stubInvoke(new EmailServerResponse());
        api.createEmailServer(new EmailServerPost(), Collections.singletonMap("X-C","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-C"));
    }

    @Test
    public void testCreateEmailServer_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.createEmailServer(new EmailServerPost());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("err"));
        }
    }

    /* deleteEmailServer */
    @Test
    public void testDeleteEmailServer_Success() throws Exception {
        stubVoidInvoke();
        api.deleteEmailServer("srv1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("srv1");
    }

    @Test
    public void testDeleteEmailServer_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteEmailServer("srv2", Collections.singletonMap("X-D","v"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdr.getValue().get("X-D"));
    }

    @Test
    public void testDeleteEmailServer_MissingId() {
        try {
            api.deleteEmailServer(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailServerId"));
        }
    }

    /* getEmailServer */
    @Test
    public void testGetEmailServer_Success() throws Exception {
        EmailServerListResponse expected = new EmailServerListResponse();
        stubInvoke(expected);

        EmailServerListResponse actual = api.getEmailServer("srv3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("srv3");
    }

    @Test
    public void testGetEmailServer_WithHeaders() throws Exception {
        stubInvoke(new EmailServerListResponse());
        api.getEmailServer("srv4", Collections.singletonMap("X-G","g"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdr.getValue().get("X-G"));
    }

    @Test
    public void testGetEmailServer_MissingId() {
        try {
            api.getEmailServer(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailServerId"));
        }
    }

    /* listEmailServers */
    @Test
    public void testListEmailServers_Success() throws Exception {
        EmailServerListResponse expected = new EmailServerListResponse();
        stubInvoke(expected);

        EmailServerListResponse actual = api.listEmailServers();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListEmailServers_WithHeaders() throws Exception {
        stubInvoke(new EmailServerListResponse());
        api.listEmailServers(Collections.singletonMap("X-L","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-L"));
    }

    /* testEmailServer */
    @Test
    public void testTestEmailServer_Success_WithBody() throws Exception {
        stubVoidInvoke();
        EmailTestAddresses body = new EmailTestAddresses();
        api.testEmailServer("srv5", body);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv5/test"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("srv5");
    }

    @Test
    public void testTestEmailServer_Success_NullBody() throws Exception {
        stubVoidInvoke();
        api.testEmailServer("srv6", null);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv6/test"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testTestEmailServer_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.testEmailServer("srv7", new EmailTestAddresses(), Collections.singletonMap("X-T","v"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdr.getValue().get("X-T"));
    }

    @Test
    public void testTestEmailServer_MissingId() {
        try {
            api.testEmailServer(null, null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailServerId"));
        }
    }

    /* updateEmailServer */
    @Test
    public void testUpdateEmailServer_Success_WithBody() throws Exception {
        EmailServerRequest body = new EmailServerRequest();
        EmailServerResponse expected = new EmailServerResponse();
        stubInvoke(expected);

        EmailServerResponse actual = api.updateEmailServer("srv8", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv8"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("srv8");
    }

    @Test
    public void testUpdateEmailServer_Success_NullBody() throws Exception {
        EmailServerResponse expected = new EmailServerResponse();
        stubInvoke(expected);

        EmailServerResponse actual = api.updateEmailServer("srv9", null);
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/email-servers/srv9"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testUpdateEmailServer_WithHeaders() throws Exception {
        stubInvoke(new EmailServerResponse());
        api.updateEmailServer("srv10", new EmailServerRequest(), Collections.singletonMap("X-U","h"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PATCH"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("h", hdr.getValue().get("X-U"));
    }

    @Test
    public void testUpdateEmailServer_MissingId() {
        try {
            api.updateEmailServer(null, new EmailServerRequest());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("emailServerId"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = EmailServerApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

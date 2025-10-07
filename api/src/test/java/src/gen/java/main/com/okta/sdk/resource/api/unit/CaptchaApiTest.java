package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.CaptchaApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CAPTCHAInstance;
import com.okta.sdk.resource.model.OrgCAPTCHASettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CaptchaApiTest {

    private ApiClient apiClient;
    private CaptchaApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new CaptchaApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    // createCaptchaInstance
    @Test
    public void testCreateCaptchaInstance_Success() throws Exception {
        CAPTCHAInstance body = new CAPTCHAInstance();
        CAPTCHAInstance expected = new CAPTCHAInstance();
        stubInvoke(expected);

        CAPTCHAInstance actual = api.createCaptchaInstance(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateCaptchaInstance_WithHeaders() throws Exception {
        stubInvoke(new CAPTCHAInstance());
        api.createCaptchaInstance(new CAPTCHAInstance(), Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
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

    @Test(expected = ApiException.class)
    public void testCreateCaptchaInstance_MissingBody() throws Exception {
        api.createCaptchaInstance(null);
    }

    // deleteCaptchaInstance
    @Test
    public void testDeleteCaptchaInstance_Success() throws Exception {
        stubVoidInvoke();
        api.deleteCaptchaInstance("id1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas/id1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("id1");
    }

    @Test
    public void testDeleteCaptchaInstance_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteCaptchaInstance("id2", Collections.singletonMap("X-D","v"));

        @SuppressWarnings("rawtypes")
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

    @Test(expected = ApiException.class)
    public void testDeleteCaptchaInstance_MissingId() throws Exception {
        api.deleteCaptchaInstance(null);
    }

    // deleteOrgCaptchaSettings
    @Test
    public void testDeleteOrgCaptchaSettings_Success() throws Exception {
        stubVoidInvoke();
        api.deleteOrgCaptchaSettings();

        verify(apiClient).invokeAPI(
            eq("/api/v1/org/captcha"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeleteOrgCaptchaSettings_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteOrgCaptchaSettings(Collections.singletonMap("X-H","h"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("h", hdr.getValue().get("X-H"));
    }

    // getCaptchaInstance
    @Test
    public void testGetCaptchaInstance_Success() throws Exception {
        CAPTCHAInstance expected = new CAPTCHAInstance();
        stubInvoke(expected);

        CAPTCHAInstance actual = api.getCaptchaInstance("id3");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas/id3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("id3");
    }

    @Test
    public void testGetCaptchaInstance_WithHeaders() throws Exception {
        stubInvoke(new CAPTCHAInstance());
        api.getCaptchaInstance("id4", Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
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

    @Test(expected = ApiException.class)
    public void testGetCaptchaInstance_MissingId() throws Exception {
        api.getCaptchaInstance(null);
    }

    // getOrgCaptchaSettings
    @Test
    public void testGetOrgCaptchaSettings_Success() throws Exception {
        OrgCAPTCHASettings expected = new OrgCAPTCHASettings();
        stubInvoke(expected);

        OrgCAPTCHASettings actual = api.getOrgCaptchaSettings();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/org/captcha"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetOrgCaptchaSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgCAPTCHASettings());
        api.getOrgCaptchaSettings(Collections.singletonMap("X-O","o"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("o", hdr.getValue().get("X-O"));
    }

    // listCaptchaInstances
    @Test
    public void testListCaptchaInstances_Success() throws Exception {
        List<CAPTCHAInstance> expected = Arrays.asList(new CAPTCHAInstance(), new CAPTCHAInstance());
        stubInvoke(expected);

        List<CAPTCHAInstance> actual = api.listCaptchaInstances();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListCaptchaInstances_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listCaptchaInstances(Collections.singletonMap("X-L","1"));

        @SuppressWarnings("rawtypes")
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

    // replaceCaptchaInstance
    @Test
    public void testReplaceCaptchaInstance_Success() throws Exception {
        CAPTCHAInstance body = new CAPTCHAInstance();
        CAPTCHAInstance expected = new CAPTCHAInstance();
        stubInvoke(expected);

        CAPTCHAInstance actual = api.replaceCaptchaInstance("id5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas/id5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("id5");
    }

    @Test
    public void testReplaceCaptchaInstance_WithHeaders() throws Exception {
        stubInvoke(new CAPTCHAInstance());
        api.replaceCaptchaInstance("id6", new CAPTCHAInstance(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdr.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceCaptchaInstance_MissingId() throws Exception {
        api.replaceCaptchaInstance(null, new CAPTCHAInstance());
    }

    @Test(expected = ApiException.class)
    public void testReplaceCaptchaInstance_MissingBody() throws Exception {
        api.replaceCaptchaInstance("id7", null);
    }

    // replacesOrgCaptchaSettings
    @Test
    public void testReplacesOrgCaptchaSettings_Success() throws Exception {
        OrgCAPTCHASettings body = new OrgCAPTCHASettings();
        OrgCAPTCHASettings expected = new OrgCAPTCHASettings();
        stubInvoke(expected);

        OrgCAPTCHASettings actual = api.replacesOrgCaptchaSettings(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/captcha"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplacesOrgCaptchaSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgCAPTCHASettings());
        api.replacesOrgCaptchaSettings(new OrgCAPTCHASettings(), Collections.singletonMap("X-RS","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdr.getValue().get("X-RS"));
    }

    @Test(expected = ApiException.class)
    public void testReplacesOrgCaptchaSettings_MissingBody() throws Exception {
        api.replacesOrgCaptchaSettings(null);
    }

    // updateCaptchaInstance
    @Test
    public void testUpdateCaptchaInstance_Success() throws Exception {
        CAPTCHAInstance body = new CAPTCHAInstance();
        CAPTCHAInstance expected = new CAPTCHAInstance();
        stubInvoke(expected);

        CAPTCHAInstance actual = api.updateCaptchaInstance("id8", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/captchas/id8"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("id8");
    }

    @Test
    public void testUpdateCaptchaInstance_WithHeaders() throws Exception {
        stubInvoke(new CAPTCHAInstance());
        api.updateCaptchaInstance("id9", new CAPTCHAInstance(), Collections.singletonMap("X-U","u"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("u", hdr.getValue().get("X-U"));
    }

    @Test(expected = ApiException.class)
    public void testUpdateCaptchaInstance_MissingId() throws Exception {
        api.updateCaptchaInstance(null, new CAPTCHAInstance());
    }

    @Test(expected = ApiException.class)
    public void testUpdateCaptchaInstance_MissingBody() throws Exception {
        api.updateCaptchaInstance("id10", null);
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

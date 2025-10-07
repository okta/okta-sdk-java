package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OrgPreferences;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OrgSettingCustomizationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingCustomizationApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingCustomizationApi(apiClient);

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

    /* getOrgPreferences */
    @Test
    public void testGetOrgPreferences_Success() throws Exception {
        OrgPreferences expected = new OrgPreferences();
        stubInvoke(expected);
        OrgPreferences actual = api.getOrgPreferences();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/preferences"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetOrgPreferences_WithHeaders() throws Exception {
        stubInvoke(new OrgPreferences());
        api.getOrgPreferences(Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-H"));
    }

    /* setOrgHideOktaUIFooter */
    @Test
    public void testSetOrgHideOktaUIFooter_Success() throws Exception {
        OrgPreferences expected = new OrgPreferences();
        stubInvoke(expected);
        OrgPreferences actual = api.setOrgHideOktaUIFooter();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/preferences/hideEndUserFooter"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testSetOrgHideOktaUIFooter_WithHeaders() throws Exception {
        stubInvoke(new OrgPreferences());
        api.setOrgHideOktaUIFooter(Collections.singletonMap("X-Hide","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headers.getValue().get("X-Hide"));
    }

    /* setOrgShowOktaUIFooter */
    @Test
    public void testSetOrgShowOktaUIFooter_Success() throws Exception {
        OrgPreferences expected = new OrgPreferences();
        stubInvoke(expected);
        OrgPreferences actual = api.setOrgShowOktaUIFooter();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/preferences/showEndUserFooter"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testSetOrgShowOktaUIFooter_WithHeaders() throws Exception {
        stubInvoke(new OrgPreferences());
        api.setOrgShowOktaUIFooter(Collections.singletonMap("X-Show","y"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("y", headers.getValue().get("X-Show"));
    }

    /* uploadOrgLogo */
    @Test
    public void testUploadOrgLogo_Success() throws Exception {
        stubInvoke(null);
        File tmp = File.createTempFile("logo", ".png");
        api.uploadOrgLogo(tmp);
        ArgumentCaptor<Map> formCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/logo"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), formCap.capture(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertSame(tmp, formCap.getValue().get("file"));
        tmp.delete();
    }

    @Test
    public void testUploadOrgLogo_WithHeaders() throws Exception {
        stubInvoke(null);
        File tmp = File.createTempFile("logo", ".png");
        api.uploadOrgLogo(tmp, Collections.singletonMap("X-Up","v"));
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
        assertEquals("v", headerCap.getValue().get("X-Up"));
        tmp.delete();
    }

    @Test
    public void testUploadOrgLogo_MissingFile() {
        try {
            api.uploadOrgLogo(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testSetOrgHideOktaUIFooter_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.setOrgHideOktaUIFooter();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingCustomizationApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

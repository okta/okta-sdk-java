package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OrgSetting;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class OrgSettingGeneralApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingGeneralApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingGeneralApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString()))
            .thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class)))
            .thenReturn("application/json");
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

    /* getOrgSettings */
    @Test
    public void testGetOrgSettings_Success() throws Exception {
        OrgSetting expected = new OrgSetting();
        stubInvoke(expected);
        OrgSetting actual = api.getOrgSettings();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetOrgSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgSetting());
        api.getOrgSettings(Collections.singletonMap("X-G","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-G"));
    }

    /* replaceOrgSettings */
    @Test
    public void testReplaceOrgSettings_Success() throws Exception {
        OrgSetting expected = new OrgSetting();
        stubInvoke(expected);
        OrgSetting body = new OrgSetting();
        OrgSetting actual = api.replaceOrgSettings(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceOrgSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgSetting());
        api.replaceOrgSettings(new OrgSetting(), Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("1", headers.getValue().get("X-R"));
    }

    @Test
    public void testReplaceOrgSettings_MissingBody() {
        try {
            api.replaceOrgSettings(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("orgSetting"));
        }
    }

    @Test
    public void testReplaceOrgSettings_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.replaceOrgSettings(new OrgSetting());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("err"));
        }
    }

    /* updateOrgSettings (partial update) */
    @Test
    public void testUpdateOrgSettings_Success() throws Exception {
        OrgSetting expected = new OrgSetting();
        stubInvoke(expected);
        OrgSetting body = new OrgSetting();
        OrgSetting actual = api.updateOrgSettings(body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org"), eq("POST"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateOrgSettings_NullBodyAllowed() throws Exception {
        OrgSetting expected = new OrgSetting();
        stubInvoke(expected);
        OrgSetting actual = api.updateOrgSettings(null);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateOrgSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgSetting());
        api.updateOrgSettings(new OrgSetting(), Collections.singletonMap("X-U","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertEquals("v", headers.getValue().get("X-U"));
    }

    /* Header negotiation check */
    @Test
    public void testSelectHeaderAcceptCalledWithPath() throws Exception {
        stubInvoke(new OrgSetting());
        api.getOrgSettings();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/org"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingGeneralApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

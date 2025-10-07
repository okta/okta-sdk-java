package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.OktaPersonalSettingsApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.OktaPersonalAdminFeatureSettings;
import com.okta.sdk.resource.model.PersonalAppsBlockList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OktaPersonalSettingsApiTest {

    private ApiClient apiClient;
    private OktaPersonalSettingsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new OktaPersonalSettingsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        // Safe parameterToPair (not really used here but consistent)
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object v = inv.getArgument(1);
            return Collections.singletonList(new Pair(name, v == null ? null : String.valueOf(v)));
        });
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

    /* listPersonalAppsExportBlockList */
    @Test
    public void testListPersonalAppsExportBlockList_Success() throws Exception {
        PersonalAppsBlockList expected = new PersonalAppsBlockList();
        stubInvoke(expected);
        PersonalAppsBlockList actual = api.listPersonalAppsExportBlockList();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/okta-personal-settings/api/v1/export-blocklists"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListPersonalAppsExportBlockList_WithHeaders() throws Exception {
        stubInvoke(new PersonalAppsBlockList());
        api.listPersonalAppsExportBlockList(Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-H"));
    }

    /* replaceBlockedEmailDomains */
    @Test
    public void testReplaceBlockedEmailDomains_Success() throws Exception {
        stubVoidInvoke();
        PersonalAppsBlockList body = new PersonalAppsBlockList();
        api.replaceBlockedEmailDomains(body);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/okta-personal-settings/api/v1/export-blocklists"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceBlockedEmailDomains_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.replaceBlockedEmailDomains(new PersonalAppsBlockList(), Collections.singletonMap("X-R","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceBlockedEmailDomains_MissingBody() {
        try {
            api.replaceBlockedEmailDomains(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* replaceOktaPersonalAdminSettings */
    @Test
    public void testReplaceOktaPersonalAdminSettings_Success() throws Exception {
        stubVoidInvoke();
        OktaPersonalAdminFeatureSettings body = new OktaPersonalAdminFeatureSettings();
        api.replaceOktaPersonalAdminSettings(body);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/okta-personal-settings/api/v1/edit-feature"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceOktaPersonalAdminSettings_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.replaceOktaPersonalAdminSettings(new OktaPersonalAdminFeatureSettings(),
            Collections.singletonMap("X-A","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-A"));
    }

    @Test
    public void testReplaceOktaPersonalAdminSettings_MissingBody() {
        try {
            api.replaceOktaPersonalAdminSettings(null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testListPersonalAppsExportBlockList_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.listPersonalAppsExportBlockList();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    @Test
    public void testReplaceOktaPersonalAdminSettings_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.replaceOktaPersonalAdminSettings(new OktaPersonalAdminFeatureSettings());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("bad"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = OktaPersonalSettingsApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

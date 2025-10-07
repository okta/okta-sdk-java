package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OrgOktaCommunicationSetting;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OrgSettingCommunicationApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingCommunicationApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingCommunicationApi(apiClient);

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

    /* getOktaCommunicationSettings */
    @Test
    public void testGetOktaCommunicationSettings_Success() throws Exception {
        OrgOktaCommunicationSetting expected = new OrgOktaCommunicationSetting();
        stubInvoke(expected);
        OrgOktaCommunicationSetting actual = api.getOktaCommunicationSettings();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaCommunication"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetOktaCommunicationSettings_WithHeaders() throws Exception {
        stubInvoke(new OrgOktaCommunicationSetting());
        api.getOktaCommunicationSettings(Collections.singletonMap("X-H","v"));
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

    /* optInUsersToOktaCommunicationEmails */
    @Test
    public void testOptInUsers_Success() throws Exception {
        OrgOktaCommunicationSetting expected = new OrgOktaCommunicationSetting();
        stubInvoke(expected);
        OrgOktaCommunicationSetting actual = api.optInUsersToOktaCommunicationEmails();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaCommunication/optIn"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testOptInUsers_WithHeaders() throws Exception {
        stubInvoke(new OrgOktaCommunicationSetting());
        api.optInUsersToOktaCommunicationEmails(Collections.singletonMap("X-In","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-In"));
    }

    /* optOutUsersFromOktaCommunicationEmails */
    @Test
    public void testOptOutUsers_Success() throws Exception {
        OrgOktaCommunicationSetting expected = new OrgOktaCommunicationSetting();
        stubInvoke(expected);
        OrgOktaCommunicationSetting actual = api.optOutUsersFromOktaCommunicationEmails();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaCommunication/optOut"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testOptOutUsers_WithHeaders() throws Exception {
        stubInvoke(new OrgOktaCommunicationSetting());
        api.optOutUsersFromOktaCommunicationEmails(Collections.singletonMap("X-Out","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", cap.getValue().get("X-Out"));
    }

    /* ApiException propagation */
    @Test
    public void testOptOutUsers_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.optOutUsersFromOktaCommunicationEmails();
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("unavailable"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingCommunicationApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

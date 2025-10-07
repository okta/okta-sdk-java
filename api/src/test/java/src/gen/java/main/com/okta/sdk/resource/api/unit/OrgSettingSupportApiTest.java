package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OktaSupportCase;
import com.okta.sdk.resource.model.OktaSupportCases;
import com.okta.sdk.resource.model.OrgAerialConsent;
import com.okta.sdk.resource.model.OrgAerialConsentDetails;
import com.okta.sdk.resource.model.OrgAerialConsentRevoked;
import com.okta.sdk.resource.model.OrgOktaSupportSettingsObj;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class OrgSettingSupportApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.OrgSettingSupportApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.OrgSettingSupportApi(apiClient);

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

    /* extendOktaSupport (deprecated, void) */
    @Test
    public void testExtendOktaSupport_Success() throws Exception {
        stubInvoke(null);
        api.extendOktaSupport();
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaSupport/extend"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testExtendOktaSupport_WithHeaders() throws Exception {
        stubInvoke(null);
        api.extendOktaSupport(Collections.singletonMap("X-Ext","1"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("1", headers.getValue().get("X-Ext"));
    }

    /* getAerialConsent */
    @Test
    public void testGetAerialConsent_Success() throws Exception {
        OrgAerialConsentDetails expected = new OrgAerialConsentDetails();
        stubInvoke(expected);
        OrgAerialConsentDetails actual = api.getAerialConsent();
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/aerial"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetAerialConsent_WithHeaders() throws Exception {
        stubInvoke(new OrgAerialConsentDetails());
        api.getAerialConsent(Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            headers.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any()
        );
        assertEquals("v", headers.getValue().get("X-H"));
    }

    /* getOrgOktaSupportSettings */
    @Test
    public void testGetOrgOktaSupportSettings_Success() throws Exception {
        OrgOktaSupportSettingsObj expected = new OrgOktaSupportSettingsObj();
        stubInvoke(expected);
        OrgOktaSupportSettingsObj actual = api.getOrgOktaSupportSettings();
        assertSame(expected, actual);
    }

    /* grantAerialConsent (optional body) */
    @Test
    public void testGrantAerialConsent_WithBody() throws Exception {
        OrgAerialConsentDetails expected = new OrgAerialConsentDetails();
        stubInvoke(expected);
        OrgAerialConsent body = new OrgAerialConsent();
        OrgAerialConsentDetails actual = api.grantAerialConsent(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/aerial/grant"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testGrantAerialConsent_NullBody() throws Exception {
        OrgAerialConsentDetails expected = new OrgAerialConsentDetails();
        stubInvoke(expected);
        OrgAerialConsentDetails actual = api.grantAerialConsent(null);
        assertSame(expected, actual);
    }

    /* grantOktaSupport (deprecated, void) */
    @Test
    public void testGrantOktaSupport_Success() throws Exception {
        stubInvoke(null);
        api.grantOktaSupport();
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaSupport/grant"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    /* listOktaSupportCases */
    @Test
    public void testListOktaSupportCases_Success() throws Exception {
        OktaSupportCases expected = new OktaSupportCases();
        stubInvoke(expected);
        OktaSupportCases actual = api.listOktaSupportCases();
        assertSame(expected, actual);
    }

    /* revokeAerialConsent */
    @Test
    public void testRevokeAerialConsent_WithBody() throws Exception {
        OrgAerialConsentRevoked expected = new OrgAerialConsentRevoked();
        stubInvoke(expected);
        OrgAerialConsent body = new OrgAerialConsent();
        OrgAerialConsentRevoked actual = api.revokeAerialConsent(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/aerial/revoke"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testRevokeAerialConsent_NullBody() throws Exception {
        OrgAerialConsentRevoked expected = new OrgAerialConsentRevoked();
        stubInvoke(expected);
        OrgAerialConsentRevoked actual = api.revokeAerialConsent(null);
        assertSame(expected, actual);
    }

    /* revokeOktaSupport (deprecated, void) */
    @Test
    public void testRevokeOktaSupport_Success() throws Exception {
        stubInvoke(null);
        api.revokeOktaSupport();
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaSupport/revoke"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        );
    }

    /* updateOktaSupportCase */
    @Test
    public void testUpdateOktaSupportCase_Success() throws Exception {
        OktaSupportCase expected = new OktaSupportCase();
        stubInvoke(expected);
        OktaSupportCase body = new OktaSupportCase();
        OktaSupportCase actual = api.updateOktaSupportCase("12345", body);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/org/privacy/oktaSupport/cases/12345"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), eq(body),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUpdateOktaSupportCase_MissingCaseNumber() {
        try {
            api.updateOktaSupportCase(null, new OktaSupportCase());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
            assertTrue(e.getMessage().contains("caseNumber"));
        }
    }

    @Test
    public void testUpdateOktaSupportCase_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PATCH"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502, "backend"));
        try {
            api.updateOktaSupportCase("C-1", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("backend"));
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptAndContentTypeCalled() throws Exception {
        stubInvoke(new OrgAerialConsentDetails());
        api.getAerialConsent();
        verify(apiClient).selectHeaderAccept(any(String[].class), eq("/api/v1/org/privacy/aerial"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.OrgSettingSupportApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

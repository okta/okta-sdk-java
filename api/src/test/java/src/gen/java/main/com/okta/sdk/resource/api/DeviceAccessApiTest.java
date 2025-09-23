package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.DesktopMFAEnforceNumberMatchingChallengeOrgSetting;
import com.okta.sdk.resource.model.DesktopMFARecoveryPinOrgSetting;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DeviceAccessApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.DeviceAccessApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.DeviceAccessApi(apiClient);

        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    /* Helpers */
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

    /* getDesktopMFAEnforceNumberMatchingChallengeOrgSetting */
    @Test
    public void testGetEnforceNumberMatchingChallengeSetting_Success() throws Exception {
        DesktopMFAEnforceNumberMatchingChallengeOrgSetting expected =
            new DesktopMFAEnforceNumberMatchingChallengeOrgSetting();
        stubInvoke(expected);

        DesktopMFAEnforceNumberMatchingChallengeOrgSetting actual =
            api.getDesktopMFAEnforceNumberMatchingChallengeOrgSetting();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/device-access/api/v1/desktop-mfa/enforce-number-matching-challenge-settings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetEnforceNumberMatchingChallengeSetting_WithHeaders() throws Exception {
        stubInvoke(new DesktopMFAEnforceNumberMatchingChallengeOrgSetting());
        Map<String,String> hdrs = Collections.singletonMap("X-H","v");
        api.getDesktopMFAEnforceNumberMatchingChallengeOrgSetting(hdrs);

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", hdrCap.getValue().get("X-H"));
    }

    /* getDesktopMFARecoveryPinOrgSetting */
    @Test
    public void testGetRecoveryPinSetting_Success() throws Exception {
        DesktopMFARecoveryPinOrgSetting expected = new DesktopMFARecoveryPinOrgSetting();
        stubInvoke(expected);

        DesktopMFARecoveryPinOrgSetting actual = api.getDesktopMFARecoveryPinOrgSetting();
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/device-access/api/v1/desktop-mfa/recovery-pin-settings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetRecoveryPinSetting_WithHeaders() throws Exception {
        stubInvoke(new DesktopMFARecoveryPinOrgSetting());
        api.getDesktopMFARecoveryPinOrgSetting(Collections.singletonMap("X-R","1"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-R"));
    }

    /* replaceDesktopMFAEnforceNumberMatchingChallengeOrgSetting */
    @Test
    public void testReplaceEnforceNumberMatchingChallengeSetting_Success() throws Exception {
        DesktopMFAEnforceNumberMatchingChallengeOrgSetting body =
            new DesktopMFAEnforceNumberMatchingChallengeOrgSetting();
        DesktopMFAEnforceNumberMatchingChallengeOrgSetting expected =
            new DesktopMFAEnforceNumberMatchingChallengeOrgSetting();
        stubInvoke(expected);

        DesktopMFAEnforceNumberMatchingChallengeOrgSetting actual =
            api.replaceDesktopMFAEnforceNumberMatchingChallengeOrgSetting(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/device-access/api/v1/desktop-mfa/enforce-number-matching-challenge-settings"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceEnforceNumberMatchingChallengeSetting_WithHeaders() throws Exception {
        stubInvoke(new DesktopMFAEnforceNumberMatchingChallengeOrgSetting());
        api.replaceDesktopMFAEnforceNumberMatchingChallengeOrgSetting(
            new DesktopMFAEnforceNumberMatchingChallengeOrgSetting(),
            Collections.singletonMap("X-C","x"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", hdrCap.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceEnforceNumberMatchingChallengeSetting_MissingBody() throws Exception {
        api.replaceDesktopMFAEnforceNumberMatchingChallengeOrgSetting(null);
    }

    /* replaceDesktopMFARecoveryPinOrgSetting */
    @Test
    public void testReplaceRecoveryPinSetting_Success() throws Exception {
        DesktopMFARecoveryPinOrgSetting body = new DesktopMFARecoveryPinOrgSetting();
        DesktopMFARecoveryPinOrgSetting expected = new DesktopMFARecoveryPinOrgSetting();
        stubInvoke(expected);

        DesktopMFARecoveryPinOrgSetting actual =
            api.replaceDesktopMFARecoveryPinOrgSetting(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/device-access/api/v1/desktop-mfa/recovery-pin-settings"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceRecoveryPinSetting_WithHeaders() throws Exception {
        stubInvoke(new DesktopMFARecoveryPinOrgSetting());
        api.replaceDesktopMFARecoveryPinOrgSetting(
            new DesktopMFARecoveryPinOrgSetting(),
            Collections.singletonMap("X-H","h"));

        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("h", hdrCap.getValue().get("X-H"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceRecoveryPinSetting_MissingBody() throws Exception {
        api.replaceDesktopMFARecoveryPinOrgSetting(null);
    }
}

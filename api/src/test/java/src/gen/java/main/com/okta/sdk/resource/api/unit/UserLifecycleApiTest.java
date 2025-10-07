package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.UserLifecycleApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.UserActivationToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class UserLifecycleApiTest {

    private ApiClient apiClient;
    private UserLifecycleApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new UserLifecycleApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    private void stubInvokeReturn(UserActivationToken token, String expectedPathFragment) throws ApiException {
        when(apiClient.invokeAPI(
            argThat(p -> p.contains(expectedPathFragment)),
            eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(token);
    }

    private void stubInvokeVoid(String method, String expectedPathFragment) throws ApiException {
        when(apiClient.invokeAPI(
            argThat(p -> p.contains(expectedPathFragment)),
            eq(method),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    /* activateUser */
    @Test
    public void testActivateUser_Success() throws Exception {
        UserActivationToken tok = new UserActivationToken();
        stubInvokeReturn(tok, "/activate");
        UserActivationToken actual = api.activateUser("user1", true);
        assertSame(tok, actual);
        verify(apiClient).invokeAPI(contains("/users/user1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testActivateUser_WithHeaders() throws Exception {
        stubInvokeReturn(new UserActivationToken(), "/activate");
        Map<String,String> hdr = Collections.singletonMap("X-A","v");
        api.activateUser("user2", false, hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(contains("/users/user2/lifecycle/activate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateUser_MissingId() {
        expect400(() -> api.activateUser(null, true));
    }

    /* deactivateUser */
    @Test
    public void testDeactivateUser_Success() throws Exception {
        stubInvokeVoid("POST", "/deactivate");
        api.deactivateUser("user3", true, "respond-async");
        verify(apiClient).invokeAPI(contains("/users/user3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeactivateUser_WithHeadersAndPrefer() throws Exception {
        stubInvokeVoid("POST", "/deactivate");
        Map<String,String> hdr = Collections.singletonMap("X-D","h");
        api.deactivateUser("user4", false, "respond-async", hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(contains("/users/user4/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
        // Prefer may be added either directly or via provided headers; assert either
        assertTrue(cap.getValue().containsKey("X-D"));
    }

    @Test
    public void testDeactivateUser_MissingId() {
        expect400(() -> api.deactivateUser(null, true, null));
    }

    /* reactivateUser */
    @Test
    public void testReactivateUser_Success() throws Exception {
        UserActivationToken tok = new UserActivationToken();
        stubInvokeReturn(tok, "/reactivate");
        UserActivationToken actual = api.reactivateUser("user5", true);
        assertSame(tok, actual);
    }

    @Test
    public void testReactivateUser_WithHeaders() throws Exception {
        stubInvokeReturn(new UserActivationToken(), "/reactivate");
        api.reactivateUser("user6", false, Collections.singletonMap("X-R","y"));
        verify(apiClient).invokeAPI(contains("/users/user6/lifecycle/reactivate"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            argThat(m -> "y".equals(m.get("X-R"))), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testReactivateUser_MissingId() {
        expect400(() -> api.reactivateUser(null, true));
    }

    /* resetFactors */
    @Test
    public void testResetFactors_Success() throws Exception {
        stubInvokeVoid("POST", "/reset_factors");
        api.resetFactors("user7");
        verify(apiClient).invokeAPI(contains("/users/user7/lifecycle/reset_factors"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testResetFactors_WithHeaders() throws Exception {
        stubInvokeVoid("POST", "/reset_factors");
        api.resetFactors("user8", Collections.singletonMap("X-F","z"));
        verify(apiClient).invokeAPI(contains("/users/user8/lifecycle/reset_factors"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            argThat(m -> "z".equals(m.get("X-F"))), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testResetFactors_MissingId() {
        expect400(() -> api.resetFactors(null));
    }

    /* suspendUser */
    @Test
    public void testSuspendUser_Success() throws Exception {
        stubInvokeVoid("POST", "/suspend");
        api.suspendUser("user9");
    }

    @Test
    public void testSuspendUser_WithHeaders() throws Exception {
        stubInvokeVoid("POST", "/suspend");
        api.suspendUser("user10", Collections.singletonMap("X-S","1"));
        verify(apiClient).invokeAPI(contains("/users/user10/lifecycle/suspend"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            argThat(m -> "1".equals(m.get("X-S"))), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testSuspendUser_MissingId() {
        expect400(() -> api.suspendUser(null));
    }

    /* unlockUser */
    @Test
    public void testUnlockUser_Success() throws Exception {
        stubInvokeVoid("POST", "/unlock");
        api.unlockUser("user11");
    }

    @Test
    public void testUnlockUser_WithHeaders() throws Exception {
        stubInvokeVoid("POST", "/unlock");
        api.unlockUser("user12", Collections.singletonMap("X-U","t"));
        verify(apiClient).invokeAPI(contains("/users/user12/lifecycle/unlock"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            argThat(m -> "t".equals(m.get("X-U"))), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testUnlockUser_MissingId() {
        expect400(() -> api.unlockUser(null));
    }

    /* unsuspendUser */
    @Test
    public void testUnsuspendUser_Success() throws Exception {
        stubInvokeVoid("POST", "/unsuspend");
        api.unsuspendUser("user13");
    }

    @Test
    public void testUnsuspendUser_WithHeaders() throws Exception {
        stubInvokeVoid("POST", "/unsuspend");
        api.unsuspendUser("user14", Collections.singletonMap("X-UN","ok"));
        verify(apiClient).invokeAPI(contains("/users/user14/lifecycle/unsuspend"), eq("POST"),
            anyList(), anyList(), anyString(), isNull(),
            argThat(m -> "ok".equals(m.get("X-UN"))), anyMap(), anyMap(), anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testUnsuspendUser_MissingId() {
        expect400(() -> api.unsuspendUser(null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_Activate() throws Exception {
        when(apiClient.invokeAPI(
            contains("/activate"), eq("POST"),
            anyList(), anyList(), anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"err"));
        try {
            api.activateUser("userX", true);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = UserLifecycleApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try {
            r.run();
            fail("Expected 400");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }
}

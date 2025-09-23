package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.ServiceAccountApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.AppServiceAccount;
import com.okta.sdk.resource.model.AppServiceAccountForUpdate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ServiceAccountApiTest {

    private ApiClient apiClient;
    private ServiceAccountApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new ServiceAccountApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(Collections.emptyList());
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

    /* createAppServiceAccount */
    @Test
    public void testCreateAppServiceAccount_Success() throws Exception {
        AppServiceAccount expected = new AppServiceAccount();
        stubInvoke(expected);
        AppServiceAccount body = new AppServiceAccount();
        AppServiceAccount actual = api.createAppServiceAccount(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/privileged-access/api/v1/service-accounts"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateAppServiceAccount_WithHeaders() throws Exception {
        stubInvoke(new AppServiceAccount());
        Map<String, String> hdrs = Collections.singletonMap("X-Test-Header", "value");
        api.createAppServiceAccount(new AppServiceAccount(), hdrs);

        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("value", cap.getValue().get("X-Test-Header"));
    }

    @Test
    public void testCreateAppServiceAccount_MissingBody() {
        try {
            api.createAppServiceAccount(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("body"));
        }
    }

    /* deleteAppServiceAccount */
    @Test
    public void testDeleteAppServiceAccount_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAppServiceAccount("sa123");
        verify(apiClient).invokeAPI(
            eq("/privileged-access/api/v1/service-accounts/sa123"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("sa123");
    }

    @Test
    public void testDeleteAppServiceAccount_MissingId() {
        try {
            api.deleteAppServiceAccount(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("'id'"));
        }
    }

    /* getAppServiceAccount */
    @Test
    public void testGetAppServiceAccount_Success() throws Exception {
        AppServiceAccount expected = new AppServiceAccount();
        stubInvoke(expected);
        AppServiceAccount actual = api.getAppServiceAccount("sa456");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/privileged-access/api/v1/service-accounts/sa456"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("sa456");
    }

    @Test
    public void testGetAppServiceAccount_MissingId() {
        try {
            api.getAppServiceAccount(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("'id'"));
        }
    }

    /* listAppServiceAccounts */
    @Test
    public void testListAppServiceAccounts_Success_AllParams() throws Exception {
        List<AppServiceAccount> expected = Arrays.asList(new AppServiceAccount());
        stubInvoke(expected);
        List<AppServiceAccount> actual = api.listAppServiceAccounts(50, "after_cursor", "search_term");
        assertSame(expected, actual);

        verify(apiClient).parameterToPair("limit", 50);
        verify(apiClient).parameterToPair("after", "after_cursor");
        verify(apiClient).parameterToPair("match", "search_term");

        verify(apiClient).invokeAPI(
            eq("/privileged-access/api/v1/service-accounts"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    /* updateAppServiceAccount */
    @Test
    public void testUpdateAppServiceAccount_Success() throws Exception {
        AppServiceAccount expected = new AppServiceAccount();
        stubInvoke(expected);
        AppServiceAccountForUpdate body = new AppServiceAccountForUpdate();
        AppServiceAccount actual = api.updateAppServiceAccount("sa789", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/privileged-access/api/v1/service-accounts/sa789"), eq("PATCH"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("sa789");
    }

    @Test
    public void testUpdateAppServiceAccount_MissingId() {
        try {
            api.updateAppServiceAccount(null, new AppServiceAccountForUpdate());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
            assertTrue(ex.getMessage().contains("'id'"));
        }
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503, "Service Unavailable"));
        try {
            api.getAppServiceAccount("sa-error");
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(503, ex.getCode());
            assertEquals("Service Unavailable", ex.getMessage());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = ServiceAccountApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}
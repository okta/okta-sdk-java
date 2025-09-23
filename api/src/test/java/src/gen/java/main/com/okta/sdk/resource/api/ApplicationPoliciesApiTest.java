package src.gen.java.main.com.okta.sdk.resource.api;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationPoliciesApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationPoliciesApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);
        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");
        api = new com.okta.sdk.resource.api.ApplicationPoliciesApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAssignApplicationPolicy_success_defaultHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), eq("application/json"), eq("application/json"), any(), isNull()))
            .thenReturn(null);

        api.assignApplicationPolicy("app123", "pol456");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("PUT"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), eq("application/json"), eq("application/json"), any(), isNull());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app123/policies/pol456");
        assertTrue(headerCap.getValue().isEmpty(), "No additional headers expected");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAssignApplicationPolicy_success_withAdditionalHeaders() throws ApiException {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v1");

        when(apiClient.invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), eq("application/json"), eq("application/json"), any(), isNull()))
            .thenReturn(null);

        api.assignApplicationPolicy("appH", "polH", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), eq("application/json"), eq("application/json"), any(), isNull());

        assertEquals(headerCap.getValue().get("X-Test"), "v1");
        assertEquals(headerCap.getValue().size(), 1);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testAssignApplicationPolicy_missingAppId() throws ApiException {
        api.assignApplicationPolicy(null, "pol1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testAssignApplicationPolicy_missingPolicyId() throws ApiException {
        api.assignApplicationPolicy("app1", null);
    }
}

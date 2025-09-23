package src.gen.java.main.com.okta.sdk.resource.api;

import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationLogosApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationLogosApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);
        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("multipart/form-data");
        api = new com.okta.sdk.resource.api.ApplicationLogosApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUploadApplicationLogo_success_defaultHeaders() throws ApiException {
        File file = mock(File.class);
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.uploadApplicationLogo("app123", file);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> formCap = ArgumentCaptor.forClass(Map.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), formCap.capture(), anyString(), anyString(), any(), isNull());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app123/logo");
        assertTrue(formCap.getValue().containsKey("file"));
        assertSame(formCap.getValue().get("file"), file);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUploadApplicationLogo_success_withAdditionalHeaders() throws ApiException {
        File file = mock(File.class);
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v1");

        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.uploadApplicationLogo("appH", file, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(headerCap.getValue().get("X-Test"), "v1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUploadApplicationLogo_missingAppId() throws ApiException {
        api.uploadApplicationLogo(null, mock(File.class));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testUploadApplicationLogo_missingFile() throws ApiException {
        api.uploadApplicationLogo("appX", null);
    }
}

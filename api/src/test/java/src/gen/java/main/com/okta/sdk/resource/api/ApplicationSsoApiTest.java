package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.api.ApplicationSsoApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationSsoApiTest {

    private ApiClient apiClient;
    private ApplicationSsoApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(inv.getArgument(0), String.valueOf(v)));
        });
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("text/xml");
        when(apiClient.selectHeaderContentType(any())).thenReturn("text/xml");

        api = new ApplicationSsoApi(apiClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPreviewSAMLmetadataForApplication_success_defaultHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), eq("text/xml"), eq("text/xml"), any(), any(TypeReference.class)))
            .thenReturn("<xml/>");

        String out = api.previewSAMLmetadataForApplication("app123", "kid789");
        assertEquals(out, "<xml/>");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qParamsCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);

        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), qParamsCap.capture(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), eq("text/xml"), eq("text/xml"), any(), any());

        assertEquals(pathCap.getValue(), "/api/v1/apps/app123/sso/saml/metadata");
        assertTrue(hasPair(qParamsCap.getValue(), "kid", "kid789"));
        assertTrue(headerCap.getValue().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPreviewSAMLmetadataForApplication_success_withAdditionalHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn("<xml/>");

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test", "val1");

        api.previewSAMLmetadataForApplication("appH", "kidH", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Test"), "val1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testPreviewSAMLmetadataForApplication_missingAppId() throws ApiException {
        api.previewSAMLmetadataForApplication(null, "kid1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testPreviewSAMLmetadataForApplication_missingKid() throws ApiException {
        api.previewSAMLmetadataForApplication("app1", null);
    }

    // helper
    private boolean hasPair(List<?> list, String name, String value) {
        for (Object o : list) {
            Pair p = (Pair) o;
            if (name.equals(p.getName()) && value.equals(String.valueOf(p.getValue()))) return true;
        }
        return false;
    }
}

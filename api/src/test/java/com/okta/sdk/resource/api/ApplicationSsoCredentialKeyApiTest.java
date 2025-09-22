package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.Csr;
import com.okta.sdk.resource.model.CsrMetadata;
import com.okta.sdk.resource.model.JsonWebKey;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ApplicationSsoCredentialKeyApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationSsoCredentialKeyApi api;

    @BeforeMethod
    public void setUp() {
        apiClient = mock(ApiClient.class);
        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(inv.getArgument(0), String.valueOf(v)));
        });
        when(apiClient.selectHeaderAccept(any(), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any())).thenReturn("application/json");
        api = new com.okta.sdk.resource.api.ApplicationSsoCredentialKeyApi(apiClient);
    }

    // cloneApplicationKey
    @Test
    @SuppressWarnings("unchecked")
    public void testCloneApplicationKey_success_defaultHeaders() throws ApiException {
        JsonWebKey ret = new JsonWebKey();
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(ret);

        JsonWebKey out = api.cloneApplicationKey("app1","k1","tApp");
        assertNotNull(out);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), qCap.capture(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app1/credentials/keys/k1/clone");
        assertTrue(hasPair(qCap.getValue(),"targetAid","tApp"));
        assertTrue(headerCap.getValue().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCloneApplicationKey_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","v1");
        api.cloneApplicationKey("appH","kH","tH", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Test"), "v1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCloneApplicationKey_missingAppId() throws ApiException {
        api.cloneApplicationKey(null,"k1","t1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCloneApplicationKey_missingKeyId() throws ApiException {
        api.cloneApplicationKey("app1",null,"t1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testCloneApplicationKey_missingTarget() throws ApiException {
        api.cloneApplicationKey("app1","k1",null);
    }

    // generateApplicationKey
    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateApplicationKey_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        api.generateApplicationKey("app2", 5);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> qCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), qCap.capture(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app2/credentials/keys/generate");
        assertTrue(hasPair(qCap.getValue(),"validityYears","5"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateApplicationKey_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Gen","1");
        api.generateApplicationKey("app3", 3, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Gen"), "1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGenerateApplicationKey_missingAppId() throws ApiException {
        api.generateApplicationKey(null, 1);
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGenerateApplicationKey_missingValidity() throws ApiException {
        api.generateApplicationKey("app4", null);
    }

    // generateCsrForApplication
    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateCsrForApplication_success() throws ApiException {
        CsrMetadata meta = new CsrMetadata();
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(meta),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn("csr-data");

        String out = api.generateCsrForApplication("app5", meta);
        assertEquals(out, "csr-data");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app5/credentials/csrs");
        assertSame(bodyCap.getValue(), meta);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateCsrForApplication_withHeaders() throws ApiException {
        CsrMetadata meta = new CsrMetadata();
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(meta),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn("csr-data");

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Csr","y");
        api.generateCsrForApplication("app6", meta, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Csr"), "y");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGenerateCsrForApplication_missingAppId() throws ApiException {
        api.generateCsrForApplication(null, new CsrMetadata());
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGenerateCsrForApplication_missingMetadata() throws ApiException {
        api.generateCsrForApplication("app7", null);
    }

    // getApplicationKey
    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplicationKey_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        api.getApplicationKey("app8","k8");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app8/credentials/keys/k8");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetApplicationKey_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-GK","1");
        api.getApplicationKey("app9","k9", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-GK"), "1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApplicationKey_missingAppId() throws ApiException {
        api.getApplicationKey(null,"k1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetApplicationKey_missingKeyId() throws ApiException {
        api.getApplicationKey("app1",null);
    }

    // getCsrForApplication
    @Test
    @SuppressWarnings("unchecked")
    public void testGetCsrForApplication_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new Csr());

        api.getCsrForApplication("app10","csr10");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app10/credentials/csrs/csr10");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCsrForApplication_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new Csr());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-GCSR","h");
        api.getCsrForApplication("app11","csr11", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-GCSR"), "h");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetCsrForApplication_missingAppId() throws ApiException {
        api.getCsrForApplication(null,"c1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testGetCsrForApplication_missingCsrId() throws ApiException {
        api.getCsrForApplication("app1",null);
    }

    // listApplicationKeys
    @Test
    @SuppressWarnings("unchecked")
    public void testListApplicationKeys_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new JsonWebKey()));

        List<JsonWebKey> list = api.listApplicationKeys("app12");
        assertEquals(list.size(),1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app12/credentials/keys");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListApplicationKeys_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-LK","l");
        api.listApplicationKeys("app13", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-LK"), "l");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListApplicationKeys_missingAppId() throws ApiException {
        api.listApplicationKeys(null);
    }

    // listCsrsForApplication
    @Test
    @SuppressWarnings("unchecked")
    public void testListCsrsForApplication_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new Csr()));

        List<Csr> list = api.listCsrsForApplication("app14");
        assertEquals(list.size(),1);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app14/credentials/csrs");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListCsrsForApplication_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(Collections.emptyList());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-LCSR","1");
        api.listCsrsForApplication("app15", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("GET"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-LCSR"), "1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testListCsrsForApplication_missingAppId() throws ApiException {
        api.listCsrsForApplication(null);
    }

    // publishCsrFromApplication
    @Test
    @SuppressWarnings("unchecked")
    public void testPublishCsrFromApplication_success() throws ApiException {
        File file = mock(File.class);
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(file),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        api.publishCsrFromApplication("app16","csr16", file);

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("POST"), anyList(), anyList(), anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app16/credentials/csrs/csr16/lifecycle/publish");
        assertSame(bodyCap.getValue(), file);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPublishCsrFromApplication_withHeaders() throws ApiException {
        File file = mock(File.class);
        when(apiClient.invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), eq(file),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), any(TypeReference.class)))
            .thenReturn(new JsonWebKey());

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Pub","p");
        api.publishCsrFromApplication("app17","csr17", file, headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), any());
        assertEquals(headerCap.getValue().get("X-Pub"), "p");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testPublishCsrFromApplication_missingAppId() throws ApiException {
        api.publishCsrFromApplication(null,"c1", mock(File.class));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testPublishCsrFromApplication_missingCsrId() throws ApiException {
        api.publishCsrFromApplication("app1",null, mock(File.class));
    }

    @Test(expectedExceptions = ApiException.class)
    public void testPublishCsrFromApplication_missingBody() throws ApiException {
        api.publishCsrFromApplication("app1","c1", null);
    }

    // revokeCsrFromApplication
    @Test
    @SuppressWarnings("unchecked")
    public void testRevokeCsrFromApplication_success() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        api.revokeCsrFromApplication("app18","csr18");

        ArgumentCaptor<String> pathCap = ArgumentCaptor.forClass(String.class);
        verify(apiClient).invokeAPI(pathCap.capture(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(pathCap.getValue(), "/api/v1/apps/app18/credentials/csrs/csr18");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRevokeCsrFromApplication_withHeaders() throws ApiException {
        when(apiClient.invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            anyMap(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull()))
            .thenReturn(null);

        Map<String,String> headers = new HashMap<>();
        headers.put("X-Rev","r");
        api.revokeCsrFromApplication("app19","csr19", headers);

        ArgumentCaptor<Map> headerCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), isNull(),
            headerCap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(), isNull());
        assertEquals(headerCap.getValue().get("X-Rev"), "r");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRevokeCsrFromApplication_missingAppId() throws ApiException {
        api.revokeCsrFromApplication(null,"c1");
    }

    @Test(expectedExceptions = ApiException.class)
    public void testRevokeCsrFromApplication_missingCsrId() throws ApiException {
        api.revokeCsrFromApplication("app1",null);
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

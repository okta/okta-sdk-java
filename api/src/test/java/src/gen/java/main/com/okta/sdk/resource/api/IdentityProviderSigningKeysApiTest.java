package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.CsrMetadata;
import com.okta.sdk.resource.model.IdPCsr;
import com.okta.sdk.resource.model.IdPKeyCredential;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class IdentityProviderSigningKeysApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.IdentityProviderSigningKeysApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.IdentityProviderSigningKeysApi(apiClient);

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

    /* cloneIdentityProviderKey */
    @Test
    public void testCloneIdentityProviderKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPKeyCredential actual = api.cloneIdentityProviderKey("idp1","kid1","target1");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("targetIdpId","target1");
        verify(apiClient).escapeString("idp1");
        verify(apiClient).escapeString("kid1");
    }

    @Test
    public void testCloneIdentityProviderKey_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        api.cloneIdentityProviderKey("idpH","kidH","targetH", Collections.singletonMap("X-C","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-C"));
    }

    @Test
    public void testCloneIdentityProviderKey_MissingParams() {
        // idpId
        try { api.cloneIdentityProviderKey(null,"k","t"); fail("Expected"); } catch (ApiException e) { assertEquals(400,e.getCode()); }
        // kid
        try { api.cloneIdentityProviderKey("idp",null,"t"); fail("Expected"); } catch (ApiException e) { assertEquals(400,e.getCode()); }
        // targetIdpId
        try { api.cloneIdentityProviderKey("idp","k",null); fail("Expected"); } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    /* generateCsrForIdentityProvider */
    @Test
    public void testGenerateCsrForIdentityProvider_Success() throws Exception {
        IdPCsr expected = new IdPCsr();
        stubInvoke(expected);
        CsrMetadata meta = new CsrMetadata();
        IdPCsr actual = api.generateCsrForIdentityProvider("idp2", meta);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp2/credentials/csrs"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(meta, bodyCap.getValue());
        verify(apiClient).escapeString("idp2");
    }

    @Test
    public void testGenerateCsrForIdentityProvider_MissingParams() {
        try { api.generateCsrForIdentityProvider(null, new CsrMetadata()); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.generateCsrForIdentityProvider("idp3", null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* generateIdentityProviderSigningKey */
    @Test
    public void testGenerateIdentityProviderSigningKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPKeyCredential actual = api.generateIdentityProviderSigningKey("idp4", 2);
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("validityYears",2);
        verify(apiClient).escapeString("idp4");
    }

    @Test
    public void testGenerateIdentityProviderSigningKey_MissingIdpId() {
        try { api.generateIdentityProviderSigningKey(null, 1); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* getCsrForIdentityProvider */
    @Test
    public void testGetCsrForIdentityProvider_Success() throws Exception {
        IdPCsr expected = new IdPCsr();
        stubInvoke(expected);
        IdPCsr actual = api.getCsrForIdentityProvider("idp5","csr1");
        assertSame(expected, actual);
        verify(apiClient).escapeString("idp5");
        verify(apiClient).escapeString("csr1");
    }

    @Test
    public void testGetCsrForIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdPCsr());
        api.getCsrForIdentityProvider("idp6","csr2", Collections.singletonMap("X-G","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetCsrForIdentityProvider_MissingParams() {
        try { api.getCsrForIdentityProvider(null,"c"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.getCsrForIdentityProvider("idp",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* getIdentityProviderSigningKey */
    @Test
    public void testGetIdentityProviderSigningKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPKeyCredential actual = api.getIdentityProviderSigningKey("idp7","kid7");
        assertSame(expected, actual);
        verify(apiClient).escapeString("idp7");
        verify(apiClient).escapeString("kid7");
    }

    @Test
    public void testGetIdentityProviderSigningKey_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        api.getIdentityProviderSigningKey("idp8","kid8", Collections.singletonMap("X-H","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-H"));
    }

    @Test
    public void testGetIdentityProviderSigningKey_MissingParams() {
        try { api.getIdentityProviderSigningKey(null,"k"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.getIdentityProviderSigningKey("idp",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listActiveIdentityProviderSigningKey */
    @Test
    public void testListActiveIdentityProviderSigningKey_Success() throws Exception {
        List<IdPKeyCredential> expected = Arrays.asList(new IdPKeyCredential());
        stubInvoke(expected);
        List<IdPKeyCredential> actual = api.listActiveIdentityProviderSigningKey("idp9");
        assertSame(expected, actual);
        verify(apiClient).escapeString("idp9");
    }

    @Test
    public void testListActiveIdentityProviderSigningKey_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listActiveIdentityProviderSigningKey("idp10", Collections.singletonMap("X-A","y"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("y", cap.getValue().get("X-A"));
    }

    @Test
    public void testListActiveIdentityProviderSigningKey_MissingIdpId() {
        try { api.listActiveIdentityProviderSigningKey(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listCsrsForIdentityProvider */
    @Test
    public void testListCsrsForIdentityProvider_Success() throws Exception {
        stubInvoke(Collections.emptyList());
        List<IdPCsr> list = api.listCsrsForIdentityProvider("idp11");
        assertNotNull(list);
        verify(apiClient).escapeString("idp11");
    }

    @Test
    public void testListCsrsForIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listCsrsForIdentityProvider("idp12", Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testListCsrsForIdentityProvider_MissingIdpId() {
        try { api.listCsrsForIdentityProvider(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listIdentityProviderSigningKeys */
    @Test
    public void testListIdentityProviderSigningKeys_Success() throws Exception {
        stubInvoke(Collections.emptyList());
        List<IdPKeyCredential> list = api.listIdentityProviderSigningKeys("idp13");
        assertNotNull(list);
        verify(apiClient).escapeString("idp13");
    }

    @Test
    public void testListIdentityProviderSigningKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listIdentityProviderSigningKeys("idp14", Collections.singletonMap("X-K","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-K"));
    }

    @Test
    public void testListIdentityProviderSigningKeys_MissingIdpId() {
        try { api.listIdentityProviderSigningKeys(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* publishCsrForIdentityProvider */
    @Test
    public void testPublishCsrForIdentityProvider_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        File cert = mock(File.class);
        IdPKeyCredential actual = api.publishCsrForIdentityProvider("idp15","csr15", cert);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp15/credentials/csrs/csr15/lifecycle/publish"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(cert, bodyCap.getValue());
        verify(apiClient).escapeString("idp15");
        verify(apiClient).escapeString("csr15");
    }

    @Test
    public void testPublishCsrForIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        File cert = mock(File.class);
        api.publishCsrForIdentityProvider("idp16","csr16", cert, Collections.singletonMap("X-P","z"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("z", cap.getValue().get("X-P"));
    }

    @Test
    public void testPublishCsrForIdentityProvider_MissingParams() {
        File cert = mock(File.class);
        try { api.publishCsrForIdentityProvider(null,"c",cert); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.publishCsrForIdentityProvider("idp",null,cert); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.publishCsrForIdentityProvider("idp","c",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* revokeCsrForIdentityProvider */
    @Test
    public void testRevokeCsrForIdentityProvider_Success() throws Exception {
        stubVoidInvoke();
        api.revokeCsrForIdentityProvider("idp17","csr17");
        verify(apiClient).escapeString("idp17");
        verify(apiClient).escapeString("csr17");
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp17/credentials/csrs/csr17"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testRevokeCsrForIdentityProvider_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.revokeCsrForIdentityProvider("idp18","csr18", Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-R"));
    }

    @Test
    public void testRevokeCsrForIdentityProvider_MissingParams() {
        try { api.revokeCsrForIdentityProvider(null,"c"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
        try { api.revokeCsrForIdentityProvider("idp",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testPublishCsrForIdentityProvider_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.publishCsrForIdentityProvider("idpX","csrX", mock(File.class));
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(500, e.getCode());
            assertTrue(e.getMessage().contains("boom"));
        }
    }

    @Test
    public void testCloneIdentityProviderKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.cloneIdentityProviderKey("idpZ","kidZ","targetZ");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.IdentityProviderSigningKeysApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

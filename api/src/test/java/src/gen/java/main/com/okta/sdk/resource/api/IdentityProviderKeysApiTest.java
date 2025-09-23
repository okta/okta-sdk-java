package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.IdPCertificateCredential;
import com.okta.sdk.resource.model.IdPKeyCredential;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class IdentityProviderKeysApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.IdentityProviderKeysApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.IdentityProviderKeysApi(apiClient);

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

    /* createIdentityProviderKey */
    @Test
    public void testCreateIdentityProviderKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPCertificateCredential body = new IdPCertificateCredential();
        IdPKeyCredential actual = api.createIdentityProviderKey(body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateIdentityProviderKey_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        api.createIdentityProviderKey(new IdPCertificateCredential(), Collections.singletonMap("X-C","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-C"));
    }

    @Test
    public void testCreateIdentityProviderKey_MissingBody() {
        try {
            api.createIdentityProviderKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* deleteIdentityProviderKey */
    @Test
    public void testDeleteIdentityProviderKey_Success() throws Exception {
        stubVoidInvoke();
        api.deleteIdentityProviderKey("kid1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys/kid1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("kid1");
    }

    @Test
    public void testDeleteIdentityProviderKey_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteIdentityProviderKey("kid2", Collections.singletonMap("X-D","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteIdentityProviderKey_MissingKid() {
        try {
            api.deleteIdentityProviderKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getIdentityProviderKey */
    @Test
    public void testGetIdentityProviderKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPKeyCredential actual = api.getIdentityProviderKey("kid3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys/kid3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("kid3");
    }

    @Test
    public void testGetIdentityProviderKey_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        api.getIdentityProviderKey("kid4", Collections.singletonMap("X-G","g"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetIdentityProviderKey_MissingKid() {
        try {
            api.getIdentityProviderKey(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listIdentityProviderKeys */
    @Test
    public void testListIdentityProviderKeys_Success_AllParams() throws Exception {
        List<IdPKeyCredential> expected = Arrays.asList(new IdPKeyCredential());
        stubInvoke(expected);
        List<IdPKeyCredential> actual = api.listIdentityProviderKeys("cursor1", 50);
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("after","cursor1");
        verify(apiClient).parameterToPair("limit",50);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListIdentityProviderKeys_Success_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<IdPKeyCredential> list = api.listIdentityProviderKeys(null, null);
        assertNotNull(list);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListIdentityProviderKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listIdentityProviderKeys(null, null, Collections.singletonMap("X-L","1"));
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

    /* replaceIdentityProviderKey */
    @Test
    public void testReplaceIdentityProviderKey_Success() throws Exception {
        IdPKeyCredential expected = new IdPKeyCredential();
        stubInvoke(expected);
        IdPKeyCredential body = new IdPKeyCredential();
        IdPKeyCredential actual = api.replaceIdentityProviderKey("kid5", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/credentials/keys/kid5"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("kid5");
    }

    @Test
    public void testReplaceIdentityProviderKey_WithHeaders() throws Exception {
        stubInvoke(new IdPKeyCredential());
        api.replaceIdentityProviderKey("kid6", new IdPKeyCredential(), Collections.singletonMap("X-R","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceIdentityProviderKey_MissingKid() {
        try {
            api.replaceIdentityProviderKey(null, new IdPKeyCredential());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testReplaceIdentityProviderKey_MissingBody() {
        try {
            api.replaceIdentityProviderKey("kid7", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateIdentityProviderKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createIdentityProviderKey(new IdPCertificateCredential());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    @Test
    public void testReplaceIdentityProviderKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.replaceIdentityProviderKey("kid8", new IdPKeyCredential());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(503, ex.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.IdentityProviderKeysApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

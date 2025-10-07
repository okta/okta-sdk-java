package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.OAuth2ResourceServerCredentialsKeysApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.OAuth2ResourceServerJsonWebKey;
import com.okta.sdk.resource.model.OAuth2ResourceServerJsonWebKeyRequestBody;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class OAuth2ResourceServerCredentialsKeysApiTest {

    private ApiClient apiClient;
    private OAuth2ResourceServerCredentialsKeysApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new OAuth2ResourceServerCredentialsKeysApi(apiClient);

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

    /* addOAuth2ResourceServerJsonWebKey */
    @Test
    public void testAddOAuth2ResourceServerJsonWebKey_Success() throws Exception {
        OAuth2ResourceServerJsonWebKey expected = new OAuth2ResourceServerJsonWebKey();
        stubInvoke(expected);
        OAuth2ResourceServerJsonWebKeyRequestBody body = new OAuth2ResourceServerJsonWebKeyRequestBody();
        OAuth2ResourceServerJsonWebKey actual = api.addOAuth2ResourceServerJsonWebKey("as1", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as1/resourceservercredentials/keys"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("as1");
    }

    @Test
    public void testAddOAuth2ResourceServerJsonWebKey_WithHeaders() throws Exception {
        stubInvoke(new OAuth2ResourceServerJsonWebKey());
        api.addOAuth2ResourceServerJsonWebKey("asH", new OAuth2ResourceServerJsonWebKeyRequestBody(),
            Collections.singletonMap("X-C","v"));
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
    public void testAddOAuth2ResourceServerJsonWebKey_MissingAuthServerId() {
        try {
            api.addOAuth2ResourceServerJsonWebKey(null, new OAuth2ResourceServerJsonWebKeyRequestBody());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    @Test
    public void testAddOAuth2ResourceServerJsonWebKey_MissingBody() {
        try {
            api.addOAuth2ResourceServerJsonWebKey("as1", null);
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }

    /* activateOAuth2ResourceServerJsonWebKey */
    @Test
    public void testActivateOAuth2ResourceServerJsonWebKey_Success() throws Exception {
        OAuth2ResourceServerJsonWebKey expected = new OAuth2ResourceServerJsonWebKey();
        stubInvoke(expected);
        OAuth2ResourceServerJsonWebKey actual = api.activateOAuth2ResourceServerJsonWebKey("as2","kid1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as2/resourceservercredentials/keys/kid1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("as2");
        verify(apiClient).escapeString("kid1");
    }

    @Test
    public void testActivateOAuth2ResourceServerJsonWebKey_WithHeaders() throws Exception {
        stubInvoke(new OAuth2ResourceServerJsonWebKey());
        api.activateOAuth2ResourceServerJsonWebKey("asH","kH", Collections.singletonMap("X-A","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateOAuth2ResourceServerJsonWebKey_MissingAuthServerId() {
        try {
            api.activateOAuth2ResourceServerJsonWebKey(null,"k1");
            fail("Expected");
        } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testActivateOAuth2ResourceServerJsonWebKey_MissingKeyId() {
        try {
            api.activateOAuth2ResourceServerJsonWebKey("as2", null);
            fail("Expected");
        } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deactivateOAuth2ResourceServerJsonWebKey */
    @Test
    public void testDeactivateOAuth2ResourceServerJsonWebKey_Success() throws Exception {
        OAuth2ResourceServerJsonWebKey expected = new OAuth2ResourceServerJsonWebKey();
        stubInvoke(expected);
        OAuth2ResourceServerJsonWebKey actual = api.deactivateOAuth2ResourceServerJsonWebKey("as3","k2");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as3/resourceservercredentials/keys/k2/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testDeactivateOAuth2ResourceServerJsonWebKey_MissingAuthServerId() {
        try { api.deactivateOAuth2ResourceServerJsonWebKey(null,"k2"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testDeactivateOAuth2ResourceServerJsonWebKey_MissingKeyId() {
        try { api.deactivateOAuth2ResourceServerJsonWebKey("as3",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* deleteOAuth2ResourceServerJsonWebKey */
    @Test
    public void testDeleteOAuth2ResourceServerJsonWebKey_Success() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2ResourceServerJsonWebKey("as4","k3");
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as4/resourceservercredentials/keys/k3"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeleteOAuth2ResourceServerJsonWebKey_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteOAuth2ResourceServerJsonWebKey("asH","kH", Collections.singletonMap("X-D","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteOAuth2ResourceServerJsonWebKey_MissingAuthServerId() {
        try { api.deleteOAuth2ResourceServerJsonWebKey(null,"k3"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testDeleteOAuth2ResourceServerJsonWebKey_MissingKeyId() {
        try { api.deleteOAuth2ResourceServerJsonWebKey("as4",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* getOAuth2ResourceServerJsonWebKey */
    @Test
    public void testGetOAuth2ResourceServerJsonWebKey_Success() throws Exception {
        OAuth2ResourceServerJsonWebKey expected = new OAuth2ResourceServerJsonWebKey();
        stubInvoke(expected);
        OAuth2ResourceServerJsonWebKey actual = api.getOAuth2ResourceServerJsonWebKey("as5","k4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as5/resourceservercredentials/keys/k4"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetOAuth2ResourceServerJsonWebKey_WithHeaders() throws Exception {
        stubInvoke(new OAuth2ResourceServerJsonWebKey());
        api.getOAuth2ResourceServerJsonWebKey("asG","kG", Collections.singletonMap("X-G","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-G"));
    }

    @Test
    public void testGetOAuth2ResourceServerJsonWebKey_MissingAuthServerId() {
        try { api.getOAuth2ResourceServerJsonWebKey(null,"k4"); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    @Test
    public void testGetOAuth2ResourceServerJsonWebKey_MissingKeyId() {
        try { api.getOAuth2ResourceServerJsonWebKey("as5",null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* listOAuth2ResourceServerJsonWebKeys */
    @Test
    public void testListOAuth2ResourceServerJsonWebKeys_Success() throws Exception {
        List<OAuth2ResourceServerJsonWebKey> expected = Arrays.asList(new OAuth2ResourceServerJsonWebKey());
        stubInvoke(expected);
        List<OAuth2ResourceServerJsonWebKey> list = api.listOAuth2ResourceServerJsonWebKeys("as6");
        assertSame(expected, list);
        verify(apiClient).invokeAPI(
            eq("/api/v1/authorizationServers/as6/resourceservercredentials/keys"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListOAuth2ResourceServerJsonWebKeys_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listOAuth2ResourceServerJsonWebKeys("asL", Collections.singletonMap("X-L","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-L"));
    }

    @Test
    public void testListOAuth2ResourceServerJsonWebKeys_MissingAuthServerId() {
        try { api.listOAuth2ResourceServerJsonWebKeys(null); fail("Expected"); } catch (ApiException e){ assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testAddOAuth2ResourceServerJsonWebKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.addOAuth2ResourceServerJsonWebKey("asErr", new OAuth2ResourceServerJsonWebKeyRequestBody());
            fail("Expected");
        } catch (ApiException e){
            assertEquals(502, e.getCode());
            assertTrue(e.getMessage().contains("bad"));
        }
    }

    @Test
    public void testDeleteOAuth2ResourceServerJsonWebKey_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.deleteOAuth2ResourceServerJsonWebKey("asErr2","kErr2");
            fail("Expected");
        } catch (ApiException e){
            assertEquals(503, e.getCode());
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = OAuth2ResourceServerCredentialsKeysApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

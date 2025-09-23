package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.IdentityProvider;
import com.okta.sdk.resource.model.IdentityProviderType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class IdentityProviderApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.IdentityProviderApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.IdentityProviderApi(apiClient);

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

    /* activateIdentityProvider */
    @Test
    public void testActivateIdentityProvider_Success() throws Exception {
        IdentityProvider expected = new IdentityProvider();
        stubInvoke(expected);
        IdentityProvider actual = api.activateIdentityProvider("idp1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp1/lifecycle/activate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp1");
    }

    @Test
    public void testActivateIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProvider());
        api.activateIdentityProvider("idp2", Collections.singletonMap("X-A","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-A"));
    }

    @Test
    public void testActivateIdentityProvider_MissingId() {
        try {
            api.activateIdentityProvider(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* createIdentityProvider */
    @Test
    public void testCreateIdentityProvider_Success() throws Exception {
        IdentityProvider expected = new IdentityProvider();
        stubInvoke(expected);
        IdentityProvider body = new IdentityProvider();
        IdentityProvider actual = api.createIdentityProvider(body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProvider());
        api.createIdentityProvider(new IdentityProvider(), Collections.singletonMap("X-C","v"));
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
    public void testCreateIdentityProvider_MissingBody() {
        try {
            api.createIdentityProvider(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* deactivateIdentityProvider */
    @Test
    public void testDeactivateIdentityProvider_Success() throws Exception {
        IdentityProvider expected = new IdentityProvider();
        stubInvoke(expected);
        IdentityProvider actual = api.deactivateIdentityProvider("idp3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp3/lifecycle/deactivate"), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp3");
    }

    @Test
    public void testDeactivateIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProvider());
        api.deactivateIdentityProvider("idp4", Collections.singletonMap("X-D","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeactivateIdentityProvider_MissingId() {
        try {
            api.deactivateIdentityProvider(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* deleteIdentityProvider */
    @Test
    public void testDeleteIdentityProvider_Success() throws Exception {
        stubVoidInvoke();
        api.deleteIdentityProvider("idp5");
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp5"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("idp5");
    }

    @Test
    public void testDeleteIdentityProvider_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteIdentityProvider("idp6", Collections.singletonMap("X-Del","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-Del"));
    }

    @Test
    public void testDeleteIdentityProvider_MissingId() {
        try {
            api.deleteIdentityProvider(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* getIdentityProvider */
    @Test
    public void testGetIdentityProvider_Success() throws Exception {
        IdentityProvider expected = new IdentityProvider();
        stubInvoke(expected);
        IdentityProvider actual = api.getIdentityProvider("idp7");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp7"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp7");
    }

    @Test
    public void testGetIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProvider());
        api.getIdentityProvider("idp8", Collections.singletonMap("X-G","g"));
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
    public void testGetIdentityProvider_MissingId() {
        try {
            api.getIdentityProvider(null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* listIdentityProviders */
    @Test
    public void testListIdentityProviders_Success_AllParams() throws Exception {
        List<IdentityProvider> expected = Arrays.asList(new IdentityProvider());
        stubInvoke(expected);
        List<IdentityProvider> actual = api.listIdentityProviders("query","after1",50, IdentityProviderType.SAML2);
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("q","query");
        verify(apiClient).parameterToPair("after","after1");
        verify(apiClient).parameterToPair("limit",50);
        verify(apiClient).parameterToPair("type", IdentityProviderType.SAML2);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListIdentityProviders_Success_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<IdentityProvider> list = api.listIdentityProviders(null,null,null,null);
        assertNotNull(list);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testListIdentityProviders_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listIdentityProviders(null,null,null,null, Collections.singletonMap("X-L","1"));
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

    /* replaceIdentityProvider */
    @Test
    public void testReplaceIdentityProvider_Success() throws Exception {
        IdentityProvider expected = new IdentityProvider();
        stubInvoke(expected);
        IdentityProvider body = new IdentityProvider();
        IdentityProvider actual = api.replaceIdentityProvider("idp9", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp9"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("idp9");
    }

    @Test
    public void testReplaceIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProvider());
        api.replaceIdentityProvider("idp10", new IdentityProvider(), Collections.singletonMap("X-R","r"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceIdentityProvider_MissingId() {
        try {
            api.replaceIdentityProvider(null, new IdentityProvider());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    public void testReplaceIdentityProvider_MissingBody() {
        try {
            api.replaceIdentityProvider("idpX", null);
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(400, ex.getCode());
        }
    }

    /* ApiException propagation */
    @Test
    public void testCreateIdentityProvider_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(500,"boom"));
        try {
            api.createIdentityProvider(new IdentityProvider());
            fail("Expected ApiException");
        } catch (ApiException ex) {
            assertEquals(500, ex.getCode());
            assertTrue(ex.getMessage().contains("boom"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.IdentityProviderApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

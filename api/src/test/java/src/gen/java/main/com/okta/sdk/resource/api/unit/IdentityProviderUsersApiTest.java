package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.IdentityProvider;
import com.okta.sdk.resource.model.IdentityProviderApplicationUser;
import com.okta.sdk.resource.model.SocialAuthToken;
import com.okta.sdk.resource.model.UserIdentityProviderLinkRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class IdentityProviderUsersApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.IdentityProviderUsersApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.IdentityProviderUsersApi(apiClient);

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

    /* getIdentityProviderApplicationUser */
    @Test
    public void testGetIdentityProviderApplicationUser_Success() throws Exception {
        IdentityProviderApplicationUser expected = new IdentityProviderApplicationUser();
        stubInvoke(expected);
        IdentityProviderApplicationUser actual = api.getIdentityProviderApplicationUser("idp1","user1");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp1/users/user1"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp1");
        verify(apiClient).escapeString("user1");
    }

    @Test
    public void testGetIdentityProviderApplicationUser_WithHeaders() throws Exception {
        stubInvoke(new IdentityProviderApplicationUser());
        api.getIdentityProviderApplicationUser("idp2","user2", Collections.singletonMap("X-H","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-H"));
    }

    @Test
    public void testGetIdentityProviderApplicationUser_MissingIdpId() {
        try {
            api.getIdentityProviderApplicationUser(null,"userX");
            fail("Expected ApiException");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    @Test
    public void testGetIdentityProviderApplicationUser_MissingUserId() {
        try {
            api.getIdentityProviderApplicationUser("idpX",null);
            fail("Expected ApiException");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    /* linkUserToIdentityProvider */
    @Test
    public void testLinkUserToIdentityProvider_Success() throws Exception {
        IdentityProviderApplicationUser expected = new IdentityProviderApplicationUser();
        stubInvoke(expected);
        UserIdentityProviderLinkRequest body = new UserIdentityProviderLinkRequest();
        IdentityProviderApplicationUser actual = api.linkUserToIdentityProvider("idp3","user3", body);
        assertSame(expected, actual);
        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp3/users/user3"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("idp3");
        verify(apiClient).escapeString("user3");
    }

    @Test
    public void testLinkUserToIdentityProvider_WithHeaders() throws Exception {
        stubInvoke(new IdentityProviderApplicationUser());
        UserIdentityProviderLinkRequest body = new UserIdentityProviderLinkRequest();
        api.linkUserToIdentityProvider("idp4","user4", body, Collections.singletonMap("X-L","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-L"));
    }

    @Test
    public void testLinkUserToIdentityProvider_MissingIdpId() {
        try {
            api.linkUserToIdentityProvider(null,"user", new UserIdentityProviderLinkRequest());
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    @Test
    public void testLinkUserToIdentityProvider_MissingUserId() {
        try {
            api.linkUserToIdentityProvider("idp",null, new UserIdentityProviderLinkRequest());
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    @Test
    public void testLinkUserToIdentityProvider_MissingBody() {
        try {
            api.linkUserToIdentityProvider("idp","user", null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    /* listIdentityProviderApplicationUsers */
    @Test
    public void testListIdentityProviderApplicationUsers_AllParams() throws Exception {
        List<IdentityProviderApplicationUser> expected = Arrays.asList(new IdentityProviderApplicationUser());
        stubInvoke(expected);
        List<IdentityProviderApplicationUser> actual =
            api.listIdentityProviderApplicationUsers("idp5","qVal","after1",50,"expandX");
        assertSame(expected, actual);
        verify(apiClient).parameterToPair("q","qVal");
        verify(apiClient).parameterToPair("after","after1");
        verify(apiClient).parameterToPair("limit",50);
        verify(apiClient).parameterToPair("expand","expandX");
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp5/users"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp5");
    }

    @Test
    public void testListIdentityProviderApplicationUsers_Minimal() throws Exception {
        stubInvoke(Collections.emptyList());
        List<IdentityProviderApplicationUser> list =
            api.listIdentityProviderApplicationUsers("idp6", null,null,null,null);
        assertNotNull(list);
    }

    @Test
    public void testListIdentityProviderApplicationUsers_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listIdentityProviderApplicationUsers("idp7", null,null,null,null,
            Collections.singletonMap("X-Q","z"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("z", cap.getValue().get("X-Q"));
    }

    @Test
    public void testListIdentityProviderApplicationUsers_MissingIdpId() {
        try {
            api.listIdentityProviderApplicationUsers(null,null,null,null,null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400, e.getCode()); }
    }

    /* listSocialAuthTokens */
    @Test
    public void testListSocialAuthTokens_Success() throws Exception {
        List<SocialAuthToken> expected = Arrays.asList(new SocialAuthToken());
        stubInvoke(expected);
        List<SocialAuthToken> actual = api.listSocialAuthTokens("idp8","user8");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp8/users/user8/credentials/tokens"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("idp8");
        verify(apiClient).escapeString("user8");
    }

    @Test
    public void testListSocialAuthTokens_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listSocialAuthTokens("idp9","user9", Collections.singletonMap("X-T","v"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("v", cap.getValue().get("X-T"));
    }

    @Test
    public void testListSocialAuthTokens_MissingIdpId() {
        try {
            api.listSocialAuthTokens(null,"u");
            fail("Expected");
        } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    @Test
    public void testListSocialAuthTokens_MissingUserId() {
        try {
            api.listSocialAuthTokens("idp",null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    /* listUserIdentityProviders */
    @Test
    public void testListUserIdentityProviders_Success() throws Exception {
        List<IdentityProvider> expected = Arrays.asList(new IdentityProvider());
        stubInvoke(expected);
        List<IdentityProvider> actual = api.listUserIdentityProviders("user10");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/users/user10/idps"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("user10");
    }

    @Test
    public void testListUserIdentityProviders_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listUserIdentityProviders("user11", Collections.singletonMap("X-U","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-U"));
    }

    @Test
    public void testListUserIdentityProviders_MissingUserId() {
        try {
            api.listUserIdentityProviders(null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    /* unlinkUserFromIdentityProvider */
    @Test
    public void testUnlinkUserFromIdentityProvider_Success() throws Exception {
        stubVoidInvoke();
        api.unlinkUserFromIdentityProvider("idp12","user12");
        verify(apiClient).invokeAPI(
            eq("/api/v1/idps/idp12/users/user12"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("idp12");
        verify(apiClient).escapeString("user12");
    }

    @Test
    public void testUnlinkUserFromIdentityProvider_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.unlinkUserFromIdentityProvider("idp13","user13", Collections.singletonMap("X-R","x"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), any(),
            cap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("x", cap.getValue().get("X-R"));
    }

    @Test
    public void testUnlinkUserFromIdentityProvider_MissingIdpId() {
        try {
            api.unlinkUserFromIdentityProvider(null,"user");
            fail("Expected");
        } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    @Test
    public void testUnlinkUserFromIdentityProvider_MissingUserId() {
        try {
            api.unlinkUserFromIdentityProvider("idp",null);
            fail("Expected");
        } catch (ApiException e) { assertEquals(400,e.getCode()); }
    }

    /* ApiException propagation */
    @Test
    public void testLinkUserToIdentityProvider_ApiExceptionPropagates() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"unavailable"));
        try {
            api.linkUserToIdentityProvider("idpZ","userZ", new UserIdentityProviderLinkRequest());
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
            assertTrue(e.getMessage().contains("unavailable"));
        }
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = com.okta.sdk.resource.api.IdentityProviderUsersApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }
}

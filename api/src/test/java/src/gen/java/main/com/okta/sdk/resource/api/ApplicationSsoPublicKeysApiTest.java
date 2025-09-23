package src.gen.java.main.com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.*;
import com.okta.sdk.resource.model.AddJwkRequest;
import com.okta.sdk.resource.model.ListJwk200ResponseInner;
import com.okta.sdk.resource.model.OAuth2ClientSecret;
import com.okta.sdk.resource.model.OAuth2ClientSecretRequestBody;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationSsoPublicKeysApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.ApplicationSsoPublicKeysApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.ApplicationSsoPublicKeysApi(apiClient);

        // Common stubs to prevent NPE in replaceAll
        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
    }

    private <T> void stubInvoke(T returnValue) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(), anyMap(), anyMap(), anyMap(),
            anyString(), anyString(), any(), any(TypeReference.class)
        )).thenReturn(returnValue);
    }

    @Test
    public void testActivateOAuth2ClientJsonWebKey() throws Exception {
        ListJwk200ResponseInner expected = new ListJwk200ResponseInner();
        stubInvoke(expected);
        assertEquals(expected, api.activateOAuth2ClientJsonWebKey("appId", "keyId"));
        verify(apiClient).escapeString("appId");
        verify(apiClient).escapeString("keyId");
    }

    @Test(expected = ApiException.class)
    public void testActivateOAuth2ClientJsonWebKey_MissingAppId() throws Exception {
        api.activateOAuth2ClientJsonWebKey(null, "keyId");
    }

    @Test(expected = ApiException.class)
    public void testActivateOAuth2ClientJsonWebKey_MissingKeyId() throws Exception {
        api.activateOAuth2ClientJsonWebKey("appId", null);
    }

    @Test
    public void testActivateOAuth2ClientSecret() throws Exception {
        OAuth2ClientSecret expected = new OAuth2ClientSecret();
        stubInvoke(expected);
        assertEquals(expected, api.activateOAuth2ClientSecret("appId", "secretId"));
    }

    @Test(expected = ApiException.class)
    public void testActivateOAuth2ClientSecret_MissingAppId() throws Exception {
        api.activateOAuth2ClientSecret(null, "secretId");
    }

    @Test(expected = ApiException.class)
    public void testActivateOAuth2ClientSecret_MissingSecretId() throws Exception {
        api.activateOAuth2ClientSecret("appId", null);
    }

    @Test
    public void testAddJwk() throws Exception {
        ListJwk200ResponseInner expected = new ListJwk200ResponseInner();
        stubInvoke(expected);
        assertEquals(expected, api.addJwk("appId", new AddJwkRequest()));
    }

    @Test(expected = ApiException.class)
    public void testAddJwk_MissingAppId() throws Exception {
        api.addJwk(null, new AddJwkRequest());
    }

    @Test(expected = ApiException.class)
    public void testAddJwk_MissingBody() throws Exception {
        api.addJwk("appId", null);
    }

    @Test
    public void testCreateOAuth2ClientSecret() throws Exception {
        OAuth2ClientSecret expected = new OAuth2ClientSecret();
        stubInvoke(expected);
        assertEquals(expected, api.createOAuth2ClientSecret("appId", new OAuth2ClientSecretRequestBody()));
    }

    @Test(expected = ApiException.class)
    public void testCreateOAuth2ClientSecret_MissingAppId() throws Exception {
        api.createOAuth2ClientSecret(null, new OAuth2ClientSecretRequestBody());
    }

    @Test
    public void testDeactivateOAuth2ClientSecret() throws Exception {
        OAuth2ClientSecret expected = new OAuth2ClientSecret();
        stubInvoke(expected);
        assertEquals(expected, api.deactivateOAuth2ClientSecret("appId", "secretId"));
    }

    @Test(expected = ApiException.class)
    public void testDeactivateOAuth2ClientSecret_MissingAppId() throws Exception {
        api.deactivateOAuth2ClientSecret(null, "secretId");
    }

    @Test(expected = ApiException.class)
    public void testDeactivateOAuth2ClientSecret_MissingSecretId() throws Exception {
        api.deactivateOAuth2ClientSecret("appId", null);
    }

    @Test
    public void testDeleteOAuth2ClientSecret() throws Exception {
        stubInvoke(null);
        api.deleteOAuth2ClientSecret("appId", "secretId");
        verify(apiClient).escapeString("secretId");
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2ClientSecret_MissingAppId() throws Exception {
        api.deleteOAuth2ClientSecret(null, "secretId");
    }

    @Test(expected = ApiException.class)
    public void testDeleteOAuth2ClientSecret_MissingSecretId() throws Exception {
        api.deleteOAuth2ClientSecret("appId", null);
    }

    @Test
    public void testDeleteJwk() throws Exception {
        stubInvoke(null);
        api.deletejwk("appId", "keyId");
        verify(apiClient).escapeString("keyId");
    }

    @Test(expected = ApiException.class)
    public void testDeleteJwk_MissingAppId() throws Exception {
        api.deletejwk(null, "keyId");
    }

    @Test(expected = ApiException.class)
    public void testDeleteJwk_MissingKeyId() throws Exception {
        api.deletejwk("appId", null);
    }

    @Test
    public void testGetJwk() throws Exception {
        GetJwk200Response expected = new GetJwk200Response();
        stubInvoke(expected);
        assertEquals(expected, api.getJwk("appId", "keyId"));
    }

    @Test(expected = ApiException.class)
    public void testGetJwk_MissingAppId() throws Exception {
        api.getJwk(null, "keyId");
    }

    @Test(expected = ApiException.class)
    public void testGetJwk_MissingKeyId() throws Exception {
        api.getJwk("appId", null);
    }

    @Test
    public void testGetOAuth2ClientSecret() throws Exception {
        OAuth2ClientSecret expected = new OAuth2ClientSecret();
        stubInvoke(expected);
        assertEquals(expected, api.getOAuth2ClientSecret("appId", "secretId"));
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2ClientSecret_MissingAppId() throws Exception {
        api.getOAuth2ClientSecret(null, "secretId");
    }

    @Test(expected = ApiException.class)
    public void testGetOAuth2ClientSecret_MissingSecretId() throws Exception {
        api.getOAuth2ClientSecret("appId", null);
    }

    @Test
    public void testListJwk() throws Exception {
        List<ListJwk200ResponseInner> expected = Collections.singletonList(new ListJwk200ResponseInner());
        stubInvoke(expected);
        assertEquals(expected, api.listJwk("appId"));
    }

    @Test(expected = ApiException.class)
    public void testListJwk_MissingAppId() throws Exception {
        api.listJwk(null);
    }

    @Test
    public void testListOAuth2ClientSecrets() throws Exception {
        List<OAuth2ClientSecret> expected = Collections.singletonList(new OAuth2ClientSecret());
        stubInvoke(expected);
        assertEquals(expected, api.listOAuth2ClientSecrets("appId"));
    }

    @Test(expected = ApiException.class)
    public void testListOAuth2ClientSecrets_MissingAppId() throws Exception {
        api.listOAuth2ClientSecrets(null);
    }
}

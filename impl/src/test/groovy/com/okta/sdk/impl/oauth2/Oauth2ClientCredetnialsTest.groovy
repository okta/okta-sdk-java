package com.okta.sdk.impl.oauth2

import com.okta.commons.http.config.HttpClientConfiguration
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.impl.config.ClientConfiguration
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.openapitools.client.ApiClient
import org.openapitools.client.auth.ApiKeyAuth
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.testng.Assert.*

class Oauth2ClientCredentialsTest {

    @Test
    void testAutoRefreshToken() {
        def accessTokenRetrieverService = mock(AccessTokenRetrieverServiceImpl)

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken()
        oAuth2AccessToken.setAccessToken("accessToken")
        oAuth2AccessToken.setExpiresIn(301)
        when(accessTokenRetrieverService.getOAuth2AccessToken()).then { oAuth2AccessToken }

        def credentials = new OAuth2ClientCredentials(accessTokenRetrieverService)
        assertNull(credentials.getCredentials())
        //token must be initialized manually once
        credentials.refreshOAuth2AccessToken()
        assertEquals(credentials.getCredentials().accessToken, "accessToken")
        verify(accessTokenRetrieverService).getOAuth2AccessToken()
        clearInvocations(accessTokenRetrieverService)

        //change then token value
        OAuth2AccessToken newOAuth2AccessToken = new OAuth2AccessToken()
        newOAuth2AccessToken.setAccessToken("newAccessToken")
        newOAuth2AccessToken.setExpiresIn(2)
        when(accessTokenRetrieverService.getOAuth2AccessToken()).then { newOAuth2AccessToken }

        //previous token not expired yet, token should not be refreshed
        credentials.applyToParams(new ArrayList<>(), new HashMap<>(), new HashMap<>())
        assertEquals(credentials.getCredentials().accessToken, "accessToken")
        verify(accessTokenRetrieverService, never()).getOAuth2AccessToken()
        clearInvocations(accessTokenRetrieverService)

        //let token expiration go below 5 minutes
        Thread.sleep(2000)

        //token is expired, it should get a new one with the new value
        credentials.applyToParams(new ArrayList<>(), new HashMap<>(), new HashMap<>())
        assertEquals(credentials.getCredentials().accessToken, "newAccessToken")
        verify(accessTokenRetrieverService).getOAuth2AccessToken()
    }

    @Test
    void testReplaceAuthentication() {
        def apiClient = new ApiClient(mock(CloseableHttpClient), mock(CacheManager), mock(HttpClientConfiguration))
        def accessTokenRetrieverService = new AccessTokenRetrieverServiceImpl(mock(ClientConfiguration), apiClient)
        def credentials = new OAuth2ClientCredentials(accessTokenRetrieverService)
        apiClient.replaceAuthentication("oauth2", credentials)
        apiClient.setAccessToken("accessToken")
        assertEquals(credentials.getAccessToken(), "accessToken")
    }

    @Test
    void testReplaceAuthentication2WrongName() {
        def apiClient = new ApiClient(mock(CloseableHttpClient), mock(CacheManager), mock(HttpClientConfiguration))
        def credentials = new OAuth2ClientCredentials(mock(AccessTokenRetrieverServiceImpl))
        def exception = expectThrows(RuntimeException.class) {
            apiClient.replaceAuthentication("oauth", credentials)
        }
        assertTrue(exception.message.equals("oauth authentication not configured!"))
    }

    @Test
    void testReplaceAuthenticationWrongType() {
        def apiClient = new ApiClient(mock(CloseableHttpClient), mock(CacheManager), mock(HttpClientConfiguration))
        def exception = expectThrows(RuntimeException.class) {
            apiClient.replaceAuthentication("oauth2", new ApiKeyAuth("", ""))
        }
        assertTrue(exception.message.contains("ApiKeyAuth cannot replace authentication oauth2"))
    }

}

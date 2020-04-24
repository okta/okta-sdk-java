/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.http

import com.okta.commons.http.HttpHeaders
import com.okta.commons.http.Request
import com.okta.commons.http.authc.RequestAuthenticator
import com.okta.sdk.impl.Util
import com.okta.sdk.impl.http.authc.OAuth2RequestAuthenticator
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverService
import com.okta.sdk.impl.oauth2.OAuth2AccessToken
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials
import com.okta.sdk.impl.oauth2.OAuth2TokenRetrieverException

import org.testng.annotations.Test

import java.security.InvalidKeyException

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

/**
 * Test for {@link com.okta.sdk.impl.http.authc.OAuth2RequestAuthenticator} class
 *
 * @since 1.6.0
 */
class OAuth2RequestAuthenticatorTest {

    @Test
    void testInstantiationWithNullClientCredentials() {
        Util.expect(IllegalArgumentException) {
            new OAuth2RequestAuthenticator(null)
        }
    }

    @Test
    void testAuthenticateRequestWithUnexpiredInitialToken() {
        def initialAccessTokenStr = "initial-token-12345"

        def request = mock(Request)

        def clientCredentials = mock(OAuth2ClientCredentials)
        def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
        def initialAccessTokenObj = mock(OAuth2AccessToken)

        def httpHeaders = mock(HttpHeaders)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(false)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)

        when(request.getHeaders()).thenReturn(httpHeaders)

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(initialAccessTokenObj, times(1)).hasExpired()
        verify(request.getHeaders(), times(1))
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + initialAccessTokenStr)
    }

    @Test
    void testAuthenticateRequestWithExpiredInitialToken() {
        def initialAccessTokenStr = "initial-token-12345"
        def refreshedAccessTokenStr = "refreshed-token-12345"

        def request = mock(Request)

        def clientCredentials = mock(OAuth2ClientCredentials)
        def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
        def initialAccessTokenObj = mock(OAuth2AccessToken)
        def refreshedAccessTokenObj = mock(OAuth2AccessToken)

        def httpHeaders = mock(HttpHeaders)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(true)

        when(refreshedAccessTokenObj.getAccessToken()).thenReturn(refreshedAccessTokenStr)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)
        when(accessTokenRetrievalService.getOAuth2AccessToken()).thenReturn(refreshedAccessTokenObj)

        when(request.getHeaders()).thenReturn(httpHeaders)

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(initialAccessTokenObj, times(2)).hasExpired() // double locking
        verify(accessTokenRetrievalService, times(1)).getOAuth2AccessToken()
        verify(clientCredentials, times(1)).setCredentials(refreshedAccessTokenObj)
        verify(request.getHeaders(), times(1))
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + refreshedAccessTokenStr)
    }

    @Test(expectedExceptions = OAuth2TokenRetrieverException)
    void testRefreshTokenFetchException() {
        def initialAccessTokenStr = "initial-token-12345"

        def request = mock(Request)

        def clientCredentials = mock(OAuth2ClientCredentials)
        def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
        def initialAccessTokenObj = mock(OAuth2AccessToken)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(true)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)
        when(accessTokenRetrievalService.getOAuth2AccessToken())
            .thenThrow(new OAuth2TokenRetrieverException("Failed to renew expired OAuth2 access token"))

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(initialAccessTokenObj, times(2)).hasExpired() // double locking
        verify(clientCredentials, never()).setCredentials(mock(OAuth2AccessToken))
        verify(request.getHeaders(), never())
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + any(String.class))
    }

    @Test(expectedExceptions = OAuth2TokenRetrieverException)
    void testRefreshTokenFetchInvalidKeyException() {
        def initialAccessTokenStr = "initial-token-12345"

        def request = mock(Request)

        def clientCredentials = mock(OAuth2ClientCredentials)
        def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
        def initialAccessTokenObj = mock(OAuth2AccessToken)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(true)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)
        when(accessTokenRetrievalService.getOAuth2AccessToken())
            .thenThrow(new InvalidKeyException("Failed to renew expired OAuth2 access token"))

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(initialAccessTokenObj, times(2)).hasExpired() // double locking
        verify(clientCredentials, never()).setCredentials(mock(OAuth2AccessToken))
        verify(request.getHeaders(), never())
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + any(String.class))
    }

    @Test
    void testRefreshTokenFetchWithTokenReuse() {
        def initialAccessTokenStr = "initial-token-12345"
        def refreshedAccessTokenStr = "refreshed-token-12345"

        def request = mock(Request)

        def clientCredentials = mock(OAuth2ClientCredentials)
        def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
        def initialAccessTokenObj = mock(OAuth2AccessToken)
        def refreshedAccessTokenObj = mock(OAuth2AccessToken)

        def httpHeaders = mock(HttpHeaders)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(true)

        when(refreshedAccessTokenObj.getAccessToken()).thenReturn(refreshedAccessTokenStr)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)
        when(accessTokenRetrievalService.getOAuth2AccessToken()).thenReturn(refreshedAccessTokenObj)

        when(request.getHeaders()).thenReturn(httpHeaders)

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(initialAccessTokenObj, times(2)).hasExpired() // double locking
        verify(accessTokenRetrievalService, times(1)).getOAuth2AccessToken()
        verify(clientCredentials, times(1)).setCredentials(refreshedAccessTokenObj)
        verify(request.getHeaders(), times(1))
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + refreshedAccessTokenStr)

        // reset mocks
        reset(request, clientCredentials, accessTokenRetrievalService, initialAccessTokenObj, refreshedAccessTokenObj, httpHeaders)

        // reuse the refreshed token which we got above (do not expire it)
        when(request.getHeaders()).thenReturn(httpHeaders)
        when(clientCredentials.getCredentials()).thenReturn(refreshedAccessTokenObj)
        when(refreshedAccessTokenObj.getAccessToken()).thenReturn(refreshedAccessTokenStr)
        when(refreshedAccessTokenObj.hasExpired()).thenReturn(false)

        new OAuth2RequestAuthenticator(clientCredentials).authenticate(request)

        verify(clientCredentials, times(1)).getCredentials()
        verify(refreshedAccessTokenObj, times(1)).hasExpired()
        verify(accessTokenRetrievalService, never()).getOAuth2AccessToken()
        verify(clientCredentials, never()).setCredentials(refreshedAccessTokenObj)
        verify(request.getHeaders(), times(1))
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + refreshedAccessTokenStr)
    }

}
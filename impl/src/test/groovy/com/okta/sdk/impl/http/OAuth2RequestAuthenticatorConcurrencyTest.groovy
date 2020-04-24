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

import com.okta.sdk.impl.http.authc.OAuth2RequestAuthenticator
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverService
import com.okta.sdk.impl.oauth2.OAuth2AccessToken
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials

import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.mockito.Mockito.when
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times

/**
 * Concurreny test for {@link com.okta.sdk.impl.http.authc.OAuth2RequestAuthenticator} class
 *
 * @since 1.6.0
 */
class OAuth2RequestAuthenticatorConcurrencyTest {

    OAuth2RequestAuthenticator oAuth2RequestAuthenticator

    private static final int PPP = 1000

    def initialAccessTokenStr = "initial-token-12345"
    def refreshedAccessTokenStr = "refreshed-token-12345"

    def request = mock(Request)
    def clientCredentials = mock(OAuth2ClientCredentials)
    def accessTokenRetrievalService = mock(AccessTokenRetrieverService)
    def initialAccessTokenObj = mock(OAuth2AccessToken)
    def refreshedAccessTokenObj = mock(OAuth2AccessToken)
    def httpHeaders = mock(HttpHeaders)

    @BeforeTest
    void initialize() {
        oAuth2RequestAuthenticator = new OAuth2RequestAuthenticator(clientCredentials)

        when(initialAccessTokenObj.getAccessToken()).thenReturn(initialAccessTokenStr)
        when(initialAccessTokenObj.hasExpired()).thenReturn(true)

        when(refreshedAccessTokenObj.getAccessToken()).thenReturn(refreshedAccessTokenStr)

        when(clientCredentials.getCredentials()).thenReturn(initialAccessTokenObj)
        when(clientCredentials.getAccessTokenRetrieverService()).thenReturn(accessTokenRetrievalService)
        when(accessTokenRetrievalService.getOAuth2AccessToken()).thenReturn(refreshedAccessTokenObj)

        when(request.getHeaders()).thenReturn(httpHeaders)
    }

    @Test(threadPoolSize = 5, invocationCount = 100)
    void testAuthenticateRequestWithExpiredInitialToken() {
        oAuth2RequestAuthenticator.authenticate(request)
    }

    @AfterTest
    void verifyMocks() {
        verify(clientCredentials, times(100)).getCredentials()
        verify(initialAccessTokenObj, times(200)).hasExpired() // double locking
        verify(accessTokenRetrievalService, times(100)).getOAuth2AccessToken()
        verify(clientCredentials, times(100)).setCredentials(refreshedAccessTokenObj)
        verify(request.getHeaders(), times(100))
            .set(RequestAuthenticator.AUTHORIZATION_HEADER, "Bearer " + refreshedAccessTokenStr)
    }
    
}
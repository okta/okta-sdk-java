/*
 * Copyright 2022-Present Okta, Inc.
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
package com.okta.sdk.impl.resource

import com.okta.commons.http.HttpHeaders
import com.okta.commons.http.HttpMethod
import com.okta.commons.http.MediaType
import com.okta.commons.http.Request
import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.Response
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.ds.DefaultDataStore
import com.okta.sdk.impl.resource.application.DefaultApplication
import com.okta.sdk.impl.resource.application.DefaultProvisioningConnection
import com.okta.sdk.impl.resource.application.DefaultProvisioningConnectionProfile
import com.okta.sdk.impl.resource.application.DefaultProvisioningConnectionRequest
import com.okta.sdk.impl.util.StringInputStream
import com.okta.sdk.resource.application.ProvisioningConnection
import com.okta.sdk.resource.application.ProvisioningConnectionAuthScheme
import com.okta.sdk.resource.application.ProvisioningConnectionProfile
import com.okta.sdk.resource.application.ProvisioningConnectionStatus
import org.mockito.ArgumentCaptor
import org.testng.annotations.Test

import java.util.stream.Collectors

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Unit tests of
 * @see ProvisioningConnection
 * @see ProvisioningConnectionProfile
 */
class ProvisioningConnectionTest {

    @Test
    void testGetDefaultProvisioningConnectionForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream('{"authScheme": "UNKNOWN","status": "UNKNOWN"}'))
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")

        def provisioningConnection = new DefaultProvisioningConnection(defaultDataStore)
            .getDefaultProvisioningConnectionForApplication(application.getId())

        def request = requestCapture.getValue()
        assertThat request.getMethod(), is(HttpMethod.GET)
        assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/connections/default")
        assertThat provisioningConnection, notNullValue()
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.UNKNOWN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.UNKNOWN)
    }

    @Test
    void testSetDefaultProvisioningConnectionForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream('{"authScheme": "TOKEN","status": "ENABLED"}'))
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")
        def profile = new DefaultProvisioningConnectionProfile(defaultDataStore)
            .setAuthScheme(ProvisioningConnectionAuthScheme.TOKEN)
            .setToken("foo")
        def provisioningRequest = new DefaultProvisioningConnectionRequest(defaultDataStore).setProfile(profile)

        def provisioningConnection = profile
            .setDefaultProvisioningConnectionForApplication(application.getId(), provisioningRequest, true)

        def request = requestCapture.getValue()
        def requestBody = new BufferedReader(new InputStreamReader(request.getBody())).lines().collect(Collectors.joining())
        assertThat request.getMethod(), is(HttpMethod.POST)
        assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/connections/default")
        assertThat requestBody, is('{"profile":{"authScheme":"TOKEN","token":"foo"}}')
        assertThat provisioningConnection, notNullValue()
        assertThat provisioningConnection.getAuthScheme(), is(ProvisioningConnectionAuthScheme.TOKEN)
        assertThat provisioningConnection.getStatus(), is(ProvisioningConnectionStatus.ENABLED)
    }


    @Test
    void testActivateDefaultProvisioningConnectionForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(false)
        when(response.getHeaders()).thenReturn(headers)
        when(response.getHttpStatus()).thenReturn(HttpURLConnection.HTTP_NO_CONTENT)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")

        new DefaultProvisioningConnection(defaultDataStore).activateDefaultProvisioningConnectionForApplication(application.getId())

        assertThat requestCapture.getValue().getResourceUrl().getPath(), is("/api/v1/apps/app-id/connections/default/lifecycle/activate")
    }

    @Test
    void testDeactivateDefaultProvisioningConnectionForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(false)
        when(response.getHeaders()).thenReturn(headers)
        when(response.getHttpStatus()).thenReturn(HttpURLConnection.HTTP_NO_CONTENT)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")

        new DefaultProvisioningConnection(defaultDataStore).deactivateDefaultProvisioningConnectionForApplication(application.getId())

        assertThat requestCapture.getValue().getResourceUrl().getPath(), is("/api/v1/apps/app-id/connections/default/lifecycle/deactivate")
    }
}

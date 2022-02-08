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
import com.okta.sdk.impl.resource.application.DefaultApplicationFeature
import com.okta.sdk.impl.resource.application.DefaultCapabilitiesObject
import com.okta.sdk.impl.resource.application.DefaultOrg2OrgApplication
import com.okta.sdk.impl.util.StringInputStream
import com.okta.sdk.resource.common.EnabledStatus
import org.hamcrest.MatcherAssert
import org.mockito.ArgumentCaptor
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
/**
 * Unit tests for {@code /api/v1/apps}.
 * @since 2.10.0
 */
class ApplicationFeatureTest {

    @Test
    void testListFeaturesForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        def responseBody = '[' +
            '  {' +
            '    "name": "USER_PROVISIONING",' +
            '    "status": "ENABLED",' +
            '    "description": "User provisioning settings from Okta to a downstream application",' +
            '    "capabilities": {' +
            '      "create":{"lifecycleCreate":{"status":"DISABLED"}},' +
            '      "update":{"profile":{"status":"DISABLED"},"lifecycleDeactivate":{"status":"DISABLED"},"password":{"status":"DISABLED","seed":"RANDOM","change":"KEEP_EXISTING"}}' +
            '    }' +
            '  }' +
            ']'
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")

        def applicationFeatureList = new DefaultApplicationFeature(defaultDataStore)
            .listFeaturesForApplication(application.getId())

        def request = requestCapture.getValue()
        assertThat request.getMethod(), is(HttpMethod.GET)
        MatcherAssert.assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/features")
        assertThat applicationFeatureList, notNullValue()
        def features = applicationFeatureList.collect()
        assertThat features.size(), is(1)
        assertThat features.get(0).getName(), is("USER_PROVISIONING")
        assertThat features.get(0).getStatus(), is(EnabledStatus.ENABLED)
    }

    @Test
    void testGetFeatureForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        def responseBody = '{' +
            '  "name": "USER_PROVISIONING",' +
            '  "status": "ENABLED",' +
            '  "description": "User provisioning settings from Okta to a downstream application",' +
            '  "capabilities": {' +
            '    "create":{"lifecycleCreate":{"status":"DISABLED"}},' +
            '    "update":{"profile":{"status":"DISABLED"},"lifecycleDeactivate":{"status":"DISABLED"},"password":{"status":"DISABLED","seed":"RANDOM","change":"KEEP_EXISTING"}}' +
            '  }' +
            '}'
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")
        def applicationFeature = application.getFeatureForApplication("USER_PROVISIONING")

        def request = requestCapture.getValue()
        assertThat request.getMethod(), is(HttpMethod.GET)
        MatcherAssert.assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/features/USER_PROVISIONING")
        assertThat applicationFeature, notNullValue()
        assertThat applicationFeature.getName(), is("USER_PROVISIONING")
        assertThat applicationFeature.getStatus(), is(EnabledStatus.ENABLED)
    }

    @Test
    void testUpdateFeatureForApplication() {
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        def responseBody = '{' +
            '  "name": "USER_PROVISIONING",' +
            '  "status": "DISABLED",' +
            '  "description": "User provisioning settings from Okta to a downstream application",' +
            '  "capabilities": {' +
            '    "create":{"lifecycleCreate":{"status":"DISABLED"}},' +
            '    "update":{"profile":{"status":"DISABLED"},"lifecycleDeactivate":{"status":"DISABLED"},"password":{"status":"DISABLED","seed":"RANDOM","change":"KEEP_EXISTING"}}' +
            '  }' +
            '}'
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultApplication(defaultDataStore)
        application.put("id", "app-id")
        def applicationFeature = application.updateFeatureForApplication("USER_PROVISIONING", new DefaultCapabilitiesObject(defaultDataStore))

        def request = requestCapture.getValue()
        assertThat request.getMethod(), is(HttpMethod.PUT)
        MatcherAssert.assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/features/USER_PROVISIONING")
        assertThat applicationFeature, notNullValue()
        assertThat applicationFeature.getName(), is("USER_PROVISIONING")
        assertThat applicationFeature.getStatus(), is(EnabledStatus.DISABLED)
    }

    @Test
    void testUploadApplicationLogo(){
        def requestCapture = ArgumentCaptor.forClass(Request)
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.getHttpStatus()).thenReturn(HttpURLConnection.HTTP_CREATED)
        when(response.hasBody()).thenReturn(false)
        when(response.getHeaders()).thenReturn(headers)

        def application = new DefaultOrg2OrgApplication(defaultDataStore)
        application.put("id", "app-id")

        new DefaultApplication(defaultDataStore).uploadApplicationLogo(application.getId(), new File("src/test/resources/okta_logo_favicon.png"))

        def request = requestCapture.getValue()
        assertThat request.getMethod(), is(HttpMethod.POST)
        MatcherAssert.assertThat request.getResourceUrl().getPath(), is("/api/v1/apps/app-id/logo")
    }
}

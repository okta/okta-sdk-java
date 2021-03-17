/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.ds

import com.okta.commons.http.HttpHeaders
import com.okta.commons.http.MediaType
import com.okta.commons.http.Request
import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.Response
import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.http.HttpHeadersHolder
import com.okta.sdk.impl.resource.TestResource
import com.okta.sdk.impl.util.StringInputStream
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.testng.annotations.AfterTest
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.testng.Assert.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * @since 0.5.0
 */
class DefaultDataStoreTest {

    @AfterTest
    void cleanUpHeaders() {
        HttpHeadersHolder.clear();
    }

    @Test
    void testApiKeyResolverReturnsCorrectApiKey() {
        def requestExecutor = mock(RequestExecutor)
        def clientCredentialsResolver = mock(ClientCredentialsResolver)
        def clientCredentials = mock(ClientCredentials)

        when(clientCredentialsResolver.getClientCredentials()).thenReturn(clientCredentials)

        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", clientCredentialsResolver)

        assertEquals(defaultDataStore.getClientCredentials(), clientCredentials)
        assertNotEquals(defaultDataStore.getClientCredentials(), clientCredentialsResolver)
    }

    @Test
    void testSetHrefOnGetResource() {

        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def responseBody = '{"name": "jcoder"}'
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(Mockito.any())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        def testResource = defaultDataStore.getResource(resourceHref, TestResource)
        assertThat testResource.getResourceHref(), is(resourceHref)
    }

    @Test
    void testSettingRequestParamsForGetResource() {

        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def responseBody = '{"name": "jcoder"}'
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def requestCapture = ArgumentCaptor.forClass(Request)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        defaultDataStore.getResource(resourceHref, TestResource, [query1: "query-value1", query2: "query-value2"],
                                                                 [header1: ["header-value1"], header2: ["header2-valueA", "header2-valueB"]])

        def request = requestCapture.getValue()
        assertThat request.headers, allOf(hasEntry("header1", ["header-value1"]),
                                          hasEntry("header2", ["header2-valueA", "header2-valueB"]))
        assertThat request.queryString, allOf(hasEntry("query1", "query-value1"),
                                              hasEntry("query2", "query-value2"))
    }

    @Test
    void testSettingRequestParamsForDeleteResource() {

        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def requestCapture = ArgumentCaptor.forClass(Request)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(false)
        when(response.getHeaders()).thenReturn(new HttpHeaders())

        defaultDataStore.delete(resourceHref, [query1: "query-value1", query2: "query-value2"],
                                              [header1: ["header-value1"], header2: ["header2-valueA", "header2-valueB"]])

        def request = requestCapture.getValue()
        assertThat request.headers, allOf(hasEntry("header1", ["header-value1"]),
                                          hasEntry("header2", ["header2-valueA", "header2-valueB"]))
        assertThat request.queryString, allOf(hasEntry("query1", "query-value1"),
                                              hasEntry("query2", "query-value2"))
    }

    @Test
    void testSettingRequestParamsForPostResource() {

        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def responseBody = '{"name": "jcoder"}'
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def requestCapture = ArgumentCaptor.forClass(Request)
        def resource = new TestResource(null, null)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        defaultDataStore.save(resourceHref, resource, null, [query1: "query-value1", query2: "query-value2"],
                                                                          [header1: ["header-value1"], header2: ["header2-valueA", "header2-valueB"]])

        def request = requestCapture.getValue()
        assertThat request.headers, allOf(hasEntry("header1", ["header-value1"]),
                                          hasEntry("header2", ["header2-valueA", "header2-valueB"]))
        assertThat request.queryString, allOf(hasEntry("query1", "query-value1"),
                                              hasEntry("query2", "query-value2"))
    }

    @Test
    void testOverrideUserAgentTest() {
        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def responseBody = '{"name": "jcoder"}'
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def requestCapture = ArgumentCaptor.forClass(Request)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        defaultDataStore.getResource(resourceHref, TestResource, null,
                                                                 ["User-Agent": ["test-value"]])

        def request = requestCapture.getValue()
        assertThat request.headers, hasEntry("User-Agent", ["test-value"])
        assertThat request.headers.get("X-Okta-User-Agent-Extended"), hasSize(1)
        assertThat request.headers.getFirst("X-Okta-User-Agent-Extended"), allOf(
                                        containsString("okta-sdk-java/"),
                                        containsString("java/${System.getProperty("java.version")}"),
                                        containsString("${System.getProperty("os.name")}/${System.getProperty("os.version")}"))
    }

    @Test
    void testAdditionalHeaders() {
        def resourceHref = "https://api.okta.com/v1/testResource"
        def requestExecutor = mock(RequestExecutor)
        def apiKeyResolver = mock(ClientCredentialsResolver)
        def response = mock(Response)
        def responseBody = '{"name": "jcoder"}'
        def defaultDataStore = new DefaultDataStore(requestExecutor, "https://api.okta.com/v1", apiKeyResolver)
        def requestCapture = ArgumentCaptor.forClass(Request)
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        when(requestExecutor.executeRequest(requestCapture.capture())).thenReturn(response)
        when(response.hasBody()).thenReturn(true)
        when(response.getBody()).thenReturn(new StringInputStream(responseBody))
        when(response.getHeaders()).thenReturn(headers)

        HttpHeadersHolder.set([
            "threaded-header1": ["value1", "value2"],
            "threaded-header2": ["single-value"]
        ])

        defaultDataStore.getResource(resourceHref, TestResource, null,
            ["Foo-Bar": ["test-value"]])

        def request = requestCapture.getValue()
        // from the request arg
        assertThat request.headers, hasEntry("Foo-Bar", ["test-value"])
        // thread local
        assertThat request.headers, hasEntry("threaded-header1", ["value1", "value2"])
        assertThat request.headers, hasEntry("threaded-header2", ["single-value"])
        // default headers
        assertThat request.headers.get("User-Agent"), hasSize(1)
        assertThat request.headers.getFirst("User-Agent"), allOf(
            containsString("okta-sdk-java/"),
            containsString("java/${System.getProperty("java.version")}"),
            containsString("${System.getProperty("os.name")}/${System.getProperty("os.version")}"))
    }
}
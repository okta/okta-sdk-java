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
package com.okta.sdk.impl.http.httpclient

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Proxy
import com.okta.sdk.http.HttpMethod
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.http.HttpHeaders
import com.okta.sdk.impl.http.QueryString
import com.okta.sdk.impl.http.Request
import com.okta.sdk.impl.http.Response
import com.okta.sdk.impl.http.authc.DefaultRequestAuthenticatorFactory
import com.okta.sdk.impl.http.authc.RequestAuthenticator
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.okta.sdk.impl.http.support.DefaultResponse
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.message.BasicHeader
import org.testng.Assert
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

import static org.mockito.Mockito.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNull

class HttpClientRequestExecutorTest {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/124
    void testToSdkResponseWithNullContentString() {

        def clientCredentials = mock(ClientCredentials)

        HttpResponse httpResponse = mock(HttpResponse)
        StatusLine statusLine = mock(StatusLine)
        HttpEntity entity = mock(HttpEntity)
        InputStream entityContent = mock(InputStream)

        when(clientCredentials.getCredentials()).thenReturn("token-foo")
        when(httpResponse.getStatusLine()).thenReturn(statusLine)
        when(statusLine.getStatusCode()).thenReturn(200)
        when(httpResponse.getAllHeaders()).thenReturn(null)
        when(httpResponse.getEntity()).thenReturn(entity)
        when(entity.getContentEncoding()).thenReturn(null)
        when(entity.getContent()).thenReturn(entityContent)
        when(entity.getContentLength()).thenReturn(-1l)

        def e = new HttpClientRequestExecutor(clientCredentials, null, AuthenticationScheme.SSWS, null, 20000) {
            @Override
            protected byte[] toBytes(HttpEntity he) throws IOException {
                return null
            }
        }

        def sdkResponse = e.toSdkResponse(httpResponse)

        assertNull sdkResponse.body
        assertThat sdkResponse.httpStatus, is(200)
    }

    @Test
    void testClientConfigurationConstructor() {

        def clientCredentials = mock(ClientCredentials)

        def clientConfig = new ClientConfiguration()
        clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(clientCredentials))
        clientConfig.setProxy(new Proxy("example.com", 3333, "proxy-username", "proxy-password"))
        clientConfig.setAuthenticationScheme(AuthenticationScheme.NONE)
        clientConfig.setRequestAuthenticatorFactory(new DefaultRequestAuthenticatorFactory())
        clientConfig.setConnectionTimeout(1111)
        clientConfig.setRetryMaxElapsed(2)
        clientConfig.setRetryMaxAttempts(3)

        def requestExecutor = new HttpClientRequestExecutor(clientConfig)

        assertThat requestExecutor.maxElapsedMillis, is(2000)
        assertThat requestExecutor.numRetries, is(3)
    }

    @Test
    void testExecuteRequest() {

        def content = "my-content"
        def request = mockRequest()
        def requestAuthenticator = mock(RequestAuthenticator)
        def httpRequest = mock(HttpRequestBase)
        def httpClientRequestFactory = mock(HttpClientRequestFactory)
        def httpClient = mock(HttpClient)
        def httpResponse = mockHttpResponse(content)

        when(httpClientRequestFactory.createHttpClientRequest(request, null)).thenReturn(httpRequest)
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse)

        def requestExecutor = createRequestExecutor(requestAuthenticator)
        requestExecutor.httpClientRequestFactory = httpClientRequestFactory
        requestExecutor.httpClient = httpClient

        def response = requestExecutor.executeRequest(request)
        def responseBody = response.body.text

        assertThat responseBody, is(content)
        assertThat response.httpStatus, is(200)
        assertThat response.isClientError(), is(false)
        assertThat response.isError(), is(false)
        assertThat response.isServerError(), is(false)

        verify(requestAuthenticator).authenticate(request)
    }

    @Test
    void testExecuteRequestWith429() {

        def content1 = "fake-429-content"
        def content2 = "some-content"
        def request = mockRequest()
        def httpRequest1 = mock(HttpRequestBase)
        def httpRequest2 = mock(HttpRequestBase)
        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
        long currentTime = System.currentTimeMillis()
        long resetTime = ((currentTime / 1000L) + 5L) as Long // current time plus 5 seconds
        def dateHeaderValue1 = dateFormat.format(new Date(currentTime))
        def dateHeaderValue2 = dateFormat.format(new Date(currentTime + 10 * 1000))

        Header[] headers1 = [
                new BasicHeader("X-Rate-Limit-Reset", resetTime.toString()),
                new BasicHeader("Date", dateHeaderValue1),
                new BasicHeader("X-Okta-Request-Id", "a-request-id")]

        Header[] headers2 = [
                new BasicHeader("Date", dateHeaderValue2),
                new BasicHeader("X-Okta-Request-Id", "another-request-id")]

        def httpResponse1 = mockHttpResponse(content1, 429, headers1)
        def httpResponse2 = mockHttpResponse(content2, 200, headers2)
        def requestAuthenticator = mock(RequestAuthenticator)
        def httpClientRequestFactory = mock(HttpClientRequestFactory)
        def httpClient = mock(HttpClient)

        when(httpClientRequestFactory.createHttpClientRequest(request, null)).thenReturn(httpRequest1)
                                                                                          .thenReturn(httpRequest2)
        when(httpClient.execute(httpRequest1)).thenReturn(httpResponse1)
        when(httpClient.execute(httpRequest2)).thenReturn(httpResponse2)

        def requestExecutor = createRequestExecutor(requestAuthenticator)
        requestExecutor.httpClientRequestFactory = httpClientRequestFactory
        requestExecutor.httpClient = httpClient

        Response response = null
        def totalTime = time { response = requestExecutor.executeRequest(request) }

        def responseBody = response.body.text

        assertThat responseBody, is(content2)
        assertThat response.httpStatus, is(200)
        assertThat response.isClientError(), is(false)
        assertThat response.isError(), is(false)
        assertThat response.isServerError(), is(false)

        def headers = request.getHeaders()
        verify(headers).add("X-Okta-Retry-For", "a-request-id")
        verify(headers).add("X-Okta-Retry-Count", "2")
        verify(requestAuthenticator, times(2)).authenticate(request)

        // plus 500ms for slow CPUs
        assertThat totalTime as Integer, is(both(
                                                greaterThanOrEqualTo(6000))
                                            .and(
                                                lessThan(6500)))
    }

    @Test
    void test429DelayTooLong() {

        def content = "error-content"
        def request = mockRequest()
        def requestAuthenticator = mock(RequestAuthenticator)
        def httpRequest = mock(HttpRequestBase)
        def httpClientRequestFactory = mock(HttpClientRequestFactory)
        def httpClient = mock(HttpClient)
        def dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
        long currentTime = System.currentTimeMillis()
        long resetTime = ((currentTime / 1000L) + 20L) as Long // current time plus 20 seconds
        def dateHeaderValue = dateFormat.format(new Date(currentTime))
        Header[] headers = [
                new BasicHeader("X-Rate-Limit-Reset", resetTime.toString()),
                new BasicHeader("Date", dateHeaderValue),
                new BasicHeader("X-Okta-Request-Id", "a-request-id")]
        def httpResponse = mockHttpResponse(content, 429, headers)

        when(httpClientRequestFactory.createHttpClientRequest(request, null)).thenReturn(httpRequest)
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse)

        def requestExecutor = createRequestExecutor(requestAuthenticator)
        requestExecutor.httpClientRequestFactory = httpClientRequestFactory
        requestExecutor.httpClient = httpClient

        def totalTime = time {
            def response = requestExecutor.executeRequest(request)
            assertThat response.httpStatus, is(429)
        }

        // should be almost instant, but we are just making sure we didn't sleep for 20 sec
        assertThat totalTime, lessThan(1000L)
    }

    private static long time(Closure closure) {
        def startTime = System.currentTimeMillis()
        closure.call()
        return (System.currentTimeMillis() - startTime)
    }

    static <T extends Throwable> T expect(Class<T> catchMe, Closure closure) {
        try {
            closure.call()
            Assert.fail("Expected ${catchMe.getName()} to be thrown.")
        } catch(e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
            return e
        }
    }

    private HttpClientRequestExecutor createRequestExecutor(RequestAuthenticator requestAuthenticator = mock(RequestAuthenticator), int maxElapsed = 15, int maxAttempts = 4) {

        return new HttpClientRequestExecutor(createClientConfiguration(requestAuthenticator, maxElapsed, maxAttempts))
    }

    private ClientConfiguration createClientConfiguration(RequestAuthenticator requestAuthenticator = mock(RequestAuthenticator), int maxElapsed = 15, int maxAttempts = 4) {

        def clientCredentials = mock(ClientCredentials)
        def clientConfig = mock(ClientConfiguration)
        def requestAuthFactory = mock(RequestAuthenticatorFactory)

        when(clientConfig.getClientCredentialsResolver()).thenReturn(new DefaultClientCredentialsResolver(clientCredentials))
        when(clientConfig.getRequestAuthenticatorFactory()).thenReturn(requestAuthFactory)
        when(clientConfig.getAuthenticationScheme()).thenReturn(AuthenticationScheme.SSWS)
        when(clientConfig.getConnectionTimeout()).thenReturn(1111)
        when(clientConfig.getRetryMaxElapsed()).thenReturn(maxElapsed)
        when(clientConfig.getRetryMaxAttempts()).thenReturn(maxAttempts)

        when(requestAuthFactory.create(AuthenticationScheme.SSWS, clientCredentials)).thenReturn(requestAuthenticator)

        return clientConfig
    }

    private Request mockRequest(String uri = "https://example.com/a-resource",
                                HttpMethod method = HttpMethod.GET,
                                HttpHeaders headers = mock(HttpHeaders),
                                QueryString queryString = new QueryString()) {

        def request = mock(Request)

        when(request.getQueryString()).thenReturn(queryString)
        when(request.getHeaders()).thenReturn(headers)
        when(request.getResourceUrl()).thenReturn(new URI(uri))
        when(request.getMethod()).thenReturn(method)

        return request
    }

    private Response stubResponse(String content, int statusCode = 200, Header[] headers = null) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8)
        return new DefaultResponse(statusCode, null, new ByteArrayInputStream(bytes), bytes.length)
    }

    private HttpResponse mockHttpResponse(String content, int statusCode = 200, Header[] headers = null) {

        def httpResponse = mock(HttpResponse)
        def statusLine = mock(StatusLine)
        def entity = mock(HttpEntity)
        def entityContent = new ByteArrayInputStream(content.getBytes())

        when(httpResponse.getStatusLine()).thenReturn(statusLine)
        when(httpResponse.getEntity()).thenReturn(entity)
        when(httpResponse.getAllHeaders()).thenReturn(headers)
        if (headers != null) {
            headers.each {
                when(httpResponse.getFirstHeader(it.name)).thenReturn(it)
                Header[] singleHeader = [it]
                when(httpResponse.getHeaders(it.name)).thenReturn(singleHeader)
            }
        }

        when(statusLine.getStatusCode()).thenReturn(statusCode)

        when(entity.getContentEncoding()).thenReturn(null)
        when(entity.getContent()).thenReturn(entityContent)
        when(entity.getContentLength()).thenReturn(content.length().longValue())

        return httpResponse
    }
}
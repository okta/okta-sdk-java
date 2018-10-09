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
package com.okta.sdk.impl.http.okhttp

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Proxy
import com.okta.sdk.http.HttpMethod
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.http.HttpHeaders
import com.okta.sdk.impl.http.QueryString
import com.okta.sdk.impl.http.Request
import com.okta.sdk.impl.http.authc.DefaultRequestAuthenticatorFactory
import com.okta.sdk.impl.http.authc.DisabledAuthenticator
import com.okta.sdk.impl.http.authc.RequestAuthenticator
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNull
import static org.mockito.Mockito.*

class OkHttpRequestExecutorTest {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/124
    void testToSdkResponseWithNullContentString() {

        def clientConfiguration = new ClientConfiguration()
        clientConfiguration.setAuthenticationScheme(AuthenticationScheme.SSWS)
        clientConfiguration.setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("test-api-token")))
        def requestExecutor = new OkHttpRequestExecutor(clientConfiguration)

        def okRequest = new okhttp3.Request.Builder()
                            .url("https://test.example.com")
                            .build()
        def okResponse = new okhttp3.Response.Builder()
                            .body(null)
                            .code(200)
                            .message("OK")
                            .request(okRequest)
                            .protocol(Protocol.HTTP_1_1)
                            .build()

        def response = requestExecutor.toSdkResponse(okResponse)

        assertNull response.body
        assertThat response.httpStatus, is(200)
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

        def requestExecutor = new OkHttpRequestExecutor(clientConfig)
        assertThat requestExecutor.client.proxy().type(), is(java.net.Proxy.Type.HTTP)
        assertThat requestExecutor.client.proxy().address().getHostName(), is("example.com")
        assertThat requestExecutor.client.proxy().address().getPort(), is(3333)
        assertThat requestExecutor.requestAuthenticator, instanceOf(DisabledAuthenticator)
        assertThat requestExecutor.client.connectTimeoutMillis(), is(1111 * 1000)
    }

    @Test
    void testExecuteRequest() {

        def content = "my-content"
        def okResponse = stubResponse(content)
        def headers = mock(HttpHeaders)
        def query = mock(QueryString)
        def request = mock(Request)
        def requestAuthenticator = mock(RequestAuthenticator)

        when(request.getHeaders()).thenReturn(headers)
        when(request.getResourceUrl()).thenReturn(new URI("https://testExecuteRequest.example.com"))
        when(request.getQueryString()).thenReturn(query)
        when(request.getMethod()).thenReturn(HttpMethod.GET)

        def interceptor = new Interceptor() {
            @Override
            okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                return okResponse
            }
        }

        def requestExecutor = createRequestExecutor(new OkHttpClient.Builder()
                                                                    .addInterceptor(interceptor)
                                                                    .build(),
                                                    requestAuthenticator)

        def response = requestExecutor.executeRequest(request)
        def responseBody = response.body.text

        assertThat responseBody, is(content)
        assertThat response.httpStatus, is(200)
        assertThat response.isClientError(), is(false)
        assertThat response.isError(), is(false)
        assertThat response.isServerError(), is(false)

        verify(requestAuthenticator).authenticate(request)
    }

    def stubResponse(String body = "some-content", int code = 200, String url = "https://test.example.com") {

        def responseBody = ResponseBody.create(MediaType.parse("text/plain"), body)

        def okRequest = new okhttp3.Request.Builder()
                            .url(url)
                            .build()

        def okResponse = new okhttp3.Response.Builder()
                            .body(responseBody)
                            .code(code)
                            .message("OK")
                            .request(okRequest)
                            .protocol(Protocol.HTTP_1_1)
                            .build()

        return okResponse
    }

    def createRequestExecutor(OkHttpClient client = null, RequestAuthenticator requestAuthenticator = null) {



        def clientConfiguration = new ClientConfiguration()
        clientConfiguration.setAuthenticationScheme(AuthenticationScheme.SSWS)
        def clientCredentials = new TokenClientCredentials("test-api-token")
        def clientCredentialsResolver = new DefaultClientCredentialsResolver(clientCredentials)
        clientConfiguration.setClientCredentialsResolver(clientCredentialsResolver)

        if (requestAuthenticator != null) {
            def requestAuthenticatorFactory = mock(RequestAuthenticatorFactory)
            when(requestAuthenticatorFactory.create(AuthenticationScheme.SSWS, clientCredentials)).thenReturn(requestAuthenticator)
            clientConfiguration.setRequestAuthenticatorFactory(requestAuthenticatorFactory)
        }

        return client == null ? new OkHttpRequestExecutor(clientConfiguration)
                              : new OkHttpRequestExecutor(clientConfiguration, client)
    }
}
/*
 * Copyright 2014 Stormpath, Inc.
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

import com.okta.sdk.api.ApiKey
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.impl.authc.credentials.ApiKeyCredentials
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNull

class HttpClientRequestExecutorTest {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/124
    void testToSdkResponseWithNullContentString() {

        def apiKeyCredentials = mock(ApiKeyCredentials)

        HttpResponse httpResponse = mock(HttpResponse)
        StatusLine statusLine = mock(StatusLine)
        HttpEntity entity = mock(HttpEntity)
        InputStream entityContent = mock(InputStream)
        ApiKey apiKey = mock(ApiKey)

        when(apiKeyCredentials.getApiKey()).thenReturn(apiKey)
        when(httpResponse.getStatusLine()).thenReturn(statusLine)
        when(statusLine.getStatusCode()).thenReturn(200)
        when(httpResponse.getAllHeaders()).thenReturn(null)
        when(httpResponse.getEntity()).thenReturn(entity)
        when(entity.getContentEncoding()).thenReturn(null)
        when(entity.getContent()).thenReturn(entityContent)
        when(entity.getContentLength()).thenReturn(-1l)

        def e = new HttpClientRequestExecutor(apiKeyCredentials, null, AuthenticationScheme.SSWS, null, 20000) {
            @Override
            protected byte[] toBytes(HttpEntity he) throws IOException {
                return null
            }
        }

        def sdkResponse = e.toSdkResponse(httpResponse)

        assertNull sdkResponse.body
        assertThat sdkResponse.httpStatus, is(200)

    }
}

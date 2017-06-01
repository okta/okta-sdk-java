/*
 * Copyright 2015 Stormpath, Inc.
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

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertNull

class HttpClientRequestExecutorTest {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/124
    void testToSdkResponseWithNullContentString() {

        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        HttpResponse httpResponse = createStrictMock(HttpResponse)
        StatusLine statusLine = createStrictMock(StatusLine)
        HttpEntity entity = createStrictMock(HttpEntity)
        InputStream entityContent = createStrictMock(InputStream)
        ApiKey apiKey = createMock(ApiKey)

        expect(apiKeyCredentials.getApiKey()).andReturn(apiKey)
        expect(httpResponse.getStatusLine()).andStubReturn(statusLine)
        expect(statusLine.getStatusCode()).andStubReturn(200)
        expect(httpResponse.getAllHeaders()).andStubReturn(null)
        expect(httpResponse.getEntity()).andStubReturn(entity)
        expect(entity.getContentEncoding())andStubReturn(null)
        expect(entity.getContent()).andStubReturn(entityContent)
        expect(entity.getContentLength()).andStubReturn(-1)

        replay apiKeyCredentials, httpResponse, statusLine, entity, entityContent, apiKey

        def e = new HttpClientRequestExecutor(apiKeyCredentials, null, AuthenticationScheme.SSWS, null, 20000) {
            @Override
            protected byte[] toBytes(HttpEntity he) throws IOException {
                return null
            }
        }

        def sdkResponse = e.toSdkResponse(httpResponse)

        assertNull sdkResponse.body

        verify apiKeyCredentials, httpResponse, statusLine, entity, entityContent, apiKey

    }
}

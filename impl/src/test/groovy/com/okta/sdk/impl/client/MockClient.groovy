/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.client

import com.okta.commons.http.DefaultResponse
import com.okta.commons.http.MediaType
import com.okta.commons.http.Request
import com.okta.commons.http.RequestExecutor
import com.okta.commons.http.Response
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import org.mockito.Mockito

import java.nio.charset.StandardCharsets

import static org.mockito.Mockito.when

class MockClient extends DefaultClient {

    RequestExecutor mockRequestExecutor

    static ClientConfiguration defaultClientConfig = new ClientConfiguration() {{
        setBaseUrlResolver(new DefaultBaseUrlResolver("https://okta.example.com"))
        setClientCredentialsResolver(new DefaultClientCredentialsResolver(new TokenClientCredentials("api_client_key")))
    }}


    MockClient(ClientConfiguration clientConfig = defaultClientConfig) {
        super(clientConfig, new DisabledCacheManager())
    }

    @Override
    protected RequestExecutor createRequestExecutor(ClientConfiguration clientConfiguration) {
        mockRequestExecutor = Mockito.mock(RequestExecutor)
        return mockRequestExecutor
    }

    MockClient withMockResponse(Request request, Response response) {
        when(mockRequestExecutor.executeRequest(request)).thenReturn(response)
        return this
    }

    MockClient withMockResponse(Request request, String jsonFilePath) {

        String content = this.getClass().getResource(jsonFilePath).text
        return withMockResponse(request, new DefaultResponse(200,
                                             MediaType.APPLICATION_JSON,
                                             new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                                             content.size()))
    }
}
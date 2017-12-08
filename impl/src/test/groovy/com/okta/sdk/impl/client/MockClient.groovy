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

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Proxy
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver
import com.okta.sdk.impl.cache.DisabledCacheManager
import com.okta.sdk.impl.config.ClientConfiguration
import com.okta.sdk.impl.http.MediaType
import com.okta.sdk.impl.http.Request
import com.okta.sdk.impl.http.RequestExecutor
import com.okta.sdk.impl.http.Response
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.okta.sdk.impl.http.support.DefaultResponse
import com.okta.sdk.impl.util.DefaultBaseUrlResolver
import org.mockito.Mockito

import java.nio.charset.StandardCharsets

import static org.mockito.Mockito.when

class MockClient extends DefaultClient {

    RequestExecutor mockRequestExecutor

    MockClient(ClientConfiguration clientConfig = new ClientConfiguration()) {

        super(new DefaultClientCredentialsResolver(new TokenClientCredentials("api_client_key")),
                new DefaultBaseUrlResolver("https://okta.example.com"),
                null,
                new DisabledCacheManager(),
                clientConfig.getAuthenticationScheme(),
                clientConfig.getRequestAuthenticatorFactory(),
                clientConfig.getConnectionTimeout())
    }

    @Override
    protected RequestExecutor createRequestExecutor(ClientCredentials clientCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {
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
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
package com.okta.sdk.impl.client

import com.okta.sdk.cache.CacheManager
import com.okta.sdk.client.AuthenticationScheme
import com.okta.sdk.client.Proxy
import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.sdk.impl.http.Request
import com.okta.sdk.impl.http.RequestExecutor
import com.okta.sdk.impl.http.Response
import com.okta.sdk.impl.http.RestException
import com.okta.sdk.impl.util.BaseUrlResolver

import java.util.concurrent.atomic.AtomicInteger

/**
 * A AbstractClient subclass that will count every request delegated to the RequestExecutor, useful for integration
 * tests that need to assert traffic profiles to the server.
 *
 * This implementation DOES NOT count HTTP redirects since the default RequestExecutor will execute them transparently.
 *
 * @since 0.5.0
 */
public class RequestCountingClient extends DefaultClient {

    private AtomicInteger count = new AtomicInteger();

    public RequestCountingClient(ClientCredentialsResolver apiKeyResolver, BaseUrlResolver baseUrlResolver, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme, int connectionTimeout) {
        super(apiKeyResolver, baseUrlResolver, proxy, cacheManager, authenticationScheme, null, connectionTimeout)
    }

    @Override
    protected InternalDataStore createDataStore(final RequestExecutor requestExecutor, BaseUrlResolver baseUrlResolver, ClientCredentialsResolver apiKeyResolver, CacheManager cacheManager) {

        RequestExecutor countingExecutor = new RequestExecutor() {
            @Override
            Response executeRequest(Request request) throws RestException {
                count.incrementAndGet();
                return requestExecutor.executeRequest(request);
            }
        };

        return super.createDataStore(countingExecutor, baseUrlResolver, clientCredentials, apiKeyResolver, cacheManager)
    }

    /**
     * Returns the number of NON-REDIRECT requests sent to the server since the client was instantiated or since the
     * last time {@link #resetRequestCount()} was invoked.
     *
     * @return the number of NON-REDIRECT requests sent to the server since the client was instantiated or since the
     * last time {@link #resetRequestCount()} was invoked.
     */
    public int getRequestCount() {
        return count.get();
    }

    /**
     * Sets the request count to zero.
     */
    public void resetRequestCount() {
        count.set(0);
    }
}

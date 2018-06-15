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
package com.okta.sdk.impl.client;

import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Proxy;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.util.BaseUrlResolver;

/**
 * Abstract client implementation, use BaseClient instead. Kept only for backwards compatibility, this class will
 * be removed in future versions.
 *
 * @deprecated see {@link BaseClient}
 * @see <a href="http://www.okta.com/docs/quickstart/connect">Communicating with Okta: Get your API Key</a>
 * @since 0.5.0
 */
@Deprecated
public abstract class AbstractClient extends BaseClient implements Client {

    /**
     * Instantiates a new Client instance that will communicate with the Okta REST API.  See the class-level
     * JavaDoc for a usage example.
     *
     * @param clientCredentialsResolver       Okta API Key resolver
     * @param baseUrlResolver      Okta base URL resolver
     * @param proxy                the HTTP proxy to be used when communicating with the Okta API server (can be
     *                             null)
     * @param cacheManager         the {@link com.okta.sdk.cache.CacheManager} that should be used to cache
     *                             Okta REST resources (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Okta API
     *                             server (can be null)
     * @param requestAuthenticatorFactory factory used to handle creating autentication requests
     * @param connectionTimeout    connection timeout in seconds
     */
    public AbstractClient(ClientCredentialsResolver clientCredentialsResolver, BaseUrlResolver baseUrlResolver, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {
        super(clientCredentialsResolver, baseUrlResolver, proxy, cacheManager, authenticationScheme, requestAuthenticatorFactory, connectionTimeout);
    }

    public AbstractClient(ClientConfiguration clientConfiguration, CacheManager cacheManager) {
        super(clientConfiguration, cacheManager);
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public InternalDataStore getDataStore() {
        return super.getDataStore();
    }
}
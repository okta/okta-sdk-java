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
import com.okta.sdk.client.Client;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.ds.InternalDataStore;

/**
 * Abstract client implementation, use BaseClient instead. Kept only for backwards compatibility, this class will
 * be removed in future versions.
 *
 * @deprecated see {@link BaseClient}
 * @see <a href="https://developer.okta.com/docs/guides/create-an-api-token/overview">Communicating with Okta: Get your API Token</a>
 * @since 0.5.0
 */
@Deprecated
public abstract class AbstractClient extends BaseClient implements Client {


    public AbstractClient(ClientConfiguration clientConfiguration, CacheManager cacheManager) {
        super(clientConfiguration, cacheManager);
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public InternalDataStore getDataStore() {
        return super.getDataStore();
    }
}

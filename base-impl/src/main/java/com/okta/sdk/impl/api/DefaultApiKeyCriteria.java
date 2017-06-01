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
package com.okta.sdk.impl.api;

import com.okta.sdk.api.ApiKeyCriteria;
import com.okta.sdk.api.ApiKeyOptions;
import com.okta.sdk.impl.query.DefaultCriteria;

/**
 * @since 1.0.RC
 */
public class DefaultApiKeyCriteria extends DefaultCriteria<ApiKeyCriteria, ApiKeyOptions> implements ApiKeyCriteria {

    public static final int DEFAULT_ENCRYPTION_SIZE = 128;

    public static final int DEFAULT_ENCRYPTION_ITERATIONS = 1024;

    public DefaultApiKeyCriteria() {
        super(null);
    }

    @Override
    public ApiKeyCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public ApiKeyCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }
}

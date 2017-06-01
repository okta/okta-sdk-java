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
package com.okta.sdk.api;


import com.okta.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with {@link ApiKey} resources. This class can also be used for working
 * with {@link com.okta.sdk.api.ApiKey} resources. For example:
 * <pre>
 * <b>ApiKeys.builder()</b>
 *     .setFileLocation(path)
 *     .build();
 * </pre>
 *
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class ApiKeys {

    /**
     * Returns a new {@link ApiKeyBuilder} instance, used to construct {@link ApiKey} instances to authenticate the calls to Okta.
     *
     * @return a new {@link ApiKeyBuilder} instance, used to construct {@link ApiKey} instances to authenticate the calls to Okta.
     */
    public static ApiKeyBuilder builder() {
        return (ApiKeyBuilder) Classes.newInstance("com.okta.sdk.impl.api.ClientApiKeyBuilder");
    }
}

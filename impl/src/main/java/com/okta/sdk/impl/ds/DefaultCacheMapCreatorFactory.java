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
package com.okta.sdk.impl.ds;

import com.okta.sdk.impl.http.QueryString;
import com.okta.sdk.resource.Resource;

import java.util.Map;

/**
 * @since 0.5.0
 * @see CacheMapCreator
 */
public class DefaultCacheMapCreatorFactory implements CacheMapCreatorFactory {

    /**
     * <p>
     *     Creates a {@link CacheMapCreator} based on the provided arguments.
     * </p>
     * @param clazz the class used to determined the type of {@link CacheMapCreator} to create.
     * @param data the data map used to create the {@link CacheMapCreator}.
     * @param queryString the query string used to create the {@link CacheMapCreator}.
     *
     * @return a new {@link CacheMapCreator} instance.
     */
    @Override
    public CacheMapCreator create(Class<? extends Resource> clazz, Map<String, ?> data, QueryString queryString) {

        return new DefaultCacheMapCreator(data);
    }
}

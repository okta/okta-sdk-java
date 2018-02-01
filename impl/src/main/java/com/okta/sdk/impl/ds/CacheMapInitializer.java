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
 * <p>
 *     Interface used to define the method to initialize a cache map. Used in the {@link DefaultDataStore}.
 * </p>
 * @since 0.5.0
 */
public interface CacheMapInitializer {

    /**
     * Used to initialize a cache map based on the provided class, data and query string.
     *
     * @param clazz the resource class that will be used to identify the type of resource.
     * @param data the data to be cached that should be analyzed to initialize the map.
     * @param queryString the query string to be analyzed to initialize the map.
     * @return the initialized map.
     */
    Map<String, Object> initialize(Class<? extends Resource> clazz, Map<String, ?> data, QueryString queryString);
}

/*
 * Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.ds.cache;

import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;

/**
 * Defines the strategy used for reading/adding/updating/removing objects from the cache based on a
 * {@link ResourceDataRequest} and {@link ResourceDataResult}.
 *
 * @since 1.0
 */
public interface ResourceCacheStrategy {

    /**
     * Cache the {@code result} data that was returned based on the {@code request}.
     * @param request the data request
     * @param result the result to be cached
     */
    void cache(ResourceDataRequest request, ResourceDataResult result);

    /**
     * Attempts to retrieve data from the cache based on the {@code request}. If the object is NOT found in the cache,
     * {@code null} is returned.
     * @param request the source request used to query the cache.
     * @return A result containing data from cache, or null
     */
    ResourceDataResult readFromCache(ResourceDataRequest request);
}
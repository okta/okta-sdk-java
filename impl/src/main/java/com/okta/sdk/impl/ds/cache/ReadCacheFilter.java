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
package com.okta.sdk.impl.ds.cache;

import com.okta.sdk.impl.ds.Filter;
import com.okta.sdk.impl.ds.FilterChain;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;


public class ReadCacheFilter implements Filter {

    private final ResourceCacheStrategy cacheStrategy;

    public ReadCacheFilter(ResourceCacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = cacheStrategy.readFromCache(request);
        if (result != null) {
            return result;
        }

        //cache miss - let the chain continue:
        return chain.filter(request);
    }
}

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
package com.okta.sdk.impl.ds.cache;

import com.okta.sdk.api.ApiKey;
import com.okta.sdk.api.ApiKeyList;
import com.okta.sdk.ds.DataStore;
import com.okta.sdk.impl.ds.DefaultResourceDataResult;
import com.okta.sdk.impl.ds.FilterChain;
import com.okta.sdk.impl.ds.ResourceAction;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.http.QueryString;
import com.okta.sdk.impl.resource.CollectionProperties;
import com.okta.sdk.impl.util.BaseUrlResolver;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.okta.sdk.impl.api.ApiKeyParameter.ID;

public class ReadCacheFilter extends AbstractCacheFilter {

    private static final Logger cacheLog = LoggerFactory.getLogger(DataStore.class.getName() + "-cache");

    private BaseUrlResolver baseUrlResolver;

    /**
     * @since 1.2.0
     */
    public ReadCacheFilter(BaseUrlResolver baseUrlResolver, CacheResolver cacheResolver, boolean collectionCachingEnabled) {
        super(cacheResolver, collectionCachingEnabled);
        Assert.notNull(baseUrlResolver, "baseUrlResolver cannot be null.");
        this.baseUrlResolver = baseUrlResolver;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        if (isCacheRetrievalEnabled(request)) {
            ResourceDataResult result = getCachedResourceData(request);
            if (result != null) {
                return result;
            }
        }

        //cache miss - let the chain continue:
        return chain.filter(request);
    }

    private ResourceDataResult getCachedResourceData(ResourceDataRequest request) {

        final CanonicalUri uri = request.getUri();
        final String href = uri.getAbsolutePath();
        final QueryString query = uri.getQuery();
        final Class<? extends Resource> clazz = request.getResourceClass();

        Map<String, ?> data = null;

        if (isApiKeyCollectionQuery(request)) {

            String cacheHref = baseUrlResolver.getBaseUrl() + "/apiKeys/" + query.get(ID.getName());
            Class<ApiKey> cacheClass = ApiKey.class;

            Map<String, ?> apiKeyData = getCachedValue(cacheHref, cacheClass);

            if (!Collections.isEmpty(apiKeyData)) {
                data = new CollectionProperties.Builder().setHref(href)
                                                         .setItemsMap(apiKeyData)
                                                         .build();
            }
        } else {
            //Prevent an expanded request to obtain a non-expanded resource from the cache
            String cacheKey = getCacheKey(request);
            if (! (request.getUri().hasQuery() && request.getUri().getQuery().containsKey("expand") ^ (cacheKey != null && cacheKey.contains("expand=")))) {
                data = getCachedValue(cacheKey, clazz);
            }
        }

        if (Collections.isEmpty(data)) {
            return null;
        }

        if (cacheLog.isTraceEnabled()) {
            cacheLog.trace("Executing cache request: action: '{}', uri: {}", request.getAction(), request.getUri().getAbsolutePath());
        }

        return new DefaultResourceDataResult(request.getAction(), uri, clazz, coerce(data));
    }

    private int getValue(QueryString query, String propName, int defaultValue) {
        return query.containsKey(propName) ? Integer.valueOf(query.get(propName)) : defaultValue;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> coerce(Map<String, ?> data) {
        return (Map<String, Object>) data;
    }

    private boolean isApiKeyCollectionQuery(ResourceDataRequest request) {
        return ApiKeyList.class.isAssignableFrom(request.getResourceClass()) &&
               request.getUri().hasQuery() && request.getUri().getQuery().containsKey(ID.getName());
    }

    private boolean isCacheRetrievalEnabled(ResourceDataRequest request) {

        Class<? extends Resource> clazz = request.getResourceClass();

        return

            //create, update and delete all should bypass cache reads:
            request.getAction() == ResourceAction.READ &&

            //Collection caching is EXPERIMENTAL so it is off by default
            //we do cache ApiKeyList. This is a fix for #216
            (!CollectionResource.class.isAssignableFrom(clazz) || ApiKeyList.class.isAssignableFrom(clazz) ||
                    (CollectionResource.class.isAssignableFrom(clazz) && isCollectionCachingEnabled()));

    }
}

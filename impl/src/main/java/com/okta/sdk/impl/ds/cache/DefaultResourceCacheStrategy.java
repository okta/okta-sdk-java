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

import com.okta.sdk.cache.Cache;
import com.okta.sdk.impl.ds.CacheMapInitializer;
import com.okta.sdk.impl.ds.DefaultCacheMapInitializer;
import com.okta.sdk.impl.ds.DefaultResourceDataResult;
import com.okta.sdk.impl.ds.ResourceAction;
import com.okta.sdk.impl.ds.ResourceDataRequest;
import com.okta.sdk.impl.ds.ResourceDataResult;
import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.resource.ResourceHrefResolver;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.resource.CollectionResource;
import com.okta.sdk.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This default implementation of {@link ResourceCacheStrategy} manages reading and writing to/from a {@link Cache}. This
 * implementation also invalidates an object's cache when {@code DELETE} and other mutating operations are called.
 * <p>
 * Key Points:
 * <ul>
 *     <li>Collection Resources are NOT cached</li>
 *     <li>Any individual Resource with a {@code _link.self.href} is cached</li>
 *     <li>The {@link ResourceHrefResolver} will be consulted if a Resource does NOT have a @{code _link.self.href} property</li>
 *     <li>A call to the {@code cache} method with a {@code DELETE} action will also remove that object from the cache</li>
 *     <li>Any @{code CREATE} or {@code UPDATE} call to the {@code cache} method, that has a _parent_ set in the {@link ResourceDataRequest} will also remove the parent from the cache</li>
 * </ul>
 *
 * @since 1.0
 */
public class DefaultResourceCacheStrategy implements ResourceCacheStrategy {

    private final Logger logger = LoggerFactory.getLogger(DefaultResourceCacheStrategy.class);

    private final ResourceHrefResolver hrefResolver;
    private final CacheResolver cacheResolver;
    private final CacheMapInitializer cacheMapInitializer = new DefaultCacheMapInitializer();

    public DefaultResourceCacheStrategy(ResourceHrefResolver hrefResolver, CacheResolver cacheResolver) {
        this.hrefResolver = hrefResolver;
        this.cacheResolver = cacheResolver;
    }

    @Override
    public void cache(ResourceDataRequest request, ResourceDataResult result) {

        if (request.getAction() == ResourceAction.DELETE) {
            String key = getCacheKey(request);
            uncache(key, request.getResourceClass());
        } else if (isCacheable(result)) {
            cache(result.getResourceClass(), result.getData(), result.getUri());
        } else {
            if (request.getParentUri() != null) {
                logger.debug("Removing parent cache  '{}'", request.getUri().getAbsolutePath());
                String key = getCacheKey(request.getParentUri().getAbsolutePath());
                uncache(key, request.getParentResourceClass());
            } else {
                logger.debug("Cannot cache action: '{}', href: '{}', class: '{}'", result.getAction(), result.getUri().getAbsolutePath(), result.getResourceClass());
            }
        }
    }

    @Override
    public ResourceDataResult readFromCache(ResourceDataRequest request) {

        if (!isCacheRetrievalEnabled(request)) {
            return null;
        }

        final CanonicalUri uri = request.getUri();
        final Class<? extends Resource> clazz = request.getResourceClass();

        Map<String, ?> data = null;

        // Prevent an expanded request to obtain a non-expanded resource from the cache
        String cacheKey = getCacheKey(request);
        if (! (request.getUri().hasQuery() && request.getUri().getQuery().containsKey("expand") ^ (cacheKey != null && cacheKey.contains("expand=")))) {
            data = getCachedValue(cacheKey, clazz);
        }

        // return if no data
        if (Collections.isEmpty(data)) {
            return null;
        }

        return new DefaultResourceDataResult(request.getAction(), uri, clazz, coerce(data));
    }

    @SuppressWarnings("unchecked")
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data, CanonicalUri uri) {

        Assert.notEmpty(data, "Resource data cannot be null or empty.");
        String href = hrefResolver.resolveHref(data, clazz);

        if (isDirectlyCacheable(clazz, data)) {
            Assert.notNull(href, "Resource data must contain an 'href' attribute.");
            Assert.isTrue(data.size() > 0, "Resource data must be materialized to be cached " +
                    "(need more at least one attribute).");
        }

        // create a map to reflect the resource's canonical representation - this is what will be cached:
        Map<String, Object> cacheValue = cacheMapInitializer.initialize(clazz, data, uri.getQuery());

        data.entrySet().forEach(entry ->
            cacheValue.put(entry.getKey(), entry.getValue())
        );

        if (isDirectlyCacheable(clazz, cacheValue)) {
            Cache cache = getCache(clazz);
            String cacheKey = getCacheKey(href);
            Object previousCacheValue = cache.put(cacheKey, cacheValue);
            logger.debug("Caching object for key '{}', class: '{}', updated {}", cacheKey, clazz, previousCacheValue != null);
        }
    }

    @SuppressWarnings("unchecked")
    private void uncache(String cacheKey, Class<? extends Resource> resourceType) {
        Assert.hasText(cacheKey, "cacheKey cannot be null or empty.");
        Assert.notNull(resourceType, "resourceType cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(resourceType);
        cache.remove(cacheKey);
        logger.debug("Removing cache for key '{}', class: '{}'", cacheKey, resourceType);
    }

    private boolean isCacheable(ResourceDataResult result) {
        if (Collections.isEmpty(result.getData())) {
            return false;
        }

        Class<? extends Resource> clazz = result.getResourceClass();

        // @since 0.5.0
        boolean materialized = isMaterialized(result.getData(), clazz);

        if (!materialized) {
            logger.debug("Class: {}, is not cacheable.", clazz.getSimpleName());
        }

        return materialized;
    }

    /**
     * Quick fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>.
     *
     */
    private boolean isDirectlyCacheable(Class<? extends Resource> clazz, Map<String, ?> data) {
        return isMaterialized(data, clazz) &&
               // do NOT cache collections
               !CollectionResource.class.isAssignableFrom(clazz);
    }

    private boolean isCacheRetrievalEnabled(ResourceDataRequest request) {
        return
            // create, update and delete all should bypass cache reads:
            request.getAction() == ResourceAction.READ &&

            // Collection caching is disabled
            !CollectionResource.class.isAssignableFrom(request.getResourceClass());
    }

    /**
     * Returns {@code true} if the specified data map represents a materialized resource data set, {@code false}
     * otherwise.
     *
     * @param props the data properties to test
     * @return {@code true} if the specified data map represents a materialized resource data set, {@code false}
     * otherwise.
     */
    private <R extends Resource> boolean isMaterialized(Map<String, ?> props, Class<R> clazz) {

        // this check to see if the data map has an self href in it (which is used for the caching key)
        return hrefResolver.resolveHref(props, clazz) != null;
    }

    private <T> Cache<String, Map<String, ?>> getCache(Class<T> clazz) {
        return this.cacheResolver.getCache(clazz);
    }

    private Map<String, ?> getCachedValue(String href, Class<? extends Resource> clazz) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Class argument cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(clazz);

        Map<String, ?> value = cache.get(href);
        if (value != null) {
            logger.debug("Cache hit for key      '{}', class: '{}'", href, clazz);
        }
        return value;
    }

    private String getCacheKey(ResourceDataRequest request) {
        return getCacheKey(request.getUri().getAbsolutePath());
    }

    private String getCacheKey(String href) {
        return href;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> coerce(Map<String, ?> data) {
        return (Map<String, Object>) data;
    }
}
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
package com.okta.sdk.cache;

/**
 * A CacheManager provides and maintains the lifecycle of {@link Cache Cache} instances.
 * <p>
 * This interface provides an abstraction (wrapper) API on top of an underlying
 * cache framework's main Manager component (e.g. JCache, Ehcache, Hazelcast, JCS, OSCache, JBossCache, TerraCotta,
 * Coherence, GigaSpaces, etc, etc), allowing a Okta SDK user to configure any cache mechanism they choose.
 *
 * @since 0.5.0
 */
public interface CacheManager {

    /**
     * Acquires the cache with the specified {@code name}.  If a cache does not yet exist with that name, a new one
     * will be created with that name and returned.
     *
     * @param name the name of the cache to acquire.
     * @param <K> type of cache key
     * @param <V> type of cache value
     * @return the Cache with the given name
     */
    <K, V> Cache<K, V> getCache(String name);
}

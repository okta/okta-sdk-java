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
package com.okta.sdk.impl.cache

import com.okta.sdk.cache.Cache
import java.time.Duration
import groovy.json.JsonSlurper
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.5.0
 */
class DefaultCacheManagerTest {

    private DefaultCacheManager mgr;

    @BeforeTest
    void setUp() {
        this.mgr = new DefaultCacheManager();
    }

    @Test
    void testGetCache() {
        def cache = mgr.getCache('foo');
        assertNotNull cache
        assertEquals 'foo', cache.name
        assertSame cache, mgr.getCache('foo')
    }

    @Test
    void testGetCachePutIfAbsent() {
        mgr = new DefaultCacheManager() {
            @Override
            protected Cache createCache(String name) {
                Cache first = super.createCache(name)

                //simulate something else putting a cache with the same name in
                //we should see this cache and not the first one created:
                Cache second = super.createCache(name);
                second.put('key', 'value');
                caches.put(name, second);

                return first;
            }
        }

        def cache = mgr.getCache('foo')
        assertNotNull cache
        assertEquals 'foo', cache.name
        assertEquals 'value', cache.get('key')
    }

    @Test
    void testDefaultTtl() {
        mgr = new DefaultCacheManager()
        Duration ttl = Duration.ofSeconds(30)
        mgr.setDefaultTimeToLive(ttl)

        def cache = mgr.getCache('foo')
        assertEquals cache.timeToLive, Duration.ofSeconds(30)
    }

    @Test
    void testDefaultTtlSeconds() {
        mgr.setDefaultTimeToLiveSeconds(30)
        assertEquals mgr.defaultTimeToLive, Duration.ofSeconds(30)
    }

    @Test
    void testDefaultTti() {
        Duration tti = Duration.ofSeconds(20)
        mgr.setDefaultTimeToIdle(tti)

        def cache = mgr.getCache('foo')
        assertEquals cache.timeToIdle.seconds, tti.getSeconds()
    }

    @Test
    void testDefaultTtiSeconds() {
        mgr.setDefaultTimeToIdleSeconds(30)
        assertEquals Duration.ofSeconds(30), mgr.defaultTimeToIdle
    }

    @Test
    void testToString() {

        mgr = new DefaultCacheManager()

        mgr.getCache('foo')
        mgr.getCache('bar')

        def string = mgr.toString()
        def json = new JsonSlurper().parseText(string)

        assertEquals json.cacheCount, 2
        assertEquals json.caches.size(), 2
        assertEquals json.defaultTimeToLive, 'indefinite'
        assertEquals json.defaultTimeToIdle, 'indefinite'

        def names = ['foo', 'bar']

        def caches = [:]
        for( def cache : json.caches) {
            caches.put(cache.name, cache)
        }

        for(def name : names) {
            def cache = caches.get(name)
            assertEquals cache.name, name
            assertEquals cache.size, 0
            assertEquals cache.accessCount, 0
            assertEquals cache.hitCount, 0
            assertEquals cache.missCount, 0
            assertEquals cache.hitRatio, 0.0
        }
    }

}

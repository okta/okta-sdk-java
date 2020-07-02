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

import java.time.Duration
import groovy.json.JsonSlurper
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.5.0
 */
class DefaultCacheTest {

    @Test
    void testDefault() {
        def cache = new DefaultCache('foo')
        assertEquals 'foo', cache.name
    }

    @Test
    void testGetReturningNull() {
        def cache = new DefaultCache('foo');
        assertNull cache.get('key')
    }

    @Test
    void testSetTtl() {
        def cache = new DefaultCache('foo')
        def ttl = Duration.ofMillis(30)
        cache.setTimeToLive(ttl)
        assertSame cache.timeToLive, ttl
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testSetNegativeTtl() {
        def cache = new DefaultCache('foo')
        def ttl = Duration.ofMillis(-30)
        cache.setTimeToLive(ttl)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testSetZeroTtl() {
        def cache = new DefaultCache('foo')
        def ttl = Duration.ofMillis(0)
        cache.setTimeToLive(ttl)
    }

    @Test
    void testSetTti() {
        def cache = new DefaultCache('foo')
        def tti = Duration.ofMillis(30)
        cache.setTimeToIdle(tti)
        assertSame cache.timeToIdle, tti
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testSetNegativeTti() {
        def cache = new DefaultCache('foo')
        def tti = Duration.ofMillis(-30)
        cache.setTimeToIdle(tti)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testSetZeroTti() {
        def cache = new DefaultCache('foo')
        def tti = Duration.ofMillis(0)
        cache.setTimeToIdle(tti)
    }

    @Test
    void testRemove() {
        def cache = new DefaultCache('foo')
        def existing = cache.put('key', 'value')
        assertNull existing

        existing = cache.remove('key')
        assertEquals existing, 'value'

        assertEquals cache.accessCount, 1
        assertEquals cache.hitCount, 1
        assertEquals cache.missCount, 0
        assertEquals cache.hitRatio, 1.0d

        def value = cache.get('key')
        assertNull value

        assertEquals cache.accessCount, 2
        assertEquals cache.hitCount, 1
        assertEquals cache.missCount, 1
        def ratio = cache.hitRatio
        assertEquals ratio, 0.5d

        value = cache.remove('key')
        assertNull value

        assertEquals cache.accessCount, 3
        assertEquals cache.hitCount, 1
        assertEquals cache.missCount, 2
        ratio = cache.hitRatio
        assertEquals ratio, (1d/3d)
    }


    @Test
    void testClear() {
        def cache = new DefaultCache('foo')
        cache.put('key', 'value')
        assertEquals cache.size(), 1
        cache.clear()
        assertEquals cache.size(), 0
    }

    @Test
    void testToString() {
        def cache = new DefaultCache('foo')
        def json = new JsonSlurper().parseText(cache.toString())

        assertEquals json.name, 'foo'
        assertEquals json.size, 0
        assertEquals json.accessCount, 0
        assertEquals json.hitCount, 0
        assertEquals json.missCount, 0
        assertEquals json.hitRatio, 0.0

        cache.put('key', 'value')
        def value = cache.get('key')
        assertEquals value, 'value'

        json = new JsonSlurper().parseText(cache.toString())

        assertEquals json.name, 'foo'
        assertEquals json.size, 1
        assertEquals json.accessCount, 1
        assertEquals json.hitCount, 1
        assertEquals json.missCount, 0
        assertEquals json.hitRatio, 1.0
    }

    @Test
    void testPutReplacesPreviousValue() {
        def cache = new DefaultCache('foo', [:], null, null);

        def key = 'key'
        def value1 = 'value1'
        def value2 = 'value2'

        assertNull cache.put(key, value1)

        def prev = cache.put(key, value2)

        assertEquals prev, value1
        assertEquals cache.get(key), value2
    }

    @Test
    void testTimeToLive() {

        def cache = new DefaultCache('foo', [:], Duration.ofMillis(10), null)

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals value, found
        assertEquals cache.size(), 1

        Thread.sleep(15)

        found = cache.get(key)

        assertNull found
        assertEquals cache.size(), 0
    }

    @Test
    void testTimeToIdle() {

        def cache = new DefaultCache('foo', [:], null, Duration.ofMillis(50))

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals found, value
        assertEquals cache.size(), 1

        Thread.sleep(5)

        found = cache.get(key)
        assertEquals found, value
        assertEquals cache.size(), 1

        Thread.sleep(300)

        found = cache.get(key)
        assertNull found
        assertEquals cache.size(), 0
    }

    @Test
    void testTimeToLiveAndTimeToIdle() {

        def cache = new DefaultCache('foo', [:], Duration.ofMillis(100), Duration.ofMillis(40))

        def key = 'key'
        def value = 'value'

        def prev = cache.put(key, value)
        assertNull prev

        def found = cache.get(key)
        assertEquals found, value
        assertEquals 1, cache.size()

        //each time we access after sleeping 15 seconds, we should always acquire the value since the last
        //access timestamp is being updated, preventing expunging due to idle.

        Thread.sleep(20)
        found = cache.get(key)
        assertEquals(found, value)
        assertEquals 1, cache.size()

        Thread.sleep(20)
        found = cache.get(key)
        assertEquals(found, value)
        assertEquals 1, cache.size()

        Thread.sleep(20)
        found = cache.get(key)
        assertEquals(found, value)
        assertEquals 1, cache.size()

        //Now we need to ensure that no matter how frequently the value is used (not idle), we still need to remove
        //the value if older than the TTL

        //20 + 20 + 20 = 60.  Add another 50 millis, and we'll be ~ 110 millis, which is older than the TTL of 100 above.
        Thread.sleep(50)

        found = cache.get(key)
        assertNull found
        assertEquals 0, cache.size()
    }
}

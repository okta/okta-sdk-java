/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.ds

import com.okta.sdk.impl.http.QueryString
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC9
 */
class DefaultCacheKeyTest {

    @Test
    void testWithQueryString() {

        def qs = new QueryString([
            "key_one":"value_one",
            "key_two":"value_two"
        ])
        def cacheKey = new DefaultCacheKey("https://mysite.com", qs)

        assertEquals cacheKey.toString(), "https://mysite.com?key_one=value_one&key_two=value_two"
    }

    @Test
    void testWithQueryStringOnURl() {

        // saved as alpha order
        def qs = new QueryString([
            "key_three":"value_three",
            "key_two":"value_two"
        ])
        def cacheKey = new DefaultCacheKey("https://mysite.com?key_one=value_one", qs)

        assertEquals cacheKey.toString(), "https://mysite.com?key_one=value_one&key_three=value_three&key_two=value_two"
    }

    @Test
    void testHashCode() {

        def qs = new QueryString(["key_one":"value_one"])
        def cacheKey = new DefaultCacheKey("https://mysite.com", qs)

        assertEquals cacheKey.hashCode(), "https://mysite.com?key_one=value_one".hashCode()
    }

    @Test
    void testEquals() {

        def qs = new QueryString(["key_one":"value_one"])
        def cacheKey1 = new DefaultCacheKey("https://mysite.com", qs)
        assertTrue cacheKey1.equals(cacheKey1)

        def cacheKey2 = new DefaultCacheKey("https://mysite.com", qs)
        assertTrue cacheKey1.equals(cacheKey2)

        assertFalse cacheKey1.equals("not the right type")
    }
}

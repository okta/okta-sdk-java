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
package com.okta.sdk.impl.http

import com.okta.sdk.http.HttpMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 1.0.RC9
 */
class DefaultHttpRequestTest {

    @Test
    void testGetHeaderNoHeaders() {

        def httpRequest = new DefaultHttpRequest([:], HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Header")
    }

    @Test
    void testGetHeaderNullValue() {

        def headers = ["My-Header":null]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Header")
    }

    @Test
    void testGetHeaderNotFound() {

        def headers = ["My-Header":["My-Value"] as String[]]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertNull httpRequest.getHeader("My-Other-Header")
    }

    @Test
    void testGetHeaderFound() {

        def headers = ["My-Header":["My-Value"] as String[]]
        def httpRequest = new DefaultHttpRequest(headers, HttpMethod.GET, null, null)
        assertEquals httpRequest.getHeader("My-Header"), "My-Value"
    }

    @Test
    void testGetParameterNull() {

        def httpRequest = new DefaultHttpRequest([:], HttpMethod.GET, null, null)
        assertNull httpRequest.getParameter("My-Parameter")
    }
}

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
package com.okta.sdk.impl.authc

import com.okta.sdk.authc.BasicAuthenticationOptions
import com.okta.sdk.authc.UsernamePasswordRequests
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.testng.Assert.*

/**
 * @since 1.0.0
 */
class UsernamePasswordRequestBuilderTest {

    @Test
    void testAllProperties() {
        def options = mock(BasicAuthenticationOptions)
        def authenticationRequest = UsernamePasswordRequests.builder()
                .setUsernameOrEmail("usernameOrEmail")
                .setPassword("myPassword123!")
                .setHost("http://myHost.com")
                .withResponseOptions(options)
                .build()

        assertEquals(authenticationRequest.getPrincipals(), "usernameOrEmail")
        assertEquals(authenticationRequest.password, "myPassword123!".toCharArray())
        assertEquals(authenticationRequest.getHost(), "http://myHost.com")
        assertSame(authenticationRequest.getResponseOptions(), options)
    }

    @Test
    void testNullChecks() {
        try {
            UsernamePasswordRequests.builder().build()
            fail("Should have thrown");
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "usernameOrEmail has not been set. It is a required attribute.")
        }

        try {
            UsernamePasswordRequests.builder().setUsernameOrEmail("someFooUsername").setHost(null)
            fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "host cannot be null.")
        }

        try {
            UsernamePasswordRequests.builder().setUsernameOrEmail("someFooUsername").withResponseOptions(null)
            fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "options cannot be null.")
        }
    }

}

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
package com.okta.sdk.impl.api

import com.okta.sdk.authc.credentials.TokenClientCredentials
import org.testng.annotations.Test

import static org.testng.Assert.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.containsString

/**
 * @since 0.5.0
 */
class TokenClientCredentialsTest {

    @Test
    void testConstructorSecretNull() {
        try {
            new TokenClientCredentials(null);
            fail("Should have thrown due to null secret.")
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage(), containsString("Your Okta API token is missing"))
        }
    }

    @Test
    void testConstructor() {
        def apiKey = new TokenClientCredentials("barSecret");
        org.testng.Assert.assertEquals(apiKey.getCredentials(), "barSecret")
    }

    @Test
    void testToString() {
        def apiKey = new TokenClientCredentials( "barSecret");
        org.testng.Assert.assertEquals(apiKey.toString(), "<TokenClientCredentials>")
    }

    @Test
    void testEquals() {
        def apiKey = new TokenClientCredentials("barSecret");
        def apiKey2 = new TokenClientCredentials("barSecret");
        def apiKey3 = new TokenClientCredentials("nope");

        assertTrue(apiKey.equals(apiKey))
        assertTrue(apiKey.equals(apiKey2))
        assertFalse(apiKey.equals(apiKey3))
        assertFalse(apiKey.equals("anything"))
    }
}

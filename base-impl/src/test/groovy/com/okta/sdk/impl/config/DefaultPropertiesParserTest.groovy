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
package com.okta.sdk.impl.config

import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

class DefaultPropertiesParserTest {
    def parser

    @BeforeTest
    void setup() {
        parser = new DefaultPropertiesParser()
    }

    @Test
    void testKeyValueNoWhitespace() {

        def testStr = "okta.web.verifyEmail.nextUri=/login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)
            assertEquals(result.size(), 1)
            assertEquals(result.get("okta.web.verifyEmail.nextUri"), "/login?status=verified")
        }
    }

    @Test
    void testKeyValueLotsOfWhitespace() {

        def testStr = "okta.web.verifyEmail.nextUri                                                     =                                         /login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)
            assertEquals(result.size(), 1)
            assertEquals(result.get("okta.web.verifyEmail.nextUri"), "/login?status=verified")
        }
    }

    @Test
    void testKeyNoValue() {

        def testStr = "okta.web.verifyEmail.nextUri = "
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 1)
            assertEquals(result.get("okta.web.verifyEmail.nextUri"), null)
        }
    }

    @Test
    void testValueNoKey() {

        def testStr = " = /login?status=verified"
        [testStr, new TestStringResource(testStr)].each {
            try {
                parser.parse(it)
                fail()
            } catch (IllegalArgumentException iae) {
                assertEquals(iae.message, "Line argument must contain a key. None was found.")
            }
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testMultiLine() {

        def testStr =
            "okta.web.verifyEmail.nextUri=/login?status=verified\n" +
            "okta.web.login.nextUri=/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("okta.web.login.nextUri"), "/")
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testComments() {

        def testStr =
            "okta.web.verifyEmail.nextUri=/login?status=verified\n" +
            "# this is a comment\n" +
            "; this is also a comment\n" +
            "okta.web.login.nextUri=/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("okta.web.login.nextUri"), "/")
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testContinuation() {

        def testStr =
                "okta.web.verifyEmail.nextUri = \\\n" +
                    "/login?status=verified\n" +
                "okta.web.login.nextUri = \\\n" +
                    "/"
        [testStr, new TestStringResource(testStr)].each {
            def result = parser.parse(it)

            assertEquals(result.size(), 2)
            assertEquals(result.get("okta.web.login.nextUri"), "/")
        }
    }
}

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
package com.okta.sdk.impl.config

import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0
 */
class YAMLPropertiesSourceTest {

    @Test
    void testGetProperties() {
        def testStr = "okta:\n  web:\n    verifyEmail:\n      nextUri: /register?status=verified"
        def properties = new YAMLPropertiesSource(new TestStringResource(testStr)).properties

        assertEquals properties.get("okta.web.verifyEmail.nextUri"), "/register?status=verified"
    }

    @Test
    void testGetMapProperties() {
        def yamlMap = "a_nested_map:\n" +
                "    key: value\n" +
                "    another_key: Another Value\n" +
                "    another_nested_map:\n" +
                "        hello: hello"
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlMap)).properties

        assertEquals properties.get("a_nested_map.key"), "value"
        assertEquals properties.get("a_nested_map.another_nested_map.hello"), "hello"
    }

    @Test
    void testGetCollectionProperties() {
        def yamlCollection = "a_sequence:\n" +
                "    - Item 1\n" +
                "    - Item 2\n" +
                "    - 0.5 # sequences can contain disparate types.\n" +
                "    - Item 4"
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlCollection)).properties

        assertEquals properties.get("a_sequence"), "Item 1,Item 2,0.5,Item 4"
    }

    @Test
    void testGetFieldOrderWithRawStrings() {
        def yamlCollection = "fieldOrder:\n" +
                "  - username\n" +
                "  - givenName\n" +
                "  - middleName\n" +
                "  - surname\n" +
                "  - email\n" +
                "  - password\n" +
                "  - confirmPassword"
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlCollection)).properties

        assertEquals properties.get("fieldOrder"), "username,givenName,middleName,surname,email,password,confirmPassword"
    }
    
    @Test
    void testGetFieldOrderWithQuotedStrings() {
        def yamlCollection = "fieldOrder:\n" +
                "  - \"username\"\n" +
                "  - \"givenName\"\n" +
                "  - \"middleName\"\n" +
                "  - \"surname\"\n" +
                "  - \"email\"\n" +
                "  - \"password\"\n" +
                "  - \"confirmPassword\""
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlCollection)).properties

        assertEquals properties.get("fieldOrder"), "username,givenName,middleName,surname,email,password,confirmPassword"
    }

    @Test
    void testInvalidProperties() {
        try {
            new YAMLPropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}

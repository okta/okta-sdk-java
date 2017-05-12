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
 * @since 1.0.0
 */
class JSONPropertiesSourceTest {

    @Test
    void testGetProperties() {
        def testStr = "{ \n" +
                "  \"okta\": {\n" +
                "    \"web\": {\n" +
                "      \"verify\": {\n" +
                "        \"uri\": \"/double-check\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        def properties = new JSONPropertiesSource(new TestStringResource(testStr)).properties
        assertEquals properties.get("okta.web.verify.uri"), "/double-check"
    }

    @Test
    void testGetCollectionProperties() {
        def yamlCollection = "{\n" +
                "  \"collection\": [\n" +
                "    {\"name\": \"Item 1\"}, {\"name\": \"Item 2\"}, {\"name\": \"Item 3\"}\n" +
                "  ]\n" +
                "}"
        def properties = new YAMLPropertiesSource(new TestStringResource(yamlCollection)).properties

        assertEquals properties.get("collection"), "{name=Item 1},{name=Item 2},{name=Item 3}"
    }

    @Test
    void testInvalidProperties() {
        try {
            new JSONPropertiesSource(new BadResource()).properties
            fail("should not be here")
        } catch (IllegalArgumentException e) {
            assertTrue e.getMessage().contains("BadResource")
        }
    }
}

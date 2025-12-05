/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.okta.sdk.helper;

import org.testng.annotations.Test;

import java.util.StringJoiner;

import static org.testng.Assert.*;

public class HelperConstantsTest {

    @Test
    public void testQueryStringJoiner() {
        // Test the behavior of the QUERY_STRING_JOINER
        StringJoiner joiner = HelperConstants.QUERY_STRING_JOINER;

        // Add some test values and verify the joined result
        StringJoiner testJoiner = new StringJoiner("&");
        testJoiner.add("param1=value1");
        testJoiner.add("param2=value2");

        // Create a new joiner with the same delimiter to compare behavior
        StringJoiner constantJoiner = HelperConstants.QUERY_STRING_JOINER;
        constantJoiner.add("param1=value1");
        constantJoiner.add("param2=value2");

        assertEquals(constantJoiner.toString(), testJoiner.toString());
        assertEquals(constantJoiner.toString(), "param1=value1&param2=value2");
    }

    @Test
    public void testAuthNames() {
        // Verify the AUTH_NAMES array content
        String[] authNames = HelperConstants.AUTH_NAMES;

        assertEquals(authNames.length, 2, "AUTH_NAMES should contain exactly 2 elements");
        assertEquals(authNames[0], "apiToken", "First auth name should be 'apiToken'");
        assertEquals(authNames[1], "oauth2", "Second auth name should be 'oauth2'");
    }

    @Test
    public void testMediaType() {
        // Verify the MEDIA_TYPE array content
        String[] mediaType = HelperConstants.MEDIA_TYPE;

        assertEquals(mediaType.length, 1, "MEDIA_TYPE should contain exactly 1 element");
        assertEquals(mediaType[0], "application/json",
            "Media type value should match MediaType.APPLICATION_JSON_VALUE");
    }
}

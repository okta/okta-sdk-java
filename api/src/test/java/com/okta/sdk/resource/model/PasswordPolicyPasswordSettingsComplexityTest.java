/*
 * Copyright 2026-Present Okta, Inc.
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
package com.okta.sdk.resource.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * OKTA-1228272: maxConsecutiveCharacters was missing from the SDK's PasswordPolicyPasswordSettingsComplexity
 * model even though the Okta API accepts it. Because McpObjectMapper-style deserialization typically runs
 * with FAIL_ON_UNKNOWN_PROPERTIES=false, a caller-supplied value for this field was silently dropped
 * instead of surfaced, giving a false impression the field was accepted.
 */
public class PasswordPolicyPasswordSettingsComplexityTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize_maxConsecutiveCharacters_isPopulated() throws Exception {
        String json = "{\"minLength\":8,\"minLowerCase\":1,\"maxConsecutiveCharacters\":1}";

        PasswordPolicyPasswordSettingsComplexity complexity =
            objectMapper.readValue(json, PasswordPolicyPasswordSettingsComplexity.class);

        assertEquals(complexity.getMinLength(), Integer.valueOf(8));
        assertEquals(complexity.getMaxConsecutiveCharacters(), Integer.valueOf(1));
    }

    @Test
    public void serialize_maxConsecutiveCharacters_isIncluded() throws Exception {
        PasswordPolicyPasswordSettingsComplexity complexity = new PasswordPolicyPasswordSettingsComplexity()
            .maxConsecutiveCharacters(1);

        String json = objectMapper.writeValueAsString(complexity);

        assertTrue(json.contains("\"maxConsecutiveCharacters\":1"), "expected maxConsecutiveCharacters in output, got: " + json);
    }
}

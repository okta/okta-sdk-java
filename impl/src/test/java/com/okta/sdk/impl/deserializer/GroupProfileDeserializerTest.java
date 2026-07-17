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
package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.okta.sdk.resource.model.GroupProfile;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link GroupProfileDeserializer}.
 *
 * GH-1642: custom group schema attributes (e.g. {@code MaxUsersCount}) were silently dropped instead of
 * being surfaced via {@link GroupProfile#getAdditionalProperties()}, because the deserializer's
 * {@code default} case discarded any property it didn't explicitly recognize.
 */
public class GroupProfileDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(GroupProfile.class, new GroupProfileDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testDeserialize_withCustomAttribute_isSurfacedInAdditionalProperties() throws JsonProcessingException {
        String json = "{\"name\":\"Engineering\",\"description\":\"Eng team\",\"MaxUsersCount\":42}";

        GroupProfile profile = objectMapper.readValue(json, GroupProfile.class);

        assertEquals(profile.getName(), "Engineering");
        assertEquals(profile.getDescription(), "Eng team");
        assertNotNull(profile.getAdditionalProperties());
        assertEquals(profile.getAdditionalProperties().get("MaxUsersCount"), 42);
    }

    @Test
    public void testDeserialize_withMultipleCustomAttributes_allSurfaced() throws JsonProcessingException {
        String json = "{\"name\":\"Sales\",\"region\":\"EMEA\",\"costCenter\":\"CC-100\",\"isVip\":true}";

        GroupProfile profile = objectMapper.readValue(json, GroupProfile.class);

        assertEquals(profile.getName(), "Sales");
        assertEquals(profile.getAdditionalProperties().get("region"), "EMEA");
        assertEquals(profile.getAdditionalProperties().get("costCenter"), "CC-100");
        assertEquals(profile.getAdditionalProperties().get("isVip"), true);
    }

    @Test
    public void testDeserialize_withOnlyKnownFields_hasEmptyAdditionalProperties() throws JsonProcessingException {
        String json = "{\"name\":\"NoCustomAttrs\"}";

        GroupProfile profile = objectMapper.readValue(json, GroupProfile.class);

        assertEquals(profile.getName(), "NoCustomAttrs");
        assertTrue(profile.getAdditionalProperties().isEmpty());
    }
}

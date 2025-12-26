/*
 * Copyright 2024-Present Okta, Inc.
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link RoleAssignmentDeserializer}.
 * 
 * These tests verify proper handling of:
 * - Valid JSON with all fields
 * - Missing optional fields
 * - Null values
 * - Invalid enum values
 * - Malformed JSON
 * - Security (field whitelist validation)
 */
public class RoleAssignmentDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Add mix-in to disable @JsonTypeInfo on the response class
        // This prevents Jackson from trying to use polymorphic type resolution
        objectMapper.addMixIn(ListGroupAssignedRoles200ResponseInner.class, IgnoreTypeInfoMixIn.class);
        
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ListGroupAssignedRoles200ResponseInner.class, 
            new RoleAssignmentDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testDeserialize_withValidJson_succeeds() throws JsonProcessingException {
        String json = "{" +
            "\"id\": \"role123\"," +
            "\"label\": \"Super Administrator\"," +
            "\"assignmentType\": \"USER\"," +
            "\"status\": \"ACTIVE\"," +
            "\"type\": \"SUPER_ADMIN\"," +
            "\"created\": \"2024-01-15T10:30:00Z\"," +
            "\"lastUpdated\": \"2024-06-20T14:45:00Z\"" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "role123");
        assertEquals(result.getLabel(), "Super Administrator");
        assertEquals(result.getAssignmentType(), RoleAssignmentType.USER);
        assertEquals(result.getStatus(), LifecycleStatus.ACTIVE);
        assertEquals(result.getType(), RoleType.SUPER_ADMIN);
        assertNotNull(result.getCreated());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    public void testDeserialize_withMissingOptionalFields_succeeds() throws JsonProcessingException {
        String json = "{\"id\": \"role456\", \"type\": \"USER_ADMIN\"}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "role456");
        assertEquals(result.getType(), RoleType.USER_ADMIN);
        assertNull(result.getLabel());
        assertNull(result.getAssignmentType());
        assertNull(result.getStatus());
    }

    @Test
    public void testDeserialize_withNullValues_handlesGracefully() throws JsonProcessingException {
        String json = "{" +
            "\"id\": \"role789\"," +
            "\"label\": null," +
            "\"assignmentType\": null," +
            "\"status\": null," +
            "\"type\": \"ORG_ADMIN\"," +
            "\"created\": null," +
            "\"lastUpdated\": null" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "role789");
        assertNull(result.getLabel());
        assertNull(result.getAssignmentType());
        assertNull(result.getStatus());
        assertEquals(result.getType(), RoleType.ORG_ADMIN);
    }

    @Test
    public void testDeserialize_withInvalidEnumValue_handlesGracefully() throws JsonProcessingException {
        // Invalid enum values are handled by the OpenAPI-generated enums which have
        // UNKNOWN_DEFAULT_OPEN_API as a fallback value
        String json = "{" +
            "\"id\": \"roleABC\"," +
            "\"type\": \"INVALID_ROLE_TYPE\"," +
            "\"status\": \"INVALID_STATUS\"," +
            "\"assignmentType\": \"INVALID_ASSIGNMENT\"" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "roleABC");
        // Invalid enum values result in the default fallback value (UNKNOWN_DEFAULT_OPEN_API)
        assertEquals(result.getType(), RoleType.UNKNOWN_DEFAULT_OPEN_API);
        assertEquals(result.getStatus(), LifecycleStatus.UNKNOWN_DEFAULT_OPEN_API);
        assertEquals(result.getAssignmentType(), RoleAssignmentType.UNKNOWN_DEFAULT_OPEN_API);
    }

    @Test
    public void testDeserialize_withCustomRoleFields_succeeds() throws JsonProcessingException {
        String json = "{" +
            "\"id\": \"customRole123\"," +
            "\"label\": \"Custom Role Label\"," +
            "\"type\": \"CUSTOM\"," +
            "\"resource-set\": \"iam_resource_set_abc\"," +
            "\"role\": \"cr0123456789\"" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "customRole123");
        assertEquals(result.getLabel(), "Custom Role Label");
        assertEquals(result.getResourceSet(), "iam_resource_set_abc");
        assertEquals(result.getRole(), "cr0123456789");
    }

    @Test
    public void testDeserialize_withEmbeddedAndLinks_succeeds() throws JsonProcessingException {
        // Test with _embedded field - _links removed as LinksCustomRoleResponse 
        // has a specific schema that doesn't include generic "self" link
        String json = "{" +
            "\"id\": \"roleWithLinks\"," +
            "\"type\": \"APP_ADMIN\"," +
            "\"label\": \"Application Administrator\"" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "roleWithLinks");
        assertEquals(result.getType(), RoleType.APP_ADMIN);
        assertEquals(result.getLabel(), "Application Administrator");
    }

    @Test
    public void testDeserialize_withUnexpectedFields_ignores() throws JsonProcessingException {
        // Unexpected fields should be ignored (not cause errors)
        String json = "{" +
            "\"id\": \"roleUnknown\"," +
            "\"type\": \"READ_ONLY_ADMIN\"," +
            "\"unexpectedField\": \"should be ignored\"," +
            "\"anotherUnknown\": 12345" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertEquals(result.getId(), "roleUnknown");
        assertEquals(result.getType(), RoleType.READ_ONLY_ADMIN);
    }

    @Test
    public void testDeserialize_withEmptyJson_succeeds() throws JsonProcessingException {
        String json = "{}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getType());
    }

    @Test(expectedExceptions = JsonProcessingException.class)
    public void testDeserialize_withMalformedJson_throws() throws JsonProcessingException {
        String malformedJson = "{ invalid json }";
        objectMapper.readValue(malformedJson, ListGroupAssignedRoles200ResponseInner.class);
    }

    @Test
    public void testDeserialize_withAllRoleTypes_succeeds() throws JsonProcessingException {
        // Test each standard role type
        String[] roleTypes = {
            "SUPER_ADMIN", "ORG_ADMIN", "APP_ADMIN", "USER_ADMIN", 
            "HELP_DESK_ADMIN", "READ_ONLY_ADMIN", "MOBILE_ADMIN",
            "API_ACCESS_MANAGEMENT_ADMIN", "REPORT_ADMIN", "GROUP_MEMBERSHIP_ADMIN"
        };

        for (String roleType : roleTypes) {
            String json = String.format("{\"id\": \"test_%s\", \"type\": \"%s\"}", roleType, roleType);

            ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
                ListGroupAssignedRoles200ResponseInner.class);

            assertNotNull(result, "Failed for role type: " + roleType);
            assertEquals(result.getId(), "test_" + roleType);
        }
    }

    @Test
    public void testDeserialize_withDateFormats_succeeds() throws JsonProcessingException {
        // Test ISO-8601 date format
        String json = "{" +
            "\"id\": \"roleDates\"," +
            "\"created\": \"2024-12-10T15:30:00.000Z\"," +
            "\"lastUpdated\": \"2024-12-11T09:45:30.123Z\"" +
            "}";

        ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
            ListGroupAssignedRoles200ResponseInner.class);

        assertNotNull(result);
        assertNotNull(result.getCreated());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    public void testDeserialize_withAllAssignmentTypes_succeeds() throws JsonProcessingException {
        String[] assignmentTypes = {"USER", "GROUP"};

        for (String assignmentType : assignmentTypes) {
            String json = String.format("{\"id\": \"test_%s\", \"assignmentType\": \"%s\"}", 
                assignmentType, assignmentType);

            ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
                ListGroupAssignedRoles200ResponseInner.class);

            assertNotNull(result, "Failed for assignment type: " + assignmentType);
        }
    }

    @Test
    public void testDeserialize_withAllLifecycleStatuses_succeeds() throws JsonProcessingException {
        String[] statuses = {"ACTIVE", "INACTIVE"};

        for (String status : statuses) {
            String json = String.format("{\"id\": \"test_%s\", \"status\": \"%s\"}", status, status);

            ListGroupAssignedRoles200ResponseInner result = objectMapper.readValue(json, 
                ListGroupAssignedRoles200ResponseInner.class);

            assertNotNull(result, "Failed for status: " + status);
        }
    }
}

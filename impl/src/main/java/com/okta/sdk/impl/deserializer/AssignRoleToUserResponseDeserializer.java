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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.AssignRoleToUser201Response;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;

/**
 * Custom JSON deserializer for AssignRoleToUser201Response.
 * 
 * The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * AssignRoleToUser201Response, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.
 */
public class AssignRoleToUserResponseDeserializer extends StdDeserializer<AssignRoleToUser201Response> {

    private static final long serialVersionUID = 1L;

    public AssignRoleToUserResponseDeserializer() {
        this(null);
    }

    public AssignRoleToUserResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AssignRoleToUser201Response deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();

        AssignRoleToUser201Response response = new AssignRoleToUser201Response();

        // Parse id (read-only field, use reflection)
        if (node.has("id") && !node.get("id").isNull()) {
            setFieldValue(response, "id", node.get("id").asText());
        }

        // Parse label (read-only field, use reflection)
        if (node.has("label") && !node.get("label").isNull()) {
            setFieldValue(response, "label", node.get("label").asText());
        }

        // Parse assignmentType
        if (node.has("assignmentType") && !node.get("assignmentType").isNull()) {
            response.setAssignmentType(RoleAssignmentType.fromValue(node.get("assignmentType").asText()));
        }

        // Parse status
        if (node.has("status") && !node.get("status").isNull()) {
            response.setStatus(LifecycleStatus.fromValue(node.get("status").asText()));
        }

        // Parse type
        if (node.has("type") && !node.get("type").isNull()) {
            response.setType(RoleType.fromValue(node.get("type").asText()));
        }

        // Parse created (read-only field, use reflection)
        if (node.has("created") && !node.get("created").isNull()) {
            OffsetDateTime created = mapper.treeToValue(node.get("created"), OffsetDateTime.class);
            setFieldValue(response, "created", created);
        }

        // Parse lastUpdated (read-only field, use reflection)
        if (node.has("lastUpdated") && !node.get("lastUpdated").isNull()) {
            OffsetDateTime lastUpdated = mapper.treeToValue(node.get("lastUpdated"), OffsetDateTime.class);
            setFieldValue(response, "lastUpdated", lastUpdated);
        }

        // Parse resource-set (read-only field for CustomRole, use reflection)
        if (node.has("resource-set") && !node.get("resource-set").isNull()) {
            setFieldValue(response, "resourceSet", node.get("resource-set").asText());
        }

        // Parse role (read-only field for CustomRole, use reflection)
        if (node.has("role") && !node.get("role").isNull()) {
            setFieldValue(response, "role", node.get("role").asText());
        }

        // Parse _embedded
        if (node.has("_embedded") && !node.get("_embedded").isNull()) {
            StandardRoleEmbedded embedded = mapper.treeToValue(node.get("_embedded"), StandardRoleEmbedded.class);
            response.setEmbedded(embedded);
        }

        // Parse _links
        if (node.has("_links") && !node.get("_links").isNull()) {
            LinksCustomRoleResponse links = mapper.treeToValue(node.get("_links"), LinksCustomRoleResponse.class);
            response.setLinks(links);
        }

        return response;
    }

    /**
     * Sets a field value using reflection. This is needed for read-only fields
     * that don't have public setters.
     */
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Ignore - field may not exist in the model
        }
    }
}

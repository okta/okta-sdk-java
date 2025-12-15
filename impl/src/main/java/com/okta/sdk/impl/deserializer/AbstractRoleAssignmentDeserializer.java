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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for role assignment deserializers.
 * 
 * <p>This class provides common deserialization logic for role assignment responses
 * from the Okta API. The API uses the 'type' field as a discriminator for role assignments,
 * but since generated classes don't extend the response types, Jackson's default polymorphic
 * deserialization fails.</p>
 * 
 * <p><strong>Security Note:</strong> This deserializer uses reflection to set read-only fields
 * that don't have public setters. Field names are validated against a whitelist to prevent
 * arbitrary field access from potentially malicious JSON input.</p>
 *
 * @param <T> The response type being deserialized
 */
public abstract class AbstractRoleAssignmentDeserializer<T> extends StdDeserializer<T> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractRoleAssignmentDeserializer.class);

    /**
     * Whitelist of allowed field names that can be set via reflection.
     * This prevents malicious JSON from setting arbitrary fields.
     */
    private static final Set<String> ALLOWED_FIELDS = Set.of(
        "id", "label", "created", "lastUpdated", "resourceSet", "role"
    );

    /**
     * Cache for Field objects to avoid repeated reflection lookups.
     * Key format: "className.fieldName"
     */
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    protected AbstractRoleAssignmentDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Creates a new instance of the response type.
     *
     * @return A new instance of type T
     */
    protected abstract T createInstance();

    /**
     * Sets the assignment type on the response object.
     *
     * @param response The response object
     * @param assignmentType The assignment type to set
     */
    protected abstract void setAssignmentType(T response, RoleAssignmentType assignmentType);

    /**
     * Sets the status on the response object.
     *
     * @param response The response object
     * @param status The lifecycle status to set
     */
    protected abstract void setStatus(T response, LifecycleStatus status);

    /**
     * Sets the role type on the response object.
     *
     * @param response The response object
     * @param type The role type to set
     */
    protected abstract void setType(T response, RoleType type);

    /**
     * Sets the embedded object on the response.
     *
     * @param response The response object
     * @param embedded The embedded object to set
     */
    protected abstract void setEmbedded(T response, StandardRoleEmbedded embedded);

    /**
     * Sets the links object on the response.
     *
     * @param response The response object
     * @param links The links object to set
     */
    protected abstract void setLinks(T response, LinksCustomRoleResponse links);

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();

        T response = createInstance();

        // Parse id (read-only field, use reflection)
        if (hasNonNullValue(node, "id")) {
            setFieldValueSafe(response, "id", node.get("id").asText(), ctxt);
        }

        // Parse label (read-only field, use reflection)
        if (hasNonNullValue(node, "label")) {
            setFieldValueSafe(response, "label", node.get("label").asText(), ctxt);
        }

        // Parse assignmentType
        if (hasNonNullValue(node, "assignmentType")) {
            try {
                setAssignmentType(response, RoleAssignmentType.fromValue(node.get("assignmentType").asText()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid assignmentType value '{}': {}", node.get("assignmentType").asText(), e.getMessage());
            }
        }

        // Parse status
        if (hasNonNullValue(node, "status")) {
            try {
                setStatus(response, LifecycleStatus.fromValue(node.get("status").asText()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status value '{}': {}", node.get("status").asText(), e.getMessage());
            }
        }

        // Parse type
        if (hasNonNullValue(node, "type")) {
            try {
                setType(response, RoleType.fromValue(node.get("type").asText()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid type value '{}': {}", node.get("type").asText(), e.getMessage());
            }
        }

        // Parse created (read-only field, use reflection)
        if (hasNonNullValue(node, "created")) {
            OffsetDateTime created = mapper.treeToValue(node.get("created"), OffsetDateTime.class);
            if (created != null) {
                setFieldValueSafe(response, "created", created, ctxt);
            }
        }

        // Parse lastUpdated (read-only field, use reflection)
        if (hasNonNullValue(node, "lastUpdated")) {
            OffsetDateTime lastUpdated = mapper.treeToValue(node.get("lastUpdated"), OffsetDateTime.class);
            if (lastUpdated != null) {
                setFieldValueSafe(response, "lastUpdated", lastUpdated, ctxt);
            }
        }

        // Parse resource-set (read-only field for CustomRole, use reflection)
        if (hasNonNullValue(node, "resource-set")) {
            setFieldValueSafe(response, "resourceSet", node.get("resource-set").asText(), ctxt);
        }

        // Parse role (read-only field for CustomRole, use reflection)
        if (hasNonNullValue(node, "role")) {
            setFieldValueSafe(response, "role", node.get("role").asText(), ctxt);
        }

        // Parse _embedded
        if (hasNonNullValue(node, "_embedded")) {
            StandardRoleEmbedded embedded = mapper.treeToValue(node.get("_embedded"), StandardRoleEmbedded.class);
            if (embedded != null) {
                setEmbedded(response, embedded);
            }
        }

        // Parse _links
        if (hasNonNullValue(node, "_links")) {
            LinksCustomRoleResponse links = mapper.treeToValue(node.get("_links"), LinksCustomRoleResponse.class);
            if (links != null) {
                setLinks(response, links);
            }
        }

        return response;
    }

    /**
     * Checks if a JSON node has a non-null value for the given field name.
     *
     * @param node The JSON node to check
     * @param fieldName The field name to look for
     * @return true if the field exists and is not null
     */
    protected boolean hasNonNullValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull();
    }

    /**
     * Sets a field value using reflection with security validation and caching.
     * 
     * <p><strong>Security Note:</strong> This method validates field names against a whitelist
     * to prevent arbitrary field access from malicious JSON input.</p>
     *
     * @param target The object to set the field on
     * @param fieldName The name of the field to set
     * @param value The value to set
     * @param ctxt The deserialization context for error reporting
     * @throws JsonMappingException if the field is not allowed or cannot be set
     */
    protected void setFieldValueSafe(Object target, String fieldName, Object value, DeserializationContext ctxt) 
            throws JsonMappingException {
        // Security: Validate field name against whitelist
        if (!ALLOWED_FIELDS.contains(fieldName)) {
            logger.error("Attempted to set disallowed field '{}' on {}", fieldName, target.getClass().getSimpleName());
            throw JsonMappingException.from(ctxt, "Field not allowed: " + fieldName);
        }

        String cacheKey = target.getClass().getName() + "." + fieldName;
        
        try {
            Field field = FIELD_CACHE.computeIfAbsent(cacheKey, k -> {
                try {
                    Field f = target.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException e) {
                    logger.error("Field '{}' not found on {}: {}", fieldName, target.getClass().getSimpleName(), e.getMessage());
                    return null;
                }
            });

            if (field == null) {
                logger.error("Failed to access field '{}' on {}", fieldName, target.getClass().getSimpleName());
                throw JsonMappingException.from(ctxt, "Cannot access field: " + fieldName);
            }

            field.set(target, value);
        } catch (IllegalAccessException e) {
            logger.error("Failed to set field '{}' on {}: {}", fieldName, target.getClass().getSimpleName(), e.getMessage());
            throw JsonMappingException.from(ctxt, "Cannot set field: " + fieldName, e);
        }
    }
}

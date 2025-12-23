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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.GroupOwner;
import com.okta.sdk.resource.model.GroupOwnerOriginType;
import com.okta.sdk.resource.model.GroupOwnerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom JSON deserializer for GroupOwner responses.
 * 
 * <p>The Okta Group Owner API returns dates in a non-ISO-8601 format:
 * "Wed Dec 10 19:57:11 UTC 2025" instead of "2025-12-10T19:57:11Z"</p>
 * 
 * <p>This deserializer handles both standard ISO-8601 format and the
 * non-standard format returned by the Group Owner API.</p>
 * 
 * <p><strong>Security Note:</strong> This deserializer uses reflection to set read-only fields
 * that don't have public setters. Field names are validated against a whitelist to prevent
 * arbitrary field access from potentially malicious JSON input.</p>
 */
public class GroupOwnerDeserializer extends StdDeserializer<GroupOwner> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GroupOwnerDeserializer.class);

    /**
     * Whitelist of allowed field names that can be set via reflection.
     * This prevents malicious JSON from setting arbitrary fields.
     */
    private static final Set<String> ALLOWED_FIELDS = Set.of(
        "displayName", "lastUpdated"
    );

    /**
     * Cache for Field objects to avoid repeated reflection lookups.
     */
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    // Pattern for non-standard date format: "Wed Dec 10 19:57:11 UTC 2025"
    private static final DateTimeFormatter NON_STANDARD_FORMATTER = 
        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

    // Alternative pattern with single-digit day: "Wed Dec  7 19:57:11 UTC 2025"
    private static final DateTimeFormatter NON_STANDARD_FORMATTER_SINGLE_DAY = 
        DateTimeFormatter.ofPattern("EEE MMM  d HH:mm:ss z yyyy", Locale.ENGLISH);

    public GroupOwnerDeserializer() {
        this(null);
    }

    public GroupOwnerDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GroupOwner deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        GroupOwner groupOwner = new GroupOwner();

        // Parse id (has setter)
        if (hasNonNullValue(node, "id")) {
            groupOwner.id(node.get("id").asText());
        }

        // Parse displayName (read-only field, use reflection)
        if (hasNonNullValue(node, "displayName")) {
            setFieldValueSafe(groupOwner, "displayName", node.get("displayName").asText(), ctxt);
        }

        // Parse lastUpdated with custom date handling (read-only field, use reflection)
        if (hasNonNullValue(node, "lastUpdated")) {
            String dateStr = node.get("lastUpdated").asText();
            OffsetDateTime dateTime = parseDateTime(dateStr);
            if (dateTime != null) {
                setFieldValueSafe(groupOwner, "lastUpdated", dateTime, ctxt);
            }
        }

        // Parse originId (has setter)
        if (hasNonNullValue(node, "originId")) {
            groupOwner.originId(node.get("originId").asText());
        }

        // Parse originType (has setter)
        if (hasNonNullValue(node, "originType")) {
            try {
                groupOwner.originType(GroupOwnerOriginType.fromValue(node.get("originType").asText()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid originType value '{}': {}", node.get("originType").asText(), e.getMessage());
            }
        }

        // Parse resolved (has setter)
        if (hasNonNullValue(node, "resolved")) {
            groupOwner.resolved(node.get("resolved").asBoolean());
        }

        // Parse type (has setter)
        if (hasNonNullValue(node, "type")) {
            try {
                groupOwner.type(GroupOwnerType.fromValue(node.get("type").asText()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid type value '{}': {}", node.get("type").asText(), e.getMessage());
            }
        }

        return groupOwner;
    }

    /**
     * Checks if a JSON node has a non-null value for the given field name.
     *
     * @param node The JSON node to check
     * @param fieldName The field name to look for
     * @return true if the field exists and is not null
     */
    private boolean hasNonNullValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull();
    }

    /**
     * Parse date string supporting both ISO-8601 and non-standard formats.
     * 
     * @param dateStr The date string to parse
     * @return The parsed OffsetDateTime, or null if parsing fails
     */
    private OffsetDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        // First try ISO-8601 format (most common)
        try {
            return OffsetDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            // Try non-standard format: "Wed Dec 10 19:57:11 UTC 2025"
            try {
                java.time.ZonedDateTime zdt = java.time.ZonedDateTime.parse(dateStr, NON_STANDARD_FORMATTER);
                return zdt.toOffsetDateTime();
            } catch (DateTimeParseException e2) {
                // Try single-digit day format: "Wed Dec  7 19:57:11 UTC 2025"
                try {
                    java.time.ZonedDateTime zdt = java.time.ZonedDateTime.parse(dateStr, NON_STANDARD_FORMATTER_SINGLE_DAY);
                    return zdt.toOffsetDateTime();
                } catch (DateTimeParseException e3) {
                    logger.warn("Unable to parse date string: '{}'. Tried ISO-8601 and non-standard formats.", dateStr);
                    return null;
                }
            }
        }
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
    private void setFieldValueSafe(Object target, String fieldName, Object value, DeserializationContext ctxt) 
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

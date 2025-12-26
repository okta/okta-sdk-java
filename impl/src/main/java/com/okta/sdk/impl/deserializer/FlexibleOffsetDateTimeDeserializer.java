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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Custom JSON deserializer for OffsetDateTime that handles non-ISO-8601 formats.
 * 
 * Some Okta APIs (e.g., Group Owner API) return dates in a non-standard format:
 * "Wed Dec 10 19:57:11 UTC 2025" instead of "2025-12-10T19:57:11Z"
 * 
 * This deserializer handles both standard ISO-8601 format and the non-standard format.
 */
public class FlexibleOffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FlexibleOffsetDateTimeDeserializer.class);

    // Pattern for non-standard date format: "Wed Dec 10 19:57:11 UTC 2025"
    private static final DateTimeFormatter NON_STANDARD_FORMATTER = 
        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

    // Alternative pattern with single-digit day: "Wed Dec  7 19:57:11 UTC 2025"  
    private static final DateTimeFormatter NON_STANDARD_FORMATTER_SINGLE_DAY = 
        DateTimeFormatter.ofPattern("EEE MMM  d HH:mm:ss z yyyy", Locale.ENGLISH);

    public FlexibleOffsetDateTimeDeserializer() {
        this(null);
    }

    public FlexibleOffsetDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String dateStr = jp.getText();
        return parseDateTime(dateStr);
    }

    /**
     * Parse date string supporting both ISO-8601 and non-standard formats.
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
                // Try with single-digit day format: "Wed Dec  7 19:57:11 UTC 2025"
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
}

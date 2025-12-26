/*
 * Copyright 2023-Present Okta, Inc.
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
package com.okta.sdk.impl.retry;

import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class RetryUtil {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    /**
     * Maximum exponential back-off time before retrying a request (20 seconds).
     */
    private static final int DEFAULT_MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    /**
     * Initial backoff delay in milliseconds for exponential backoff calculation.
     * Combined with the retry count, this determines the delay between retries.
     */
    private static final long INITIAL_BACKOFF_MS = 300;

    /**
     * Additional buffer time (1 second) added to rate limit delays.
     * This provides a safety margin to ensure the rate limit has fully reset.
     */
    private static final long RATE_LIMIT_BUFFER_MS = 1000;

    /**
     * Minimum delay between retries (1 second).
     * Ensures we don't retry too quickly even if calculated delay is very small.
     */
    private static final long MIN_RETRY_DELAY_MS = 1000;

    /**
     * HTTP date format as per RFC 1123 (used in Date header).
     */
    private static final DateTimeFormatter HTTP_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    /**
     * Calculates the delay in milliseconds for exponential backoff.
     * 
     * @param retries The current retry attempt number (0-based)
     * @return The calculated delay in milliseconds, capped at MAX_BACKOFF
     */
    static long getDefaultDelayMillis(int retries) {
        long result = (long) (Math.pow(2, retries) * INITIAL_BACKOFF_MS);
        long millis = Math.min(result, DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
        logger.debug("getDefaultDelayMillis: [{}]", millis);
        return millis;
    }

    /**
     * Calculates the delay in milliseconds for a 429 (Too Many Requests) response.
     * Uses the x-rate-limit-reset header to determine when the rate limit resets.
     * 
     * @param response The HTTP response containing rate limit headers
     * @return The delay in milliseconds, or -1 if headers are missing/invalid
     */
    static long get429DelayMillis(HttpResponse response) {
        // the time at which the rate limit will reset, specified in UTC epoch time.
        long resetLimit = getRateLimitResetValue(response);
        if (resetLimit == -1L) {
            return -1;
        }

        // If the Date header is not set, do not continue
        Date requestDate = dateFromHeader(response);
        if (requestDate == null) {
            return -1;
        }

        long waitUntil = resetLimit * 1000L;
        long requestTime = requestDate.getTime();
        long delay = Math.max(waitUntil - requestTime + RATE_LIMIT_BUFFER_MS, MIN_RETRY_DELAY_MS);
        logger.debug("429 wait: Math.max({} - {} + {}ms), {}ms = {})", 
            waitUntil, requestTime, RATE_LIMIT_BUFFER_MS, MIN_RETRY_DELAY_MS, delay);

        return delay;
    }

    /**
     * Parses the Date header from an HTTP response.
     * 
     * @param response The HTTP response
     * @return The parsed Date, or null if the header is missing or cannot be parsed
     */
    static Date dateFromHeader(HttpResponse response) {
        try {
            Header dateHeader = response.getHeader("Date");
            if (dateHeader == null || TextUtils.isBlank(dateHeader.getValue())) {
                logger.debug("Date header is missing or empty");
                return null;
            }

            String dateValue = dateHeader.getValue();
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateValue, HTTP_DATE_FORMATTER);
            return Date.from(offsetDateTime.toInstant());

        } catch (ProtocolException e) {
            logger.error("Protocol error reading Date header: {}", e.getMessage());
            return null;
        } catch (DateTimeParseException e) {
            logger.error("Failed to parse Date header: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gets the rate limit reset value from the x-rate-limit-reset header.
     * 
     * @param response The HTTP response
     * @return The reset timestamp in seconds (epoch time), or -1 if not present
     */
    static long getRateLimitResetValue(HttpResponse response) {
        Header header = response.getFirstHeader("x-rate-limit-reset");
        return header != null && !TextUtils.isBlank(header.getValue()) ? Long.parseLong(header.getValue()) : -1L;
    }
}

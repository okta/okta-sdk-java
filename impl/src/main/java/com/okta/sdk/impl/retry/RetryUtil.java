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

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class RetryUtil {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    /**
     * Maximum exponential back-off time before retrying a request
     */
    private static final int DEFAULT_MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    static long getDefaultDelayMillis(int retries) {
        long scaleFactor = 300;
        long result = (long) (Math.pow(2, retries) * scaleFactor);
        long millis = Math.min(result, DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
        logger.debug("getDefaultDelayMillis: [{}]", millis);
        return millis;
    }

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
        long delay = Math.max(waitUntil - requestTime + 1000, 1000);
        logger.debug("429 wait: Math.max({} - {} + 1s), 1s = {})", waitUntil, requestTime, delay);

        return delay;
    }

    static Date dateFromHeader(HttpResponse response) {
        Date result;

        try {
            result = new Date(response.getHeader("Date").getValue());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    static long getRateLimitResetValue(HttpResponse response) {
        Header header = response.getFirstHeader("x-rate-limit-reset");
        return header != null && StringUtils.isNotBlank(header.getValue()) ? Long.parseLong(header.getValue()) : -1L;
    }
}

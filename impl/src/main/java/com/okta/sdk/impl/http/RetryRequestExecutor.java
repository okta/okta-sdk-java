/*
 * Copyright 2018-Present Okta, Inc.
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
package com.okta.sdk.impl.http;

import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.http.support.BackoffStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RetryRequestExecutor implements RequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(RetryRequestExecutor.class);

    /**
     * Maximum exponential back-off time before retrying a request
     */
    private static final int DEFAULT_MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    private static final int DEFAULT_MAX_RETRIES = 4;

    private int maxRetries = DEFAULT_MAX_RETRIES;

    private int maxElapsedMillis = 0;

    private BackoffStrategy backoffStrategy;

    private final RequestExecutor delegate;

    public RetryRequestExecutor(ClientConfiguration clientConfiguration, RequestExecutor delegate) {
        this.delegate = delegate;

        if (clientConfiguration.getRetryMaxElapsed() >= 0) {
            maxElapsedMillis = clientConfiguration.getRetryMaxElapsed() * 1000;
        }

        if (clientConfiguration.getRetryMaxAttempts() > 0) {
            maxRetries = clientConfiguration.getRetryMaxAttempts();
        }
    }

    @Override
    public Response executeRequest(Request request) throws RestException {

        Assert.notNull(request, "Request argument cannot be null.");

        int retryCount = 0;
        Response response = null;
        String requestId = null;
        Timer timer = new Timer();

        // Make a copy of the original request params and headers so that we can
        // permute them in the loop and start over with the original every time.
        QueryString originalQuery = new QueryString();
        originalQuery.putAll(request.getQueryString());

        HttpHeaders originalHeaders = new HttpHeaders();
        originalHeaders.putAll(request.getHeaders());

        while (true) {

            try {

                if (retryCount > 0) {
                    request.setQueryString(originalQuery);
                    request.setHeaders(originalHeaders);

                    // remember the request-id header if we need to retry
                    if (requestId == null) {
                        requestId = getRequestId(response);
                    }

                    InputStream content = request.getBody();
                    if (content != null && content.markSupported()) {
                        content.reset();
                    }

                    try {
                        // if we cannot pause, then return the original response
                        pauseBeforeRetry(retryCount, response, timer.split());
                    } catch (RestException e) {
                        if (log.isDebugEnabled()) {
                            log.warn("Unable to pause for retry: {}", e.getMessage(), e);
                        } else {
                            log.warn("Unable to pause for retry: {}", e.getMessage());
                        }

                        return response;
                    }
                }

                retryCount++;

                // include X-Okta headers when retrying
                setOktaHeaders(request, requestId, retryCount);

                response = doExecuteRequest(request);

                //allow the loop to continue to execute a retry request
                if (!shouldRetry(response, retryCount, timer.split())) {
                    return response;
                }

            } catch (SocketException | SocketTimeoutException e) { // known generic retryable exceptions
                if (!shouldRetry(retryCount, timer.split())) {
                    throw new RestException("Unable to execute HTTP request: " + e.getMessage(), e);
                }
                log.debug("Retrying on {}: {}", e.getClass().getName(), e.getMessage());
            } catch (RestException e) {
                // exceptions from delegate marked as retrHttpClientRequestExecutoryable
                if (!e.isRetryable() || !shouldRetry(retryCount, timer.split())) {
                    throw e;
                }
            } catch (Exception e) {
                throw new RestException("Unable to execute HTTP request: " + e.getMessage(), e);
            }
        }
    }

    // exposed to allow HttpClientRequestExecutor to be backward compatible
    // do NOT use directly
    protected Response doExecuteRequest(Request request) {
        return delegate.executeRequest(request);
    }

    /**
     * Exponential sleep on failed request to avoid flooding a service with
     * retries.
     *
     * @param retries           Current retry count.
     */
    private void pauseBeforeRetry(int retries, Response response, long timeElapsed) throws RestException {
        long delay = -1;
        long timeElapsedLeft = maxElapsedMillis - timeElapsed;

        // check before continuing
        if (!shouldRetry(retries, timeElapsed)) {
            throw failedToRetry();
        }

        if (backoffStrategy != null) {
            delay = Math.min(this.backoffStrategy.getDelayMillis(retries), timeElapsedLeft);
        } else if (response != null && response.getHttpStatus() == 429) {
            delay = get429DelayMillis(response);
            if (!shouldRetry(retries, timeElapsed + delay)) {
                throw failedToRetry();
            }
            log.debug("429 detected, will retry in {}ms, attempt number: {}", delay, retries);
        }

        // default / fallback strategy (backwards compatible implementation)
        if (delay < 0) {
            delay = Math.min(getDefaultDelayMillis(retries), timeElapsedLeft);
        }

        // this shouldn't happen, but guard against a negative delay at this point
        if (delay < 0) {
            throw failedToRetry();
        }

        log.debug("Retryable condition detected, will retry in {}ms, attempt number: {}", delay, retries);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestException(e.getMessage(), e);
        }
    }

    private long get429DelayMillis(Response response) {

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
        log.debug("429 wait: Math.max({} - {} + 1s), 1s = {})", waitUntil, requestTime, delay);

        return delay;
    }

    private Date dateFromHeader(Response response) {
        Date result = null;
        long dateLong = response.getHeaders().getDate();
        if (dateLong > 0) {
            result = new Date(dateLong);
        }
        return result;
    }

    private long getDefaultDelayMillis(int retries) {
        long scaleFactor = 300;
        long result = (long) (Math.pow(2, retries) * scaleFactor);
        return Math.min(result, DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
    }

    private boolean shouldRetry(int retryCount, long timeElapsed) {
               // either maxRetries or maxElapsedMillis is enabled
        return (maxRetries > 0 || maxElapsedMillis > 0)

               // maxRetries count is disabled OR if set check it
               && (maxRetries <= 0 || retryCount <= this.maxRetries)

               // maxElapsedMillis is disabled OR if set check it
               && (maxElapsedMillis <= 0 || timeElapsed < maxElapsedMillis);
    }

    private boolean shouldRetry(Response response, int retryCount, long timeElapsed) {
        int httpStatus = response.getHttpStatus();

        // supported status codes
        return shouldRetry(retryCount, timeElapsed)
            && (httpStatus == 429
             || httpStatus == 503
             || httpStatus == 504);
    }

    private RestException failedToRetry() {
        return new RestException("Cannot retry request, next request will exceed retry configuration.");
    }

    private long getRateLimitResetValue(Response response) {
        return response.getHeaders().getOrDefault("X-Rate-Limit-Reset", java.util.Collections.emptyList()).stream()
            .filter(value -> !Strings.isEmpty(value))
            .filter(value -> value.chars().allMatch(Character::isDigit))
            .map(Long::parseLong)
            .filter(value -> value > 0)
            .min(Comparator.naturalOrder())
            .orElse(-1L);
    }

    private String getRequestId(Response response) {
        if (response != null) {
            return response.getHeaders().getFirst("X-Okta-Request-Id");
        }
        return null;
    }

    /**
     * Adds {@code X-Okta-Retry-For} and {@code X-Okta-Retry-Count} headers to request if not null/empty or zero.
     *
     * @param request the request to add headers too
     * @param requestId request ID of the original request that failed
     * @param retryCount the number of times the request has been retried
     */
    private void setOktaHeaders(Request request, String requestId, int retryCount) {
        if (Strings.hasText(requestId)) {
            request.getHeaders().add("X-Okta-Retry-For", requestId);
        }
        if (retryCount > 1) {
            request.getHeaders().add("X-Okta-Retry-Count", Integer.toString(retryCount));
        }
    }

    @Deprecated
    public BackoffStrategy getBackoffStrategy() {
        return this.backoffStrategy;
    }

    @Deprecated
    public void setBackoffStrategy(BackoffStrategy backoffStrategy) {
        this.backoffStrategy = backoffStrategy;
    }

    @Deprecated
    public int getNumRetries() {
        return maxRetries;
    }

    @Deprecated
    public void setNumRetries(int numRetries) {
        this.maxRetries = numRetries;
    }

    @Deprecated
    int getMaxElapsedMillis() {
        return maxElapsedMillis;
    }

    @Deprecated
    void setMaxElapsedMillis(int maxElapsedMillis) {
        this.maxElapsedMillis = maxElapsedMillis;
    }

    private static class Timer {

        private long startTime = System.currentTimeMillis();

        long split() {
            return System.currentTimeMillis() - startTime;
        }
    }

}

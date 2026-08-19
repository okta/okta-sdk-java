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
package com.okta.sdk.impl.retry;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class OktaHttpRequestRetryStrategyTest {

    /**
     * Regression test for a server (or MitM) returning an unbounded x-rate-limit-reset value on a
     * 429 response. Without a client-side ceiling, this would cause getRetryInterval to sleep for
     * an excessively long time (e.g. years), which can block callers indefinitely.
     */
    @Test
    public void testGetRetryIntervalCaps429DelayWithDefaultConfig() throws ProtocolException {
        // no explicit retryMaxElapsed configured -> falls back to the default 20s cap
        OktaHttpRequestRetryStrategy strategy = new OktaHttpRequestRetryStrategy(4);

        HttpResponse response = maliciousRateLimitResponse();
        TimeValue retryInterval = strategy.getRetryInterval(response, 1, mock(HttpContext.class));

        assertEquals(RetryUtil.DEFAULT_MAX_BACKOFF_IN_MILLISECONDS, retryInterval.toMilliseconds());
    }

    @Test
    public void testGetRetryIntervalHonorsConfiguredRetryMaxElapsed() throws ProtocolException {
        int retryMaxElapsedSeconds = 5;
        OktaHttpRequestRetryStrategy strategy = new OktaHttpRequestRetryStrategy(4, retryMaxElapsedSeconds);

        HttpResponse response = maliciousRateLimitResponse();
        TimeValue retryInterval = strategy.getRetryInterval(response, 1, mock(HttpContext.class));

        assertEquals(TimeUnit.SECONDS.toMillis(retryMaxElapsedSeconds), retryInterval.toMilliseconds());
    }

    @Test
    public void testGetRetryIntervalForNon429UsesExponentialBackoff() {
        OktaHttpRequestRetryStrategy strategy = new OktaHttpRequestRetryStrategy(4);

        HttpResponse response = mock(HttpResponse.class);
        when(response.getCode()).thenReturn(503);

        TimeValue retryInterval = strategy.getRetryInterval(response, 3, mock(HttpContext.class));
        assertTrue(retryInterval.toMilliseconds() > 0 && retryInterval.toMilliseconds() <= RetryUtil.DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
    }

    /**
     * Builds a 429 response with an x-rate-limit-reset one year in the future, simulating a
     * malicious/MitM server trying to force an excessively long client-side sleep.
     */
    private static HttpResponse maliciousRateLimitResponse() throws ProtocolException {
        HttpResponse response = mock(HttpResponse.class);
        when(response.getCode()).thenReturn(429);

        long currentTime = System.currentTimeMillis();
        long maliciousResetTime = currentTime / 1000 + TimeUnit.DAYS.toSeconds(365);

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(maliciousResetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        return response;
    }
}

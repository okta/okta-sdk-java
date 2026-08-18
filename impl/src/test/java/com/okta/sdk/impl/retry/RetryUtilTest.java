/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class RetryUtilTest {

    @Test
    public void testGetDefaultDelayMillis() {
        // Test exponential backoff calculation
        assertEquals(300, RetryUtil.getDefaultDelayMillis(0));
        assertEquals(600, RetryUtil.getDefaultDelayMillis(1));
        assertEquals(1200, RetryUtil.getDefaultDelayMillis(2));
        assertEquals(2400, RetryUtil.getDefaultDelayMillis(3));

        // Test maximum backoff limit (20 seconds = 20,000 ms)
        assertEquals(20000, RetryUtil.getDefaultDelayMillis(10));
    }

    @Test
    public void testGetRateLimitResetValue() {
        // Test with header present
        HttpResponse response = mock(HttpResponse.class);
        Header header = mock(Header.class);
        when(header.getValue()).thenReturn("1609459200");
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(header);

        assertEquals(1609459200L, RetryUtil.getRateLimitResetValue(response));

        // Test with header absent
        HttpResponse noHeaderResponse = mock(HttpResponse.class);
        when(noHeaderResponse.getFirstHeader("x-rate-limit-reset")).thenReturn(null);

        assertEquals(-1L, RetryUtil.getRateLimitResetValue(noHeaderResponse));

        // Test with empty header value
        HttpResponse emptyHeaderResponse = mock(HttpResponse.class);
        Header emptyHeader = mock(Header.class);
        when(emptyHeader.getValue()).thenReturn("");
        when(emptyHeaderResponse.getFirstHeader("x-rate-limit-reset")).thenReturn(emptyHeader);

        assertEquals(-1L, RetryUtil.getRateLimitResetValue(emptyHeaderResponse));
    }

    @Test
    public void testDateFromHeader() throws ProtocolException {
        // Test with valid date header
        HttpResponse response = mock(HttpResponse.class);
        Header header = mock(Header.class);

        // HTTP date format: RFC 1123 format "EEE, dd MMM yyyy HH:mm:ss zzz"
        String httpDateString = "Fri, 01 Jan 2021 00:00:00 GMT";
        Date expectedDate = new Date(1609459200000L); // 2021-01-01T00:00:00Z

        when(header.getValue()).thenReturn(httpDateString);
        when(response.getHeader("Date")).thenReturn(header);

        Date result = RetryUtil.dateFromHeader(response);
        assertEquals(expectedDate, result);
    }


    @Test
    public void testDateFromHeaderException() throws ProtocolException {
        // Test behavior when header causes exception - should return null (graceful handling)
        HttpResponse response = mock(HttpResponse.class);
        when(response.getHeader("Date")).thenThrow(new ProtocolException("Test exception"));

        Date result = RetryUtil.dateFromHeader(response);
        assertNull(result, "Should return null when ProtocolException occurs");
    }

    @Test
    public void testGet429DelayMillis() throws ProtocolException {
        // Test with all headers present
        HttpResponse response = mock(HttpResponse.class);

        // Reset time 30 seconds in the future
        long currentTime = System.currentTimeMillis();
        long resetTime = currentTime / 1000 + 30;

        // Setup reset header
        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(resetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        // Setup date header with proper HTTP date format (RFC 1123)
        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String httpDateString = dateFormat.format(new Date(currentTime));

        when(dateHeader.getValue()).thenReturn(httpDateString);
        when(response.getHeader("Date")).thenReturn(dateHeader);

        // Delay should be approximately 30 seconds (30000ms) plus the 1000ms padding,
        // well within the (generous) 60 second max used here
        long delay = RetryUtil.get429DelayMillis(response, 60_000L);
        assertTrue(delay >= 30000 && delay <= 32000);

        // Test with missing rate limit header
        HttpResponse noResetResponse = mock(HttpResponse.class);
        when(noResetResponse.getFirstHeader("x-rate-limit-reset")).thenReturn(null);
        assertEquals(-1, RetryUtil.get429DelayMillis(noResetResponse, 60_000L));
    }

    @Test
    public void testGet429DelayMillisIsCappedByMaxDelay() throws ProtocolException {
        // Simulates a malicious/MitM server returning an x-rate-limit-reset far in the future
        // to try to force an excessively long thread sleep.
        HttpResponse response = mock(HttpResponse.class);

        long currentTime = System.currentTimeMillis();
        long resetTime = currentTime / 1000 + TimeUnit.DAYS.toSeconds(365); // 1 year in the future

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(resetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        long maxDelayMillis = 20_000L;
        long delay = RetryUtil.get429DelayMillis(response, maxDelayMillis);
        assertEquals(maxDelayMillis, delay);
    }

    @Test
    public void testGet429DelayMillisBelowMaxDelayIsUnaffected() throws ProtocolException {
        HttpResponse response = mock(HttpResponse.class);

        long currentTime = System.currentTimeMillis();
        long resetTime = currentTime / 1000 + 5; // 5 seconds in the future

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(resetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        // 5s wait + 1s buffer = ~6s, well under the 20s max, so the max should not kick in
        long delay = RetryUtil.get429DelayMillis(response, 20_000L);
        assertTrue(delay >= 6000 && delay <= 7000);
    }

    @Test
    public void testGet429DelayMillisCapWinsOverMinimumFloor() throws ProtocolException {
        // Even a "normal" (small, non-malicious) reset value must still respect an aggressively
        // small configured max, i.e. the safety ceiling always wins over MIN_RETRY_DELAY_MS.
        HttpResponse response = mock(HttpResponse.class);

        long currentTime = System.currentTimeMillis();
        long resetTime = currentTime / 1000 + 30;

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(resetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        long maxDelayMillis = 500L; // below MIN_RETRY_DELAY_MS (1000ms)
        assertEquals(maxDelayMillis, RetryUtil.get429DelayMillis(response, maxDelayMillis));
    }

    @Test
    public void testGet429DelayMillisWithOverflowingResetHeaderIsStillBounded() throws ProtocolException {
        // A malicious server could try to overflow the internal long math (resetLimit * 1000L) by
        // sending a value near Long.MAX_VALUE. Regardless of how that overflow resolves, the final
        // Math.min(..., maxDelayMillis) clamp must guarantee the result never exceeds maxDelayMillis.
        HttpResponse response = mock(HttpResponse.class);

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(Long.MAX_VALUE));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date()));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        long maxDelayMillis = 20_000L;
        long delay = RetryUtil.get429DelayMillis(response, maxDelayMillis);
        assertTrue(delay >= 0 && delay <= maxDelayMillis);
    }

    @Test
    public void testGet429DelayMillisWithExpiredResetTimeUsesMinimum() throws ProtocolException {
        // Reset time already in the past should fall back to MIN_RETRY_DELAY_MS, not a negative delay.
        HttpResponse response = mock(HttpResponse.class);

        long currentTime = System.currentTimeMillis();
        long resetTime = currentTime / 1000 - 3600; // 1 hour in the past

        Header resetHeader = mock(Header.class);
        when(resetHeader.getValue()).thenReturn(String.valueOf(resetTime));
        when(response.getFirstHeader("x-rate-limit-reset")).thenReturn(resetHeader);

        Header dateHeader = mock(Header.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        when(dateHeader.getValue()).thenReturn(dateFormat.format(new Date(currentTime)));
        when(response.getHeader("Date")).thenReturn(dateHeader);

        assertEquals(1000L, RetryUtil.get429DelayMillis(response, 20_000L));
    }

}

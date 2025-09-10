package com.okta.sdk.impl.retry;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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


    @Test(expectedExceptions = RuntimeException.class)
    public void testDateFromHeaderException() throws ProtocolException {
        // Test behavior when header causes exception
        HttpResponse response = mock(HttpResponse.class);
        when(response.getHeader("Date")).thenThrow(new ProtocolException("Test exception"));

        RetryUtil.dateFromHeader(response);
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

        // Delay should be approximately 30 seconds (30000ms) plus the 1000ms padding
        long delay = RetryUtil.get429DelayMillis(response);
        assertTrue(delay >= 30000 && delay <= 32000);

        // Test with missing rate limit header
        HttpResponse noResetResponse = mock(HttpResponse.class);
        when(noResetResponse.getFirstHeader("x-rate-limit-reset")).thenReturn(null);
        assertEquals(-1, RetryUtil.get429DelayMillis(noResetResponse));
    }



}

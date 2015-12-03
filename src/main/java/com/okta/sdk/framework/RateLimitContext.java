package com.okta.sdk.framework;

import com.okta.sdk.exceptions.SdkException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;

public class RateLimitContext {
    private HttpResponse httpResponse;

    public RateLimitContext(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    private String getHeaderValueString(String headerName) throws Exception {
        if (httpResponse == null) {
            throw new SdkException("No http response");
        }

        Header[] headers = httpResponse.getHeaders(headerName);
        if (headers.length > 0) {
            Header header = headers[0];
            return header.getValue();
        } else {
            throw new SdkException("No " + headerName + " header");
        }
    }

    private long getHeaderValueLong(String headerName) throws Exception {
        String headerString = getHeaderValueString(headerName);
        try {
            return Long.parseLong(headerString);
        } catch (Exception e){
            throw new SdkException("Error parsing " + headerName + " header");
        }
    }

    /**
     * @return The number of requests remaining in the current window
     * @throws Exception
     */
    public long getNumRequestsRemaining() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Remaining");
    }

    /**
     * @return When the next window starts, in Unix time. When the
     * next window starts, the server will reset the request count
     * @throws Exception
     */
    public long getNextWindowUnixTime() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Reset");
    }

    /**
     * @return When the next window starts, as a DateTime. When the
     * next window starts, the server will reset the request count
     * @throws Exception
     */
    public DateTime getNextWindowDateTime() throws Exception {
        Long unixTime = getNextWindowUnixTime();
        try {
            return new DateTime(unixTime * 1000L);
        } catch (Exception e) {
            throw new SdkException("Unable to convert X-Rate-Limit-Reset to DateTime");
        }
    }

    /**
     * @return The maximum number of requests allowed in a window
     * @throws Exception
     */
    public Long getRequestLimit() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Limit");
    }
}
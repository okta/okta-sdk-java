/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

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

    /**
     * @return The number of requests remaining in the current window.
     * @throws Exception if value does not exist.
     */
    public long getNumRequestsRemaining() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Remaining");
    }

    /**
     * Returns when the next window starts in Unix time.
     * Server will reset the request count when the next window starts.
     *
     * @return {@link Long}
     * @throws Exception if value does not exist.
     */
    public long getNextWindowUnixTime() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Reset");
    }

    /**
     * When the next window starts, as a DateTime. When the
     * next window starts, the server will reset the request count.
     *
     * @return {@link DateTime}
     * @throws Exception if value does not exist.
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
     * Returns the maximum number of requests allowed in a window.
     *
     * @return {@link Long}
     * @throws Exception if value does not exist.
     */
    public Long getRequestLimit() throws Exception {
        return getHeaderValueLong("X-Rate-Limit-Limit");
    }

    /**
     * Returns the string value of the specified header.
     *
     * @param headerName {@link String}
     * @return {@link String}
     * @throws Exception if the header was not found.
     */
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

    /**
     * Returns the long value of the specified header.
     *
     * @param headerName {@link String}
     * @return {@link Long}
     * @throws Exception if the header does not exist.
     */
    private long getHeaderValueLong(String headerName) throws Exception {
        String headerString = getHeaderValueString(headerName);
        try {
            return Long.parseLong(headerString);
        } catch (Exception e){
            throw new SdkException("Error parsing " + headerName + " header");
        }
    }

}

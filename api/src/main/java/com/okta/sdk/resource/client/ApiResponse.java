/*
 * Copyright (c) 2025-Present, Okta, Inc.
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
package com.okta.sdk.resource.client;

import java.util.List;
import java.util.Map;

/**
 * A stateless data object that represents a complete API response.
 * 
 * This class packages together everything from an HTTP response:
 * - The response body (deserialized data)
 * - The response headers (including Link headers for pagination)
 * - The HTTP status code (200, 404, etc.)
 * 
 * Unlike the old approach which stored headers in a shared HashMap,
 * ApiResponse is an immutable, thread-safe object that can be passed
 * around without side effects.
 * 
 * Example usage:
 * <pre>
 * ApiResponse&lt;List&lt;User&gt;&gt; response = apiClient.invokeAPIWithHttpInfo(...);
 * 
 * int status = response.getStatusCode();              // e.g., 200
 * List&lt;User&gt; users = response.getBody();             // The actual data
 * Map&lt;String, List&lt;String&gt;&gt; headers = response.getHeaders();  // Response headers
 * 
 * // Check for pagination
 * List&lt;String&gt; linkHeaders = headers.get("Link");
 * </pre>
 *
 * @param <T> The type of the response body (e.g., List&lt;User&gt;, Group, etc.)
 * @since 17.0.0
 */
public class ApiResponse<T> {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final T body;

    /**
     * Constructs a new ApiResponse with the given status code, headers, and body.
     *
     * @param statusCode the HTTP status code (e.g., 200, 404, 500)
     * @param headers the response headers as a map (header name → list of values)
     * @param body the deserialized response body (can be null for 204 No Content)
     */
    public ApiResponse(int statusCode, Map<String, List<String>> headers, T body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Gets the HTTP status code from the response.
     *
     * @return the status code (e.g., 200 for success, 404 for not found)
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the response headers.
     * 
     * Common headers include:
     * - "Link": Pagination links (e.g., rel="next")
     * - "Content-Type": Response content type
     * - "X-Rate-Limit-Remaining": API rate limit info
     *
     * @return a map of header names to lists of header values
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Gets the deserialized response body.
     * 
     * This is the actual data returned by the API, already converted
     * from JSON into Java objects.
     *
     * @return the response body, or null if the response was empty (204 No Content)
     */
    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", headersCount=" + (headers != null ? headers.size() : 0) +
                ", body=" + (body != null ? body.getClass().getSimpleName() : "null") +
                '}';
    }
}

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

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link ApiResponse}
 */
public class ApiResponseTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int statusCode = 200;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        List<String> body = Arrays.asList("item1", "item2");

        // Act
        ApiResponse<List<String>> response = new ApiResponse<>(statusCode, headers, body);

        // Assert
        assertEquals(response.getStatusCode(), statusCode);
        assertEquals(response.getHeaders(), headers);
        assertEquals(response.getBody(), body);
    }

    @Test
    public void testWithNullBody() {
        // Arrange
        int statusCode = 204; // No Content
        Map<String, List<String>> headers = new HashMap<>();

        // Act
        ApiResponse<List<String>> response = new ApiResponse<>(statusCode, headers, null);

        // Assert
        assertEquals(response.getStatusCode(), statusCode);
        assertEquals(response.getHeaders(), headers);
        assertNull(response.getBody());
    }

    @Test
    public void testWithEmptyHeaders() {
        // Arrange
        Map<String, List<String>> emptyHeaders = new HashMap<>();
        List<String> body = Arrays.asList("data");

        // Act
        ApiResponse<List<String>> response = new ApiResponse<>(200, emptyHeaders, body);

        // Assert
        assertNotNull(response.getHeaders());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void testWithNullHeaders() {
        // Arrange
        List<String> body = Arrays.asList("data");

        // Act
        ApiResponse<List<String>> response = new ApiResponse<>(200, null, body);

        // Assert
        assertNull(response.getHeaders());
    }

    @Test
    public void testHeadersAreMutable() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Link", Arrays.asList("<https://example.com>; rel=\"next\""));
        List<String> body = Arrays.asList("data");
        ApiResponse<List<String>> response = new ApiResponse<>(200, headers, body);

        // Act - modify the original headers map
        headers.put("NewHeader", Arrays.asList("value"));

        // Assert - response should reflect the change (no defensive copy)
        assertEquals(response.getHeaders().size(), 2);
        assertNotNull(response.getHeaders().get("NewHeader"));
    }

    @Test
    public void testBodyIsMutable() {
        // Arrange
        List<String> body = new ArrayList<>(Arrays.asList("item1"));
        ApiResponse<List<String>> response = new ApiResponse<>(200, new HashMap<>(), body);

        // Act - modify the original body list
        body.add("item2");

        // Assert - response should reflect the change (no defensive copy)
        assertEquals(response.getBody().size(), 2);
        assertTrue(response.getBody().contains("item2"));
    }

    @Test
    public void testDifferentStatusCodes() {
        // Test various status codes
        int[] statusCodes = {200, 201, 204, 400, 404, 500};
        
        for (int code : statusCodes) {
            ApiResponse<String> response = new ApiResponse<>(code, null, null);
            assertEquals(response.getStatusCode(), code);
        }
    }

    @Test
    public void testComplexHeaderStructure() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Link", Arrays.asList(
            "<https://example.com?page=2>; rel=\"next\"",
            "<https://example.com?page=1>; rel=\"prev\""
        ));
        headers.put("X-Rate-Limit-Remaining", Arrays.asList("999"));
        headers.put("Cache-Control", Arrays.asList("no-cache", "no-store"));

        // Act
        ApiResponse<Void> response = new ApiResponse<>(200, headers, null);

        // Assert
        assertEquals(response.getHeaders().get("Link").size(), 2);
        assertEquals(response.getHeaders().get("X-Rate-Limit-Remaining").size(), 1);
        assertEquals(response.getHeaders().get("Cache-Control").size(), 2);
    }
}

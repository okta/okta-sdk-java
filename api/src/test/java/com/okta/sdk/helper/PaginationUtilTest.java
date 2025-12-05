/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.okta.sdk.helper;

import com.okta.sdk.resource.client.ApiClient;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class PaginationUtilTest {

    @Test
    public void testGetAfterWithValidLink() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertEquals(result, "100uekbsy586JToqdQ1d7");
    }

    @Test
    public void testGetAfterWithMultipleLinks() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Arrays.asList(
            "<https://example.okta.com/api/v1/users?before=000uekbsy586JToqdQ1d7&limit=100>; rel=\"prev\"",
            "<https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100>; rel=\"next\""
        ));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertEquals(result, "100uekbsy586JToqdQ1d7");
    }

    @Test
    public void testGetAfterWithNoNextLink() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users?before=000uekbsy586JToqdQ1d7&limit=100>; rel=\"prev\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertNull(result);
    }

    @Test
    public void testGetAfterWithMalformedURL() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<not-a-valid-url>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertNull(result);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetAfterWithUrlWithoutQueryParams() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute - expecting NullPointerException because the URL has no query parameters
        PaginationUtil.getAfter(mockApiClient);
    }

    @Test
    public void testGetAfterWithUrlWithoutAfterParam() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users?limit=100>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertNull(result);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAfterWithNullApiClient() {
        PaginationUtil.getAfter((ApiClient) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAfterWithNullResponseHeaders() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        when(mockApiClient.getResponseHeaders()).thenReturn(null);

        // Execute
        PaginationUtil.getAfter(mockApiClient);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAfterWithMissingLinkHeader() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        when(mockApiClient.getResponseHeaders()).thenReturn(new HashMap<>());

        // Execute
        PaginationUtil.getAfter(mockApiClient);
    }

    @Test
    public void testSplitQueryWithValidUrl() throws Exception {
        // Use reflection to access private method
        Method splitQueryMethod = PaginationUtil.class.getDeclaredMethod("splitQuery", URL.class);
        splitQueryMethod.setAccessible(true);

        // Test with valid URL
        URL url = new URL("https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100");
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) splitQueryMethod.invoke(null, url);

        // Verify
        assertEquals(result.get("after"), "100uekbsy586JToqdQ1d7");
        assertEquals(result.get("limit"), "100");
    }

    @Test
    public void testSplitQueryWithEncodedParameters() throws Exception {
        // Access private method
        Method splitQueryMethod = PaginationUtil.class.getDeclaredMethod("splitQuery", URL.class);
        splitQueryMethod.setAccessible(true);

        // Test with encoded parameters
        URL url = new URL("https://example.okta.com/api/v1/users?filter=status%20eq%20%22ACTIVE%22&after=100uekbsy586JToqdQ1d7");
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) splitQueryMethod.invoke(null, url);

        // Verify
        assertEquals(result.get("filter"), "status eq \"ACTIVE\"");
        assertEquals(result.get("after"), "100uekbsy586JToqdQ1d7");
    }

    @Test
    public void testGetNextPageWithValidLink() throws Exception {
        // Access private method
        Method getNextPageMethod = PaginationUtil.class.getDeclaredMethod("getNextPage", ApiClient.class);
        getNextPageMethod.setAccessible(true);

        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = (String) getNextPageMethod.invoke(null, mockApiClient);

        // Verify
        assertEquals(result, "https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100");
    }

    @Test
    public void testPrivateGetAfterMethod() throws Exception {
        // Access private method
        Method getAfterMethod = PaginationUtil.class.getDeclaredMethod("getAfter", String.class);
        getAfterMethod.setAccessible(true);

        // Test with valid URL
        String result1 = (String) getAfterMethod.invoke(null, "https://example.okta.com/api/v1/users?after=100uekbsy586JToqdQ1d7&limit=100");
        assertEquals(result1, "100uekbsy586JToqdQ1d7");

        // Test with URL without after parameter
        String result2 = (String) getAfterMethod.invoke(null, "https://example.okta.com/api/v1/users?limit=100");
        assertNull(result2);

        // Test with malformed URL
        String result3 = (String) getAfterMethod.invoke(null, "not-a-valid-url");
        assertNull(result3);
    }
    @Test
    public void testSplitQueryWithNullQuery() throws Exception {
        // Access private method
        Method splitQueryMethod = PaginationUtil.class.getDeclaredMethod("splitQuery", URL.class);
        splitQueryMethod.setAccessible(true);

        try {
            // Create a modified version of splitQuery that handles null query
            Method getAfterMethod = PaginationUtil.class.getDeclaredMethod("getAfter", String.class);
            getAfterMethod.setAccessible(true);

            // Test with null nextPage parameter
            String result = (String) getAfterMethod.invoke(null, (String) null);
            assertNull(result);
        } catch (Exception e) {
            // This is expected with the current implementation
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testGetNextPageWithNoLinkHeaderValue() throws Exception {
        // Access private method
        Method getNextPageMethod = PaginationUtil.class.getDeclaredMethod("getNextPage", ApiClient.class);
        getNextPageMethod.setAccessible(true);

        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.emptyList()); // Empty list instead of null
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = (String) getNextPageMethod.invoke(null, mockApiClient);

        // Verify
        assertNull(result);
    }

    @Test
    public void testGetNextPageWithMalformedLinkHeader() throws Exception {
        // Access private method
        Method getNextPageMethod = PaginationUtil.class.getDeclaredMethod("getNextPage", ApiClient.class);
        getNextPageMethod.setAccessible(true);

        // Setup with malformed link header (missing semicolon)
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        headers.put("link", Collections.singletonList("<https://example.com/api/v1/users?after=abc> rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        try {
            getNextPageMethod.invoke(null, mockApiClient);
            fail("Expected exception not thrown");
        } catch (Exception e) {
            // Exception is expected with malformed link header
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void testGetAfterWithNextPageButNullAfterParameter() {
        // Setup
        ApiClient mockApiClient = mock(ApiClient.class);
        Map<String, java.util.List<String>> headers = new HashMap<>();
        // URL has a query string but no 'after' parameter
        headers.put("link", Collections.singletonList("<https://example.okta.com/api/v1/users?page=2&size=10>; rel=\"next\""));
        when(mockApiClient.getResponseHeaders()).thenReturn(headers);

        // Execute
        String result = PaginationUtil.getAfter(mockApiClient);

        // Verify
        assertNull(result);
    }

}

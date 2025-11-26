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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link PagedIterator}
 */
public class PagedIteratorTest {

    @Test
    public void testIteratorWithSinglePage() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            List<String> items = Arrays.asList("user1", "user2", "user3");
            return new ApiResponse<>(200, headers, items);
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert
        assertEquals(collected.size(), 3);
        assertEquals(collected, Arrays.asList("user1", "user2", "user3"));
    }

    @Test
    public void testIteratorWithMultiplePages() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            int page = callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            
            switch (page) {
                case 1:
                    headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
                    return new ApiResponse<>(200, headers, Arrays.asList("page1-item1", "page1-item2"));
                case 2:
                    headers.put("Link", Arrays.asList("<https://example.com?after=def>; rel=\"next\""));
                    return new ApiResponse<>(200, headers, Arrays.asList("page2-item1", "page2-item2"));
                default:
                    return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("page3-item1"));
            }
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert
        assertEquals(callCount.get(), 3);
        assertEquals(collected.size(), 5);
        assertEquals(collected.get(0), "page1-item1");
        assertEquals(collected.get(4), "page3-item1");
    }

    @Test
    public void testIteratorWithEmptyResult() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), new ArrayList<>());

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);

        // Assert
        assertFalse(iterator.hasNext());
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testNextOnExhaustedIterator() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), new ArrayList<>());
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);

        // Act & Assert
        iterator.next(); // Should throw NoSuchElementException
    }

    @Test
    public void testIteratorWithNullBody() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), null);

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);

        // Assert
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorEarlyBreak() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
            return new ApiResponse<>(200, headers, Arrays.asList("item1", "item2", "item3"));
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
            if (collected.size() == 2) {
                break; // Early break
            }
        }

        // Assert
        assertEquals(callCount.get(), 1); // Only first page fetched
        assertEquals(collected.size(), 2);
    }

    @Test
    public void testLinkHeaderParsingCaseInsensitive() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            int page = callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            
            if (page == 1) {
                // Use lowercase "link" header
                headers.put("link", Arrays.asList("<https://example.com?page=2>; rel=\"next\""));
                return new ApiResponse<>(200, headers, Arrays.asList("item1"));
            } else {
                return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item2"));
            }
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert
        assertEquals(callCount.get(), 2);
        assertEquals(collected.size(), 2);
    }

    @Test
    public void testLinkHeaderWithMultipleRels() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            int page = callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            
            if (page == 1) {
                // First page has multiple rel values including "next"
                headers.put("Link", Arrays.asList(
                    "<https://example.com?page=1>; rel=\"prev\"",
                    "<https://example.com?page=3>; rel=\"next\"",
                    "<https://example.com?page=1>; rel=\"first\""
                ));
                return new ApiResponse<>(200, headers, Arrays.asList("page1"));
            } else {
                // Second page has no next link
                return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("page2"));
            }
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert - Should fetch both pages because first page had a "next" link
        assertEquals(callCount.get(), 2);
        assertEquals(collected.size(), 2);
    }

    @Test
    public void testLinkHeaderWithNoNextRel() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Link", Arrays.asList("<https://example.com?page=1>; rel=\"prev\""));
            return new ApiResponse<>(200, headers, Arrays.asList("item1"));
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert
        assertEquals(collected.size(), 1);
    }

    @Test
    public void testMultipleHasNextCalls() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            return new ApiResponse<>(200, headers, Arrays.asList("item1", "item2"));
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        
        // Assert - Multiple hasNext() calls should be idempotent
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), "item1");
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), "item2");
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testBufferManagement() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            int page = callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            
            if (page < 3) {
                headers.put("Link", Arrays.asList("<https://example.com?page=" + (page + 1) + ">; rel=\"next\""));
            }
            return new ApiResponse<>(200, headers, Arrays.asList("page" + page + "-item1", "page" + page + "-item2"));
        };

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        
        // Assert - Verify items are buffered and fetched as needed
        assertEquals(callCount.get(), 0); // No calls yet
        assertTrue(iterator.hasNext());
        assertEquals(callCount.get(), 1); // First call made
        assertEquals(iterator.next(), "page1-item1");
        assertEquals(callCount.get(), 1); // Still on first page buffer
        assertEquals(iterator.next(), "page1-item2");
        assertEquals(callCount.get(), 1); // Still on first page buffer
        assertTrue(iterator.hasNext());
        assertEquals(callCount.get(), 2); // Second call made
        assertEquals(iterator.next(), "page2-item1");
    }

    @Test
    public void testWithNullHeaders() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, null, Arrays.asList("item1"));

        // Act
        PagedIterator<String> iterator = new PagedIterator<>(fetcher);
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        // Assert
        assertEquals(collected.size(), 1);
        assertEquals(collected.get(0), "item1");
    }
}

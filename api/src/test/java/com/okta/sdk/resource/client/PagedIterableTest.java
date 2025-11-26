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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link PagedIterable}
 */
public class PagedIterableTest {

    @Test
    public void testIterableCreatesNewIteratorPerLoop() {
        // Arrange
        AtomicInteger fetchCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            fetchCount.incrementAndGet();
            return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item1", "item2"));
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act - Loop twice
        List<String> collected1 = new ArrayList<>();
        for (String item : iterable) {
            collected1.add(item);
        }

        List<String> collected2 = new ArrayList<>();
        for (String item : iterable) {
            collected2.add(item);
        }

        // Assert
        assertEquals(fetchCount.get(), 2); // Two separate fetches
        assertEquals(collected1, Arrays.asList("item1", "item2"));
        assertEquals(collected2, Arrays.asList("item1", "item2"));
    }

    @Test
    public void testIterableWithSinglePage() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            return new ApiResponse<>(200, headers, Arrays.asList("user1", "user2", "user3"));
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }

        // Assert
        assertEquals(collected.size(), 3);
        assertEquals(collected, Arrays.asList("user1", "user2", "user3"));
    }

    @Test
    public void testIterableWithMultiplePages() {
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
                    return new ApiResponse<>(200, headers, Arrays.asList("page2-item1"));
                default:
                    return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("page3-item1", "page3-item2"));
            }
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }

        // Assert
        assertEquals(callCount.get(), 3);
        assertEquals(collected.size(), 5);
        assertEquals(collected.get(0), "page1-item1");
        assertEquals(collected.get(4), "page3-item2");
    }

    @Test
    public void testIterableWithEmptyResult() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), new ArrayList<>());
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }

        // Assert
        assertTrue(collected.isEmpty());
    }

    @Test
    public void testIterableEarlyBreak() {
        // Arrange
        AtomicInteger callCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
            return new ApiResponse<>(200, headers, Arrays.asList("item1", "item2", "item3", "item4", "item5"));
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
            if (collected.size() == 3) {
                break; // Early break
            }
        }

        // Assert
        assertEquals(callCount.get(), 1); // Only first page fetched
        assertEquals(collected.size(), 3);
    }

    @Test
    public void testIterableThreadSafety() throws InterruptedException {
        // Arrange
        AtomicInteger totalFetches = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            totalFetches.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            if (nextUrl == null) {
                headers.put("Link", Arrays.asList("<https://example.com?page=2>; rel=\"next\""));
                return new ApiResponse<>(200, headers, Arrays.asList("item1", "item2"));
            } else {
                return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item3", "item4"));
            }
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act - Iterate in multiple threads simultaneously
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        List<List<String>> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    List<String> collected = new ArrayList<>();
                    for (String item : iterable) {
                        collected.add(item);
                    }
                    results.add(collected);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        assertEquals(results.size(), numThreads);
        for (List<String> result : results) {
            assertEquals(result.size(), 4);
            assertTrue(result.contains("item1"));
            assertTrue(result.contains("item4"));
        }
        // Each thread should have fetched pages independently
        assertEquals(totalFetches.get(), numThreads * 2); // 10 threads * 2 pages each
    }

    @Test
    public void testIterableWithStream() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            if (nextUrl == null) {
                headers.put("Link", Arrays.asList("<https://example.com?page=2>; rel=\"next\""));
                return new ApiResponse<>(200, headers, Arrays.asList("apple", "banana"));
            } else {
                return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("cherry", "date"));
            }
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        iterable.forEach(collected::add);

        // Assert
        assertEquals(collected.size(), 4);
        assertTrue(collected.contains("apple"));
        assertTrue(collected.contains("date"));
    }

    @Test
    public void testIterableMultipleIterations() {
        // Arrange
        AtomicInteger fetchCount = new AtomicInteger(0);
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            fetchCount.incrementAndGet();
            return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item1", "item2"));
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        Iterator<String> iter1 = iterable.iterator();
        Iterator<String> iter2 = iterable.iterator();
        Iterator<String> iter3 = iterable.iterator();

        // Assert - Each iterator is independent
        assertTrue(iter1.hasNext());
        assertTrue(iter2.hasNext());
        assertTrue(iter3.hasNext());
        
        assertEquals(iter1.next(), "item1");
        assertEquals(iter2.next(), "item1");
        assertEquals(iter3.next(), "item1");
        
        assertEquals(fetchCount.get(), 3); // Each iterator made its own fetch
    }

    @Test
    public void testIterableWithNullBody() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), null);
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }

        // Assert
        assertTrue(collected.isEmpty());
    }

    @Test
    public void testIterablePreservesOriginalFetcher() {
        // Arrange
        List<String> fetchedUrls = new CopyOnWriteArrayList<>();
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            fetchedUrls.add(nextUrl == null ? "FIRST" : nextUrl);
            Map<String, List<String>> headers = new HashMap<>();
            if (nextUrl == null) {
                headers.put("Link", Arrays.asList("<https://example.com?page=2>; rel=\"next\""));
            }
            return new ApiResponse<>(200, headers, Arrays.asList("data"));
        };
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act
        for (String item : iterable) {
            // consume
        }

        // Assert
        assertEquals(fetchedUrls.size(), 2);
        assertEquals(fetchedUrls.get(0), "FIRST");
        assertEquals(fetchedUrls.get(1), "https://example.com?page=2");
    }

    @Test
    public void testIterableReuseAfterExhaustion() {
        // Arrange
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) ->
            new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item1", "item2"));
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);

        // Act - First iteration
        List<String> collected1 = new ArrayList<>();
        for (String item : iterable) {
            collected1.add(item);
        }

        // Act - Second iteration after first is exhausted
        List<String> collected2 = new ArrayList<>();
        for (String item : iterable) {
            collected2.add(item);
        }

        // Assert
        assertEquals(collected1, collected2);
        assertEquals(collected1.size(), 2);
    }

    @Test
    public void testIterableWithComplexDataTypes() {
        // Arrange
        Function<String, ApiResponse<List<Map<String, String>>>> fetcher = (nextUrl) -> {
            List<Map<String, String>> data = new ArrayList<>();
            Map<String, String> user1 = new HashMap<>();
            user1.put("id", "001");
            user1.put("name", "Alice");
            data.add(user1);
            
            Map<String, String> user2 = new HashMap<>();
            user2.put("id", "002");
            user2.put("name", "Bob");
            data.add(user2);
            
            return new ApiResponse<>(200, new HashMap<>(), data);
        };
        PagedIterable<Map<String, String>> iterable = new PagedIterable<>(fetcher);

        // Act
        List<Map<String, String>> collected = new ArrayList<>();
        for (Map<String, String> item : iterable) {
            collected.add(item);
        }

        // Assert
        assertEquals(collected.size(), 2);
        assertEquals(collected.get(0).get("name"), "Alice");
        assertEquals(collected.get(1).get("name"), "Bob");
    }
}

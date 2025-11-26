package com.okta.sdk.resource.client;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Standalone test for the new pagination implementation.
 * Run with: javac -cp target/classes PaginationTest.java && java -cp .:target/classes -ea PaginationTest
 */
public class PaginationTest {

    public static void main(String[] args) {
        System.out.println("🧪 Testing Pagination Implementation\n");
        System.out.println("=====================================\n");
        
        PaginationTest test = new PaginationTest();
        
        try {
            test.testApiResponseCreation();
            test.testPagedIterableSinglePage();
            test.testPagedIterableMultiplePages();
            test.testPagedIterableEmptyResult();
            test.testPagedIterableEarlyBreak();
            test.testPagedIterableThreadSafety();
            test.testPagedIteratorLinkHeaderParsing();
            test.testPagedIterableWithNullBody();
            test.testMultipleIterationsOnSameIterable();
            
            System.out.println("\n=====================================");
            System.out.println("✅ All tests passed!");
            System.out.println("=====================================\n");
        } catch (AssertionError e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void testApiResponseCreation() {
        System.out.println("1. Testing ApiResponse creation...");
        
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
        
        List<String> body = Arrays.asList("item1", "item2", "item3");
        
        ApiResponse<List<String>> response = new ApiResponse<>(200, headers, body);
        
        assert response.getStatusCode() == 200 : "Status code should be 200";
        assert response.getBody().equals(body) : "Body should match";
        assert response.getHeaders().equals(headers) : "Headers should match";
        assert response.getHeaders().get("Link") != null : "Link header should exist";
        
        System.out.println("   ✓ ApiResponse correctly stores body, headers, and status");
    }

    private void testPagedIterableSinglePage() {
        System.out.println("\n2. Testing PagedIterable with single page...");
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            List<String> items = Arrays.asList("user1", "user2", "user3");
            return new ApiResponse<>(200, headers, items);
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }
        
        assert collected.size() == 3 : "Should collect 3 items";
        assert collected.get(0).equals("user1") : "First item should be user1";
        
        System.out.println("   ✓ Single page iteration works (3 items collected)");
    }

    private void testPagedIterableMultiplePages() {
        System.out.println("\n3. Testing PagedIterable with multiple pages...");
        
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
                    return new ApiResponse<>(200, headers, Arrays.asList("page3-item1"));
            }
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }
        
        assert callCount.get() == 3 : "Should have made 3 API calls, but made " + callCount.get();
        assert collected.size() == 5 : "Should have collected 5 items, but got " + collected.size();
        assert collected.get(0).equals("page1-item1");
        assert collected.get(4).equals("page3-item1");
        
        System.out.println("   ✓ Multiple pages fetched automatically (3 pages, 5 total items)");
    }

    private void testPagedIterableEmptyResult() {
        System.out.println("\n4. Testing PagedIterable with empty result...");
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            return new ApiResponse<>(200, new HashMap<>(), new ArrayList<>());
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }
        
        assert collected.size() == 0 : "Should have no items";
        
        System.out.println("   ✓ Empty results handled correctly");
    }

    private void testPagedIterableEarlyBreak() {
        System.out.println("\n5. Testing PagedIterable with early break...");
        
        AtomicInteger callCount = new AtomicInteger(0);
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            callCount.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
            
            return new ApiResponse<>(200, headers, Arrays.asList("item1", "item2", "item3"));
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        int count = 0;
        for (String item : iterable) {
            count++;
            if (count == 5) {
                break;
            }
        }
        
        assert count == 5 : "Should have collected 5 items";
        assert callCount.get() == 2 : "Should have made 2 API calls, but made " + callCount.get();
        
        System.out.println("   ✓ Early break works (stopped at 5 items, 2 pages fetched)");
    }

    private void testPagedIterableThreadSafety() throws InterruptedException {
        System.out.println("\n6. Testing PagedIterable thread-safety...");
        
        AtomicInteger totalCalls = new AtomicInteger(0);
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            totalCalls.incrementAndGet();
            Map<String, List<String>> headers = new HashMap<>();
            
            if (nextUrl == null) {
                headers.put("Link", Arrays.asList("<https://example.com?after=abc>; rel=\"next\""));
                return new ApiResponse<>(200, headers, Arrays.asList("page1-item1", "page1-item2"));
            } else {
                return new ApiResponse<>(200, headers, Arrays.asList("page2-item1", "page2-item2"));
            }
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<Thread> threads = new ArrayList<>();
        List<List<String>> results = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                List<String> threadResult = new ArrayList<>();
                for (String item : iterable) {
                    threadResult.add(item);
                }
                results.add(threadResult);
            });
            threads.add(thread);
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        assert results.size() == 5 : "Should have 5 result lists";
        for (List<String> result : results) {
            assert result.size() == 4 : "Each thread should have collected 4 items, but got " + result.size();
            assert result.get(0).equals("page1-item1");
            assert result.get(3).equals("page2-item2");
        }
        
        assert totalCalls.get() == 10 : "Should have made 10 total API calls, but made " + totalCalls.get();
        
        System.out.println("   ✓ Thread-safety verified (5 concurrent threads, no collisions)");
    }

    private void testPagedIteratorLinkHeaderParsing() {
        System.out.println("\n7. Testing Link header parsing...");
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            Map<String, List<String>> headers = new HashMap<>();
            
            if (nextUrl == null) {
                headers.put("Link", Arrays.asList(
                    "<https://example.okta.com/api/v1/users?after=abc123>; rel=\"next\""
                ));
                return new ApiResponse<>(200, headers, Arrays.asList("item1"));
            } else if (nextUrl.contains("abc123")) {
                headers.put("Link", Arrays.asList(
                    "<https://example.okta.com/api/v1/users?after=abc123>; rel=\"self\", " +
                    "<https://example.okta.com/api/v1/users?after=def456>; rel=\"next\""
                ));
                return new ApiResponse<>(200, headers, Arrays.asList("item2"));
            } else {
                return new ApiResponse<>(200, headers, Arrays.asList("item3"));
            }
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }
        
        assert collected.size() == 3 : "Should have collected 3 items";
        
        System.out.println("   ✓ Link headers parsed correctly (3 pages traversed)");
    }

    private void testPagedIterableWithNullBody() {
        System.out.println("\n8. Testing PagedIterable with null body...");
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            return new ApiResponse<>(204, new HashMap<>(), null);
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> collected = new ArrayList<>();
        for (String item : iterable) {
            collected.add(item);
        }
        
        assert collected.size() == 0 : "Should handle null body gracefully";
        
        System.out.println("   ✓ Null body handled gracefully");
    }

    private void testMultipleIterationsOnSameIterable() {
        System.out.println("\n9. Testing multiple iterations on same iterable...");
        
        AtomicInteger callCount = new AtomicInteger(0);
        
        Function<String, ApiResponse<List<String>>> fetcher = (nextUrl) -> {
            callCount.incrementAndGet();
            return new ApiResponse<>(200, new HashMap<>(), Arrays.asList("item1", "item2"));
        };
        
        PagedIterable<String> iterable = new PagedIterable<>(fetcher);
        
        List<String> firstResult = new ArrayList<>();
        for (String item : iterable) {
            firstResult.add(item);
        }
        
        int callsAfterFirst = callCount.get();
        
        List<String> secondResult = new ArrayList<>();
        for (String item : iterable) {
            secondResult.add(item);
        }
        
        assert firstResult.equals(secondResult) : "Both iterations should produce same results";
        assert callCount.get() == callsAfterFirst * 2 : "Should have made twice as many calls";
        
        System.out.println("   ✓ Multiple iterations work independently (each gets new iterator)");
    }
}

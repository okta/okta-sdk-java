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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The new stateful, but *isolated*, iterator for pagination.
 *
 * This object is created for each loop and holds the state
 * (the next page URL and the current page's items) for that
 * loop *only*. It is not shared, making it thread-safe.
 *
 * @param <T> The type of item in the collection (e.g., User)
 */
class PagedIterator<T> implements Iterator<T> {

    // Regex to find the "next" link in a Link header
    private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");
    
    private final Function<String, ApiResponse<List<T>>> pageFetcher;
    
    // --- THIS IS THE ISOLATED STATE ---
    private String nextUrl = null;
    private boolean isFirstPage = true;
    private Queue<T> itemBuffer = new LinkedList<>();
    // ---

    /**
     * Constructs a new PagedIterator with the given page fetching strategy.
     *
     * @param pageFetcher A function that takes a next URL (or null for the first page)
     *                    and returns an ApiResponse containing a list of items and headers.
     */
    public PagedIterator(Function<String, ApiResponse<List<T>>> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    @Override
    public boolean hasNext() {
        // 1. If we have items in our buffer, we're good.
        if (!itemBuffer.isEmpty()) {
            return true;
        }

        // 2. If it's the first page OR we have a next link,
        //    we must try to fetch the next page.
        if (isFirstPage || nextUrl != null) {
            fetchNextPage();
        }

        // 3. After fetching, check the buffer again.
        //    If it's still empty, we've reached the end.
        return !itemBuffer.isEmpty();
    }

    @Override
    public T next() {
        // hasNext() ensures the buffer is populated if needed.
        if (!hasNext()) {
            throw new NoSuchElementException("No more items in pagination.");
        }
        // Return the next item from the current page's buffer.
        return itemBuffer.poll();
    }

    /**
     * The main engine. Calls the fetcher "strategy",
     * fills the buffer, and saves the next link.
     */
    private void fetchNextPage() {
        // Call the "strategy" function from API class
        ApiResponse<List<T>> response = pageFetcher.apply(isFirstPage ? null : nextUrl);
        
        // Add all items from the response body to our buffer
        if (response.getBody() != null) {
            itemBuffer.addAll(response.getBody());
        }

        // Update our *private* state with the next link from the headers
        this.nextUrl = parseNextLinkFromHeaders(response.getHeaders());
        this.isFirstPage = false;
    }

    /**
     * Helper to parse the 'Link' header for rel="next"
     *
     * @param headers The response headers map
     * @return The next URL if found, null otherwise
     */
    private String parseNextLinkFromHeaders(Map<String, List<String>> headers) {
        if (headers == null) return null;
        
        // Try both "Link" and "link" as HTTP headers can vary in case
        List<String> linkHeaders = headers.get("Link");
        if (linkHeaders == null || linkHeaders.isEmpty()) {
            linkHeaders = headers.get("link");
        }
        if (linkHeaders == null || linkHeaders.isEmpty()) {
            return null;
        }
        
        for (String linkHeader : linkHeaders) {
            Matcher matcher = NEXT_LINK_PATTERN.matcher(linkHeader);
            if (matcher.find()) {
                String url = matcher.group(1);
                return url; // The URL
            }
        }
        
        return null; // No "next" link found
    }
}

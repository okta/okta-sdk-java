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
import java.util.List;
import java.util.function.Function;

/**
 * A new lazy, paginated iterable.
 *
 * This class is a "recipe" for fetching paginated data. It does no
 * work until the iterator() method is called (e.g., by a for-each loop).
 *
 * Each call to iterator() creates a new, independent PagedIterator instance,
 * ensuring thread-safety and isolation of pagination state.
 *
 * @param <T> The type of item in the collection (e.g., User)
 */
public class PagedIterable<T> implements Iterable<T> {

    /**
     * The "strategy" or "recipe" for fetching one page.
     * The String input is the 'nextUrl' (or null for the first page).
     * The ApiResponse output contains the page of results.
     */
    private final Function<String, ApiResponse<List<T>>> pageFetcher;

    /**
     * Constructs a new PagedIterable with the given page fetching strategy.
     *
     * @param pageFetcher A function that takes a next URL (or null for the first page)
     *                    and returns an ApiResponse containing a list of items and headers.
     */
    public PagedIterable(Function<String, ApiResponse<List<T>>> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    /**
     * Creates a new, stateful iterator.
     * This is the key to thread-safety: every loop gets its
     * own private iterator to manage its own private state.
     *
     * @return A new PagedIterator instance for this iteration.
     */
    @Override
    public Iterator<T> iterator() {
        return new PagedIterator<>(pageFetcher);
    }
}

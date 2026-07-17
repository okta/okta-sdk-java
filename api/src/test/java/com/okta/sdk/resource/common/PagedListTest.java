/*
 * Copyright 2026-Present Okta, Inc.
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
package com.okta.sdk.resource.common;

import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link PagedList}, in particular {@link PagedList#constructPagedList}.
 *
 * OKTA-1217985: SDK list methods must always return a {@link PagedList}, even when a filter (e.g. {@code q})
 * narrows the result to a single page and the response has no {@code Link} header. Consumers that cast the
 * result to {@code PagedList} would otherwise get a {@code ClassCastException}.
 */
public class PagedListTest {

    @Test
    public void constructPagedList_withNoLinkHeader_returnsPagedListNotPlainList() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);

        List<String> value = Arrays.asList("a", "b");
        Object result = PagedList.constructPagedList(response, value);

        assertTrue(result instanceof PagedList, "expected a PagedList even without a Link header");
        PagedList<?> pagedList = (PagedList<?>) result;
        assertEquals(pagedList.size(), 2);
        assertFalse(pagedList.hasMoreItems());
    }

    @Test
    public void constructPagedList_withNextLinkHeader_returnsPagedListWithNextPage() {
        BasicHttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader("link", "<https://example.okta.com/api/v1/users?after=abc123>; rel=\"next\"");

        List<String> value = Arrays.asList("a", "b", "c");
        Object result = PagedList.constructPagedList(response, value);

        assertTrue(result instanceof PagedList);
        PagedList<?> pagedList = (PagedList<?>) result;
        assertEquals(pagedList.size(), 3);
        assertTrue(pagedList.hasMoreItems());
        assertEquals(pagedList.getAfter(), "abc123");
    }

    @Test
    public void constructPagedList_withSelfLinkOnly_returnsPagedListWithNoMoreItems() {
        BasicHttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader("link", "<https://example.okta.com/api/v1/users?after=xyz>; rel=\"self\"");

        List<String> value = Arrays.asList("a");
        Object result = PagedList.constructPagedList(response, value);

        assertTrue(result instanceof PagedList);
        PagedList<?> pagedList = (PagedList<?>) result;
        assertEquals(pagedList.getSelf(), "https://example.okta.com/api/v1/users?after=xyz");
        assertFalse(pagedList.hasMoreItems());
    }

    @Test
    public void constructPagedList_withAlreadyPagedList_returnsSameInstance() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        PagedList<String> existing = new PagedList<>(Arrays.asList("a"), null, null, null);

        Object result = PagedList.constructPagedList(response, existing);

        assertTrue(result == existing);
    }
}

/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.resource

import com.okta.sdk.impl.ds.InternalDataStore
import org.mockito.Mockito
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link AbstractCollectionResource}.
 */
class AbstractCollectionResourceTest {

    @Test
    void testPagedCollection() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def page1 = createTestPage(0, 200, "https://example.com/resource?nextPage=1")
        def page2 = createTestPage(200, 200, "https://example.com/resource?nextPage=2")
        def page3 = createTestPage(400, 13, null)

        def collectionResource1 = new TestCollectionResource(ds, page1)
        expectPage(page1, ds)
        def collectionResource2 = new TestCollectionResource(ds, page2)
        when(ds.getResource(collectionResource1.getNextPageUrl(), TestCollectionResource)).thenReturn(collectionResource2)
        expectPage(page2, ds)
        def collectionResource3 = new TestCollectionResource(ds, page3)
        when(ds.getResource(collectionResource2.getNextPageUrl(), TestCollectionResource)).thenReturn(collectionResource3)
        expectPage(page3, ds)

        verifyCollection(new TestCollectionResource(ds, page1), 413)
    }

    @Test
    void testSinglePagedCollection() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def page1 = createTestPage(0, 13, null)

        expectPage(page1, ds)

        verifyCollection(new TestCollectionResource(ds, page1), 13)
    }

    @Test
    void testSamePageForIteratorAndSingle() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def page1 = createTestPage(0, 1, null)

        expectPage(page1, ds)

        def collectionResource = spy(new TestCollectionResource(ds, page1))

        collectionResource.iterator().hasNext()
        def single = collectionResource.single()
        assertThat single.getName(), is("Name-0")

        // make sure the current page is used for the call to the first iterator, and then again to the call to single()
        verify(collectionResource, times(2)).getCurrentPage()
    }

    @Test
    void testIsEmptyCall() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def pageNotEmpty = createTestPage(0, 3, null)
        expectPage(pageNotEmpty, ds)
        assertThat "Expected TestCollectionResource.isEmpty() to return false", !new TestCollectionResource(ds, pageNotEmpty).isEmpty()

        def pageEmpty = createTestPage(0, 0, null)
        expectPage(pageEmpty, ds)
        assertThat "Expected TestCollectionResource.isEmpty() to return true", new TestCollectionResource(ds, pageEmpty).isEmpty()
    }

    @Test
    void testIteratorAndIsEmptyUseSamePage() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def pageNotEmpty = createTestPage(0, 3, null)
        expectPage(pageNotEmpty, ds)
        def nonEmptyResource = spy(new TestCollectionResource(ds, pageNotEmpty))
        assertThat "TestCollectionResource.iterator.hasNext() returns true", nonEmptyResource.iterator().hasNext()
        assertThat "Expected TestCollectionResource.isEmpty() to return false", !nonEmptyResource.isEmpty()
        verify(nonEmptyResource, times(2)).getCurrentPage()

        def pageEmpty = createTestPage(0, 0, null)
        expectPage(pageEmpty, ds)
        def emptyResource = spy(new TestCollectionResource(ds, pageEmpty))
        assertThat "TestCollectionResource.iterator.hasNext() returns false", !emptyResource.iterator().hasNext()
        assertThat "Expected TestCollectionResource.isEmpty() to return true", emptyResource.isEmpty()
        verify(emptyResource, times(2)).getCurrentPage()
    }

    @Test
    void testEmptyLastPagedCollection() {

        InternalDataStore ds = mock(InternalDataStore)
        when(ds.getBaseUrl()).thenReturn("https://example.com/")

        def page1 = createTestPage(0, 200, "https://example.com/resource?nextPage=1")
        def page2 = createTestPage(200, 0, null)

        expectPage(page1, ds)
        def collectionResource = new TestCollectionResource(ds, page2)
        when(ds.getResource("https://example.com/resource?nextPage=1", TestCollectionResource)).thenReturn(collectionResource)
        expectPage(page2, ds)

        verifyCollection(new TestCollectionResource(ds, page1), 200)
    }

    def verifyCollection(AbstractCollectionResource collectionResource, int size) {
        def counter = 0
        for (Iterator<TestResource> iter = collectionResource.iterator(); iter.hasNext();) {
            TestResource r = iter.next()
            assertThat r.getName(), is("Name-${counter}".toString())
            counter++
        }
        assertThat counter, is(size)
    }

    def expectPage(def page, InternalDataStore ds) {
        for (def item : page["items"]) {
            def resource = new TestResource(ds, item)
            when(ds.instantiate(TestResource, item)).thenReturn(resource)
        }
    }

    def createTestPage(int startIndex, int pageSize, String nextHref) {

        def itemArray = []
        for (int ii = startIndex; ii < startIndex + pageSize; ii++) {

            def item = ['name': "Name-${ii}",
                        'description': "Description-${ii}"]
            itemArray.add(item)
        }

        return [items    : itemArray,
                nextPage : nextHref]
    }
}
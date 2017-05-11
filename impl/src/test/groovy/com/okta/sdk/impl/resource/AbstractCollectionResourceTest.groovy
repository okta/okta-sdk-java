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
import org.testng.annotations.Test

import java.util.stream.StreamSupport

import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link AbstractCollectionResource}.
 */
class AbstractCollectionResourceTest {

    @Test
    void testPagedCollection() {

        InternalDataStore ds = createStrictMock(InternalDataStore)

        def page1 = createTestPage(0, 200, "https://example.com/resource?nextPage=1")
        def page2 = createTestPage(200, 200, "https://example.com/resource?nextPage=2")
        def page3 = createTestPage(400, 13, null)

        expectPage(page1, ds)
        expect(ds.getResource("https://example.com/resource?nextPage=1", TestCollectionResource)).andReturn(new TestCollectionResource(ds, page2))
        expectPage(page2, ds)
        expect(ds.getResource("https://example.com/resource?nextPage=2", TestCollectionResource)).andReturn(new TestCollectionResource(ds, page3))
        expectPage(page3, ds)

        replay ds

        verifyCollection(new TestCollectionResource(ds, page1), 413)

        verify ds
    }

    @Test
    void testSinglePagedCollection() {

        InternalDataStore ds = createStrictMock(InternalDataStore)

        def page1 = createTestPage(0, 13, null)

        expectPage(page1, ds)

        replay ds

        verifyCollection(new TestCollectionResource(ds, page1), 13)

        verify ds
    }


    @Test
    void testEmptyLastPagedCollection() {

        InternalDataStore ds = createStrictMock(InternalDataStore)

        def page1 = createTestPage(0, 200, "https://example.com/resource?nextPage=1")
        def page2 = createTestPage(200, 0, null)

        expectPage(page1, ds)
        expect(ds.getResource("https://example.com/resource?nextPage=1", TestCollectionResource)).andReturn(new TestCollectionResource(ds, page2))
        expectPage(page2, ds)

        replay ds

        verifyCollection(new TestCollectionResource(ds, page1), 200)

        verify ds
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
            expect(ds.instantiate(anyObject(TestResource), anyObject())).andReturn(new TestResource(ds, item))
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

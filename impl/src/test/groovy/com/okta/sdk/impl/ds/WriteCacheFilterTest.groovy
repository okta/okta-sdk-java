/*
 * Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.ds

import com.okta.commons.http.QueryString
import com.okta.sdk.impl.cache.DefaultCacheManager
import com.okta.sdk.impl.ds.cache.*
import com.okta.sdk.impl.http.support.DefaultCanonicalUri
import com.okta.sdk.impl.resource.HalResourceHrefResolver
import com.okta.sdk.impl.resource.StubEnum
import com.okta.sdk.impl.resource.StubResource
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class WriteCacheFilterTest {

    @Test
    void simpleCacheGetTest() {

        String baseUrl = "https://okta.example.com/cache-test"
        String resourceUrl = "${baseUrl}/cache-me"

        CacheResolver cacheResolver = new DefaultCacheResolver(new DefaultCacheManager())

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)
        WriteCacheFilter cacheFilter = new WriteCacheFilter(cacheStrategy)

        Map<String,Object> payload = [
            booleanPropKey: true,
            enumPropKey: StubEnum.VALUE_1,
            intPropKey: 123,
            stringPropKey: "foo",
            _links: [
                self: [
                    href: resourceUrl
                ]
            ]
        ]

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))
        ResourceDataRequest dataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        ResourceDataResult resourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, payload)
        FilterChain filterChain = mock(FilterChain)
        when(filterChain.filter(any(ResourceDataRequest))).thenReturn(resourceDataResult)
        def result = cacheFilter.filter(dataRequest, filterChain)

        assertThat result, sameInstance(resourceDataResult)
        def cachedItem = cacheResolver.getCache().get(resourceUrl)
        assertThat cachedItem, notNullValue()
        assertThat cachedItem, equalTo(payload)
    }

    @Test
    void getThenUpdateTest() {

        String baseUrl = "https://okta.example.com/cache-test"
        String resourceUrl = "${baseUrl}/cache-me"

        CacheResolver cacheResolver = new DefaultCacheResolver(new DefaultCacheManager())

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)
        WriteCacheFilter cacheFilter = new WriteCacheFilter(cacheStrategy)

        Map<String,Object> initialGetPayload = [
            booleanPropKey: true,
            enumPropKey: StubEnum.VALUE_1,
            intPropKey: 123,
            stringPropKey: "foo",
            _links: [
                self: [
                    href: resourceUrl
                ]
            ]
        ]

        Map<String,Object> updatePayload = [
            booleanPropKey: false,
            enumPropKey: StubEnum.VALUE_2,
            intPropKey: 123456,
            stringPropKey: "foobar"
        ]

        Map<String,Object> updatePayloadResponse = [
            booleanPropKey: false,
            enumPropKey: StubEnum.VALUE_2,
            intPropKey: 123456,
            stringPropKey: "foobar",
            extraProperty: "extra-extra",
            _links: [
                self: [
                    href: resourceUrl
                ]
            ]
        ]

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))

        // GET
        ResourceDataRequest getDataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        ResourceDataResult getResourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, initialGetPayload)

        // UPDATE
        ResourceDataRequest updateDataRequest = new DefaultResourceDataRequest(ResourceAction.UPDATE, canonicalUri, StubResource, updatePayload)
        ResourceDataResult updateResourceDataResult = new DefaultResourceDataResult(ResourceAction.UPDATE, canonicalUri, StubResource, updatePayloadResponse)

        FilterChain filterChain = mock(FilterChain)
        when(filterChain.filter(any(ResourceDataRequest)))
                .thenReturn(getResourceDataResult)
                .thenReturn(updateResourceDataResult)

        // do READ
        def initialGetResult = cacheFilter.filter(getDataRequest, filterChain)
        // validate initial cache of the get
        assertThat initialGetResult, sameInstance(getResourceDataResult)
        def getCachedItem = cacheResolver.getCache().get(resourceUrl)
        assertThat getCachedItem, notNullValue()
        assertThat getCachedItem, equalTo(initialGetPayload)

        // do UPDATE
        def updateResult = cacheFilter.filter(updateDataRequest, filterChain)
        // validate the updated cache
        assertThat updateResult, sameInstance(updateResourceDataResult)
        def updatedCachedItem = cacheResolver.getCache().get(resourceUrl)
        assertThat updatedCachedItem, notNullValue()
        assertThat updatedCachedItem, equalTo(updatePayloadResponse)
    }

    @Test
    void getThenDeleteTest() {

        String baseUrl = "https://okta.example.com/cache-test"
        String resourceUrl = "${baseUrl}/cache-me"

        CacheResolver cacheResolver = new DefaultCacheResolver(new DefaultCacheManager())

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)
        WriteCacheFilter cacheFilter = new WriteCacheFilter(cacheStrategy)

        Map<String,Object> initialGetPayload = [
            booleanPropKey: true,
            enumPropKey: StubEnum.VALUE_1,
            intPropKey: 123,
            stringPropKey: "foo",
            _links: [
                self: [
                    href: resourceUrl
                ]
            ]
        ]

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))

        // GET
        ResourceDataRequest getDataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        ResourceDataResult getResourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, initialGetPayload)

        // DELETE
        ResourceDataRequest deleteDataRequest = new DefaultResourceDataRequest(ResourceAction.DELETE, canonicalUri, StubResource, Collections.emptyMap())
        ResourceDataResult deleteResourceDataResult = new DefaultResourceDataResult(ResourceAction.DELETE, canonicalUri, StubResource, Collections.emptyMap())

        FilterChain filterChain = mock(FilterChain)
        when(filterChain.filter(any(ResourceDataRequest)))
                .thenReturn(getResourceDataResult)
                .thenReturn(deleteResourceDataResult)

        // do READ
        def initialGetResult = cacheFilter.filter(getDataRequest, filterChain)
        // validate initial cache of the get
        assertThat initialGetResult, sameInstance(getResourceDataResult)
        //def getCachedItem = cacheResolver.getCache(StubResource).get(resourceUrl)
        def getCachedItem = cacheResolver.getCache().get(resourceUrl)
        assertThat getCachedItem, notNullValue()
        assertThat getCachedItem, equalTo(initialGetPayload)

        // do DELETE
        def deleteResult = cacheFilter.filter(deleteDataRequest, filterChain)
        // validate the cached item has been removed
        assertThat deleteResult, sameInstance(deleteResourceDataResult)
        //def deletedCachedItem = cacheResolver.getCache(StubResource).get(resourceUrl)
        def deletedCachedItem = cacheResolver.getCache().get(resourceUrl)
        assertThat deletedCachedItem, nullValue()
    }
}
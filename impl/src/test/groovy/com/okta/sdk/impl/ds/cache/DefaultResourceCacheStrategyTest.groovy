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
package com.okta.sdk.impl.ds.cache

import com.okta.sdk.cache.Cache
import com.okta.sdk.cache.CacheManager
import com.okta.sdk.impl.cache.DefaultCacheManager
import com.okta.sdk.impl.ds.CacheRegionNameResolver
import com.okta.sdk.impl.ds.DefaultResourceDataRequest
import com.okta.sdk.impl.ds.DefaultResourceDataResult
import com.okta.sdk.impl.ds.ResourceAction
import com.okta.sdk.impl.ds.ResourceDataRequest
import com.okta.sdk.impl.ds.ResourceDataResult
import com.okta.sdk.impl.http.QueryString
import com.okta.sdk.impl.http.support.DefaultCanonicalUri
import com.okta.sdk.impl.resource.HalResourceHrefResolver
import com.okta.sdk.impl.resource.StubEnum
import com.okta.sdk.impl.resource.StubResource
import com.okta.sdk.resource.VoidResource
import org.testng.annotations.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class DefaultResourceCacheStrategyTest {

    @Test
    void basicCacheTest() {

        String baseUrl = "https://okta.example.com/cache-strategy-test"
        String resourceUrl = "${baseUrl}/cache-me"

        CacheManager cacheManager = new DefaultCacheManager()
        CacheRegionNameResolver cacheRegionNameResolver = mock(CacheRegionNameResolver)
        when(cacheRegionNameResolver.getCacheRegionName(StubResource)).thenReturn(StubResource.getName())
        CacheResolver cacheResolver = new DefaultCacheResolver(cacheManager, cacheRegionNameResolver)

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))
        ResourceDataRequest dataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        def payload = createCacheableResourceData(resourceUrl)
        ResourceDataResult resourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, payload)

        // nothing in cache, value should be null
        assertThat cacheStrategy.readFromCache(dataRequest), nullValue()

        // cache value
        cacheStrategy.cache(dataRequest, resourceDataResult)
        def cachedValue = cacheManager.getCache(StubResource.getName()).get(resourceUrl)
        assertThat cachedValue, notNullValue()
        assertThat cachedValue, equalTo(payload)

        // read will return the cached object
        ResourceDataResult readCacheResult = cacheStrategy.readFromCache(dataRequest)
        assertThat readCacheResult.getData(), equalTo(payload)

        // now a delete request
        ResourceDataRequest deleteDataRequest = new DefaultResourceDataRequest(ResourceAction.DELETE, canonicalUri, StubResource, Collections.emptyMap())
        ResourceDataResult deleteDataResult = new DefaultResourceDataResult(ResourceAction.DELETE, canonicalUri, StubResource, Collections.emptyMap())

        // read should return null because this is a delete request
        assertThat cacheStrategy.readFromCache(deleteDataRequest), nullValue()
        // cache should be unaffected
        assertThat cacheManager.getCache(StubResource.getName()).get(resourceUrl), equalTo(payload)

        // now cache the delete request
        cacheStrategy.cache(deleteDataRequest, deleteDataResult)
        assertThat cacheManager.getCache(StubResource.getName()).get(resourceUrl), nullValue()
    }

    @Test
    void testUncacheRPC() {

        String baseUrl = "https://okta.example.com/cache-strategy-test"
        String resourceUrl = "${baseUrl}/cache-me"

        Cache cache = mock(Cache)
        CacheManager cacheManager = mock(CacheManager)
        when(cacheManager.getCache(StubResource.getName())).thenReturn(cache)
        CacheRegionNameResolver cacheRegionNameResolver = mock(CacheRegionNameResolver)
        when(cacheRegionNameResolver.getCacheRegionName(StubResource)).thenReturn(StubResource.getName())
        CacheResolver cacheResolver = new DefaultCacheResolver(cacheManager, cacheRegionNameResolver)

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))
        ResourceDataRequest dataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        def payload = createCacheableResourceData(resourceUrl)
        ResourceDataResult resourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, payload)

        // cache value
        cacheStrategy.cache(dataRequest, resourceDataResult)

        DefaultCanonicalUri rpcActionUri = new DefaultCanonicalUri("${resourceUrl}/lifecycle/foobar", QueryString.create(""))
        ResourceDataRequest rpcActionDataRequest = new DefaultResourceDataRequest(ResourceAction.UPDATE, rpcActionUri, canonicalUri, VoidResource, StubResource, Collections.emptyMap(), null)
        ResourceDataResult rpcActionDataResult = new DefaultResourceDataResult(ResourceAction.UPDATE, rpcActionUri, StubResource, Collections.emptyMap())

        // hit the cache again with the RPC request
        cacheStrategy.cache(rpcActionDataRequest, rpcActionDataResult)

        verify(cache).remove(resourceUrl)
    }

    @Test
    void nonCacheableObjectTest() {

        String baseUrl = "https://okta.example.com/cache-strategy-test"
        String resourceUrl = "${baseUrl}/cache-me"

        CacheManager cacheManager = mock(CacheManager)
        CacheRegionNameResolver cacheRegionNameResolver = mock(CacheRegionNameResolver)
        when(cacheRegionNameResolver.getCacheRegionName(StubResource)).thenReturn(StubResource.getName())
        CacheResolver cacheResolver = new DefaultCacheResolver(cacheManager, cacheRegionNameResolver)

        ResourceCacheStrategy cacheStrategy = new DefaultResourceCacheStrategy(new HalResourceHrefResolver(), cacheResolver)

        DefaultCanonicalUri canonicalUri = new DefaultCanonicalUri(resourceUrl, QueryString.create(""))
        ResourceDataRequest dataRequest = new DefaultResourceDataRequest(ResourceAction.READ, canonicalUri, StubResource, Collections.emptyMap())
        def payload = createNonResourceData()
        ResourceDataResult resourceDataResult = new DefaultResourceDataResult(ResourceAction.READ, canonicalUri, StubResource, payload)

        // cache value
        cacheStrategy.cache(dataRequest, resourceDataResult)

        // getCache was never called
        verifyNoMoreInteractions(cacheManager)
    }

    def createCacheableResourceData(String href) {
        return [
            booleanPropKey: true,
            enumPropKey: StubEnum.VALUE_1,
            intPropKey: 123,
            stringPropKey: "foo",
            _links: [
                self: [
                    href: href
                ]
            ]
        ]
    }

    def createNonResourceData() {
        return [
            booleanPropKey: true,
            enumPropKey: StubEnum.VALUE_1,
            intPropKey: 123,
            stringPropKey: "foo"
        ]
    }
}
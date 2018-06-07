/*
 * Copyright 2018-Present Okta, Inc.
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

import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.VoidResource
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@link DefaultRequestBuilder}.
 * @since 1.2.0
 */
class DefaultRequestBuilderTest {

    @Test
    void testConstruct() {

        def dataStore = mock(InternalDataStore)
        def voidResource = mock(VoidResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(voidResource)
        def requestBuilder = new DefaultRequestBuilder(dataStore)

        assertThat requestBuilder.body, sameInstance(voidResource)
        assertThat requestBuilder.queryParameters, anEmptyMap()
    }

    @Test
    void testSetBody() {

        def resource = mock(Resource)
        def requestBuilder = createRequestBuilder()

        requestBuilder.setBody(resource)
        assertThat requestBuilder.body, sameInstance(resource)

        requestBuilder.setBody(null)
        assertThat requestBuilder.body, nullValue()
    }

    @Test
    void testAddQueryParameter() {

        def requestBuilder = createRequestBuilder()

        requestBuilder.addQueryParameter("key1", "value1")
        requestBuilder.addQueryParameter("key2", "value2")
        requestBuilder.addQueryParameter("key3", null)
        assertThat requestBuilder.queryParameters, equalTo([
                key1: "value1",
                key2: "value2",
                key3: null
        ])
    }

    @Test
    void testAddQueryParameters() {

        def requestBuilder = createRequestBuilder()
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        requestBuilder.setQueryParameters(params)
        assertThat requestBuilder.queryParameters, equalTo([
                key1: "value1",
                key2: "value2",
                key3: null
        ])

        requestBuilder.setQueryParameters(null)
        assertThat requestBuilder.queryParameters, anEmptyMap()
    }

    @Test
    void testGet() {

        def href = "/api/v1/foobar"
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def returnValue = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)
        when(dataStore.getResource(href, ExtensibleResource, params)).thenReturn(returnValue)

        def requestBuilder = new DefaultRequestBuilder(dataStore)

        requestBuilder.setQueryParameters(params)
        def result = requestBuilder.get(href, ExtensibleResource)
        assertThat result, sameInstance(returnValue)
    }

    @Test
    void testPut() {

        def href = "/api/v1/foobar"
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def putResource = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        def requestBuilder = new DefaultRequestBuilder(dataStore)
            .setBody(putResource)
            .setQueryParameters(params)
        requestBuilder.put(href)
        verify(dataStore).save(href, putResource, null, params)
    }

    @Test
    void testPostWithType() {

        def href = "/api/v1/foobar"
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def postResource = mock(ExtensibleResource)
        def returnValue = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)
        when(dataStore.create(href, postResource, null, ExtensibleResource, params)).thenReturn(returnValue)

        def result = new DefaultRequestBuilder(dataStore)
            .setBody(postResource)
            .setQueryParameters(params)
            .post(href, ExtensibleResource)

        assertThat result, sameInstance(returnValue)
    }

    @Test
    void testPost() {

        def href = "/api/v1/foobar"
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def postResource = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        new DefaultRequestBuilder(dataStore)
            .setBody(postResource)
            .setQueryParameters(params)
            .post(href)

        verify(dataStore).create(href, postResource, null, VoidResource, params)
    }

    @Test
    void testDelete() {

        def href = "/api/v1/foobar"
        def params = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        new DefaultRequestBuilder(dataStore)
            .setQueryParameters(params)
            .delete(href)

        verify(dataStore).delete(href, params)
    }

    private def createRequestBuilder() {

        def dataStore = mock(InternalDataStore)
        def voidResource = mock(VoidResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(voidResource)

        return new DefaultRequestBuilder(dataStore)
    }
}
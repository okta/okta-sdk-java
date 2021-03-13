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
    void testAddHeaderParameter() {

        def requestBuilder = createRequestBuilder()

        requestBuilder.addHeaderParameter("key1", "value1")
        requestBuilder.addHeaderParameter("key2", ["value21", "value22"])
        requestBuilder.addHeaderParameter("key3", null)

        assertThat requestBuilder.headerParameters.get("key1"), is(["value1"])
        assertThat requestBuilder.headerParameters.get("key2"), is(["value21", "value22"])
        assertThat requestBuilder.headerParameters.get("key3"), nullValue()
        assertThat requestBuilder.headerParameters, aMapWithSize(3)
    }

    @Test
    void testAddHeaderParameters() {

        def requestBuilder = createRequestBuilder()
        def params = [
                key1: ["value1"],
                key2: ["value21", "value22"],
                key3: null
        ]
        requestBuilder.setHeaderParameters(params)

        assertThat requestBuilder.headerParameters.get("key1"), is(["value1"])
        assertThat requestBuilder.headerParameters.get("key2"), is(["value21", "value22"])
        assertThat requestBuilder.headerParameters.get("key3"), nullValue()
        assertThat requestBuilder.headerParameters, aMapWithSize(3)

        requestBuilder.setHeaderParameters(null)
        assertThat requestBuilder.headerParameters, anEmptyMap()
    }

    @Test
    void testGet() {

        def href = "/api/v1/foobar"
        def queryParams = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def headerParams = [
                header1: ["value1"],
                header2: ["value21", "value22"],
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def returnValue = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)
        when(dataStore.getResource(href, ExtensibleResource, queryParams, headerParams)).thenReturn(returnValue)

        def requestBuilder = new DefaultRequestBuilder(dataStore)
                .setQueryParameters(queryParams)
                .setHeaderParameters(headerParams)
        def result = requestBuilder.get(href, ExtensibleResource)
        assertThat result, sameInstance(returnValue)
    }

    @Test
    void testGetRaw() {
        def href = "/api/v1/apps/clientId/sso/saml/metadata"
        def queryParams = [
                key1: "value1"
        ]
        def headerParams = [
                header1: ["value1"]
        ]
        def dataStore = mock(InternalDataStore)
        def returnValue = mock(InputStream)
        when(dataStore.getRawResponse(href, queryParams, headerParams)).thenReturn(returnValue)

        def result = new DefaultRequestBuilder(dataStore)
                .setQueryParameters(queryParams)
                .setHeaderParameters(headerParams)
                .getRaw(href)

        assertThat result, sameInstance(returnValue)
    }

    @Test
    void testPut() {

        def href = "/api/v1/foobar"
        def queryParams = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def headerParams = [
                header1: ["value1"],
                header2: ["value21", "value22"],
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def putResource = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        def requestBuilder = new DefaultRequestBuilder(dataStore)
            .setBody(putResource)
            .setQueryParameters(queryParams)
            .setHeaderParameters(headerParams)
        requestBuilder.put(href)
        verify(dataStore).save(href, putResource, null, queryParams, headerParams)
    }

    @Test
    void testPostWithType() {

        def href = "/api/v1/foobar"
        def queryParams = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def headerParams = [
                header1: ["value1"],
                header2: ["value21", "value22"],
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def postResource = mock(ExtensibleResource)
        def returnValue = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)
        when(dataStore.create(href, postResource, null, ExtensibleResource, queryParams, headerParams)).thenReturn(returnValue)

        def result = new DefaultRequestBuilder(dataStore)
            .setBody(postResource)
            .setQueryParameters(queryParams)
            .setHeaderParameters(headerParams)
            .post(href, ExtensibleResource)

        assertThat result, sameInstance(returnValue)
    }

    @Test
    void testPost() {

        def href = "/api/v1/foobar"
        def queryParams = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def headerParams = [
                header1: ["value1"],
                header2: ["value21", "value22"],
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        def postResource = mock(ExtensibleResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        new DefaultRequestBuilder(dataStore)
            .setBody(postResource)
            .setQueryParameters(queryParams)
            .setHeaderParameters(headerParams)
            .post(href)

        verify(dataStore).create(href, postResource, null, VoidResource, queryParams, headerParams)
    }

    @Test
    void testDelete() {

        def href = "/api/v1/foobar"
        def queryParams = [
                key1: "value1",
                key2: "value2",
                key3: null
        ]
        def headerParams = [
                header1: ["value1"],
                header2: ["value21", "value22"],
        ]
        def dataStore = mock(InternalDataStore)
        def resource = mock(VoidResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(resource)

        new DefaultRequestBuilder(dataStore)
            .setQueryParameters(queryParams)
            .setHeaderParameters(headerParams)
            .delete(href)

        verify(dataStore).delete(href, queryParams, headerParams)
    }

    private def createRequestBuilder() {

        def dataStore = mock(InternalDataStore)
        def voidResource = mock(VoidResource)
        when(dataStore.instantiate(VoidResource)).thenReturn(voidResource)

        return new DefaultRequestBuilder(dataStore)
    }
}
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

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.hasProperty
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.nullValue
import static org.mockito.Mockito.*

class AbstractResourceTest {

    @Test
    void matchingTemplateUrl() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "/api/v1/users/my-userId/factors/my-factorId",
                    [ userId: "my-userId",
                      factorId: "my-factorId" ])
    }

    @Test
    void fullHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId/factors/my-factorId",
                     [ userId: "my-userId",
                     factorId: "my-factorId"])
    }

    @Test
    void longerTemplateThenHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId",
                     [ userId: "my-userId"])
    }

    @Test
    void longerHrefThenTemplate() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "https://example.com/api/v1/users/my-userId/factors/my-factorId/anotherAction",
                     [ userId: "my-userId",
                     factorId: "my-factorId"])
    }

    @Test
    void oddHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    "foobar",
                     Collections.emptyMap())
    }

    @Test(expectedExceptions = URIParseException)
    void invalidHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                    " 😡  ",
                     null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void missingHref() {
        runScenario("/api/v1/users/{userId}/factors/{factorId}",
                null,
                null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void nullTemplate() {
        runScenario(null,
                "https://example.com/api/v1/users/my-userId/factors/my-factorId/anotherAction",
                null)
    }

    private static void runScenario(String templateUrl, String href, Map<String, String> expectedValues) {

        def resource = new AbstractResource(null){
            @Override
            Map<String, Property> getPropertyDescriptors() {
                return Collections.emptyMap()
            }
        }

        resource.setResourceHref(href)
        def actualMap = resource.getParamsFromHref(templateUrl)
        assertThat actualMap, equalTo(expectedValues)
    }

    StubResource createSubResource() {

        List nestedResourceData = [
                [nestedStringPropKey: "item0"],
                [nestedStringPropKey: "item1"]]

        Map data = [
                booleanPropKey: true,
                datePropKey: "2015-08-30T18:41:35.818Z",
                enumListPropKey: [
                        "VALUE_3",
                        "VALUE_1"
                ],
                enumPropKey: "VALUE_2",
                intPropKey: 42,
                doublePropKey: 42.42d,
                listPropKey: [
                        "one",
                        "two",
                        "three"
                ],
                mapPropKey: [
                        one: "1",
                        two: "22",
                        three: "333"
                ],
                resourceListPropKey: nestedResourceData,
                stringPropKey: "string_value"
        ]

        InternalDataStore dataStore = mock(InternalDataStore)
        Map item0 = nestedResourceData.get(0)
        Map item1 = nestedResourceData.get(1)
        when(dataStore.instantiate(NestedStubResource, item0)).thenReturn(new NestedStubResource(dataStore, item0))
        when(dataStore.instantiate(NestedStubResource, item1)).thenReturn(new NestedStubResource(dataStore, item1))
        return new StubResource(dataStore, data)
    }

    @Test
    void basicPropertyValuesTest() {
        StubResource resource = createSubResource()

        Date expectedDate = Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2015-08-30T18:41:35.818Z")))

        assertThat resource.getBoolean(resource.booleanProp), equalTo(true)
        assertThat resource.getDateProperty(resource.dateProp), equalTo(expectedDate)
        assertThat resource.getEnumListProperty(resource.enumListProp), equalTo([StubEnum.VALUE_3, StubEnum.VALUE_1])
        assertThat resource.getEnumProperty(resource.enumProp), equalTo(StubEnum.VALUE_2)
        assertThat resource.getInt(resource.integerProp), equalTo(42)
        assertThat resource.getIntProperty(resource.integerProp), equalTo(42)
        assertThat resource.getDoubleProperty(resource.doubleProp), equalTo(42.42d)
        assertThat resource.getListProperty(resource.listProp), equalTo(["one", "two", "three"])
        assertThat resource.getMap(resource.mapProp), equalTo([one: "1", two: "22", three: "333"])
        assertThat resource.getString(resource.stringProp), equalTo("string_value")
        assertThat resource.getResourceListProperty(resource.resourceListProp), allOf(
                hasItem(hasProperty("stringProp", equalTo("item0"))),
                hasItem(hasProperty("stringProp", equalTo("item1"))),
                hasSize(2)
        )
    }

    @Test
    void nullPropertyValueTest() {

        Map data = [
                booleanPropKey: null,
                datePropKey: null,
                enumListPropKey: null,
                enumPropKey: null,
                intPropKey: null,
                doublePropKey: null,
                listPropKey: null,
                mapPropKey: null,
                resourceListPropKey: null,
                stringPropKey: null
        ]

        StubResource resource = new StubResource(null, data)

        assertThat resource.getBoolean(resource.booleanProp), equalTo(false)
        assertThat resource.getNullableBoolean(resource.booleanProp), nullValue()
        assertThat resource.getDateProperty(resource.dateProp), nullValue()
        assertThat resource.getEnumListProperty(resource.enumListProp), nullValue()
        assertThat resource.getEnumProperty(resource.enumProp), nullValue()
        assertThat resource.getInt(resource.integerProp), equalTo(-1)
        assertThat resource.getIntProperty(resource.integerProp), nullValue()
        assertThat resource.getDoubleProperty(resource.doubleProp), nullValue()
        assertThat resource.getListProperty(resource.listProp), nullValue()
        assertThat resource.getMap(resource.mapProp), nullValue()
        assertThat resource.getString(resource.stringProp), nullValue()
        assertThat resource.getResourceListProperty(resource.resourceListProp), nullValue()
    }
}
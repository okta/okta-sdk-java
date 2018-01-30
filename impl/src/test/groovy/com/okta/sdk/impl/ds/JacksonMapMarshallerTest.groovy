/*
 * Copyright 2014 Stormpath, Inc.
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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.okta.sdk.impl.resource.AbstractResource
import com.okta.sdk.impl.resource.Property
import com.okta.sdk.impl.resource.StringProperty
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.testng.Assert.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.assertThat

/**
 * @since 0.5.0
 */
class JacksonMapMarshallerTest {

    @Test
    void testGetAndSetObjectMapper() {

        JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        assertEquals mapMarshaller.getObjectMapper(), objectMapper
    }

    @Test
    void testSetPrettyPrint() {

        JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        mapMarshaller.setPrettyPrint(true)

        assertTrue objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT)
        assertTrue mapMarshaller.isPrettyPrint()
    }

    @Test
    void testMarshalException() {

        JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
        def objectMapper = mock(ObjectMapper)
        when(objectMapper.writeValueAsString(null)).then(new Answer<String>() {
            @Override
            String answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException("kaboom")
            }
        })
        mapMarshaller.setObjectMapper(objectMapper)

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream()
            mapMarshaller.marshal(out, null)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Cannot convert null to JSON."
        }
    }

    @Test
    void testUnmarshalInputStreamException() {

        JacksonMapMarshaller mapMarshaller = new JacksonMapMarshaller()
        def objectMapper = mock(ObjectMapper)
        def inputStream = mock(InputStream)
        when(objectMapper.readValue((InputStream)eq(inputStream), (Class)eq(Object.class)))
            .then(new Answer<Map>() {
                @Override
                Map answer(InvocationOnMock invocation) throws Throwable {
                    throw new IOException("kaboom")
                }
            })
        mapMarshaller.setObjectMapper(objectMapper)

        try {
            mapMarshaller.unmarshal(inputStream, null)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert InputStream String to Map."
        }
    }

    @Test
    void testMapContainingList() {

        def testMap = [
                "test1": "value1",
                "simpleListKey": ["one", "two", "three"],
                "complexResourceList": [new FooResource(null, [foo: "bar1"]),
                                        new FooResource(null, [foo: "bar2"])]
        ]

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        new JacksonMapMarshaller().marshal(out, testMap)
        String result = out.toString()

        assertThat result, not(containsString("dirty"))
        assertThat result, containsString('"complexResourceList":[{"foo":"bar1"},{"foo":"bar2"}]')
        assertThat result, containsString('"simpleListKey":["one","two","three"]')
        assertThat result, containsString('"test1":"value1"')
    }

    static class FooResource extends AbstractResource {

        private final static StringProperty FOO_PROPERTY = new StringProperty("foo")

        private final static Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(FOO_PROPERTY)

        FooResource(InternalDataStore dataStore) {
            super(dataStore)
        }

        FooResource(InternalDataStore dataStore, Map<String, Object> properties) {
            super(dataStore, properties)
        }

        @Override
        Map<String, Property> getPropertyDescriptors() {
            return PROPERTY_DESCRIPTORS
        }

        String getFoo() {
            return getString(FOO_PROPERTY)
        }

        FooResource setFoo(String foo) {
            setProperty(FOO_PROPERTY, foo)
            return this
        }
    }
}
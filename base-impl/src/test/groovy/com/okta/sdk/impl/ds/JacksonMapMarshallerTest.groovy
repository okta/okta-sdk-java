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
package com.okta.sdk.impl.ds

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.easymock.IAnswer
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.powermock.api.easymock.PowerMock.replay
import static org.testng.Assert.*

/**
 * @since 1.0.RC9
 */
class JacksonMapMarshallerTest {

    def mapMarshaller

    @BeforeTest
    void setup() {
        mapMarshaller = new JacksonMapMarshaller()
    }

    @Test
    void testGetAndSetObjectMapper() {

        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        assertEquals mapMarshaller.getObjectMapper(), objectMapper
    }

    @Test
    void testSetPrettyPrint() {

        def objectMapper = new ObjectMapper()
        mapMarshaller.setObjectMapper(objectMapper)

        mapMarshaller.setPrettyPrint(true)

        assertTrue objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT)
        assertTrue mapMarshaller.isPrettyPrint()
    }

    @Test
    void testMarshalException() {

        def objectMapper = createMock(ObjectMapper)
        expect(objectMapper.writeValueAsString(null)).andAnswer(new IAnswer<String>() {
            @Override
            String answer() throws Throwable {
                throw new IOException("kaboom")
            }
        })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.marshal(null)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert Map to JSON String."
        }
    }

    @Test
    void testUnmarshalStringException() {

        def objectMapper = createMock(ObjectMapper)
        expect(objectMapper.readValue((String)eq("a value"), (TypeReference)isA(TypeReference)))
            .andAnswer(new IAnswer<Map>() {
                @Override
                Map answer() throws Throwable {
                    throw new IOException("kaboom")
                }
            })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.unmarshal("a value")
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert JSON String to Map."
        }
    }

    @Test
    void testUnmarshalInputStreamException() {

        def objectMapper = createMock(ObjectMapper)
        def inputStream = createMock(InputStream)
        expect(objectMapper.readValue((InputStream)eq(inputStream), (Class)eq(Object.class)))
            .andAnswer(new IAnswer<Map>() {
                @Override
                Map answer() throws Throwable {
                    throw new IOException("kaboom")
                }
            })
        mapMarshaller.setObjectMapper(objectMapper)

        replay objectMapper

        try {
            mapMarshaller.unmarshall(inputStream)
            fail("shouldn't be here")
        } catch (MarshalingException e) {
            assertEquals e.getMessage(), "Unable to convert InputStream String to Map."
        }
    }
}

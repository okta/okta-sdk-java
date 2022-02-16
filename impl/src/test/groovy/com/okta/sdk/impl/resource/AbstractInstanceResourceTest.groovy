/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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

import com.okta.sdk.impl.api.ClientCredentialsResolver
import com.okta.sdk.impl.ds.DefaultDataStore
import com.okta.sdk.impl.ds.InternalDataStore
import com.okta.commons.http.RequestExecutor
import com.okta.sdk.resource.Resource
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.mockito.Mockito.*
import static org.testng.Assert.*

/**
 * @since 0.5.0
 */
class AbstractInstanceResourceTest {

    /**
     * Asserts that the InternalDataStore is not invoked if the dirty properties can satisfy a getProperty request.
     */
    @Test
    void testNonMaterializedResourceGetDirtyPropertyDoesNotMaterialize() {

        def props = ['href': 'https://foo.com/test/123']
        InternalDataStore ds = mock(InternalDataStore)

        TestResource resource = new TestResource(ds, props)

        resource.setName('New Value')

        assertEquals 'New Value', resource.getName()
    }

    @Test
    void testMapProperty() {

        def props = [
                href            : "https://api.okta.com/v1/emailTemplates/3HEMybJjMO2za0YmfTubDI",
                name            : "Default Password Reset Email Template",
                description     : "This is the password reset email template that is associated with the directory",
                fromName        : "Test Name",
                fromEmailAddress: "test@testmail.okta.com",
                subject         : "Reset your Password",
                textBody        : "Forgot your password?\n\nWe've received a request to reset the password for this email address.",
                htmlBody        : "<p>Forgot your password?</p><br/><br/><p>We've received a request to reset the password for this email address.",
                mimeType        : "text/plain",
                defaultModel    : [
                        linkBaseUrl: "https://api.okta.com/passwordReset"
                ]
        ]
        InternalDataStore ds = mock(InternalDataStore)

        def testResource = new TestResource(ds, props)

        def linkBaseUrl = testResource.getMapProperty("defaultModel").get('linkBaseUrl')

        assertEquals(linkBaseUrl, "https://api.okta.com/passwordReset")

        assertNull testResource.getMapProperty("nonExistentMapProperty")

        try {
            testResource.getMapProperty("name")
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "'name' property value type does not match the specified type. Specified type: Map. Existing type: java.lang.String.  Value: Default Password Reset Email Template")
        }
    }


    @Test
    void testMethods() {
        int defaultPropertiesSize = 1

        def properties = [myProp: "myValue"]

        def internalDataStore = mock(InternalDataStore)
        def testResource = new TestResource(internalDataStore, properties)

        doAnswer(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                new DefaultDataStoreDelegateTo(mock(RequestExecutor), testResource).save((Resource)invocation.arguments[0])
                return null
            }
        }).when(internalDataStore).save(testResource)

        testResource.japaneseCharacters = "アナリストは「Apacheの史郎は、今日日本のソフトウェアエンジニアのた"
        testResource.spanishCharacters = "El niño está usando Stormpath para su aplicación."
        testResource.trueProperty = true

        assertEquals testResource.size(), defaultPropertiesSize + 3

        assertFalse testResource.isEmpty()

        assertTrue testResource.containsKey("trueProperty")

        assertFalse testResource.containsKey("notinthere")

        assertTrue testResource.trueProperty as boolean

        assertEquals testResource.getProperty("spanishCharacters"), "El niño está usando Stormpath para su aplicación."

        assertNotNull testResource.entrySet()

        testResource.putAll(["falseProperty": false, "integerProperty": 1234])

        assertEquals testResource.keySet().size(), defaultPropertiesSize + 5

        assertEquals testResource.values().size(), defaultPropertiesSize + 5

        assertTrue testResource.containsValue("アナリストは「Apacheの史郎は、今日日本のソフトウェアエンジニアのた")

        testResource.remove("trueProperty")

        testResource.save()

        assertEquals defaultPropertiesSize + 4, testResource.values().size()

        testResource.clear()

        assertEquals testResource.size(), 0 // okta objects do NOT contain properties like HREF, like the Stormpath ones did

        testResource.delete()

        verify(internalDataStore).delete(testResource)
        verify(internalDataStore).save(testResource)

    }

    //@since 1.0.RC3
    @Test
    void testMapManipulation() {
        def ds = mock(InternalDataStore)
        def TestResource = new TestResource(ds)
        setValue(AbstractResource, TestResource, "materialized", true)

        assertEquals(TestResource.size(), 0)
        assertFalse(TestResource.containsValue("aValue"))
        TestResource.put("aKey", "aValue")
        assertEquals(TestResource.size(), 1)
        assertTrue(TestResource.containsKey("aKey"))
        assertTrue(TestResource.containsValue("aValue"))
        TestResource.remove("aKey")
        assertEquals(TestResource.size(), 0)
        assertFalse(TestResource.containsKey("aKey"))
        assertFalse(TestResource.containsValue("aValue"))
        TestResource.put("anotherKey", "aValue")
        assertEquals(TestResource.size(), 1)
        assertTrue(TestResource.containsValue("aValue"))
        assertFalse(TestResource.containsKey("aKey"))
        assertTrue(TestResource.containsKey("anotherKey"))
        assertEquals(TestResource.entrySet().size(), 1)
        assertEquals(TestResource.keySet().size(), 1)
        assertEquals(TestResource.values().size(), 1)
        TestResource.clear()
        assertFalse(TestResource.containsKey("aKey"))
        assertFalse(TestResource.containsKey("anotherKey"))
        assertFalse(TestResource.containsValue("aValue"))
        assertEquals(TestResource.entrySet().size(), 0)
        assertEquals(TestResource.keySet().size(), 0)
        assertEquals(TestResource.values().size(), 0)
        assertEquals(TestResource.size(), 0)
        TestResource.remove("aKey")
        assertEquals(TestResource.size(), 0)

        TestResource.put("newKey", "someValue")
        TestResource.remove("newKey")
        assertEquals(TestResource.size(), 0)
        TestResource.put("newKey", "newValue00")
        assertEquals(TestResource.size(), 1)
        assertEquals(TestResource.get("newKey"), "newValue00")
        TestResource.put("newKey", "newValue01")
        assertEquals(TestResource.size(), 1)
        assertEquals(TestResource.get("newKey"), "newValue01")

        TestResource = new TestResource(ds, ["aKey": "aValue"])

        assertEquals(TestResource.size(), 1)
        assertTrue(TestResource.containsKey("aKey"))
        assertTrue(TestResource.containsValue("aValue"))
        assertEquals(TestResource.entrySet().size(), 1)
        assertEquals(TestResource.keySet().size(), 1)
        assertEquals(TestResource.values().size(), 1)
        TestResource.put("aKey", "newValue")
        assertEquals(TestResource.size(), 1)
        assertTrue(TestResource.containsKey("aKey"))
        assertFalse(TestResource.containsValue("aValue"))
        assertTrue(TestResource.containsValue("newValue"))
        assertEquals(TestResource.entrySet().size(), 1)
        assertEquals(TestResource.keySet().size(), 1)
        assertEquals(TestResource.values().size(), 1)
        TestResource.remove("aKey")
        assertEquals(TestResource.size(), 0)
        assertFalse(TestResource.containsKey("aKey"))
        assertFalse(TestResource.containsValue("aValue"))
        assertFalse(TestResource.containsValue("newValue"))
        assertEquals(TestResource.entrySet().size(), 0)
        assertEquals(TestResource.keySet().size(), 0)
        assertEquals(TestResource.values().size(), 0)
        TestResource.put("newKey", "aValue")
        assertFalse(TestResource.containsKey("aKey"))
        assertTrue(TestResource.containsKey("newKey"))
        assertTrue(TestResource.containsValue("aValue"))
        assertEquals(TestResource.size(), 1)
        assertEquals(TestResource.entrySet().size(), 1)
        assertEquals(TestResource.keySet().size(), 1)
        assertEquals(TestResource.values().size(), 1)

        TestResource.putAll(["aK": "aV", "bK": "bV", "cK": "cV", "dK": "dV"])
        assertTrue(TestResource.containsKey("dK"))
        assertEquals(TestResource.size(), 5)
        assertEquals(TestResource.keySet().size(), 5)
        assertTrue(TestResource.keySet().contains("newKey"))
        assertTrue(TestResource.keySet().contains("bK"))
        assertEquals(TestResource.values().size(), 5)
        assertTrue(TestResource.values().contains("aValue"))
        assertTrue(TestResource.values().contains("cV"))
        assertEquals(TestResource.entrySet().size(), 5)
    }

    private static void setValue(Class clazz, Object object, String fieldName, value) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }

    class DefaultDataStoreDelegateTo extends DefaultDataStore {

        private TestResource TestResource

        DefaultDataStoreDelegateTo(RequestExecutor requestExecutor, TestResource TestResource) {
            super(requestExecutor, "https://api.stormpath.com/v1", mock(ClientCredentialsResolver))
            this.TestResource = TestResource
        }

        void save(Resource resource) {
            Map properties = getValue(AbstractResource, TestResource, "properties") as HashMap
            Map dirtyProperties = getValue(AbstractResource, TestResource, "dirtyProperties") as HashMap
            HashSet deletedPropertyNames = getValue(AbstractResource, TestResource, "deletedPropertyNames") as HashSet
            properties.putAll(dirtyProperties)
            setValue(AbstractResource, TestResource, "properties", properties)
            dirtyProperties.clear()
            setValue(AbstractResource, TestResource, "dirtyProperties", dirtyProperties)
            deletedPropertyNames.clear()
            setValue(AbstractResource, TestResource, "deletedPropertyNames", deletedPropertyNames)
        }

        private Object getValue(Class clazz, Object object, String fieldName) {
            Field field = clazz.getDeclaredField(fieldName)
            field.setAccessible(true)
            return field.get(object)
        }

        private void setValue(Class clazz, Object object, String fieldName, value) {
            Field field = clazz.getDeclaredField(fieldName)
            field.setAccessible(true)
            field.set(object, value)
        }
    }
}
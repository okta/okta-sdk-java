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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.tests.it.util.ClientProvider
import org.testng.Assert
import org.testng.annotations.Test

import java.util.stream.Stream
import java.util.stream.StreamSupport

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Support for basic CRUD tests
 */
trait CrudTestSupport implements ClientProvider {

    @Test
    void basicCrudTest() {

        Client client = getClient()
        preTestSetup(client)

        // Create a resource
        def resource = create(client)

        // getting the resource again should result in the same object
        def readResource = read(client, resource.id)
        assertThat readResource, notNullValue()
        assertThat readResource, equalTo(resource)

        // update the resource
        update(client, resource)
        assertUpdate(client, read(client, resource.id))

        // delete the resource
        delete(client, resource)
        assertDelete(client, resource)
    }

    @Test
    void basicListTest() {

        // Create a resource
        def resource = create(client)
        registerForCleanup(resource)

        // search the resource collection looking for the new resource
        Optional optional = getResourceListStream(client)
                                .filter {it.id == resource.id}
                                .findFirst()

        // make sure it exists
        assertThat "New resource with id ${resource.id} was not found in list resource.", optional.isPresent()
    }

    void preTestSetup(Client client) {}

    abstract def create(Client client)

    abstract def read(Client client, String id)

    abstract Iterator getResourceCollectionIterator(Client client)

    abstract void update(Client client, def resource)

    abstract void assertUpdate(Client client, def resource)

    Stream getResourceListStream(Client client) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(getResourceCollectionIterator(client), Spliterator.ORDERED),false)
    }

    // delete
    void delete(Client client, def resource) {
        if (resource.getMetaClass().respondsTo(resource, "deactivate")) {
            resource.deactivate()
        }
        resource.delete()
    }

    void assertDelete(Client client, def resource) {
        try {
            read(client, resource.id)
            Assert.fail("Expected ResourceException (404)")
        } catch (ResourceException e) {
            assertThat e.status, equalTo(404)
        }
    }
}

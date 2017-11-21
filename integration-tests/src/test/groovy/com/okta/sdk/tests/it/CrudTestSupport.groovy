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
import com.okta.sdk.client.Clients
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Support for basic CRUD tests
 */
trait CrudTestSupport {

    @Test
    void basicCrudTest() {

        Client client = Clients.builder().build()

        preTestSetup(client)

        // get the size of initial list
        int preCount = getCount(client)

        // Create a resource
        def resource = create(client)

        // count the resource after creating
        int count = getCount(client)
        assertThat "More then one resource was created", count, is(preCount + 1)

//        // getting the resource again should result in the same object
//        assertThat read(client, resource.id), equalTo(resource)

        // update the resource
        update(client, resource)

        count = getCount(client)
        assertThat "New resource was created while attempting to update", count, is(preCount + 1)

        // delete the resource
        delete(client, resource)

        count = getCount(client)

        assertThat "Resource was not deleted", count, is(preCount)
    }

    void preTestSetup(Client client) {

    }

    // create
    abstract def create(Client client)

    // read
    abstract def read(Client client, String id)

    // update
    abstract void update(Client client, def resource)

    // delete
    void delete(Client client, def resource) {
        resource.delete()
    }

    // list
    abstract Iterator getResourceCollectionIterator(Client client)

    int getCount(Client client) {
        return getResourceCollectionIterator(client).size()
    }

}

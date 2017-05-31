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

        // get the size of initial list
        int preCount = getCount(client)

        // Create a group
        def resource = create(client)

        // get the count of users after adding
        int count = getCount(client)
        assertThat count, is(preCount + 1)

        // update the resource
        update(client, resource)

        count = getCount(client)
        assertThat count, is(preCount + 1)

        // delete the resource
        delete(client, resource)

        count = getCount(client)
        assertThat count, is(preCount)
    }

    // create
    abstract def create(Client client)

    // read
    abstract def read(Client client, String id)

    // update
    abstract void update(Client client, def resource)

    // delete
    abstract void delete(Client client, def resource)

    // list
    abstract Iterator getResourceCollectionIterator(Client client)

    int getCount(Client client) {
        return getResourceCollectionIterator(client).size()
    }

}

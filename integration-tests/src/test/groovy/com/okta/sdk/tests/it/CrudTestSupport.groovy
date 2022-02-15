/*
 * Copyright 2017-Present Okta, Inc.
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
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.Application
import com.okta.sdk.resource.AuthorizationServer
import com.okta.sdk.resource.Policy
import com.okta.sdk.resource.EventHook
import com.okta.sdk.resource.Group
import com.okta.sdk.resource.GroupRule
import com.okta.sdk.resource.User
import com.okta.sdk.resource.IdentityProvider
import com.okta.sdk.resource.InlineHook
import com.okta.sdk.resource.PolicyRule
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

    @Test (groups = ["group1"])
    void basicCrudTest() {

        Client client = getClient()
        preTestSetup(client)

        // Create a resource
        def resource = create(client)

        // getting the resource again should result in the same object
        def readResource = read(client, resource.id)
        assertThat readResource, notNullValue()
        assertThat readResource.id, equalTo(resource.id)

        // update the resource
        update(client, resource)
        assertUpdate(client, read(client, resource.id))

        // delete the resource
        delete(client, resource)
        assertDelete(client, resource)
    }

    @Test (groups = ["group1"])
    void basicListTest() {

        // Create a resource
        def resource = create(client)
        registerForCleanup(resource as Resource)

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

        if (resource._links != null && resource._links.get("deactivate") != null) {
            if (resource instanceof Application)
                client.deactivateApplication(resource.getId())
            if (resource instanceof User)
                client.deactivateUser(resource.getId())
            if (resource instanceof GroupRule)
                client.deactivateGroupRule(resource.getId())
            if (resource instanceof AuthorizationServer)
                client.deactivateAuthorizationServer(resource.getId())
            if (resource instanceof EventHook)
                client.deactivateEventHook(resource.getId())
            if (resource instanceof InlineHook)
                client.deactivateInlineHook(resource.getId())
            if (resource instanceof IdentityProvider)
                client.deactivateIdentityProvider(resource.getId())
        }

        if (resource instanceof Application)
            client.deleteApplication(resource.getId())
        if (resource instanceof Group)
            client.deleteGroup(resource.getId())
        if (resource instanceof User) {
            client.deactivateOrDeleteUser(resource.getId())
        }
        if (resource instanceof GroupRule)
            client.deleteGroupRule(resource.getId())
        if (resource instanceof AuthorizationServer)
            client.deleteAuthorizationServer(resource.getId())
        if (resource instanceof EventHook)
            client.deleteEventHook(resource.getId())
        if (resource instanceof InlineHook)
            client.deleteInlineHook(resource.getId())
        if (resource instanceof IdentityProvider)
            client.deleteIdentityProvider(resource.getId())
        if (resource instanceof Policy)
            client.deletePolicy(resource.getId())
        if (resource instanceof PolicyRule) {
            def selfHref = resource._links.get("self").get("href")
            client.delete(selfHref, resource)
        }
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

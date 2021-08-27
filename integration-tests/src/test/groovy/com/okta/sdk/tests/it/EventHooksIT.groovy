/*
 * Copyright 2020-Present Okta, Inc.
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

import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.event.hook.EventHook
import com.okta.sdk.resource.event.hook.EventHookBuilder
import com.okta.sdk.resource.event.hook.EventHookChannelConfigAuthScheme
import com.okta.sdk.resource.event.hook.EventHookChannelConfigAuthSchemeType

import com.okta.sdk.tests.it.util.ITSupport

import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.iterableWithSize
import static org.hamcrest.Matchers.notNullValue

import static com.okta.sdk.tests.it.util.Util.assertPresent

/**
 * Tests for {@code /api/v1/eventHooks}.
 * @since 2.0.0
 */
class EventHooksIT extends ITSupport {

    @Test (groups = "group3")
    void createEventHookTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())
        assertThat(createdEventHook.getName(), equalTo(name))
        assertThat(createdEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))
        assertThat(createdEventHook.getEvents().getItems(), iterableWithSize(2))
        assertThat(createdEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooks"))

        createdEventHook.deactivate()
    }

    @Test (groups = "group3")
    void getEventHookTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())

        assertThat(retrievedEventHook.getId(), notNullValue())
        assertThat(retrievedEventHook.getName(), equalTo(name))
        assertThat(createdEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))
        assertThat(retrievedEventHook.getEvents().getItems(), iterableWithSize(2))
        assertThat(retrievedEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooks"))

        createdEventHook.deactivate()
    }

    @Test (groups = "group3")
    void updateEventHookTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        EventHook toBeUpdatedEventHook = createdEventHook.setName("updated-" + name)
        createdEventHook.getEvents().getItems().add("user.lifecycle.deactivate")
        createdEventHook.getChannel().getConfig().setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
            .setType(EventHookChannelConfigAuthSchemeType.HEADER)
            .setKey("Authorization")
            .setValue("Test-Api-Key-Updated"))
        .setUri("https://www.example.com/eventHooksUpdated")

        EventHook updatedEventHook = toBeUpdatedEventHook.update()

        assertThat(updatedEventHook.getId(), notNullValue())
        assertThat(updatedEventHook.getId(), equalTo(createdEventHook.getId()))
        assertThat(createdEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))
        assertThat(updatedEventHook.getName(), equalTo("updated-" + name))
        assertThat(updatedEventHook.getEvents().getItems(), iterableWithSize(3))
        assertThat(updatedEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooksUpdated"))

        createdEventHook.deactivate()
    }

    @Test (groups = "group3")
    void deleteEventHookTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())
        assertThat(retrievedEventHook.getId(), equalTo(createdEventHook.getId()))
        assertThat(retrievedEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))

        createdEventHook.deactivate()
        createdEventHook.delete()

        try {
            client.getEventHook(createdEventHook.getId())
        }
        catch (ResourceException e) {
            assertThat(e.status, equalTo(404))
        }
    }

    @Test (groups = "group3")
    void listAllEventHooksTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        assertPresent(client.listEventHooks(), createdEventHook)

        createdEventHook.deactivate()
    }

    @Test (groups = "group3")
    void activateDeactivateEventHookTest() {
        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = EventHookBuilder.instance()
            .setName(name)
            .setUrl("https://www.example.com/eventHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())
        assertThat(createdEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))

        createdEventHook.deactivate()

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())

        assertThat(retrievedEventHook.getStatus(), equalTo(EventHook.StatusEnum.INACTIVE))
    }
}

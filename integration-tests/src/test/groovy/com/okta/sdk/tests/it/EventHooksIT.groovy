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
import com.okta.sdk.resource.EventHookChannel
import com.okta.sdk.resource.EventHookChannelConfig
import com.okta.sdk.resource.EventHookChannelConfigAuthScheme
import com.okta.sdk.resource.EventHookChannelConfigHeader
import com.okta.sdk.resource.EventSubscriptions
import com.okta.sdk.resource.event.hook.EventHook
import com.okta.sdk.resource.event.hook.EventHookChannelConfigAuthSchemeType
import com.okta.sdk.resource.event.hook.EventHookList
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for /api/v1/eventHooks
 * @since 2.0.0
 */
class EventHooksIT extends ITSupport {

    @Test
    void createEventHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                .setKey("Authorization")
                .setValue("Test-Api-Key"))))
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())
        assertThat(createdEventHook.getName(), equalTo(name))
        assertThat(createdEventHook.getEvents(), hasSize(2))
        assertThat(createdEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooks"))
    }

    @Test
    void getEventHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                        .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())

        assertThat(retrievedEventHook.getId(), notNullValue())
        assertThat(retrievedEventHook.getName(), equalTo(name))
        assertThat(retrievedEventHook.getEvents(), hasSize(2))
        assertThat(retrievedEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooks"))
    }

    @Test
    void updateEventHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                        .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
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
        assertThat(updatedEventHook.getName(), equalTo("updated-" + name))
        assertThat(updatedEventHook.getEvents(), hasSize(3))
        assertThat(updatedEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooksUpdated"))
    }

    @Test
    void deleteEventHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                        .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())
        assertThat(retrievedEventHook.getId(), equalTo(createdEventHook.getId()))

        createdEventHook.deactivate()
        createdEventHook.delete()

        sleep(2000)

        try {
            client.getEventHook(createdEventHook.getId())
        }
        catch (ResourceException e) {
            assertThat(e.status, equalTo(404))
        }
    }

    @Test
    void listAllEventHooksTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHookList eventHookList = client.listEventHooks()

        int listSize = eventHookList.size()

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                        .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())

        sleep(2000)

        assertThat(client.listEventHooks(), hasSize(listSize + 1))
    }

    @Test
    void activateDeactivateEventHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        EventHook createdEventHook = client.instantiate(EventHook)
            .setName(name)
            .setEvents(client.instantiate(EventSubscriptions)
                .setType(EventSubscriptions.TypeEnum.EVENT_TYPE)
                .setItems(["user.lifecycle.create", "user.lifecycle.activate"]))
            .setChannel(client.instantiate(EventHookChannel)
                .setType(EventHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(EventHookChannelConfig)
                    .setUri("https://www.example.com/eventHooks")
                    .setHeaders([ client.instantiate(EventHookChannelConfigHeader)
                                      .setKey("X-Test-Header")
                                      .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(EventHookChannelConfigAuthScheme)
                        .setType(EventHookChannelConfigAuthSchemeType.HEADER)
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdEventHook)

        assertThat(createdEventHook.getId(), notNullValue())
        assertThat(createdEventHook.getStatus(), equalTo(EventHook.StatusEnum.ACTIVE))

        createdEventHook.deactivate()

        EventHook retrievedEventHook = client.getEventHook(createdEventHook.getId())

        assertThat(retrievedEventHook.getStatus(), equalTo(EventHook.StatusEnum.INACTIVE))

        retrievedEventHook.delete()
    }
}

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

import com.okta.sdk.resource.InlineHook
import com.okta.sdk.resource.InlineHookChannelConfig
import com.okta.sdk.resource.InlineHookChannelConfigAuthScheme
import com.okta.sdk.resource.InlineHookChannelConfigHeaders
import com.okta.sdk.resource.InlineHookChannel
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for /api/v1/inlineHooks
 * @since 2.0.0
 */
class InlineHooksIT extends ITSupport {

    @Test
    void createInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())
        assertThat(createdInlineHook.getName(), equalTo(name))
        assertThat(createdInlineHook.getEvents(), hasSize(2))
        assertThat(createdInlineHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/inlineHooks"))
    }

    @Test
    void getInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        EventHook retrievedEventHook = client.getEventHook(createdInlineHook.getId())

        assertThat(retrievedEventHook.getId(), notNullValue())
        assertThat(retrievedEventHook.getName(), equalTo(name))
        assertThat(retrievedEventHook.getEvents(), hasSize(2))
        assertThat(retrievedEventHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/inlineHooks")
    }

    @Test
    void updateInlineHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        InlineHook toBeUpdatedInlineHook = createdInlineHook.setName("updated-" + name)
        toBeUpdatedInlineHook.getChannel().getConfig().setHeaders([
            client.instantiate(InlineHookChannelConfigHeaders)
                .setKey("X-Test-Header")
                .setValue("Test header value updated")])
        toBeUpdatedInlineHook.getChannel().getConfig().setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
            .setType("HEADER")
            .setKey("Authorization")
            .setValue("Test-Api-Key-Updated"))
            .setUri("https://www.example.com/inlineHooksUpdated")

        InlineHook updatedInlineHook = toBeUpdatedInlineHook.update()

        assertThat(updatedInlineHook.getId(), notNullValue())
        assertThat(updatedInlineHook.getId(), equalTo(createdInlineHook.getId()))
        assertThat(updatedInlineHook.getName(), equalTo("updated-" + name))
        assertThat(updatedInlineHook.getType(), equalTo(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM))
        assertThat(updatedInlineHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/eventHooksUpdated"))
    }

    @Test
    void deleteInlineHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        InlineHook retrievedInlineHook = client.getEventHook(createdInlineHook.getId())
        assertThat(retrievedInlineHook.getId(), equalTo(createdInlineHook.getId()))

        createdInlineHook.deactivate()
        createdInlineHook.delete()

        sleep(2000)

        try {
            client.getEventHook(createdInlineHook.getId())
        }
        catch (ResourceException e) {
            assertThat(e.status, equalTo(404))
        }
    }

    @Test
    void listAllInlineHooksTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        sleep(2000)

        assertThat(client.listInlineHooks().any{ createdInlineHook.getId().equals(it.id) })
    }

    @Test
    void activateDeactivateInlineHookTest() {
        String name = "event-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = client.instantiate(InlineHook)
            .setName(name)
            .setType(InlineHook.TypeEnum.OAUTH2_TOKEN_TRANSFORM)
            .setVersion("1.0.0")
            .setChannel(client.instantiate(InlineHookChannel)
                .setType(InlineHookChannel.TypeEnum.HTTP)
                .setVersion("1.0.0")
                .setConfig(client.instantiate(InlineHookChannelConfig)
                    .setUri("https://www.example.com/inlineHooks")
                    .setHeaders([client.instantiate(InlineHookChannelConfigHeaders)
                                     .setKey("X-Test-Header")
                                     .setValue("Test header value")])
                    .setAuthScheme(client.instantiate(InlineHookChannelConfigAuthScheme)
                        .setType("Header")
                        .setKey("Authorization")
                        .setValue("Test-Api-Key"))))
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())
        assertThat(createdInlineHook.getStatus(), equalTo(InlineHook.StatusEnum.ACTIVE))

        createdInlineHook.deactivate()

        InlineHook retrievedInlineHook = client.getEventHook(createdInlineHook.getId())

        assertThat(retrievedInlineHook.getStatus(), equalTo(InlineHook.StatusEnum.INACTIVE))

        retrievedInlineHook.delete()
    }
}

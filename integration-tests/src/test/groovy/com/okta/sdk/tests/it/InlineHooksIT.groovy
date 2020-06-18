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

import com.okta.sdk.resource.inline.hook.InlineHook
import com.okta.sdk.resource.inline.hook.InlineHookBuilder
import com.okta.sdk.resource.inline.hook.InlineHookChannelConfigAuthScheme
import com.okta.sdk.resource.inline.hook.InlineHookChannelConfigHeaders
import com.okta.sdk.resource.inline.hook.InlineHookChannel
import com.okta.sdk.resource.inline.hook.InlineHookStatus
import com.okta.sdk.resource.inline.hook.InlineHookType
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.assertNotPresent

/**
 * Tests for {@code /api/v1/inlineHooks}.
 * @since 2.0.0
 */
class InlineHooksIT extends ITSupport {

    @Test
    void createInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())
        assertThat(createdInlineHook.getName(), equalTo(name))
        assertThat(createdInlineHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/inlineHooks"))
    }

    @Test
    void getInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        InlineHook retrievedInlineHook = client.getInlineHook(createdInlineHook.getId())

        assertThat(retrievedInlineHook.getId(), notNullValue())
        assertThat(retrievedInlineHook.getName(), equalTo(name))
        assertThat(retrievedInlineHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/inlineHooks"))
    }

    @Test
    void updateInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
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
        assertThat(updatedInlineHook.getType(), equalTo(InlineHookType.OAUTH2_TOKENS_TRANSFORM))
        assertThat(updatedInlineHook.getChannel().getConfig().getUri(), equalTo("https://www.example.com/inlineHooksUpdated"))
    }

    @Test
    void deleteInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())

        InlineHook retrievedInlineHook = client.getInlineHook(createdInlineHook.getId())
        assertThat(retrievedInlineHook.getId(), equalTo(createdInlineHook.getId()))

        createdInlineHook.deactivate()
        createdInlineHook.delete()

        assertNotPresent(client.listInlineHooks(), createdInlineHook)
    }

    @Test
    void listAllInlineHooksTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())
        assertPresent(client.listInlineHooks(), createdInlineHook)
    }

    @Test
    void activateDeactivateInlineHookTest() {
        String name = "inline-hook-java-sdk-${UUID.randomUUID().toString()}"

        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(name)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client);
        registerForCleanup(createdInlineHook)

        assertThat(createdInlineHook.getId(), notNullValue())
        assertThat(createdInlineHook.getStatus(), equalTo(InlineHookStatus.ACTIVE))

        createdInlineHook.deactivate()

        InlineHook retrievedInlineHook = client.getInlineHook(createdInlineHook.getId())

        assertThat(retrievedInlineHook.getStatus(), equalTo(InlineHookStatus.INACTIVE))

        retrievedInlineHook.delete()
    }
}

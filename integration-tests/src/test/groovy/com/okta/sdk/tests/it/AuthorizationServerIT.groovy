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

import com.okta.sdk.resource.authorization.server.AuthorizationServer
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/authorizationServers}
 * @since 2.0.0
 */
class AuthorizationServerIT extends ITSupport {

    @Test
    void createAuthorizationServerTest() {
        String name = "java-sdk-authorization-server-" + RandomStringUtils.randomAlphanumeric(15)

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://default"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer.getId(), notNullValue())
        assertThat(createdAuthorizationServer.getName(), equalTo(name))
        assertThat(createdAuthorizationServer.getDescription(), equalTo("Test Authorization Server"))
        assertThat(createdAuthorizationServer.getAudiences(), hasSize(1))
        assertThat(createdAuthorizationServer.getAudiences(), contains("api://default"))
    }
}

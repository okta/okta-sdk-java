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

import com.okta.sdk.client.Client
import com.okta.sdk.resource.application.*
import com.okta.sdk.tests.it.util.ITSupport

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.matchesPattern

class OIdCApplicationIT extends ITSupport implements CrudTestSupport {

    @Override
    def create(Client client) {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Application app = OIdCApplicationBuilder.instance()
            .setName(name)
            .setLabel(name)
            .addRedirectUris("http://www.example.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("http://www.myapp.com")
            .setErrorRedirectUrl("http://www.myapp.com/error")
            .buildAndCreate(client)
        registerForCleanup(app)

        return (OpenIdConnectApplication) app
    }

    @Override
    def read(Client client, String id) {
        return client.getApplication(id)
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listApplications().iterator()
    }

    @Override
    void update(Client client, def application) {
        application.setLabel(application.label +"-2")
        application.visibility.hide.iOS = false
        application.update()
    }

    @Override
    void assertUpdate(Client client, def app) {
        assertThat app.label, matchesPattern('^java-sdk-it-.*-2$')
        assertThat app.visibility.hide.iOS, is(false)
    }
}

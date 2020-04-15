/*
 * Copyright 2020 Okta
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

class OIdCApplicationIT extends ITSupport implements CrudTestSupport{
    @Override
    def create(Client client) {
        Application app = OIdCApplicationBuilder.instance()
            .setName("oidc_client")
            .setLabel("SDK test app - 1")
            .addRedirectUris("http://www.google.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId("dummy_id")
            .setClientSecret("dummy_shgfhsgdhfgwg")
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("http://www.myapp.com")
            .setErrorRedirectUrl("http://www.myapp.com/errror")
            .buildAndCreate(client);

        return (OpenIdConnectApplication)app
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
        application.setLabel("test_app")
        application.visibility.hide.iOS = false
        application.update()
    }

    @Override
    void assertUpdate(Client client, def app) {
        assertThat app.label, is("test_app")
        assertThat app.visibility.hide.iOS, is(false)
        assertThat app.credentials.oauthClient.client_id, is("dummy_id")

    }
}

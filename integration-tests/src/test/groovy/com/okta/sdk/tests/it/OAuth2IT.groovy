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

import com.okta.sdk.client.AuthorizationMode
import com.okta.sdk.client.Client
import com.okta.sdk.resource.application.*
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.resource.user.UserList
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for OAuth2 flow
 *
 * Below additional env variables (or equivalent system properties) need to be set for OAUTH2 flow to be executed:
 *
 * - OKTA_CLIENT_AUTHORIZATIONMODE (should take the value "PrivateKey")
 * - OKTA_CLIENT_CLIENTID (client id)
 * - OKTA_CLIENT_SCOPES="okta.apps.read okta.apps.manage okta.users.read okta.users.manage okta.groups.read okta.groups.manage" (e.g.)
 * - OKTA_CLIENT_PRIVATEKEY="~/.okta/privateKey.pem" (e.g.)
 *
 * @since 0.6.0
 */
class OAuth2IT extends ITSupport {

    @Test(groups = "bacon")
    void testOAuth2Flow() {

        // 1. create OAuth2 client
        Client client = getClient("oauth2-flow", AuthorizationMode.PRIVATE_KEY.getLabel())
        assertThat client, notNullValue()

        // 2. create an OIDC application with the OAuth2 client
        Application oidcApp = client.instantiate(OpenIdConnectApplication)
                    .setLabel("app-${uniqueTestName}")
                    .setSettings(client.instantiate(OpenIdConnectApplicationSettings)
                    .setOAuthClient(client.instantiate(OpenIdConnectApplicationSettingsClient)
                    .setClientUri("https://example.com/client")
                    .setLogoUri("https://example.com/assets/images/logo-new.png")
                    .setRedirectUris(["https://example.com/oauth2/callback",
                                      "myapp://callback"])
                    .setResponseTypes([OAuthResponseType.TOKEN])
                    .setGrantTypes([OAuthGrantType.CLIENT_CREDENTIALS])
                    .setApplicationType(OpenIdConnectApplicationType.SERVICE)
                    .setTosUri("https://example.com/client/tos")
                    .setPolicyUri("https://example.com/client/policy")))
                    .setCredentials(client.instantiate(OAuthApplicationCredentials)
                    .setOAuthClient(client.instantiate(ApplicationCredentialsOAuthClient)
                    .setClientId(UUID.randomUUID().toString())
                    .setAutoKeyRotation(true)
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_JWT)))

        // oidcApp.getSettings().put("jwks", "blahblahblah") //TODO: generate key pair dynamically and set the JWKS here

        // 3. create an OIDC app
        oidcApp = client.createApplication(oidcApp)
        assertThat oidcApp, notNullValue()

        // 4. list the users (empty list)
        assertThat oidcApp.listApplicationUsers(), notNullValue()

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Flake'
        def email = "john-flake=${uniqueTestName}@example.com"

        // 5. create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(client)

        registerForCleanup(user)

        validateUser(user, firstName, lastName, email)

        // 6. Activate the user and verify user in list of active users
        user.activate(false)
        UserList users = client.listUsers(null, 'status eq \"ACTIVE\"', null, null, null)
        assertPresent(users, user)

        // 7. cleanup
        registerForCleanup(oidcApp)
    }
}
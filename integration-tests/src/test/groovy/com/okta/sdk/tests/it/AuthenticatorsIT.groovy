/*
 * Copyright 2021-Present Okta, Inc.
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

import com.okta.sdk.resource.authenticator.Authenticator
import com.okta.sdk.resource.authenticator.AuthenticatorList
import com.okta.sdk.resource.authenticator.AuthenticatorStatus
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import java.util.stream.Collectors

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/authenticators}.
 * These tests requires the Org to be OIE enabled.
 * @since 6.x.x
 */
class AuthenticatorsIT extends ITSupport {

    // temporarily disabled until we figure out if its safe to toggle authenticator status on an Org
    // where several other management ITs run in parallel.
    // https://oktainc.atlassian.net/browse/OKTA-433369
    @Test (groups = "bacon", enabled = false)
    void listAndGetAuthenticatorsTest() {

        AuthenticatorList authenticators = client.listAuthenticators()
        assertThat(authenticators, notNullValue())
        assertThat(authenticators.size(), greaterThan(0))

        List<String> authenticatorIds = authenticators.stream()
            .map(Authenticator::getId).collect(Collectors.toList())

        authenticatorIds.stream()
            .forEach({
                authenticatorId ->
                    Authenticator authenticator = client.getAuthenticator(authenticatorId)
                    assertThat(authenticator, notNullValue())
                    assertThat(authenticator.getName(), notNullValue())
                    assertThat(authenticator.getStatus(), notNullValue())
                    assertThat(authenticator.getKey(), notNullValue())
            })
    }

    // temporarily disabled until we figure out if its safe to toggle authenticator status on an Org
    // where several other management ITs run in parallel.
    // https://oktainc.atlassian.net/browse/OKTA-433369
    @Test (groups = "bacon", enabled = false)
    void deactivateAndActivateAuthenticatorTest() {

        AuthenticatorList authenticators = client.listAuthenticators()
        assertThat(authenticators, notNullValue())
        assertThat(authenticators.size(), greaterThan(0))

        // get okta verify authenticator
        String oktaVerifyAuthenticatorId = authenticators.stream()
            .filter(authenticator -> authenticator.getName() == "Okta Verify")
            .map(Authenticator::getId).collect(Collectors.toList())
        assertThat(oktaVerifyAuthenticatorId, notNullValue())

        oktaVerifyAuthenticatorId = oktaVerifyAuthenticatorId.substring(1, oktaVerifyAuthenticatorId.length() - 1)

        // deactivate okta verify authenticator
        Authenticator oktaVerifyAuthenticator = client.getAuthenticator(oktaVerifyAuthenticatorId)
        Authenticator deactivatedOktaVerifyAuthenticator = oktaVerifyAuthenticator.deactivate()

        // check authenticator status
        assertThat(deactivatedOktaVerifyAuthenticator.getStatus(), equalTo(AuthenticatorStatus.INACTIVE))

        // activate it back
        Authenticator activatedOktaVerifyAuthenticator = deactivatedOktaVerifyAuthenticator.activate()

        // check authenticator status
        assertThat(activatedOktaVerifyAuthenticator.getStatus(), equalTo(AuthenticatorStatus.ACTIVE))
    }
}

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
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import java.util.stream.Collectors

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.greaterThanOrEqualTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/authenticators}.
 * @since 5.x.x
 */
class AuthenticatorsIT extends ITSupport {

    // This test requires an API Token created by Org admin, otherwise the below error would be returned:
    // HTTP 401, Okta E0000015 (You do not have permission to access the feature you are requesting)

    //TODO: given the above, check if its still a good idea to write this IT.
    @Test (groups = "group3")
    void listAndGetAuthenticatorsTest() {

        AuthenticatorList authenticators = client.listAuthenticators()
        assertThat(authenticators, notNullValue())
        assertThat(authenticators.size(), greaterThanOrEqualTo(0))

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

    //Tests are not written for other APIs such as activate, deactivate authenticators because of
    //their potential to break the Org login and end up in a helpless state.
    //TODO: check if the above reasoning is fine
}

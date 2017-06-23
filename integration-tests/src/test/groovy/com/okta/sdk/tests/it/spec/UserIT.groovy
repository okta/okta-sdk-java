/*
 * Copyright 2017 Okta
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
package com.okta.sdk.tests.it.spec

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.TestResources
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ClientProvider
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*
import static com.okta.sdk.tests.it.util.Util.*

class UserIT implements ClientProvider {

    @Test
    @Scenario("user-profile-update")
    @TestResources(users="joe.coder@example.com")
    void profileUpdateTest() {

        Client client = getClient()

        def password = 'Abcd1234'
        def firstName = 'Joe'
        def lastName = 'Coder'
        def email = 'joe.coder@example.com'

        // 1. Create a user with password & recovery question
        def user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .buildAndCreate(client, false)

        registerForCleanup(user)

        validateUser(user, firstName, lastName, email)

        // 2. Update the user profile
        def originalLastUpdated = user.lastUpdated
        sleep(1000)
        user.getProfile().put("nickName", "Batman")
        user.update()

        assertThat(user.lastUpdated, greaterThan(originalLastUpdated))
        assertThat(user.profile.get("nickName"), equalTo("Batman"))
    }
}

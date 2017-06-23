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
package com.okta.sdk.tests.it.util

import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserList

import java.util.stream.Collectors
import java.util.stream.StreamSupport

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

class Util {

    static void validateUser(User user, String firstName, String lastName, String email, String login=email) {

        assertThat(user.profile, notNullValue())
        assertThat(user.profile.firstName, equalTo(firstName))
        assertThat(user.profile.lastName, equalTo(lastName))
        assertThat(user.profile.email, equalTo(email))
        assertThat(user.profile.login, equalTo(login))
    }

    static void validateUser(User user, User expectedUser) {
        assertThat(user, equalTo(expectedUser))
    }

    static void assertUserPresent(UserList results, expectedUser) {

        List<User> usersFound = StreamSupport.stream(results.spliterator(), false)
                .filter {user -> user.id == expectedUser.id}
                .collect(Collectors.toList())

        assertThat(usersFound, hasSize(1))
    }

    static def ignoring = { Class<? extends Throwable> catchMe, Closure callMe ->
        try {
            callMe.call()
        } catch(e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
        }
    }
}

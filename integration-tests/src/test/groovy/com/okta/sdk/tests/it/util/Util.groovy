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

import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.model.Group
import com.okta.sdk.resource.model.GroupType
import com.okta.sdk.resource.model.User
import com.okta.sdk.resource.model.UserGetSingleton
import org.testng.Assert

import java.util.stream.Collectors
import java.util.stream.StreamSupport

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
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

    static void validateUser(UserGetSingleton user, String firstName, String lastName, String email, String login=email) {

        assertThat(user.profile, notNullValue())
        assertThat(user.profile.firstName, equalTo(firstName))
        assertThat(user.profile.lastName, equalTo(lastName))
        assertThat(user.profile.email, equalTo(email))
        assertThat(user.profile.login, equalTo(login))
    }

    static void validateUser(User user, User expectedUser) {
        assertThat(user, equalTo(expectedUser))
    }

    static void validateGroup(Group group, Group expectedGroup) {
        validateGroup(group, expectedGroup.profile.name)
    }

    static void validateGroup(Group group, String groupName) {
        assertThat(group.profile.name, equalTo(groupName))
        assertThat(group.type, equalTo(GroupType.OKTA_GROUP))
    }

    static void validateGroup(Group group, String groupName, String description) {
        validateGroup(group, groupName)
        assertThat(group.profile.description, equalTo(description))
    }

    static void assertGroupPresent(List<Group> results, Group expectedGroup) {

        List<Group> groupsFound = StreamSupport.stream(results.spliterator(), false)
                .filter {group -> group.id == expectedGroup.id}
                .collect(Collectors.toList())

        assertThat(groupsFound, hasSize(1))
    }

    static void assertGroupAbsent(List<Group> results, Group expectedGroup) {

        List<Group> groupsFound = StreamSupport.stream(results.spliterator(), false)
            .filter {group -> group.id == expectedGroup.id}
            .collect(Collectors.toList())

        assertThat(groupsFound, hasSize(0))
    }

    static void assertUserPresent(List<User> users, User expectedUser) {

        List<User> usersFound = StreamSupport.stream(users.spliterator(), false)
                .filter { user -> user.id == expectedUser.id }
                .collect(Collectors.toList())

        assertThat(usersFound, hasSize(1))
    }

    static void assertUserInGroup(User user, Group group, GroupApi groupApi) {
        assertThat "User was not found in group.", StreamSupport.stream(groupApi.listGroupUsers(group.getId(), null, null).spliterator(), false)
                .filter{ listUser -> listUser.id == user.id}
                .findFirst().isPresent()
    }

    static void assertUserInGroup(User user, Group group, GroupApi groupApi, int times, long delayInMilliseconds, boolean present=true) {
        for (int ii=0; ii<times; ii++) {

            sleep(delayInMilliseconds)

            if (present == StreamSupport.stream(groupApi.listGroupUsers(group.getId(), null, null).spliterator(), false)
                    .filter{ listUser -> listUser.id == user.id}
                    .findFirst().isPresent()) {
                return
            }
        }

        if (present) Assert.fail("User not found in group")
        if (!present) Assert.fail("User found in group")
    }

    static void assertUserNotInGroup(User user, Group group, GroupApi groupApi) {
        assertThat "User was found in group.", !StreamSupport.stream(groupApi.listGroupUsers(group.getId(), null, null).spliterator(), false)
                .filter{ listUser -> listUser.id == user.id}
                .findFirst().isPresent()
    }

    static void assertUserNotInGroup(User user, Group group, GroupApi groupApi, int times, long delayInMilliseconds, boolean present=true) {
        for (int ii=0; ii<times; ii++) {

            sleep(delayInMilliseconds)

            if (present == !StreamSupport.stream(groupApi.listGroupUsers(group.getId(), null, null).spliterator(), false)
                .filter{ listUser -> listUser.id == user.id}
                .findFirst().isPresent()) {
                return
            }
        }

        if (present) Assert.fail("User not found in group")
        if (!present) Assert.fail("User found in group")
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

    static <T extends Throwable> T expect(Class<T> catchMe, Closure closure) {
        try {
            closure.call()
            Assert.fail("Expected ${catchMe.getName()} to be thrown.")
        } catch(e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
            return e
        }
    }
}

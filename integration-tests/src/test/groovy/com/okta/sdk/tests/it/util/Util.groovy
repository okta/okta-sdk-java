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

import org.openapitools.client.model.Group
import org.openapitools.client.model.GroupType
import org.openapitools.client.model.User
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
//
//    static void assertGroupPresent(GroupList results, Group expectedGroup) {
//
//        List<Group> groupsFound = StreamSupport.stream(results.spliterator(), false)
//                .filter {group -> group.id == expectedGroup.id}
//                .collect(Collectors.toList())
//
//        assertThat(groupsFound, hasSize(1))
//    }
//
//    static void assertGroupAbsent(GroupList results, Group expectedGroup) {
//
//        List<Group> groupsFound = StreamSupport.stream(results.spliterator(), false)
//            .filter {group -> group.id == expectedGroup.id}
//            .collect(Collectors.toList())
//
//        assertThat(groupsFound, hasSize(0))
//    }
//
//    static void assertRolePresent(RoleList results, Role expectedRole) {
//
//        List<Role> rolesFound = StreamSupport.stream(results.spliterator(), false)
//            .filter {role -> role.id == expectedRole.id}
//            .collect(Collectors.toList())
//
//        assertThat(rolesFound, hasSize(1))
//    }
//
//    static void assertRoleAbsent(RoleList results, Role expectedRole) {
//
//        List<Role> rolesFound = StreamSupport.stream(results.spliterator(), false)
//            .filter {role -> role.id == expectedRole.id}
//            .collect(Collectors.toList())
//
//        assertThat(rolesFound, hasSize(0))
//    }
//

    static void assertUserPresent(List<User> users, User expectedUser) {

        List<User> usersFound = StreamSupport.stream(users.spliterator(), false)
                .filter { user -> user.id == expectedUser.id }
                .collect(Collectors.toList())

        assertThat(usersFound, hasSize(1))
    }

//    static <T extends Resource, C extends CollectionResource<T>> void assertPresent(C results, T expected) {
//
//        List<T> resourcesFound = StreamSupport.stream(results.spliterator(), false)
//                .filter {listItem -> listItem.id == expected.id}
//                .collect(Collectors.toList())
//
//        assertThat(resourcesFound, hasSize(1))
//    }
//
//    static <T extends Resource, C extends CollectionResource<T>> void assertNotPresent(C results, T expected) {
//
//        List<T> resourcesFound = StreamSupport.stream(results.spliterator(), false)
//                .filter {listItem -> listItem.id == expected.id}
//                .collect(Collectors.toList())
//
//        assertThat(resourcesFound, hasSize(0))
//    }
//
//    static <T extends Resource, C extends CollectionResource<T>> void assertLinkedObjectPresent(C results, T expected) {
//
//        List<T> resourcesFound = StreamSupport.stream(results.spliterator(), false)
//                .filter { listItem ->
//                    ((listItem.primary.name == expected.primary.name) &&
//                            (listItem.associated.name == expected.associated.name))}
//                .collect(Collectors.toList())
//
//        assertThat(resourcesFound, hasSize(1))
//    }
//
//    static void assertGroupTargetPresent(User user, Group group, Role role) {
//        def groupTargets = user.listGroupTargets(role.id)
//
//        assertThat "GroupTarget Present not found in User role",
//                StreamSupport.stream(groupTargets.spliterator(), false)
//                    .filter{ groupTarget -> groupTarget.profile.name == group.profile.name}
//                    .findFirst().isPresent()
//    }
//
//    static void assertUserInGroup(User user, Group group) {
//        assertThat "User was not found in group.", StreamSupport.stream(group.listUsers().spliterator(), false)
//                .filter{ listUser -> listUser.id == user.id}
//                .findFirst().isPresent()
//    }
//
//    static void assertUserInGroup(User user, Group group, int times, long delayInMilliseconds, boolean present=true) {
//        for (int ii=0; ii<times; ii++) {
//
//            sleep(delayInMilliseconds)
//
//            if (present == StreamSupport.stream(group.listUsers().spliterator(), false)
//                    .filter{ listUser -> listUser.id == user.id}
//                    .findFirst().isPresent()) {
//                return
//            }
//        }
//
//        if (present) Assert.fail("User not found in group")
//        if (!present) Assert.fail("User found in group")
//    }
//
//    static void assertUserNotInGroup(User user, Group group) {
//        assertThat "User was found in group.", !StreamSupport.stream(group.listUsers().spliterator(), false)
//                .filter{ listUser -> listUser.id == user.id}
//                .findFirst().isPresent()
//    }
//
//    static void assertUserNotInGroup(User user, Group group, int times, long delayInMilliseconds, boolean present=true) {
//        for (int ii=0; ii<times; ii++) {
//
//            sleep(delayInMilliseconds)
//
//            if (present == !StreamSupport.stream(group.listUsers().spliterator(), false)
//                .filter{ listUser -> listUser.id == user.id}
//                .findFirst().isPresent()) {
//                return
//            }
//        }
//
//        if (present) Assert.fail("User not found in group")
//        if (!present) Assert.fail("User found in group")
//    }
//
//    static def ignoring = { Class<? extends Throwable> catchMe, Closure callMe ->
//        try {
//            callMe.call()
//        } catch(e) {
//            if (!e.class.isAssignableFrom(catchMe)) {
//                throw e
//            }
//        }
//    }
//
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

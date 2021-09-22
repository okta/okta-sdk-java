/*
 * Copyright 2017-Present Okta, Inc.
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
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertGroupPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.assertUserInGroup
import static com.okta.sdk.tests.it.util.Util.assertUserNotInGroup
import static com.okta.sdk.tests.it.util.Util.validateGroup
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/groups}.
 * @since 0.5.0
 */
class GroupsIT extends ITSupport implements CrudTestSupport {

    @Override
    def create(Client client) {
        return GroupBuilder.instance()
                .setName("java-sdk-it-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)
    }

    @Override
    def read(Client client, String id) {
        return client.getGroup(id)
    }

    @Override
    void update(Client client, def group) {
        group.getProfile().description = "IT created Group - Updated"
        group.update()
    }

    @Override
    void assertUpdate(Client client, def resource) {
        assertThat resource.getProfile().description, equalTo("IT created Group - Updated")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listGroups().iterator()
    }

    @Test (groups = "group2")
    @Scenario("list-groups")
    void listGroupsTest() {

        String groupName = "sdk-java List Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(createdGroup)

        validateGroup(createdGroup, groupName)

        // 2. List all groups and find the group created
        assertGroupPresent(client.listGroups(), createdGroup)
    }

    @Test (groups = "group2")
    @Scenario("search-groups")
    void searchGroupTest() {

        String groupName = "Search Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Search the group by name
        assertPresent(client.listGroups(groupName, null, null), group)
    }

    @Test (groups = "bacon")
    @Scenario("search-groups-by-search-parameter")
    void searchGroupsBySearchParameterTest() {

        String groupName = "Search Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Search the group by search parameter
        Thread.sleep(getTestOperationDelay())
        assertPresent(client.listGroups(null, "profile.name eq \"" + groupName + "\"", null), group)
    }

    @Test (groups = "group2")
    @Scenario("update-group")
    void updateGroupTest() {

        String groupName = "Update Test Group ${uniqueTestName}"
        String groupNameUpdated = "${groupName} - Updated"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Update the group name and description
        group.getProfile().name = groupNameUpdated
        group.getProfile().description = 'Description updated'
        group.update()

        validateGroup(group, groupNameUpdated, 'Description updated')
    }

    @Test (groups = "group2")
    @Scenario("group-user-operations")
    void groupUserOperationsTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'With-Group'
        def email = "john-with-group-${uniqueTestName}@example.com"

        // 1. Create a user and a group
        def user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password.toCharArray())
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        String groupName = "Group-Member API Test Group ${uniqueTestName}"

        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Add user to the group and validate user present in group
        user.addToGroup(group.getId())

        assertUserInGroup(user, group, 5, getTestOperationDelay())

        // 3. Remove user from group and validate user removed
        group.removeUser(user.getId())

        assertUserNotInGroup(user, group, 5, getTestOperationDelay())
    }
}

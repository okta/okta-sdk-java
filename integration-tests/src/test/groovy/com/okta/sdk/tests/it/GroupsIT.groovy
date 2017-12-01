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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
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
 * Tests for /api/v1/groups
 * @since 0.5.0
 */
class GroupsIT implements CrudTestSupport {

    @Override
    def create(Client client) {
        return GroupBuilder.instance()
                .setName("my-user-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)
    }

    @Override
    def read(Client client, String id) {
        return client.getGroup(id)
    }

    @Override
    void update(Client client, def group) {
        group.profile.description = "IT created Group - Updated"
        group.update()
    }

    @Override
    void assertUpdate(Client client, def resource) {
        assertThat resource.profile.description, equalTo("IT created Group - Updated")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listGroups().iterator()
    }

    @Test
    @Scenario("list-groups")
    @TestResources(groups = "List Test Group")
    void listGroupsTest() {

        String groupName = "List Test Group"

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)

        validateGroup(createdGroup, groupName);

        // 2. List all groups and find the group created
        assertGroupPresent(client.listGroups(), createdGroup)
    }

    @Test
    @Scenario("search-groups")
    @TestResources(groups = "Search Test Group")
    void searchGroupTest() {

        String groupName = "Search Test Group"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Search the group by name
        assertPresent(client.listGroups(groupName, null, null), group)
    }

    @Test
    @Scenario("update-group")
    @TestResources(groups = ["Update Test Group", "Update Test Group - Updated"])
    void updateGroupTest() {

        String groupName = "Update Test Group"
        String groupNameUpdated = "Update Test Group - Updated"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Update the group name and description
        group.profile.name = groupNameUpdated
        group.profile.description = 'Description updated'
        group.update()

        validateGroup(group, groupNameUpdated, 'Description updated')
    }

    @Test
    @Scenario("group-user-operations")
    @TestResources(
            users = "john-with-group@example.com",
            groups = ["Group-Member API Test Group"])
    void groupUserOperationsTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'With-Group'
        def email = 'john-with-group@example.com'

        // 1. Create a user and a group
        def user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        String groupName = "Group-Member API Test Group"

        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Add user to the group and validate user present in group
        user.addToGroup(group.id)
        assertUserInGroup(user, group)

        // 3. Remove user from group and validate user removed
        group.removeUser(user.id)
        assertUserNotInGroup(user, group)
    }
}
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
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import com.okta.sdk.tests.it.util.ClientProvider
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

class GroupIT implements ClientProvider {

    @Test
    @Scenario("group-get-and-stats")
    @TestResources(groups="Get Test Group")
    void getGroupAndStatsTest() {

        Client client = getClient()

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName("Get Test Group")
            .buildAndCreate(client)

        // 2. Get the group by ID
        Group group = client.getGroup(createdGroup.id)
        validateGroup(group, createdGroup)

        // 3. Get group stats
//        Stats stats = createdGroup.getStatus();
//        assertThat(stats.usersCount, equalTo(0))
//        assertThat(stats.appsCount, equalTo(0))
//        assertThat(stats.groupPushMappingsCount, equalTo(0))

        // 4. Delete the group
        group.delete()
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

        // 3. Delete the group
        createdGroup.delete()
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
        def email = 'jjohn-with-group@example.com'

        // 1. Create a user and a group
        def user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .buildAndCreate(client, false)
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

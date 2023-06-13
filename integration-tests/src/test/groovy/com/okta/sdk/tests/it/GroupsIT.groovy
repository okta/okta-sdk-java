/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.UserBuilder
import org.openapitools.client.api.GroupApi
import org.openapitools.client.api.UserApi
import org.openapitools.client.model.Group
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertGroupPresent
import static com.okta.sdk.tests.it.util.Util.assertUserInGroup
import static com.okta.sdk.tests.it.util.Util.assertUserNotInGroup
import static com.okta.sdk.tests.it.util.Util.validateGroup
import static com.okta.sdk.tests.it.util.Util.validateUser

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.5.0
 */
class GroupsIT extends ITSupport {

    GroupApi groupApi = new GroupApi(getClient())
    UserApi userApi = new UserApi(getClient())

    @Test (groups = "group1")
    @Scenario("list-groups")
    void listGroupsTest() {

        String groupName = "sdk-java List Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(createdGroup)

        validateGroup(createdGroup, groupName)

        // 2. List all groups and find the group created
        assertGroupPresent(groupApi.listGroups(null, null, null, null, null, null, null, null), createdGroup)
    }

    @Test (groups = "group1")
    @Scenario("search-groups")
    void searchGroupTest() {

        String groupName = "Search Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Search the group by name
        assertGroupPresent(groupApi.listGroups(groupName, null, null, null, null, null, null, null), group)
    }

    // disabled due to an issue in backend infrastructure at the moment
    @Test (enabled = false, groups = "bacon")
    @Scenario("search-groups-by-search-parameter")
    void searchGroupsBySearchParameterTest() {

        String groupName = "Search Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Search the group by search parameter
        Thread.sleep(getTestOperationDelay())

        assertGroupPresent(groupApi.listGroups(null, null, null, null, null, "profile.name eq \"" + groupName + "\"", null, null), group)
    }

    @Test
    @Scenario("update-group")
    void updateGroupTest() {

        String groupName = "Update Test Group ${uniqueTestName}"
        String groupNameUpdated = "${groupName} - Updated"

        // 1. Create a new group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Update the group name and description
        group.getProfile().name = groupNameUpdated
        group.getProfile().description = 'Description updated'

        groupApi.replaceGroup(group.getId(), group)

        validateGroup(group, groupNameUpdated, 'Description updated')
    }

    @Test (groups = "group1")
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
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        String groupName = "Group-Member API Test Group ${uniqueTestName}"

        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Add user to the group and validate user present in group
        groupApi.assignUserToGroup(group.getId(), user.getId())

        assertUserInGroup(user, group, groupApi, 5, getTestOperationDelay())

        // 3. Remove user from group and validate user removed
        groupApi.unassignUserFromGroup(group.getId(), user.getId())

        assertUserNotInGroup(user, group, groupApi,5, getTestOperationDelay())
    }
}
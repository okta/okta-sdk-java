/*
 * Copyright 2020-Present Okta, Inc.
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

import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.role.AssignRoleRequest
import com.okta.sdk.resource.role.RoleType
import com.okta.sdk.resource.user.Role
import com.okta.sdk.resource.user.RoleList
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertGroupPresent
import static com.okta.sdk.tests.it.util.Util.assertGroupAbsent
import static com.okta.sdk.tests.it.util.Util.assertRolePresent
import static com.okta.sdk.tests.it.util.Util.assertRoleAbsent
import static com.okta.sdk.tests.it.util.Util.validateGroup

/**
 * Tests for /api/v1/groups/roles
 * @since 2.0.0
 */
class GroupRolesIT extends ITSupport {

    @Test
    @Scenario("list-roles-assigned-to-group")
    void listRolesAssignedToGroupTest() {

        String groupName = "sdk-java List Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(createdGroup)

        validateGroup(createdGroup, groupName)

        // 2. List all groups and find the group created
        assertGroupPresent(client.listGroups(), createdGroup)

        // 3. Create assign role requests
        AssignRoleRequest userAdminRoleRequest = client.instantiate(AssignRoleRequest)
        userAdminRoleRequest.setType(RoleType.USER_ADMIN)

        AssignRoleRequest appAdminRoleRequest = client.instantiate(AssignRoleRequest)
        appAdminRoleRequest.setType(RoleType.APP_ADMIN)

        // 4. Assign USER_ADMIN & APP_ADMIN roles
        Role userAdminRole = createdGroup.assignRole(userAdminRoleRequest)
        Role appAdminRole = createdGroup.assignRole(appAdminRoleRequest)

        // 5. List assigned group roles and check the assignments
        RoleList roles = client.listGroupAssignedRoles(createdGroup.getId())

        assertRolePresent(roles, userAdminRole)
        assertRolePresent(roles, appAdminRole)
    }

    @Test
    @Scenario("unassigned-roles-for-group")
    void unassignRoleForGroupTest() {

        String groupName = "sdk-java List Test Group ${uniqueTestName}"

        // 1. Create a new group
        Group createdGroup = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(createdGroup)

        validateGroup(createdGroup, groupName)

        // 2. List all groups and find the group created
        assertGroupPresent(client.listGroups(), createdGroup)

        // 3. Create a USER_ADMIN role request
        AssignRoleRequest userAdminRoleRequest = client.instantiate(AssignRoleRequest)
        userAdminRoleRequest.setType(RoleType.USER_ADMIN)

        // 4. Assign the above role
        Role userAdminRole = createdGroup.assignRole(userAdminRoleRequest)

        // 5. List assigned group roles and check the assignments
        assertRolePresent(client.listGroupAssignedRoles(createdGroup.getId()), userAdminRole)

        // 6. Remove the role from group
        client.removeRoleFromGroup(createdGroup.getId(), userAdminRole.getId())

        // 7. List assigned group roles and check the assignments
        assertRoleAbsent(client.listGroupAssignedRoles(createdGroup.getId()), userAdminRole)
    }

    @Test
    @Scenario("list-group-targets-for-group")
    void listGroupTargetsForGroupTest() {

        String groupName1 = "sdk-java List Test Group ${uniqueTestName}-1"
        String groupName2 = "sdk-java List Test Group ${uniqueTestName}-2"

        // 1. Create two groups
        Group createdGroup1 = GroupBuilder.instance()
            .setName(groupName1)
            .buildAndCreate(client)
        Group createdGroup2 = GroupBuilder.instance()
            .setName(groupName2)
            .buildAndCreate(client)
        registerForCleanup(createdGroup1)
        registerForCleanup(createdGroup2)

        validateGroup(createdGroup1, groupName1)
        validateGroup(createdGroup2, groupName2)

        // 2. List all groups and find the groups created
        assertGroupPresent(client.listGroups(), createdGroup1)
        assertGroupPresent(client.listGroups(), createdGroup2)

        // 3. Create a USER_ADMIN role & assign it to the first group
        Role userAdminRole = createdGroup1.assignRole(client.instantiate(AssignRoleRequest)
            .setType(RoleType.USER_ADMIN))

        // 4. Add second group as the admin target for the user admin role
        userAdminRole.addAdminGroup(createdGroup2.getId())

        // 5. Verify the same
        assertGroupPresent(client.listGroupTargetsForGroupRole(createdGroup1.getId(), userAdminRole.getId()), createdGroup2)
    }

    @Test
    @Scenario("list-group-targets-for-group")
    void removeGroupTargetFromGroupAdministratorRoleGivenToGroupTest() {

        String groupName1 = "sdk-java List Test Group ${uniqueTestName}-1"
        String groupName2 = "sdk-java List Test Group ${uniqueTestName}-2"
        String groupName3 = "sdk-java List Test Group ${uniqueTestName}-3"

        // 1. Create three groups
        Group createdGroup1 = GroupBuilder.instance()
            .setName(groupName1)
            .buildAndCreate(client)
        Group createdGroup2 = GroupBuilder.instance()
            .setName(groupName2)
            .buildAndCreate(client)
        Group createdGroup3 = GroupBuilder.instance()
            .setName(groupName3)
            .buildAndCreate(client)
        registerForCleanup(createdGroup1)
        registerForCleanup(createdGroup2)
        registerForCleanup(createdGroup3)

        validateGroup(createdGroup1, groupName1)
        validateGroup(createdGroup2, groupName2)
        validateGroup(createdGroup3, groupName3)

        // 2. List all groups and find the groups created
        assertGroupPresent(client.listGroups(), createdGroup1)
        assertGroupPresent(client.listGroups(), createdGroup2)
        assertGroupPresent(client.listGroups(), createdGroup3)

        // 3. Create a USER_ADMIN role & assign it to the first group
        Role userAdminRole = createdGroup1.assignRole(client.instantiate(AssignRoleRequest)
            .setType(RoleType.USER_ADMIN))

        // 4. Add second group as the admin target for the user admin role
        userAdminRole.addAdminGroup(createdGroup2.getId())

        // 5. Add third group as the admin target for the user admin role
        userAdminRole.addAdminGroup(createdGroup3.getId())

        // 6. Verify if both the above targets (group 2 & group 3) are added
        assertGroupPresent(client.listGroupTargetsForGroupRole(createdGroup1.getId(), userAdminRole.getId()), createdGroup2)
        assertGroupPresent(client.listGroupTargetsForGroupRole(createdGroup1.getId(), userAdminRole.getId()), createdGroup3)

        // 7. Remove group 2 from the target
        client.removeGroupTargetFromGroupAdministratorRoleGivenToGroup(createdGroup1.getId(), userAdminRole.getId(), createdGroup2.getId())

        // 8. Verify that group 2 is removed from the target
        assertGroupAbsent(client.listGroupTargetsForGroupRole(createdGroup1.getId(), userAdminRole.getId()), createdGroup2)
    }
}

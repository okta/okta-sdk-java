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
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertGroupPresent
import static com.okta.sdk.tests.it.util.Util.assertGroupAbsent
import static com.okta.sdk.tests.it.util.Util.validateGroup
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat

/**
 * Tests for {@code /api/v1/users/roles}.
 * @since 2.0.0
 */
class UserRolesIT extends ITSupport {

    @Test
    @Scenario("assign-super-admin-role-to-user")
    void assignSuperAdminRoleToUserTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Role'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Assign SUPER_ADMIN role
        AssignRoleRequest assignRoleRequest = client.instantiate(AssignRoleRequest)
        assignRoleRequest.setType(RoleType.SUPER_ADMIN)

        Role assignedRole = user.assignRole(assignRoleRequest)
        assertThat("Incorrect RoleType", assignedRole.getType() == RoleType.SUPER_ADMIN)
    }

    @Test
    @Scenario("group-targets-for-role")
    void groupTargetsForRoleTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Role'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Create two groups
        String groupName1 = "Group-Member API Test Group ${uniqueTestName}-1"
        String groupName2 = "Group-Member API Test Group ${uniqueTestName}-2"

        Group group1 = GroupBuilder.instance()
            .setName(groupName1)
            .buildAndCreate(client)
        Group group2 = GroupBuilder.instance()
            .setName(groupName2)
            .buildAndCreate(client)
        registerForCleanup(group1)
        registerForCleanup(group2)

        validateGroup(group1, groupName1)
        validateGroup(group2, groupName2)

        // 3. Assign a role
        AssignRoleRequest assignRoleRequest = client.instantiate(AssignRoleRequest)
        assignRoleRequest.setType(RoleType.USER_ADMIN)

        Role superAdminRole = user.assignRole(assignRoleRequest)

        // 4. Add the created groups as role targets
        // Need 2 groups, because if you remove the last one it throws an (expected) exception.
        user.addGroupTarget(superAdminRole.getId(), group1.getId())
        user.addGroupTarget(superAdminRole.getId(), group2.getId())

        assertGroupPresent(user.listGroupTargets(superAdminRole.getId()), group1)
        assertGroupPresent(user.listGroupTargets(superAdminRole.getId()), group2)

        // 5. Remove the target
        user.removeGroupTarget(superAdminRole.getId(), group1.getId())

        assertGroupAbsent(user.listGroupTargets(superAdminRole.getId()), group1)
    }
}



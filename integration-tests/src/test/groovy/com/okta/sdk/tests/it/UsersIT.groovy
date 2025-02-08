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

import com.okta.sdk.impl.resource.DefaultGroupBuilder
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationGroupsApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.PolicyApi
import com.okta.sdk.resource.api.RoleAssignmentAUserApi
import com.okta.sdk.resource.api.RoleBTargetAdminApi
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserCredApi
import com.okta.sdk.resource.api.UserLifecycleApi
import com.okta.sdk.resource.api.UserResourcesApi
import com.okta.sdk.resource.api.UserTypeApi
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.model.AddGroupRequest
import com.okta.sdk.resource.model.Application
import com.okta.sdk.resource.model.ApplicationGroupAssignment
import com.okta.sdk.resource.model.AssignRoleToUser201Response
import com.okta.sdk.resource.model.AssignRoleToUserRequest
import com.okta.sdk.resource.model.AuthenticationProvider
import com.okta.sdk.resource.model.AuthenticationProviderType
import com.okta.sdk.resource.model.ChangePasswordRequest
import com.okta.sdk.resource.model.CreateUserTypeRequest
import com.okta.sdk.resource.model.ForgotPasswordResponse
import com.okta.sdk.resource.model.Group
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner
import com.okta.sdk.resource.model.OktaUserGroupProfile
import com.okta.sdk.resource.model.PasswordCredential
import com.okta.sdk.resource.model.PasswordPolicyPasswordSettings
import com.okta.sdk.resource.model.PasswordPolicyPasswordSettingsAge
import com.okta.sdk.resource.model.PasswordPolicyRule
import com.okta.sdk.resource.model.PasswordPolicyRuleAction
import com.okta.sdk.resource.model.PasswordPolicyRuleActions
import com.okta.sdk.resource.model.PasswordPolicyRuleConditions
import com.okta.sdk.resource.model.PasswordPolicySettings
import com.okta.sdk.resource.model.Policy
import com.okta.sdk.resource.model.PolicyAccess
import com.okta.sdk.resource.model.PolicyNetworkCondition
import com.okta.sdk.resource.model.PolicyNetworkConnection
import com.okta.sdk.resource.model.RecoveryQuestionCredential
import com.okta.sdk.resource.model.RoleType
import com.okta.sdk.resource.model.SelfServicePasswordResetAction
import com.okta.sdk.resource.model.UpdateUserRequest
import com.okta.sdk.resource.model.User
import com.okta.sdk.resource.model.UserCredentials
import com.okta.sdk.resource.model.UserGetSingleton
import com.okta.sdk.resource.model.UserProfile
import com.okta.sdk.resource.model.UserStatus
import com.okta.sdk.resource.model.UserType

import com.okta.sdk.resource.user.UserBuilder

import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import com.okta.sdk.resource.client.ApiException
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Duration
import java.util.stream.Collectors

import static com.okta.sdk.tests.it.util.Util.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
/**
 * Tests for {@code /api/v1/users}.
 * @since 0.5.0
 */
class UsersIT extends ITSupport {

    ApplicationApi applicationApi = new ApplicationApi(getClient())
    GroupApi groupApi = new GroupApi(getClient())
    ApplicationGroupsApi applicationGroupsApi = new ApplicationGroupsApi(getClient())
    PolicyApi policyApi = new PolicyApi(getClient())
    UserApi userApi = new UserApi(getClient())
    UserCredApi userCredApi = new UserCredApi(getClient())
    RoleAssignmentAUserApi roleAssignmentApi = new RoleAssignmentAUserApi(getClient())

    @Test
    void doCrudTest() {

        User user = randomUser()
        registerForCleanup(user)
        assertThat(user.getStatus(), equalTo(UserStatus.PROVISIONED))

        // deactivate
        userApi.deleteUser(user.getId(), false, null)

        UserGetSingleton retrievedUser = userApi.getUser(user.getId(), null, "false")
        assertThat(retrievedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))
    }

    @Test (groups = "group2")
    @Scenario("create-user-with-user-type")
    void createUserWithUserTypeTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Activate'
        def email = "john-activate=${uniqueTestName}@example.com"

        UserTypeApi userTypeApi = new UserTypeApi(getClient())

        // 1. Create a User Type
        String name = "java_sdk_it_" + RandomStringUtils.randomAlphanumeric(15)

        CreateUserTypeRequest userType = new CreateUserTypeRequest()
            .name(name)
            .displayName(name)
            .description(name + "_test_description")
        UserType createdUserType = userTypeApi.createUserType(userType)
        registerForCleanup(createdUserType)

        assertThat(createdUserType.getId(), notNullValue())

        // 2. Create a user with the User Type
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
        // See https://developer.okta.com/docs/reference/api/user-types/#specify-the-user-type-of-a-new-user
            .setType(createdUserType.getId())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 3. Assert User Type
        assertThat(user.getType().getId(), equalTo(createdUserType.getId()))

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        // 4.Verify user in list of active users
        List<User> users = userApi.listUsers(null, null, null, 200, 'status eq \"ACTIVE\"', null, null, null)
        assertThat(users, hasSize(greaterThan(0)))
        //assertPresent(users, user)
    }

    @Test (groups = "group2")
    @Scenario("user-activate")
    void userActivateTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Activate'
        def email = "john-activate=${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Activate the user and verify user in list of active users
        UserLifecycleApi userLifecycleApi = new UserLifecycleApi(getClient())
        userLifecycleApi.activateUser(user.getId(), false)

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        List<User> users = userApi.listUsers(null, null, null, null, 'status eq \"ACTIVE\"', null, null, null)

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        assertUserPresent(users, user)
    }

    @Test (groups = "group2")
    @Scenario("user-with-special-character-in-email")
    void userWithSpecialCharacterInEmailTest() {
        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'hashtag'
        def email = "john-${uniqueTestName}#@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Thread.sleep(getTestOperationDelay())

        UserGetSingleton retrievedUser = userApi.getUser(email,null, "false")
        assertThat(retrievedUser.id, equalTo(user.id))
    }

    @Test (groups = "group2")
    @Scenario("user-with-special-character-in-name")
    void userWithSpecialCharacterInNameTest() {
        def password = 'Passw0rd!2@3#'
        def firstName = 'Árvíztűrő'
        def lastName = 'Tükörfúrógép'
        def displayName = firstName + ' ' + lastName
        def email = "john-${uniqueTestName}@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setDisplayName(displayName)
            .setPassword(password.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        UserGetSingleton retrievedUser = userApi.getUser(email,null, "false")
        assertThat(retrievedUser.id, equalTo(user.id))
        assertThat(retrievedUser.profile.firstName, equalTo(firstName))
        assertThat(retrievedUser.profile.lastName, equalTo(lastName))
        assertThat(retrievedUser.profile.displayName, equalTo(displayName))
    }

    @Test (groups = "group2")
    @Scenario("user-with-plus-sign-in-email")
    void filterUserTest() {
        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'hashtag'
        def email = "john+${uniqueTestName}@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Thread.sleep(getTestOperationDelay())

        List<User> users =
            userApi.listUsers(null, null, null, null, "profile.login eq \"${email}\"", null, null, null)

        assertUserPresent(users, user)
    }

    @Test (groups = "group2")
    @Scenario("user-role-assign")
    void roleAssignTest() {

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
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Assign USER_ADMIN role to the user
        AssignRoleToUserRequest assignRoleToUserRequest = new AssignRoleToUserRequest()
        assignRoleToUserRequest.setType(RoleType.USER_ADMIN.name())

        AssignRoleToUser201Response role = roleAssignmentApi.assignRoleToUser(user.getId(), assignRoleToUserRequest, true)

        // 3. List roles for the user and verify added role
        List<ListGroupAssignedRoles200ResponseInner> roles = roleAssignmentApi.listAssignedRolesForUser(user.getId(), null)
        Optional<ListGroupAssignedRoles200ResponseInner> match = roles.stream().filter(r -> r.getId() == role.getId()).findAny()
        assertThat(match.isPresent(), is(true))

        // 4. Verify added role
        assertThat(roleAssignmentApi.getUserAssignedRole(user.getId(), role.getId()).getId(), equalTo(role.getId()))

        // 5. Remove role for the user
        roleAssignmentApi.unassignRoleFromUser(user.getId(), role.getId())

        // 6. List roles for user and verify role was removed
        roles = roleAssignmentApi.listAssignedRolesForUser(user.getId(), null)
        match = roles.stream().filter(r -> r.getId() != role.getId()).findAny()
        assertThat(match.isPresent(), is(false))
    }

    @Test (groups = "group2")
    @Scenario("user-change-password")
    void changePasswordTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Change-Password'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the user's password
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
        PasswordCredential passwordCredentialOld = new PasswordCredential()
        passwordCredentialOld.setValue("Passw0rd!2@3#")
        changePasswordRequest.setOldPassword(passwordCredentialOld)
        PasswordCredential passwordCredentialNew = new PasswordCredential()
        passwordCredentialNew.setValue("!2@3#Passw0rd")
        changePasswordRequest.setNewPassword(passwordCredentialNew)

        UserCredentials userCredentials = userCredApi.changePassword(user.getId(), changePasswordRequest, true)
        assertThat userCredentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA)

        // 3. make the test recording happy, and call a get on the user
        // TODO: fix har file
        userApi.getUser(user.getId(),null,"false")
    }

    @Test(expectedExceptions = ApiException, groups = "group2")
    void changeStrictPasswordTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Change-Password'
        def email = "john-${uniqueTestName}@example.com"

        String name = "java-sdk-it-${UUID.randomUUID().toString()}"

        OktaUserGroupProfile oktaUserGroupProfile = new OktaUserGroupProfile()
        oktaUserGroupProfile.setName(name)
        oktaUserGroupProfile.setDescription(name)
        AddGroupRequest addGroupRequest = new AddGroupRequest()
        addGroupRequest.setProfile(oktaUserGroupProfile)
        Group createdGroup = groupApi.addGroup(addGroupRequest)
        registerForCleanup(createdGroup)

        assertThat createdGroup, notNullValue()
        assertThat createdGroup.getId(), notNullValue()

        Policy policy = randomPasswordPolicy(createdGroup.getId())
        PasswordPolicyPasswordSettingsAge passwordPolicyPasswordSettingsAge = new PasswordPolicyPasswordSettingsAge()
        passwordPolicyPasswordSettingsAge.setMinAgeMinutes(Duration.ofDays(2L).toMinutes() as int)

        PasswordPolicyPasswordSettings passwordPolicyPasswordSettings = new PasswordPolicyPasswordSettings()
        passwordPolicyPasswordSettings.setAge(passwordPolicyPasswordSettingsAge)

        PasswordPolicySettings passwordPolicySettings = new PasswordPolicySettings()
        passwordPolicySettings.setPassword(passwordPolicyPasswordSettings)

        policy.setSettings(passwordPolicySettings)

        policy = policyApi.replacePolicy(policy.getId(), policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()

        PolicyNetworkCondition policyNetworkCondition = new PolicyNetworkCondition()
        policyNetworkCondition.setConnection(PolicyNetworkConnection.ANYWHERE)

        PasswordPolicyRuleConditions passwordPolicyRuleConditions = new PasswordPolicyRuleConditions()
        passwordPolicyRuleConditions.setNetwork(policyNetworkCondition)

        PasswordPolicyRuleAction passwordPolicyRuleActionAllow = new PasswordPolicyRuleAction()
        passwordPolicyRuleActionAllow.access(PolicyAccess.ALLOW)

        PasswordPolicyRuleAction passwordPolicyRuleActionDeny = new PasswordPolicyRuleAction()
        passwordPolicyRuleActionDeny.access(PolicyAccess.DENY)

        PasswordPolicyRuleActions passwordPolicyRuleActions = new PasswordPolicyRuleActions()
        passwordPolicyRuleActions.setPasswordChange(passwordPolicyRuleActionAllow)

        SelfServicePasswordResetAction selfServicePasswordResetAction = new SelfServicePasswordResetAction()
        selfServicePasswordResetAction.setAccess(PolicyAccess.ALLOW)

        passwordPolicyRuleActions.setSelfServicePasswordReset(selfServicePasswordResetAction)
        passwordPolicyRuleActions.setSelfServiceUnlock(passwordPolicyRuleActionDeny)

        PasswordPolicyRule passwordPolicyRule = new PasswordPolicyRule()
        passwordPolicyRule.setConditions(passwordPolicyRuleConditions)
        passwordPolicyRule.setActions(passwordPolicyRuleActions)
        passwordPolicyRule.setName(policyRuleName)

        policyApi.createPolicyRule(policy.getId(), passwordPolicyRule, null, true)

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .setGroups(createdGroup.getId())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the user's password
        PasswordCredential passwordCredentialOld = new PasswordCredential()
            .value("Passw0rd!2@3#")
        PasswordCredential passwordCredentialNew = new PasswordCredential()
            .value("!2@3#Passw0rd")
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
            .oldPassword(passwordCredentialOld)
            .newPassword(passwordCredentialNew)

        // would throw a HTTP 403
        userCredApi.changePassword(user.getId(), changePasswordRequest, true)

        UserCredentials userCredentials = userCredApi.changePassword(user.getId(), changePasswordRequest, false)
        assertThat userCredentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA)

        // 3. make the test recording happy, and call a get on the user
        // TODO: fix har file
        userApi.getUser(user.getId(), null, "false")
    }

    @Test(expectedExceptions = ApiException, groups = "group2")
    @Scenario("user-change-recovery-question")
    void changeRecoveryQuestionTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Change-Recovery-Question'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user with password & recovery question
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the recovery question
        RecoveryQuestionCredential recoveryQuestionCredential = new RecoveryQuestionCredential()
        recoveryQuestionCredential.setQuestion("How many roads must a man walk down?")
        recoveryQuestionCredential.setAnswer("forty two")

        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue("Passw0rd!2@3#")

        UserCredentials userCredentials = new UserCredentials()
        userCredentials.setPassword(passwordCredential)
        userCredentials.setRecoveryQuestion(recoveryQuestionCredential)

        userCredentials = userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)

        assertThat userCredentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA)
        assertThat userCredentials.getRecoveryQuestion().question, equalTo('How many roads must a man walk down?')

        // 3. Update the user password through updated recovery question
        userCredentials.getPassword().value = '!2@3#Passw0rd'.toCharArray()
        userCredentials.getRecoveryQuestion().answer = 'forty two'

        // below would throw HTTP 403 exception
        userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)

        // 4. make the test recording happy, and call a get on the user
        // TODO: fix har file
        userApi.getUser(user.getId(), null, "false")
    }

    @Test (groups = "bacon")
    void forgotPasswordTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Forgot-Password'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        ForgotPasswordResponse forgotPasswordResponse = userCredApi.forgotPassword(user.getId(), false)
        assertThat forgotPasswordResponse.getResetPasswordUrl(), containsString("/reset-password/")
    }

    @Test (groups = "group2")
    @Scenario("user-expire-password")
    void expirePasswordTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Expire-Password'
        def email = "john-${uniqueTestName}@example.com"

        UserApi userApi = new UserApi(getClient())

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Expire the user's password
        User updatedUser = userCredApi.expirePassword(user.getId())
        assertThat updatedUser, notNullValue()
        assertThat updatedUser.getStatus().name(), equalTo("PASSWORD_EXPIRED")
    }

    @Test (groups = "group2")
    @Scenario("user-get-reset-password-url")
    void resetPasswordUrlTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Get-Reset-Password-URL'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Get the reset password link
        ForgotPasswordResponse forgotPasswordResponse = userCredApi.forgotPassword(user.getId(), false)
        assertThat forgotPasswordResponse.getResetPasswordUrl(), notNullValue()
    }

    @Test (groups = "group2")
    @Scenario("user-get")
    void getUserTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Get-User'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(createdUser)

        validateUser(createdUser, firstName, lastName, email)

        // 2. Get the user by user ID
        UserGetSingleton user = userApi.getUser(createdUser.getId(), null, "false")
        validateUser(user, firstName, lastName, email)

        // 3. Get the user by user login
        UserGetSingleton userByLogin = userApi.getUser(createdUser.getProfile().getLogin(), null, "false")
        validateUser(userByLogin, firstName, lastName, email)

        // 3. deactivate the user
        userApi.deleteUser(user.getId(), false, null)

        // 4. delete the user
        // Second delete API call is required to delete the user
        // Ref: https://developer.okta.com/docs/api/openapi/okta-management/management/tag/User/#tag/User/operation/deleteUser
        userApi.deleteUser(user.getId(), false, null)

        Thread.sleep(getTestOperationDelay())

        // 4. get user expect 404
        expect(ApiException) {
            userApi.getUser(email, null, "false")
        }
    }

    @Test (groups = "group2")
    @Scenario("user-group-target-role")
    void groupTargetRoleTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Group-Target'
        def email = "john-${uniqueTestName}@example.com"
        def groupName = "Group-Target Test Group ${uniqueTestName}"

        RoleAssignmentAUserApi roleAssignmentApi = new RoleAssignmentAUserApi(getClient())
        RoleBTargetAdminApi roleTargetApi = new RoleBTargetAdminApi(getClient())

        //RoleAssignmentApi roleAssignmentApi = new RoleAssignmentApi(getClient())
        //RoleTargetApi roleTargetApi = new RoleTargetApi(getClient())

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Assign USER_ADMIN role to the user
        AssignRoleToUserRequest assignRoleRequest = new AssignRoleToUserRequest()
        assignRoleRequest.setType(RoleType.USER_ADMIN.name())

        AssignRoleToUser201Response role = roleAssignmentApi.assignRoleToUser(user.getId(), assignRoleRequest, true)

        // 3. Add Group Target to User Admin Role
        roleTargetApi.assignGroupTargetToUserRole(user.getId(), role.getId(), group.getId())

        // 4. List Group Targets for Role
        List<Group> groupTargets = roleTargetApi.listGroupTargetsForRole(user.getId(), role.getId(), null, null)
        Optional<Group> match = groupTargets.stream().filter(g -> g.getProfile().getName() == group.getProfile().getName()).findAny()
        assertThat(match.isPresent(), is(true))

        // 5. Remove Group Target from Admin User Role and verify removed
        // Note: Don’t remove the last group target from a role assignment, as this causes an exception.
        // To get around this, create a new group and add this group target to user admin role

        def adminGroupName = "Group-Target User Admin Test Group ${uniqueTestName}"

        Group adminGroup = GroupBuilder.instance()
            .setName(adminGroupName)
            .buildAndCreate(groupApi)
        registerForCleanup(adminGroup)
        validateGroup(adminGroup, adminGroupName)

        roleTargetApi.assignGroupTargetToUserRole(user.getId(), role.getId(), adminGroup.getId())
        roleTargetApi.unassignGroupTargetFromUserAdminRole(user.getId(), role.getId(), adminGroup.getId())

        groupTargets = roleTargetApi.listGroupTargetsForRole(user.getId(), role.getId(), null, null)
        match = groupTargets.stream().filter(g -> g.getProfile().getName() == group.getProfile().getName()).findAny()
        assertThat(match.isPresent(), is(true))
    }

    @Test (groups = "group2")
    @Scenario("user-profile-update")
    void userProfileUpdate() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def middleName = 'Mark'
        def lastName = 'Profile-Update'
        def displayName = 'Joe Mark'
        def honorificPrefix = 'Mr'
        def city = 'Boston'
        def countryCode = 'US'
        def preferredLanguage = 'en-US'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName)
            .setDisplayName(displayName)
            .setHonorificPrefix(honorificPrefix)
            .setCity(city)
            .setCountryCode(countryCode)
            .setPreferredLanguage(preferredLanguage)
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        assertThat(user.getProfile().middleName, equalTo(middleName))
        assertThat(user.getProfile().honorificPrefix, equalTo(honorificPrefix))
        assertThat(user.getProfile().city, equalTo(city))
        assertThat(user.getProfile().countryCode, equalTo(countryCode))
        assertThat(user.getProfile().preferredLanguage, equalTo(preferredLanguage))

        def originalLastUpdated = user.lastUpdated

        // 2. Update the user profile and verify that profile was updated
        // Need to wait 1 second here as that is the minimum time resolution of the 'lastUpdated' field
        sleep(1000)

        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
        UserProfile userProfile = new UserProfile()
        userProfile.setNickName("Batman")

        // Note: Custom user profile properties can be added with something like below, but
        // the respective property must first be associated to the User schema.
        // You can use the Profile Editor in your Org administrator UI or the Schemas API
        // to manage schema extensions.
        //userProfile.getAdditionalProperties().put("foo", "bar")

        updateUserRequest.setProfile(userProfile)

        userApi.updateUser(user.getId(), updateUserRequest, true)

        UserGetSingleton updatedUser = userApi.getUser(user.getId(), null, "false")

        assertThat(updatedUser.lastUpdated, greaterThan(originalLastUpdated))
        assertThat(updatedUser.getProfile(), not(user.getProfile()))
        assertThat(updatedUser.getProfile().getProperties().get("nickName"), equalTo("Batman"))
        //assertThat(userProfile.getAdditionalProperties().get("foo"), equalTo("bar"))
    }

    @Test (groups = "group2")
    @Scenario("user-suspend")
    void userSuspendTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Suspend'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Suspend the user and verify user in list of suspended users
        UserLifecycleApi userLifecycleApi = new UserLifecycleApi(getClient())
        userLifecycleApi.suspendUser(user.getId())

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        List<User> users = userApi.listUsers(null, null, null, null, 'status eq \"SUSPENDED\"',null, null, null)
        Optional<User> match = users.stream().filter(u -> u.getId() == user.getId()).findAny()
        assertThat(match.isPresent(), is(true))

        // 3. Unsuspend the user and verify user in list of active users
        userLifecycleApi.unsuspendUser(user.getId())

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        users = userApi.listUsers(null, null, null, null, 'status eq \"ACTIVE\"',null, null, null)
        match = users.stream().filter(u -> u.getId() == user.getId()).findAny()
        assertThat(match.isPresent(), is(true))
    }

    @Test(groups = "group2")
    void getUserInvalidUserId() {
        def userId = "invalid-user-id-${uniqueTestName}@example.com"

        expect(ApiException) {
            userApi.getUser(userId, null, "false")
        }
    }

    @Test (groups = "group2")
    void createUserWithGroups() {

        def groupName = "Group-to-Assign-${uniqueTestName}"
        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'Create-User-With-Group'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(groupApi)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Create a user
        User createUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(false)
            .addGroup(group.getId())
            .buildAndCreate(userApi)
        registerForCleanup(createUser)
        validateUser(createUser, firstName, lastName, email)

        UserResourcesApi userResourcesApi = new UserResourcesApi(getClient())
\
        List<Group> groups = userResourcesApi.listUserGroups(createUser.getId()).stream().collect(Collectors.toList())
        assertThat groups, allOf(hasSize(2))
        assertThat groups.get(0).getProfile().name, equalTo("Everyone")
        assertThat groups.get(1).getId(), equalTo(group.id)
    }

    @Test (groups = "group3")
    void importUserWithSha512Password() {

        def salt = "aSalt"
        def hashedPassword = hashPassword("aPassword", salt)
        def email = "joe.coder+${uniqueTestName}@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setSha512PasswordHash(hashedPassword, salt, "PREFIX")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        assertThat user.getCredentials(), notNullValue()
        assertThat user.getCredentials().getProvider().getType(), equalTo(AuthenticationProviderType.IMPORT)
    }

    @Test (groups = "group3")
    @Scenario("user-forgot-password-set-new-password")
    void forgotPasswordSetNewPasswordTest() {

        def password = 'OldPassw0rd!2@3#'
        def firstName = 'John'
        def middleName = 'Mark'
        def lastName = 'Forgot-Password'
        def email = "john-${uniqueTestName}@example.com"

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName)
            .setPassword(password.toCharArray())
            .setActive(true)
            .setSecurityQuestion("How many roads must a man walk down?")
            .setSecurityQuestionAnswer("forty two")
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue("NewPassw0rd!2@3#")

        RecoveryQuestionCredential recoveryQuestionCredential = new RecoveryQuestionCredential()
        recoveryQuestionCredential.setQuestion("How many roads must a man walk down?")
        recoveryQuestionCredential.setAnswer("forty two")

        UserCredentials userCredentials = new UserCredentials()
        userCredentials.setPassword(passwordCredential)
        userCredentials.setRecoveryQuestion(recoveryQuestionCredential)

        userCredentials = userCredApi.forgotPasswordSetNewPassword(user.getId(), userCredentials, false)
        assertThat userCredentials.getRecoveryQuestion().getQuestion(), equalTo("How many roads must a man walk down?")
        assertThat userCredentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA)
    }

    @Test (groups = "group3")
    void userWithAuthenticationProviderTest() {

        def firstName = 'John'
        def lastName = 'Forgot-Password'
        def email = "john-${uniqueTestName}@example.com"

        AuthenticationProvider authenticationProvider = new AuthenticationProvider()
        authenticationProvider.setType(AuthenticationProviderType.OKTA)

        // 1. Create a user
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setLogin(email)
            .setProvider(authenticationProvider)
            .buildAndCreate(userApi)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        assertThat user.getCredentials(), notNullValue()
        assertThat user.getCredentials().getProvider().getType(), equalTo(AuthenticationProviderType.OKTA)
    }

    @Test (groups = "group3")
    void testListGroupAssignmentsWithExpand() {

        for (int i = 1; i < 30; i++) {
            registerForCleanup(new DefaultGroupBuilder().setName("test-group_" + i + "_${uniqueTestName}").buildAndCreate(groupApi))
        }

        //  Fetch the GroupAssignment list in two requests
        def expandParameter = "group"

        List<Application> applicationList =
            applicationApi.listApplications( null, null, null, null, null, null, null)

        Application application = applicationList.first()

        List<ApplicationGroupAssignment> groupAssignments =
            applicationGroupsApi.listApplicationGroupAssignments(application.getId(), null, null, null, expandParameter)

        //  Make sure both pages (all resources) contain an expand parameter
        for (ApplicationGroupAssignment groupAssignment : groupAssignments) {
            def embedded = groupAssignment.getEmbedded()
            assertThat(embedded, notNullValue())
            assertThat(embedded.get(expandParameter), notNullValue())
            assertThat(embedded.get(expandParameter).get("type"), notNullValue())
            assertThat(embedded.get(expandParameter).get("profile"), notNullValue())
        }
    }

    private static String hashPassword(String password, String salt) {
        def messageDigest = MessageDigest.getInstance("SHA-512")
        messageDigest.update(StringUtils.getBytes(salt, StandardCharsets.UTF_8))
        def bytes = messageDigest.digest(StringUtils.getBytes(password, StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(bytes)
    }
}
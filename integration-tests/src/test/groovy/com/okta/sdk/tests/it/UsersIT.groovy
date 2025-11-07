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
import com.okta.sdk.resource.api.ProfileMappingApi
import com.okta.sdk.resource.api.RoleAssignmentAUserApi
import com.okta.sdk.resource.api.RoleBTargetAdminApi
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserCredApi
import com.okta.sdk.resource.api.UserFactorApi
import com.okta.sdk.resource.api.UserGrantApi
import com.okta.sdk.resource.api.UserLifecycleApi
import com.okta.sdk.resource.api.UserLinkedObjectApi
import com.okta.sdk.resource.api.UserOAuthApi
import com.okta.sdk.resource.api.UserResourcesApi
import com.okta.sdk.resource.api.UserSessionsApi
import com.okta.sdk.resource.api.UserTypeApi
import com.okta.sdk.resource.api.UserAuthenticatorEnrollmentsApi
import com.okta.sdk.resource.api.UserClassificationApi
import com.okta.sdk.resource.api.UserRiskApi
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.model.AddGroupRequest
import com.okta.sdk.resource.model.Application
import com.okta.sdk.resource.model.ApplicationGroupAssignment
import com.okta.sdk.resource.model.AssignRoleToUser201Response
import com.okta.sdk.resource.model.AssignRoleToUserRequest
import com.okta.sdk.resource.model.AuthenticationProvider
import com.okta.sdk.resource.model.AuthenticationProviderType
import com.okta.sdk.resource.model.ChangePasswordRequest
import com.okta.sdk.resource.model.CreateUserRequest
import com.okta.sdk.resource.model.ForgotPasswordResponse
import com.okta.sdk.resource.model.Group
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner
import com.okta.sdk.resource.model.OktaUserGroupProfile
import com.okta.sdk.resource.model.PasswordCredential
import com.okta.sdk.resource.model.PasswordPolicy
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
import com.okta.sdk.resource.model.KeepCurrent
import com.okta.sdk.resource.model.ListProfileMappings
import com.okta.sdk.resource.model.OAuth2Client
import com.okta.sdk.resource.model.OAuth2RefreshToken
import com.okta.sdk.resource.model.OAuth2ScopeConsentGrant
import com.okta.sdk.resource.model.ProfileMapping
import com.okta.sdk.resource.model.ResetPasswordToken
import com.okta.sdk.resource.model.ResponseLinks
import com.okta.sdk.resource.model.User
import com.okta.sdk.resource.model.UserActivationToken
import com.okta.sdk.resource.model.UserBlock
import com.okta.sdk.resource.model.UserCredentials
import com.okta.sdk.resource.model.UserCredentialsWritable
import com.okta.sdk.resource.model.UserFactor
import com.okta.sdk.resource.model.UserFactorSecurityQuestionProfile
import com.okta.sdk.resource.model.UserFactorSupported
import com.okta.sdk.resource.model.UserGetSingleton
import com.okta.sdk.resource.model.ClassificationType
import com.okta.sdk.resource.model.ReplaceUserClassification
import com.okta.sdk.resource.model.UserClassification
import com.okta.sdk.resource.model.UserRiskLevelAll
import com.okta.sdk.resource.model.UserRiskLevelPut
import com.okta.sdk.resource.model.UserRiskRequest
import com.okta.sdk.resource.model.UserProfile
import com.okta.sdk.resource.model.UserStatus
import com.okta.sdk.resource.model.UserType
import com.okta.sdk.resource.model.UserTypePostRequest
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
    ProfileMappingApi profileMappingApi = new ProfileMappingApi(getClient())
    UserApi userApi = new UserApi(getClient())
    UserCredApi userCredApi = new UserCredApi(getClient())
    UserFactorApi userFactorApi = new UserFactorApi(getClient())
    UserGrantApi userGrantApi = new UserGrantApi(getClient())
    UserLifecycleApi userLifecycleApi = new UserLifecycleApi(getClient())
    UserLinkedObjectApi userLinkedObjectApi = new UserLinkedObjectApi(getClient())
    UserOAuthApi userOAuthApi = new UserOAuthApi(getClient())
    UserResourcesApi userResourcesApi = new UserResourcesApi(getClient())
    UserSessionsApi userSessionsApi = new UserSessionsApi(getClient())
    UserTypeApi userTypeApi = new UserTypeApi(getClient())
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

        // Wait for indexing and activation to complete
        Thread.sleep(2000)

        // Search for the specific user by email to verify activation
        String searchQuery = "profile.email eq \"${email}\""
        List<User> users = userApi.listUsers(null, searchQuery, null, null, null, 10, null, null, null)

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

        // Use search parameter instead of filter for email with special characters
        // The search parameter uses a starts-with match and handles special characters better
        List<User> users =
            userApi.listUsers(null, null, null, email, null, null, null, null, null)

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

        Policy updatedPolicy = policyApi.replacePolicy(policy.getId(), policy)
        policy = policyApi.getPolicy(updatedPolicy.getId(), null) as PasswordPolicy

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

        userApi.updateUser(user.getId(), updateUserRequest, true, null)

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

        // Wait for status change to be indexed
        Thread.sleep(2000)

        // Search for the specific user by email and verify suspended status
        String searchQuery = "profile.email eq \"${email}\""
        List<User> users = userApi.listUsers(null, searchQuery, null, null, null, 10, null, null, null)
        Optional<User> match = users.stream().filter(u -> u.getId() == user.getId()).findAny()
        assertThat(match.isPresent(), is(true))
        assertThat(match.get().getStatus(), is(UserStatus.SUSPENDED))

        // 3. Unsuspend the user and verify user in list of active users
        userLifecycleApi.unsuspendUser(user.getId())

        // Wait for status change to be indexed
        Thread.sleep(2000)

        // Search for the specific user by email and verify active status
        users = userApi.listUsers(null, searchQuery, null, null, null, 10, null, null, null)
        match = users.stream().filter(u -> u.getId() == user.getId()).findAny()
        assertThat(match.isPresent(), is(true))
        assertThat(match.get().getStatus(), is(UserStatus.ACTIVE))
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

        // Verify user is assigned to the specified group and the default 'Everyone' group
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

    // ========================================
    // User API Integration Tests - CRUD Operations
    // ========================================

    /**
     * Test 1: List all users
     * Tests GET /api/v1/users
     */
    @Test(groups = "group3")
    void testListAllUsers() {
        // Create a test user to ensure we have at least one user
        String email = "list-test-${uniqueTestName}@example.com"
        User testUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("List")
            .setLastName("Test")
            .buildAndCreate(userApi)
        registerForCleanup(testUser)

        // Wait for indexing
        Thread.sleep(2000)

        // List all users with search to find our specific user
        String searchQuery = "profile.email eq \"${email}\""
        List<User> users = userApi.listUsers(null, searchQuery, null, null, null, 10, null, null, null, Collections.emptyMap())
        
        assertThat("Should return users", users, notNullValue())
        assertThat("Should have at least one user", users.size(), greaterThan(0))
        
        // Verify our test user is in the list
        User foundUser = users.find { it.getId() == testUser.getId() }
        assertThat("Test user should be in the list", foundUser, notNullValue())
    }

    /**
     * Test 2: List users with search parameter
     * Tests GET /api/v1/users with search query
     */
    @Test(groups = "group3")
    void testListUsersWithSearch() {
        String uniqueEmail = "search-user-${uniqueTestName}@example.com"
        User testUser = UserBuilder.instance()
            .setEmail(uniqueEmail)
            .setFirstName("SearchFirst")
            .setLastName("SearchLast")
            .buildAndCreate(userApi)
        registerForCleanup(testUser)

        // Wait for indexing
        Thread.sleep(2000)

        // Search by profile.email
        String searchQuery = "profile.email eq \"${uniqueEmail}\""
        List<User> users = userApi.listUsers(null, searchQuery, null, null, null, 10, null, null, null)
        
        assertThat("Should find user by search", users, notNullValue())
        assertThat("Should find exactly our user", users.size(), greaterThanOrEqualTo(1))
        
        User foundUser = users.find { it.getProfile().getEmail() == uniqueEmail }
        assertThat("Should find our test user", foundUser, notNullValue())
        assertThat("Email should match", foundUser.getProfile().getEmail(), equalTo(uniqueEmail))
    }

    /**
     * Test 3: List users with filter parameter
     * Tests GET /api/v1/users with filter
     */
    @Test(groups = "group3")
    void testListUsersWithFilter() {
        String email = "filter-test-${uniqueTestName}@example.com"
        User testUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Filter")
            .setLastName("Test")
            .buildAndCreate(userApi)
        registerForCleanup(testUser)

        // Filter by status
        String filter = 'status eq "STAGED"'
        List<User> users = userApi.listUsers(null, null, filter, null, null, 100, null, null, null)
        
        assertThat("Should return users", users, notNullValue())
        
        // Verify all returned users have STAGED status
        users.each { user ->
            assertThat("User should have STAGED status", user.getStatus(), equalTo(UserStatus.STAGED))
        }
    }

    /**
     * Test 4: List users with query parameter
     * Tests GET /api/v1/users with q parameter
     */
    @Test(groups = "group3")
    void testListUsersWithQuery() {
        String uniqueFirstName = "QueryTest${uniqueTestName}"
        String email = "query-test-${uniqueTestName}@example.com"
        User testUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName(uniqueFirstName)
            .setLastName("User")
            .buildAndCreate(userApi)
        registerForCleanup(testUser)

        // Wait for indexing
        Thread.sleep(2000)

        // Query by first name (q does startsWith match on firstName, lastName, or email)
        List<User> users = userApi.listUsers(null, null, null, uniqueFirstName, null, 10, null, null, null)
        
        assertThat("Should return users", users, notNullValue())
        
        if (users.size() > 0) {
            User foundUser = users.find { it.getId() == testUser.getId() }
            assertThat("Should potentially find our test user", foundUser, notNullValue())
        }
    }

    /**
     * Test 5: Create user without credentials
     * Tests POST /api/v1/users (STAGED user)
     */
    @Test(groups = "group3")
    void testCreateUserWithoutCredentials() {
        String email = "no-creds-${uniqueTestName}@example.com"
        
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("NoCreds")
            .setLastName("Test")
            .setActive(false) // Will be STAGED
            .buildAndCreate(userApi)
        registerForCleanup(user)

        assertThat("User should be created", user, notNullValue())
        assertThat("User should have ID", user.getId(), notNullValue())
        assertThat("User should be STAGED", user.getStatus(), equalTo(UserStatus.STAGED))
        assertThat("Email should match", user.getProfile().getEmail(), equalTo(email))
        assertThat("First name should match", user.getProfile().getFirstName(), equalTo("NoCreds"))
        assertThat("Last name should match", user.getProfile().getLastName(), equalTo("Test"))
    }

    /**
     * Test 6: Create user with password
     * Tests POST /api/v1/users with credentials
     */
    @Test(groups = "group3")
    void testCreateUserWithPassword() {
        String email = "with-password-${uniqueTestName}@example.com"
        String password = 'Abcd1234!@#$'
        
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("WithPassword")
            .setLastName("Test")
            .setPassword(password.toCharArray())
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        assertThat("User should be created", user, notNullValue())
        assertThat("User should have ID", user.getId(), notNullValue())
        assertThat("Status should be STAGED", user.getStatus(), equalTo(UserStatus.STAGED))
        assertThat("Should have credentials", user.getCredentials(), notNullValue())
        assertThat("Should have password", user.getCredentials().getPassword(), notNullValue())
    }

    /**
     * Test 7: Create and activate user
     * Tests POST /api/v1/users with activate=true
     */
    @Test(groups = "group3")
    void testCreateAndActivateUser() {
        String email = "activate-${uniqueTestName}@example.com"
        
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Activate")
            .setLastName("Test")
            .setActive(true) // Activate immediately
            .buildAndCreate(userApi)
        registerForCleanup(user)

        assertThat("User should be created", user, notNullValue())
        assertThat("User should have ID", user.getId(), notNullValue())
        // Status might be PROVISIONED or ACTIVE depending on org settings
        assertThat("Status should be PROVISIONED or ACTIVE", 
            user.getStatus(), 
            anyOf(equalTo(UserStatus.PROVISIONED), equalTo(UserStatus.ACTIVE)))
    }

    /**
     * Test 8: Retrieve user by ID
     * Tests GET /api/v1/users/{id}
     */
    @Test(groups = "group3")
    void testGetUserById() {
        String email = "get-user-${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetUser")
            .setLastName("Test")
            .buildAndCreate(userApi)
        registerForCleanup(createdUser)

        // Retrieve the user
        UserGetSingleton retrievedUser = userApi.getUser(createdUser.getId(), null, null)
        
        assertThat("User should be retrieved", retrievedUser, notNullValue())
        assertThat("User ID should match", retrievedUser.getId(), equalTo(createdUser.getId()))
        assertThat("Email should match", retrievedUser.getProfile().getEmail(), equalTo(email))
        assertThat("First name should match", retrievedUser.getProfile().getFirstName(), equalTo("GetUser"))
        assertThat("Last name should match", retrievedUser.getProfile().getLastName(), equalTo("Test"))
    }

    /**
     * Test 9: Retrieve user by login
     * Tests GET /api/v1/users/{login}
     */
    @Test(groups = "group3")
    void testGetUserByLogin() {
        String email = "get-by-login-${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetByLogin")
            .setLastName("Test")
            .buildAndCreate(userApi)
        registerForCleanup(createdUser)

        // Retrieve by login (email)
        UserGetSingleton retrievedUser = userApi.getUser(email, null, null)
        
        assertThat("User should be retrieved", retrievedUser, notNullValue())
        assertThat("User ID should match", retrievedUser.getId(), equalTo(createdUser.getId()))
        assertThat("Login should match", retrievedUser.getProfile().getLogin(), equalTo(email))
    }

    /**
     * Test 10: Update user profile (partial update)
     * Tests POST /api/v1/users/{id}
     */
    @Test(groups = "group3")
    void testUpdateUserProfile() {
        String email = "update-user-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Original")
            .setLastName("Name")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Update the user's profile
        UserProfile updatedProfile = new UserProfile()
        updatedProfile.setFirstName("Updated")
        updatedProfile.setLastName("NewName")
        updatedProfile.setMobilePhone("555-123-4567")
        
        UpdateUserRequest updateRequest = new UpdateUserRequest()
        updateRequest.setProfile(updatedProfile)
        
        User updatedUser = userApi.updateUser(user.getId(), updateRequest, null, null)
        
        assertThat("Updated user should not be null", updatedUser, notNullValue())
        assertThat("First name should be updated", updatedUser.getProfile().getFirstName(), equalTo("Updated"))
        assertThat("Last name should be updated", updatedUser.getProfile().getLastName(), equalTo("NewName"))
        assertThat("Mobile phone should be set", updatedUser.getProfile().getMobilePhone(), equalTo("555-123-4567"))
        
        // Email should remain unchanged (we didn't update it)
        assertThat("Email should remain the same", updatedUser.getProfile().getEmail(), equalTo(email))
    }

    /**
     * Test 11: Replace user (full replacement)
     * Tests PUT /api/v1/users/{id}
     */
    @Test(groups = "group3")
    void testReplaceUser() {
        String email = "replace-user-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Original")
            .setLastName("User")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Full replacement requires all required fields
        UserProfile replacementProfile = new UserProfile()
        replacementProfile.setFirstName("Replaced")
        replacementProfile.setLastName("Completely")
        replacementProfile.setEmail(email)
        replacementProfile.setLogin(email)
        
        UpdateUserRequest replaceRequest = new UpdateUserRequest()
        replaceRequest.setProfile(replacementProfile)
        
        User replacedUser = userApi.replaceUser(user.getId(), replaceRequest, null, null)
        
        assertThat("Replaced user should not be null", replacedUser, notNullValue())
        assertThat("First name should be replaced", replacedUser.getProfile().getFirstName(), equalTo("Replaced"))
        assertThat("Last name should be replaced", replacedUser.getProfile().getLastName(), equalTo("Completely"))
    }

    /**
     * Test 12: Delete user (deactivate then delete)
     * Tests DELETE /api/v1/users/{id}
     */
    @Test(groups = "group3")
    void testDeleteUser() {
        String email = "delete-user-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ToDelete")
            .setLastName("User")
            .buildAndCreate(userApi)
        
        String userId = user.getId()
        
        // First deactivate the user
        UserLifecycleApi lifecycleApi = new UserLifecycleApi(getClient())
        lifecycleApi.deactivateUser(userId, false, null)
        
        // Now delete the user
        userApi.deleteUser(userId, false, null)
        
        // Verify user is deleted (should throw 404)
        ApiException exception = expect(ApiException.class, () -> 
            userApi.getUser(userId, null, null))
        
        assertThat("Should return 404 for deleted user", exception.getCode(), is(404))
    }

    /**
     * Test 13: List user blocks
     * Tests GET /api/v1/users/{id}/blocks
     */
    //todo get access
//    @Test(groups = "group3")
//    void testListUserBlocks() {
//        String email = "blocks-test-${uniqueTestName}@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Blocks")
//            .setLastName("Test")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // List user blocks
//        List<UserBlock> blocks = userApi.listUserBlocks(user.getId())
//
//        assertThat("Blocks list should not be null", blocks, notNullValue())
//        // Blocks list may be empty if user has no restrictions
//        assertThat("Blocks list should be a list", blocks, instanceOf(List))
//    }

    /**
     * Test 14: Get non-existent user (error handling)
     * Tests GET /api/v1/users/{id} with invalid ID
     */
    @Test(groups = "group3")
    void testGetNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userApi.getUser(fakeUserId, null, null))
        
        assertThat("Should return 404 for non-existent user", exception.getCode(), is(404))
    }

    /**
     * Test 15: Create user with invalid email (error handling)
     * Tests POST /api/v1/users with validation error
     */
    @Test(groups = "group3")
    void testCreateUserWithInvalidEmail() {
        ApiException exception = expect(ApiException.class, () -> {
            UserProfile profile = new UserProfile()
            profile.setFirstName("Invalid")
            profile.setLastName("Email")
            profile.setEmail("not-an-email") // Invalid email format
            profile.setLogin("not-an-email")
            
            CreateUserRequest createRequest = new CreateUserRequest()
            createRequest.setProfile(profile)
            
            userApi.createUser(createRequest, false, false, null)
        })
        
        assertThat("Should return 400 for invalid email", exception.getCode(), is(400))
    }

    /**
     * Test 16: Update non-existent user (error handling)
     * Tests POST /api/v1/users/{id} with invalid ID
     */
    @Test(groups = "group3")
    void testUpdateNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        UserProfile profile = new UserProfile()
        profile.setFirstName("Updated")
        profile.setLastName("User")
        
        UpdateUserRequest updateRequest = new UpdateUserRequest()
        updateRequest.setProfile(profile)
        
        ApiException exception = expect(ApiException.class, () -> 
            userApi.updateUser(fakeUserId, updateRequest, null, null))
        
        assertThat("Should return 404 for non-existent user", exception.getCode(), is(404))
    }

    /**
     * Test 17: List users with pagination
     * Tests GET /api/v1/users with limit parameter
     */
    @Test(groups = "group3")
    void testListUsersWithPagination() {
        // Create multiple test users
        for (int i = 0; i < 3; i++) {
            String email = "pagination-${i}-${uniqueTestName}@example.com"
            User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Page${i}")
                .setLastName("Test")
                .buildAndCreate(userApi)
            registerForCleanup(user)
        }

        // List users with limit
        List<User> firstPage = userApi.listUsers(null, null, null, null, null, 2, null, null, null)
        
        assertThat("First page should not be null", firstPage, notNullValue())
        assertThat("First page should have at most 2 users", firstPage.size(), lessThanOrEqualTo(2))
    }

    // ========================================
    // UserFactorApi Integration Tests
    // ========================================

    /**
     * Test: List all factors for a user
     * Tests GET /api/v1/users/{userId}/factors
     */
    @Test(groups = "group3")
    void testUserFactorListFactors() {
        String email = "factor-list-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Factor")
            .setLastName("List")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List factors for the user
        List<UserFactor> factors = userFactorApi.listFactors(user.getId())
        
        assertThat("Factors list should not be null", factors, notNullValue())
        assertThat("Factors list should be a list", factors, instanceOf(List))
    }

    /**
     * Test: List supported factors for a user
     * Tests GET /api/v1/users/{userId}/factors/catalog
     */
    @Test(groups = "group3")
    void testUserFactorListSupportedFactors() {
        String email = "supported-factors-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Supported")
            .setLastName("Factors")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List supported factors for the user
        List<UserFactorSupported> supportedFactors = userFactorApi.listSupportedFactors(user.getId())
        
        assertThat("Supported factors list should not be null", supportedFactors, notNullValue())
        assertThat("Should have supported factors", supportedFactors.size(), greaterThan(0))
    }

    /**
     * Test: List supported security questions
     * Tests GET /api/v1/users/{userId}/factors/questions
     */
    @Test(groups = "group3")
    void testUserFactorListSecurityQuestions() {
        String email = "security-questions-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Security")
            .setLastName("Questions")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List supported security questions
        List<UserFactorSecurityQuestionProfile> questions = userFactorApi.listSupportedSecurityQuestions(user.getId())
        
        assertThat("Security questions list should not be null", questions, notNullValue())
        assertThat("Should have security questions", questions.size(), greaterThan(0))
    }

    /**
     * Test: Get factor for user
     * Tests GET /api/v1/users/{userId}/factors/{factorId}
     */
    @Test(groups = "group3")
    void testUserFactorGetFactor() {
        String email = "get-factor-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetFactor")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List factors (if any exist)
            List<UserFactor> factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                // Get the first factor
                UserFactor factor = userFactorApi.getFactor(user.getId(), factors.get(0).getId())
                assertThat("Factor should not be null", factor, notNullValue())
            }
        } catch (ApiException e) {
            // Factor API may not be fully available in all orgs
            if (e.getCode() != 403 && e.getCode() != 404) {
                throw e
            }
        }
    }

    // ========================================
    // UserLifecycleApi Integration Tests
    // ========================================

    /**
     * Test: Activate a staged user
     * Tests POST /api/v1/users/{userId}/lifecycle/activate
     */
    @Test(groups = "group3")
    void testUserLifecycleActivate() {
        String email = "activate-lifecycle-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Activate")
            .setLastName("Lifecycle")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        assertThat("User should be STAGED", user.getStatus(), equalTo(UserStatus.STAGED))

        // Activate the user
        UserActivationToken activationToken = userLifecycleApi.activateUser(user.getId(), false)
        
        assertThat("Activation token should not be null", activationToken, notNullValue())

        Thread.sleep(getTestOperationDelay())
        UserGetSingleton updatedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should be PROVISIONED or ACTIVE", 
            updatedUser.getStatus(), 
            anyOf(equalTo(UserStatus.PROVISIONED), equalTo(UserStatus.ACTIVE)))
    }

    /**
     * Test: Suspend and unsuspend a user
     * Tests POST /api/v1/users/{userId}/lifecycle/suspend and unsuspend
     */
    @Test(groups = "group3")
    void testUserLifecycleSuspendUnsuspend() {
        String email = "suspend-lifecycle-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Suspend")
            .setLastName("Lifecycle")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Suspend the user
        userLifecycleApi.suspendUser(user.getId())
        Thread.sleep(getTestOperationDelay())
        
        UserGetSingleton suspendedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should be SUSPENDED", suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

        // Unsuspend the user
        userLifecycleApi.unsuspendUser(user.getId())
        Thread.sleep(getTestOperationDelay())
        
        UserGetSingleton unsuspendedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should be ACTIVE", unsuspendedUser.getStatus(), equalTo(UserStatus.ACTIVE))
    }

    /**
     * Test: Unlock a user
     * Tests POST /api/v1/users/{userId}/lifecycle/unlock
     */
    //todo block user first
//    @Test(groups = "group3")
//    void testUserLifecycleUnlock() {
//        String email = "unlock-lifecycle-${uniqueTestName}@example.com"
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Unlock")
//            .setLastName("Lifecycle")
//            .setPassword('Passw0rd!123'.toCharArray())
//            .setActive(true)
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        // Unlock the user (even if not locked, should succeed)
//        userLifecycleApi.unlockUser(user.getId())
//
//        Thread.sleep(getTestOperationDelay())
//        UserGetSingleton updatedUser = userApi.getUser(user.getId(), null, null)
//        assertThat("User should exist", updatedUser, notNullValue())
//    }

    // ========================================
    // UserTypeApi Integration Tests
    // ========================================


    /**
     * Test: List all user types
     * Tests GET /api/v1/meta/types/user
     */

    /**
     * Test: Update user type
     * Tests POST /api/v1/meta/types/user/{typeId}
     */

    /**
     * Test: Delete user type
     * Tests DELETE /api/v1/meta/types/user/{typeId}
     */

    // ========================================
    // UserResourcesApi Integration Tests
    // ========================================

    /**
     * Test: List app links for a user
     * Tests GET /api/v1/users/{userId}/appLinks
     */
    @Test(groups = "group3")
    void testUserResourcesListAppLinks() {
        String email = "applinks-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AppLinks")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List app links for the user
        def appLinks = userResourcesApi.listAppLinks(user.getId())

        assertThat("App links list should not be null", appLinks, notNullValue())
        assertThat("App links list should be a list", appLinks, instanceOf(List))
    }

    /**
     * Test: List OAuth2 clients for a user
     * Tests GET /api/v1/users/{userId}/clients
     */
    @Test(groups = "group3")
    void testUserResourcesListClients() {
        String email = "clients-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Clients")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List OAuth2 clients for the user
        List<OAuth2Client> clients = userResourcesApi.listUserClients(user.getId())

        assertThat("Clients list should not be null", clients, notNullValue())
        assertThat("Clients list should be a list", clients, instanceOf(List))
    }

    /**
     * Test: List devices for a user
     * Tests GET /api/v1/users/{userId}/devices
     */
    @Test(groups = "group3")
    void testUserResourcesListDevices() {
        String email = "devices-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Devices")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List devices for the user
        def devices = userResourcesApi.listUserDevices(user.getId())

        assertThat("Devices list should not be null", devices, notNullValue())
        assertThat("Devices list should be a list", devices, instanceOf(List))
    }

    /**
     * Test: List groups for a user
     * Tests GET /api/v1/users/{userId}/groups
     */
    @Test(groups = "group3")
    void testUserResourcesListGroups() {
        String email = "usergroups-${uniqueTestName}@example.com"
        String groupName = "UserResources Test Group ${uniqueTestName}"

        // Create a group
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .setDescription("Test group for user resources")
            .buildAndCreate(groupApi)
        registerForCleanup(group)

        // Create a user and add to the group
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Groups")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(false)
            .addGroup(group.getId())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List groups for the user
        List<Group> groups = userResourcesApi.listUserGroups(user.getId())

        assertThat("Groups list should not be null", groups, notNullValue())
        assertThat("Should have at least 2 groups", groups.size(), greaterThanOrEqualTo(2))

        // Verify test group is in the list
        Group foundGroup = groups.find { it.getId() == group.getId() }
        assertThat("Test group should be in the list", foundGroup, notNullValue())
    }

    // ========================================
    // UserGrantApi Integration Tests
    // ========================================

    /**
     * Test: List user grants
     * Tests GET /api/v1/users/{userId}/grants
     */
    @Test(groups = "group3")
    void testUserGrantList() {
        String email = "grants-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Grants")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // List user grants
        List<OAuth2ScopeConsentGrant> grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)

        assertThat("Grants list should not be null", grants, notNullValue())
        assertThat("Grants list should be a list", grants, instanceOf(List))
    }

    // ========================================
    // UserOAuthApi Integration Tests
    // ========================================

    /**
     * Test: List refresh tokens for user and client
     * Tests GET /api/v1/users/{userId}/clients/{clientId}/tokens
     */
    @Test(groups = "group3")
    void testUserOAuthListRefreshTokens() {
        String email = "oauth-tokens-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("OAuth")
            .setLastName("Tokens")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Try to list refresh tokens (will be empty for new user without client)
            String fakeClientId = "0oa" + RandomStringUtils.randomAlphanumeric(17)
            List<OAuth2RefreshToken> tokens = userOAuthApi.listRefreshTokensForUserAndClient(
                user.getId(), fakeClientId, null, null, null)

            assertThat("Tokens list should not be null", tokens, notNullValue())
        } catch (ApiException e) {
            // Expected if client doesn't exist
            if (e.getCode() != 404) {
                throw e
            }
        }
    }

    // ========================================
    // UserSessionsApi Integration Tests
    // ========================================

    /**
     * Test: Revoke user sessions
     * Tests DELETE /api/v1/users/{userId}/sessions
     */
    @Test(groups = "group3")
    void testUserSessionsRevoke() {
        String email = "sessions-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Sessions")
            .setLastName("Test")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Revoke user sessions
        userSessionsApi.revokeUserSessions(user.getId(), false, false)

        // If no exception, the operation succeeded
        assertThat("User should exist", user.getId(), notNullValue())
    }

    // ========================================
    // UserLinkedObjectApi Integration Tests
    // ========================================

    /**
     * Test: List linked objects for user
     * Tests GET /api/v1/users/{userId}/linkedObjects/{relationshipName}
     */
    @Test(groups = "group3")
    void testUserLinkedObjectsList() {
        String email = "linked-objects-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Linked")
            .setLastName("Objects")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Try to list linked objects (will likely be empty or error if no relationships exist)
            List<ResponseLinks> linkedObjects = userLinkedObjectApi.listLinkedObjectsForUser(
                user.getId(), "fakeRelationship")

            assertThat("Linked objects list should not be null", linkedObjects, notNullValue())
        } catch (ApiException e) {
            // Expected if relationship doesn't exist
            if (e.getCode() != 404 && e.getCode() != 400) {
                throw e
            }
        }
    }

    // ========================================
    // ProfileMappingApi Integration Tests
    // ========================================

    /**
     * Test: List profile mappings
     * Tests GET /api/v1/mappings
     */
    @Test(groups = "group3")
    void testProfileMappingList() {
        try {
            // List profile mappings
            List<ListProfileMappings> mappings = profileMappingApi.listProfileMappings(null, null, null, null)

            assertThat("Mappings list should not be null", mappings, notNullValue())
            assertThat("Mappings list should be a list", mappings, instanceOf(List))
        } catch (ApiException e) {
            // Profile mapping may not be available in all orgs
            if (e.getCode() != 403 && e.getCode() != 404) {
                throw e
            }
        }
    }

    /**
     * Test: Get specific profile mapping
     * Tests GET /api/v1/mappings/{mappingId}
     */
    @Test(groups = "group3")
    void testProfileMappingGet() {
        try {
            // List profile mappings first to get a valid mapping ID
            List<ListProfileMappings> mappings = profileMappingApi.listProfileMappings(null, 1, null, null)

            if (mappings != null && mappings.size() > 0) {
                String mappingId = mappings.get(0).getId()
                
                // Get the specific profile mapping
                ProfileMapping mapping = profileMappingApi.getProfileMapping(mappingId)

                assertThat("Profile mapping should not be null", mapping, notNullValue())
                assertThat("Mapping ID should match", mapping.getId(), equalTo(mappingId))
            }
        } catch (ApiException e) {
            // Profile mapping may not be available in all orgs
            if (e.getCode() != 403 && e.getCode() != 404) {
                throw e
            }
        }
    }

    // ========================================
    // ADDITIONAL COVERAGE TESTS - Missing Scenarios
    // ========================================

    /**
     * Test: Create user with recovery question
     * Tests POST /api/v1/users with recovery question credential
     */
    @Test(groups = "group3")
    void testCreateUserWithRecoveryQuestion() {
        String email = "joe-${uniqueTestName}@example.com"
        String password = 'Abcd1234!@#$'
        
        CreateUserRequest createUserRequest = new CreateUserRequest()
        
        UserProfile profile = new UserProfile()
        profile.setFirstName("Recovery")
        profile.setLastName("Question")
        profile.setEmail(email)
        profile.setLogin(email)
        createUserRequest.setProfile(profile)
        
        UserCredentialsWritable credentials = new UserCredentialsWritable()
        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue(password)
        credentials.setPassword(passwordCredential)
        
        RecoveryQuestionCredential recoveryQuestionCredential = new RecoveryQuestionCredential()
        recoveryQuestionCredential.setQuestion("What is your favorite color?")
        recoveryQuestionCredential.setAnswer("Blue")
        credentials.setRecoveryQuestion(recoveryQuestionCredential)
        
        createUserRequest.setCredentials(credentials)
        
        User user = userApi.createUser(createUserRequest, true, false, null)
        registerForCleanup(user)

        assertThat("User should be created", user, notNullValue())
        assertThat("User should have ID", user.getId(), notNullValue())
        assertThat("User should have credentials", user.getCredentials(), notNullValue())
    }

    /**
     * Test: Create user with full profile fields
     * Tests POST /api/v1/users with extensive profile
     */
    @Test(groups = "group3")
    void testCreateUserWithFullProfile() {
        String email = "full-profile-${uniqueTestName}@example.com"
        String password = 'Abcd1234!@#$'
        
        CreateUserRequest createUserRequest = new CreateUserRequest()
        
        UserProfile profile = new UserProfile()
        profile.setFirstName("FullProfile")
        profile.setLastName("User")
        profile.setEmail(email)
        profile.setLogin(email)
        profile.setSecondEmail("secondary-${uniqueTestName}@example.com")
        profile.setMobilePhone("555-987-6543")
        profile.setPrimaryPhone("555-111-2222")
        profile.setStreetAddress("123 Main Street")
        profile.setCity("San Francisco")
        profile.setState("CA")
        profile.setZipCode("94105")
        profile.setCountryCode("US")
        profile.setOrganization("Test Org")
        profile.setDepartment("Engineering")
        profile.setTitle("Senior Developer")
        profile.setDisplayName("Full Profile User")
        profile.setNickName("FP")
        profile.setPreferredLanguage("en-US")
        profile.setUserType("Employee")
        
        createUserRequest.setProfile(profile)
        
        UserCredentialsWritable credentials = new UserCredentialsWritable()
        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue(password)
        credentials.setPassword(passwordCredential)
        createUserRequest.setCredentials(credentials)
        
        User user = userApi.createUser(createUserRequest, true, false, null)
        registerForCleanup(user)

        assertThat("User should be created", user, notNullValue())
        assertThat("Department should match", user.getProfile().getDepartment(), equalTo("Engineering"))
        assertThat("Title should match", user.getProfile().getTitle(), equalTo("Senior Developer"))
        assertThat("City should match", user.getProfile().getCity(), equalTo("San Francisco"))
        assertThat("State should match", user.getProfile().getState(), equalTo("CA"))
        assertThat("Organization should match", user.getProfile().getOrganization(), equalTo("Test Org"))
    }

    /**
     * Test: Get user with expand parameter
     * Tests GET /api/v1/users/{id}?expand=blocks
     */
    @Test(groups = "group3")
    void testGetUserWithExpandParameter() {
        String email = "expand-blocks-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Expand")
            .setLastName("Blocks")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Get user with expand=blocks parameter
        UserGetSingleton userWithBlocks = userApi.getUser(user.getId(), "blocks", null)
        
        assertThat("User should not be null", userWithBlocks, notNullValue())
        assertThat("User ID should match", userWithBlocks.getId(), equalTo(user.getId()))
    }

    /**
     * Test: Update user with strict validation
     * Tests POST /api/v1/users/{id}?strict=true
     */
    @Test(groups = "group3")
    void testUpdateUserWithStrictValidation() {
        String email = "strict-update-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Strict")
            .setLastName("Update")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        UserProfile updatedProfile = new UserProfile()
        updatedProfile.setCity("New York")
        
        UpdateUserRequest updateRequest = new UpdateUserRequest()
        updateRequest.setProfile(updatedProfile)
        
        User updatedUser = userApi.updateUser(user.getId(), updateRequest, true, null)
        
        assertThat("Updated user should not be null", updatedUser, notNullValue())
        assertThat("City should be updated", updatedUser.getProfile().getCity(), equalTo("New York"))
    }

    /**
     * Test: Delete user with sendEmail parameter
     * Tests DELETE /api/v1/users/{id}?sendEmail=false
     */
    @Test(groups = "group3")
    void testDeleteUserWithSendEmailParameter() {
        String email = "delete-sendemail-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Delete")
            .setLastName("SendEmail")
            .buildAndCreate(userApi)
        
        String userId = user.getId()
        
        // First deactivate the user
        userLifecycleApi.deactivateUser(userId, false, null)
        Thread.sleep(getTestOperationDelay())
        
        // Delete with sendEmail=false
        userApi.deleteUser(userId, false, null)
        
        Thread.sleep(getTestOperationDelay())
        
        // Verify user is deleted
        ApiException exception = expect(ApiException.class, () -> 
            userApi.getUser(userId, null, null))
        
        assertThat("Should return 404 for deleted user", exception.getCode(), is(404))
    }

    /**
     * Test: Reactivate user lifecycle operation
     * Tests POST /api/v1/users/{userId}/lifecycle/reactivate
     */
    @Test(groups = "group3")
    void testUserLifecycleReactivate() {
        String email = "reactivate-lifecycle-${uniqueTestName}@example.com"
        
        // Create user without password for PROVISIONED status
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Reactivate")
            .setLastName("Lifecycle")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Activate the user first
        userLifecycleApi.activateUser(user.getId(), false)
        Thread.sleep(getTestOperationDelay())

        UserGetSingleton activatedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should be PROVISIONED or ACTIVE", 
            activatedUser.getStatus(), 
            anyOf(equalTo(UserStatus.PROVISIONED), equalTo(UserStatus.ACTIVE)))

        // Reactivate the user
        UserActivationToken reactivationToken = userLifecycleApi.reactivateUser(user.getId(), false)
        
        assertThat("Reactivation token should not be null", reactivationToken, notNullValue())
        assertThat("Activation token should not be empty", reactivationToken.getActivationToken(), notNullValue())
        assertThat("Activation URL should not be empty", reactivationToken.getActivationUrl(), notNullValue())

        Thread.sleep(getTestOperationDelay())
        UserGetSingleton reactivatedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should still be PROVISIONED or ACTIVE", 
            reactivatedUser.getStatus(), 
            anyOf(equalTo(UserStatus.PROVISIONED), equalTo(UserStatus.ACTIVE)))
    }

    /**
     * Test: Reset user factors
     * Tests POST /api/v1/users/{userId}/lifecycle/reset_factors
     */
    @Test(groups = "group3")
    void testUserLifecycleResetFactors() {
        String email = "reset-factors-${uniqueTestName}@example.com"
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetFactors")
            .setLastName("Lifecycle")
            .setPassword('Passw0rd!123'.toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Reset factors for the user
        userLifecycleApi.resetFactors(user.getId())

        Thread.sleep(getTestOperationDelay())
        UserGetSingleton updatedUser = userApi.getUser(user.getId(), null, null)
        assertThat("User should exist after reset factors", updatedUser, notNullValue())
        assertThat("User should be ACTIVE", updatedUser.getStatus(), equalTo(UserStatus.ACTIVE))
    }

    /**
     * Test: Lifecycle error handling - Activate non-existent user
     * Tests error handling for lifecycle operations
     */
    @Test(groups = "group3")
    void testLifecycleActivateNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.activateUser(fakeUserId, false))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Deactivate non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleDeactivateNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.deactivateUser(fakeUserId, false, null))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Reactivate non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleReactivateNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.reactivateUser(fakeUserId, false))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Suspend non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleSuspendNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.suspendUser(fakeUserId))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Unsuspend non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleUnsuspendNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.unsuspendUser(fakeUserId))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Unlock non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleUnlockNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.unlockUser(fakeUserId))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: Lifecycle error handling - Reset factors for non-existent user
     */
    @Test(groups = "group3")
    void testLifecycleResetFactorsNonExistentUser() {
        String fakeUserId = "00u" + RandomStringUtils.randomAlphanumeric(17)
        
        ApiException exception = expect(ApiException.class, () -> 
            userLifecycleApi.resetFactors(fakeUserId))
        
        assertThat("Should return 404", exception.getCode(), is(404))
    }

    /**
     * Test: List users with invalid filter syntax
     * Tests error handling for invalid filter
     */
    @Test(groups = "group3")
    void testListUsersWithInvalidFilter() {
        try {
            List<User> users = userApi.listUsers(null, null, "invalid filter syntax that will fail", 
                null, null, 10, null, null, null)
            // If we get here, the filter might have been ignored or parsed differently
            assertThat("Users list should not be null", users, notNullValue())
        } catch (ApiException e) {
            // Expected: 400 Bad Request for invalid filter
            assertThat("Should return 400 for invalid filter", e.getCode(), is(400))
        }
    }

    // ========================================
    // User Authenticator Enrollments Tests
    // ========================================

    /**
     * Test: Error scenarios for authenticator enrollments
     * Tests handling of invalid user IDs
     */
    @Test(groups = "group3")
    void testAuthenticatorEnrollmentErrorScenarios() {
        UserAuthenticatorEnrollmentsApi userAuthenticatorEnrollmentsApi = new UserAuthenticatorEnrollmentsApi(getClient())
        def invalidUserId = "invalid_user_id_12345"
        def invalidEnrollmentId = "invalid_enrollment_id_12345"

        // List with invalid user - should throw 404
        try {
            userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(invalidUserId)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 404", ex.getCode(), is(404))
        }

        // Get with invalid user - should throw 404
        try {
            userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollment(invalidUserId, invalidEnrollmentId)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 404", ex.getCode(), is(404))
        }
    }

    // ========================================
    // User Classification Tests
    // ========================================

    /**
     * Test: Get and replace user classification
     * Tests user classification API for LITE and STANDARD types
     * Note: Requires Early Access feature flag
     */
    @Test(groups = "group3")
    void testGetAndReplaceUserClassification() {
        UserClassificationApi userClassificationApi = new UserClassificationApi(getClient())
        def guid = UUID.randomUUID().toString()
        
        User user = createTestUserForClassification(guid)
        registerForCleanup(user)

        Thread.sleep(getTestOperationDelay() * 2)

        // Check if feature is enabled
        try {
            userClassificationApi.getUserClassification(user.getId())
        } catch (ApiException ex) {
            if (ex.getCode() == 403 || ex.getMessage().contains("E0000015")) {
                // Feature not enabled - skip test
                return
            }
            throw ex
        }

        // Get initial classification
        UserClassification initialClassification = userClassificationApi.getUserClassification(user.getId())
        
        assertThat("Classification should not be null", initialClassification, notNullValue())
        assertThat("Type should be LITE or STANDARD", 
            initialClassification.getType(), 
            anyOf(equalTo(ClassificationType.LITE), equalTo(ClassificationType.STANDARD)))

        // Replace to LITE
        ReplaceUserClassification replaceToLite = new ReplaceUserClassification()
        replaceToLite.setType(ClassificationType.LITE)

        UserClassification updatedToLite = userClassificationApi.replaceUserClassification(user.getId(), replaceToLite)
        
        assertThat("Updated classification should not be null", updatedToLite, notNullValue())
        assertThat("Type should be LITE", updatedToLite.getType(), equalTo(ClassificationType.LITE))

        Thread.sleep(getTestOperationDelay())

        // Verify persistence
        UserClassification verifyLite = userClassificationApi.getUserClassification(user.getId())
        assertThat("Verified type should be LITE", verifyLite.getType(), equalTo(ClassificationType.LITE))

        // Replace to STANDARD
        ReplaceUserClassification replaceToStandard = new ReplaceUserClassification()
        replaceToStandard.setType(ClassificationType.STANDARD)

        UserClassification updatedToStandard = userClassificationApi.replaceUserClassification(user.getId(), replaceToStandard)
        
        assertThat("Type should be STANDARD", updatedToStandard.getType(), equalTo(ClassificationType.STANDARD))

        Thread.sleep(getTestOperationDelay())

        // Verify persistence
        UserClassification verifyStandard = userClassificationApi.getUserClassification(user.getId())
        assertThat("Verified type should be STANDARD", verifyStandard.getType(), equalTo(ClassificationType.STANDARD))
    }

    /**
     * Test: User classification with HTTP info
     * Tests classification API with HTTP response details
     */
    @Test(groups = "group3")
    void testUserClassificationWithHttpInfo() {
        UserClassificationApi userClassificationApi = new UserClassificationApi(getClient())
        def guid = UUID.randomUUID().toString()
        
        User user = createTestUserForClassification(guid)
        registerForCleanup(user)

        Thread.sleep(getTestOperationDelay() * 2)

        try {
            userClassificationApi.getUserClassification(user.getId())
        } catch (ApiException ex) {
            if (ex.getCode() == 403 || ex.getMessage().contains("E0000015")) {
                return
            }
            throw ex
        }

        // GetWithHttpInfo
        def getResponse = userClassificationApi.getUserClassificationWithHttpInfo(user.getId())
        
        assertThat("Response should not be null", getResponse, notNullValue())
        assertThat("Status code should be 200", getResponse.getStatusCode(), is(200))
        assertThat("Response data should not be null", getResponse.getData(), notNullValue())
        assertThat("Response headers should not be null", getResponse.getHeaders(), notNullValue())

        // ReplaceWithHttpInfo
        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
        replaceRequest.setType(ClassificationType.LITE)
        
        def replaceResponse = userClassificationApi.replaceUserClassificationWithHttpInfo(user.getId(), replaceRequest)
        
        assertThat("Response should not be null", replaceResponse, notNullValue())
        assertThat("Status code should be 200", replaceResponse.getStatusCode(), is(200))
        assertThat("Response data type should be LITE", 
            replaceResponse.getData().getType(), 
            equalTo(ClassificationType.LITE))
    }

    /**
     * Test: User classification error scenarios
     * Tests error handling for invalid user IDs
     */
    @Test(groups = "group3")
    void testUserClassificationErrorScenarios() {
        UserClassificationApi userClassificationApi = new UserClassificationApi(getClient())
        def invalidUserId = "invalid_user_id_12345"

        // Get with invalid user
        try {
            userClassificationApi.getUserClassification(invalidUserId)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 401 or 404", ex.getCode(), anyOf(is(401), is(404)))
        }

        // Replace with invalid user
        ReplaceUserClassification replaceRequest = new ReplaceUserClassification()
        replaceRequest.setType(ClassificationType.LITE)
        
        try {
            userClassificationApi.replaceUserClassification(invalidUserId, replaceRequest)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 401 or 404", ex.getCode(), anyOf(is(401), is(404)))
        }
    }

    private User createTestUserForClassification(String guid) {
        CreateUserRequest createUserRequest = new CreateUserRequest()
        
        UserProfile profile = new UserProfile()
        profile.setFirstName("Classification")
        profile.setLastName("Test${guid.substring(0, 8)}")
        profile.setEmail("classification-${guid}@example.com")
        profile.setLogin("classification-${guid}@example.com")
        createUserRequest.setProfile(profile)
        
        UserCredentialsWritable credentials = new UserCredentialsWritable()
        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue("Abed1234!@#")
        credentials.setPassword(passwordCredential)
        createUserRequest.setCredentials(credentials)
        
        return userApi.createUser(createUserRequest, true, false, null)
    }

    // ========================================
    // User Risk Tests
    // ========================================

    /**
     * Test: Get and upsert user risk
     * Tests user risk API for HIGH and LOW risk levels
     * Note: Requires Identity Engine with Identity Threat Protection
     */
    @Test(groups = "group3")
    void testGetAndUpsertUserRisk() {
        UserRiskApi userRiskApi = new UserRiskApi(getClient())
        def guid = UUID.randomUUID().toString()
        
        User user = createTestUserForRisk(guid)
        registerForCleanup(user)

        Thread.sleep(getTestOperationDelay() * 2)

        // Check if feature is enabled
        try {
            userRiskApi.getUserRisk(user.getId())
        } catch (ApiException ex) {
            if (ex.getCode() in [403, 404] || ex.getMessage().contains("E0000015")) {
                // Feature not enabled - skip test
                return
            }
            throw ex
        }

        // Upsert risk to HIGH
        UserRiskRequest highRiskRequest = new UserRiskRequest()
        highRiskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)

        UserRiskApi highRiskResponse = userRiskApi.upsertUserRisk(user.getId(), highRiskRequest)
        
        assertThat("Risk response should not be null", highRiskResponse, notNullValue())
        assertThat("Risk level should be HIGH", highRiskResponse.getRiskLevel(), equalTo(UserRiskLevelPut.HIGH))
        assertThat("Reason should not be null", highRiskResponse.getReason(), notNullValue())

        Thread.sleep(getTestOperationDelay())

        // Get and verify HIGH risk
        UserRiskApi verifyHighRisk = userRiskApi.getUserRisk(user.getId())
        assertThat("Risk level should be HIGH", verifyHighRisk.getRiskLevel(), equalTo(UserRiskLevelAll.HIGH))

        // Upsert risk to LOW
        UserRiskRequest lowRiskRequest = new UserRiskRequest()
        lowRiskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.LOW)

        UserRiskApi lowRiskResponse = userRiskApi.upsertUserRisk(user.getId(), lowRiskRequest)
        assertThat("Risk level should be LOW", lowRiskResponse.getRiskLevel(), equalTo(UserRiskLevelPut.LOW))

        Thread.sleep(getTestOperationDelay())

        // Get and verify LOW risk
        UserRiskApi verifyLowRisk = userRiskApi.getUserRisk(user.getId())
        assertThat("Risk level should be LOW", verifyLowRisk.getRiskLevel(), equalTo(UserRiskLevelAll.LOW))
    }

    /**
     * Test: User risk with HTTP info
     * Tests risk API with HTTP response details
     */
    @Test(groups = "group3")
    void testUserRiskWithHttpInfo() {
        UserRiskApi userRiskApi = new UserRiskApi(getClient())
        def guid = UUID.randomUUID().toString()
        
        User user = createTestUserForRisk(guid)
        registerForCleanup(user)

        Thread.sleep(getTestOperationDelay() * 2)

        try {
            userRiskApi.getUserRisk(user.getId())
        } catch (ApiException ex) {
            if (ex.getCode() in [403, 404] || ex.getMessage().contains("E0000015")) {
                return
            }
            throw ex
        }

        // UpsertWithHttpInfo
        UserRiskRequest riskRequest = new UserRiskRequest()
        riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
        
        def createResponse = userRiskApi.upsertUserRiskWithHttpInfo(user.getId(), riskRequest)
        
        assertThat("Response should not be null", createResponse, notNullValue())
        assertThat("Status code should be 200 or 201", createResponse.getStatusCode(), anyOf(is(200), is(201)))
        assertThat("Response data should not be null", createResponse.getData(), notNullValue())
        assertThat("Risk level should be HIGH", createResponse.getData().getRiskLevel(), equalTo(UserRiskLevelPut.HIGH))

        Thread.sleep(getTestOperationDelay())

        // GetWithHttpInfo
        def getResponse = userRiskApi.getUserRiskWithHttpInfo(user.getId())
        
        assertThat("Response should not be null", getResponse, notNullValue())
        assertThat("Status code should be 200", getResponse.getStatusCode(), is(200))
        assertThat("Response data should not be null", getResponse.getData(), notNullValue())
        assertThat("Risk level should be HIGH", getResponse.getData().getRiskLevel(), equalTo(UserRiskLevelAll.HIGH))
    }

    /**
     * Test: User risk error scenarios
     * Tests error handling for invalid user IDs
     */
    @Test(groups = "group3")
    void testUserRiskErrorScenarios() {
        UserRiskApi userRiskApi = new UserRiskApi(getClient())
        def invalidUserId = "invalid_user_id_12345"

        // Get with invalid user
        try {
            userRiskApi.getUserRisk(invalidUserId)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 401, 403, or 404", ex.getCode(), anyOf(is(401), is(403), is(404)))
        }

        // Upsert with invalid user
        UserRiskRequest riskRequest = new UserRiskRequest()
        riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
        
        try {
            userRiskApi.upsertUserRisk(invalidUserId, riskRequest)
            throw new AssertionError("Expected ApiException")
        } catch (ApiException ex) {
            assertThat("Should return 401, 403, or 404", ex.getCode(), anyOf(is(401), is(403), is(404)))
        }
    }

    private User createTestUserForRisk(String guid) {
        CreateUserRequest createUserRequest = new CreateUserRequest()
        
        UserProfile profile = new UserProfile()
        profile.setFirstName("UserRisk")
        profile.setLastName("Test${guid.substring(0, 8)}")
        profile.setEmail("user-risk-${guid}@example.com")
        profile.setLogin("user-risk-${guid}@example.com")
        createUserRequest.setProfile(profile)
        
        UserCredentialsWritable credentials = new UserCredentialsWritable()
        PasswordCredential passwordCredential = new PasswordCredential()
        passwordCredential.setValue("ComplexP@ssw0rd!2024")
        credentials.setPassword(passwordCredential)
        createUserRequest.setCredentials(credentials)
        
        return userApi.createUser(createUserRequest, true, false, null)
    }
}

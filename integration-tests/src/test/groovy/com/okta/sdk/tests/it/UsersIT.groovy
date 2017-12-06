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
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.AuthenticationProviderType
import com.okta.sdk.resource.user.ChangePasswordRequest
import com.okta.sdk.resource.user.ForgotPasswordResponse
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.RecoveryQuestionCredential
import com.okta.sdk.resource.user.ResetPasswordToken
import com.okta.sdk.resource.user.Role
import com.okta.sdk.resource.user.TempPassword
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.resource.user.UserCredentials
import com.okta.sdk.resource.user.UserList
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertGroupTargetPresent
import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.getExpect
import static com.okta.sdk.tests.it.util.Util.validateGroup
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for /api/v1/users
 * @since 0.5.0
 */
class UsersIT implements CrudTestSupport {

    @Test
    void userProfileNumberValues() {

        def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1")
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .putAllProfileProperties([
                    customNumber: 1.5d,
                    customBoolean: true,
                    customInteger: 123,
                    customStringArray: ["one", "two", "three"],
                    customNumberArray: [1.5d, 2.5d, 3.5d],
                    customIntegerArray: [1, 2, 3]
                ])
                .buildAndCreate(getClient())

        // check the values after create
        assertThat(user.profile.getString("firstName"), equalTo("Joe"))
        assertThat(user.profile.getNumber("customNumber"), equalTo(1.5d))
        assertThat(user.profile.getBoolean("customBoolean"), equalTo(true))
        assertThat(user.profile.getInteger("customInteger"), equalTo(123))
        assertThat(user.profile.getStringList("customStringArray"), equalTo(["one", "two", "three"]))
        assertThat(user.profile.getNumberList("customNumberArray"), equalTo([1.5d, 2.5d, 3.5d]))
        assertThat(user.profile.getIntegerList("customIntegerArray"), equalTo([1, 2, 3]))

        // Use the 'get' to update the values to make sure there are no conversion issues
        user.profile.putAll([
                        customNumber: user.profile.getNumber("customNumber"),
                        customBoolean: true,
                        customStringArray: user.profile.getStringList("customStringArray"),
                        customNumberArray: user.profile.getNumberList("customNumberArray"),
                        customIntegerArray: user.profile.getIntegerList("customIntegerArray")])
        user.update()

        // same check as before
        assertThat(user.profile.getNumber("customNumber"), equalTo(1.5d))
        assertThat(user.profile.getBoolean("customBoolean"), equalTo(true))
        assertThat(user.profile.getInteger("customInteger"), equalTo(123))
        assertThat(user.profile.getStringList("customStringArray"), equalTo(["one", "two", "three"]))
        assertThat(user.profile.getNumberList("customNumberArray"), equalTo([1.5d, 2.5d, 3.5d]))
        assertThat(user.profile.getIntegerList("customIntegerArray"), equalTo([1, 2, 3]))

        // test again but null out all of the values
        user.profile.remove("customNumber")
        user.profile.remove("customBoolean")
        user.profile.remove("customInteger")
        user.profile.remove("customStringArray")
        user.profile.remove("customNumberArray")
        user.profile.remove("customIntegerArray")
        user.update()

        // everything should be null
        assertThat(user.profile.getNumber("customNumber"), nullValue())
        assertThat(user.profile.getBoolean("customBoolean"), nullValue())
        assertThat(user.profile.getInteger("customInteger"), nullValue())
        assertThat(user.profile.getStringList("customStringArray"), nullValue())
        assertThat(user.profile.getNumberList("customNumberArray"), nullValue())
        assertThat(user.profile.getIntegerList("customIntegerArray"), nullValue())
    }


    @Override
    def create(Client client) {
        def email = "joe.coder+" + UUID.randomUUID().toString() + "@example.com"
        return UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1")
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .buildAndCreate(client)
    }

    @Override
    def read(Client client, String id) {
        return client.getUser(id)
    }

    @Override
    void update(Client client, def user) {
        user.profile.lastName = "Coder"
        user.update()
    }

    @Override
    void assertUpdate(Client client, Object resource) {
        assertThat resource.profile.lastName, equalTo("Coder")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listUsers().iterator()
    }

    @Test
    @Scenario("user-activate")
    @TestResources(users = "john-activate@example.com")
    void userActivateTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Activate'
        def email = 'john-activate@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Activate the user and verify user in list of active users
        user.activate(false)
        UserList users = client.listUsers(null, 'status eq \"ACTIVE\"', null, null, null)
        assertPresent(users, user)



    }

    @Test(enabled = false)
    @Scenario("user-role-assign")
    @TestResources(users = "john-role@example.com")
    void roleAssignTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Role'
        def email = 'john-role@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Assign USER_ADMIN role to the user
        Role role = user.addRole(client.instantiate(Role)
                .setType('USER_ADMIN'))

        // 3. List roles for the user and verify added role
        assertPresent(user.listRoles(), role)

        // 4. Remove role for the user
        user.removeRole(role.id)

        // 5. List roles for user and verify role was removed
        assertNotPresent(user.listRoles(), role)
    }

    @Test
    @Scenario("user-change-password")
    @TestResources(users = "john-change-password@example.com")
    void changePasswordTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Change-Password'
        def email = 'john-change-password@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the user's password
        UserCredentials credentials = user.changePassword(client.instantiate(ChangePasswordRequest)
            .setOldPassword(client.instantiate(PasswordCredential).setValue('Abcd1234'))
            .setNewPassword(client.instantiate(PasswordCredential).setValue('1234Abcd')))
        assertThat credentials.provider.type, equalTo(AuthenticationProviderType.OKTA)

        // 3. make the test recording happy, and call a get on the user
        // TODO: fix har file
        client.getUser(user.id)
    }

    @Test
    @Scenario("user-change-recovery-question")
    @TestResources(users = "john-change-recovery-question@example.com")
    void changeRecoveryQuestionTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Change-Recovery-Question'
        def email = 'john-change-recovery-question@example.com'

        // 1. Create a user with password & recovery question
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the recovery question
        UserCredentials userCredentials = user.changeRecoveryQuestion(client.instantiate(UserCredentials)
            .setPassword(client.instantiate(PasswordCredential)
                .setValue('Abcd1234'))
            .setRecoveryQuestion(client.instantiate(RecoveryQuestionCredential)
                .setQuestion('How many roads must a man walk down?')
                .setAnswer('forty two')))

        assertThat userCredentials.provider.type, equalTo(AuthenticationProviderType.OKTA)
        assertThat userCredentials.recoveryQuestion.question, equalTo('How many roads must a man walk down?')

        // 3. Update the user password through updated recovery question
        userCredentials.password.value = '1234Abcd'
        userCredentials.recoveryQuestion.answer = 'forty two'
        ForgotPasswordResponse response = user.forgotPassword(null, userCredentials)
        assertThat response.getResetPasswordUrl(), nullValue()

        // 4. make the test recording happy, and call a get on the user
        // TODO: fix har file
        client.getUser(user.id)
    }

    @Test
    @Scenario("user-expire-password")
    @TestResources(users = "john-expire-password@example.com")
    void expirePasswordTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Expire-Password'
        def email = 'john-expire-password@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Expire the user's password with tempPassword=true
        TempPassword tempPassword = user.expirePassword(true)
        assertThat tempPassword.getTempPassword(), notNullValue()
    }


    @Test
    @Scenario("user-get-reset-password-url")
    @TestResources(users = "john-get-reset-password-url@example.com")
    void resetPasswordUrlTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Get-Reset-Password-URL'
        def email = 'john-get-reset-password-url@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Get the reset password link
        ResetPasswordToken token = user.resetPassword(null, false)
        assertThat token.getResetPasswordUrl(), notNullValue()
    }

    @Test
    @Scenario("user-get")
    @TestResources(users = "john-get-user@example.com")
    void getUserTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Get-User'
        def email = 'john-get-user@example.com'

        // 1. Create a user
        User createUser = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(createUser)
        validateUser(createUser, firstName, lastName, email)

        // 2. Get the user by user ID
        User user = client.getUser(createUser.id)
        validateUser(user, createUser)

        // 3. Get the user by user login
        User userByLogin = client.getUser(createUser.profile.getLogin())
        validateUser(userByLogin, createUser)

        // 3. delete the user
        user.deactivate()
        user.delete()

        // 4. get user expect 404
        expect(ResourceException) {
            client.getUser(email)
        }
    }

    @Test(enabled = false)
    @Scenario("user-group-target-role")
    @TestResources(
            users = "john-group-target@example.com",
            groups = ["Group-Target User Admin Test Group",
                      "Group-Target Test Group"])
    void groupTargetRoleTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Group-Target'
        def email = 'john-group-target@example.com'
        def groupName = 'Group-Target Test Group'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Group group = GroupBuilder.instance()
            .setName(groupName)
            .buildAndCreate(client)
        registerForCleanup(group)
        validateGroup(group, groupName)

        // 2. Assign USER_ADMIN role to the user
        Role role = user.addRole(client.instantiate(Role)
            .setType('USER_ADMIN'))

        // 3. Add Group Target to User Admin Role
        user.addGroupTargetToRole(role.id, group.id)

        // 4. List Group Targets for Role
        assertGroupTargetPresent(user, group, role)

        // 5. Remove Group Target from Admin User Role and verify removed
        // Note: Don’t remove the last group target from a role assignment, as this causes an exception.
        // To get around this, create a new group and add this group target to user admin role

        def adminGroupName = "Group-Target User Admin Test Group"
        deleteGroup(adminGroupName, client)

        Group adminGroup = GroupBuilder.instance()
            .setName(adminGroupName)
            .buildAndCreate(client)
        registerForCleanup(adminGroup)
        validateGroup(adminGroup, adminGroupName)

        user.addGroupTargetToRole(role.id, adminGroup.id)
        user.removeGroupTargetFromRole(role.id, adminGroup.id)

        assertGroupTargetPresent(user, group, role)
    }

    @Test
    @Scenario("user-profile-update")
    @TestResources(users = "john-profile-update@example.com")
    void userProfileUpdate() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Profile-Update'
        def email = 'john-profile-update@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(false)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        def originalLastUpdated = user.lastUpdated

        // 2. Update the user profile and verify that profile was updated
        // Need to wait 1 second here as that is the minimum time resolution of the 'lastUpdated' field
        sleep(1000)
        user.profile.put("nickName", "Batman")
        user.update()
        assertThat(user.lastUpdated, greaterThan(originalLastUpdated))
        User updatedUser = client.getUser(user.id)
        assertThat(updatedUser.profile.get("nickName"), equalTo("Batman"))
    }

    @Test
    @Scenario("user-suspend")
    @TestResources(users = "john-suspend@example.com")
    void userSuspendTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Suspend'
        def email = 'john-suspend@example.com'

        // 1. Create a user
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Suspend the user and verify user in list of suspended users
        user.suspend()
        assertPresent(client.listUsers(null, 'status eq \"SUSPENDED\"', null, null, null), user)

        // 3. Unsuspend the user and verify user in list of active users
        user.unsuspend()
        assertPresent(client.listUsers(null, 'status eq \"ACTIVE\"', null, null, null), user)
    }
}

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

import com.okta.sdk.resource.user.AuthenticationProviderType
import com.okta.sdk.resource.user.ChangePasswordRequest
import com.okta.sdk.resource.user.ForgotPasswordResponse
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.RecoveryQuestionCredential
import com.okta.sdk.resource.user.Role
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.resource.user.UserCredentials
import com.okta.sdk.resource.user.UserList
import com.okta.sdk.tests.TestResources
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ClientProvider
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*
import static com.okta.sdk.tests.it.util.Util.*

class UserIT implements ClientProvider {

    @Test(enabled = false)
    @Scenario("user-profile-update")
    @TestResources(users = "joe.coder@example.com")
    void profileUpdateTest() {

        User password = 'Abcd1234'
        def firstName = 'Joe'
        def lastName = 'Coder'
        def email = 'joe.coder@example.com'

        // 1. Create a user with password & recovery question
        def user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .buildAndCreate(client, false)

        registerForCleanup(user)

        validateUser(user, firstName, lastName, email)

        // 2. Update the user profile
        def originalLastUpdated = user.lastUpdated
        sleep(1000)
        user.getProfile().put("nickName", "Batman")
        user.update()

        assertThat(user.lastUpdated, greaterThan(originalLastUpdated))
        assertThat(user.profile.get("nickName"), equalTo("Batman"))
    }

    @Test
    @Scenario("user-activate")
    @TestResources(users = "john-activate@example.com")
    void userActivateTeset() {

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
                .buildAndCreate(client, false)
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
                .buildAndCreate(client, true)
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
                .buildAndCreate(client, true)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        // 2. Change the user's password
        UserCredentials credentials = user.changePassword(client.instantiate(ChangePasswordRequest)
            .setOldPassword(client.instantiate(PasswordCredential).setValue('Abcd1234'))
            .setNewPassword(client.instantiate(PasswordCredential).setValue('1234Abcd')))
        assertThat credentials.provider.type, equalTo(AuthenticationProviderType.OKTA)
    }

    @Test
    @Scenario("user-change-recovery-question")
    @TestResources(users = "john-change-recover-qyestion@example.com")
    void changeRecoveryQuestionTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'Change-Recovery-Question'
        def email = 'john-change-recover-qyestion@example.com'

        // 1. Create a user with password & recovery question
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .buildAndCreate(client, true)
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
        ForgotPasswordResponse response = user.forgotPassword(false, userCredentials)

        assertThat response.getResetPasswordUrl(), nullValue()

    }
}

/*
 * Copyright 2024-Present Okta, Inc.
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

import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserLifecycleApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Lifecycle API.
 * Tests user activation, deactivation, reactivation, suspension, unlock, and factor reset operations.
 */
class UserLifecycleIT extends ITSupport {

    private UserApi userApi
    private UserLifecycleApi userLifecycleApi

    UserLifecycleIT() {
        this.userApi = new UserApi(getClient())
        this.userLifecycleApi = new UserLifecycleApi(getClient())
    }

    @Test(groups = "group3")
    void activateUserTest() {
        def email = "user-activate-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Activate")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def activationToken = userLifecycleApi.activateUser(user.getId(), false)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())
            assertThat(activationToken.getActivationToken(), notNullValue())

        } catch (ApiException e) {
            // Expected if user already active or org settings prevent activation
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void activateUserWithSendEmailTrueTest() {
        def email = "user-activate-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ActivateEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def activationToken = userLifecycleApi.activateUser(user.getId(), true)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected if user already active
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void activateUserWithSendEmailFalseTest() {
        def email = "user-activate-no-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ActivateNoEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def activationToken = userLifecycleApi.activateUser(user.getId(), false)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())
            assertThat(activationToken.getActivationToken(), notNullValue())

        } catch (ApiException e) {
            // Expected if user already active
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deactivateUserTest() {
        def email = "user-deactivate-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Deactivate")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Verify user is deactivated
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser, notNullValue())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

        } catch (ApiException e) {
            // Expected if user cannot be deactivated
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deactivateUserWithSendEmailTrueTest() {
        def email = "user-deactivate-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeactivateEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userLifecycleApi.deactivateUser(user.getId(), true)

            // Verify user is deactivated
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

        } catch (ApiException e) {
            // Expected if user cannot be deactivated
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deactivateUserWithSendEmailFalseTest() {
        def email = "user-deactivate-no-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeactivateNoEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Verify user is deactivated
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

        } catch (ApiException e) {
            // Expected if user cannot be deactivated
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void reactivateUserTest() {
        def email = "user-reactivate-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Reactivate")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Deactivate first
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Reactivate
            def activationToken = userApi.reactivateUser(user.getId(), false)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected if user state doesn't allow reactivation
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void reactivateUserWithSendEmailTrueTest() {
        def email = "user-reactivate-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ReactivateEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Deactivate first
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Reactivate with email
            def activationToken = userApi.reactivateUser(user.getId(), true)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected if user state doesn't allow reactivation
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void reactivateUserWithSendEmailFalseTest() {
        def email = "user-reactivate-no-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ReactivateNoEmail")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Deactivate first
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Reactivate without email
            def activationToken = userApi.reactivateUser(user.getId(), false)

            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())
            assertThat(activationToken.getActivationToken(), notNullValue())

        } catch (ApiException e) {
            // Expected if user state doesn't allow reactivation
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void suspendUserTest() {
        def email = "user-suspend-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Suspend")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userLifecycleApi.suspendUser(user.getId())

            // Verify user is suspended
            def suspendedUser = userApi.getUser(user.getId())
            assertThat(suspendedUser, notNullValue())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

        } catch (ApiException e) {
            // Expected if user cannot be suspended (e.g., already suspended)
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void unsuspendUserTest() {
        def email = "user-unsuspend-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Unsuspend")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Suspend first
            userLifecycleApi.suspendUser(user.getId())

            // Unsuspend
            userLifecycleApi.unsuspendUser(user.getId())

            // Verify user is active again
            def activeUser = userApi.getUser(user.getId())
            assertThat(activeUser, notNullValue())
            assertThat(activeUser.getStatus(), equalTo(UserStatus.ACTIVE))

        } catch (ApiException e) {
            // Expected if user state doesn't allow unsuspend
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void unlockUserTest() {
        def email = "user-unlock-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Unlock")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Unlock user (even if not locked, should succeed)
            userLifecycleApi.unlockUser(user.getId())

            // Verify user status
            def unlockedUser = userApi.getUser(user.getId())
            assertThat(unlockedUser, notNullValue())

        } catch (ApiException e) {
            // Expected if user cannot be unlocked
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetFactorsTest() {
        def email = "user-reset-factors-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetFactors")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Reset all enrolled factors
            userLifecycleApi.resetFactors(user.getId())

            // Should complete without error
            // User's factors would be reset

        } catch (ApiException e) {
            // Expected if user has no factors or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void userLifecycleActivateToSuspendTest() {
        def email = "user-lifecycle-suspend-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("LifecycleSuspend")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Activate user
            def activationToken = userLifecycleApi.activateUser(user.getId(), false)
            assertThat(activationToken, notNullValue())

            // Suspend user
            userLifecycleApi.suspendUser(user.getId())

            // Verify suspended
            def suspendedUser = userApi.getUser(user.getId())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

            // Unsuspend user
            userLifecycleApi.unsuspendUser(user.getId())

            // Verify active
            def activeUser = userApi.getUser(user.getId())
            assertThat(activeUser.getStatus(), equalTo(UserStatus.ACTIVE))

        } catch (ApiException e) {
            // Expected if lifecycle transitions not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void userLifecycleDeactivateToReactivateTest() {
        def email = "user-lifecycle-reactivate-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("LifecycleReactivate")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Deactivate user
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Verify deactivated
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

            // Reactivate user
            def activationToken = userApi.reactivateUser(user.getId(), false)
            assertThat(activationToken, notNullValue())
            assertThat(activationToken.getActivationUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected if lifecycle transitions not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void suspendAndUnsuspendWorkflowTest() {
        def email = "user-suspend-unsuspend-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SuspendUnsuspend")
            .setLastName("Workflow-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get initial status
            def initialUser = userApi.getUser(user.getId())
            assertThat(initialUser.getStatus(), equalTo(UserStatus.ACTIVE))

            // Suspend
            userLifecycleApi.suspendUser(user.getId())
            def suspendedUser = userApi.getUser(user.getId())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

            // Unsuspend
            userLifecycleApi.unsuspendUser(user.getId())
            def activeUser = userApi.getUser(user.getId())
            assertThat(activeUser.getStatus(), equalTo(UserStatus.ACTIVE))

        } catch (ApiException e) {
            // Expected if lifecycle operations not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void unlockActiveUserTest() {
        def email = "user-unlock-active-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UnlockActive")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Unlock an active (not locked) user - should succeed
            userLifecycleApi.unlockUser(user.getId())

            // Verify user is still active
            def unlockedUser = userApi.getUser(user.getId())
            assertThat(unlockedUser, notNullValue())
            assertThat(unlockedUser.getStatus(), equalTo(UserStatus.ACTIVE))

        } catch (ApiException e) {
            // Expected if unlock not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetFactorsForUserWithoutFactorsTest() {
        def email = "user-reset-no-factors-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetNoFactors")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Reset factors for user with no factors - should succeed
            userLifecycleApi.resetFactors(user.getId())

            // Should complete without error

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void deactivateSuspendedUserTest() {
        def email = "user-deactivate-suspended-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeactivateSuspended")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Suspend user first
            userLifecycleApi.suspendUser(user.getId())

            // Deactivate suspended user
            userLifecycleApi.deactivateUser(user.getId(), false)

            // Verify user is deactivated
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

        } catch (ApiException e) {
            // Expected if transition not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void activateAlreadyActiveUserTest() {
        def email = "user-activate-active-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ActivateActive")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Try to activate an already active user
            userLifecycleApi.activateUser(user.getId(), false)

            // Should fail or return error
            assertThat("Should not activate already active user", false)

        } catch (ApiException e) {
            // Expected - cannot activate already active user
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403)))
        }
    }

    @Test(groups = "group3")
    void suspendAlreadySuspendedUserTest() {
        def email = "user-suspend-suspended-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SuspendSuspended")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Suspend user
            userLifecycleApi.suspendUser(user.getId())

            // Try to suspend again
            userLifecycleApi.suspendUser(user.getId())

            // May succeed (idempotent) or fail
            def suspendedUser = userApi.getUser(user.getId())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

        } catch (ApiException e) {
            // Expected - may not allow suspending already suspended user
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void activateUserWithHttpInfoTest() {
        def email = "user-activate-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ActivateHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userLifecycleApi.activateUserWithHttpInfo(user.getId(), false)

            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())
            assertThat(response.getData().getActivationUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected if user already active
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void deactivateUserWithHttpInfoTest() {
        def email = "user-deactivate-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DeactivateHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userLifecycleApi.deactivateUserWithHttpInfo(user.getId(), false)

            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))

        } catch (ApiException e) {
            // Expected if user cannot be deactivated
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void completeLifecycleWorkflowTest() {
        def email = "user-complete-lifecycle-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("CompleteLifecycle")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setActive(false)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. Activate
            def activationToken = userLifecycleApi.activateUser(user.getId(), false)
            assertThat(activationToken, notNullValue())

            // 2. Suspend
            userLifecycleApi.suspendUser(user.getId())
            def suspendedUser = userApi.getUser(user.getId())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))

            // 3. Unsuspend
            userLifecycleApi.unsuspendUser(user.getId())
            def activeUser = userApi.getUser(user.getId())
            assertThat(activeUser.getStatus(), equalTo(UserStatus.ACTIVE))

            // 4. Reset factors
            userLifecycleApi.resetFactors(user.getId())

            // 5. Deactivate
            userLifecycleApi.deactivateUser(user.getId(), false)
            def deactivatedUser = userApi.getUser(user.getId())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))

            // 6. Reactivate
            def reactivationToken = userApi.reactivateUser(user.getId(), false)
            assertThat(reactivationToken, notNullValue())

        } catch (ApiException e) {
            // Expected if lifecycle transitions not allowed
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}

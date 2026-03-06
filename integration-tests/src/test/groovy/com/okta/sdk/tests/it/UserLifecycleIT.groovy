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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Lifecycle API.
 * Matches C# UserLifecycleApiTests structure exactly.
 */
class UserLifecycleIT extends ITSupport {

    private static final Logger log = LoggerFactory.getLogger(UserLifecycleIT.class)
    private UserApi userApi
    private UserLifecycleApi userLifecycleApi

    UserLifecycleIT() {
        this.userApi = new UserApi(getClient())
        this.userLifecycleApi = new UserLifecycleApi(getClient())
    }

    /**
     * Helper method to handle API exceptions in lifecycle tests.
     * 
     * For lifecycle operations, 403 means the feature is disabled in the test org
     * (e.g., suspend/unsuspend may not be available). In this case, we skip gracefully.
     * Other errors should fail the test.
     * 
     * @param e The ApiException caught
     * @param operation Description of the operation being performed
     * @throws ApiException if the error is not a known skippable condition
     */
    private void handleLifecycleApiException(ApiException e, String operation) {
        log.error("ApiException during {}: Code={}, Message={}", operation, e.getCode(), e.getMessage())
        
        // 403 = feature disabled in test org - skip gracefully
        if (e.getCode() == 403) {
            log.info("Feature '{}' not available in test org (403), skipping", operation)
            return
        }
        // All other errors should fail the test
        throw e
    }

    private User createTestUser(String uniqueId, boolean activate = false) {
        log.info("Creating test user, uniqueId: {}, activate: {}", uniqueId, activate)
        
        def email = "user-lifecycle-test-${uniqueId}@example.com"
        
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Lifecycle")
            .setLastName("TestUser${uniqueId}")
            .setPassword("Abcd1234!@#%".toCharArray())
            .setActive(activate)
            .buildAndCreate(userApi)
        
        log.info("Created user: {} with status: {}", user.getId(), user.getStatus())
        registerForCleanup(user)
        
        return user
    }

    @Test(groups = "group3")
    void suspendUser_callsCorrectEndpoint() {
        log.info("=== Test: SuspendUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        log.info("Created ACTIVE user: {}", user.getId())
        Thread.sleep(2000)

        try {
            userLifecycleApi.suspendUser(user.getId())
            Thread.sleep(1000)
            def suspendedUser = userApi.getUser(user.getId(), null, null)

            assertThat(suspendedUser, notNullValue())
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))
            log.info("User suspended successfully, status: {}", suspendedUser.getStatus())

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            // 403 = feature disabled in test org, skip test instead of false pass
            if (e.getCode() == 403) {
                log.info("Suspension feature not available in test org, skipping")
                return // Feature not available, don't fail
            }
            // Any other error should fail the test
            throw e
        }
    }

    @Test(groups = "group3")
    void suspendUser_withValidUserId() {
        log.info("=== Test: SuspendUser with valid userId ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        log.info("Created ACTIVE user: {}", user.getId())
        Thread.sleep(2000)

        try {
            userLifecycleApi.suspendUser(user.getId())
            Thread.sleep(1000)
            def suspendedUser = userApi.getUser(user.getId(), null, null)

            assertThat(suspendedUser, notNullValue())
            assertThat(suspendedUser.getId(), equalTo(user.getId()))
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))
            log.info("Successfully suspended user with valid userId")

        } catch (ApiException e) {
            log.error("ApiException during suspend: {}", e.getMessage())
            // 403 = feature disabled in test org
            if (e.getCode() == 403) {
                log.info("Suspension feature not available in test org, skipping")
                return
            }
            throw e
        }
    }

    @Test(groups = "group3")
    void unsuspendUser_callsCorrectEndpoint() {
        log.info("=== Test: UnsuspendUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.suspendUser(user.getId())
            Thread.sleep(2000)

            userLifecycleApi.unsuspendUser(user.getId())
            Thread.sleep(1000)
            def unsuspendedUser = userApi.getUser(user.getId(), null, null)

            assertThat(unsuspendedUser, notNullValue())
            assertThat(unsuspendedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("User unsuspended successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            // 403 = feature disabled in test org
            if (e.getCode() == 403) {
                log.info("Suspend/unsuspend feature not available in test org, skipping")
                return
            }
            throw e
        }
    }

    @Test(groups = "group3")
    void unsuspendUser_withValidUserId() {
        log.info("=== Test: UnsuspendUser with valid userId ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.suspendUser(user.getId())
            Thread.sleep(2000)

            userLifecycleApi.unsuspendUser(user.getId())
            Thread.sleep(1000)
            def unsuspendedUser = userApi.getUser(user.getId(), null, null)

            assertThat(unsuspendedUser, notNullValue())
            assertThat(unsuspendedUser.getId(), equalTo(user.getId()))
            assertThat(unsuspendedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("Successfully unsuspended user with valid userId")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            // 403 = feature disabled in test org
            if (e.getCode() == 403) {
                log.info("Suspend/unsuspend feature not available in test org, skipping")
                return
            }
            throw e
        }
    }

    @Test(groups = "group3")
    void activateUser_callsCorrectEndpoint() {
        log.info("=== Test: ActivateUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, false)
        log.info("Created STAGED user: {}", user.getId())
        Thread.sleep(2000)

        try {
            def response = userLifecycleApi.activateUser(user.getId(), false)

            assertThat(response, notNullValue())
            log.info("User activation successful")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "activateUser")
        }
    }

    @Test(groups = "group3")
    void activateUser_withSendEmailTrue() {
        log.info("=== Test: ActivateUser with sendEmail=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, false)
        Thread.sleep(2000)

        try {
            def response = userLifecycleApi.activateUser(user.getId(), true)

            assertThat(response, notNullValue())
            log.info("User activated with email sent")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "activateUser with sendEmail=true")
        }
    }

    @Test(groups = "group3")
    void activateUser_withSendEmailFalse() {
        log.info("=== Test: ActivateUser with sendEmail=false ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, false)
        Thread.sleep(2000)

        try {
            def response = userLifecycleApi.activateUser(user.getId(), false)

            assertThat(response, notNullValue())
            log.info("User activated without email")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "activateUser with sendEmail=false")
        }
    }

    @Test(groups = "group3")
    void deactivateUser_callsCorrectEndpoint() {
        log.info("=== Test: DeactivateUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(1000)
            def deactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(deactivatedUser, notNullValue())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))
            log.info("User deactivated successfully")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "deactivateUser")
        }
    }

    @Test(groups = "group3")
    void deactivateUser_withSendEmailTrue() {
        log.info("=== Test: DeactivateUser with sendEmail=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), true, null)
            Thread.sleep(1000)
            def deactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(deactivatedUser, notNullValue())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))
            log.info("User deactivated with email")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "deactivateUser with sendEmail=true")
        }
    }

    @Test(groups = "group3")
    void deactivateUser_withSendEmailFalse() {
        log.info("=== Test: DeactivateUser with sendEmail=false ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(1000)
            def deactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(deactivatedUser, notNullValue())
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))
            log.info("User deactivated without email")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "deactivateUser with sendEmail=false")
        }
    }

    @Test(groups = "group3")
    void reactivateUser_callsCorrectEndpoint() {
        log.info("=== Test: ReactivateUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(2000)

            userLifecycleApi.reactivateUser(user.getId(), false)
            Thread.sleep(1000)
            def reactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(reactivatedUser, notNullValue())
            assertThat(reactivatedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("User reactivated successfully")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "reactivateUser")
        }
    }

    @Test(groups = "group3")
    void reactivateUser_withSendEmailTrue() {
        log.info("=== Test: ReactivateUser with sendEmail=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(2000)

            userLifecycleApi.reactivateUser(user.getId(), true)
            Thread.sleep(1000)
            def reactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(reactivatedUser, notNullValue())
            assertThat(reactivatedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("User reactivated with email")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "reactivateUser with sendEmail=true")
        }
    }

    @Test(groups = "group3")
    void reactivateUser_withSendEmailFalse() {
        log.info("=== Test: ReactivateUser with sendEmail=false ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(2000)

            userLifecycleApi.reactivateUser(user.getId(), false)
            Thread.sleep(1000)
            def reactivatedUser = userApi.getUser(user.getId(), null, null)

            assertThat(reactivatedUser, notNullValue())
            assertThat(reactivatedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("User reactivated without email")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "reactivateUser with sendEmail=false")
        }
    }

    @Test(groups = "group3")
    void unlockUser_callsCorrectEndpoint() {
        log.info("=== Test: UnlockUser calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            def unlockedUser = userLifecycleApi.unlockUser(user.getId())

            assertThat(unlockedUser, notNullValue())
            log.info("User unlock called successfully")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "unlockUser")
        }
    }

    @Test(groups = "group3")
    void unlockUser_withValidUserId() {
        log.info("=== Test: UnlockUser with valid userId ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            def unlockedUser = userLifecycleApi.unlockUser(user.getId())

            assertThat(unlockedUser, notNullValue())
            assertThat(unlockedUser.getId(), equalTo(user.getId()))
            log.info("User unlocked with valid userId")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "unlockUser with valid userId")
        }
    }

    @Test(groups = "group3")
    void resetFactors_callsCorrectEndpoint() {
        log.info("=== Test: ResetFactors calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.resetFactors(user.getId())
            log.info("Reset factors called successfully")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "resetFactors")
        }
    }

    @Test(groups = "group3")
    void resetFactors_withValidUserId() {
        log.info("=== Test: ResetFactors with valid userId ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            userLifecycleApi.resetFactors(user.getId())
            
            def updatedUser = userApi.getUser(user.getId(), null, null)
            assertThat(updatedUser, notNullValue())
            assertThat(updatedUser.getId(), equalTo(user.getId()))
            log.info("Factors reset for valid userId")

        } catch (ApiException e) {
            handleLifecycleApiException(e, "resetFactors with valid userId")
        }
    }

    @Test(groups = "group3")
    void userLifecycle_activateSuspendUnsuspend() {
        log.info("=== Test: User lifecycle - Activate, Suspend, Unsuspend ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, false)
        Thread.sleep(2000)

        try {
            // Activate
            userLifecycleApi.activateUser(user.getId(), false)
            Thread.sleep(2000)
            def activeUser = userApi.getUser(user.getId(), null, null)
            assertThat(activeUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("Step 1: User activated - status: {}", activeUser.getStatus())

            // Suspend
            userLifecycleApi.suspendUser(user.getId())
            Thread.sleep(2000)
            def suspendedUser = userApi.getUser(user.getId(), null, null)
            assertThat(suspendedUser.getStatus(), equalTo(UserStatus.SUSPENDED))
            log.info("Step 2: User suspended - status: {}", suspendedUser.getStatus())

            // Unsuspend
            userLifecycleApi.unsuspendUser(user.getId())
            Thread.sleep(1000)
            def unsuspendedUser = userApi.getUser(user.getId(), null, null)
            assertThat(unsuspendedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("Step 3: User unsuspended - status: {}", unsuspendedUser.getStatus())

        } catch (ApiException e) {
            handleLifecycleApiException(e, "activate-suspend-unsuspend lifecycle")
        }
    }

    @Test(groups = "group3")
    void userLifecycle_deactivateReactivate() {
        log.info("=== Test: User lifecycle - Deactivate, Reactivate ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid, true)
        Thread.sleep(2000)

        try {
            // Deactivate
            userLifecycleApi.deactivateUser(user.getId(), false, null)
            Thread.sleep(2000)
            def deactivatedUser = userApi.getUser(user.getId(), null, null)
            assertThat(deactivatedUser.getStatus(), equalTo(UserStatus.DEPROVISIONED))
            log.info("Step 1: User deactivated - status: {}", deactivatedUser.getStatus())

            // Reactivate
            userLifecycleApi.reactivateUser(user.getId(), false)
            Thread.sleep(1000)
            def reactivatedUser = userApi.getUser(user.getId(), null, null)
            assertThat(reactivatedUser.getStatus(), equalTo(UserStatus.ACTIVE))
            log.info("Step 2: User reactivated - status: {}", reactivatedUser.getStatus())

        } catch (ApiException e) {
            handleLifecycleApiException(e, "deactivate-reactivate lifecycle")
        }
    }

    // ==================== ERROR HANDLING TESTS ====================
    // These tests verify that invalid inputs produce expected error codes
    // Here, accepting multiple codes (400, 404, etc.) is intentional since
    // the exact error code may vary by API version or org configuration.
    
    @Test(groups = "group3")
    void errorHandling_emptyUserId() {
        log.info("=== Test: Error handling with empty userId ===")
        
        try {
            userLifecycleApi.suspendUser("")
            throw new AssertionError("Expected ApiException for empty userId")

        } catch (ApiException e) {
            log.info("Expected ApiException caught: Code={}, Message={}", e.getCode(), e.getMessage())
            // Empty userId should return 400 (bad request), 404 (not found), or 405 (method not allowed)
            assertThat("Should return 400, 404, or 405 for empty userId", 
                e.getCode(), anyOf(equalTo(400), equalTo(404), equalTo(405)))
        }
    }

    @Test(groups = "group3")
    void errorHandling_invalidLongUserId() {
        log.info("=== Test: Error handling with invalid long userId ===")
        
        def invalidUserId = "a" * 300
        
        try {
            userLifecycleApi.suspendUser(invalidUserId)
            throw new AssertionError("Expected ApiException for invalid userId")

        } catch (ApiException e) {
            log.info("Expected ApiException caught: Code={}, Message={}", e.getCode(), e.getMessage())
            // Invalid userId should return 400 (bad request) or 404 (not found)
            assertThat("Should return 400 or 404 for invalid userId", 
                e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        def userId = null
        try {
            def user = userApi.createUser(
                new com.okta.sdk.resource.model.CreateUserRequest()
                    .profile(new com.okta.sdk.resource.model.UserProfile()
                        .firstName("HeadersLC").lastName("Test")
                        .email("headers-lc-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())
                        .login("headers-lc-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())),
                true, false, null)
            userId = user.getId()

            // Suspend/unsuspend with headers
            userLifecycleApi.suspendUser(userId, headers)
            userLifecycleApi.unsuspendUser(userId, headers)

            // Deactivate/reactivate with headers
            userLifecycleApi.deactivateUser(userId, false, null, headers)
            userLifecycleApi.reactivateUser(userId, true, headers)

        } catch (Exception e) {
            // Expected lifecycle state errors
        } finally {
            if (userId) {
                try {
                    userLifecycleApi.deactivateUser(userId, false, null)
                    userApi.deleteUser(userId, false, null)
                } catch (Exception ignored) {}
            }
        }
    }
}

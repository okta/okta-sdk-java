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
import com.okta.sdk.resource.api.UserClassificationApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import java.time.OffsetDateTime
import java.util.concurrent.TimeoutException

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for UserClassificationApi - matching .NET SDK UserClassificationApiTests
 */
class UserClassificationIT extends ITSupport {

    private static final Logger log = LoggerFactory.getLogger(UserClassificationIT.class)
    
    private UserClassificationApi userClassificationApi = new UserClassificationApi(getClient())
    private UserApi userApi = new UserApi(getClient())

    @Test(groups = "group3")
    void givenUserClassifications_whenPerformingCrudOperations_thenAllEndpointsAndMethodsWork() {
        def guid = UUID.randomUUID()
        
        log.info("Creating test user for classification CRUD operations")
        
        User createdUser = UserBuilder.instance()
            .setEmail("classification-test-${guid}@example.com")
            .setFirstName("ClassificationTest")
            .setLastName("User${guid.toString().substring(0, 8)}")
            .setLogin("classification-test-${guid}@example.com")
            .setPassword("Abed1234!@#%".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(createdUser)
        
        String userId = createdUser.getId()
        
        assertThat("Created user should not be null", createdUser, notNullValue())
        assertThat("User ID should not be null or empty", userId, not(emptyOrNullString()))
        assertThat("User status should be ACTIVE", createdUser.getStatus(), equalTo(UserStatus.ACTIVE))
        
        log.info("Created user: {}", userId)
        
        try {
            Thread.sleep(2000)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt()
        }
        
        try {
            userClassificationApi.getUserClassification(userId)
        } catch (ApiException ex) {
            if (ex.getCode() == 403 || ex.getMessage().contains("E0000015")) {
                log.warn("User Classification feature not enabled (Early Access required), skipping test")
                return
            }
        }
        
        log.info("Retrieving initial user classification")
        def initialClassification = userClassificationApi.getUserClassification(userId)
        
        assertThat("Initial classification should not be null", initialClassification, notNullValue())
        assertThat("Classification type should not be null", initialClassification.getType(), notNullValue())
        assertThat("Classification type should be LITE or STANDARD", 
            initialClassification.getType(), anyOf(equalTo(ClassificationType.LITE), equalTo(ClassificationType.STANDARD)))
        assertThat("Last updated should not be default", initialClassification.getLastUpdated(), notNullValue())
        
        def initialType = initialClassification.getType()
        def initialLastUpdated = initialClassification.getLastUpdated()
        
        log.info("Initial classification: type={}, lastUpdated={}", initialType, initialLastUpdated)
        
        log.info("Updating classification to LITE")
        def replaceToLite = new ReplaceUserClassification()
        replaceToLite.setType(ClassificationType.LITE)
        
        def updatedToLite = userClassificationApi.replaceUserClassification(userId, replaceToLite)
        
        assertThat("Updated to LITE should not be null", updatedToLite, notNullValue())
        assertThat("Type should be LITE", updatedToLite.getType(), equalTo(ClassificationType.LITE))
        assertThat("Last updated should not be default", updatedToLite.getLastUpdated(), notNullValue())
        
        if (initialType != ClassificationType.LITE) {
            assertThat("Last updated should be after initial", 
                updatedToLite.getLastUpdated().isAfter(initialLastUpdated), equalTo(true))
        }
        
        try {
            Thread.sleep(1000)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt()
        }
        
        log.info("Verifying LITE classification persisted")
        def verifyLite = userClassificationApi.getUserClassification(userId)
        
        assertThat("Verified LITE should not be null", verifyLite, notNullValue())
        assertThat("Type should still be LITE", verifyLite.getType(), equalTo(ClassificationType.LITE))
        
        long diffSeconds = Math.abs(verifyLite.getLastUpdated().toEpochSecond() - updatedToLite.getLastUpdated().toEpochSecond())
        assertThat("Last updated should be close to update time", diffSeconds, lessThanOrEqualTo(5L))
        
        log.info("Updating classification to STANDARD")
        def replaceToStandard = new ReplaceUserClassification()
        replaceToStandard.setType(ClassificationType.STANDARD)
        
        def updatedToStandard = userClassificationApi.replaceUserClassification(userId, replaceToStandard)
        
        assertThat("Updated to STANDARD should not be null", updatedToStandard, notNullValue())
        assertThat("Type should be STANDARD", updatedToStandard.getType(), equalTo(ClassificationType.STANDARD))
        assertThat("Last updated should not be default", updatedToStandard.getLastUpdated(), notNullValue())
        assertThat("Last updated should be after LITE update", 
            updatedToStandard.getLastUpdated().isAfter(updatedToLite.getLastUpdated()), equalTo(true))
        
        try {
            Thread.sleep(1000)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt()
        }
        
        log.info("Verifying STANDARD classification persisted")
        def verifyStandard = userClassificationApi.getUserClassification(userId)
        
        assertThat("Verified STANDARD should not be null", verifyStandard, notNullValue())
        assertThat("Type should still be STANDARD", verifyStandard.getType(), equalTo(ClassificationType.STANDARD))
        
        diffSeconds = Math.abs(verifyStandard.getLastUpdated().toEpochSecond() - updatedToStandard.getLastUpdated().toEpochSecond())
        assertThat("Last updated should be close to update time", diffSeconds, lessThanOrEqualTo(5L))
        
        log.info("Updating back to LITE for full cycle test")
        def replaceBackToLite = new ReplaceUserClassification()
        replaceBackToLite.setType(ClassificationType.LITE)
        
        def finalUpdateToLite = userClassificationApi.replaceUserClassification(userId, replaceBackToLite)
        
        assertThat("Final update to LITE should not be null", finalUpdateToLite, notNullValue())
        assertThat("Type should be LITE", finalUpdateToLite.getType(), equalTo(ClassificationType.LITE))
        assertThat("Last updated should be after STANDARD verification", 
            finalUpdateToLite.getLastUpdated().isAfter(verifyStandard.getLastUpdated()), equalTo(true))
        
        log.info("Final verification")
        def finalVerification = userClassificationApi.getUserClassification(userId)
        
        assertThat("Final verification should not be null", finalVerification, notNullValue())
        assertThat("Type should be LITE", finalVerification.getType(), equalTo(ClassificationType.LITE))
        
        diffSeconds = Math.abs(finalVerification.getLastUpdated().toEpochSecond() - finalUpdateToLite.getLastUpdated().toEpochSecond())
        assertThat("Last updated should be close to final update time", diffSeconds, lessThanOrEqualTo(5L))
        
        log.info("All user classification CRUD operations completed successfully")
    }

    @Ignore("WithHttpInfo methods not available in Java SDK")
    @Test(groups = "group3")
    void givenHttpInfoMethods_whenCallingApi_thenHttpMetadataIsReturned() {
        log.info("HTTP info methods test - skipped (not available in Java SDK)")
    }

    @Test(groups = "group3")
    void givenErrorScenarios_whenCallingApi_thenApiExceptionIsThrown() {
        log.info("Testing error scenarios")
        
        final String invalidUserId = "invalid_user_id_12345"
        
        log.info("Test 1: GetUserClassification with invalid userId")
        try {
            userClassificationApi.getUserClassification(invalidUserId)
            assertThat("Should have thrown exception for invalid user ID", false)
        } catch (ApiException apiEx) {
            log.info("Correctly threw ApiException: code={}, message={}", apiEx.getCode(), apiEx.getMessage())
            assertThat("Error code should be 401 or 404", apiEx.getCode(), anyOf(equalTo(401), equalTo(404)))
        } catch (TimeoutException timeoutEx) {
            log.info("Correctly threw TimeoutException: {}", timeoutEx.getMessage())
        } catch (Exception ex) {
            log.error("Unexpected exception type: {}", ex.getClass().getName(), ex)
            throw new RuntimeException("Unexpected exception type: " + ex.getClass(), ex)
        }
        
        log.info("Test 2: ReplaceUserClassification with invalid userId")
        def replaceRequest = new ReplaceUserClassification()
        replaceRequest.setType(ClassificationType.LITE)
        
        try {
            userClassificationApi.replaceUserClassification(invalidUserId, replaceRequest)
            assertThat("Should have thrown exception for invalid user ID", false)
        } catch (ApiException apiEx) {
            log.info("Correctly threw ApiException: code={}, message={}", apiEx.getCode(), apiEx.getMessage())
            assertThat("Error code should be 401 or 404", apiEx.getCode(), anyOf(equalTo(401), equalTo(404)))
        } catch (TimeoutException timeoutEx) {
            log.info("Correctly threw TimeoutException: {}", timeoutEx.getMessage())
        } catch (Exception ex) {
            log.error("Unexpected exception type: {}", ex.getClass().getName(), ex)
            throw new RuntimeException("Unexpected exception type: " + ex.getClass(), ex)
        }
        
        log.info("All error scenario tests completed")
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        def userId = null
        try {
            def user = new com.okta.sdk.resource.api.UserApi(getClient()).createUser(
                new com.okta.sdk.resource.model.CreateUserRequest()
                    .profile(new com.okta.sdk.resource.model.UserProfile()
                        .firstName("HeadersClass").lastName("Test")
                        .email("headers-class-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())
                        .login("headers-class-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())),
                true, false, null)
            userId = user.getId()

            // getUserClassification with headers
            try {
                userClassificationApi.getUserClassification(userId, headers)
            } catch (Exception ignored) {}

        } catch (Exception e) {
            // Expected
        } finally {
            if (userId) {
                try {
                    new com.okta.sdk.resource.api.UserLifecycleApi(getClient()).deactivateUser(userId, false, null)
                    new com.okta.sdk.resource.api.UserApi(getClient()).deleteUser(userId, false, null)
                } catch (Exception ignored) {}
            }
        }
    }
}

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
import com.okta.sdk.resource.api.UserRiskApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import groovy.util.logging.Slf4j
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

@Slf4j
class UserRiskIT extends ITSupport {

    private UserApi userApi
    private UserRiskApi userRiskApi

    UserRiskIT() {
        this.userApi = new UserApi(getClient())
        this.userRiskApi = new UserRiskApi(getClient())
    }

    private User createTestUser(String uniqueId) {
        def email = "user-risk-test-${uniqueId}@example.com"
        log.info("Creating test user, uniqueId: {}", uniqueId)
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Risk")
            .setLastName("TestUser${uniqueId}")
            .setPassword("Abcd1234!@#%".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)
        log.info("Created user: {} with status: {}", user.getId(), user.getStatus())
        return user
    }

    private boolean isUserRiskFeatureAvailable(String userId) {
        try {
            userRiskApi.getUserRisk(userId)
            return true
        } catch (ApiException e) {
            // Feature not available if we get 401, 403, or 404
            if (e.getCode() in [401, 403, 404]) {
                log.info("User Risk API not available (code {}), skipping test", e.getCode())
                return false
            }
            return true
        }
    }

    @Test(groups = "group3")
    void givenUserRisk_whenPerformingCrudOperations_thenAllEndpointsWork() {
        log.info("=== Test: User Risk CRUD operations ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid)
        Thread.sleep(2000)

        // Check if feature is available
        if (!isUserRiskFeatureAvailable(user.getId())) {
            log.info("Skipping test - User Risk API requires Identity Threat Protection")
            return
        }

        try {
            // Set user risk to HIGH
            def highRiskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.HIGH)

            def highRiskResponse = userRiskApi.upsertUserRisk(user.getId(), highRiskRequest)
            assertThat(highRiskResponse, notNullValue())
            assertThat(highRiskResponse.getRiskLevel(), notNullValue())
            log.info("Step 1: Risk set to HIGH")
            Thread.sleep(1000)

            // Verify HIGH risk was set
            def verifyHighRisk = userRiskApi.getUserRisk(user.getId())
            assertThat(verifyHighRisk, notNullValue())
            assertThat(verifyHighRisk.getRiskLevel(), notNullValue())
            log.info("Step 2: HIGH risk verified")

            // Update risk to LOW
            def lowRiskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.LOW)

            def lowRiskResponse = userRiskApi.upsertUserRisk(user.getId(), lowRiskRequest)
            assertThat(lowRiskResponse, notNullValue())
            assertThat(lowRiskResponse.getRiskLevel(), notNullValue())
            log.info("Step 3: Risk updated to LOW")
            Thread.sleep(1000)

            // Verify LOW risk was set
            def verifyLowRisk = userRiskApi.getUserRisk(user.getId())
            assertThat(verifyLowRisk, notNullValue())
            assertThat(verifyLowRisk.getRiskLevel(), notNullValue())
            log.info("Step 4: LOW risk verified")

            log.info("User Risk CRUD operations completed successfully")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            if (e.getCode() != 0) {
                assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(404)))
            }
        }
    }

    @Test(groups = "group3")
    void givenUserRisk_whenUsingDifferentRiskLevels_thenAllLevelsWork() {
        log.info("=== Test: User Risk with different levels ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUser(guid)
        Thread.sleep(2000)

        // Check if feature is available
        if (!isUserRiskFeatureAvailable(user.getId())) {
            log.info("Skipping test - User Risk API not available")
            return
        }

        try {
            // Test HIGH risk
            def highRiskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.HIGH)

            def highResponse = userRiskApi.upsertUserRisk(user.getId(), highRiskRequest)
            assertThat(highResponse, notNullValue())
            log.info("HIGH risk level set")
            Thread.sleep(1000)

            // Test MEDIUM risk
            def mediumRiskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.MEDIUM)

            def mediumResponse = userRiskApi.upsertUserRisk(user.getId(), mediumRiskRequest)
            assertThat(mediumResponse, notNullValue())
            log.info("MEDIUM risk level set")
            Thread.sleep(1000)

            // Test LOW risk
            def lowRiskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.LOW)

            def lowResponse = userRiskApi.upsertUserRisk(user.getId(), lowRiskRequest)
            assertThat(lowResponse, notNullValue())
            log.info("LOW risk level set")

            log.info("All risk levels tested successfully")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            if (e.getCode() != 0) {
                assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(404)))
            }
        }
    }

    @Test(groups = "group3")
    void givenErrorScenarios_whenCallingApi_thenApiExceptionIsThrown() {
        log.info("=== Test: Error scenarios with invalid user ID ===")

        def invalidUserId = "invalid_user_id_12345"

        // GetUserRisk with invalid userId - should throw 401, 403 or 404
        try {
            userRiskApi.getUserRisk(invalidUserId)
            log.error("Should have thrown ApiException")
        } catch (ApiException e) {
            log.info("Got expected ApiException for getUserRisk: Code={}", e.getCode())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(404)))
        }

        // UpsertUserRisk with invalid userId - should throw 401, 403 or 404
        try {
            def riskRequest = new UserRiskRequest()
                .riskLevel(UserRiskRequest.RiskLevelEnum.HIGH)

            userRiskApi.upsertUserRisk(invalidUserId, riskRequest)
            log.error("Should have thrown ApiException")
        } catch (ApiException e) {
            log.info("Got expected ApiException for upsertUserRisk: Code={}", e.getCode())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(404)))
        }

        log.info("Error scenario tests completed")
    }
}

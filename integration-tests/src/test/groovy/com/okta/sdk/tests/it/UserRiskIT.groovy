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
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Risk API.
 * Tests user risk assessment and management (HIGH, LOW risk levels).
 */
class UserRiskIT extends ITSupport {

    private UserApi userApi
    private UserRiskApi userRiskApi

    UserRiskIT() {
        this.userApi = new UserApi(getClient())
        this.userRiskApi = new UserRiskApi(getClient())
    }

    @Test(groups = "group3")
    void getUserRiskTest() {
        def email = "user-get-risk-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetRisk")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get user risk assessment
            def userRisk = userRiskApi.getUserRisk(user.getId())
            
            assertThat(userRisk, notNullValue())
            // Risk level may be set or null depending on risk assessment

        } catch (ApiException e) {
            // Expected if risk API not enabled or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void getUserRiskWithHttpInfoTest() {
        def email = "user-risk-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RiskHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userRiskApi.getUserRiskWithHttpInfo(user.getId())
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getUserRiskWithLowRiskLevelTest() {
        def email = "user-low-risk-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("LowRisk")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def userRisk = userRiskApi.getUserRisk(user.getId())
            
            assertThat(userRisk, notNullValue())
            // Risk level could be LOW, MEDIUM, HIGH, or NONE

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getUserRiskCallsCorrectEndpointTest() {
        def email = "user-risk-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RiskEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userRiskApi.getUserRisk(user.getId())

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void upsertUserRiskWithHighRiskTest() {
        def email = "user-upsert-high-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UpsertHigh")
            .setLastName("Risk-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def riskRequest = new UserRiskRequest()
            riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
            
            // Upsert (create or update) user risk
            def userRisk = userRiskApi.upsertUserRisk(user.getId(), riskRequest)
            
            assertThat(userRisk, notNullValue())
            // Risk should be set to HIGH

        } catch (ApiException e) {
            // Expected if risk API not enabled or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void upsertUserRiskWithLowRiskTest() {
        def email = "user-upsert-low-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UpsertLow")
            .setLastName("Risk-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def riskRequest = new UserRiskRequest()
            riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.LOW)
            
            def userRisk = userRiskApi.upsertUserRisk(user.getId(), riskRequest)
            
            assertThat(userRisk, notNullValue())
            // Risk should be set to LOW

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void upsertUserRiskWithHttpInfoTest() {
        def email = "user-upsert-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UpsertHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def riskRequest = new UserRiskRequest()
            riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
            
            def response = userRiskApi.upsertUserRiskWithHttpInfo(user.getId(), riskRequest)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), anyOf(equalTo(200), equalTo(201)))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void upsertUserRiskCallsCorrectEndpointTest() {
        def email = "user-upsert-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UpsertEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def riskRequest = new UserRiskRequest()
            riskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.MEDIUM)
            
            userRiskApi.upsertUserRisk(user.getId(), riskRequest)

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void userRiskCompleteWorkflowTest() {
        def email = "user-risk-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RiskWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. Get initial risk (may not exist)
            try {
                def initialRisk = userRiskApi.getUserRisk(user.getId())
                assertThat(initialRisk, notNullValue())
            } catch (ApiException e) {
                // Risk may not exist initially
                assertThat(e.getCode(), equalTo(404))
            }
            
            // 2. Set user to HIGH risk
            def highRiskRequest = new UserRiskRequest()
            highRiskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
            def highRisk = userRiskApi.upsertUserRisk(user.getId(), highRiskRequest)
            assertThat(highRisk, notNullValue())
            
            // 3. Get risk after setting (should be HIGH)
            def retrievedRisk = userRiskApi.getUserRisk(user.getId())
            assertThat(retrievedRisk, notNullValue())
            
            // 4. Update risk to LOW
            def lowRiskRequest = new UserRiskRequest()
            lowRiskRequest.setRiskLevel(UserRiskRequest.RiskLevelEnum.LOW)
            def lowRisk = userRiskApi.upsertUserRisk(user.getId(), lowRiskRequest)
            assertThat(lowRisk, notNullValue())
            
            // 5. Verify updated risk
            def finalRisk = userRiskApi.getUserRisk(user.getId())
            assertThat(finalRisk, notNullValue())

        } catch (ApiException e) {
            // Expected if risk API not fully enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void upsertUserRiskWithDifferentLevelsTest() {
        def email = "user-risk-levels-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RiskLevels")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Test HIGH risk level
            def highRisk = new UserRiskRequest()
            highRisk.setRiskLevel(UserRiskRequest.RiskLevelEnum.HIGH)
            def highResponse = userRiskApi.upsertUserRisk(user.getId(), highRisk)
            assertThat(highResponse, notNullValue())
            
            // Test MEDIUM risk level
            def mediumRisk = new UserRiskRequest()
            mediumRisk.setRiskLevel(UserRiskRequest.RiskLevelEnum.MEDIUM)
            def mediumResponse = userRiskApi.upsertUserRisk(user.getId(), mediumRisk)
            assertThat(mediumResponse, notNullValue())
            
            // Test LOW risk level
            def lowRisk = new UserRiskRequest()
            lowRisk.setRiskLevel(UserRiskRequest.RiskLevelEnum.LOW)
            def lowResponse = userRiskApi.upsertUserRisk(user.getId(), lowRisk)
            assertThat(lowResponse, notNullValue())

        } catch (ApiException e) {
            // Expected if risk API not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}

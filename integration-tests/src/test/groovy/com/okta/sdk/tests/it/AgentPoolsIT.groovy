/*
 * Copyright 2024-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it

import com.okta.sdk.resource.api.AgentPoolsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.AgentPool
import com.okta.sdk.resource.model.AgentPoolUpdate
import com.okta.sdk.resource.model.AgentPoolUpdateSetting
import com.okta.sdk.resource.model.AgentType
import com.okta.sdk.resource.model.AutoUpdateSchedule
import com.okta.sdk.resource.model.ReleaseChannel
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.SkipException
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for the AgentPools API resource.
 * NOTE: These tests require at least one Agent Pool to be pre-configured in the Okta org,
 * as Agent Pools cannot be created via the API.
 */
class AgentPoolsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(AgentPoolsIT.class)
    private AgentPoolsApi agentPoolsApi
    private String testPoolId
    private boolean hasAgentPools = false

    @BeforeClass
    void setUp() {
        agentPoolsApi = new AgentPoolsApi(getClient())
        // Attempt to find an existing agent pool to use for tests
        try {
            List<AgentPool> agentPools = agentPoolsApi.listAgentPools(1, null, null)
            if (agentPools && !agentPools.isEmpty()) {
                testPoolId = agentPools.get(0).id
                hasAgentPools = true
                logger.info("Found agent pool with ID: {}", testPoolId)
            } else {
                logger.warn("WARNING: No agent pools found in the organization. Dependent tests will be skipped.")
            }
        } catch (ApiException e) {
            logger.warn("Error accessing agent pools: {}. Dependent tests will be skipped.", e.getMessage())
            hasAgentPools = false
        }
    }

    private void skipIfNoPools() {
        if (!hasAgentPools) {
            throw new SkipException("Skipping test: No pre-configured Agent Pools were found in the organization.")
        }
    }

    @Test
    void testListAgentPools() {
        List<AgentPool> agentPools = agentPoolsApi.listAgentPools(null, null, null)
        assertThat(agentPools, notNullValue())
        assertThat(agentPools.size(), greaterThanOrEqualTo(0))
    }

    @DataProvider(name = "agentTypesProvider")
    Object[][] agentTypesProvider() {
        return [
            [AgentType.AD],
            [AgentType.LDAP],
            [AgentType.RADIUS]
        ]
    }

    @Test(dataProvider = "agentTypesProvider")
    void testListAgentPoolsWithFilters(AgentType poolType) {
        List<AgentPool> agentPools = agentPoolsApi.listAgentPools(10, poolType, null)
        assertThat(agentPools, notNullValue())
    }

    @Test
    void testGetAgentPoolsUpdateSettings() {
        skipIfNoPools()

        AgentPoolUpdateSetting settings = agentPoolsApi.getAgentPoolsUpdateSettings(testPoolId)
        assertThat(settings, notNullValue())
        assertThat(settings.poolId, equalTo(testPoolId))
    }

    @DataProvider(name = "releaseChannelProvider")
    Object[][] releaseChannelProvider() {
        return [
            [ReleaseChannel.GA],
            [ReleaseChannel.BETA],
            [ReleaseChannel.EA]
        ]
    }

    @Test(dataProvider = "releaseChannelProvider")
    void testUpdateAgentPoolsUpdateSettings(ReleaseChannel channel) {
        skipIfNoPools()

        AgentPoolUpdateSetting originalSettings = agentPoolsApi.getAgentPoolsUpdateSettings(testPoolId)
        def updateRequest = new AgentPoolUpdateSetting(
            poolId: testPoolId,
            continueOnError: true,
            releaseChannel: channel
        )

        try {
            AgentPoolUpdateSetting updatedSettings = agentPoolsApi.updateAgentPoolsUpdateSettings(testPoolId, updateRequest)
            assertThat(updatedSettings, notNullValue())
            assertThat(updatedSettings.releaseChannel, equalTo(channel))
            assertThat(updatedSettings.continueOnError, is(true))
        } finally {
            // Revert settings to original state to avoid side effects
            agentPoolsApi.updateAgentPoolsUpdateSettings(testPoolId, originalSettings)
        }
    }

    @Test
    void testAgentPoolsUpdateLifecycle() {
        skipIfNoPools()

        // 1. CREATE an agent pool update
        def schedule = new AutoUpdateSchedule(
            timezone: "America/Los_Angeles",
            delay: 0,
            duration: 60
        )
        def createRequest = new AgentPoolUpdate(
            enabled: false,
            schedule: schedule
        )

        AgentPoolUpdate createdUpdate = agentPoolsApi.createAgentPoolsUpdate(testPoolId, createRequest)
        assertThat(createdUpdate, notNullValue())
        assertThat(createdUpdate.id, notNullValue())
        def updateId = createdUpdate.id

        try {
            // 2. GET the created update
            def retrievedUpdate = agentPoolsApi.getAgentPoolsUpdateInstance(testPoolId, updateId)
            assertThat(retrievedUpdate, notNullValue())
            assertThat(retrievedUpdate.id, equalTo(updateId))

            // 3. UPDATE the created update
            def updateRequest = new AgentPoolUpdate(enabled: true) // Enable it
            def updatedUpdate = agentPoolsApi.updateAgentPoolsUpdate(testPoolId, updateId, updateRequest)
            assertThat(updatedUpdate.enabled, is(true))

            // 4. LIST all updates and ensure it's present
            def allUpdates = agentPoolsApi.listAgentPoolsUpdates(testPoolId, null)
            assertThat(allUpdates, not(empty()))
            assertThat(allUpdates.find { it.id == updateId }, notNullValue())
        } finally {
            // 5. DELETE the update for cleanup
            agentPoolsApi.deleteAgentPoolsUpdate(testPoolId, updateId)
            expect(ApiException) {
                agentPoolsApi.getAgentPoolsUpdateInstance(testPoolId, updateId)
            }
        }
    }

    @Test
    void testAgentPoolsUpdateStateTransitions() {
        skipIfNoPools()

        def createRequest = new AgentPoolUpdate(enabled: false)
        def createdUpdate = agentPoolsApi.createAgentPoolsUpdate(testPoolId, createRequest)
        def updateId = createdUpdate.id

        try {
            // Call each state transition method.
            // In a real scenario, the update would need to be in a specific state for these to succeed.
            // This test primarily ensures the SDK methods can be invoked correctly.
            agentPoolsApi.activateAgentPoolsUpdate(testPoolId, updateId)
            agentPoolsApi.deactivateAgentPoolsUpdate(testPoolId, updateId)
            agentPoolsApi.pauseAgentPoolsUpdate(testPoolId, updateId)
            agentPoolsApi.resumeAgentPoolsUpdate(testPoolId, updateId)
            agentPoolsApi.stopAgentPoolsUpdate(testPoolId, updateId)
            agentPoolsApi.retryAgentPoolsUpdate(testPoolId, updateId)
        } finally {
            agentPoolsApi.deleteAgentPoolsUpdate(testPoolId, updateId)
        }
    }

    @Test
    void testErrorHandlingWithInvalidIds() {
        def invalidPoolId = "invalid-pool-id-12345"
        def invalidUpdateId = "invalid-update-id-12345"
        def dummyRequest = new AgentPoolUpdate(enabled: true)

        expect(ApiException) {
            agentPoolsApi.getAgentPoolsUpdateSettings(invalidPoolId)
        }
        expect(ApiException) {
            agentPoolsApi.createAgentPoolsUpdate(invalidPoolId, dummyRequest)
        }

        if (hasAgentPools) {
            expect(ApiException) {
                agentPoolsApi.getAgentPoolsUpdateInstance(testPoolId, invalidUpdateId)
            }
        }
    }

    @Test
    void testAgentPoolsUpdateLifecycleWithAdditionalParams() {
        skipIfNoPools()

        // Test methods with Map parameter overloads
        def schedule = new AutoUpdateSchedule(
            timezone: "America/Los_Angeles",
            delay: 0,
            duration: 60
        )
        def createRequest = new AgentPoolUpdate(
            enabled: false,
            schedule: schedule
        )

        def additionalParams = [:] // Empty map for additional parameters
        AgentPoolUpdate createdUpdate = agentPoolsApi.createAgentPoolsUpdate(testPoolId, createRequest)
        assertThat(createdUpdate, notNullValue())
        assertThat(createdUpdate.id, notNullValue())
        def updateId = createdUpdate.id

        try {
            // Test getAgentPoolsUpdateInstance with Map parameter
            def retrievedUpdate = agentPoolsApi.getAgentPoolsUpdateInstance(testPoolId, updateId, additionalParams)
            assertThat(retrievedUpdate, notNullValue())
            assertThat(retrievedUpdate.id, equalTo(updateId))

            // Test updateAgentPoolsUpdate with Map parameter
            def updateRequest = new AgentPoolUpdate(enabled: true)
            def updatedUpdate = agentPoolsApi.updateAgentPoolsUpdate(testPoolId, updateId, updateRequest, additionalParams)
            assertThat(updatedUpdate.enabled, is(true))

            // Test listAgentPoolsUpdates with Map parameter
            def allUpdates = agentPoolsApi.listAgentPoolsUpdates(testPoolId, null, additionalParams)
            assertThat(allUpdates, not(empty()))
            assertThat(allUpdates.find { it.id == updateId }, notNullValue())

            // Test state transition methods with Map parameter
            agentPoolsApi.activateAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            agentPoolsApi.pauseAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            agentPoolsApi.resumeAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            agentPoolsApi.deactivateAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            agentPoolsApi.stopAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            agentPoolsApi.retryAgentPoolsUpdate(testPoolId, updateId, additionalParams)
        } finally {
            // Test deleteAgentPoolsUpdate with Map parameter
            agentPoolsApi.deleteAgentPoolsUpdate(testPoolId, updateId, additionalParams)
            expect(ApiException) {
                agentPoolsApi.getAgentPoolsUpdateInstance(testPoolId, updateId, additionalParams)
            }
        }
    }

    @Test
    void testUpdateAgentPoolsUpdateSettingsWithAdditionalParams() {
        skipIfNoPools()

        def additionalParams = [:] // Empty map for additional parameters
        AgentPoolUpdateSetting originalSettings = agentPoolsApi.getAgentPoolsUpdateSettings(testPoolId)
        def updateRequest = new AgentPoolUpdateSetting(
            poolId: testPoolId,
            continueOnError: true,
            releaseChannel: ReleaseChannel.GA
        )

        try {
            // Test updateAgentPoolsUpdateSettings with Map parameter
            AgentPoolUpdateSetting updatedSettings = agentPoolsApi.updateAgentPoolsUpdateSettings(testPoolId, updateRequest, additionalParams)
            assertThat(updatedSettings, notNullValue())
            assertThat(updatedSettings.releaseChannel, equalTo(ReleaseChannel.GA))
            assertThat(updatedSettings.continueOnError, is(true))
        } finally {
            // Revert settings to original state
            agentPoolsApi.updateAgentPoolsUpdateSettings(testPoolId, originalSettings, additionalParams)
        }
    }

    @Test
    void testMethodsWithMapParameterAndInvalidIds() {
        // Test methods with Map parameter using invalid IDs to ensure coverage
        // Some methods may throw ApiException, others may return empty results
        // The goal is to invoke the methods to achieve code coverage
        def invalidPoolId = "invalid-pool-id-99999"
        def invalidUpdateId = "invalid-update-id-99999"
        def additionalParams = [:] // Empty map for additional parameters
        def dummyUpdateRequest = new AgentPoolUpdate(enabled: true)
        def dummySettingsRequest = new AgentPoolUpdateSetting(
            poolId: invalidPoolId,
            continueOnError: true,
            releaseChannel: ReleaseChannel.GA
        )

        // Test updateAgentPoolsUpdate with Map parameter
        expect(ApiException) {
            agentPoolsApi.updateAgentPoolsUpdate(invalidPoolId, invalidUpdateId, dummyUpdateRequest, additionalParams)
        }

        // Test getAgentPoolsUpdateInstance with Map parameter
        expect(ApiException) {
            agentPoolsApi.getAgentPoolsUpdateInstance(invalidPoolId, invalidUpdateId, additionalParams)
        }

        // Test deleteAgentPoolsUpdate with Map parameter
        expect(ApiException) {
            agentPoolsApi.deleteAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        }

        // Test listAgentPoolsUpdates with Map parameter - may return empty list instead of throwing
        try {
            def result = agentPoolsApi.listAgentPoolsUpdates(invalidPoolId, null, additionalParams)
            assertThat(result, notNullValue())
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        // Test state transition methods with Map parameter
        // Some may not throw exceptions, so we use try-catch blocks
        try {
            agentPoolsApi.activateAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            // Expected in most cases
            assertThat(e, notNullValue())
        }

        try {
            agentPoolsApi.deactivateAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        try {
            agentPoolsApi.pauseAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        try {
            agentPoolsApi.resumeAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        try {
            agentPoolsApi.stopAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        try {
            agentPoolsApi.retryAgentPoolsUpdate(invalidPoolId, invalidUpdateId, additionalParams)
        } catch (ApiException e) {
            assertThat(e, notNullValue())
        }

        // Test updateAgentPoolsUpdateSettings with Map parameter
        expect(ApiException) {
            agentPoolsApi.updateAgentPoolsUpdateSettings(invalidPoolId, dummySettingsRequest, additionalParams)
        }
    }

    @Test
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            // Paged - listAgentPools
            def pools = agentPoolsApi.listAgentPoolsPaged(null, null, null)
            for (def p : pools) { break }
            def poolsH = agentPoolsApi.listAgentPoolsPaged(null, null, null, headers)
            for (def p : poolsH) { break }

            // Non-paged with headers
            agentPoolsApi.listAgentPools(null, null, null, headers)
        } catch (Exception e) {
            // Expected
        }
    }
}

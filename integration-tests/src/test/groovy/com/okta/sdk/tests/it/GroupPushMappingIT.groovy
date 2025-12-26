/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.GroupPushMappingApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for {@code /api/v1/apps/{appId}/group-push/mappings}.
 *
 * <p>These tests cover the complete lifecycle of Group Push Mapping API operations including:
 * <ul>
 *   <li>Creating group push mappings with new or existing target groups</li>
 *   <li>Retrieving specific mappings</li>
 *   <li>Listing mappings with filtering and pagination</li>
 *   <li>Updating mapping status (ACTIVE/INACTIVE)</li>
 *   <li>Deleting mappings with/without target group deletion</li>
 *   <li>Error handling and validation</li>
 *   <li>Lifecycle enforcement (must be INACTIVE before deletion)</li>
 * </ul>
 *
 * <p>REST Endpoints tested:
 * <ul>
 *   <li>POST   /api/v1/apps/{appId}/group-push/mappings - Create mapping</li>
 *   <li>GET    /api/v1/apps/{appId}/group-push/mappings - List mappings</li>
 *   <li>GET    /api/v1/apps/{appId}/group-push/mappings/{mappingId} - Get mapping</li>
 *   <li>PATCH  /api/v1/apps/{appId}/group-push/mappings/{mappingId} - Update mapping</li>
 *   <li>DELETE /api/v1/apps/{appId}/group-push/mappings/{mappingId} - Delete mapping</li>
 * </ul>
 */
class GroupPushMappingIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(GroupPushMappingIT.class)

    private GroupApi groupApi = new GroupApi(getClient())
    private ApplicationApi applicationApi = new ApplicationApi(getClient())
    private GroupPushMappingApi groupPushMappingApi = new GroupPushMappingApi(getClient())

    private List<String> groupsToCleanup = []
    private List<String> appsToCleanup = []
    private List<String> mappingsToCleanup = []
    private String testApplicationId = null

    @AfterMethod
    void cleanup() {
        logger.info("Starting cleanup...")

        // Clean up mappings first (must deactivate before deleting)
        if (testApplicationId) {
            for (String mappingId : mappingsToCleanup) {
                try {
                    logger.debug("Deactivating and deleting mapping: {}", mappingId)
                    // Try to deactivate first
                    try {
                        UpdateGroupPushMappingRequest updateRequest = new UpdateGroupPushMappingRequest()
                        updateRequest.status(GroupPushMappingStatusUpsert.INACTIVE)
                        groupPushMappingApi.updateGroupPushMapping(testApplicationId, mappingId, updateRequest)
                    } catch (ApiException e) {
                        logger.debug("Mapping already inactive or doesn't exist: {}", e.getMessage())
                    }

                    // Then delete (deleteTargetGroup = false to preserve downstream groups)
                    groupPushMappingApi.deleteGroupPushMapping(testApplicationId, mappingId, false)
                    logger.debug("Successfully deleted mapping: {}", mappingId)
                } catch (ApiException e) {
                    logger.warn("Failed to cleanup mapping {}: {}", mappingId, e.getMessage())
                }
            }
        }
        mappingsToCleanup.clear()

        // Clean up applications
        for (String appId : appsToCleanup) {
            try {
                logger.debug("Deactivating and deleting app: {}", appId)
                applicationApi.deactivateApplication(appId)
                TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
                applicationApi.deleteApplication(appId)
                logger.debug("Successfully deleted app: {}", appId)
            } catch (ApiException e) {
                logger.warn("Failed to cleanup app {}: {}", appId, e.getMessage())
            }
        }
        appsToCleanup.clear()
        testApplicationId = null

        // Clean up groups
        for (String groupId : groupsToCleanup) {
            try {
                logger.debug("Deleting group: {}", groupId)
                groupApi.deleteGroup(groupId)
                logger.debug("Successfully deleted group: {}", groupId)
            } catch (ApiException e) {
                logger.warn("Failed to cleanup group {}: {}", groupId, e.getMessage())
            }
        }
        groupsToCleanup.clear()

        logger.info("Cleanup complete")
    }

    private void registerMapping(String mappingId) {
        mappingsToCleanup.add(mappingId)
        logger.debug("Registered mapping for cleanup: {}", mappingId)
    }

    private void unregisterMapping(String mappingId) {
        mappingsToCleanup.remove(mappingId)
        logger.debug("Unregistered mapping from cleanup: {}", mappingId)
    }

    private Application createTestApplication() {
        if (testApplicationId != null) {
            // Reuse existing app
            return applicationApi.getApplication(testApplicationId, null)
        }

        String appLabel = "GroupPushMapping-TestApp-${uniqueTestName}"

        // Create an OIDC application that can support group push
        OpenIdConnectApplication application = new OpenIdConnectApplication()
        application.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
        application.label(appLabel)
        application.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)
        oauthClient.autoKeyRotation(true)
        credentials.oauthClient(oauthClient)
        application.credentials(credentials)

        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient oauthClientSettings = new OpenIdConnectApplicationSettingsClient()
        oauthClientSettings.clientUri("https://example.com/client")
        oauthClientSettings.responseTypes([OAuthResponseType.CODE])
        oauthClientSettings.redirectUris(["https://example.com/oauth2/callback"])
        oauthClientSettings.grantTypes([GrantType.AUTHORIZATION_CODE])
        oauthClientSettings.applicationType(OpenIdConnectApplicationType.WEB)
        settings.oauthClient(oauthClientSettings)
        application.settings(settings)

        Application createdApp = applicationApi.createApplication(application, true, null)
        testApplicationId = createdApp.getId()
        appsToCleanup.add(testApplicationId)
        logger.info("Created test application: {}", testApplicationId)

        return createdApp
    }

    private boolean isGroupPushMappingApiAvailable(String appId) {
        try {
            // Try to list mappings to see if the API is available
            groupPushMappingApi.listGroupPushMappings(appId, null, null, null, null, null)
            return true
        } catch (ApiException e) {
            if (e.getCode() == 403 || e.getCode() == 404) {
                logger.info("Group Push Mapping API not available: {}", e.getMessage())
                return false
            }
            throw e
        }
    }

    private Group createTestGroup(String nameSuffix) {
        String groupName = "GroupPushMapping-${nameSuffix}-${uniqueTestName}"
        Group group = GroupBuilder.instance()
            .setName(groupName)
            .setDescription("Source group for group push mapping tests")
            .buildAndCreate(groupApi)
        groupsToCleanup.add(group.getId())
        logger.debug("Created test group: {} ({})", group.getId(), groupName)
        return group
    }

    // ========================================
    // Test 1: Comprehensive Group Push Mapping Operations
    // ========================================
    @Test(groups = "group1")
    @Scenario("comprehensive-group-push-mapping-operations")
    void testComprehensiveGroupPushMappingOperations() {
        logger.info("Testing comprehensive Group Push Mapping API operations...")

        // ==================== SETUP ====================
        Application app = createTestApplication()
        
        // Check if Group Push Mapping API is available
        if (!isGroupPushMappingApiAvailable(app.getId())) {
            logger.info("Skipping test - Group Push Mapping API not available")
            return
        }
        
        Group sourceGroup1 = createTestGroup("Source1")
        Group sourceGroup2 = createTestGroup("Source2")
        Group sourceGroup3 = createTestGroup("Source3")

        // ==================== CREATE OPERATIONS ====================

        // Scenario 1: Create mapping with a new target group (targetGroupName)
        String targetGroupName1 = "PushTarget-New-${uniqueTestName}"
        CreateGroupPushMappingRequest createRequest1 = new CreateGroupPushMappingRequest()
        createRequest1.sourceGroupId(sourceGroup1.getId())
        createRequest1.targetGroupName(targetGroupName1)
        createRequest1.status(GroupPushMappingStatusUpsert.ACTIVE)

        GroupPushMapping createdMapping1 = groupPushMappingApi.createGroupPushMapping(app.getId(), createRequest1)
        registerMapping(createdMapping1.getId())

        // Verify creation
        assertThat("Created mapping should not be null", createdMapping1, notNullValue())
        assertThat("Mapping ID should not be null", createdMapping1.getId(), notNullValue())
        assertThat("Mapping ID should start with correct prefix", createdMapping1.getId(), startsWith("gPm"))
        assertThat("Source group ID should match", createdMapping1.getSourceGroupId(), is(sourceGroup1.getId()))
        assertThat("Target group ID should be created", createdMapping1.getTargetGroupId(), notNullValue())
        assertThat("Status should be ACTIVE", createdMapping1.getStatus(), is(GroupPushMappingStatus.ACTIVE))
        assertThat("Created timestamp should be recent", createdMapping1.getCreated(), notNullValue())
        assertThat("LastUpdated timestamp should be recent", createdMapping1.getLastUpdated(), notNullValue())

        // Validate Links structure
        assertThat("Links should not be null", createdMapping1.getLinks(), notNullValue())
        assertThat("App link should not be null", createdMapping1.getLinks().getApp(), notNullValue())
        assertThat("App link href should contain app ID", createdMapping1.getLinks().getApp().getHref(), containsString(app.getId()))
        assertThat("Source group link should not be null", createdMapping1.getLinks().getSourceGroup(), notNullValue())
        assertThat("Source group link href should contain source group ID", 
            createdMapping1.getLinks().getSourceGroup().getHref(), containsString(sourceGroup1.getId()))
        assertThat("Target group link should not be null", createdMapping1.getLinks().getTargetGroup(), notNullValue())
        assertThat("Target group link href should not be empty", 
            createdMapping1.getLinks().getTargetGroup().getHref(), not(isEmptyOrNullString()))

        String mapping1Id = createdMapping1.getId()
        String targetGroupId1 = createdMapping1.getTargetGroupId()

        // Scenario 2: Create mapping with INACTIVE status
        String targetGroupName2 = "PushTarget-Inactive-${uniqueTestName}"
        CreateGroupPushMappingRequest createRequest2 = new CreateGroupPushMappingRequest()
        createRequest2.sourceGroupId(sourceGroup2.getId())
        createRequest2.targetGroupName(targetGroupName2)
        createRequest2.status(GroupPushMappingStatusUpsert.INACTIVE)

        GroupPushMapping createdMapping2 = groupPushMappingApi.createGroupPushMapping(app.getId(), createRequest2)
        registerMapping(createdMapping2.getId())

        assertThat("Mapping should be created", createdMapping2, notNullValue())
        assertThat("Mapping ID should start with gPm", createdMapping2.getId(), startsWith("gPm"))
        assertThat("Source group ID should match", createdMapping2.getSourceGroupId(), is(sourceGroup2.getId()))
        assertThat("Status should be INACTIVE", createdMapping2.getStatus(), is(GroupPushMappingStatus.INACTIVE))
        assertThat("Links should be complete", createdMapping2.getLinks(), notNullValue())

        String mapping2Id = createdMapping2.getId()

        // Scenario 3: Create another mapping with new target group
        String targetGroupName3 = "PushTarget-Third-${uniqueTestName}"
        CreateGroupPushMappingRequest createRequest3 = new CreateGroupPushMappingRequest()
        createRequest3.sourceGroupId(sourceGroup3.getId())
        createRequest3.targetGroupName(targetGroupName3)
        createRequest3.status(GroupPushMappingStatusUpsert.ACTIVE)

        GroupPushMapping createdMapping3 = groupPushMappingApi.createGroupPushMapping(app.getId(), createRequest3)
        registerMapping(createdMapping3.getId())

        assertThat("Third mapping should be created", createdMapping3, notNullValue())
        assertThat("Source group ID should match", createdMapping3.getSourceGroupId(), is(sourceGroup3.getId()))
        assertThat("Target group ID should be created", createdMapping3.getTargetGroupId(), notNullValue())
        assertThat("Status should be ACTIVE", createdMapping3.getStatus(), is(GroupPushMappingStatus.ACTIVE))

        String mapping3Id = createdMapping3.getId()

        // ==================== READ OPERATIONS ====================

        // Get specific mapping by ID
        GroupPushMapping retrievedMapping1 = groupPushMappingApi.getGroupPushMapping(app.getId(), mapping1Id)

        assertThat("Retrieved mapping should not be null", retrievedMapping1, notNullValue())
        assertThat("Mapping ID should match", retrievedMapping1.getId(), is(mapping1Id))
        assertThat("Source group ID should match", retrievedMapping1.getSourceGroupId(), is(sourceGroup1.getId()))
        assertThat("Target group ID should match", retrievedMapping1.getTargetGroupId(), is(targetGroupId1))
        assertThat("Status should be ACTIVE", retrievedMapping1.getStatus(), is(GroupPushMappingStatus.ACTIVE))
        assertThat("Created timestamp should match", retrievedMapping1.getCreated(), is(createdMapping1.getCreated()))
        assertThat("Links structure should be complete", retrievedMapping1.getLinks(), notNullValue())
        assertThat("App link should contain app ID", retrievedMapping1.getLinks().getApp().getHref(), containsString(app.getId()))
        assertThat("Source group link should contain source group ID", 
            retrievedMapping1.getLinks().getSourceGroup().getHref(), containsString(sourceGroup1.getId()))
        assertThat("Target group link should contain target group ID", 
            retrievedMapping1.getLinks().getTargetGroup().getHref(), containsString(targetGroupId1))

        // List all mappings for application
        List<GroupPushMapping> allMappings = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, null, null, null)

        assertThat("Mappings list should not be null", allMappings, notNullValue())
        assertThat("Should have at least 3 created mappings", allMappings.size(), greaterThanOrEqualTo(3))

        List<String> mappingIds = allMappings.collect { it.getId() }
        assertThat("List should contain mapping 1", mappingIds, hasItem(mapping1Id))
        assertThat("List should contain mapping 2", mappingIds, hasItem(mapping2Id))
        assertThat("List should contain mapping 3", mappingIds, hasItem(mapping3Id))

        // Verify all mappings have complete data structure
        for (GroupPushMapping mapping : allMappings) {
            assertThat("Mapping ID should not be empty", mapping.getId(), not(isEmptyOrNullString()))
            assertThat("Mapping ID should start with gPm", mapping.getId(), startsWith("gPm"))
            assertThat("Source group ID should not be empty", mapping.getSourceGroupId(), not(isEmptyOrNullString()))
            assertThat("Target group ID should not be empty", mapping.getTargetGroupId(), not(isEmptyOrNullString()))
            assertThat("Status should not be null", mapping.getStatus(), notNullValue())
            assertThat("Created timestamp should not be null", mapping.getCreated(), notNullValue())
            assertThat("LastUpdated timestamp should not be null", mapping.getLastUpdated(), notNullValue())
            assertThat("Links should not be null", mapping.getLinks(), notNullValue())
            assertThat("App link should not be null", mapping.getLinks().getApp(), notNullValue())
            assertThat("Source group link should not be null", mapping.getLinks().getSourceGroup(), notNullValue())
            assertThat("Target group link should not be null", mapping.getLinks().getTargetGroup(), notNullValue())
        }

        // List with filtering by sourceGroupId
        // Parameter order: appId, after, limit, lastUpdated, sourceGroupId, status
        List<GroupPushMapping> filteredBySource = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, null, sourceGroup1.getId(), null)

        assertThat("Filtered list should not be null", filteredBySource, notNullValue())
        assertThat("Filtered list should contain mapping 1", 
            filteredBySource.find { it.getId() == mapping1Id }, notNullValue())

        for (GroupPushMapping mapping : filteredBySource) {
            assertThat("All filtered mappings should have matching source group", 
                mapping.getSourceGroupId(), is(sourceGroup1.getId()))
        }

        // List with filtering by status (ACTIVE)
        // Parameter order: appId, after, limit, lastUpdated, sourceGroupId, status
        List<GroupPushMapping> activeOnly = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, null, null, GroupPushMappingStatus.ACTIVE)

        assertThat("Active mappings list should not be null", activeOnly, notNullValue())
        List<String> activeIds = activeOnly.collect { it.getId() }
        assertThat("Active list should contain mapping 1", activeIds, hasItem(mapping1Id))
        assertThat("Active list should contain mapping 3", activeIds, hasItem(mapping3Id))

        for (GroupPushMapping mapping : activeOnly) {
            assertThat("All mappings should be ACTIVE", mapping.getStatus(), is(GroupPushMappingStatus.ACTIVE))
        }

        // List with filtering by status (INACTIVE)
        // Parameter order: appId, after, limit, lastUpdated, sourceGroupId, status
        List<GroupPushMapping> inactiveOnly = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, null, null, GroupPushMappingStatus.INACTIVE)

        assertThat("Inactive mappings list should not be null", inactiveOnly, notNullValue())
        List<String> inactiveIds = inactiveOnly.collect { it.getId() }
        assertThat("Inactive list should contain mapping 2", inactiveIds, hasItem(mapping2Id))

        for (GroupPushMapping mapping : inactiveOnly) {
            assertThat("All mappings should be INACTIVE", mapping.getStatus(), is(GroupPushMappingStatus.INACTIVE))
        }

        // List with limit parameter
        // Parameter order: appId, after, limit, lastUpdated, sourceGroupId, status
        List<GroupPushMapping> limitedMappings = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, 2, null, null, null)

        assertThat("Limited mappings list should not be null", limitedMappings, notNullValue())
        assertThat("Limited list should respect limit", limitedMappings.size(), lessThanOrEqualTo(2))

        // List with lastUpdated filter
        // API expects ISO 8601 format with UTC timezone without microseconds: YYYY-MM-DDTHH:mm:ssZ
        OffsetDateTime yesterday = OffsetDateTime.now(java.time.ZoneOffset.UTC)
            .minus(1, ChronoUnit.DAYS)
            .truncatedTo(ChronoUnit.SECONDS) // Remove microseconds
        String lastUpdatedFilter = yesterday.format(java.time.format.DateTimeFormatter.ISO_INSTANT)
        // Parameter order: appId, after, limit, lastUpdated, sourceGroupId, status
        List<GroupPushMapping> recentMappings = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, lastUpdatedFilter, null, null)

        assertThat("Recent mappings list should not be null", recentMappings, notNullValue())
        List<String> recentIds = recentMappings.collect { it.getId() }
        assertThat("Recent list should contain mapping 1", recentIds, hasItem(mapping1Id))
        assertThat("Recent list should contain mapping 2", recentIds, hasItem(mapping2Id))

        // ==================== UPDATE OPERATIONS ====================

        // Update mapping 1: Change status from ACTIVE to INACTIVE
        UpdateGroupPushMappingRequest updateRequest1 = new UpdateGroupPushMappingRequest()
        updateRequest1.status(GroupPushMappingStatusUpsert.INACTIVE)

        GroupPushMapping updatedMapping1 = groupPushMappingApi.updateGroupPushMapping(
            app.getId(), mapping1Id, updateRequest1)

        assertThat("Updated mapping should not be null", updatedMapping1, notNullValue())
        assertThat("Mapping ID should match", updatedMapping1.getId(), is(mapping1Id))
        assertThat("Status should be INACTIVE", updatedMapping1.getStatus(), is(GroupPushMappingStatus.INACTIVE))
        assertThat("LastUpdated should be newer", 
            updatedMapping1.getLastUpdated().isAfter(createdMapping1.getLastUpdated()), is(true))

        // Verify other fields remain unchanged
        assertThat("Source group ID should not change", updatedMapping1.getSourceGroupId(), is(sourceGroup1.getId()))
        assertThat("Target group ID should not change", updatedMapping1.getTargetGroupId(), is(targetGroupId1))
        assertThat("Created timestamp should not change", updatedMapping1.getCreated(), is(createdMapping1.getCreated()))
        assertThat("Links should still be present", updatedMapping1.getLinks(), notNullValue())

        // Verify the status change persisted
        GroupPushMapping verifyInactive = groupPushMappingApi.getGroupPushMapping(app.getId(), mapping1Id)
        assertThat("Status change should persist", verifyInactive.getStatus(), is(GroupPushMappingStatus.INACTIVE))

        // Update mapping 2: Change status from INACTIVE to ACTIVE
        UpdateGroupPushMappingRequest updateRequest2 = new UpdateGroupPushMappingRequest()
        updateRequest2.status(GroupPushMappingStatusUpsert.ACTIVE)

        GroupPushMapping updatedMapping2 = groupPushMappingApi.updateGroupPushMapping(
            app.getId(), mapping2Id, updateRequest2)

        assertThat("Updated mapping should not be null", updatedMapping2, notNullValue())
        assertThat("Mapping ID should match", updatedMapping2.getId(), is(mapping2Id))
        assertThat("Status should be ACTIVE", updatedMapping2.getStatus(), is(GroupPushMappingStatus.ACTIVE))

        // Update mapping 3 to INACTIVE (for deletion test)
        UpdateGroupPushMappingRequest updateRequest3 = new UpdateGroupPushMappingRequest()
        updateRequest3.status(GroupPushMappingStatusUpsert.INACTIVE)

        GroupPushMapping updatedMapping3 = groupPushMappingApi.updateGroupPushMapping(
            app.getId(), mapping3Id, updateRequest3)

        assertThat("Updated mapping should not be null", updatedMapping3, notNullValue())
        assertThat("Status should be INACTIVE", updatedMapping3.getStatus(), is(GroupPushMappingStatus.INACTIVE))
        assertThat("LastUpdated should be newer", 
            updatedMapping3.getLastUpdated().isAfter(createdMapping3.getLastUpdated()), is(true))
        assertThat("Source group ID should not change", updatedMapping3.getSourceGroupId(), is(sourceGroup3.getId()))
        assertThat("Created timestamp should not change", updatedMapping3.getCreated(), is(createdMapping3.getCreated()))

        // ==================== DELETE OPERATIONS ====================

        // Delete mapping 1 without deleting target group (mapping is already INACTIVE)
        groupPushMappingApi.deleteGroupPushMapping(app.getId(), mapping1Id, false)
        unregisterMapping(mapping1Id)

        // Verify deletion - should throw 404
        ApiException deleteVerifyException1 = expect(ApiException.class, () -> 
            groupPushMappingApi.getGroupPushMapping(app.getId(), mapping1Id))

        assertThat("Deleted mapping should return 404", deleteVerifyException1.getCode(), is(404))

        // Delete mapping 3 without deleting target group (already INACTIVE)
        groupPushMappingApi.deleteGroupPushMapping(app.getId(), mapping3Id, false)
        unregisterMapping(mapping3Id)

        // Verify deletion
        ApiException deleteVerifyException3 = expect(ApiException.class, () -> 
            groupPushMappingApi.getGroupPushMapping(app.getId(), mapping3Id))

        assertThat("Deleted mapping should return 404", deleteVerifyException3.getCode(), is(404))

        // Deactivate mapping 2 before deletion
        groupPushMappingApi.updateGroupPushMapping(
            app.getId(), mapping2Id, 
            new UpdateGroupPushMappingRequest().status(GroupPushMappingStatusUpsert.INACTIVE))

        // Delete mapping 2 with target group deletion
        groupPushMappingApi.deleteGroupPushMapping(app.getId(), mapping2Id, true)
        unregisterMapping(mapping2Id)

        // Verify mapping deletion
        ApiException deleteVerifyException2 = expect(ApiException.class, () -> 
            groupPushMappingApi.getGroupPushMapping(app.getId(), mapping2Id))

        assertThat("Deleted mapping should return 404", deleteVerifyException2.getCode(), is(404))

        // Verify all mappings have been cleaned up
        List<GroupPushMapping> remainingMappings = groupPushMappingApi.listGroupPushMappings(
            app.getId(), null, null, null, null, null)

        List<String> remainingIds = remainingMappings.collect { it.getId() }
        assertThat("Mapping 1 should not remain", remainingIds, not(hasItem(mapping1Id)))
        assertThat("Mapping 2 should not remain", remainingIds, not(hasItem(mapping2Id)))
        assertThat("Mapping 3 should not remain", remainingIds, not(hasItem(mapping3Id)))

        logger.info("Comprehensive Group Push Mapping operations test completed successfully")
    }

    // ========================================
    // Test 2: Error Scenarios and Validation
    // ========================================
    @Test(groups = "group1")
    @Scenario("group-push-mapping-error-scenarios")
    void testGroupPushMappingErrorScenarios() {
        logger.info("Testing Group Push Mapping API error scenarios...")

        // ==================== SETUP ====================
        Application app = createTestApplication()
        
        // Check if Group Push Mapping API is available
        if (!isGroupPushMappingApiAvailable(app.getId())) {
            logger.info("Skipping test - Group Push Mapping API not available")
            return
        }
        
        Group sourceGroup = createTestGroup("ErrorTest")

        String nonExistentAppId = "0oa" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        String nonExistentMappingId = "gPm" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        String nonExistentGroupId = "00g" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)

        // ==================== ERROR SCENARIOS ====================

        // Error 1: Get non-existent mapping - 404
        ApiException getException = expect(ApiException.class, () -> 
            groupPushMappingApi.getGroupPushMapping(app.getId(), nonExistentMappingId))

        assertThat("Non-existent mapping should return 404", getException.getCode(), is(404))
        assertThat("Error should have message", getException.getMessage(), not(isEmptyOrNullString()))

        // Error 2: Create mapping with missing sourceGroupId - 400
        ApiException createException1 = expect(ApiException.class, () -> {
            CreateGroupPushMappingRequest invalidRequest = new CreateGroupPushMappingRequest()
            // Missing sourceGroupId
            invalidRequest.targetGroupName("Invalid-${uniqueTestName}")
            invalidRequest.status(GroupPushMappingStatusUpsert.ACTIVE)
            groupPushMappingApi.createGroupPushMapping(app.getId(), invalidRequest)
        })

        assertThat("Missing sourceGroupId should return 400", createException1.getCode(), is(400))
        assertThat("Error should have message", createException1.getMessage(), not(isEmptyOrNullString()))

        // Error 3: Create mapping with missing both targetGroupId and targetGroupName - 400
        ApiException createException2 = expect(ApiException.class, () -> {
            CreateGroupPushMappingRequest invalidRequest = new CreateGroupPushMappingRequest()
            invalidRequest.sourceGroupId(sourceGroup.getId())
            // Missing both targetGroupId and targetGroupName
            invalidRequest.status(GroupPushMappingStatusUpsert.ACTIVE)
            groupPushMappingApi.createGroupPushMapping(app.getId(), invalidRequest)
        })

        assertThat("Missing target should return 400", createException2.getCode(), is(400))
        assertThat("Error should have message", createException2.getMessage(), not(isEmptyOrNullString()))

        // Error 4: Create mapping for non-existent application - 404
        ApiException createException3 = expect(ApiException.class, () -> {
            CreateGroupPushMappingRequest validRequest = new CreateGroupPushMappingRequest()
            validRequest.sourceGroupId(sourceGroup.getId())
            validRequest.targetGroupName("Test-${uniqueTestName}")
            validRequest.status(GroupPushMappingStatusUpsert.ACTIVE)
            groupPushMappingApi.createGroupPushMapping(nonExistentAppId, validRequest)
        })

        assertThat("Non-existent app should return 404", createException3.getCode(), is(404))
        assertThat("Error should have message", createException3.getMessage(), not(isEmptyOrNullString()))

        // Error 5: Update non-existent mapping - 404
        ApiException updateException = expect(ApiException.class, () -> {
            UpdateGroupPushMappingRequest updateRequest = new UpdateGroupPushMappingRequest()
            updateRequest.status(GroupPushMappingStatusUpsert.INACTIVE)
            groupPushMappingApi.updateGroupPushMapping(app.getId(), nonExistentMappingId, updateRequest)
        })

        assertThat("Updating non-existent mapping should return 404", updateException.getCode(), is(404))
        assertThat("Error should have message", updateException.getMessage(), not(isEmptyOrNullString()))

        // Error 6: Delete non-existent mapping - 404
        ApiException deleteException = expect(ApiException.class, () -> 
            groupPushMappingApi.deleteGroupPushMapping(app.getId(), nonExistentMappingId, false))

        assertThat("Deleting non-existent mapping should return 404", deleteException.getCode(), is(404))
        assertThat("Error should have message", deleteException.getMessage(), not(isEmptyOrNullString()))

        // Error 7: Cannot delete ACTIVE mapping - 400
        CreateGroupPushMappingRequest createRequest = new CreateGroupPushMappingRequest()
        createRequest.sourceGroupId(sourceGroup.getId())
        createRequest.targetGroupName("DeleteActiveTest-${uniqueTestName}")
        createRequest.status(GroupPushMappingStatusUpsert.ACTIVE)

        GroupPushMapping createdMapping = groupPushMappingApi.createGroupPushMapping(app.getId(), createRequest)
        registerMapping(createdMapping.getId())

        try {
            // Try to delete while ACTIVE - should fail
            ApiException deleteActiveException = expect(ApiException.class, () -> 
                groupPushMappingApi.deleteGroupPushMapping(app.getId(), createdMapping.getId(), false))

            assertThat("Cannot delete ACTIVE mapping - should return 400", deleteActiveException.getCode(), is(400))
            assertThat("Error should have message", deleteActiveException.getMessage(), not(isEmptyOrNullString()))
        } finally {
            // Cleanup - deactivate then delete
            try {
                groupPushMappingApi.updateGroupPushMapping(
                    app.getId(), createdMapping.getId(),
                    new UpdateGroupPushMappingRequest().status(GroupPushMappingStatusUpsert.INACTIVE))
                groupPushMappingApi.deleteGroupPushMapping(app.getId(), createdMapping.getId(), false)
                unregisterMapping(createdMapping.getId())
            } catch (ApiException e) {
                logger.warn("Cleanup failed: {}", e.getMessage())
            }
        }

        // Error 8: Create mapping with invalid source group - 400 or 404
        ApiException invalidSourceException = expect(ApiException.class, () -> {
            CreateGroupPushMappingRequest invalidRequest = new CreateGroupPushMappingRequest()
            invalidRequest.sourceGroupId(nonExistentGroupId)
            invalidRequest.targetGroupName("InvalidSource-${uniqueTestName}")
            invalidRequest.status(GroupPushMappingStatusUpsert.ACTIVE)
            groupPushMappingApi.createGroupPushMapping(app.getId(), invalidRequest)
        })

        assertThat("Invalid source group should return 400 or 404", 
            invalidSourceException.getCode(), anyOf(is(400), is(404)))
        assertThat("Error should have message", invalidSourceException.getMessage(), not(isEmptyOrNullString()))

        // Error 9: List mappings for non-existent application - 404
        ApiException listException = expect(ApiException.class, () -> 
            groupPushMappingApi.listGroupPushMappings(nonExistentAppId, null, null, null, null, null))

        assertThat("Listing mappings for non-existent app should return 404", listException.getCode(), is(404))
        assertThat("Error should have message", listException.getMessage(), not(isEmptyOrNullString()))

        // Verify all errors have meaningful messages
        List<ApiException> allExceptions = [
            getException, createException1, createException2, createException3,
            updateException, deleteException, invalidSourceException, listException
        ]

        for (ApiException exception : allExceptions) {
            assertThat("All API errors should have meaningful messages", 
                exception.getMessage(), not(isEmptyOrNullString()))
        }

        logger.info("Error scenarios test completed successfully")
    }

    // ========================================
    // Test 3: Lifecycle Operations and State Transitions
    // ========================================
    @Test(groups = "group1")
    @Scenario("group-push-mapping-lifecycle")
    void testGroupPushMappingLifecycle() {
        logger.info("Testing Group Push Mapping lifecycle and state transitions...")

        // ==================== SETUP ====================
        Application app = createTestApplication()
        
        // Check if Group Push Mapping API is available
        if (!isGroupPushMappingApiAvailable(app.getId())) {
            logger.info("Skipping test - Group Push Mapping API not available")
            return
        }
        
        Group sourceGroup = createTestGroup("Lifecycle")

        // Create a mapping in ACTIVE state
        CreateGroupPushMappingRequest createRequest = new CreateGroupPushMappingRequest()
        createRequest.sourceGroupId(sourceGroup.getId())
        createRequest.targetGroupName("LifecycleTest-${uniqueTestName}")
        createRequest.status(GroupPushMappingStatusUpsert.ACTIVE)

        GroupPushMapping mapping = groupPushMappingApi.createGroupPushMapping(app.getId(), createRequest)
        registerMapping(mapping.getId())

        try {
            // Verify initial state
            assertThat("Initial status should be ACTIVE", mapping.getStatus(), is(GroupPushMappingStatus.ACTIVE))

            // Transition 1: ACTIVE → INACTIVE
            UpdateGroupPushMappingRequest deactivateRequest = new UpdateGroupPushMappingRequest()
            deactivateRequest.status(GroupPushMappingStatusUpsert.INACTIVE)

            GroupPushMapping deactivatedMapping = groupPushMappingApi.updateGroupPushMapping(
                app.getId(), mapping.getId(), deactivateRequest)

            assertThat("Status should transition to INACTIVE", 
                deactivatedMapping.getStatus(), is(GroupPushMappingStatus.INACTIVE))

            // Transition 2: INACTIVE → ACTIVE
            UpdateGroupPushMappingRequest reactivateRequest = new UpdateGroupPushMappingRequest()
            reactivateRequest.status(GroupPushMappingStatusUpsert.ACTIVE)

            GroupPushMapping reactivatedMapping = groupPushMappingApi.updateGroupPushMapping(
                app.getId(), mapping.getId(), reactivateRequest)

            assertThat("Status should transition back to ACTIVE", 
                reactivatedMapping.getStatus(), is(GroupPushMappingStatus.ACTIVE))

            // Final transition to INACTIVE before deletion
            groupPushMappingApi.updateGroupPushMapping(app.getId(), mapping.getId(), deactivateRequest)

            // Now delete should succeed
            groupPushMappingApi.deleteGroupPushMapping(app.getId(), mapping.getId(), false)
            unregisterMapping(mapping.getId())

            // Verify deletion
            ApiException verifyException = expect(ApiException.class, () -> 
                groupPushMappingApi.getGroupPushMapping(app.getId(), mapping.getId()))

            assertThat("Deleted mapping should return 404", verifyException.getCode(), is(404))

            logger.info("Lifecycle test completed successfully")

        } catch (Exception e) {
            // Cleanup on failure
            try {
                groupPushMappingApi.updateGroupPushMapping(
                    app.getId(), mapping.getId(),
                    new UpdateGroupPushMappingRequest().status(GroupPushMappingStatusUpsert.INACTIVE))
                groupPushMappingApi.deleteGroupPushMapping(app.getId(), mapping.getId(), false)
                unregisterMapping(mapping.getId())
            } catch (ApiException cleanupException) {
                logger.warn("Cleanup failed: {}", cleanupException.getMessage())
            }
            throw e
        }
    }
}

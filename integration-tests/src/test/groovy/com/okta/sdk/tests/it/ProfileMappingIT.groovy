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
import com.okta.sdk.resource.api.ProfileMappingApi
import com.okta.sdk.resource.application.OIDCApplicationBuilder
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for {@code /api/v1/mappings} - Profile Mapping API.
 *
 * <p>These tests cover the complete lifecycle of Profile Mapping API operations including:
 * <ul>
 *   <li>Listing all profile mappings with pagination and filtering</li>
 *   <li>Retrieving specific profile mappings by ID</li>
 *   <li>Updating profile mapping properties and expressions</li>
 *   <li>Filtering by sourceId and targetId</li>
 *   <li>Pagination with after cursor and limit</li>
 *   <li>Error handling and validation</li>
 *   <li>Response structure validation</li>
 * </ul>
 *
 * <p>REST Endpoints tested:
 * <ul>
 *   <li>GET    /api/v1/mappings - List profile mappings</li>
 *   <li>GET    /api/v1/mappings/{mappingId} - Get profile mapping</li>
 *   <li>POST   /api/v1/mappings/{mappingId} - Update profile mapping</li>
 * </ul>
 *
 * <p><strong>Note:</strong> Profile mappings are automatically created by Okta when applications
 * are created. This test creates an OIDC application to generate profile mappings for testing.
 */
class ProfileMappingIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ProfileMappingIT.class)

    private ApplicationApi applicationApi = new ApplicationApi(getClient())
    private ProfileMappingApi profileMappingApi = new ProfileMappingApi(getClient())

    /**
     * Test 1: Comprehensive Profile Mapping Operations
     * Covers all basic CRUD operations and list/filter functionality
     */
    @Test(groups = "group1")
    @Scenario("comprehensive-profile-mapping-operations")
    void testComprehensiveProfileMappingOperations() {
        logger.info("Testing comprehensive Profile Mapping API operations...")

        String uniqueTestId = UUID.randomUUID().toString()
        
        // ==================== SETUP: Create OIDC Application ====================
        // OIDC applications automatically have profile mappings created
        String label = "SDK Test ProfileMapping ${uniqueTestId}"
        
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.label(label)
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
        
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        settingsClient.redirectUris(["https://example.com/oauth2/callback"])
        settingsClient.responseTypes([OAuthResponseType.TOKEN, OAuthResponseType.CODE])
        settingsClient.grantTypes([GrantType.IMPLICIT, GrantType.AUTHORIZATION_CODE])
        
        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        oidcApp.settings(settings)
        
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId(UUID.randomUUID().toString())
        oauthClient.autoKeyRotation(true)
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
        
        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        oidcApp.credentials(credentials)
        oidcApp.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        Application app = applicationApi.createApplication(oidcApp, true, null)
        registerForCleanup(app)
        
        assertThat("Application should be created", app, notNullValue())
        assertThat("Application should have ID", app.getId(), notNullValue())
        
        logger.info("Created test application: ${app.getId()}")
        
        // Wait for profile mappings to be created
        Thread.sleep(5000)
        
        // ==================== LIST PROFILE MAPPINGS WITH TARGET FILTER ====================
        // Start with filtered list to avoid org-wide issues
        logger.info("Testing LIST with targetId filter...")
        
        List<ListProfileMappings> targetFilteredMappings = profileMappingApi.listProfileMappings(
            null, null, null, app.getId())
        
        assertThat("Target filtered mappings should not be null", targetFilteredMappings, notNullValue())
        assertThat("Should find mappings for test application", targetFilteredMappings, not(empty()))
        assertThat("All mappings should have matching targetId", 
            targetFilteredMappings.every { it.getTarget()?.getId() == app.getId() }, is(true))
        
        logger.info("Found ${targetFilteredMappings.size()} mappings with targetId=${app.getId()}")
        
        // Get the first mapping for detailed testing
        def testMapping = targetFilteredMappings.first()
        String testMappingId = testMapping.getId()
        String sourceId = testMapping.getSource()?.getId()
        
        assertThat("Test mapping ID should not be null", testMappingId, notNullValue())
        assertThat("Test mapping should have source", sourceId, notNullValue())
        
        // ==================== LIST WITH SOURCE ID FILTER ====================
        logger.info("Testing LIST with sourceId filter...")
        
        List<ListProfileMappings> sourceFilteredMappings = profileMappingApi.listProfileMappings(
            null, null, sourceId, null)
        
        assertThat("Source filtered mappings should not be null", sourceFilteredMappings, notNullValue())
        assertThat("Should find mappings for source", sourceFilteredMappings, not(empty()))
        assertThat("All mappings should have matching sourceId",
            sourceFilteredMappings.every { it.getSource()?.getId() == sourceId }, is(true))
        
        logger.info("Found ${sourceFilteredMappings.size()} mappings with sourceId=${sourceId}")
        
        // ==================== LIST WITH BOTH SOURCE AND TARGET FILTERS ====================
        logger.info("Testing LIST with both sourceId and targetId filters...")
        
        List<ListProfileMappings> bothFilteredMappings = profileMappingApi.listProfileMappings(
            null, null, sourceId, app.getId())
        
        assertThat("Both filtered mappings should not be null", bothFilteredMappings, notNullValue())
        assertThat("Should find mapping matching both filters", bothFilteredMappings, not(empty()))
        assertThat("All mappings should match both sourceId and targetId",
            bothFilteredMappings.every { 
                it.getSource()?.getId() == sourceId && it.getTarget()?.getId() == app.getId() 
            }, is(true))
        
        logger.info("Found ${bothFilteredMappings.size()} mappings with both sourceId and targetId")
        
        // ==================== LIST WITH LIMIT ====================
        logger.info("Testing LIST with limit parameter...")
        
        List<ListProfileMappings> limitedMappings = profileMappingApi.listProfileMappings(null, 5, sourceId, null)
        
        assertThat("Limited mappings should not be null", limitedMappings, notNullValue())
        assertThat("Should have mappings", limitedMappings, not(empty()))
        
        logger.info("LIST with limit=5 returned ${limitedMappings.size()} mappings")
        
        // ==================== LIST WITH MAXIMUM LIMIT ====================
        logger.info("Testing LIST with maximum limit (200)...")
        
        List<ListProfileMappings> maxLimitMappings = profileMappingApi.listProfileMappings(null, 200, sourceId, null)
        
        assertThat("Max limit mappings should not be null", maxLimitMappings, notNullValue())
        
        logger.info("LIST with limit=200 returned ${maxLimitMappings.size()} mappings")
        
        // ==================== GET PROFILE MAPPING BY ID ====================
        logger.info("Testing GET profile mapping by ID...")
        
        ProfileMapping mapping = profileMappingApi.getProfileMapping(testMappingId)
        
        assertThat("Retrieved mapping should not be null", mapping, notNullValue())
        assertThat("Mapping ID should match", mapping.getId(), is(testMappingId))
        assertThat("Mapping should have source", mapping.getSource(), notNullValue())
        assertThat("Mapping should have target", mapping.getTarget(), notNullValue())
        assertThat("Mapping should have properties", mapping.getProperties(), notNullValue())
        
        // Verify source structure
        assertThat("Source should have ID", mapping.getSource().getId(), notNullValue())
        assertThat("Source should have type", mapping.getSource().getType(), notNullValue())
        assertThat("Source should have name", mapping.getSource().getName(), notNullValue())
        assertThat("Source should have links", mapping.getSource().getLinks(), notNullValue())
        
        // Verify target structure
        assertThat("Target should have ID", mapping.getTarget().getId(), is(app.getId()))
        assertThat("Target should have type", mapping.getTarget().getType(), notNullValue())
        assertThat("Target should have name", mapping.getTarget().getName(), notNullValue())
        assertThat("Target should have links", mapping.getTarget().getLinks(), notNullValue())
        
        // Verify links structure
        assertThat("Mapping should have links", mapping.getLinks(), notNullValue())
        
        logger.info("Successfully retrieved mapping ${testMappingId}")
        
        // ==================== UPDATE PROFILE MAPPING ====================
        logger.info("Testing UPDATE profile mapping...")
        
        // Find a property to update (look for common properties)
        Map<String, ProfileMappingProperty> currentProperties = mapping.getProperties()
        assertThat("Mapping should have properties to update", currentProperties, notNullValue())
        assertThat("Mapping should have at least one property", currentProperties, not(anEmptyMap()))
        
        // Find first property with expression
        String propertyKey = currentProperties.keySet().find { key ->
            currentProperties.get(key)?.getExpression() != null
        }
        
        if (propertyKey != null) {
            logger.info("Updating property: ${propertyKey}")
            
            ProfileMappingProperty originalProperty = currentProperties.get(propertyKey)
            String originalExpression = originalProperty.getExpression()
            def originalPushStatus = originalProperty.getPushStatus()
            
            // Create update request - modify push status
            ProfileMappingRequest updateRequest = new ProfileMappingRequest()
            Map<String, ProfileMappingProperty> updatedProperties = new HashMap<>(currentProperties)
            
            ProfileMappingProperty updatedProperty = new ProfileMappingProperty()
            updatedProperty.setExpression(originalExpression)
            updatedProperty.setPushStatus(ProfileMappingPropertyPushStatus.PUSH)
            
            updatedProperties.put(propertyKey, updatedProperty)
            updateRequest.setProperties(updatedProperties)
            
            ProfileMapping updatedMapping = profileMappingApi.updateProfileMapping(testMappingId, updateRequest)
            
            assertThat("Updated mapping should not be null", updatedMapping, notNullValue())
            assertThat("Mapping ID should not change", updatedMapping.getId(), is(testMappingId))
            assertThat("Updated mapping should have properties", updatedMapping.getProperties(), notNullValue())
            assertThat("Updated property should exist", 
                updatedMapping.getProperties().containsKey(propertyKey), is(true))
            
            logger.info("Successfully updated mapping ${testMappingId}")
            
            // Restore original push status
            updatedProperty.setPushStatus(originalPushStatus ?: ProfileMappingPropertyPushStatus.PUSH)
            updatedProperties.put(propertyKey, updatedProperty)
            updateRequest.setProperties(updatedProperties)
            
            profileMappingApi.updateProfileMapping(testMappingId, updateRequest)
            logger.info("Restored original property values")
            
        } else {
            logger.info("No suitable property found for update test - performing no-op update")
            
            // Perform no-op update to test SDK serialization
            ProfileMappingRequest updateRequest = new ProfileMappingRequest()
            updateRequest.setProperties(currentProperties)
            
            ProfileMapping updatedMapping = profileMappingApi.updateProfileMapping(testMappingId, updateRequest)
            
            assertThat("No-op update should succeed", updatedMapping, notNullValue())
            assertThat("Mapping ID should not change", updatedMapping.getId(), is(testMappingId))
        }
        
        // ==================== PAGINATION WITH AFTER CURSOR ====================
        logger.info("Testing pagination with 'after' cursor...")
        
        List<ListProfileMappings> firstPage = profileMappingApi.listProfileMappings(null, 2, sourceId, null)
        assertThat("First page should not be null", firstPage, notNullValue())
        
        if (firstPage.size() >= 2) {
            String lastIdFromFirstPage = firstPage.last().getId()
            logger.info("First page last ID: ${lastIdFromFirstPage}")
            
            List<ListProfileMappings> secondPage = profileMappingApi.listProfileMappings(
                lastIdFromFirstPage, 2, sourceId, null)
            
            assertThat("Second page should not be null", secondPage, notNullValue())
            
            if (!secondPage.isEmpty()) {
                // Verify second page doesn't contain items from first page
                List<String> firstPageIds = firstPage.collect { it.getId() }
                List<String> secondPageIds = secondPage.collect { it.getId() }
                
                boolean hasOverlap = secondPageIds.any { firstPageIds.contains(it) }
                assertThat("Second page should not overlap with first page", hasOverlap, is(false))
                
                logger.info("Pagination working correctly - no overlap between pages")
            }
        }
        
        logger.info("Comprehensive Profile Mapping operations test completed successfully")
    }

    /**
     * Test 2: Error Scenarios and Validation
     * Tests error handling for invalid inputs
     */
    @Test(groups = "group1")
    @Scenario("profile-mapping-error-scenarios")
    void testProfileMappingErrorScenarios() {
        logger.info("Testing Profile Mapping API error scenarios...")
        
        // Error 1: Get non-existent mapping - 404
        String nonExistentId = "prm" + UUID.randomUUID().toString().replace("-", "").substring(0, 20)
        
        ApiException getException = expect(ApiException.class, {
            profileMappingApi.getProfileMapping(nonExistentId)
        })
        
        assertThat("Non-existent mapping should return 404", getException.getCode(), is(404))
        assertThat("Error should have message", getException.getMessage(), not(isEmptyOrNullString()))
        assertThat("Error message should contain 'not found'", 
            getException.getMessage().toLowerCase(), containsString("not found"))
        
        logger.info("Error 1 verified: GET non-existent mapping returns 404")
        
        // Error 2: Get mapping with invalid ID format - 400 or 404
        ApiException invalidException = expect(ApiException.class, {
            profileMappingApi.getProfileMapping("invalid-id")
        })
        
        assertThat("Invalid mapping ID should return error", invalidException.getCode(), isOneOf(400, 404))
        assertThat("Error should have message", invalidException.getMessage(), not(isEmptyOrNullString()))
        
        logger.info("Error 2 verified: GET with invalid ID format returns ${invalidException.getCode()}")
        
        // Error 3: Get mapping with null ID - SDK validation error
        ApiException nullException = expect(ApiException.class, {
            profileMappingApi.getProfileMapping(null)
        })
        
        assertThat("Null mappingId should return 400", nullException.getCode(), is(400))
        assertThat("Error should indicate missing parameter", 
            nullException.getMessage(), containsString("required parameter"))
        
        logger.info("Error 3 verified: GET with null ID returns 400 with validation message")
        
        // Error 4: Update non-existent mapping - 404
        ProfileMappingRequest updateRequest = new ProfileMappingRequest()
        updateRequest.setProperties(new HashMap<>())
        
        ApiException updateException = expect(ApiException.class, {
            profileMappingApi.updateProfileMapping(nonExistentId, updateRequest)
        })
        
        assertThat("Update non-existent mapping should return 404", updateException.getCode(), is(404))
        assertThat("Error should have message", updateException.getMessage(), not(isEmptyOrNullString()))
        
        logger.info("Error 4 verified: UPDATE non-existent mapping returns 404")
        
        // Error 5: List with non-existent sourceId - returns empty or 404
        String nonExistentSourceId = "otysbe" + UUID.randomUUID().toString().replace("-", "").substring(0, 15)
        
        try {
            List<ListProfileMappings> result = profileMappingApi.listProfileMappings(
                null, null, nonExistentSourceId, null)
            
            // Some orgs may return empty list, others may return 404
            assertThat("Result should not be null", result, notNullValue())
            logger.info("Error 5 verified: LIST with non-existent sourceId returns empty list or 404")
            
        } catch (ApiException e) {
            assertThat("Non-existent sourceId should return 404 or 400", 
                e.getCode(), isOneOf(400, 404))
            logger.info("Error 5 verified: LIST with non-existent sourceId returns ${e.getCode()}")
        }
        
        logger.info("Error scenarios test completed successfully")
    }

    /**
     * Test 3: Response Structure Validation
     * Validates that API responses have all required fields per documentation
     */
    @Test(groups = "group1")
    @Scenario("profile-mapping-response-structure")
    void testProfileMappingResponseStructure() {
        logger.info("Testing Profile Mapping response structure validation...")
        
        String uniqueTestId = UUID.randomUUID().toString()
        
        // Create test application to generate profile mappings
        String label = "SDK Test Structure ${uniqueTestId}"
        
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.label(label)
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
        
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        settingsClient.redirectUris(["https://example.com/oauth2/callback"])
        settingsClient.responseTypes([OAuthResponseType.CODE])
        settingsClient.grantTypes([GrantType.AUTHORIZATION_CODE])
        
        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        oidcApp.settings(settings)
        
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId(UUID.randomUUID().toString())
        oauthClient.autoKeyRotation(true)
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
        
        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        oidcApp.credentials(credentials)
        oidcApp.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        Application app = applicationApi.createApplication(oidcApp, true, null)
        registerForCleanup(app)
        
        // Wait for mappings to be created
        Thread.sleep(5000)
        
        // Get mapping for the application
        List<ListProfileMappings> mappings = profileMappingApi.listProfileMappings(
            null, null, null, app.getId())
        
        assertThat("Should have at least one mapping", mappings, not(empty()))
        
        String mappingId = mappings.first().getId()
        ProfileMapping mapping = profileMappingApi.getProfileMapping(mappingId)
        
        // ==================== VALIDATE REQUIRED FIELDS ====================
        
        assertThat("Mapping must have ID", mapping.getId(), notNullValue())
        assertThat("Mapping ID should match regex", mapping.getId(), matchesRegex("^prm[a-zA-Z0-9]+\$"))
        
        // Verify source structure (required per API docs)
        ProfileMappingSource source = mapping.getSource()
        assertThat("Mapping must have source", source, notNullValue())
        assertThat("Source must have ID", source.getId(), notNullValue())
        assertThat("Source must have name", source.getName(), notNullValue())
        assertThat("Source must have type", source.getType(), notNullValue())
        assertThat("Source type must be valid", source.getType(), 
            isOneOf("user", "appuser", "group", "client_credentials"))
        assertThat("Source must have links", source.getLinks(), notNullValue())
        
        // Verify target structure (required per API docs)
        ProfileMappingTarget target = mapping.getTarget()
        assertThat("Mapping must have target", target, notNullValue())
        assertThat("Target must have ID", target.getId(), is(app.getId()))
        assertThat("Target must have name", target.getName(), notNullValue())
        assertThat("Target must have type", target.getType(), notNullValue())
        assertThat("Target type must be valid", target.getType(),
            isOneOf("user", "appuser", "group", "client_credentials"))
        assertThat("Target must have links", target.getLinks(), notNullValue())
        
        // Verify properties structure
        assertThat("Mapping should have properties", mapping.getProperties(), notNullValue())
        
        // Verify _links structure (required per API docs)
        def mappingLinks = mapping.getLinks()
        assertThat("Mapping must have _links", mappingLinks, notNullValue())
        
        logger.info("Response structure validation completed successfully")
    }

    /**
     * Test 4: Combined Query Parameters
     * Tests using all query parameters together
     */
    @Test(groups = "group1")
    @Scenario("profile-mapping-combined-parameters")
    void testCombinedQueryParameters() {
        logger.info("Testing combined query parameters...")
        
        String uniqueTestId = UUID.randomUUID().toString()
        
        // Create test application
        String label = "SDK Test Combined ${uniqueTestId}"
        
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.label(label)
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
        
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        settingsClient.redirectUris(["https://example.com/oauth2/callback"])
        settingsClient.responseTypes([OAuthResponseType.CODE])
        settingsClient.grantTypes([GrantType.AUTHORIZATION_CODE])
        
        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        oidcApp.settings(settings)
        
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId(UUID.randomUUID().toString())
        oauthClient.autoKeyRotation(true)
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
        
        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        oidcApp.credentials(credentials)
        oidcApp.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        Application app = applicationApi.createApplication(oidcApp, true, null)
        registerForCleanup(app)
        
        // Wait for mappings
        Thread.sleep(5000)
        
        // Get mapping details
        List<ListProfileMappings> targetMappings = profileMappingApi.listProfileMappings(
            null, null, null, app.getId())
        
        assertThat("Should have mappings for app", targetMappings, not(empty()))
        
        String sourceId = targetMappings.first().getSource()?.getId()
        assertThat("Should have source ID", sourceId, notNullValue())
        
        // Test all parameters combined: sourceId + targetId + limit
        List<ListProfileMappings> combinedResults = profileMappingApi.listProfileMappings(
            null, 1, sourceId, app.getId())
        
        assertThat("Combined query should return results", combinedResults, notNullValue())
        assertThat("Should match filters", combinedResults, not(empty()))
        assertThat("Should match sourceId", combinedResults.first().getSource()?.getId(), is(sourceId))
        assertThat("Should match targetId", combinedResults.first().getTarget()?.getId(), is(app.getId()))
        
        // Test all parameters including after cursor
        if (combinedResults.size() > 0) {
            String afterCursor = combinedResults.first().getId()
            
            List<ListProfileMappings> paginatedResults = profileMappingApi.listProfileMappings(
                afterCursor, 1, sourceId, app.getId())
            
            assertThat("Paginated results should not be null", paginatedResults, notNullValue())
            
            if (!paginatedResults.isEmpty()) {
                assertThat("Paginated result should not include cursor ID",
                    paginatedResults.first().getId(), not(is(afterCursor)))
            }
        }
        
        logger.info("Combined query parameters test completed successfully")
    }

    /**
     * Test 5: Edge Cases
     * Tests boundary conditions and edge cases
     */
    @Test(groups = "group1")
    @Scenario("profile-mapping-edge-cases")
    void testEdgeCases() {
        logger.info("Testing Profile Mapping API edge cases...")
        
        // Create a test app to get valid mappings to work with
        String uniqueTestId = UUID.randomUUID().toString()
        String label = "SDK Test EdgeCase ${uniqueTestId}"
        
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.label(label)
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
        
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        settingsClient.redirectUris(["https://example.com/oauth2/callback"])
        settingsClient.responseTypes([OAuthResponseType.CODE])
        settingsClient.grantTypes([GrantType.AUTHORIZATION_CODE])
        
        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        oidcApp.settings(settings)
        
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId(UUID.randomUUID().toString())
        oauthClient.autoKeyRotation(true)
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
        
        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        oidcApp.credentials(credentials)
        oidcApp.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        Application app = applicationApi.createApplication(oidcApp, true, null)
        registerForCleanup(app)
        
        Thread.sleep(3000)
        
        // Get valid mappings for this app
        List<ListProfileMappings> appMappings = profileMappingApi.listProfileMappings(
            null, null, null, app.getId())
        
        assertThat("Should have mappings for test app", appMappings, not(empty()))
        
        String sourceId = appMappings.first().getSource()?.getId()
        assertThat("Should have sourceId", sourceId, notNullValue())
        
        // Edge case 1: List with very high limit (API max is 200) - use sourceId filter to avoid CVDType
        List<ListProfileMappings> maxLimitMappings = profileMappingApi.listProfileMappings(
            null, 200, sourceId, null)
        
        assertThat("API should handle maximum limit", maxLimitMappings, notNullValue())
        
        logger.info("Edge case 1: Maximum limit (200) returned ${maxLimitMappings.size()} mappings")
        
        // Edge case 2: List with minimum limit - use sourceId filter
        List<ListProfileMappings> minLimitMappings = profileMappingApi.listProfileMappings(
            null, 1, sourceId, null)
        
        assertThat("API should handle minimum limit", minLimitMappings, notNullValue())
        
        logger.info("Edge case 2: Minimum limit (1) returned ${minLimitMappings.size()} mappings")
        
        // Edge case 3: Verify mapping consistency between Get and List
        if (!minLimitMappings.isEmpty()) {
            String mappingId = minLimitMappings.first().getId()
            ProfileMapping specificMapping = profileMappingApi.getProfileMapping(mappingId)
            
            boolean foundInList = maxLimitMappings.any { it.getId() == mappingId }
            assertThat("Mapping from GET should exist in LIST results", foundInList, is(true))
            
            def listMapping = maxLimitMappings.find { it.getId() == mappingId }
            assertThat("Source ID should be consistent", 
                listMapping.getSource()?.getId(), is(specificMapping.getSource()?.getId()))
            assertThat("Target ID should be consistent",
                listMapping.getTarget()?.getId(), is(specificMapping.getTarget()?.getId()))
            
            logger.info("Edge case 3: Mapping consistency verified between GET and LIST")
        }
        
        logger.info("Edge cases test completed successfully")
    }
}

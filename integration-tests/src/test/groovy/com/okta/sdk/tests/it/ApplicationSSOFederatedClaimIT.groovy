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

import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationSsoFederatedClaimsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for ApplicationSsoFederatedClaimApi.
 * Federated claims are included in tokens produced by federation protocols (OIDC id_tokens, SAML Assertions).
 * These tests verify CRUD operations for managing federated claims in SSO applications.
 */
class ApplicationSSOFederatedClaimIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationSSOFederatedClaimIT.class)
    
    private static ApplicationApi applicationApi
    private static ApplicationSsoFederatedClaimsApi federatedClaimApi
    private static String testAppId
    private static String targetAppId

    @BeforeClass
    void setup() {
        applicationApi = new ApplicationApi(getClient())
        federatedClaimApi = new ApplicationSsoFederatedClaimsApi(getClient())

        // Create test OIDC application
        testAppId = createTestOidcApp()
        targetAppId = createTestOidcApp()

        logger.info("Setup complete - Test App ID: ${testAppId}, Target App ID: ${targetAppId}")
    }

    @AfterClass
    void cleanup() {
        try {
            if (testAppId) {
                applicationApi.deactivateApplication(testAppId)
                applicationApi.deleteApplication(testAppId)
            }
        } catch (Exception e) {
            logger.warn("Failed to delete test app: ${e.message}")
        }

        try {
            if (targetAppId) {
                applicationApi.deactivateApplication(targetAppId)
                applicationApi.deleteApplication(targetAppId)
            }
        } catch (Exception e) {
            logger.warn("Failed to delete target app: ${e.message}")
        }

        logger.info("Cleanup complete")
    }

    private String createTestOidcApp() {
        def app = new OpenIdConnectApplication()
            .name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label("Test Federated Claims App ${UUID.randomUUID()}")
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
            .credentials(new OAuthApplicationCredentials()
                .oauthClient(new ApplicationCredentialsOAuthClient()
                    .clientId(null)
                    .autoKeyRotation(true)
                    .tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)))
            .settings(new OpenIdConnectApplicationSettings()
                .oauthClient(new OpenIdConnectApplicationSettingsClient()
                    .clientUri("https://example.com")
                    .responseTypes([OAuthResponseType.CODE])
                    .grantTypes([GrantType.AUTHORIZATION_CODE])
                    .applicationType(OpenIdConnectApplicationType.WEB)
                    .redirectUris(["https://example.com/callback"])))

        def createdApp = applicationApi.createApplication(app, null, null)
        registerForCleanup(createdApp)
        return createdApp.getId()
    }

    @Test
    void testComprehensiveFederatedClaimOperations() {
        logger.info("Starting comprehensive federated claim operations test...")

        // Clean up any existing claims first to ensure clean state
        cleanupAllClaims(testAppId)

        // ==================== CREATE OPERATIONS ====================
        logger.info("Test 1: Create Federated Claim")

        def claimRequest = new FederatedClaimRequestBody()
            .name("department")
            .expression("user.profile.department")

        def createdClaim = federatedClaimApi.createFederatedClaim(testAppId, claimRequest)
        assertThat("Created claim should not be null", createdClaim, notNullValue())
        assertThat("Claim ID should not be empty", createdClaim.getId(), not(emptyString()))
        assertThat("Claim ID should start with 'ofc'", createdClaim.getId(), startsWith("ofc"))
        assertThat("Claim name should match", createdClaim.getName(), equalTo("department"))
        assertThat("Claim expression should match", createdClaim.getExpression(), equalTo("user.profile.department"))
        assertThat("Created timestamp should not be empty", createdClaim.getCreated(), not(emptyString()))
        assertThat("LastUpdated timestamp should not be empty", createdClaim.getLastUpdated(), not(emptyString()))

        def claimId = createdClaim.getId()
        logger.info("Created federated claim with ID: ${claimId}")

        // Create second claim for testing
        logger.info("Test 2: Create Additional Federated Claim")

        def claimRequest2 = new FederatedClaimRequestBody()
            .name("userRole")
            .expression("user.profile.title")

        def createdClaim2 = federatedClaimApi.createFederatedClaim(testAppId, claimRequest2)
        assertThat("Second claim should not be null", createdClaim2, notNullValue())
        assertThat("Second claim name should match", createdClaim2.getName(), equalTo("userRole"))
        assertThat("Second claim expression should match", createdClaim2.getExpression(), equalTo("user.profile.title"))

        def claimId2 = createdClaim2.getId()
        logger.info("Created second federated claim with ID: ${claimId2}")

        // ==================== GET OPERATIONS ====================
        logger.info("Test 3: Get Federated Claim")

        // NOTE: The API returns FederatedClaimRequestBody (only name and expression)
        // not FederatedClaim (which includes id, created, lastUpdated)
        def retrievedClaim = federatedClaimApi.getFederatedClaim(testAppId, claimId)
        assertThat("Retrieved claim should not be null", retrievedClaim, notNullValue())
        assertThat("Retrieved claim name should match", retrievedClaim.getName(), equalTo("department"))
        assertThat("Retrieved claim expression should match", retrievedClaim.getExpression(), equalTo("user.profile.department"))

        logger.info("Successfully retrieved federated claim")

        // ==================== LIST OPERATIONS ====================
        logger.info("Test 4: List Federated Claims")

        def claimsList = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Claims list should not be null", claimsList, notNullValue())
        assertThat("Claims list should have at least 2 items", claimsList.size(), greaterThanOrEqualTo(2))
        
        def foundDepartment = claimsList.find { it.getName() == "department" }
        def foundUserRole = claimsList.find { it.getName() == "userRole" }
        assertThat("Should find 'department' claim", foundDepartment, notNullValue())
        assertThat("Should find 'userRole' claim", foundUserRole, notNullValue())

        logger.info("Found ${claimsList.size()} federated claims")

        // ==================== REPLACE OPERATIONS ====================
        logger.info("Test 5: Replace Federated Claim")

        def replaceClaim = new FederatedClaim()
            .name("division")
            .expression("user.profile.division")

        def replacedClaim = federatedClaimApi.replaceFederatedClaim(testAppId, claimId, replaceClaim)
        assertThat("Replaced claim should not be null", replacedClaim, notNullValue())
        assertThat("Claim ID should remain the same", replacedClaim.getId(), equalTo(claimId))
        assertThat("Claim name should be updated", replacedClaim.getName(), equalTo("division"))
        assertThat("Claim expression should be updated", replacedClaim.getExpression(), equalTo("user.profile.division"))
        assertThat("LastUpdated should not be empty", replacedClaim.getLastUpdated(), not(emptyString()))

        logger.info("Successfully replaced federated claim")

        // Replace second claim
        logger.info("Test 6: Replace Second Federated Claim")

        // Wait to ensure timestamp difference between create and replace operations
        Thread.sleep(1000)

        def replaceClaim2 = new FederatedClaim()
            .name("manager")
            .expression("user.profile.managerId")

        def replacedClaim2 = federatedClaimApi.replaceFederatedClaim(testAppId, claimId2, replaceClaim2)
        assertThat("Second replaced claim should not be null", replacedClaim2, notNullValue())
        assertThat("Second claim ID should remain unchanged", replacedClaim2.getId(), equalTo(claimId2))
        assertThat("Second claim name should be updated", replacedClaim2.getName(), equalTo("manager"))
        assertThat("Second claim created timestamp should remain the same", replacedClaim2.getCreated(), equalTo(createdClaim2.getCreated()))
        assertThat("Second claim lastUpdated should differ from original", replacedClaim2.getLastUpdated(), not(equalTo(createdClaim2.getLastUpdated())))

        logger.info("Successfully replaced second federated claim")

        // ==================== DELETE OPERATIONS ====================
        logger.info("Test 7: Delete Federated Claim")

        federatedClaimApi.deleteFederatedClaim(testAppId, claimId)
        logger.info("Deleted federated claim: ${claimId}")

        // Verify deletion - should throw 404
        try {
            federatedClaimApi.getFederatedClaim(testAppId, claimId)
            throw new AssertionError("Expected 404 exception when getting deleted claim")
        } catch (ApiException e) {
            assertThat("Should throw 404 for deleted claim", e.getCode(), equalTo(404))
        }

        logger.info("Verified claim deletion (404 error as expected)")

        // Delete second claim
        logger.info("Test 8: Delete Second Federated Claim")

        federatedClaimApi.deleteFederatedClaim(testAppId, claimId2)
        logger.info("Deleted second federated claim: ${claimId2}")

        // Verify second deletion
        try {
            federatedClaimApi.getFederatedClaim(testAppId, claimId2)
            throw new AssertionError("Expected 404 exception when getting second deleted claim")
        } catch (ApiException e) {
            assertThat("Should throw 404 for second deleted claim", e.getCode(), equalTo(404))
        }

        // Final list should be empty
        def finalList = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Final claims list should be empty", finalList, empty())

        logger.info("All comprehensive federated claim operations completed successfully!")
    }

    @Test
    void testErrorHandling() {
        logger.info("Testing error handling scenarios...")

        def fakeAppId = "0oa${UUID.randomUUID().toString().replace('-', '')}"
        def fakeClaimId = "ofc${UUID.randomUUID().toString().replace('-', '')}"

        def claimRequest = new FederatedClaimRequestBody()
            .name("test")
            .expression("user.profile.test")

        def federatedClaim = new FederatedClaim()
            .name("test")
            .expression("user.profile.test")

        // ==================== SCENARIO 1: Invalid App ID (404 errors) ====================
        logger.info("Test: Invalid app ID scenarios")

        // CREATE with invalid app ID
        try {
            federatedClaimApi.createFederatedClaim(fakeAppId, claimRequest)
            throw new AssertionError("Expected 404 for create with invalid app ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid app ID on create", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for create with invalid app ID")

        // LIST with invalid app ID
        try {
            federatedClaimApi.listFederatedClaims(fakeAppId)
            throw new AssertionError("Expected 404 for list with invalid app ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid app ID on list", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for list with invalid app ID")

        // GET with invalid app ID
        try {
            federatedClaimApi.getFederatedClaim(fakeAppId, fakeClaimId)
            throw new AssertionError("Expected 404 for get with invalid app ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid app ID on get", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for get with invalid app ID")

        // REPLACE with invalid app ID
        try {
            federatedClaimApi.replaceFederatedClaim(fakeAppId, fakeClaimId, federatedClaim)
            throw new AssertionError("Expected 404 for replace with invalid app ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid app ID on replace", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for replace with invalid app ID")

        // DELETE with invalid app ID
        try {
            federatedClaimApi.deleteFederatedClaim(fakeAppId, fakeClaimId)
            throw new AssertionError("Expected 404 for delete with invalid app ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid app ID on delete", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for delete with invalid app ID")

        // ==================== SCENARIO 2: Invalid Claim ID (404 errors) ====================
        logger.info("Test: Invalid claim ID scenarios")

        // GET with invalid claim ID
        try {
            federatedClaimApi.getFederatedClaim(testAppId, fakeClaimId)
            throw new AssertionError("Expected 404 for get with invalid claim ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid claim ID on get", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for get with invalid claim ID")

        // REPLACE with invalid claim ID
        try {
            federatedClaimApi.replaceFederatedClaim(testAppId, fakeClaimId, federatedClaim)
            throw new AssertionError("Expected 404 for replace with invalid claim ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid claim ID on replace", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for replace with invalid claim ID")

        // DELETE with invalid claim ID
        try {
            federatedClaimApi.deleteFederatedClaim(testAppId, fakeClaimId)
            throw new AssertionError("Expected 404 for delete with invalid claim ID")
        } catch (ApiException e) {
            assertThat("Should throw 404 for invalid claim ID on delete", e.getCode(), equalTo(404))
        }
        logger.info("Correctly threw 404 for delete with invalid claim ID")

        // ==================== SCENARIO 3: Null Parameters ====================
        logger.info("Test: Null parameter scenarios")

        // CREATE with null app ID
        try {
            federatedClaimApi.createFederatedClaim(null, claimRequest)
            throw new AssertionError("Expected exception for create with null app ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null app ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for create with null app ID")

        // CREATE with null request body
        try {
            federatedClaimApi.createFederatedClaim(testAppId, null)
            throw new AssertionError("Expected exception for create with null request body")
        } catch (Exception e) {
            assertThat("Should throw exception for null request body", e, notNullValue())
        }
        logger.info("Correctly threw exception for create with null request body")

        // GET with null app ID
        try {
            federatedClaimApi.getFederatedClaim(null, "claimId")
            throw new AssertionError("Expected exception for get with null app ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null app ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for get with null app ID")

        // GET with null claim ID
        try {
            federatedClaimApi.getFederatedClaim(testAppId, null)
            throw new AssertionError("Expected exception for get with null claim ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null claim ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for get with null claim ID")

        // REPLACE with null app ID
        try {
            federatedClaimApi.replaceFederatedClaim(null, "claimId", federatedClaim)
            throw new AssertionError("Expected exception for replace with null app ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null app ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for replace with null app ID")

        // REPLACE with null claim ID
        try {
            federatedClaimApi.replaceFederatedClaim(testAppId, null, federatedClaim)
            throw new AssertionError("Expected exception for replace with null claim ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null claim ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for replace with null claim ID")

        // DELETE with null app ID
        try {
            federatedClaimApi.deleteFederatedClaim(null, "claimId")
            throw new AssertionError("Expected exception for delete with null app ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null app ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for delete with null app ID")

        // DELETE with null claim ID
        try {
            federatedClaimApi.deleteFederatedClaim(testAppId, null)
            throw new AssertionError("Expected exception for delete with null claim ID")
        } catch (Exception e) {
            assertThat("Should throw exception for null claim ID", e, notNullValue())
        }
        logger.info("Correctly threw exception for delete with null claim ID")

        logger.info("Error handling tests completed successfully!")
    }

    @Test
    void testMultipleClaimsListing() {
        logger.info("Testing listing multiple federated claims...")

        // Clean up any existing claims first to ensure clean state
        cleanupAllClaims(testAppId)

        // Verify empty list initially
        def emptyList = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Initial claims list should be empty", emptyList, empty())
        logger.info("Verified initial empty list")

        // Create multiple claims
        logger.info("Creating multiple claims...")

        def claim1 = federatedClaimApi.createFederatedClaim(testAppId, new FederatedClaimRequestBody()
            .name("location")
            .expression("user.profile.city"))

        def claim2 = federatedClaimApi.createFederatedClaim(testAppId, new FederatedClaimRequestBody()
            .name("country")
            .expression("user.profile.countryCode"))

        def claim3 = federatedClaimApi.createFederatedClaim(testAppId, new FederatedClaimRequestBody()
            .name("costCenter")
            .expression("user.profile.costCenter"))

        logger.info("Created 3 federated claims")

        // List all claims
        def allClaims = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Claims list should not be null", allClaims, notNullValue())
        assertThat("Claims list should have 3 items", allClaims.size(), equalTo(3))

        // Verify each claim exists
        def foundLocation = allClaims.find { it.getName() == "location" && it.getExpression() == "user.profile.city" }
        def foundCountry = allClaims.find { it.getName() == "country" && it.getExpression() == "user.profile.countryCode" }
        def foundCostCenter = allClaims.find { it.getName() == "costCenter" && it.getExpression() == "user.profile.costCenter" }

        assertThat("Should find 'location' claim", foundLocation, notNullValue())
        assertThat("Should find 'country' claim", foundCountry, notNullValue())
        assertThat("Should find 'costCenter' claim", foundCostCenter, notNullValue())

        // Verify all claims have required properties
        allClaims.each { claim ->
            assertThat("Claim ID should not be empty", claim.getId(), not(emptyString()))
            assertThat("Claim ID should start with 'ofc'", claim.getId(), startsWith("ofc"))
            assertThat("Claim name should not be empty", claim.getName(), not(emptyString()))
            assertThat("Claim expression should not be empty", claim.getExpression(), not(emptyString()))
            assertThat("Claim created timestamp should not be empty", claim.getCreated(), not(emptyString()))
            assertThat("Claim lastUpdated timestamp should not be empty", claim.getLastUpdated(), not(emptyString()))
        }

        logger.info("Verified all claims have required properties")

        // Cleanup
        federatedClaimApi.deleteFederatedClaim(testAppId, claim1.getId())
        federatedClaimApi.deleteFederatedClaim(testAppId, claim2.getId())
        federatedClaimApi.deleteFederatedClaim(testAppId, claim3.getId())

        // Verify all deleted
        def finalList = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Final claims list should be empty", finalList, empty())

        logger.info("Multiple claims listing test completed successfully!")
    }

    @Test
    void testDifferentExpressions() {
        logger.info("Testing different Okta Expression Language expressions...")

        // Clean up any existing claims first to ensure clean state
        cleanupAllClaims(testAppId)

        def expressions = [
            "user_email": "user.profile.email",
            "user_name": "user.profile.firstName",
            "user_login": "user.profile.login",
            "user_mobile": "user.profile.mobilePhone",
            "conditional": "user.profile.email != null ? user.profile.email : user.profile.login"
        ]

        def createdClaims = []

        // Create claims with different expressions
        expressions.each { name, expression ->
            logger.info("Creating claim '${name}' with expression: ${expression}")
            def claim = federatedClaimApi.createFederatedClaim(testAppId, new FederatedClaimRequestBody()
                .name(name)
                .expression(expression))

            assertThat("Claim should not be null", claim, notNullValue())
            assertThat("Claim name should match", claim.getName(), equalTo(name))
            assertThat("Claim expression should match", claim.getExpression(), equalTo(expression))
            createdClaims.add(claim)
        }

        logger.info("Created ${createdClaims.size()} claims with different expressions")

        // Verify all created
        def allClaims = federatedClaimApi.listFederatedClaims(testAppId)
        assertThat("Claims list should have ${expressions.size()} items", allClaims.size(), equalTo(expressions.size()))

        // Verify each expression is retrievable
        createdClaims.each { createdClaim ->
            def retrieved = federatedClaimApi.getFederatedClaim(testAppId, createdClaim.getId())
            assertThat("Retrieved claim name should match", retrieved.getName(), equalTo(createdClaim.getName()))
            assertThat("Retrieved claim expression should match", retrieved.getExpression(), equalTo(createdClaim.getExpression()))
        }

        logger.info("Verified all claims are retrievable")

        // Cleanup
        createdClaims.each { claim ->
            federatedClaimApi.deleteFederatedClaim(testAppId, claim.getId())
        }

        logger.info("Different expressions test completed successfully!")
    }

    @Test
    void testInvalidDataValidation() {
        logger.info("Testing invalid data validation...")

        // Test 1: Invalid expression syntax - malformed EL
        logger.info("Test: Invalid expression syntax")
        def invalidExpressionRequest = new FederatedClaimRequestBody()
            .name("invalid_expr")
            .expression("user.profile.")  // Invalid - incomplete expression

        try {
            federatedClaimApi.createFederatedClaim(testAppId, invalidExpressionRequest)
            throw new AssertionError("Expected 400 for invalid expression syntax")
        } catch (ApiException e) {
            assertThat("Should throw 400 for invalid expression", e.getCode(), equalTo(400))
            assertThat("Error message should contain 'validation'", e.getMessage()?.toLowerCase(), containsString("validation"))
        }
        logger.info("Correctly threw 400 for invalid expression syntax")

        // Test 2: Empty name
        logger.info("Test: Empty claim name")
        def emptyNameRequest = new FederatedClaimRequestBody()
            .name("")
            .expression("user.profile.email")

        try {
            federatedClaimApi.createFederatedClaim(testAppId, emptyNameRequest)
            throw new AssertionError("Expected 400 for empty name")
        } catch (ApiException e) {
            assertThat("Should throw 400 for empty name", e.getCode(), equalTo(400))
        }
        logger.info("Correctly threw 400 for empty name")

        // Test 3: Empty expression
        logger.info("Test: Empty expression")
        def emptyExprRequest = new FederatedClaimRequestBody()
            .name("test")
            .expression("")

        try {
            federatedClaimApi.createFederatedClaim(testAppId, emptyExprRequest)
            throw new AssertionError("Expected 400 for empty expression")
        } catch (ApiException e) {
            assertThat("Should throw 400 for empty expression", e.getCode(), equalTo(400))
        }
        logger.info("Correctly threw 400 for empty expression")

        // Test 4: Reserved claim names
        logger.info("Test: Reserved OIDC claim name")
        def reservedNameRequest = new FederatedClaimRequestBody()
            .name("sub")  // 'sub' is a reserved OIDC claim
            .expression("user.profile.email")

        try {
            federatedClaimApi.createFederatedClaim(testAppId, reservedNameRequest)
            throw new AssertionError("Expected 400 for reserved claim name")
        } catch (ApiException e) {
            assertThat("Should throw 400 for reserved name", e.getCode(), equalTo(400))
            assertThat("Error message should contain 'reserved'", e.getMessage()?.toLowerCase(), containsString("reserved"))
        }
        logger.info("Correctly threw 400 for reserved claim name")

        logger.info("Invalid data validation tests completed successfully!")
    }

    @Test
    void testTimestampValidation() {
        logger.info("Testing timestamp validation on replace operations...")

        // Create initial claim
        def createRequest = new FederatedClaimRequestBody()
            .name("originalName")
            .expression("user.profile.email")

        def createdClaim = federatedClaimApi.createFederatedClaim(testAppId, createRequest)
        def originalCreated = createdClaim.getCreated()
        def originalLastUpdated = createdClaim.getLastUpdated()

        logger.info("Created claim with timestamps - Created: ${originalCreated}, LastUpdated: ${originalLastUpdated}")

        // Wait to ensure timestamp difference
        Thread.sleep(1000)

        // Replace the claim
        def replaceRequest = new FederatedClaim()
            .name("updatedName")
            .expression("user.profile.login")

        def replacedClaim = federatedClaimApi.replaceFederatedClaim(testAppId, createdClaim.getId(), replaceRequest)

        // Verify timestamp behavior
        assertThat("ID should not change", replacedClaim.getId(), equalTo(createdClaim.getId()))
        assertThat("Created timestamp should never change", replacedClaim.getCreated(), equalTo(originalCreated))
        assertThat("LastUpdated should change on replace", replacedClaim.getLastUpdated(), not(equalTo(originalLastUpdated)))

        // Parse and compare timestamps
        def createdTime = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(replacedClaim.getCreated()))
        def lastUpdatedTime = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(replacedClaim.getLastUpdated()))
        assertThat("LastUpdated should be after Created", lastUpdatedTime.isAfter(createdTime), equalTo(true))

        logger.info("Verified timestamp behavior - Created: ${replacedClaim.getCreated()}, LastUpdated: ${replacedClaim.getLastUpdated()}")

        // Cleanup
        federatedClaimApi.deleteFederatedClaim(testAppId, createdClaim.getId())

        logger.info("Timestamp validation test completed successfully!")
    }

    /**
     * Helper method to clean up all existing claims for an app
     * This ensures each test starts with a clean state
     */
    private void cleanupAllClaims(String appId) {
        try {
            def existingClaims = federatedClaimApi.listFederatedClaims(appId)
            existingClaims.each { claim ->
                try {
                    federatedClaimApi.deleteFederatedClaim(appId, claim.getId())
                    logger.debug("Deleted existing claim: ${claim.getId()} (${claim.getName()})")
                } catch (Exception e) {
                    logger.warn("Failed to delete claim ${claim.getId()}: ${e.message}")
                }
            }
            if (existingClaims.size() > 0) {
                logger.info("Cleaned up ${existingClaims.size()} existing claims")
            }
        } catch (Exception e) {
            logger.warn("Failed to list/cleanup existing claims: ${e.message}")
        }
    }
}

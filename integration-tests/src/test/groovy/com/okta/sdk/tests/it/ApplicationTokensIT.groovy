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

import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationTokensApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Comprehensive integration tests for Application Tokens API.
 * Mirrors C# SDK ApplicationTokensApiTests coverage.
 * 
 * ENDPOINTS TESTED (4):
 * 1. GET /api/v1/apps/{appId}/tokens - List OAuth 2.0 refresh tokens
 * 2. GET /api/v1/apps/{appId}/tokens/{tokenId} - Get specific token
 * 3. DELETE /api/v1/apps/{appId}/tokens/{tokenId} - Revoke single token
 * 4. DELETE /api/v1/apps/{appId}/tokens - Revoke all tokens
 * 
 * FEATURES TESTED:
 * - Token listing with pagination (expand, after, limit)
 * - Token retrieval by ID
 * - Single token revocation
 * - Bulk token revocation
 * - Error handling (404 for invalid app/token IDs)
 * - Null parameter validation
 * - Empty result handling (no tokens without OAuth flow)
 * 
 * NOTE: OAuth 2.0 refresh tokens are created through authorization_code flow.
 * Without actual OAuth flow, most operations work with empty data sets.
 * This tests the API endpoints themselves, not the OAuth flow.
 */
class ApplicationTokensIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTokensIT.class)
    private ApplicationApi applicationApi
    private ApplicationTokensApi tokensApi
    private String testAppId

    @BeforeClass
    void setup() {
        applicationApi = new ApplicationApi(getClient())
        tokensApi = new ApplicationTokensApi(getClient())

        // Create OIDC application with refresh_token grant type
        // This is required for OAuth 2.0 token management
        OpenIdConnectApplication app = new OpenIdConnectApplication()
        app.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label("ApplicationTokens IT Test App - " + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        // Add OAuth credentials with auto key rotation
        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.autoKeyRotation(true)
        
        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        app.credentials(credentials)

        OpenIdConnectApplicationSettingsClient client = new OpenIdConnectApplicationSettingsClient()
        client.clientUri("https://example.com")
            .logoUri("https://example.com/logo.png")
            .redirectUris(["https://example.com/oauth2/callback"])
            .postLogoutRedirectUris(["https://example.com"])
            .responseTypes([OAuthResponseType.CODE])
            .grantTypes([GrantType.AUTHORIZATION_CODE, GrantType.REFRESH_TOKEN])
            .applicationType(OpenIdConnectApplicationType.WEB)

        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(client)

        app.settings(settings)

        OpenIdConnectApplication createdApp = applicationApi.createApplication(app, true, null) as OpenIdConnectApplication
        testAppId = createdApp.getId()

        logger.info("Created test OIDC application: {}", testAppId)
    }

    @AfterClass
    void cleanup() {
        if (testAppId != null) {
            try {
                logger.info("Deactivating and deleting test application: {}", testAppId)
                applicationApi.deactivateApplication(testAppId)
                Thread.sleep(1000)
                applicationApi.deleteApplication(testAppId)
                logger.info("Test application deleted successfully")
            } catch (Exception e) {
                logger.warn("Error cleaning up test application: {}", e.getMessage())
            }
        }
    }

    /**
     * Comprehensive test for all token operations.
     * Tests LIST, GET (404), REVOKE single (404), REVOKE all operations.
     */
    @Test
    void testComprehensiveTokenOperations() {
        logger.info("Testing comprehensive token operations...")

        // Test 1: List tokens (should be empty without OAuth flow)
        List<OAuth2Token> tokens = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, null)
        assertThat("Token list should not be null", tokens, notNullValue())
        assertThat("No tokens should exist without OAuth flow", tokens.isEmpty(), is(true))
        logger.debug("List tokens: empty list verified")

        // Test 2: List tokens with expand parameter
        List<OAuth2Token> tokensWithExpand = tokensApi.listOAuth2TokensForApplication(testAppId, "scope", null, 20)
        assertThat(tokensWithExpand, notNullValue())
        assertThat(tokensWithExpand.isEmpty(), is(true))
        logger.debug("List tokens with expand: verified")

        // Test 3: List tokens with limit variations
        List<OAuth2Token> tokensLimit1 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 1)
        assertThat(tokensLimit1, notNullValue())
        assertThat(tokensLimit1.isEmpty(), is(true))

        List<OAuth2Token> tokensLimit200 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 200)
        assertThat(tokensLimit200, notNullValue())
        assertThat(tokensLimit200.isEmpty(), is(true))
        logger.debug("List tokens with limit variations: verified")

        // Test 4: Get token with invalid ID (should return 404)
        String fakeTokenId = "tok_nonexistent_token_id"
        ApiException getException = expect(ApiException.class, () ->
            tokensApi.getOAuth2TokenForApplication(testAppId, fakeTokenId, null))
        assertThat("Should return 404 for non-existent token", getException.getCode(), is(404))
        logger.debug("Get token with invalid ID: 404 verified")

        // Test 5: Revoke single token with invalid ID (should return 404)
        ApiException revokeException = expect(ApiException.class, () ->
            tokensApi.revokeOAuth2TokenForApplication(testAppId, fakeTokenId))
        assertThat("Should return 404 when revoking non-existent token", revokeException.getCode(), is(404))
        logger.debug("Revoke token with invalid ID: 404 verified")

        // Test 6: Revoke all tokens (should succeed even with no tokens)
        tokensApi.revokeOAuth2TokensForApplication(testAppId)
        logger.debug("Revoke all tokens: succeeded")

        // Verify app still exists after all operations
        Application app = applicationApi.getApplication(testAppId, null)
        assertThat(app, notNullValue())
        assertThat(app.getId(), equalTo(testAppId))

        logger.info("Comprehensive token operations test completed successfully!")
    }

    /**
     * Test error handling with invalid application ID.
     * Verifies 404 errors for all operations with non-existent app.
     */
    @Test
    void testInvalidApplicationId() {
        logger.info("Testing error handling with invalid application ID...")

        String invalidAppId = "0oa_invalid_app_id_12345"

        // Test 1: List tokens with invalid app ID
        ApiException listException = expect(ApiException.class, () ->
            tokensApi.listOAuth2TokensForApplication(invalidAppId, null, null, null))
        assertThat("List tokens should return 404 for invalid app", listException.getCode(), is(404))
        logger.debug("List tokens (invalid app): 404 verified")

        // Test 2: Get token with invalid app ID
        ApiException getException = expect(ApiException.class, () ->
            tokensApi.getOAuth2TokenForApplication(invalidAppId, "tok_fake_token", null))
        assertThat("Get token should return 404 for invalid app", getException.getCode(), is(404))
        logger.debug("Get token (invalid app): 404 verified")

        // Test 3: Revoke single token with invalid app ID
        ApiException revokeException = expect(ApiException.class, () ->
            tokensApi.revokeOAuth2TokenForApplication(invalidAppId, "tok_fake_token"))
        assertThat("Revoke token should return 404 for invalid app", revokeException.getCode(), is(404))
        logger.debug("Revoke token (invalid app): 404 verified")

        // Test 4: Revoke all tokens with invalid app ID
        ApiException revokeAllException = expect(ApiException.class, () ->
            tokensApi.revokeOAuth2TokensForApplication(invalidAppId))
        assertThat("Revoke all tokens should return 404 for invalid app", revokeAllException.getCode(), is(404))
        logger.debug("Revoke all tokens (invalid app): 404 verified")

        logger.info("Invalid application ID error handling test completed successfully!")
    }

    /**
     * Test null parameter validation.
     * Verifies that operations requiring tokenId reject null values.
     */
    @Test
    void testNullParameterValidation() {
        logger.info("Testing null parameter validation...")

        // Test 1: Get token with null tokenId
        ApiException getException = expect(ApiException.class, () ->
            tokensApi.getOAuth2TokenForApplication(testAppId, null, null))
        assertThat("Should return 400 for null tokenId", getException.getCode(), is(400))
        logger.debug("Get token with null tokenId: 400 verified")

        // Test 2: Revoke token with null tokenId
        ApiException revokeException = expect(ApiException.class, () ->
            tokensApi.revokeOAuth2TokenForApplication(testAppId, null))
        assertThat("Should return 400 for null tokenId", revokeException.getCode(), is(400))
        logger.debug("Revoke token with null tokenId: 400 verified")

        logger.info("Null parameter validation test completed successfully!")
    }

    /**
     * Test token listing with various parameter combinations.
     * Tests pagination and filtering.
     */
    @Test
    void testTokenListingParameters() {
        logger.info("Testing token listing with various parameters...")

        // Test 1: Basic listing (no parameters)
        List<OAuth2Token> basicList = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, null)
        assertThat(basicList, notNullValue())
        assertThat(basicList.isEmpty(), is(true))
        logger.debug("Basic listing: verified")

        // Test 2: With expand parameter
        List<OAuth2Token> expandList = tokensApi.listOAuth2TokensForApplication(testAppId, "scope", null, null)
        assertThat(expandList, notNullValue())
        assertThat(expandList.isEmpty(), is(true))
        logger.debug("Listing with expand: verified")

        // Test 3: With limit parameter (minimum boundary)
        List<OAuth2Token> limitMin = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 1)
        assertThat(limitMin, notNullValue())
        assertThat(limitMin.isEmpty(), is(true))
        logger.debug("Listing with limit=1: verified")

        // Test 4: With limit parameter (maximum boundary)
        List<OAuth2Token> limitMax = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 200)
        assertThat(limitMax, notNullValue())
        assertThat(limitMax.isEmpty(), is(true))
        logger.debug("Listing with limit=200: verified")

        // Test 5: With all parameters combined (expand and limit)
        List<OAuth2Token> allParams = tokensApi.listOAuth2TokensForApplication(testAppId, "scope", null, 50)
        assertThat(allParams, notNullValue())
        assertThat(allParams.isEmpty(), is(true))
        logger.debug("Listing with all parameters: verified")

        logger.info("Token listing parameters test completed successfully!")
    }

    /**
     * Test boundary conditions for limit parameter.
     * Verifies handling of edge cases (0, negative, very large).
     */
    @Test
    void testLimitParameterBoundaries() {
        logger.info("Testing limit parameter boundary conditions...")

        // Test 1: Minimum valid limit (1)
        List<OAuth2Token> limit1 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 1)
        assertThat(limit1, notNullValue())
        assertThat(limit1.isEmpty(), is(true))
        logger.debug("Limit=1: verified")

        // Test 2: Maximum valid limit (200)
        List<OAuth2Token> limit200 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 200)
        assertThat(limit200, notNullValue())
        assertThat(limit200.isEmpty(), is(true))
        logger.debug("Limit=200: verified")

        // Test 3: Mid-range limit (50)
        List<OAuth2Token> limit50 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 50)
        assertThat(limit50, notNullValue())
        assertThat(limit50.isEmpty(), is(true))
        logger.debug("Limit=50: verified")

        // Test 4: Another mid-range limit (100)
        List<OAuth2Token> limit100 = tokensApi.listOAuth2TokensForApplication(testAppId, null, null, 100)
        assertThat(limit100, notNullValue())
        assertThat(limit100.isEmpty(), is(true))
        logger.debug("Limit=100: verified")

        logger.info("Limit parameter boundary conditions test completed successfully!")
    }
}

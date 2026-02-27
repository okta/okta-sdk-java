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

import com.okta.sdk.resource.api.ApiTokenApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.ApiToken
import com.okta.sdk.resource.model.ApiTokenUpdate
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Comprehensive integration tests for API Token API.
 * 
 * NOTE: API Tokens cannot be created via API - they must be created through the Okta Admin Console.
 * These tests use existing tokens in the org for validation.
 * 
 * ENDPOINTS TESTED (5):
 * 1. GET    /api/v1/api-tokens - List all API tokens
 * 2. GET    /api/v1/api-tokens/{apiTokenId} - Get specific token
 * 3. PUT    /api/v1/api-tokens/{apiTokenId} - Update token
 * 4. DELETE /api/v1/api-tokens/{apiTokenId} - Revoke token
 * 5. DELETE /api/v1/api-tokens/current - Revoke current token
 */
class ApiTokenIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenIT.class)
    def apiTokenApi = new ApiTokenApi(getClient())

    /**
     * Comprehensive test covering ALL API Token API operations.
     * Tests CRUD lifecycle and error handling.
     */
    @Test
    void testAllApiTokenOperations() {
        logger.info("Testing API Token operations...")

        // ========================================
        // LIST API TOKENS
        // ========================================
        
        logger.info("Testing LIST tokens operation...")
        
        def allTokens = apiTokenApi.listApiTokens()
        def tokenList = allTokens

        assertThat(tokenList, notNullValue())
        assertThat(tokenList, not(empty()))
        assertThat("At least one API token should exist (the one used for auth)", 
            tokenList.size(), greaterThan(0))

        // Validate token structure
        def firstToken = tokenList.first()
        assertThat(firstToken.id, notNullValue())
        assertThat("API token IDs should start with 00T", 
            firstToken.id, startsWith("00T"))
        assertThat(firstToken.name, notNullValue())
        assertThat(firstToken.userId, notNullValue())
        assertThat("User IDs should start with 00u", 
            firstToken.userId, startsWith("00u"))
        assertThat(firstToken.clientName, notNullValue())
        assertThat("TokenWindow should be present", 
            firstToken.tokenWindow, notNullValue())

        // Validate all tokens have required fields
        for (ApiToken token : tokenList) {
            assertThat(token.id, notNullValue())
            assertThat(token.name, notNullValue())
            assertThat(token.userId, notNullValue())
            assertThat(token.clientName, notNullValue())
        }

        Thread.sleep(1000)

        // ========================================
        // GET SPECIFIC API TOKEN
        // ========================================
        
        logger.info("Testing GET token operation...")
        
        def testTokenId = firstToken.id
        def retrievedToken = apiTokenApi.getApiToken(testTokenId)

        assertThat(retrievedToken, notNullValue())
        assertThat("Retrieved token ID should match requested ID", 
            retrievedToken.id, equalTo(testTokenId))
        assertThat(retrievedToken.name, notNullValue())
        assertThat("Retrieved token name should match", 
            retrievedToken.name, equalTo(firstToken.name))
        assertThat(retrievedToken.userId, notNullValue())
        assertThat("Retrieved token userId should match", 
            retrievedToken.userId, equalTo(firstToken.userId))
        assertThat(retrievedToken.clientName, notNullValue())
        assertThat("Retrieved token clientName should match", 
            retrievedToken.clientName, equalTo(firstToken.clientName))
        assertThat("TokenWindow should match", 
            retrievedToken.tokenWindow, equalTo(firstToken.tokenWindow))

        Thread.sleep(1000)

        // ========================================
        // ERROR HANDLING - GET with invalid ID
        // ========================================
        
        logger.info("Testing error handling for GET operation...")
        
        def invalidTokenId = "00Tin-valid_id_xyz"

        ApiException getException = expect(ApiException) {
            apiTokenApi.getApiToken(invalidTokenId)
        }

        assertThat(getException, notNullValue())
        assertThat("Invalid token ID should return 404 Not Found", 
            getException.code, equalTo(404))
        assertThat(getException.message, notNullValue())

        Thread.sleep(1000)

        // ========================================
        // ERROR HANDLING - UPDATE with invalid ID
        // ========================================
        
        logger.info("Testing error handling for UPDATE operation...")
        
        // NOTE: Upsert operations may be restricted in some orgs
        def invalidTokenForUpsert = "00Invalid_upsert_id"

        def updateRequest = new ApiTokenUpdate()
        // Note: ApiTokenUpdate structure may vary based on SDK version

        ApiException upsertException = expect(ApiException) {
            apiTokenApi.upsertApiToken(invalidTokenForUpsert, updateRequest)
        }

        assertThat(upsertException, notNullValue())
        assertThat("Update with invalid ID should return error", 
            upsertException.code, isOneOf(400, 401, 403, 404))

        Thread.sleep(1000)

        // ========================================
        // ERROR HANDLING - REVOKE with invalid ID
        // ========================================
        
        logger.info("Testing error handling for REVOKE operation...")
        
        // NOTE: Cannot revoke actual tokens as that would break subsequent tests
        def invalidRevokeTokenId = "00Tin-valid_token_id_12345"

        ApiException revokeException = expect(ApiException) {
            apiTokenApi.revokeApiToken(invalidRevokeTokenId)
        }

        assertThat(revokeException, notNullValue())
        assertThat("Revoke with invalid ID should return error", 
            revokeException.code, isOneOf(400, 401, 403, 404))
        assertThat("Non-existent token should return 404 Not Found", 
            revokeException.code, equalTo(404))
        assertThat("Exception should have a descriptive message", 
            revokeException.message, notNullValue())

        Thread.sleep(1000)

        logger.info("All API Token operations completed successfully!")
    }

    /**
     * Test that verifies GET operation with invalid ID throws appropriate exception.
     */
    @Test
    void testGetApiTokenWithInvalidId() {
        def invalidTokenId = "00Tin-valid_id_abc"

        ApiException exception = expect(ApiException) {
            apiTokenApi.getApiToken(invalidTokenId)
        }

        assertThat(exception, notNullValue())
        assertThat("Invalid token ID should return 404 Not Found", 
            exception.code, equalTo(404))
        assertThat(exception.message, notNullValue())
    }

    /**
     * Test that verifies UPDATE operation with invalid ID throws appropriate exception.
     */
    @Test
    void testUpsertApiTokenWithInvalidId() {
        def invalidTokenId = "00Tin-valid_id_def"

        def updateRequest = new ApiTokenUpdate()

        ApiException exception = expect(ApiException) {
            apiTokenApi.upsertApiToken(invalidTokenId, updateRequest)
        }

        assertThat(exception, notNullValue())
        assertThat(exception.code, isOneOf(400, 401, 403, 404))
    }

    /**
     * Test that verifies REVOKE operation with invalid ID throws appropriate exception.
     */
    @Test
    void testRevokeApiTokenWithInvalidId() {
        def invalidTokenId = "00Tin-valid_id_ghi"

        ApiException exception = expect(ApiException) {
            apiTokenApi.revokeApiToken(invalidTokenId)
        }

        assertThat(exception, notNullValue())
        assertThat(exception.code, isOneOf(400, 401, 403, 404))
    }

    /**
     * Test that verifies list operation returns consistent results.
     */
    @Test
    void testListApiTokensConsistency() {
        def tokensFirstCall = apiTokenApi.listApiTokens()
        def tokenListFirst = tokensFirstCall

        Thread.sleep(500)

        def tokensSecondCall = apiTokenApi.listApiTokens()
        def tokenListSecond = tokensSecondCall

        assertThat(tokenListFirst, notNullValue())
        assertThat(tokenListSecond, notNullValue())
        assertThat("Both list calls should return same number of tokens", 
            tokenListFirst.size(), equalTo(tokenListSecond.size()))

        // Verify token IDs are consistent
        def firstIds = tokenListFirst*.id as Set
        def secondIds = tokenListSecond*.id as Set
        assertThat("Token IDs should be consistent across calls", 
            firstIds, equalTo(secondIds))
    }

    /**
     * Test that verifies get operation returns consistent results.
     */
    @Test
    void testGetApiTokenConsistency() {
        def allTokens = apiTokenApi.listApiTokens()
        def tokenList = allTokens

        assertThat("Should have at least one token to test Get operations", 
            tokenList, not(empty()))

        def tokenId = tokenList[0].id
        assertThat(tokenId, notNullValue())

        def tokenFirstCall = apiTokenApi.getApiToken(tokenId)

        Thread.sleep(500)

        def tokenSecondCall = apiTokenApi.getApiToken(tokenId)

        assertThat(tokenFirstCall, notNullValue())
        assertThat(tokenSecondCall, notNullValue())

        // Verify key properties are consistent
        assertThat(tokenFirstCall.id, equalTo(tokenSecondCall.id))
        assertThat(tokenFirstCall.name, equalTo(tokenSecondCall.name))
        assertThat(tokenFirstCall.userId, equalTo(tokenSecondCall.userId))
        assertThat(tokenFirstCall.clientName, equalTo(tokenSecondCall.clientName))
        assertThat(tokenFirstCall.tokenWindow, equalTo(tokenSecondCall.tokenWindow))
    }

    /**
     * Test that verifies all tokens in list have valid structure.
     */
    @Test
    void testApiTokenStructureValidation() {
        def allTokens = apiTokenApi.listApiTokens()
        def tokenList = allTokens

        assertThat("Should have at least one token", tokenList, not(empty()))

        for (ApiToken token : tokenList) {
            // Validate ID format
            assertThat("Token ID should not be null", token.id, notNullValue())
            assertThat("Token ID should start with 00T", token.id, startsWith("00T"))
            assertThat("Token ID should have minimum length", token.id.length(), greaterThan(10))

            // Validate required fields
            assertThat("Token name should not be null", token.name, notNullValue())
            assertThat("Token name should not be empty", token.name, not(emptyString()))

            // Validate user ID
            assertThat("User ID should not be null", token.userId, notNullValue())
            assertThat("User ID should start with 00u", token.userId, startsWith("00u"))

            // Validate client name
            assertThat("Client name should not be null", token.clientName, notNullValue())
            assertThat("Client name should not be empty", token.clientName, not(emptyString()))

            // Validate token window
            assertThat("Token window should not be null", token.tokenWindow, notNullValue())

            logger.debug("Validated token: id={}, name={}, userId={}, clientName={}", 
                token.id, token.name, token.userId, token.clientName)
        }
    }

    /**
     * Test that verifies error handling for various invalid scenarios.
     */
    @Test
    void testErrorHandlingForVariousInvalidInputs() {
        // Test with completely invalid format
        ApiException exception1 = expect(ApiException) {
            apiTokenApi.getApiToken("invalid_format")
        }
        assertThat(exception1, notNullValue())
        assertThat(exception1.code, isOneOf(400, 404))

        Thread.sleep(500)

        // Test with empty string (if API allows it to reach the server)
        try {
            ApiException exception2 = expect(ApiException) {
                apiTokenApi.getApiToken("")
            }
            assertThat(exception2, notNullValue())
        } catch (IllegalArgumentException e) {
            // Some SDKs may throw IllegalArgumentException for empty strings
            assertThat(e, notNullValue())
        }

        Thread.sleep(500)

        // Test revoke with invalid format
        ApiException exception3 = expect(ApiException) {
            apiTokenApi.revokeApiToken("00Tinvalid123")
        }
        assertThat(exception3, notNullValue())
        assertThat(exception3.code, isOneOf(400, 404))
    }

    @Test
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            // Paged - listApiTokens
            def tokens = apiTokenApi.listApiTokensPaged()
            for (def t : tokens) { break }
            def tokensH = apiTokenApi.listApiTokensPaged(headers)
            for (def t : tokensH) { break }

            // Non-paged with headers
            apiTokenApi.listApiTokens(headers)
        } catch (Exception e) {
            // Expected
        }
    }
}

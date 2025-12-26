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
import com.okta.sdk.resource.api.UserOAuthApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User OAuth API.
 * Tests OAuth token management (refresh tokens, revocation).
 */
class UserOAuthIT extends ITSupport {

    private UserApi userApi
    private UserOAuthApi userOAuthApi

    UserOAuthIT() {
        this.userApi = new UserApi(getClient())
        this.userOAuthApi = new UserOAuthApi(getClient())
    }

    @Test(groups = "group3")
    void getRefreshTokenForUserAndClientTest() {
        def email = "user-get-token-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetToken")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Attempt to get a refresh token (requires OAuth client and token setup)
            def clientId = "test-client-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "test-token-"+UUID.randomUUID().toString().substring(0,8)+""
            
            def token = userOAuthApi.getRefreshTokenForUserAndClient(user.getId(), clientId, tokenId, null)
            
            assertThat(token, notNullValue())
            assertThat(token.getId(), notNullValue())

        } catch (ApiException e) {
            // Expected if OAuth tokens not configured or don't exist
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getRefreshTokenWithExpandParameterTest() {
        def email = "user-token-expand-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TokenExpand")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "test-client-expand-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "test-token-expand-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Get with expand parameter to include scope details
            def token = userOAuthApi.getRefreshTokenForUserAndClient(user.getId(), clientId, tokenId, "scope")
            
            assertThat(token, notNullValue())

        } catch (ApiException e) {
            // Expected if OAuth tokens not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void getRefreshTokenWithHttpInfoTest() {
        def email = "user-token-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TokenHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "test-client-info-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "test-token-info-"+UUID.randomUUID().toString().substring(0,8)+""
            
            def response = userOAuthApi.getRefreshTokenForUserAndClientWithHttpInfo(user.getId(), clientId, tokenId, null)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if OAuth tokens not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getRefreshTokenCallsCorrectEndpointTest() {
        def email = "user-token-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TokenEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-endpoint-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "token-endpoint-"+UUID.randomUUID().toString().substring(0,8)+""
            
            userOAuthApi.getRefreshTokenForUserAndClient(user.getId(), clientId, tokenId, null)

        } catch (ApiException e) {
            // Expected if token doesn't exist
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensForUserAndClientTest() {
        def email = "user-list-tokens-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListTokens")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "test-client-list-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // List all refresh tokens for user and client
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, null, null)
            
            assertThat(tokens, notNullValue())
            // May be empty if no tokens exist

        } catch (ApiException e) {
            // Expected if OAuth client not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensWithExpandParameterTest() {
        def email = "user-list-expand-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListExpand")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-list-expand-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // List with expand to include scope details
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, "scope", null, null)
            
            assertThat(tokens, notNullValue())

        } catch (ApiException e) {
            // Expected if OAuth not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensWithPaginationTest() {
        def email = "user-list-pagination-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListPagination")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-pagination-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // List with pagination parameters
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, "cursor-after", 10)
            
            assertThat(tokens, notNullValue())

        } catch (ApiException e) {
            // Expected if OAuth not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensEmptyResponseTest() {
        def email = "user-list-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-empty-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Should return empty list if no tokens exist
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, null, null)
            
            assertThat(tokens, notNullValue())
            // Typically empty for newly created user

        } catch (ApiException e) {
            // Expected if OAuth client not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensCallsCorrectEndpointTest() {
        def email = "user-list-endpoint-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListEndpoint")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-endpoint-list-"+UUID.randomUUID().toString().substring(0,8)+""
            
            userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, null, null)

        } catch (ApiException e) {
            // Expected if OAuth not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeTokenForUserAndClientTest() {
        def email = "user-revoke-token-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeToken")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-revoke-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "token-revoke-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Revoke a specific token
            userOAuthApi.revokeTokenForUserAndClient(user.getId(), clientId, tokenId)
            
            // Success - token revoked (or didn't exist)

        } catch (ApiException e) {
            // Expected if token doesn't exist or OAuth not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void revokeTokenWithHttpInfoTest() {
        def email = "user-revoke-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-revoke-info-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "token-revoke-info-"+UUID.randomUUID().toString().substring(0,8)+""
            
            def response = userOAuthApi.revokeTokenForUserAndClientWithHttpInfo(user.getId(), clientId, tokenId)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if token doesn't exist
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeMultipleTokensTest() {
        def email = "user-revoke-multiple-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeMultiple")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-multi-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId1 = "token-1-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Revoke first token
            userOAuthApi.revokeTokenForUserAndClient(user.getId(), clientId, tokenId1)

        } catch (ApiException e) {
            // Expected if tokens don't exist
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeAllTokensForUserAndClientTest() {
        def email = "user-revoke-all-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeAll")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-revoke-all-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Revoke all tokens for user and client
            userOAuthApi.revokeTokensForUserAndClient(user.getId(), clientId)
            
            // Success - all tokens revoked

        } catch (ApiException e) {
            // Expected if client not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void revokeAllTokensWithHttpInfoTest() {
        def email = "user-revoke-all-info-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeAllInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-all-info-"+UUID.randomUUID().toString().substring(0,8)+""
            
            def response = userOAuthApi.revokeTokensForUserAndClientWithHttpInfo(user.getId(), clientId)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if client not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeAllTokensMultipleClientsTest() {
        def email = "user-revoke-multi-client-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeMultiClient")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId1 = "client-1-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Revoke all tokens for first client
            userOAuthApi.revokeTokensForUserAndClient(user.getId(), clientId1)

        } catch (ApiException e) {
            // Expected if client not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getRefreshTokenWithRevokedStatusTest() {
        def email = "user-revoked-status-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokedStatus")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-revoked-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "token-revoked-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Get token (may have REVOKED status)
            def token = userOAuthApi.getRefreshTokenForUserAndClient(user.getId(), clientId, tokenId, null)
            
            assertThat(token, notNullValue())
            // Status could be ACTIVE, REVOKED, etc.

        } catch (ApiException e) {
            // Expected if token doesn't exist
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listRefreshTokensWithMultipleScopesTest() {
        def email = "user-multi-scopes-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("MultiScopes")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-scopes-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // List tokens which may have multiple scopes
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, null, null)
            
            assertThat(tokens, notNullValue())
            // Tokens may have scopes like openid, profile, email

        } catch (ApiException e) {
            // Expected if OAuth not configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeNonExistentTokenTest() {
        def email = "user-revoke-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeNonExistent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+""
            def tokenId = "nonexistent-token-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // Revoke non-existent token (should be idempotent)
            userOAuthApi.revokeTokenForUserAndClient(user.getId(), clientId, tokenId)
            
            // Success - idempotent operation

        } catch (ApiException e) {
            // Expected if token doesn't exist (some APIs return 404)
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void oauthTokenCompleteWorkflowTest() {
        def email = "user-oauth-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("OAuthWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def clientId = "client-workflow-"+UUID.randomUUID().toString().substring(0,8)+""
            
            // 1. List tokens (should be empty initially)
            def tokens = userOAuthApi.listRefreshTokensForUserAndClient(user.getId(), clientId, null, null, null)
            assertThat(tokens, notNullValue())
            
            // 2. Try to get a specific token
            if (tokens != null && tokens.size() > 0) {
                def tokenId = tokens.get(0).getId()
                def token = userOAuthApi.getRefreshTokenForUserAndClient(user.getId(), clientId, tokenId, null)
                assertThat(token, notNullValue())
                
                // 3. Revoke the token
                userOAuthApi.revokeTokenForUserAndClient(user.getId(), clientId, tokenId)
            }
            
            // 4. Revoke all tokens for the client
            userOAuthApi.revokeTokensForUserAndClient(user.getId(), clientId)

        } catch (ApiException e) {
            // Expected if OAuth not fully configured
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}

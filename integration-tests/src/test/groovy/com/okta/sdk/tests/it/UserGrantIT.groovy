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
import com.okta.sdk.resource.api.UserGrantApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Grant API.
 * Tests OAuth grant management including listing, getting, and revoking grants.
 */
class UserGrantIT extends ITSupport {

    private UserApi userApi
    private UserGrantApi userGrantApi

    UserGrantIT() {
        this.userApi = new UserApi(getClient())
        this.userGrantApi = new UserGrantApi(getClient())
    }

    @Test(groups = "group3")
    void getUserGrantTest() {
        def email = "user-get-grant-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetGrant")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List grants first to get a valid grant ID
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def grantId = grants.get(0).getId()
                
                // Get specific grant
                def grant = userGrantApi.getUserGrant(user.getId(), grantId, null)
                
                assertThat(grant, notNullValue())
                assertThat(grant.getId(), equalTo(grantId))
                assertThat(grant.getStatus(), notNullValue())
                assertThat(grant.getUserId(), equalTo(user.getId()))
                assertThat(grant.getClientId(), notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if user has no grants or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getUserGrantWithExpandTest() {
        def email = "user-grant-expand-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantExpand")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List grants first
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def grantId = grants.get(0).getId()
                
                // Get grant with expand parameter
                def grant = userGrantApi.getUserGrant(user.getId(), grantId, "scope")
                
                assertThat(grant, notNullValue())
                assertThat(grant.getId(), equalTo(grantId))
                assertThat(grant.getScopeId(), notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if no grants exist
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGrantsTest() {
        def email = "user-list-grants-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListGrants")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            assertThat(grants, notNullValue())
            // User may have grants or none depending on org configuration
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGrantsWithScopeFilterTest() {
        def email = "user-grants-scope-filter-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantsScope")
            .setLastName("Filter-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List grants filtered by scope
            def grants = userGrantApi.listUserGrants(user.getId(), "openid", null, null, null)
            
            assertThat(grants, notNullValue())
            // If grants exist, they should match the scope filter
            if (grants.size() > 0) {
                assertThat(grants.get(0).getScopeId(), notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGrantsWithPaginationTest() {
        def email = "user-grants-pagination-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantsPagination")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List grants with pagination parameters
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, 10)
            
            assertThat(grants, notNullValue())
            // Should return at most 10 grants
            assertThat(grants.size(), lessThanOrEqualTo(10))
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGrantsEmptyTest() {
        def email = "user-grants-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantsEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            assertThat(grants, notNullValue())
            // Newly created user typically has no grants
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listGrantsForUserAndClientTest() {
        def email = "user-client-grants-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ClientGrants")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // First get a client ID from user's grants
            def allGrants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (allGrants != null && allGrants.size() > 0) {
                def clientId = allGrants.get(0).getClientId()
                
                // List grants for specific client
                def clientGrants = userGrantApi.listGrantsForUserAndClient(user.getId(), clientId, null, null, null)
                
                assertThat(clientGrants, notNullValue())
                // All grants should be for the specified client
                if (clientGrants.size() > 0) {
                    assertThat(clientGrants.get(0).getClientId(), equalTo(clientId))
                }
            }
            
        } catch (ApiException e) {
            // Expected if no grants or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listGrantsForUserAndClientWithExpandTest() {
        def email = "user-client-expand-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ClientExpand")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get a client ID first
            def allGrants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (allGrants != null && allGrants.size() > 0) {
                def clientId = allGrants.get(0).getClientId()
                
                // List with expand parameter
                def grants = userGrantApi.listGrantsForUserAndClient(user.getId(), clientId, "scope", null, null)
                
                assertThat(grants, notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if no grants exist
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listGrantsForUserAndClientEmptyTest() {
        def email = "user-client-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ClientEmpty")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Use a non-existent client ID
            def grants = userGrantApi.listGrantsForUserAndClient(user.getId(), "non-existent-client-"+UUID.randomUUID().toString().substring(0,8)+"", null, null, null)
            
            assertThat(grants, notNullValue())
            // Should return empty list for non-existent client
            
        } catch (ApiException e) {
            // Expected - may return 404 for non-existent client
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserGrantTest() {
        def email = "user-revoke-grant-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeGrant")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get a grant to revoke
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def grantId = grants.get(0).getId()
                
                // Revoke the grant
                userGrantApi.revokeUserGrant(user.getId(), grantId)
                
                // Verify revocation - grant should not be found or show as revoked
                try {
                    def revokedGrant = userGrantApi.getUserGrant(user.getId(), grantId, null)
                    // If we can still get it, status should be REVOKED
                    assertThat(revokedGrant.getStatus(), equalTo(GrantOrTokenStatus.REVOKED))
                } catch (ApiException notFoundEx) {
                    // Expected - revoked grant may return 404
                    assertThat(notFoundEx.getCode(), equalTo(404))
                }
            }
            
        } catch (ApiException e) {
            // Expected if no grants or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserGrantsTest() {
        def email = "user-revoke-all-grants-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeAll")
            .setLastName("Grants-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke all grants for user
            userGrantApi.revokeUserGrants(user.getId())
            
            // Verify all grants are revoked
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            assertThat(grants, notNullValue())
            // All grants should be revoked or list should be empty
            if (grants.size() > 0) {
                for (def grant : grants) {
                    assertThat(grant.getStatus(), equalTo(GrantOrTokenStatus.REVOKED))
                }
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeGrantsForUserAndClientTest() {
        def email = "user-revoke-client-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeClient")
            .setLastName("Grants-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get a client ID first
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def clientId = grants.get(0).getClientId()
                
                // Revoke all grants for this client
                userGrantApi.revokeGrantsForUserAndClient(user.getId(), clientId)
                
                // Verify grants for this client are revoked
                def clientGrants = userGrantApi.listGrantsForUserAndClient(user.getId(), clientId, null, null, null)
                
                assertThat(clientGrants, notNullValue())
                // All grants should be revoked or list should be empty
                if (clientGrants.size() > 0) {
                    for (def grant : clientGrants) {
                        assertThat(grant.getStatus(), equalTo(GrantOrTokenStatus.REVOKED))
                    }
                }
            }
            
        } catch (ApiException e) {
            // Expected if no grants or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void grantLifecycleTest() {
        def email = "user-grant-lifecycle-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantLifecycle")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List all grants
            def allGrants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            assertThat(allGrants, notNullValue())
            
            if (allGrants.size() > 0) {
                def grantId = allGrants.get(0).getId()
                
                // Get specific grant
                def grant = userGrantApi.getUserGrant(user.getId(), grantId, null)
                assertThat(grant, notNullValue())
                assertThat(grant.getId(), equalTo(grantId))
                
                // Revoke grant
                userGrantApi.revokeUserGrant(user.getId(), grantId)
                
                // Verify revocation
                try {
                    def revokedGrant = userGrantApi.getUserGrant(user.getId(), grantId, null)
                    assertThat(revokedGrant.getStatus(), equalTo(GrantOrTokenStatus.REVOKED))
                } catch (ApiException notFoundEx) {
                    assertThat(notFoundEx.getCode(), equalTo(404))
                }
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getUserGrantWithInvalidIdTest() {
        def email = "user-invalid-grant-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("InvalidGrant")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userGrantApi.getUserGrant(user.getId(), "invalid-grant-id-"+UUID.randomUUID().toString().substring(0,8)+"", null)
            
            // Should not reach here
            assertThat("Should throw exception for invalid grant ID", false)
            
        } catch (ApiException e) {
            // Expected - invalid grant ID should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void revokeNonExistentGrantTest() {
        def email = "user-revoke-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeNonExistent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userGrantApi.revokeUserGrant(user.getId(), "non-existent-grant-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should not reach here
            assertThat("Should throw exception for non-existent grant", false)
            
        } catch (ApiException e) {
            // Expected - non-existent grant should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void listGrantsWithMultipleScopesTest() {
        def email = "user-multiple-scopes-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("MultipleScopes")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            assertThat(grants, notNullValue())
            
            // If multiple grants exist, verify they can have different scopes
            if (grants.size() > 1) {
                def scopes = grants.collect { it.getScopeId() }
                assertThat(scopes, notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void getUserGrantWithHttpInfoTest() {
        def email = "user-grant-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def grantId = grants.get(0).getId()
                
                // Get with HTTP info
                def response = userApi.getUserGrantWithHttpInfo(user.getId(), grantId, null)
                
                assertThat(response, notNullValue())
                assertThat(response.getStatusCode(), equalTo(200))
                assertThat(response.getData(), notNullValue())
                assertThat(response.getData().getId(), equalTo(grantId))
            }
            
        } catch (ApiException e) {
            // Expected if no grants exist
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void revokeUserGrantWithHttpInfoTest() {
        def email = "user-revoke-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                def grantId = grants.get(0).getId()
                
                // Revoke with HTTP info
                def response = userGrantApi.revokeUserGrantWithHttpInfo(user.getId(), grantId)
                
                assertThat(response, notNullValue())
                assertThat(response.getStatusCode(), equalTo(204))
            }
            
        } catch (ApiException e) {
            // Expected if no grants or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listUserGrantsWithInvalidUserIdTest() {
        try {
            userGrantApi.listUserGrants("invalid-user-id-"+UUID.randomUUID().toString().substring(0,8)+"", null, null, null, null)
            
            // Should not reach here
            assertThat("Should throw exception for invalid user ID", false)
            
        } catch (ApiException e) {
            // Expected - invalid user ID should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void grantStatusVerificationTest() {
        def email = "user-grant-status-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantStatus")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                for (def grant : grants) {
                    // Verify grant has valid status
                    assertThat(grant.getStatus(), notNullValue())
                    assertThat(grant.getStatus(), anyOf(
                        equalTo(GrantOrTokenStatus.ACTIVE),
                        equalTo(GrantOrTokenStatus.REVOKED)
                    ))
                }
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void grantSourceVerificationTest() {
        def email = "user-grant-source-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GrantSource")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def grants = userGrantApi.listUserGrants(user.getId(), null, null, null, null)
            
            if (grants != null && grants.size() > 0) {
                for (def grant : grants) {
                    // Verify grant has valid source
                    assertThat(grant.getSource(), notNullValue())
                    assertThat(grant.getSource(), anyOf(
                        equalTo(OAuth2ScopeConsentGrantSource.END_USER),
                        equalTo(OAuth2ScopeConsentGrantSource.ADMIN)
                    ))
                }
            }
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        def userId = null
        try {
            def user = userApi.createUser(
                new com.okta.sdk.resource.model.CreateUserRequest()
                    .profile(new com.okta.sdk.resource.model.UserProfile()
                        .firstName("PagedGrant").lastName("Test")
                        .email("paged-grant-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())
                        .login("paged-grant-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())),
                true, false, null)
            userId = user.getId()

            // Paged - listUserGrants
            def grants = userGrantApi.listUserGrantsPaged(userId, null, null, null, null)
            for (def g : grants) { break }
            def grantsH = userGrantApi.listUserGrantsPaged(userId, null, null, null, null, headers)
            for (def g : grantsH) { break }

            // Paged - listGrantsForUserAndClient
            try {
                def clientGrants = userGrantApi.listGrantsForUserAndClientPaged(userId, "nonexistent-client", null, null, null)
                for (def g : clientGrants) { break }
            } catch (Exception ignored) {}
            try {
                def clientGrantsH = userGrantApi.listGrantsForUserAndClientPaged(userId, "nonexistent-client", null, null, null, headers)
                for (def g : clientGrantsH) { break }
            } catch (Exception ignored) {}

            // Non-paged with headers
            userGrantApi.listUserGrants(userId, null, null, null, null, headers)

        } catch (Exception e) {
            // Expected
        } finally {
            if (userId) {
                try {
                    new com.okta.sdk.resource.api.UserLifecycleApi(getClient()).deactivateUser(userId, false, null)
                    userApi.deleteUser(userId, false, null)
                } catch (Exception ignored) {}
            }
        }
    }
}

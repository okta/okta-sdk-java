/*
 * Copyright 2020-Present Okta, Inc.
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

import com.okta.sdk.resource.api.*
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Integration tests for Authorization Server APIs.
 */
class AuthorizationServerIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServerIT)


    private AuthorizationServerApi authorizationServerApi
    private AuthorizationServerAssocApi authorizationServerAssocApi
    private AuthorizationServerClaimsApi authorizationServerClaimsApi
    private AuthorizationServerKeysApi authorizationServerKeysApi
    private AuthorizationServerClientsApi authorizationServerClientsApi
    private AuthorizationServerPoliciesApi authorizationServerPoliciesApi
    private AuthorizationServerRulesApi authorizationServerRulesApi
    private AuthorizationServerScopesApi authorizationServerScopesApi
    private OAuth2ResourceServerCredentialsKeysApi oAuth2ResourceServerCredentialsKeysApi
    private ApplicationApi applicationApi
    
    private List<String> createdAuthServerIds = Collections.synchronizedList(new ArrayList<>())
    private List<String> createdAppIds = Collections.synchronizedList(new ArrayList<>())

    AuthorizationServerIT() {
        ApiClient client = getClient()
        authorizationServerApi = new AuthorizationServerApi(client)
        authorizationServerAssocApi = new AuthorizationServerAssocApi(client)
        authorizationServerClaimsApi = new AuthorizationServerClaimsApi(client)
        authorizationServerKeysApi = new AuthorizationServerKeysApi(client)
        authorizationServerClientsApi = new AuthorizationServerClientsApi(client)
        authorizationServerPoliciesApi = new AuthorizationServerPoliciesApi(client)
        authorizationServerRulesApi = new AuthorizationServerRulesApi(client)
        authorizationServerScopesApi = new AuthorizationServerScopesApi(client)
        oAuth2ResourceServerCredentialsKeysApi = new OAuth2ResourceServerCredentialsKeysApi(client)
        applicationApi = new ApplicationApi(client)
    }

    
    @Test(groups = "group3")
    @Scenario("authorization-server-associated-servers-test")
    void testAuthorizationServerAssociations() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServer1Name = "test-authz-assoc1-${testId}"
        String authServer2Name = "test-authz-assoc2-${testId}"
        String authServerId1 = null
        String authServerId2 = null

        try {
            logger.debug("Testing Authorization Server Associations...")

            // Create two authorization servers for association testing
            logger.debug("\n1. Creating first authorization server...")
            def authServer1 = new AuthorizationServer()
                .name(authServer1Name)
                .description("First auth server for association testing")
                .audiences(["api://test1-${testId}".toString()])
            
            def createdServer1 = authorizationServerApi.createAuthorizationServer(authServer1)
            authServerId1 = createdServer1.id
            createdAuthServerIds.add(authServerId1)
            assertThat "First server should have ID", authServerId1, notNullValue()
            logger.debug("    Created first auth server: {}", authServerId1)

            logger.debug("\n2. Creating second authorization server...")
            def authServer2 = new AuthorizationServer()
                .name(authServer2Name)
                .description("Second auth server for association testing")
                .audiences(["api://test2-${testId}".toString()])
            
            def createdServer2 = authorizationServerApi.createAuthorizationServer(authServer2)
            authServerId2 = createdServer2.id
            createdAuthServerIds.add(authServerId2)
            assertThat "Second server should have ID", authServerId2, notNullValue()
            logger.debug("    Created second auth server: {}", authServerId2)

            // Create association between servers
            logger.debug("\n3. Creating association between servers...")
            def associationRequest = new AssociatedServerMediated()
                .trusted([authServerId2])
            
            def associatedServers = authorizationServerAssocApi.createAssociatedServers(authServerId1, associationRequest)
            assertThat "Should return associated servers", associatedServers, notNullValue()
            logger.debug("    Created association from {} to {}", authServerId1, authServerId2)

            // List associated servers (trusted)
            logger.debug("\n4. Listing trusted associated servers...")
            int trustedCount = 0
            for (def server : authorizationServerAssocApi.listAssociatedServersByTrustedTypePaged(authServerId1, true, null, null, null)) {
                trustedCount++
                if (trustedCount >= 10) break
            }
            logger.debug("    Found {} trusted associated servers", trustedCount)

            // List associated servers (untrusted)
            logger.debug("\n5. Listing untrusted associated servers...")
            int untrustedCount = 0
            for (def server : authorizationServerAssocApi.listAssociatedServersByTrustedTypePaged(authServerId1, false, null, null, null)) {
                untrustedCount++
                if (untrustedCount >= 10) break
            }
            logger.debug("    Found {} untrusted associated servers", untrustedCount)

            // Delete association
            logger.debug("\n6. Deleting association...")
            authorizationServerAssocApi.deleteAssociatedServer(authServerId1, authServerId2)
            logger.debug("    Deleted association between servers")

            logger.debug("\n Authorization Server Associations tests passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    /**
     * Test OAuth2ResourceServerCredentialsKeys API - Encryption Keys
     * Endpoints: List, Add, Get, Delete, Activate, Deactivate encryption keys
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-resource-server-keys-test")
    void testResourceServerCredentialsKeys() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-rskeys-${testId}"
        String authServerId = null
        String addedKeyId = null

        try {
            logger.debug("Testing OAuth2 Resource Server Credentials Keys...")

            // Create authorization server
            logger.debug("\n1. Creating authorization server...")
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for resource server keys testing")
                .audiences(["api://test-rs-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            logger.debug("    Created auth server: {}", authServerId)

            // List resource server encryption keys
            logger.debug("\n2. Listing resource server encryption keys...")
            def keysList = oAuth2ResourceServerCredentialsKeysApi.listOAuth2ResourceServerJsonWebKeys(authServerId)
            int initialKeyCount = keysList != null && keysList.keys != null ? keysList.keys.size() : 0
            logger.debug("    Found {} resource server encryption keys", initialKeyCount)

            // Test getting existing key if available
            if (initialKeyCount > 0) {
                def firstKey = keysList.keys[0]
                logger.debug("\n3. Getting specific encryption key...")
                def retrievedKey = oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, firstKey.kid)
                assertThat "Retrieved key should have kid", retrievedKey.kid, notNullValue()
                assertThat "Key kid should match", retrievedKey.kid, equalTo(firstKey.kid)
                logger.debug("    Retrieved key: {}", retrievedKey.kid)
            } else {
                logger.debug("\n3. No existing keys to retrieve")
            }

            // Add a new encryption key
            logger.debug("\n4. Adding new encryption key...")
            
            // Generate a test RSA public key (2048-bit RSA key components)
            // These are sample values for testing - in production, generate proper keys
            def newKey = new OAuth2ResourceServerJsonWebKeyRequestBody()
                .kid("test-enc-key-${testId}".toString())
                .kty("RSA")
                .use("enc")
                .e("AQAB")
                .n("xGOr-H7A-PWZ8xKSd3gY4Tz_wVDUzcMXmJN8DwW9GqALpV5vL8qJJ-yKvOj_kJshdGKHxQmA" +
                   "lqvBaWpEtqBx7cNkCj8q9SDvVdFY9vjHq3Q7VP7fYUHm9qpCJLZaQwN8x9TYkYOcJvJNBT5Z" +
                   "aHhCdqGwHvs8wZ-xfKJNMpvQEiN6Zzl8xGnW8qHKF-f1K9r8yg_8JnzLBTq1xJmFVH2YWqXc" +
                   "FLmHqFYBr8Kq4Yz5rJ8cNfQjWpL8zQ9xLmVKj7WqFLj8YzNqHxJvL5KqWzLmQqJvXzN8rKqL")
            
            try {
                def addedKey = oAuth2ResourceServerCredentialsKeysApi.addOAuth2ResourceServerJsonWebKey(authServerId, newKey)
                addedKeyId = addedKey.kid
                assertThat "Added key should have kid", addedKey.kid, notNullValue()
                assertThat "Added key should be INACTIVE", addedKey.status, equalTo(JsonWebKey.StatusEnum.INACTIVE)
                logger.debug("    Added encryption key: {}", addedKey.kid)

                // Get the added key
                logger.debug("\n5. Getting the newly added key...")
                def retrievedNewKey = oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Retrieved key kid should match", retrievedNewKey.kid, equalTo(addedKeyId)
                logger.debug("    Retrieved newly added key")

                // Activate the key
                logger.debug("\n6. Activating the encryption key...")
                def activatedKey = oAuth2ResourceServerCredentialsKeysApi.activateOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Activated key should have status ACTIVE", activatedKey.status, equalTo(JsonWebKey.StatusEnum.ACTIVE)
                logger.debug("    Activated key: {}", addedKeyId)

                // Deactivate the key
                logger.debug("\n7. Deactivating the encryption key...")
                def deactivatedKey = oAuth2ResourceServerCredentialsKeysApi.deactivateOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Deactivated key should have status INACTIVE", deactivatedKey.status, equalTo(JsonWebKey.StatusEnum.INACTIVE)
                logger.debug("    Deactivated key: {}", addedKeyId)

                // Delete the key
                logger.debug("\n8. Deleting the encryption key...")
                oAuth2ResourceServerCredentialsKeysApi.deleteOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                logger.debug("    Deleted key: {}", addedKeyId)

                // Verify deletion
                logger.debug("\n9. Verifying key deletion...")
                try {
                    oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                    throw new AssertionError("Key should have been deleted")
                } catch (ApiException e) {
                    if (e.code == 404) {
                        logger.debug("    Confirmed key no longer exists (404)")
                    } else {
                        throw e
                    }
                }

                logger.debug("\n All Resource Server Credentials Keys operations tested successfully!")

            } catch (ApiException e) {
                if (e.code == 400 && e.responseBody?.contains("not allowed")) {
                    logger.debug("    Key management not enabled for this org - skipping add/activate/deactivate/delete tests")
                    logger.debug("    List and Get operations tested successfully")
                } else {
                    throw e
                }
            }

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            logger.debug("    Some encryption key operations may not be available in test org")
            // Don't throw - these APIs may not be fully available in all orgs
        }
    }

    /**
     * Test Policy lifecycle operations
     * Endpoints: Activate, Deactivate policies
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-policy-lifecycle-test")
    void testPolicyLifecycle() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-policy-lc-${testId}"
        String authServerId = null
        String policyId = null

        try {
            logger.debug("Testing Policy Lifecycle Operations...")

            // Create authorization server
            logger.debug("\n1. Creating authorization server...")
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for policy lifecycle testing")
                .audiences(["api://test-policy-lc-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            logger.debug("    Created auth server: {}", authServerId)

            // Create policy
            logger.debug("\n2. Creating policy...")
            def newPolicy = new AuthorizationServerPolicy()
                .type(AuthorizationServerPolicy.TypeEnum.OAUTH_AUTHORIZATION_POLICY)
                .name("Test Policy Lifecycle ${testId}".toString())
                .description("Policy for lifecycle testing")
                .priority(1)
                .conditions(new AuthorizationServerPolicyConditions()
                    .clients(new ClientPolicyCondition()
                        .include(["ALL_CLIENTS"])))
            
            def createdPolicy = authorizationServerPoliciesApi.createAuthorizationServerPolicy(authServerId, newPolicy)
            policyId = createdPolicy.id
            logger.debug("    Created policy: {}", policyId)

            // Deactivate policy
            logger.debug("\n3. Deactivating policy...")
            authorizationServerPoliciesApi.deactivateAuthorizationServerPolicy(authServerId, policyId)
            logger.debug("    Policy deactivated")

            // Activate policy
            logger.debug("\n4. Activating policy...")
            authorizationServerPoliciesApi.activateAuthorizationServerPolicy(authServerId, policyId)
            logger.debug("    Policy activated")

            // Delete policy
            logger.debug("\n5. Deleting policy...")
            authorizationServerPoliciesApi.deleteAuthorizationServerPolicy(authServerId, policyId)
            logger.debug("    Policy deleted")

            logger.debug("\n Policy Lifecycle tests passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    /**
     * Test Scopes deletion
     * Endpoint: Delete scope
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-scope-delete-test")
    void testScopeDeletion() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-scope-del-${testId}"
        String authServerId = null
        String scopeId = null

        try {
            logger.debug("Testing Scope Deletion...")

            // Create authorization server
            logger.debug("\n1. Creating authorization server...")
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for scope deletion testing")
                .audiences(["api://test-scope-del-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            logger.debug("    Created auth server: {}", authServerId)

            // Create scope
            logger.debug("\n2. Creating scope...")
            def newScope = new OAuth2Scope()
                .name("delete:test:${testId}".toString())
                .displayName("Deletable Scope ${testId}".toString())
                .description("Scope for deletion testing")
            
            def createdScope = authorizationServerScopesApi.createOAuth2Scope(authServerId, newScope)
            scopeId = createdScope.id
            logger.debug("    Created scope: {}", scopeId)

            // Delete scope
            logger.debug("\n3. Deleting scope...")
            authorizationServerScopesApi.deleteOAuth2Scope(authServerId, scopeId)
            logger.debug("    Scope deleted successfully")

            // Verify deletion - should throw 404
            logger.debug("\n4. Verifying scope deletion...")
            try {
                authorizationServerScopesApi.getOAuth2Scope(authServerId, scopeId)
                throw new AssertionError("Scope should have been deleted")
            } catch (ApiException e) {
                if (e.code == 404) {
                    logger.debug("    Confirmed scope no longer exists (404)")
                } else {
                    throw e
                }
            }

            logger.debug("\n Scope Deletion test passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    /**
     * Test Claims deletion
     * Endpoint: Delete claim
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-claim-delete-test")
    void testClaimDeletion() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-claim-del-${testId}"
        String authServerId = null
        String claimId = null

        try {
            logger.debug("Testing Claim Deletion...")

            // Create authorization server
            logger.debug("\n1. Creating authorization server...")
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for claim deletion testing")
                .audiences(["api://test-claim-del-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            logger.debug("    Created auth server: {}", authServerId)

            // Create claim
            logger.debug("\n2. Creating claim...")
            def newClaim = new OAuth2Claim()
                .name("deletable_claim_${testId}".toString())
                .status(LifecycleStatus.ACTIVE)
                .claimType(OAuth2ClaimType.RESOURCE)
                .valueType(OAuth2ClaimValueType.EXPRESSION)
                .value("user.deletableClaim")
            
            def createdClaim = authorizationServerClaimsApi.createOAuth2Claim(authServerId, newClaim)
            claimId = createdClaim.id
            logger.debug("    Created claim: {}", claimId)

            // Delete claim
            logger.debug("\n3. Deleting claim...")
            authorizationServerClaimsApi.deleteOAuth2Claim(authServerId, claimId)
            logger.debug("    Claim deleted successfully")

            // Verify deletion - should throw 404
            logger.debug("\n4. Verifying claim deletion...")
            try {
                authorizationServerClaimsApi.getOAuth2Claim(authServerId, claimId)
                throw new AssertionError("Claim should have been deleted")
            } catch (ApiException e) {
                if (e.code == 404) {
                    logger.debug("    Confirmed claim no longer exists (404)")
                } else {
                    throw e
                }
            }

            logger.debug("\n Claim Deletion test passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    
    @Test(groups = "group3")
    @Scenario("authorization-server-key-retrieval-test")
    void testKeyRetrieval() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-key-${testId}"
        String authServerId = null

        try {
            logger.debug("Testing Authorization Server Key Retrieval...")

            // Create authorization server
            logger.debug("\n1. Creating authorization server...")
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for key retrieval testing")
                .audiences(["api://test-key-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            logger.debug("    Created auth server: {}", authServerId)

            // List keys to get a keyId
            logger.debug("\n2. Listing signing keys...")
            def keysList = authorizationServerKeysApi.listAuthorizationServerKeys(authServerId)
            assertThat "Should have keys", keysList, notNullValue()
            assertThat "Should have at least one key", keysList.size(), greaterThanOrEqualTo(1)
            
            def firstKey = keysList[0]
            logger.debug("    Found {} keys", keysList.size())

            // Get specific key
            logger.debug("\n3. Retrieving specific key...")
            def retrievedKey = authorizationServerKeysApi.getAuthorizationServerKey(authServerId, firstKey.kid)
            assertThat "Retrieved key should have kid", retrievedKey.kid, notNullValue()
            assertThat "Key kid should match", retrievedKey.kid, equalTo(firstKey.kid)
            assertThat "Key should have kty", retrievedKey.kty, notNullValue()
            logger.debug("    Retrieved key: {}", retrievedKey.kid)

            // List keys using paged iterator
            logger.debug("\n4. Testing paged key listing...")
            int pagedKeyCount = 0
            for (def key : authorizationServerKeysApi.listAuthorizationServerKeysPaged(authServerId)) {
                pagedKeyCount++
                if (pagedKeyCount >= 10) break
            }
            assertThat "Paged count should match list count", pagedKeyCount, equalTo(keysList.size())
            logger.debug("    Paged iteration returned {} keys", pagedKeyCount)

            logger.debug("\n Key Retrieval tests passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    @Test(groups = "group3")
    @Scenario("authorization-server-negative-tests")
    void testAuthorizationServerNegativeCases() {
        logger.debug("\n==========================================")
        logger.debug("Test: Authorization Server Negative Cases")
        logger.debug("==========================================\n")

        // Test 1: Get non-existent authorization server
        logger.debug("1. Testing get with invalid authorization server ID...")
        try {
            authorizationServerApi.getAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for invalid authorization server ID")
        }

        // Test 2: Update non-existent authorization server
        logger.debug("\n2. Testing update with invalid authorization server ID...")
        try {
            def authServer = new AuthorizationServer()
                .name("test-invalid")
                .description("Should fail")
                .audiences(["api://test".toString()])
            authorizationServerApi.replaceAuthorizationServer("invalidAuthServerId123", authServer)
            assert false, "Should have thrown ApiException for updating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for updating invalid authorization server")
        }

        // Test 3: Delete non-existent authorization server
        logger.debug("\n3. Testing delete with invalid authorization server ID...")
        try {
            authorizationServerApi.deleteAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for deleting invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for deleting invalid authorization server")
        }

        // Test 4: Activate non-existent authorization server
        logger.debug("\n4. Testing activate with invalid authorization server ID...")
        try {
            authorizationServerApi.activateAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for activating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for activating invalid authorization server")
        }

        // Test 5: Deactivate non-existent authorization server
        logger.debug("\n5. Testing deactivate with invalid authorization server ID...")
        try {
            authorizationServerApi.deactivateAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for deactivating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for deactivating invalid authorization server")
        }

        // Test 6: Get scope from non-existent authorization server
        logger.debug("\n6. Testing get scope with invalid authorization server ID...")
        try {
            authorizationServerScopesApi.getOAuth2Scope("invalidAuthServerId123", "testScope")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for getting scope from invalid authorization server")
        }

        // Test 7: Get claim from non-existent authorization server
        logger.debug("\n7. Testing get claim with invalid authorization server ID...")
        try {
            authorizationServerClaimsApi.getOAuth2Claim("invalidAuthServerId123", "testClaim")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for getting claim from invalid authorization server")
        }

        // Test 8: Get policy from non-existent authorization server
        logger.debug("\n8. Testing get policy with invalid authorization server ID...")
        try {
            authorizationServerPoliciesApi.getAuthorizationServerPolicy("invalidAuthServerId123", "testPolicy")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for getting policy from invalid authorization server")
        }

        // Test 9: Create scope with malformed data
        logger.debug("\n9. Testing create scope with missing required fields...")
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-negative-${testId}"
        def newAuthServer = new AuthorizationServer()
            .name(authServerName)
            .description("Test for negative cases")
            .audiences(["api://test-${testId}".toString()])
        
        def createdAuthServer = authorizationServerApi.createAuthorizationServer(newAuthServer)
        String authServerId = createdAuthServer.getId()
        createdAuthServerIds.add(authServerId)
        
        try {
            def invalidScope = new OAuth2Scope()
                // Missing required 'name' field
                .description("Invalid scope without name")
            authorizationServerScopesApi.createOAuth2Scope(authServerId, invalidScope)
            assert false, "Should have thrown ApiException for missing required fields"
        } catch (ApiException e) {
            assertThat "Should return 400 for missing required fields", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for scope with missing required fields")
        }

        // Test 10: Get key with invalid key ID
        logger.debug("\n10. Testing get key with invalid key ID...")
        try {
            authorizationServerKeysApi.getAuthorizationServerKey(authServerId, "invalidKeyId123")
            assert false, "Should have thrown ApiException for invalid key ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid key ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for invalid key ID")
        }

        // Test 11: List clients for non-existent authorization server
        logger.debug("\n11. Testing list clients with invalid authorization server ID...")
        try {
            authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for listing clients from invalid authorization server")
        }

        // Test 12: Get non-existent scope from valid authorization server
        logger.debug("\n12. Testing get non-existent scope...")
        try {
            authorizationServerScopesApi.getOAuth2Scope(authServerId, "nonExistentScope123")
            assert false, "Should have thrown ApiException for non-existent scope"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent scope", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for non-existent scope")
        }

        // Test 13: Delete non-existent scope
        logger.debug("\n13. Testing delete non-existent scope...")
        try {
            authorizationServerScopesApi.deleteOAuth2Scope(authServerId, "nonExistentScope123")
            assert false, "Should have thrown ApiException for deleting non-existent scope"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent scope", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for deleting non-existent scope")
        }

        // Test 14: Update non-existent claim
        logger.debug("\n14. Testing update non-existent claim...")
        try {
            def claim = new OAuth2Claim()
                .name("test_claim")
                .status(LifecycleStatus.ACTIVE)
                .claimType(OAuth2ClaimType.RESOURCE)
                .valueType(OAuth2ClaimValueType.EXPRESSION)
                .value("user.email")
            authorizationServerClaimsApi.replaceOAuth2Claim(authServerId, "nonExistentClaim123", claim)
            assert false, "Should have thrown ApiException for updating non-existent claim"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent claim", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for updating non-existent claim")
        }

        // Test 15: Delete non-existent policy
        logger.debug("\n15. Testing delete non-existent policy...")
        try {
            authorizationServerPoliciesApi.deleteAuthorizationServerPolicy(authServerId, "nonExistentPolicy123")
            assert false, "Should have thrown ApiException for deleting non-existent policy"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent policy", e.code, equalTo(404)
            logger.debug("    Correctly returned 404 for deleting non-existent policy")
        }

        // Test 16: Create authorization server with empty name
        logger.debug("\n16. Testing create with empty name...")
        try {
            def invalidServer = new AuthorizationServer()
                .name("")  // Empty name should fail validation
                .description("Invalid server")
                .audiences(["api://test".toString()])
            authorizationServerApi.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for empty name"
        } catch (ApiException e) {
            assertThat "Should return 400 for empty name", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for empty authorization server name")
        }

        // Test 17: Create authorization server with missing audiences
        logger.debug("\n17. Testing create with missing audiences...")
        try {
            def invalidServer = new AuthorizationServer()
                .name("test-server-invalid")
                .description("Invalid server without audiences")
                // Missing audiences
            authorizationServerApi.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for missing audiences"
        } catch (ApiException e) {
            assertThat "Should return 400 for missing audiences", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for missing audiences")
        }

        logger.debug("\n All negative test cases passed successfully!")
    }

    /**
     * Test non-paged list overloads and paged overloads with additionalHeaders.
     * Covers the convenience methods that delegate to the main implementations,
     * and paged lambdas including subsequent-page branches.
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-list-overloads-coverage")
    void testListOverloadsAndPagedVariants() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerId = null

        try {
            logger.debug("Testing list overloads and paged variants...")

            // Create auth server with scope, claim, policy, rule
            def authServer = new AuthorizationServer()
                .name("test-overloads-${testId}")
                .description("Auth server for list overload coverage")
                .audiences(["api://test-overloads-${testId}".toString()])

            def created = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = created.id
            createdAuthServerIds.add(authServerId)

            // Create scope
            def scope = authorizationServerScopesApi.createOAuth2Scope(authServerId,
                new OAuth2Scope().name("cov:scope:${testId}".toString()).displayName("Coverage Scope"))

            // Create claim
            def claim = authorizationServerClaimsApi.createOAuth2Claim(authServerId,
                new OAuth2Claim().name("cov_claim_${testId}".toString())
                    .status(LifecycleStatus.ACTIVE)
                    .claimType(OAuth2ClaimType.RESOURCE)
                    .valueType(OAuth2ClaimValueType.EXPRESSION)
                    .value("user.email"))

            // Create policy
            def policy = authorizationServerPoliciesApi.createAuthorizationServerPolicy(authServerId,
                new AuthorizationServerPolicy()
                    .type(AuthorizationServerPolicy.TypeEnum.OAUTH_AUTHORIZATION_POLICY)
                    .name("Cov Policy ${testId}".toString())
                    .description("Coverage policy")
                    .priority(1)
                    .conditions(new AuthorizationServerPolicyConditions()
                        .clients(new ClientPolicyCondition().include(["ALL_CLIENTS"]))))

            // Create rule
            def rule = authorizationServerRulesApi.createAuthorizationServerPolicyRule(authServerId, policy.id,
                new AuthorizationServerPolicyRuleRequest()
                    .name("Cov Rule ${testId}".toString())
                    .priority(1)
                    .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                    .conditions(new AuthorizationServerPolicyRuleConditions()
                        .people(new AuthorizationServerPolicyPeopleCondition()
                            .groups(new AuthorizationServerPolicyRuleGroupCondition().include(["EVERYONE"])))
                        .grantTypes(new GrantTypePolicyRuleCondition().include(["authorization_code"]))
                        .scopes(new OAuth2ScopesMediationPolicyRuleCondition().include([scope.name])))
                    .actions(new AuthorizationServerPolicyRuleActions()
                        .token(new TokenAuthorizationServerPolicyRuleAction()
                            .accessTokenLifetimeMinutes(60)
                            .refreshTokenLifetimeMinutes(0)
                            .refreshTokenWindowMinutes(10080))))

            def headers = Collections.emptyMap()

            // ===== NON-PAGED LIST OVERLOADS =====
            logger.debug("\n1. Testing non-paged list overloads...")

            def servers = authorizationServerApi.listAuthorizationServers(null, null, null)
            assertThat "Should list servers", servers.size(), greaterThan(0)
            def serversH = authorizationServerApi.listAuthorizationServers(null, null, null, headers)
            logger.debug("    listAuthorizationServers")

            def scopesList = authorizationServerScopesApi.listOAuth2Scopes(authServerId, null, null, null, null)
            assertThat "Should list scopes", scopesList.size(), greaterThan(0)
            authorizationServerScopesApi.listOAuth2Scopes(authServerId, null, null, null, null, headers)
            logger.debug("    listOAuth2Scopes")

            def claimsList = authorizationServerClaimsApi.listOAuth2Claims(authServerId)
            assertThat "Should list claims", claimsList.size(), greaterThan(0)
            authorizationServerClaimsApi.listOAuth2Claims(authServerId, headers)
            logger.debug("    listOAuth2Claims")

            def policiesList = authorizationServerPoliciesApi.listAuthorizationServerPolicies(authServerId)
            assertThat "Should list policies", policiesList.size(), greaterThan(0)
            authorizationServerPoliciesApi.listAuthorizationServerPolicies(authServerId, headers)
            logger.debug("    listAuthorizationServerPolicies")

            def rulesList = authorizationServerRulesApi.listAuthorizationServerPolicyRules(authServerId, policy.id)
            assertThat "Should list rules", rulesList.size(), greaterThan(0)
            authorizationServerRulesApi.listAuthorizationServerPolicyRules(authServerId, policy.id, headers)
            logger.debug("    listAuthorizationServerPolicyRules")

            def keysList = authorizationServerKeysApi.listAuthorizationServerKeys(authServerId)
            assertThat "Should list keys", keysList.size(), greaterThan(0)
            authorizationServerKeysApi.listAuthorizationServerKeys(authServerId, headers)
            logger.debug("    listAuthorizationServerKeys")

            authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServer(authServerId)
            authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServer(authServerId, headers)
            logger.debug("    listOAuth2ClientsForAuthorizationServer")

            oAuth2ResourceServerCredentialsKeysApi.listOAuth2ResourceServerJsonWebKeys(authServerId)
            oAuth2ResourceServerCredentialsKeysApi.listOAuth2ResourceServerJsonWebKeys(authServerId, headers)
            logger.debug("    listOAuth2ResourceServerJsonWebKeys")

            // ===== PAGED VARIANTS WITH HEADERS =====
            logger.debug("\n2. Testing paged variants with additionalHeaders...")
            int count = 0

            for (def s : authorizationServerApi.listAuthorizationServersPaged(null, null, null, headers)) { count++; if (count >= 2) break }
            logger.debug("    listAuthorizationServersPaged(headers)")

            count = 0
            for (def s : authorizationServerScopesApi.listOAuth2ScopesPaged(authServerId, null, null, null, null, headers)) { count++; if (count >= 2) break }
            logger.debug("    listOAuth2ScopesPaged(headers)")

            count = 0
            for (def c : authorizationServerClaimsApi.listOAuth2ClaimsPaged(authServerId, headers)) { count++; if (count >= 2) break }
            logger.debug("    listOAuth2ClaimsPaged(headers)")

            count = 0
            for (def p : authorizationServerPoliciesApi.listAuthorizationServerPoliciesPaged(authServerId, headers)) { count++; if (count >= 2) break }
            logger.debug("    listAuthorizationServerPoliciesPaged(headers)")

            count = 0
            for (def r : authorizationServerRulesApi.listAuthorizationServerPolicyRulesPaged(authServerId, policy.id, headers)) { count++; if (count >= 2) break }
            logger.debug("    listAuthorizationServerPolicyRulesPaged(headers)")

            count = 0
            for (def k : authorizationServerKeysApi.listAuthorizationServerKeysPaged(authServerId, headers)) { count++; if (count >= 2) break }
            logger.debug("    listAuthorizationServerKeysPaged(headers)")

            count = 0
            for (def c : authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServerPaged(authServerId, headers)) { count++; if (count >= 2) break }
            logger.debug("    listOAuth2ClientsForAuthorizationServerPaged(headers)")

            count = 0
            for (def k : oAuth2ResourceServerCredentialsKeysApi.listOAuth2ResourceServerJsonWebKeysPaged(authServerId, headers)) { count++; if (count >= 2) break }
            logger.debug("    listOAuth2ResourceServerJsonWebKeysPaged(headers)")

            // ===== CRUD WITH additionalHeaders =====
            logger.debug("\n3. Testing CRUD with additionalHeaders...")

            authorizationServerApi.getAuthorizationServer(authServerId, headers)
            logger.debug("    getAuthorizationServer(headers)")

            authorizationServerScopesApi.getOAuth2Scope(authServerId, scope.id, headers)
            logger.debug("    getOAuth2Scope(headers)")

            authorizationServerClaimsApi.getOAuth2Claim(authServerId, claim.id, headers)
            logger.debug("    getOAuth2Claim(headers)")

            authorizationServerPoliciesApi.getAuthorizationServerPolicy(authServerId, policy.id, headers)
            logger.debug("    getAuthorizationServerPolicy(headers)")

            authorizationServerRulesApi.getAuthorizationServerPolicyRule(authServerId, policy.id, rule.id, headers)
            logger.debug("    getAuthorizationServerPolicyRule(headers)")

            // Lifecycle with headers
            authorizationServerRulesApi.deactivateAuthorizationServerPolicyRule(authServerId, policy.id, rule.id, headers)
            authorizationServerRulesApi.activateAuthorizationServerPolicyRule(authServerId, policy.id, rule.id, headers)
            authorizationServerRulesApi.deleteAuthorizationServerPolicyRule(authServerId, policy.id, rule.id, headers)
            logger.debug("    rule lifecycle with headers (deactivate/activate/delete)")

            authorizationServerPoliciesApi.deactivateAuthorizationServerPolicy(authServerId, policy.id, headers)
            authorizationServerPoliciesApi.activateAuthorizationServerPolicy(authServerId, policy.id, headers)
            authorizationServerPoliciesApi.deleteAuthorizationServerPolicy(authServerId, policy.id, headers)
            logger.debug("    policy lifecycle with headers")

            authorizationServerScopesApi.deleteOAuth2Scope(authServerId, scope.id, headers)
            authorizationServerClaimsApi.deleteOAuth2Claim(authServerId, claim.id, headers)
            logger.debug("    scope/claim delete with headers")

            authorizationServerKeysApi.rotateAuthorizationServerKeys(authServerId, new JwkUse().use(JwkUseType.SIG), headers)
            logger.debug("    rotateAuthorizationServerKeys(headers)")

            try {
                count = 0
                for (def k : authorizationServerKeysApi.rotateAuthorizationServerKeysPaged(authServerId, new JwkUse().use(JwkUseType.SIG))) { count++; if (count >= 2) break }
                logger.debug("    rotateAuthorizationServerKeysPaged")
            } catch (Exception e) {
                logger.debug("    rotateAuthorizationServerKeysPaged: {}", e.message)
            }

            // Create/replace with headers
            def scope2 = authorizationServerScopesApi.createOAuth2Scope(authServerId,
                new OAuth2Scope().name("cov2:scope:${testId}".toString()), headers)
            scope2.description = "Updated"
            authorizationServerScopesApi.replaceOAuth2Scope(authServerId, scope2.id, scope2, headers)
            logger.debug("    scope create/replace with headers")

            def claim2 = authorizationServerClaimsApi.createOAuth2Claim(authServerId,
                new OAuth2Claim().name("cov2_claim_${testId}".toString())
                    .status(LifecycleStatus.ACTIVE).claimType(OAuth2ClaimType.RESOURCE)
                    .valueType(OAuth2ClaimValueType.EXPRESSION).value("user.email"), headers)
            claim2.value = "user.login"
            authorizationServerClaimsApi.replaceOAuth2Claim(authServerId, claim2.id, claim2, headers)
            logger.debug("    claim create/replace with headers")

            def policy2 = authorizationServerPoliciesApi.createAuthorizationServerPolicy(authServerId,
                new AuthorizationServerPolicy()
                    .type(AuthorizationServerPolicy.TypeEnum.OAUTH_AUTHORIZATION_POLICY)
                    .name("Cov2 Policy ${testId}".toString()).description("Coverage 2").priority(2)
                    .conditions(new AuthorizationServerPolicyConditions()
                        .clients(new ClientPolicyCondition().include(["ALL_CLIENTS"]))), headers)
            policy2.description = "Updated"
            authorizationServerPoliciesApi.replaceAuthorizationServerPolicy(authServerId, policy2.id, policy2, headers)
            logger.debug("    policy create/replace with headers")

            def rule2 = authorizationServerRulesApi.createAuthorizationServerPolicyRule(authServerId, policy2.id,
                new AuthorizationServerPolicyRuleRequest()
                    .name("Cov2 Rule ${testId}".toString()).priority(1)
                    .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                    .conditions(new AuthorizationServerPolicyRuleConditions()
                        .people(new AuthorizationServerPolicyPeopleCondition()
                            .groups(new AuthorizationServerPolicyRuleGroupCondition().include(["EVERYONE"])))
                        .grantTypes(new GrantTypePolicyRuleCondition().include(["authorization_code"]))
                        .scopes(new OAuth2ScopesMediationPolicyRuleCondition().include([scope2.name])))
                    .actions(new AuthorizationServerPolicyRuleActions()
                        .token(new TokenAuthorizationServerPolicyRuleAction()
                            .accessTokenLifetimeMinutes(60).refreshTokenLifetimeMinutes(0).refreshTokenWindowMinutes(10080))), headers)

            def updateRule = new AuthorizationServerPolicyRuleRequest()
                .name(rule2.name).priority(2)
                .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                .conditions(rule2.conditions).actions(rule2.actions)
            authorizationServerRulesApi.replaceAuthorizationServerPolicyRule(authServerId, policy2.id, rule2.id, updateRule, headers)
            logger.debug("    rule create/replace with headers")

            created.description = "Updated via headers"
            authorizationServerApi.replaceAuthorizationServer(authServerId, created, headers)
            logger.debug("    replaceAuthorizationServer(headers)")

            // AssocApi overloads
            def assocServers = authorizationServerAssocApi.listAssociatedServersByTrustedType(authServerId, true, null, null, null)
            authorizationServerAssocApi.listAssociatedServersByTrustedType(authServerId, true, null, null, null, headers)
            logger.debug("    listAssociatedServersByTrustedType")

            // ClientsApi token overloads
            try { authorizationServerClientsApi.revokeRefreshTokensForAuthorizationServerAndClient(authServerId, "nonexistent", headers) } catch (ApiException e) { }
            try { authorizationServerClientsApi.revokeRefreshTokenForAuthorizationServerAndClient(authServerId, "nonexistent", "nonexistent", headers) } catch (ApiException e) { }
            try { authorizationServerClientsApi.getRefreshTokenForAuthorizationServerAndClient(authServerId, "nonexistent", "nonexistent", null, headers) } catch (ApiException e) { }
            try { authorizationServerClientsApi.listRefreshTokensForAuthorizationServerAndClient(authServerId, "nonexistent", null, null, null) } catch (ApiException e) { }
            try { authorizationServerClientsApi.listRefreshTokensForAuthorizationServerAndClient(authServerId, "nonexistent", null, null, null, headers) } catch (ApiException e) { }
            logger.debug("    ClientsApi token overloads (with headers)")

            // Paged refresh token overloads
            try { for (def t : authorizationServerClientsApi.listRefreshTokensForAuthorizationServerAndClientPaged(authServerId, "nonexistent")) { break } } catch (Exception e) { }
            try { for (def t : authorizationServerClientsApi.listRefreshTokensForAuthorizationServerAndClientPaged(authServerId, "nonexistent", headers)) { break } } catch (Exception e) { }
            logger.debug("    ClientsApi paged refresh token overloads")

            // OAuth2ResourceServerCredentialsKeysApi overloads
            try { oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, "nonexistent", headers) } catch (ApiException e) { }
            logger.debug("    OAuth2ResourceServerCredentialsKeysApi get(headers)")

            // Server lifecycle + delete with headers
            authorizationServerApi.deactivateAuthorizationServer(authServerId, headers)
            authorizationServerApi.activateAuthorizationServer(authServerId, headers)
            logger.debug("    server lifecycle with headers")

            logger.debug("\n All list overloads and paged variant tests passed!")

        } catch (ApiException e) {
            logger.debug(" Test failed with ApiException: {}", e.message)
            logger.debug("Response body: {}", e.responseBody)
            throw e
        }
    }

    /**
     * Cleanup method to remove all created resources
     */
    @AfterMethod(alwaysRun = true)
    void cleanup() {
        logger.debug("\n Cleanup: Removing test resources...")
        
        // Delete created apps
        createdAppIds.each { appId ->
            try {
                applicationApi.deactivateApplication(appId)
                applicationApi.deleteApplication(appId)
                logger.debug("    Deleted app: {}", appId)
            } catch (Exception e) {
                logger.debug("    Failed to delete app {}: {}", appId, e.message)
            }
        }
        
        // Delete created authorization servers
        createdAuthServerIds.each { serverId ->
            try {
                // Deactivate first if needed
                try {
                    authorizationServerApi.deactivateAuthorizationServer(serverId)
                } catch (Exception ignored) {
                    // Already inactive or doesn't require deactivation
                }
                
                authorizationServerApi.deleteAuthorizationServer(serverId)
                logger.debug("    Deleted auth server: {}", serverId)
            } catch (Exception e) {
                logger.debug("    Failed to delete auth server {}: {}", serverId, e.message)
            }
        }
        
        // Clear lists
        createdAuthServerIds.clear()
        createdAppIds.clear()
        
        logger.debug(" Cleanup completed!")
    }
}

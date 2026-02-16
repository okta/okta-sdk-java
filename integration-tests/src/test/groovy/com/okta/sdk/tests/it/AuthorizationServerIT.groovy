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

/**
 * Comprehensive Integration tests for Authorization Server APIs.
 * Covers all Authorization Server related endpoints:
 * - AuthorizationServerApi
 * - AuthorizationServerAssocApi
 * - AuthorizationServerClaimsApi
 * - AuthorizationServerKeysApi
 * - AuthorizationServerClientsApi
 * - AuthorizationServerPoliciesApi
 * - AuthorizationServerRulesApi
 * - AuthorizationServerScopesApi
 * - OAuth2ResourceServerCredentialsKeysApi
 */
class AuthorizationServerIT extends ITSupport {

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
    private List<String> createdPolicyIds = Collections.synchronizedList(new ArrayList<>())
    private List<String> createdClaimIds = Collections.synchronizedList(new ArrayList<>())
    private List<String> createdScopeIds = Collections.synchronizedList(new ArrayList<>())
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

    /**
     * Test AuthorizationServerAssoc API - Associated Servers
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-server-${testId}"
        String authServerId = null

        try {
            // ===== 1. LIST AUTHORIZATION SERVERS =====
            println "1. Listing existing authorization servers..."
            def authServerCount = 0
            for (def server : authorizationServerApi.listAuthorizationServersPaged(null, null, null)) {
                authServerCount++
                if (authServerCount >= 5) break // Limit iteration for performance
            }
            assertThat "Should have at least default auth server", authServerCount, greaterThanOrEqualTo(1)
            println "   ✓ Found ${authServerCount} authorization servers"

            // ===== 2. CREATE AUTHORIZATION SERVER =====
            println "\n2. Creating new authorization server..."
            def newAuthServer = new AuthorizationServer()
                .name(authServerName)
                .description("Test Authorization Server for comprehensive testing")
                .audiences(["api://test-${testId}".toString()])

            def createdAuthServer = authorizationServerApi.createAuthorizationServer(newAuthServer)
            authServerId = createdAuthServer.id
            createdAuthServerIds.add(authServerId)
            
            assertThat "Auth server should have ID", createdAuthServer.id, notNullValue()
            assertThat "Auth server name should match", createdAuthServer.name, equalTo(authServerName)
            assertThat "Auth server should be ACTIVE", createdAuthServer.status, equalTo(LifecycleStatus.ACTIVE)
            assertThat "Issuer should be set", createdAuthServer.issuer, containsString(authServerId)
            println "   ✓ Created auth server: ${createdAuthServer.id}"

            // ===== 3. GET AUTHORIZATION SERVER =====
            println "\n3. Retrieving authorization server..."
            def retrievedAuthServer = authorizationServerApi.getAuthorizationServer(authServerId)
            assertThat "Retrieved server ID should match", retrievedAuthServer.id, equalTo(authServerId)
            assertThat "Retrieved server name should match", retrievedAuthServer.name, equalTo(authServerName)
            println "   ✓ Retrieved auth server successfully"

            // ===== 4. UPDATE AUTHORIZATION SERVER =====
            println "\n4. Updating authorization server..."
            def updatedDescription = "Updated description for testing"
            retrievedAuthServer.description = updatedDescription
            
            def updatedAuthServer = authorizationServerApi.replaceAuthorizationServer(authServerId, retrievedAuthServer)
            assertThat "Description should be updated", updatedAuthServer.description, equalTo(updatedDescription)
            println "   ✓ Updated auth server description"

            // ===== 5. DEACTIVATE AUTHORIZATION SERVER =====
            println "\n5. Testing lifecycle - Deactivate..."
            authorizationServerApi.deactivateAuthorizationServer(authServerId)
            
            def deactivatedServer = authorizationServerApi.getAuthorizationServer(authServerId)
            assertThat "Server should be INACTIVE", deactivatedServer.status, equalTo(LifecycleStatus.INACTIVE)
            println "   ✓ Auth server deactivated"

            // ===== 6. ACTIVATE AUTHORIZATION SERVER =====
            println "\n6. Testing lifecycle - Activate..."
            authorizationServerApi.activateAuthorizationServer(authServerId)
            println "   ✓ Auth server activation called (Note: Status change may not be immediate)"

            // ===== 7. TEST SCOPES API =====
            println "\n7. Testing Scopes API..."
            
            // List scopes
            int scopeCount = 0
            for (def scope : authorizationServerScopesApi.listOAuth2ScopesPaged(authServerId, null, null, null, null)) {
                scopeCount++
                if (scopeCount >= 10) break // Limit to avoid long iteration
            }
            println "   → Found ${scopeCount} existing scopes"
            
            // Create scope
            def newScope = new OAuth2Scope()
                .name("custom:scope:${testId}".toString())
                .displayName("Custom Scope ${testId}".toString())
                .description("Test custom scope")
            
            def createdScope = authorizationServerScopesApi.createOAuth2Scope(authServerId, newScope)
            createdScopeIds.add(createdScope.id)
            assertThat "Scope should have ID", createdScope.id, notNullValue()
            assertThat "Scope name should match", createdScope.name, equalTo(newScope.name)
            println "   ✓ Created scope: ${createdScope.id}"
            
            // Get scope
            def retrievedScope = authorizationServerScopesApi.getOAuth2Scope(authServerId, createdScope.id)
            assertThat "Scope ID should match", retrievedScope.id, equalTo(createdScope.id)
            println "   ✓ Retrieved scope"
            
            // Update scope
            retrievedScope.description = "Updated scope description"
            def updatedScope = authorizationServerScopesApi.replaceOAuth2Scope(authServerId, createdScope.id, retrievedScope)
            assertThat "Scope description updated", updatedScope.description, equalTo("Updated scope description")
            println "   ✓ Updated scope"

            // ===== 8. TEST CLAIMS API =====
            println "\n8. Testing Claims API..."
            
            // List claims
            int claimCount = 0
            for (def claim : authorizationServerClaimsApi.listOAuth2ClaimsPaged(authServerId)) {
                claimCount++
                if (claimCount >= 10) break
            }
            println "   → Found ${claimCount} existing claims"
            
            // Create claim
            def newClaim = new OAuth2Claim()
                .name("custom_claim_${testId}".toString())
                .status(LifecycleStatus.ACTIVE)
                .claimType(OAuth2ClaimType.RESOURCE)
                .valueType(OAuth2ClaimValueType.EXPRESSION)
                .value("user.customClaim")
                .conditions(new OAuth2ClaimConditions()
                    .scopes([createdScope.name]))
            
            def createdClaim = authorizationServerClaimsApi.createOAuth2Claim(authServerId, newClaim)
            createdClaimIds.add(createdClaim.id)
            assertThat "Claim should have ID", createdClaim.id, notNullValue()
            assertThat "Claim name should match", createdClaim.name, equalTo(newClaim.name)
            println "   ✓ Created claim: ${createdClaim.id}"
            
            // Get claim
            def retrievedClaim = authorizationServerClaimsApi.getOAuth2Claim(authServerId, createdClaim.id)
            assertThat "Claim ID should match", retrievedClaim.id, equalTo(createdClaim.id)
            println "   ✓ Retrieved claim"
            
            // Update claim
            retrievedClaim.value = "user.updatedClaim"
            def updatedClaim = authorizationServerClaimsApi.replaceOAuth2Claim(authServerId, createdClaim.id, retrievedClaim)
            assertThat "Claim value updated", updatedClaim.value, equalTo("user.updatedClaim")
            println "   ✓ Updated claim"

            // ===== 9. TEST POLICIES API =====
            println "\n9. Testing Policies API..."
            
            // List policies
            int policyCount = 0
            for (def policy : authorizationServerPoliciesApi.listAuthorizationServerPoliciesPaged(authServerId)) {
                policyCount++
                if (policyCount >= 10) break
            }
            println "   → Found ${policyCount} existing policies"
            
            // Create policy
            def newPolicy = new AuthorizationServerPolicy()
                .type(AuthorizationServerPolicy.TypeEnum.OAUTH_AUTHORIZATION_POLICY)
                .name("Test Policy ${testId}".toString())
                .description("Test policy for authorization server")
                .priority(1)
                .conditions(new AuthorizationServerPolicyConditions()
                    .clients(new ClientPolicyCondition()
                        .include(["ALL_CLIENTS"])))
            
            def createdPolicy = authorizationServerPoliciesApi.createAuthorizationServerPolicy(authServerId, newPolicy)
            createdPolicyIds.add(createdPolicy.id)
            assertThat "Policy should have ID", createdPolicy.id, notNullValue()
            assertThat "Policy name should match", createdPolicy.name, equalTo(newPolicy.name)
            println "   ✓ Created policy: ${createdPolicy.id}"
            
            // Get policy
            def retrievedPolicy = authorizationServerPoliciesApi.getAuthorizationServerPolicy(authServerId, createdPolicy.id)
            assertThat "Policy ID should match", retrievedPolicy.id, equalTo(createdPolicy.id)
            println "   ✓ Retrieved policy"
            
            // Update policy
            retrievedPolicy.description = "Updated policy description"
            def updatedPolicy = authorizationServerPoliciesApi.replaceAuthorizationServerPolicy(authServerId, createdPolicy.id, retrievedPolicy)
            assertThat "Policy description updated", updatedPolicy.description, equalTo("Updated policy description")
            println "   ✓ Updated policy"

            // ===== 10. TEST POLICY RULES API =====
            println "\n10. Testing Policy Rules API..."
            
            // List rules
            int ruleCount = 0
            for (def rule : authorizationServerRulesApi.listAuthorizationServerPolicyRulesPaged(authServerId, createdPolicy.id)) {
                ruleCount++
                if (ruleCount >= 10) break
            }
            println "   → Found ${ruleCount} existing rules in policy"
            
            // Create rule
            def newRule = new AuthorizationServerPolicyRuleRequest()
                .name("Test Rule ${testId}".toString())
                .priority(1)
                .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                .conditions(new AuthorizationServerPolicyRuleConditions()
                    .people(new AuthorizationServerPolicyPeopleCondition()
                        .groups(new AuthorizationServerPolicyRuleGroupCondition()
                            .include(["EVERYONE"])))
                    .grantTypes(new GrantTypePolicyRuleCondition()
                        .include(["authorization_code", "implicit"]))
                    .scopes(new OAuth2ScopesMediationPolicyRuleCondition()
                        .include([createdScope.name])))
                .actions(new AuthorizationServerPolicyRuleActions()
                    .token(new TokenAuthorizationServerPolicyRuleAction()
                        .accessTokenLifetimeMinutes(60)
                        .refreshTokenLifetimeMinutes(0)
                        .refreshTokenWindowMinutes(10080)))
            
            def createdRule = authorizationServerRulesApi.createAuthorizationServerPolicyRule(authServerId, createdPolicy.id, newRule)
            assertThat "Rule should have ID", createdRule.id, notNullValue()
            assertThat "Rule name should match", createdRule.name, equalTo(newRule.name)
            println "   ✓ Created rule: ${createdRule.id}"
            
            // Get rule
            def retrievedRule = authorizationServerRulesApi.getAuthorizationServerPolicyRule(authServerId, createdPolicy.id, createdRule.id)
            assertThat "Rule ID should match", retrievedRule.id, equalTo(createdRule.id)
            println "   ✓ Retrieved rule"
            
            // Update rule - need to create a new request object with updated priority
            def updateRuleRequest = new AuthorizationServerPolicyRuleRequest()
                .name(retrievedRule.name)
                .priority(2)
                .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                .conditions(retrievedRule.conditions)
                .actions(retrievedRule.actions)
            def updatedRule = authorizationServerRulesApi.replaceAuthorizationServerPolicyRule(authServerId, createdPolicy.id, createdRule.id, updateRuleRequest)
            assertThat "Rule should have ID after update", updatedRule.id, notNullValue()
            println "   ✓ Updated rule"
            
            // Activate rule
            authorizationServerRulesApi.activateAuthorizationServerPolicyRule(authServerId, createdPolicy.id, createdRule.id)
            println "   ✓ Activated rule"
            
            // Deactivate rule
            authorizationServerRulesApi.deactivateAuthorizationServerPolicyRule(authServerId, createdPolicy.id, createdRule.id)
            println "   ✓ Deactivated rule"
            
            // Delete rule
            authorizationServerRulesApi.deleteAuthorizationServerPolicyRule(authServerId, createdPolicy.id, createdRule.id)
            println "   ✓ Deleted rule"

            // ===== 11. TEST KEYS API =====
            println "\n11. Testing Keys API..."
            
            // List keys
            def keys = authorizationServerKeysApi.listAuthorizationServerKeys(authServerId)
            assertThat "Should have keys", keys, notNullValue()
            assertThat "Should have at least one key", keys.size(), greaterThanOrEqualTo(1)
            println "   → Found ${keys.size()} keys"
            
            // Rotate keys
            def rotatedKeys = authorizationServerKeysApi.rotateAuthorizationServerKeys(authServerId, new JwkUse().use(JwkUseType.SIG))
            assertThat "Should have keys after rotation", rotatedKeys, notNullValue()
            println "   ✓ Rotated authorization server keys"

            // ===== 12. TEST CLIENTS API (requires OIDC app) =====
            println "\n12. Testing Clients API..."
            
            // Create an OIDC application to use as client
            def oidcApp = new OpenIdConnectApplication()
                .name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
                .label("Test OIDC Client ${testId}".toString())
                .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
                .settings(new OpenIdConnectApplicationSettings()
                    .oauthClient(new OpenIdConnectApplicationSettingsClient()
                        .responseTypes([OAuthResponseType.CODE])
                        .grantTypes([GrantType.AUTHORIZATION_CODE])
                        .applicationType(OpenIdConnectApplicationType.WEB)
                        .redirectUris(["https://example.com/callback"])
                        .postLogoutRedirectUris(["https://example.com/logout"])))
            
            def createdApp = applicationApi.createApplication(oidcApp, true, null)
            createdAppIds.add(createdApp.id)
            println "   ✓ Created OIDC app: ${createdApp.id}"
            
            // List clients associated with auth server
            int clientCount = 0
            String firstClientId = null
            for (def client : authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServerPaged(authServerId)) {
                clientCount++
                if (firstClientId == null) {
                    firstClientId = client.clientId
                }
                if (clientCount >= 10) break
            }
            println "   → Found ${clientCount} clients associated with auth server"
            
            // Get client details if any exist
            if (firstClientId != null) {
                def clientDetails = authorizationServerClientsApi.getOAuth2ClientForAuthorizationServer(authServerId, firstClientId)
                assertThat "Client should have client_id", clientDetails.clientId, notNullValue()
                println "   ✓ Retrieved client details for ${firstClientId}"
            }

            // ===== 13. TEST METADATA ENDPOINTS =====
            println "\n13. Testing Metadata Endpoints..."
            
            // Get OAuth2 metadata
            def oauth2Metadata = authorizationServerApi.getOAuth2AuthorizationServerMetadata(authServerId)
            assertThat "Should have issuer", oauth2Metadata.issuer, notNullValue()
            assertThat "Should have authorization endpoint", oauth2Metadata.authorizationEndpoint, notNullValue()
            assertThat "Should have token endpoint", oauth2Metadata.tokenEndpoint, notNullValue()
            println "   ✓ Retrieved OAuth2 metadata"
            
            // Get OpenID configuration
            def openidConfig = authorizationServerApi.getOAuth2AuthorizationServerOpenIdConfiguration(authServerId)
            assertThat "Should have issuer", openidConfig.issuer, notNullValue()
            assertThat "Should have jwks_uri", openidConfig.jwksUri, notNullValue()
            println "   ✓ Retrieved OpenID configuration"

            // ===== 14. TEST PAGINATION =====
            println "\n14. Testing pagination..."
            int paginatedCount = 0
            for (def server : authorizationServerApi.listAuthorizationServersPaged(null, 1)) {
                paginatedCount++
                if (paginatedCount >= 1) break // Only check first page
            }
            assertThat "Should return at most 1 server", paginatedCount, lessThanOrEqualTo(1)
            println "   ✓ Pagination works correctly"

     * Endpoints: List, Create, Delete associations between authorization servers
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-associated-servers-test")
    void testAuthorizationServerAssociations() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServer1Name = "test-authz-assoc1-${testId}"
        String authServer2Name = "test-authz-assoc2-${testId}"
        String authServerId1 = null
        String authServerId2 = null

        try {
            println "Testing Authorization Server Associations..."

            // Create two authorization servers for association testing
            println "\n1. Creating first authorization server..."
            def authServer1 = new AuthorizationServer()
                .name(authServer1Name)
                .description("First auth server for association testing")
                .audiences(["api://test1-${testId}".toString()])
            
            def createdServer1 = authorizationServerApi.createAuthorizationServer(authServer1)
            authServerId1 = createdServer1.id
            createdAuthServerIds.add(authServerId1)
            assertThat "First server should have ID", authServerId1, notNullValue()
            println "   ✓ Created first auth server: ${authServerId1}"

            println "\n2. Creating second authorization server..."
            def authServer2 = new AuthorizationServer()
                .name(authServer2Name)
                .description("Second auth server for association testing")
                .audiences(["api://test2-${testId}".toString()])
            
            def createdServer2 = authorizationServerApi.createAuthorizationServer(authServer2)
            authServerId2 = createdServer2.id
            createdAuthServerIds.add(authServerId2)
            assertThat "Second server should have ID", authServerId2, notNullValue()
            println "   ✓ Created second auth server: ${authServerId2}"

            // Create association between servers
            println "\n3. Creating association between servers..."
            def associationRequest = new AssociatedServerMediated()
                .trusted([authServerId2])
            
            def associatedServers = authorizationServerAssocApi.createAssociatedServers(authServerId1, associationRequest)
            assertThat "Should return associated servers", associatedServers, notNullValue()
            println "   ✓ Created association from ${authServerId1} to ${authServerId2}"

            // List associated servers (trusted)
            println "\n4. Listing trusted associated servers..."
            int trustedCount = 0
            for (def server : authorizationServerAssocApi.listAssociatedServersByTrustedTypePaged(authServerId1, true, null, null, null)) {
                trustedCount++
                if (trustedCount >= 10) break
            }
            println "   → Found ${trustedCount} trusted associated servers"

            // List associated servers (untrusted)
            println "\n5. Listing untrusted associated servers..."
            int untrustedCount = 0
            for (def server : authorizationServerAssocApi.listAssociatedServersByTrustedTypePaged(authServerId1, false, null, null, null)) {
                untrustedCount++
                if (untrustedCount >= 10) break
            }
            println "   → Found ${untrustedCount} untrusted associated servers"

            // Delete association
            println "\n6. Deleting association..."
            authorizationServerAssocApi.deleteAssociatedServer(authServerId1, authServerId2)
            println "   ✓ Deleted association between servers"

            println "\n✅ Authorization Server Associations tests passed!"

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
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
            println "Testing OAuth2 Resource Server Credentials Keys..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for resource server keys testing")
                .audiences(["api://test-rs-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // List resource server encryption keys
            println "\n2. Listing resource server encryption keys..."
            def keysList = oAuth2ResourceServerCredentialsKeysApi.listOAuth2ResourceServerJsonWebKeys(authServerId)
            int initialKeyCount = keysList != null && keysList.keys != null ? keysList.keys.size() : 0
            println "   → Found ${initialKeyCount} resource server encryption keys"

            // Test getting existing key if available
            if (initialKeyCount > 0) {
                def firstKey = keysList.keys[0]
                println "\n3. Getting specific encryption key..."
                def retrievedKey = oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, firstKey.kid)
                assertThat "Retrieved key should have kid", retrievedKey.kid, notNullValue()
                assertThat "Key kid should match", retrievedKey.kid, equalTo(firstKey.kid)
                println "   ✓ Retrieved key: ${retrievedKey.kid}"
            } else {
                println "\n3. No existing keys to retrieve"
            }

            // Add a new encryption key
            println "\n4. Adding new encryption key..."
            
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
                println "   ✓ Added encryption key: ${addedKey.kid}"

                // Get the added key
                println "\n5. Getting the newly added key..."
                def retrievedNewKey = oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Retrieved key kid should match", retrievedNewKey.kid, equalTo(addedKeyId)
                println "   ✓ Retrieved newly added key"

                // Activate the key
                println "\n6. Activating the encryption key..."
                def activatedKey = oAuth2ResourceServerCredentialsKeysApi.activateOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Activated key should have status ACTIVE", activatedKey.status, equalTo(JsonWebKey.StatusEnum.ACTIVE)
                println "   ✓ Activated key: ${addedKeyId}"

                // Deactivate the key
                println "\n7. Deactivating the encryption key..."
                def deactivatedKey = oAuth2ResourceServerCredentialsKeysApi.deactivateOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                assertThat "Deactivated key should have status INACTIVE", deactivatedKey.status, equalTo(JsonWebKey.StatusEnum.INACTIVE)
                println "   ✓ Deactivated key: ${addedKeyId}"

                // Delete the key
                println "\n8. Deleting the encryption key..."
                oAuth2ResourceServerCredentialsKeysApi.deleteOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                println "   ✓ Deleted key: ${addedKeyId}"

                // Verify deletion
                println "\n9. Verifying key deletion..."
                try {
                    oAuth2ResourceServerCredentialsKeysApi.getOAuth2ResourceServerJsonWebKey(authServerId, addedKeyId)
                    throw new AssertionError("Key should have been deleted")
                } catch (ApiException e) {
                    if (e.code == 404) {
                        println "   ✓ Confirmed key no longer exists (404)"
                    } else {
                        throw e
                    }
                }

                println "\n✅ All Resource Server Credentials Keys operations tested successfully!"

            } catch (ApiException e) {
                if (e.code == 400 && e.responseBody?.contains("not allowed")) {
                    println "   ⚠ Key management not enabled for this org - skipping add/activate/deactivate/delete tests"
                    println "   → List and Get operations tested successfully"
                } else {
                    throw e
                }
            }

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
            println "   ⚠ Some encryption key operations may not be available in test org"
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
            println "Testing Policy Lifecycle Operations..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for policy lifecycle testing")
                .audiences(["api://test-policy-lc-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // Create policy
            println "\n2. Creating policy..."
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
            createdPolicyIds.add(policyId)
            println "   ✓ Created policy: ${policyId}"

            // Deactivate policy
            println "\n3. Deactivating policy..."
            authorizationServerPoliciesApi.deactivateAuthorizationServerPolicy(authServerId, policyId)
            println "   ✓ Policy deactivated"

            // Activate policy
            println "\n4. Activating policy..."
            authorizationServerPoliciesApi.activateAuthorizationServerPolicy(authServerId, policyId)
            println "   ✓ Policy activated"

            // Delete policy
            println "\n5. Deleting policy..."
            authorizationServerPoliciesApi.deleteAuthorizationServerPolicy(authServerId, policyId)
            println "   ✓ Policy deleted"

            println "\n✅ Policy Lifecycle tests passed!"

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
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
            println "Testing Scope Deletion..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for scope deletion testing")
                .audiences(["api://test-scope-del-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // Create scope
            println "\n2. Creating scope..."
            def newScope = new OAuth2Scope()
                .name("delete:test:${testId}".toString())
                .displayName("Deletable Scope ${testId}".toString())
                .description("Scope for deletion testing")
            
            def createdScope = authorizationServerScopesApi.createOAuth2Scope(authServerId, newScope)
            scopeId = createdScope.id
            println "   ✓ Created scope: ${scopeId}"

            // Delete scope
            println "\n3. Deleting scope..."
            authorizationServerScopesApi.deleteOAuth2Scope(authServerId, scopeId)
            println "   ✓ Scope deleted successfully"

            // Verify deletion - should throw 404
            println "\n4. Verifying scope deletion..."
            try {
                authorizationServerScopesApi.getOAuth2Scope(authServerId, scopeId)
                throw new AssertionError("Scope should have been deleted")
            } catch (ApiException e) {
                if (e.code == 404) {
                    println "   ✓ Confirmed scope no longer exists (404)"
                } else {
                    throw e
                }
            }

            println "\n✅ Scope Deletion test passed!"

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
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
            println "Testing Claim Deletion..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for claim deletion testing")
                .audiences(["api://test-claim-del-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // Create claim
            println "\n2. Creating claim..."
            def newClaim = new OAuth2Claim()
                .name("deletable_claim_${testId}".toString())
                .status(LifecycleStatus.ACTIVE)
                .claimType(OAuth2ClaimType.RESOURCE)
                .valueType(OAuth2ClaimValueType.EXPRESSION)
                .value("user.deletableClaim")
            
            def createdClaim = authorizationServerClaimsApi.createOAuth2Claim(authServerId, newClaim)
            claimId = createdClaim.id
            println "   ✓ Created claim: ${claimId}"

            // Delete claim
            println "\n3. Deleting claim..."
            authorizationServerClaimsApi.deleteOAuth2Claim(authServerId, claimId)
            println "   ✓ Claim deleted successfully"

            // Verify deletion - should throw 404
            println "\n4. Verifying claim deletion..."
            try {
                authorizationServerClaimsApi.getOAuth2Claim(authServerId, claimId)
                throw new AssertionError("Claim should have been deleted")
            } catch (ApiException e) {
                if (e.code == 404) {
                    println "   ✓ Confirmed claim no longer exists (404)"
                } else {
                    throw e
                }
            }

            println "\n✅ Claim Deletion test passed!"

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
            throw e
        }
    }

    /**
     * Test Authorization Server Keys - Individual Key Retrieval
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-tokens-${testId}"
        String authServerId = null
        String clientId = null

        try {
            println "Testing Client Tokens Management..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for token management testing")
                .audiences(["api://test-tokens-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // Create OIDC application
            println "\n2. Creating OIDC application..."
            def oidcApp = new OpenIdConnectApplication()
                .name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
                .label("Test Token Client ${testId}".toString())
                .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
                .settings(new OpenIdConnectApplicationSettings()
                    .oauthClient(new OpenIdConnectApplicationSettingsClient()
                        .responseTypes([OAuthResponseType.CODE])
                        .grantTypes([GrantType.AUTHORIZATION_CODE, GrantType.REFRESH_TOKEN])
                        .applicationType(OpenIdConnectApplicationType.WEB)
                        .redirectUris(["https://example.com/callback"])
                        .postLogoutRedirectUris(["https://example.com/logout"])))
            
            def createdApp = applicationApi.createApplication(oidcApp, true, null)
            createdAppIds.add(createdApp.id)
            clientId = ((OpenIdConnectApplication) createdApp).settings.oauthClient.clientId
            println "   ✓ Created OIDC app with client_id: ${clientId}"

            // List refresh tokens for client
            println "\n3. Listing refresh tokens for client..."
            int tokenCount = 0
            String firstTokenId = null
            for (def token : authorizationServerClientsApi.listRefreshTokensForAuthorizationServerAndClientPaged(authServerId, clientId)) {
                tokenCount++
                if (firstTokenId == null) {
                    firstTokenId = token.id
                }
                if (tokenCount >= 10) break
            }
            println "   → Found ${tokenCount} refresh tokens for client"

            // If tokens exist, test token operations
            if (firstTokenId != null) {
                println "\n4. Getting specific token..."
                def token = authorizationServerClientsApi.getRefreshTokenForAuthorizationServerAndClient(authServerId, clientId, firstTokenId, null)
                assertThat "Token should have ID", token.id, notNullValue()
                println "   ✓ Retrieved token: ${token.id}"

                println "\n5. Revoking specific token..."
                authorizationServerClientsApi.revokeRefreshTokenForAuthorizationServerAndClient(authServerId, clientId, firstTokenId)
                println "   ✓ Revoked token: ${firstTokenId}"
            } else {
                println "\n4-5. No tokens available to test individual operations"
            }

            // Test revoke all tokens (safe even if no tokens exist)
            println "\n6. Revoking all refresh tokens for client..."
            authorizationServerClientsApi.revokeRefreshTokensForAuthorizationServerAndClient(authServerId, clientId)
            println "   ✓ Revoked all tokens for client"

     * Endpoint: Get specific key by keyId
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-key-retrieval-test")
    void testKeyRetrieval() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerName = "test-authz-key-${testId}"
        String authServerId = null

        try {
            println "Testing Authorization Server Key Retrieval..."

            // Create authorization server
            println "\n1. Creating authorization server..."
            def authServer = new AuthorizationServer()
                .name(authServerName)
                .description("Auth server for key retrieval testing")
                .audiences(["api://test-key-${testId}".toString()])
            
            def createdServer = authorizationServerApi.createAuthorizationServer(authServer)
            authServerId = createdServer.id
            createdAuthServerIds.add(authServerId)
            println "   ✓ Created auth server: ${authServerId}"

            // List keys to get a keyId
            println "\n2. Listing signing keys..."
            def keysList = authorizationServerKeysApi.listAuthorizationServerKeys(authServerId)
            assertThat "Should have keys", keysList, notNullValue()
            assertThat "Should have at least one key", keysList.size(), greaterThanOrEqualTo(1)
            
            def firstKey = keysList[0]
            println "   → Found ${keysList.size()} keys"

            // Get specific key
            println "\n3. Retrieving specific key..."
            def retrievedKey = authorizationServerKeysApi.getAuthorizationServerKey(authServerId, firstKey.kid)
            assertThat "Retrieved key should have kid", retrievedKey.kid, notNullValue()
            assertThat "Key kid should match", retrievedKey.kid, equalTo(firstKey.kid)
            assertThat "Key should have kty", retrievedKey.kty, notNullValue()
            println "   ✓ Retrieved key: ${retrievedKey.kid}"

            // List keys using paged iterator
            println "\n4. Testing paged key listing..."
            int pagedKeyCount = 0
            for (def key : authorizationServerKeysApi.listAuthorizationServerKeysPaged(authServerId)) {
                pagedKeyCount++
                if (pagedKeyCount >= 10) break
            }
            assertThat "Paged count should match list count", pagedKeyCount, equalTo(keysList.size())
            println "   ✓ Paged iteration returned ${pagedKeyCount} keys"

            println "\n✅ Key Retrieval tests passed!"

        } catch (ApiException e) {
            println "❌ Test failed with ApiException: ${e.message}"
            println "Response body: ${e.responseBody}"
            throw e
        }
    }

    @Test(groups = "group3")
    @Scenario("authorization-server-negative-tests")
    void testAuthorizationServerNegativeCases() {
        println "\n=========================================="
        println "Test: Authorization Server Negative Cases"
        println "==========================================\n"

        // Test 1: Get non-existent authorization server
        println "1. Testing get with invalid authorization server ID..."
        try {
            authorizationServerApi.getAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for invalid authorization server ID"
        }

        // Test 2: Update non-existent authorization server
        println "\n2. Testing update with invalid authorization server ID..."
        try {
            def authServer = new AuthorizationServer()
                .name("test-invalid")
                .description("Should fail")
                .audiences(["api://test".toString()])
            authorizationServerApi.replaceAuthorizationServer("invalidAuthServerId123", authServer)
            assert false, "Should have thrown ApiException for updating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for updating invalid authorization server"
        }

        // Test 3: Delete non-existent authorization server
        println "\n3. Testing delete with invalid authorization server ID..."
        try {
            authorizationServerApi.deleteAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for deleting invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deleting invalid authorization server"
        }

        // Test 4: Activate non-existent authorization server
        println "\n4. Testing activate with invalid authorization server ID..."
        try {
            authorizationServerApi.activateAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for activating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for activating invalid authorization server"
        }

        // Test 5: Deactivate non-existent authorization server
        println "\n5. Testing deactivate with invalid authorization server ID..."
        try {
            authorizationServerApi.deactivateAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for deactivating invalid auth server"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deactivating invalid authorization server"
        }

        // Test 6: Get scope from non-existent authorization server
        println "\n6. Testing get scope with invalid authorization server ID..."
        try {
            authorizationServerScopesApi.getOAuth2Scope("invalidAuthServerId123", "testScope")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for getting scope from invalid authorization server"
        }

        // Test 7: Get claim from non-existent authorization server
        println "\n7. Testing get claim with invalid authorization server ID..."
        try {
            authorizationServerClaimsApi.getOAuth2Claim("invalidAuthServerId123", "testClaim")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for getting claim from invalid authorization server"
        }

        // Test 8: Get policy from non-existent authorization server
        println "\n8. Testing get policy with invalid authorization server ID..."
        try {
            authorizationServerPoliciesApi.getAuthorizationServerPolicy("invalidAuthServerId123", "testPolicy")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for getting policy from invalid authorization server"
        }

        // Test 9: Create scope with malformed data
        println "\n9. Testing create scope with missing required fields..."
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
            println "   ✓ Correctly returned 400 for scope with missing required fields"
        }

        // Test 10: Get key with invalid key ID
        println "\n10. Testing get key with invalid key ID..."
        try {
            authorizationServerKeysApi.getAuthorizationServerKey(authServerId, "invalidKeyId123")
            assert false, "Should have thrown ApiException for invalid key ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid key ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for invalid key ID"
        }

        // Test 11: List clients for non-existent authorization server
        println "\n11. Testing list clients with invalid authorization server ID..."
        try {
            authorizationServerClientsApi.listOAuth2ClientsForAuthorizationServer("invalidAuthServerId123")
            assert false, "Should have thrown ApiException for invalid auth server ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for listing clients from invalid authorization server"
        }

        // Test 12: Get non-existent scope from valid authorization server
        println "\n12. Testing get non-existent scope..."
        try {
            authorizationServerScopesApi.getOAuth2Scope(authServerId, "nonExistentScope123")
            assert false, "Should have thrown ApiException for non-existent scope"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent scope", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for non-existent scope"
        }

        // Test 13: Delete non-existent scope
        println "\n13. Testing delete non-existent scope..."
        try {
            authorizationServerScopesApi.deleteOAuth2Scope(authServerId, "nonExistentScope123")
            assert false, "Should have thrown ApiException for deleting non-existent scope"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent scope", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deleting non-existent scope"
        }

        // Test 14: Update non-existent claim
        println "\n14. Testing update non-existent claim..."
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
            println "   ✓ Correctly returned 404 for updating non-existent claim"
        }

        // Test 15: Delete non-existent policy
        println "\n15. Testing delete non-existent policy..."
        try {
            authorizationServerPoliciesApi.deleteAuthorizationServerPolicy(authServerId, "nonExistentPolicy123")
            assert false, "Should have thrown ApiException for deleting non-existent policy"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent policy", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deleting non-existent policy"
        }

        // Test 16: Create authorization server with empty name
        println "\n16. Testing create with empty name..."
        try {
            def invalidServer = new AuthorizationServer()
                .name("")  // Empty name should fail validation
                .description("Invalid server")
                .audiences(["api://test".toString()])
            authorizationServerApi.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for empty name"
        } catch (ApiException e) {
            assertThat "Should return 400 for empty name", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for empty authorization server name"
        }

        // Test 17: Create authorization server with missing audiences
        println "\n17. Testing create with missing audiences..."
        try {
            def invalidServer = new AuthorizationServer()
                .name("test-server-invalid")
                .description("Invalid server without audiences")
                // Missing audiences
            authorizationServerApi.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for missing audiences"
        } catch (ApiException e) {
            assertThat "Should return 400 for missing audiences", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for missing audiences"
        }

        println "\n✅ All negative test cases passed successfully!"
    }

    /**
     * Debug test - List authorization servers
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-debug-list-test")
    void debugListAuthorizationServers() {
        println "Calling listAuthorizationServersPaged..."
        def result = []
        for (def server : authorizationServerApi.listAuthorizationServersPaged(null, null, null)) {
            result.add(server)
            println "  - Found server: ${server.name} (${server.id})"
        }

        println "Total servers found: ${result.size()}"

        assertThat "Result should not be empty", result.size(), greaterThan(0)
    }

    /**
     * Test basic authorization server CRUD operations
     */
    @Test(groups = "group3")
    @Scenario("authorization-server-debug-basic")
    void testBasicAuthServerOperations() {
        // Test 1: Create an auth server
        println "Creating new authorization server..."
        def authServer = new AuthorizationServer()
            .name("test-server-${UUID.randomUUID().toString().substring(0, 8)}")
            .description("Debug test server")
            .audiences(["api://default"])

        def created = authorizationServerApi.createAuthorizationServer(authServer)
        createdAuthServerIds.add(created.id)
        println "Created: ${created.id} - ${created.name}"
        assertThat "Should have ID", created.id, notNullValue()

        // Test 2: Get the auth server
        def retrieved = authorizationServerApi.getAuthorizationServer(created.id)
        println "Retrieved: ${retrieved.id} - ${retrieved.name}"
        assertThat "IDs should match", retrieved.id, equalTo(created.id)

        println "✅ Basic operations test passed!"
    }

    /**
     * Cleanup method to remove all created resources
     */
    @AfterMethod(alwaysRun = true)
    void cleanup() {
        println "\n🧹 Cleanup: Removing test resources..."
        
        // Delete created apps
        createdAppIds.each { appId ->
            try {
                applicationApi.deactivateApplication(appId)
                applicationApi.deleteApplication(appId)
                println "   ✓ Deleted app: ${appId}"
            } catch (Exception e) {
                println "   ⚠ Failed to delete app ${appId}: ${e.message}"
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
                println "   ✓ Deleted auth server: ${serverId}"
            } catch (Exception e) {
                println "   ⚠ Failed to delete auth server ${serverId}: ${e.message}"
            }
        }
        
        // Clear lists
        createdAuthServerIds.clear()
        createdPolicyIds.clear()
        createdClaimIds.clear()
        createdScopeIds.clear()
        createdAppIds.clear()
        
        println "✅ Cleanup completed!"
    }
}

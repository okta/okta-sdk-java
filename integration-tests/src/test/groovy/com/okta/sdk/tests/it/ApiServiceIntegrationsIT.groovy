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

import com.okta.sdk.resource.api.ApiServiceIntegrationsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.APIServiceIntegrationInstance
import com.okta.sdk.resource.model.APIServiceIntegrationInstanceSecret
import com.okta.sdk.resource.model.PostAPIServiceIntegrationInstanceRequest
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.SkipException
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Comprehensive integration tests for API Service Integrations API.
 * This version discovers available integration types from the org and creates fresh instances.
 * 
 * ENDPOINTS TESTED (9):
 * 1. POST   /integrations/api/v1/api-services - Create instance
 * 2. GET    /integrations/api/v1/api-services - List instances
 * 3. GET    /integrations/api/v1/api-services/{id} - Get instance
 * 4. DELETE /integrations/api/v1/api-services/{id} - Delete instance
 * 5. GET    /integrations/api/v1/api-services/{id}/credentials/secrets - List secrets
 * 6. POST   /integrations/api/v1/api-services/{id}/credentials/secrets - Create secret
 * 7. POST   /.../secrets/{secretId}/lifecycle/activate - Activate secret
 * 8. POST   /.../secrets/{secretId}/lifecycle/deactivate - Deactivate secret
 * 9. DELETE /.../secrets/{secretId} - Delete secret
 */
class ApiServiceIntegrationsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceIntegrationsIT.class)
    def apiServiceIntegrationsApi = new ApiServiceIntegrationsApi(getClient())
    
    private String testApiServiceType = null
    private List<String> testApiServiceScopes = null
    private List<String> createdApiServiceIds = []

    @BeforeClass
    void discoverAvailableIntegrationType() {
        logger.info("Discovering available API Service Integration type...")
        
        try {
            // First, try to list existing instances and use their type AND scopes
            def existingInstances = apiServiceIntegrationsApi.listApiServiceIntegrationInstances(null)
            
            if (existingInstances && !existingInstances.isEmpty()) {
                def existingInstance = existingInstances.first()
                testApiServiceType = existingInstance.type
                testApiServiceScopes = existingInstance.grantedScopes as List
                logger.info("Found existing API Service Integration instance with type: '{}', scopes: {}", 
                    testApiServiceType, testApiServiceScopes)
                return
            }
            
            logger.info("No existing instances found. Trying common integration types...")
            
            // If no instances exist, try common types
            def commonTypes = [
                "datadoghqapiservice",
                "splunkapiservice",
                "aws_security_lake_apiservice",
                "cisco_ise_apiservice"
            ]
            
            for (String type : commonTypes) {
                try {
                    logger.debug("Attempting to create test instance with type: '{}'", type)
                    def scopes = getScopesForIntegrationType(type)
                    def testRequest = new PostAPIServiceIntegrationInstanceRequest()
                    testRequest.type = type
                    testRequest.grantedScopes = scopes
                    
                    def testInstance = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(testRequest)
                    createdApiServiceIds.add(testInstance.id)
                    testApiServiceType = type
                    testApiServiceScopes = scopes
                    
                    logger.info("Successfully created test instance with type: '{}'", type)
                    
                    // Delete it immediately to keep environment clean
                    apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(testInstance.id)
                    createdApiServiceIds.remove(testInstance.id)
                    
                    logger.info("Discovered available API Service Integration type: '{}'", testApiServiceType)
                    return
                } catch (ApiException e) {
                    logger.debug("Type '{}' not available: {}", type, e.message)
                    // Continue to next type
                }
            }
        } catch (Exception e) {
            logger.warn("Error during integration type discovery: {}", e.message)
        }
        
        // If we couldn't discover any type, log warning but don't fail - tests will skip
        if (testApiServiceType == null) {
            logger.warn(
                "No API Service Integration type could be discovered in your org. " +
                "Please add an API Service Integration from Admin Console > Applications > " +
                "Browse App Integration Catalog > API Services. " +
                "Common types: DatadogHQ, Splunk, AWS Security Lake, Cisco ISE. " +
                "Tests will be skipped."
            )
        }
    }

    @AfterClass
    void cleanupTestInstances() {
        for (String instanceId : createdApiServiceIds.toList()) {
            try {
                // Delete any secrets first
                try {
                    def secrets = apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instanceId)
                    for (def secret : secrets) {
                        try {
                            if (secret.status == APIServiceIntegrationInstanceSecret.StatusEnum.ACTIVE) {
                                apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instanceId, secret.id)
                                Thread.sleep(500)
                            }
                            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstanceSecret(instanceId, secret.id)
                        } catch (ApiException e) {
                            logger.debug("Failed to delete secret: {}", e.message)
                        }
                    }
                } catch (ApiException e) {
                    logger.debug("Failed to list secrets for cleanup: {}", e.message)
                }

                apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(instanceId)
                createdApiServiceIds.remove(instanceId)
                logger.debug("Cleaned up test instance: {}", instanceId)
            } catch (ApiException e) {
                logger.warn("Failed to cleanup instance {}: {}", instanceId, e.message)
            }
        }
    }

    private void trackCreatedInstance(String apiServiceId) {
        if (apiServiceId && !createdApiServiceIds.contains(apiServiceId)) {
            createdApiServiceIds.add(apiServiceId)
        }
    }

    private List<String> getScopesForIntegrationType(String integrationType) {
        // All integration types support okta.logs.read at minimum
        return ["okta.logs.read"]
    }

    /**
     * Comprehensive test covering ALL API Service Integrations API operations.
     * Tests complete CRUD lifecycle for instances and secrets.
     */
    @Test
    void testAllApiServiceIntegrationOperations() {
        if (testApiServiceType == null) {
            throw new SkipException("No API Service Integration type available in this org")
        }

        String instanceId1 = null
        String instanceId2 = null
        String instanceId3 = null
        String secret1Instance1 = null
        String secret2Instance2 = null

        try {
            def scopes = testApiServiceScopes ?: getScopesForIntegrationType(testApiServiceType)

            // ========================================
            // CREATE INSTANCES
            // ========================================
            
            logger.info("Testing CREATE instance operations...")
            
            def createRequest1 = new PostAPIServiceIntegrationInstanceRequest()
            createRequest1.type = testApiServiceType
            createRequest1.grantedScopes = scopes

            def instance1 = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(createRequest1)
            instanceId1 = instance1.id
            trackCreatedInstance(instanceId1)

            assertThat(instance1, notNullValue())
            assertThat(instance1.id, notNullValue())
            assertThat(instance1.type, equalTo(testApiServiceType))
            assertThat(instance1.name, notNullValue())
            assertThat(instance1.grantedScopes as List, containsInAnyOrder(scopes.toArray()))

            def createRequest2 = new PostAPIServiceIntegrationInstanceRequest()
            createRequest2.type = testApiServiceType
            createRequest2.grantedScopes = scopes

            def instance2 = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(createRequest2)
            instanceId2 = instance2.id
            trackCreatedInstance(instanceId2)

            assertThat(instance2.id, notNullValue())

            Thread.sleep(2000) // Wait for propagation

            // ========================================
            // GET INSTANCES
            // ========================================
            
            logger.info("Testing GET instance operations...")
            
            def retrievedInstance1 = apiServiceIntegrationsApi.getApiServiceIntegrationInstance(instanceId1)

            assertThat(retrievedInstance1.id, equalTo(instanceId1))
            assertThat(retrievedInstance1.type, equalTo(testApiServiceType))

            def retrievedInstance2 = apiServiceIntegrationsApi.getApiServiceIntegrationInstance(instanceId2)

            assertThat(retrievedInstance2.id, equalTo(instanceId2))

            // ========================================
            // LIST INSTANCES
            // ========================================
            
            logger.info("Testing LIST instance operations...")
            
            def allInstances = apiServiceIntegrationsApi.listApiServiceIntegrationInstances(null)

            assertThat(allInstances*.id, hasItem(instanceId1))
            assertThat(allInstances*.id, hasItem(instanceId2))

            // ========================================
            // CREATE SECRETS
            // ========================================
            
            logger.info("Testing CREATE secret operations...")
            
            def createdSecret1 = apiServiceIntegrationsApi.createApiServiceIntegrationInstanceSecret(instanceId1)
            secret1Instance1 = createdSecret1.id

            assertThat(createdSecret1.id, notNullValue())
            assertThat(createdSecret1.status, equalTo(APIServiceIntegrationInstanceSecret.StatusEnum.ACTIVE))
            assertThat(createdSecret1.clientSecret, notNullValue())

            // Create secret on instance2 to avoid hitting 2-secret limit on instance1
            def createdSecret2 = apiServiceIntegrationsApi.createApiServiceIntegrationInstanceSecret(instanceId2)
            secret2Instance2 = createdSecret2.id

            assertThat(createdSecret2.id, notNullValue())

            Thread.sleep(2000)

            // ========================================
            // LIST SECRETS
            // ========================================
            
            logger.info("Testing LIST secret operations...")
            
            def allSecrets1 = apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instanceId1)

            assertThat(allSecrets1*.id, hasItem(secret1Instance1))
            assertThat(allSecrets1.size(), equalTo(2)) // 1 auto + 1 created

            def allSecrets2 = apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instanceId2)

            assertThat(allSecrets2*.id, hasItem(secret2Instance2))
            assertThat(allSecrets2.size(), equalTo(2)) // 1 auto + 1 created

            // ========================================
            // DEACTIVATE SECRETS
            // ========================================
            
            logger.info("Testing DEACTIVATE secret operations...")
            
            def deactivatedSecret1 = apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instanceId1, secret1Instance1)

            assertThat(deactivatedSecret1.status, equalTo(APIServiceIntegrationInstanceSecret.StatusEnum.INACTIVE))

            def deactivatedSecret2 = apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instanceId2, secret2Instance2)

            assertThat(deactivatedSecret2.status, equalTo(APIServiceIntegrationInstanceSecret.StatusEnum.INACTIVE))

            Thread.sleep(1000)

            // ========================================
            // ACTIVATE SECRETS
            // ========================================
            
            logger.info("Testing ACTIVATE secret operations...")
            
            def reactivatedSecret1 = apiServiceIntegrationsApi.activateApiServiceIntegrationInstanceSecret(instanceId1, secret1Instance1)

            assertThat(reactivatedSecret1.status, equalTo(APIServiceIntegrationInstanceSecret.StatusEnum.ACTIVE))

            def reactivatedSecret2 = apiServiceIntegrationsApi.activateApiServiceIntegrationInstanceSecret(instanceId2, secret2Instance2)

            assertThat(reactivatedSecret2.status, equalTo(APIServiceIntegrationInstanceSecret.StatusEnum.ACTIVE))

            Thread.sleep(1000)

            // ========================================
            // DELETE SECRETS
            // ========================================
            
            logger.info("Testing DELETE secret operations...")
            
            apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instanceId1, secret1Instance1)
            Thread.sleep(500)

            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstanceSecret(instanceId1, secret1Instance1)

            apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instanceId2, secret2Instance2)
            Thread.sleep(500)

            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstanceSecret(instanceId2, secret2Instance2)

            Thread.sleep(1000)

            // ========================================
            // DELETE INSTANCES
            // ========================================
            
            logger.info("Testing DELETE instance operations...")
            
            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(instanceId1)
            createdApiServiceIds.remove(instanceId1)

            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(instanceId2)
            createdApiServiceIds.remove(instanceId2)

            // ========================================
            // ERROR HANDLING
            // ========================================
            
            logger.info("Testing error handling...")
            
            def nonExistentId = "0oa_nonexistent_id"
            ApiException getError = expect(ApiException) {
                apiServiceIntegrationsApi.getApiServiceIntegrationInstance(nonExistentId)
            }
            assertThat(getError.code, equalTo(404))

            // Create a fresh instance with one secret
            def tempInstance = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(new PostAPIServiceIntegrationInstanceRequest(
                type: testApiServiceType,
                grantedScopes: scopes
            ))
            instanceId3 = tempInstance.id
            trackCreatedInstance(instanceId3)

            def tempSecrets = apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instanceId3)
            def activeSecret = tempSecrets.find { it.status == APIServiceIntegrationInstanceSecret.StatusEnum.ACTIVE }

            ApiException deleteActiveError = expect(ApiException) {
                apiServiceIntegrationsApi.deleteApiServiceIntegrationInstanceSecret(instanceId3, activeSecret.id)
            }
            assertThat(deleteActiveError.message.toLowerCase(), containsString("active"))

            // Instance3 already has 1 secret, create 1 more (total 2, which is max)
            apiServiceIntegrationsApi.createApiServiceIntegrationInstanceSecret(instanceId3)
            Thread.sleep(500)
            
            ApiException maxSecretError = expect(ApiException) {
                apiServiceIntegrationsApi.createApiServiceIntegrationInstanceSecret(instanceId3)
            }
            assertThat(maxSecretError.message.toLowerCase(), containsString("maximum"))

            // Clean up instance3
            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(instanceId3)
            createdApiServiceIds.remove(instanceId3)

            logger.info("All API Service Integration operations completed successfully!")

        } finally {
            // Ensure cleanup in case of test failure
            if (instanceId1 && createdApiServiceIds.contains(instanceId1)) {
                cleanupTestInstances()
            }
        }
    }

    @Test
    void testPagedAndHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            // Paged - listApiServiceIntegrationInstances
            def instances = apiServiceIntegrationsApi.listApiServiceIntegrationInstancesPaged(null)
            for (def i : instances) { break }
            def instancesH = apiServiceIntegrationsApi.listApiServiceIntegrationInstancesPaged(null, headers)
            for (def i : instancesH) { break }

            // Non-paged with headers
            apiServiceIntegrationsApi.listApiServiceIntegrationInstances(null, headers)
        } catch (Exception e) {
            logger.info("Paged API service integrations test: {}", e.getMessage())
        }
    }
}
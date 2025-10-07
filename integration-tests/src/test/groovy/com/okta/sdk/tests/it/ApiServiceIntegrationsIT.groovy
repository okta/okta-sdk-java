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
import com.okta.sdk.resource.model.PostAPIServiceIntegrationInstanceRequest
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.SkipException
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration Tests for the API Service Integrations Resource.
 */
class ApiServiceIntegrationsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceIntegrationsIT.class)

    def apiServiceIntegrationsApi = new ApiServiceIntegrationsApi(getClient())
    // The test will ONLY look for this specific integration type.
    final String serviceIntegrationType = "java_sdk_service_integration_test"
    private boolean hasServiceIntegration = false

    @BeforeClass
    void checkForServiceIntegration() {
        // This setup method verifies that the required API Service Integration template exists.
        def probeRequest = new PostAPIServiceIntegrationInstanceRequest()
        probeRequest.type = this.serviceIntegrationType
        probeRequest.grantedScopes = ["okta.users.read"]

        try {
            // Attempt to create a temporary instance. If this succeeds, the template exists.
            def probeInstance = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(probeRequest)
            // Immediately delete the temporary instance to keep the environment clean.
            apiServiceIntegrationsApi.deleteApiServiceIntegrationInstance(probeInstance.id)
            this.hasServiceIntegration = true
            logger.info("Successfully verified that API Service Integration type '{}' exists.", this.serviceIntegrationType)
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                // This is the expected error if the template doesn't exist.
                logger.warn("Prerequisite not met: API Service Integration type '{}' not found. Dependent tests will be skipped.", this.serviceIntegrationType)
                this.hasServiceIntegration = false
            } else {
                // Any other error is unexpected.
                throw new IllegalStateException("An unexpected error occurred during test setup: ${e.getMessage()}", e)
            }
        }
    }

    private void skipIfPrerequisiteMissing() {
        if (!this.hasServiceIntegration) {
            throw new SkipException("Skipping test: The prerequisite API Service Integration type '${this.serviceIntegrationType}' was not found in the Okta org.")
        }
    }

    @Test
    void testApiServiceIntegrationLifecycle() {
        skipIfPrerequisiteMissing()

        def request = new PostAPIServiceIntegrationInstanceRequest()
        request.type = this.serviceIntegrationType
        request.grantedScopes = ["okta.users.read"]

        def createdInstance = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(request)
        registerForCleanup(createdInstance) // Automatically delete after tests run

        assertThat(createdInstance, notNullValue())
        assertThat(createdInstance.id, notNullValue())

        def retrievedInstance = apiServiceIntegrationsApi.getApiServiceIntegrationInstance(createdInstance.id)
        assertThat(retrievedInstance.id, equalTo(createdInstance.id))
        assertThat(retrievedInstance.type, equalTo(this.serviceIntegrationType))
    }

    @Test
    void testApiServiceIntegrationSecretManagement() {
        skipIfPrerequisiteMissing()

        def createRequest = new PostAPIServiceIntegrationInstanceRequest()
        createRequest.type = this.serviceIntegrationType
        createRequest.grantedScopes = ["okta.users.read"]
        def instance = apiServiceIntegrationsApi.createApiServiceIntegrationInstance(createRequest)
        registerForCleanup(instance)

        // There should be one secret by default
        def secrets = apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instance.id)
        assertThat(secrets, hasSize(1))

        // Create a second secret
        def newSecret = apiServiceIntegrationsApi.createApiServiceIntegrationInstanceSecret(instance.id)
        assertThat(apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instance.id), hasSize(2))

        // Deactivate and delete the new secret
        apiServiceIntegrationsApi.deactivateApiServiceIntegrationInstanceSecret(instance.id, newSecret.id)
        apiServiceIntegrationsApi.deleteApiServiceIntegrationInstanceSecret(instance.id, newSecret.id)

        // Verify it was deleted
        assertThat(apiServiceIntegrationsApi.listApiServiceIntegrationInstanceSecrets(instance.id), hasSize(1))
    }

    @Test
    void testErrorHandlingForNonExistentInstance() {
        def nonExistentId = "0oa" + UUID.randomUUID().toString().replace('-', '')[0..16]
        expect(ApiException) {
            apiServiceIntegrationsApi.getApiServiceIntegrationInstance(nonExistentId)
        }
    }
}
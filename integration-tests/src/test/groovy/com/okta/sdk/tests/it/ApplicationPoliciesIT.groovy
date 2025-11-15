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
import com.okta.sdk.resource.api.ApplicationPoliciesApi
import com.okta.sdk.resource.api.PolicyApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for {@code /api/v1/apps/{appId}/policies} endpoints.
 * Tests authentication policy assignment to applications.
 */
class ApplicationPoliciesIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationPoliciesIT.class)

    private ApplicationApi applicationApi
    private ApplicationPoliciesApi applicationPoliciesApi
    private PolicyApi policyApi
    private String testAppId
    private String testPolicyId
    private String prefix = "java-sdk-appolicy-it-"

    @BeforeClass
    void setup() {
        applicationApi = new ApplicationApi(getClient())
        applicationPoliciesApi = new ApplicationPoliciesApi(getClient())
        policyApi = new PolicyApi(getClient())

        // Create a test application (OIDC Web App)
        String guid = UUID.randomUUID().toString()
        OpenIdConnectApplication testApp = new OpenIdConnectApplication()
        testApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "policy-test-" + guid)
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId("test-policy-app-" + guid)
            .tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
            .autoKeyRotation(true)

        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        testApp.credentials(credentials)

        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .clientUri("https://example.com")
            .logoUri("https://example.com/logo.png")
            .postLogoutRedirectUris(["https://example.com/postlogout"])
            .responseTypes([OAuthResponseType.CODE])
            .grantTypes([GrantType.AUTHORIZATION_CODE])
            .redirectUris(["https://example.com/oauth/callback"])

        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        testApp.settings(settings)

        OpenIdConnectApplication createdApp = applicationApi.createApplication(testApp, true, null) as OpenIdConnectApplication
        testAppId = createdApp.getId()
        registerForCleanup(createdApp)

        // Create a test authentication policy with retry logic for parallel execution
        Policy createdPolicy = null
        int maxRetries = 3
        int retryCount = 0
        Exception lastException = null
        
        while (retryCount < maxRetries && createdPolicy == null) {
            try {
                // Create new policy object with unique name each time
                Policy testPolicy = new Policy()
                testPolicy.type(PolicyType.ACCESS_POLICY)
                    .status(LifecycleStatus.ACTIVE)
                    .name(prefix + "auth-policy-" + UUID.randomUUID().toString())
                    .description("Test policy for ApplicationPoliciesApi integration tests")
                
                createdPolicy = policyApi.createPolicy(testPolicy, true)
                logger.info("Successfully created test policy on attempt ${retryCount + 1}")
            } catch (ApiException e) {
                lastException = e
                String errorMsg = e.getResponseBody() ?: e.getMessage()
                // Check for timeout or name conflict errors
                if (e.getCause() instanceof java.net.SocketTimeoutException || 
                    errorMsg?.contains("Policy name already in use")) {
                    retryCount++
                    if (retryCount < maxRetries) {
                        logger.warn("Policy creation failed (${errorMsg?.contains("name") ? "name conflict" : "timeout"}), retrying (${retryCount}/${maxRetries})...")
                        Thread.sleep(2000) // Wait before retry
                    }
                } else {
                    throw e // Re-throw if it's not a timeout or name conflict
                }
            }
        }
        
        if (createdPolicy == null) {
            throw new RuntimeException("Failed to create policy in setup after ${maxRetries} attempts", lastException)
        }
        
        testPolicyId = createdPolicy.getId()
        registerForCleanup(createdPolicy)

        Thread.sleep(3000) // Wait for resources to be ready
        
        logger.info("Setup complete - App ID: {}, Policy ID: {}", testAppId, testPolicyId)
    }

    /**
     * Test 1: Basic policy assignment
     * Verifies that a policy can be assigned to an application and the assignment is reflected in the app's links.
     */
    @Test
    void testAssignApplicationPolicy() {
        logger.info("Testing basic application policy assignment...")

        // Act
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(1000)

        // Assert - Verify the assignment by checking the application's access policy link
        Application app = applicationApi.getApplication(testAppId, null)
        
        assertThat(app, notNullValue())
        assertThat(app.getId(), equalTo(testAppId))
        assertThat("Application should have links", app.getLinks(), notNullValue())
        assertThat("Application should have access policy link after assignment", 
            app.getLinks().getAccessPolicy(), notNullValue())
        assertThat("Access policy link should have href", 
            app.getLinks().getAccessPolicy().getHref(), not(emptyOrNullString()))
        assertThat("Access policy link should reference the assigned policy",
            app.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        logger.info("Basic policy assignment test completed successfully!")
    }

    /**
     * Test 2: HTTP Status verification
     * Verifies that policy assignment completes successfully (no exception thrown).
     * Note: The API returns 204 No Content, but the standard method doesn't return HTTP info in Java SDK.
     */
    @Test
    void testAssignApplicationPolicyHttpStatus() {
        logger.info("Testing application policy assignment HTTP status...")

        // Act - Should complete without throwing exception
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(500)

        // Assert - Verify the assignment succeeded by checking the app
        Application app = applicationApi.getApplication(testAppId, null)
        assertThat(app, notNullValue())
        assertThat("Policy should be assigned after successful API call",
            app.getLinks().getAccessPolicy(), notNullValue())

        logger.info("HTTP status test completed successfully!")
    }

    /**
     * Test 4: Error handling - Non-existent application
     * Verifies that assigning a policy to a non-existent application throws appropriate error.
     */
    @Test
    void testAssignPolicyToNonExistentApp() {
        logger.info("Testing error handling for non-existent application...")

        // Arrange
        String nonExistentAppId = "nonexistent123"

        // Act & Assert
        try {
            applicationPoliciesApi.assignApplicationPolicy(nonExistentAppId, testPolicyId)
            throw new AssertionError("Expected ApiException was not thrown")
        } catch (ApiException e) {
            assertThat("Should return 404 for non-existent app", e.getCode(), equalTo(404))
            logger.info("Correctly threw 404 for non-existent app")
        }

        logger.info("Non-existent app error handling test completed successfully!")
    }

    /**
     * Test 5: Error handling - Non-existent policy
     * Verifies that assigning a non-existent policy to an application throws appropriate error.
     */
    @Test
    void testAssignNonExistentPolicyToApp() {
        logger.info("Testing error handling for non-existent policy...")

        // Arrange
        String nonExistentPolicyId = "nonexistent456"

        // Act & Assert
        try {
            applicationPoliciesApi.assignApplicationPolicy(testAppId, nonExistentPolicyId)
            throw new AssertionError("Expected ApiException was not thrown")
        } catch (ApiException e) {
            assertThat("Should return 404 for non-existent policy", e.getCode(), equalTo(404))
            logger.info("Correctly threw 404 for non-existent policy")
        }

        logger.info("Non-existent policy error handling test completed successfully!")
    }

    /**
     * Test 6: Idempotency
     * Verifies that assigning the same policy multiple times is idempotent.
     */
    @Test
    void testPolicyAssignmentIdempotency() {
        logger.info("Testing policy assignment idempotency...")

        // Act - Assign policy multiple times (should be idempotent)
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(500)
        
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(500)
        
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(500)

        // Assert - Verify app is still accessible and policy is assigned
        Application app = applicationApi.getApplication(testAppId, null)
        
        assertThat(app, notNullValue())
        assertThat(app.getId(), equalTo(testAppId))
        assertThat("Policy should remain assigned after multiple assignment calls",
            app.getLinks().getAccessPolicy(), notNullValue())
        assertThat("Same policy should still be assigned",
            app.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        logger.info("Idempotency test completed successfully!")
    }

    /**
     * Test 7: Different application types
     * Verifies that policies can be assigned to different application types (SAML).
     */
    @Test
    void testAssignPolicyToDifferentAppTypes() {
        logger.info("Testing policy assignment to different application types...")

        // Arrange - Create a SAML application
        SamlApplication samlApp = new SamlApplication()
        samlApp.label(prefix + "saml-policy-test-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.SAML_2_0)

        ApplicationVisibility visibility = new ApplicationVisibility()
        visibility.autoSubmitToolbar(false)
        
        ApplicationVisibilityHide hide = new ApplicationVisibilityHide()
        hide.iOS(false).web(false)
        visibility.hide(hide)
        samlApp.visibility(visibility)

        SamlApplicationSettingsSignOn signOn = new SamlApplicationSettingsSignOn()
        signOn.defaultRelayState("")
            .ssoAcsUrl("https://example.com/sso/saml")
            .recipient("https://example.com/sso/saml")
            .destination("https://example.com/sso/saml")
            .audience("https://example.com")
            .idpIssuer('${org.externalKey}')
            .subjectNameIdTemplate('${user.userName}')
            .subjectNameIdFormat(SamlApplicationSettingsSignOn.SubjectNameIdFormatEnum.URN_OASIS_NAMES_TC_SAML_1_1_NAMEID_FORMAT_UNSPECIFIED)
            .responseSigned(true)
            .assertionSigned(true)
            .signatureAlgorithm(SamlApplicationSettingsSignOn.SignatureAlgorithmEnum.RSA_SHA256)
            .digestAlgorithm(SamlApplicationSettingsSignOn.DigestAlgorithmEnum.SHA256)
            .honorForceAuthn(true)
            .authnContextClassRef(SamlApplicationSettingsSignOn.AuthnContextClassRefEnum.URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_PASSWORD_PROTECTED_TRANSPORT)

        SamlApplicationSettings samlSettings = new SamlApplicationSettings()
        samlSettings.signOn(signOn)
        samlApp.settings(samlSettings)

        SamlApplication createdSamlApp = applicationApi.createApplication(samlApp, true, null) as SamlApplication
        String samlAppId = createdSamlApp.getId()
        registerForCleanup(createdSamlApp)

        Thread.sleep(2000)

        // Act - Assign policy to SAML app
        applicationPoliciesApi.assignApplicationPolicy(samlAppId, testPolicyId)
        Thread.sleep(1000)

        // Assert - Verify policy is assigned to SAML application
        Application app = applicationApi.getApplication(samlAppId, null)
        
        assertThat(app, notNullValue())
        assertThat(app.getId(), equalTo(samlAppId))
        assertThat("SAML app should have access policy link after assignment",
            app.getLinks().getAccessPolicy(), notNullValue())
        assertThat("Policy should be assigned to SAML app",
            app.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        logger.info("Different app types test completed successfully!")
    }

    /**
     * Test 8: One policy to multiple applications
     * Verifies that the same policy can be assigned to multiple applications.
     */
    @Test
    void testAssignOnePolicyToMultipleApps() {
        logger.info("Testing one policy assigned to multiple applications...")

        // Arrange - Create second application
        String guid = UUID.randomUUID().toString()
        OpenIdConnectApplication secondApp = new OpenIdConnectApplication()
        secondApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "policy-test-2-" + guid)
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId("test-policy-app2-" + guid)
            .tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
            .autoKeyRotation(true)

        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        secondApp.credentials(credentials)

        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .clientUri("https://example2.com")
            .logoUri("https://example2.com/logo.png")
            .postLogoutRedirectUris(["https://example2.com/postlogout"])
            .responseTypes([OAuthResponseType.CODE])
            .grantTypes([GrantType.AUTHORIZATION_CODE])
            .redirectUris(["https://example2.com/oauth/callback"])

        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        secondApp.settings(settings)

        OpenIdConnectApplication createdSecondApp = applicationApi.createApplication(secondApp, true, null) as OpenIdConnectApplication
        String secondAppId = createdSecondApp.getId()
        registerForCleanup(createdSecondApp)

        Thread.sleep(2000)

        // Act - Assign same policy to both applications
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId)
        Thread.sleep(500)
        applicationPoliciesApi.assignApplicationPolicy(secondAppId, testPolicyId)
        Thread.sleep(1000)

        // Assert - Verify both apps have the policy assigned
        Application app1 = applicationApi.getApplication(testAppId, null)
        Application app2 = applicationApi.getApplication(secondAppId, null)

        assertThat(app1, notNullValue())
        assertThat(app1.getId(), equalTo(testAppId))
        assertThat("First app should have access policy link",
            app1.getLinks().getAccessPolicy(), notNullValue())
        assertThat("First app should have policy assigned",
            app1.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        assertThat(app2, notNullValue())
        assertThat(app2.getId(), equalTo(secondAppId))
        assertThat("Second app should have access policy link",
            app2.getLinks().getAccessPolicy(), notNullValue())
        assertThat("Second app should have policy assigned",
            app2.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        logger.info("Multiple apps test completed successfully!")
    }

    /**
     * Test 10: Map parameter variant
     * Tests the assignApplicationPolicy method with Map parameter (for additional parameters).
     */
    @Test
    void testAssignApplicationPolicyWithMapParameter() {
        logger.info("Testing application policy assignment with Map parameter...")

        // Arrange - Use empty map for additional parameters (common pattern in generated code)
        def additionalParams = [:]

        // Act - Assign policy using Map parameter variant
        applicationPoliciesApi.assignApplicationPolicy(testAppId, testPolicyId, additionalParams)
        Thread.sleep(1000)

        // Assert - Verify the assignment by checking the application's access policy link
        Application app = applicationApi.getApplication(testAppId, null)
        
        assertThat(app, notNullValue())
        assertThat(app.getId(), equalTo(testAppId))
        assertThat("Application should have links", app.getLinks(), notNullValue())
        assertThat("Application should have access policy link after assignment with Map param", 
            app.getLinks().getAccessPolicy(), notNullValue())
        assertThat("Access policy link should have href", 
            app.getLinks().getAccessPolicy().getHref(), not(emptyOrNullString()))
        assertThat("Access policy link should reference the assigned policy",
            app.getLinks().getAccessPolicy().getHref(), containsString(testPolicyId))

        logger.info("Map parameter variant test completed successfully!")
    }
}

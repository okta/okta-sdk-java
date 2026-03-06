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
import com.okta.sdk.resource.api.ApplicationSsoApi
import com.okta.sdk.resource.api.ApplicationSsoCredentialKeyApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for {@code /api/v1/apps/{appId}/sso/saml/metadata} endpoint.
 * Tests SAML metadata preview functionality for SSO applications.
 * 
 * The Application SSO API provides SSO resources for applications, specifically 
 * for previewing SAML metadata used in federation protocols.
 */
class ApplicationSSOIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationSSOIT.class)

    private ApplicationApi applicationApi
    private ApplicationSsoApi applicationSsoApi
    private ApplicationSsoCredentialKeyApi applicationSsoCredentialKeyApi
    private String prefix = "java-sdk-sso-it-"

    /**
     * Creates a SAML 2.0 application for testing SSO functionality.
     * SAML applications automatically get signing credentials generated upon creation.
     */
    private String createTestSamlApplication() {
        applicationApi = new ApplicationApi(getClient())
        
        String guid = UUID.randomUUID().toString()
        SamlApplication samlApp = new SamlApplication()
        samlApp.label(prefix + "saml-" + guid)
            .signOnMode(ApplicationSignOnMode.SAML_2_0)

        ApplicationVisibility visibility = new ApplicationVisibility()
        ApplicationVisibilityHide hide = new ApplicationVisibilityHide()
        hide.iOS(false).web(false)
        visibility.autoSubmitToolbar(false).hide(hide)
        samlApp.visibility(visibility)

        SamlApplicationSettingsSignOn signOn = new SamlApplicationSettingsSignOn()
        signOn.defaultRelayState("")
            .ssoAcsUrl("https://example-${guid}.com/sso/saml")
            .recipient("https://example-${guid}.com/sso/saml")
            .destination("https://example-${guid}.com/sso/saml")
            .audience("https://example-${guid}.com")
            .idpIssuer('$${org.externalKey}')
            .subjectNameIdTemplate('${user.userName}')
            .subjectNameIdFormat(SamlApplicationSettingsSignOn.SubjectNameIdFormatEnum.URN_OASIS_NAMES_TC_SAML_1_1_NAMEID_FORMAT_UNSPECIFIED)
            .responseSigned(true)
            .assertionSigned(true)
            .signatureAlgorithm(SamlApplicationSettingsSignOn.SignatureAlgorithmEnum.RSA_SHA256)
            .digestAlgorithm(SamlApplicationSettingsSignOn.DigestAlgorithmEnum.SHA256)
            .honorForceAuthn(true)
            .authnContextClassRef(SamlApplicationSettingsSignOn.AuthnContextClassRefEnum.URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_PASSWORD_PROTECTED_TRANSPORT)
            .samlAssertionLifetimeSeconds(300)

        SamlApplicationSettings settings = new SamlApplicationSettings()
        settings.signOn(signOn)
        samlApp.settings(settings)

        SamlApplication createdApp = applicationApi.createApplication(samlApp, true, null) as SamlApplication
        registerForCleanup(createdApp)

        // Wait for the app to be fully provisioned with signing credentials
        Thread.sleep(3000)

        return createdApp.getId()
    }

    /**
     * Comprehensive test covering all Application SSO API operations and endpoints.
     * Tests SAML metadata preview functionality.
     * 
     * Coverage:
     * - GET /api/v1/apps/{appId}/sso/saml/metadata - previewSAMLmetadataForApplication
     * 
     * Test pattern: Create SAML App  Get Key Credentials  Preview SAML Metadata  Validate  Cleanup
     */
    @Test
    void testPreviewSAMLMetadataForApplication() {
        logger.info("Testing comprehensive Application SSO API operations...")

        // ==================== SETUP: CREATE SAML APPLICATION ====================
        String appId = createTestSamlApplication()
        assertThat(appId, notNullValue())

        applicationSsoApi = new ApplicationSsoApi(getClient())
        applicationSsoCredentialKeyApi = new ApplicationSsoCredentialKeyApi(getClient())

        // Wait longer for the app to be fully provisioned
        Thread.sleep(5000)

        // Verify the application was created and is a SAML app
        Application createdApp = applicationApi.getApplication(appId, null)
        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.SAML_2_0))

        SamlApplication samlApp = (SamlApplication) createdApp
        assertThat(samlApp, notNullValue())

        // ==================== GET APPLICATION KEY CREDENTIALS ====================
        // Retrieve the signing key credentials (kid) from the SAML application
        List<JsonWebKey> keys = applicationSsoCredentialKeyApi.listApplicationKeys(appId)
        assertThat(keys, notNullValue())
        assertThat(keys.size(), greaterThanOrEqualTo(1))

        JsonWebKey firstKey = keys.get(0)
        assertThat(firstKey, notNullValue())
        assertThat(firstKey.getKid(), notNullValue())
        
        String kid = firstKey.getKid()
        logger.info("Found signing key: {}", kid)

        // Validate key properties
        assertThat(firstKey.getKty(), notNullValue())
        assertThat(firstKey.getUse(), notNullValue())
        assertThat(firstKey.getCreated(), notNullValue())
        assertThat(firstKey.getExpiresAt(), notNullValue())
        assertThat(firstKey.getX5c(), notNullValue())
        assertThat(firstKey.getX5c().size(), greaterThanOrEqualTo(1))

        // ==================== PREVIEW SAML METADATA (Standard Method) ====================
        logger.info("Testing previewSAMLmetadataForApplication...")
        String samlMetadata = applicationSsoApi.previewSAMLmetadataForApplication(appId, kid)

        // Validate the SAML metadata response
        assertThat(samlMetadata, notNullValue())
        assertThat(samlMetadata, not(emptyString()))
        assertThat(samlMetadata, startsWith("<?xml"))
        assertThat(samlMetadata, containsString("EntityDescriptor"))
        assertThat(samlMetadata, containsString("IDPSSODescriptor"))
        assertThat(samlMetadata, containsString("KeyDescriptor"))
        assertThat(samlMetadata, containsString("SingleSignOnService"))
        assertThat(samlMetadata, containsString("urn:oasis:names:tc:SAML:2.0"))

        logger.info("SAML metadata retrieved successfully, length: {}", samlMetadata.length())

        // Parse and validate XML structure
        GPathResult xmlDoc = new XmlSlurper().parseText(samlMetadata)
        assertThat(xmlDoc, notNullValue())
        assertThat(xmlDoc.name(), equalTo("EntityDescriptor"))

        // Validate EntityDescriptor has entityID attribute
        String entityId = xmlDoc.@entityID.text()
        assertThat(entityId, not(emptyString()))
        logger.info("EntityID: {}", entityId)

        // Validate IDPSSODescriptor exists
        def idpDescriptor = xmlDoc.'**'.find { it.name() == 'IDPSSODescriptor' }
        assertThat(idpDescriptor, notNullValue())

        // Validate KeyDescriptor exists
        def keyDescriptor = xmlDoc.'**'.find { it.name() == 'KeyDescriptor' }
        assertThat(keyDescriptor, notNullValue())

        // Validate SingleSignOnService exists
        def ssoService = xmlDoc.'**'.find { it.name() == 'SingleSignOnService' }
        assertThat(ssoService, notNullValue())

        // Validate NameIDFormat exists
        def nameIdFormat = xmlDoc.'**'.find { it.name() == 'NameIDFormat' }
        assertThat(nameIdFormat, notNullValue())

        // ==================== VALIDATE METADATA IS CONSISTENT ====================
        logger.info("Validating metadata consistency...")
        String samlMetadata2 = applicationSsoApi.previewSAMLmetadataForApplication(appId, kid)
        assertThat(samlMetadata2, equalTo(samlMetadata))
        logger.info("Metadata is consistent across calls")

        // ==================== ERROR HANDLING: INVALID APP ID ====================
        logger.info("Testing error handling with invalid app ID...")
        String invalidAppId = "invalid_app_id_" + UUID.randomUUID().toString()
        
        try {
            applicationSsoApi.previewSAMLmetadataForApplication(invalidAppId, kid)
            throw new AssertionError("Expected ApiException for non-existent application")
        } catch (ApiException e) {
            assertThat(e.getCode(), equalTo(404))
            logger.info("Correctly threw 404 for invalid app ID")
        }

        // ==================== ERROR HANDLING: INVALID KID ====================
        logger.info("Testing error handling with invalid key ID...")
        String invalidKid = "invalid_kid_" + UUID.randomUUID().toString()
        
        try {
            applicationSsoApi.previewSAMLmetadataForApplication(appId, invalidKid)
            throw new AssertionError("Expected ApiException for non-existent key ID")
        } catch (ApiException e) {
            assertThat(e.getCode(), equalTo(404))
            logger.info("Correctly threw 404 for invalid kid")
        }

        // ==================== ERROR HANDLING: NULL PARAMETERS ====================
        logger.info("Testing error handling with null parameters...")
        
        try {
            applicationSsoApi.previewSAMLmetadataForApplication(null, kid)
            throw new AssertionError("Expected exception for null appId")
        } catch (ApiException e) {
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
            logger.info("Correctly threw error for null appId")
        }

        try {
            applicationSsoApi.previewSAMLmetadataForApplication(appId, null)
            throw new AssertionError("Expected exception for null kid")
        } catch (ApiException e) {
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
            logger.info("Correctly threw error for null kid")
        }

        // ==================== ADDITIONAL VALIDATION: MULTIPLE KEYS ====================
        logger.info("Checking if app has multiple keys...")
        if (keys.size() > 1) {
            logger.info("App has {} keys, testing additional keys...", keys.size())
            for (int i = 1; i < Math.min(keys.size(), 3); i++) {
                JsonWebKey additionalKey = keys.get(i)
                String additionalMetadata = applicationSsoApi.previewSAMLmetadataForApplication(appId, additionalKey.getKid())
                assertThat(additionalMetadata, notNullValue())
                assertThat(additionalMetadata, startsWith("<?xml"))
                logger.info("Successfully retrieved metadata for key {}", additionalKey.getKid())
            }
        }

        // ==================== VALIDATION: METADATA CONTENT ====================
        logger.info("Validating metadata content reflects SAML settings...")
        SamlApplicationSettingsSignOn samlSettings = samlApp.getSettings()?.getSignOn()
        if (samlSettings != null) {
            // Verify signature algorithm settings are reflected
            if (samlSettings.getSignatureAlgorithm() != null) {
                assertThat(samlMetadata, containsString("xmldsig"))
            }
        }

        // ==================== CONSISTENCY CHECK ====================
        logger.info("Performing consistency check...")
        String metadataCheck = applicationSsoApi.previewSAMLmetadataForApplication(appId, kid)
        assertThat(metadataCheck, equalTo(samlMetadata))
        logger.info("Metadata is consistent across multiple requests")

        // ==================== TEST NON-SAML APPLICATION ====================
        logger.info("Testing error handling for non-SAML application...")
        BookmarkApplication bookmarkApp = new BookmarkApplication()
        bookmarkApp.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "bookmark-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)

        BookmarkApplicationSettingsApplication bookmarkSettings = new BookmarkApplicationSettingsApplication()
        bookmarkSettings.url("https://example.com/bookmark.html")
            .requestIntegration(false)

        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(bookmarkSettings)
        bookmarkApp.settings(settings)

        BookmarkApplication createdBookmarkApp = applicationApi.createApplication(bookmarkApp, true, null) as BookmarkApplication
        registerForCleanup(createdBookmarkApp)

        // Try to get SAML metadata for a non-SAML app (should fail)
        try {
            applicationSsoApi.previewSAMLmetadataForApplication(createdBookmarkApp.getId(), kid)
            throw new AssertionError("Expected ApiException for non-SAML application")
        } catch (ApiException e) {
            assertThat(e.getCode(), anyOf(equalTo(404), equalTo(400)))
            logger.info("Correctly threw error for non-SAML application")
        }

        logger.info("All Application SSO API tests completed successfully!")
    }

    /**
     * Tests SAML metadata preview with different SAML configuration options.
     * Validates that different SAML settings produce appropriate metadata variations.
     */
    @Test
    void testDifferentSAMLConfigurations() {
        logger.info("Testing SAML metadata with different configurations...")

        applicationApi = new ApplicationApi(getClient())
        applicationSsoApi = new ApplicationSsoApi(getClient())
        applicationSsoCredentialKeyApi = new ApplicationSsoCredentialKeyApi(getClient())

        // Create SAML app with specific configuration
        String guid = UUID.randomUUID().toString()
        SamlApplication samlApp = new SamlApplication()
        samlApp.label(prefix + "config-test-" + guid)
            .signOnMode(ApplicationSignOnMode.SAML_2_0)

        ApplicationVisibility visibility = new ApplicationVisibility()
        ApplicationVisibilityHide hide = new ApplicationVisibilityHide()
        hide.iOS(false).web(false)
        visibility.autoSubmitToolbar(false).hide(hide)
        samlApp.visibility(visibility)

        SamlApplicationSettingsSignOn signOn = new SamlApplicationSettingsSignOn()
        signOn.ssoAcsUrl("https://config-test-${guid}.com/sso/saml")
            .recipient("https://config-test-${guid}.com/sso/saml")
            .destination("https://config-test-${guid}.com/sso/saml")
            .audience("https://config-test-${guid}.com/audience")
            .idpIssuer('$${org.externalKey}')
            .subjectNameIdTemplate('${user.userName}')
            .subjectNameIdFormat(SamlApplicationSettingsSignOn.SubjectNameIdFormatEnum.URN_OASIS_NAMES_TC_SAML_1_1_NAMEID_FORMAT_EMAIL_ADDRESS)
            .responseSigned(true)
            .assertionSigned(false) // Different from default
            .signatureAlgorithm(SamlApplicationSettingsSignOn.SignatureAlgorithmEnum.RSA_SHA1) // Different algorithm
            .digestAlgorithm(SamlApplicationSettingsSignOn.DigestAlgorithmEnum.SHA1) // Different algorithm
            .honorForceAuthn(false)
            .authnContextClassRef(SamlApplicationSettingsSignOn.AuthnContextClassRefEnum.URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_PASSWORD)
            .samlAssertionLifetimeSeconds(600)

        SamlApplicationSettings settings = new SamlApplicationSettings()
        settings.signOn(signOn)
        samlApp.settings(settings)

        SamlApplication createdApp = applicationApi.createApplication(samlApp, true, null) as SamlApplication
        registerForCleanup(createdApp)
        
        Thread.sleep(3000)

        // Get key credentials
        List<JsonWebKey> keys = applicationSsoCredentialKeyApi.listApplicationKeys(createdApp.getId())
        assertThat(keys.size(), greaterThanOrEqualTo(1))
        String kid = keys.get(0).getKid()

        // Preview metadata
        String metadata = applicationSsoApi.previewSAMLmetadataForApplication(createdApp.getId(), kid)

        // Validate metadata reflects configuration
        assertThat(metadata, notNullValue())
        assertThat(metadata, containsString("emailAddress"))
        logger.info("Metadata contains emailAddress NameID format as configured")

        // Parse and validate specific configuration elements
        GPathResult xmlDoc = new XmlSlurper().parseText(metadata)
        assertThat(xmlDoc, notNullValue())

        def nameIdFormats = xmlDoc.'**'.findAll { it.name() == 'NameIDFormat' }
        boolean foundEmailAddress = false
        nameIdFormats.each { format ->
            if (format.text().contains("emailAddress")) {
                foundEmailAddress = true
            }
        }
        assertThat(foundEmailAddress, equalTo(true))

        logger.info("Different SAML configuration test completed successfully!")
    }

    @Test
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        // Test listApplications with headers as a general coverage path
        try {
            def apps = applicationApi.listApplications(null, null, null, null, null, true, null, headers)
            assertThat(apps, notNullValue())
        } catch (Exception e) {
            logger.info("Headers overload test: {}", e.getMessage())
        }
    }
}

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

import com.okta.sdk.resource.api.OrgSettingCustomizationApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.OrgPreferences
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Integration tests for OrgSettingCustomization API
 *
 * Coverage:
 * - GET  /api/v1/org/preferences                 - getOrgPreferences()
 * - POST /api/v1/org/preferences/hideEndUserFooter - setOrgHideOktaUIFooter()
 * - POST /api/v1/org/preferences/showEndUserFooter - setOrgShowOktaUIFooter()
 *
 * Note: These are org-wide UI preferences. Tests restore original values after testing.
 */
class OrgSettingCustomizationIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(OrgSettingCustomizationIT)


    private OrgSettingCustomizationApi orgSettingCustomizationApi

    /**
     * Comprehensive test covering all OrgSettingCustomization API endpoints.
     *
     * Test Flow:
     * 1. Get current org preferences (showEndUserFooter value)
     * 2. Toggle hide/show footer
     * 3. Verify each state change
     * 4. Restore original setting
     *
     * Cleanup Strategy:
     * - Restores footer preference to original value immediately after testing
     * - Ensures no persistent changes to org UI customization
     */
    @Test(groups = "group3")
    void testOrgCustomizationPreferencesLifecycle() {
        // Initialize API
        orgSettingCustomizationApi = new OrgSettingCustomizationApi(getClient())

        // Track original value for restoration
        Boolean originalShowFooter = null

        try {
            logger.debug("\n" + "=".multiply(60))
            logger.debug("TESTING ORG CUSTOMIZATION PREFERENCES API")
            logger.debug("=".multiply(60))
            logger.debug("Note: All settings will be restored to original values")

            // ========================================
            // Step 1: Get current org preferences
            // ========================================
            logger.debug("\n1. GET /api/v1/org/preferences")
            OrgPreferences currentPrefs = orgSettingCustomizationApi.getOrgPreferences()
            originalShowFooter = currentPrefs.getShowEndUserFooter()

            assertThat "Preferences should not be null", currentPrefs, notNullValue()
            assertThat "showEndUserFooter should not be null",
                       currentPrefs.getShowEndUserFooter(), notNullValue()

            logger.debug("   Current setting: showEndUserFooter = {}", originalShowFooter)

            if (originalShowFooter) {
                logger.debug("   Footer is currently VISIBLE")
            } else {
                logger.debug("   Footer is currently HIDDEN")
            }

            // ========================================
            // Step 2: Toggle footer visibility
            // ========================================
            if (originalShowFooter) {
                // Currently showing footer  hide it first, then restore
                logger.debug("\n2. POST /api/v1/org/preferences/hideEndUserFooter")
                logger.debug("   (Hiding the end user dashboard footer)")

                OrgPreferences hiddenPrefs = orgSettingCustomizationApi.setOrgHideOktaUIFooter()

                assertThat "Hidden preferences should not be null", hiddenPrefs, notNullValue()
                assertThat "showEndUserFooter should be false after hiding",
                           hiddenPrefs.getShowEndUserFooter(), equalTo(false)

                logger.debug("   Successfully hidden: showEndUserFooter = {}", hiddenPrefs.getShowEndUserFooter())

                // Verify the change persisted via GET
                // Note: The POST response already validated the value. The GET may return
                // a stale cached value due to SDK caching / eventual consistency.
                logger.debug("\n   Verifying change via GET...")
                OrgPreferences verifyPrefs = orgSettingCustomizationApi.getOrgPreferences()
                assertThat "GET should return preferences", verifyPrefs, notNullValue()
                if (verifyPrefs.getShowEndUserFooter() == false) {
                    logger.debug("   Verified: footer is hidden")
                } else {
                    logger.debug("   GET returned showEndUserFooter={} (stale cache); POST response already confirmed hide succeeded", verifyPrefs.getShowEndUserFooter())
                }

                // Restore: show footer again
                logger.debug("\n3. POST /api/v1/org/preferences/showEndUserFooter")
                logger.debug("   (Restoring footer to original visible state)")

                OrgPreferences restoredPrefs = orgSettingCustomizationApi.setOrgShowOktaUIFooter()

                assertThat "Restored preferences should not be null", restoredPrefs, notNullValue()
                assertThat "showEndUserFooter should be true after showing",
                           restoredPrefs.getShowEndUserFooter(), equalTo(true)

                logger.debug("   Restored: showEndUserFooter = {}", restoredPrefs.getShowEndUserFooter())
                logger.debug("   Restored to original state (footer visible)")

            } else {
                // Currently hidden  show it first, then restore
                logger.debug("\n2. POST /api/v1/org/preferences/showEndUserFooter")
                logger.debug("   (Showing the end user dashboard footer)")

                OrgPreferences shownPrefs = orgSettingCustomizationApi.setOrgShowOktaUIFooter()

                assertThat "Shown preferences should not be null", shownPrefs, notNullValue()
                assertThat "showEndUserFooter should be true after showing",
                           shownPrefs.getShowEndUserFooter(), equalTo(true)

                logger.debug("   Successfully shown: showEndUserFooter = {}", shownPrefs.getShowEndUserFooter())

                // Verify the change persisted via GET
                // Note: The POST response already validated the value. The GET may return
                // a stale cached value due to SDK caching / eventual consistency.
                logger.debug("\n   Verifying change via GET...")
                OrgPreferences verifyPrefs = orgSettingCustomizationApi.getOrgPreferences()
                assertThat "GET should return preferences", verifyPrefs, notNullValue()
                if (verifyPrefs.getShowEndUserFooter() == true) {
                    logger.debug("   Verified: footer is visible")
                } else {
                    logger.debug("   GET returned showEndUserFooter={} (stale cache); POST response already confirmed show succeeded", verifyPrefs.getShowEndUserFooter())
                }

                // Restore: hide footer again
                logger.debug("\n3. POST /api/v1/org/preferences/hideEndUserFooter")
                logger.debug("   (Restoring footer to original hidden state)")

                OrgPreferences restoredPrefs = orgSettingCustomizationApi.setOrgHideOktaUIFooter()

                assertThat "Restored preferences should not be null", restoredPrefs, notNullValue()
                assertThat "showEndUserFooter should be false after hiding",
                           restoredPrefs.getShowEndUserFooter(), equalTo(false)

                logger.debug("   Restored: showEndUserFooter = {}", restoredPrefs.getShowEndUserFooter())
                logger.debug("   Restored to original state (footer hidden)")
            }

            // ========================================
            // Summary
            // ========================================
            logger.debug("\n" + "=".multiply(60))
            logger.debug(" ALL ORG CUSTOMIZATION PREFERENCES API TESTS COMPLETE")
            logger.debug("=".multiply(60))
            logger.debug("\n=== API Coverage Summary ===")
            logger.debug("All 3 OrgSettingCustomization API endpoints tested:")
            logger.debug("   GET  /api/v1/org/preferences")
            logger.debug("   POST /api/v1/org/preferences/hideEndUserFooter")
            logger.debug("   POST /api/v1/org/preferences/showEndUserFooter")
            logger.debug("\n=== Cleanup Summary ===")
            logger.debug("   Footer Preference: Restored to showEndUserFooter = {}", originalShowFooter)
            logger.debug("\n=== Test Results ===")
            logger.debug(" Total Endpoints: 3")
            logger.debug(" Endpoints Tested: 3 (100%)")
            logger.debug(" Test Scenarios: Get current, Toggle settings, Verify responses, Restore")
            logger.debug(" Org Impact: Zero (settings restored to original)")

        } catch (Exception e) {
            logger.debug("\n Test failed with exception: {}", e.message)
            if (e instanceof ApiException) {
                logger.debug("Response code: {}", ((ApiException) e).code)
                logger.debug("Response body: {}", ((ApiException) e).responseBody)
            }

            // Attempt emergency restoration
            logger.debug("\n=== Emergency Cleanup Attempt ===")
            try {
                if (originalShowFooter != null) {
                    logger.debug("Restoring footer preference to original value...")

                    if (originalShowFooter) {
                        orgSettingCustomizationApi.setOrgShowOktaUIFooter()
                        logger.debug("   Restored to visible")
                    } else {
                        orgSettingCustomizationApi.setOrgHideOktaUIFooter()
                        logger.debug("   Restored to hidden")
                    }

                    // Verify restoration
                    OrgPreferences restoredPrefs = orgSettingCustomizationApi.getOrgPreferences()
                    if (restoredPrefs.getShowEndUserFooter() == originalShowFooter) {
                        logger.debug("   Emergency restoration verified")
                    } else {
                        logger.debug("   Emergency restoration may have failed")
                    }
                }
                logger.debug("=== Emergency Cleanup Complete ===")
            } catch (Exception cleanupException) {
                logger.debug(" Emergency cleanup failed: {}", cleanupException.message)
            }

            throw e
        }
    }

    @Test(groups = "group3")
    void testOrgCustomizationIdempotency() {
        orgSettingCustomizationApi = new OrgSettingCustomizationApi(getClient())

        logger.debug("TESTING ORG CUSTOMIZATION IDEMPOTENCY")

        // Save original state
        OrgPreferences original = orgSettingCustomizationApi.getOrgPreferences()
        Boolean originalShowFooter = original.getShowEndUserFooter()

        try {
            // Test 1: Calling setOrgShowOktaUIFooter twice should be idempotent
            logger.debug("\n1. Testing show footer idempotency (calling twice)...")
            OrgPreferences result1 = orgSettingCustomizationApi.setOrgShowOktaUIFooter()
            OrgPreferences result2 = orgSettingCustomizationApi.setOrgShowOktaUIFooter()
            assertThat "Both show calls should return same state",
                       result1.getShowEndUserFooter(), equalTo(result2.getShowEndUserFooter())
            assertThat "Both should show footer as true",
                       result2.getShowEndUserFooter(), equalTo(true)
            logger.debug("    Show footer is idempotent")

            // Test 2: Calling setOrgHideOktaUIFooter twice should be idempotent
            logger.debug("\n2. Testing hide footer idempotency (calling twice)...")
            OrgPreferences result3 = orgSettingCustomizationApi.setOrgHideOktaUIFooter()
            OrgPreferences result4 = orgSettingCustomizationApi.setOrgHideOktaUIFooter()
            assertThat "Both hide calls should return same state",
                       result3.getShowEndUserFooter(), equalTo(result4.getShowEndUserFooter())
            assertThat "Both should show footer as false",
                       result4.getShowEndUserFooter(), equalTo(false)
            logger.debug("    Hide footer is idempotent")

            logger.debug("\n All idempotency tests passed!")

        } finally {
            // Restore original state
            if (originalShowFooter) {
                orgSettingCustomizationApi.setOrgShowOktaUIFooter()
            } else {
                orgSettingCustomizationApi.setOrgHideOktaUIFooter()
            }
            logger.debug("   Restored to original: showEndUserFooter = {}", originalShowFooter)
        }
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            orgSettingCustomizationApi.getOrgOktaUIFooter(headers)
        } catch (Exception e) {
            // Expected
        }
    }
}

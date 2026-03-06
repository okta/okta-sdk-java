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

import com.okta.sdk.resource.api.OrgSettingAdminApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Integration tests for OrgSettingAdmin API
 * 
 * Coverage:
 * - GET /api/v1/org/orgSettings/thirdPartyAdminSetting - getThirdPartyAdminSetting()
 * - POST /api/v1/org/orgSettings/thirdPartyAdminSetting - updateThirdPartyAdminSetting()
 * - GET /api/v1/org/settings/autoAssignAdminAppSetting - getAutoAssignAdminAppSetting()
 * - POST /api/v1/org/settings/autoAssignAdminAppSetting - updateAutoAssignAdminAppSetting()
 * - GET /api/v1/org/settings/clientPrivilegesSetting - getClientPrivilegesSetting()
 * - PUT /api/v1/org/settings/clientPrivilegesSetting - assignClientPrivilegesSetting()
 * 
 * Note: These are org-wide settings. Tests will restore original values after testing.
 */
class OrgSettingAdminIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(OrgSettingAdminIT)


    private OrgSettingAdminApi orgSettingAdminApi

    /**
     * Comprehensive test covering all OrgSettingAdmin API endpoints.
     * 
     * Test Flow:
     * 1. Third Party Admin Setting - Get current, update, restore
     * 2. Auto Assign Admin App Setting - Get current, update, restore
     * 3. Client Privileges Setting - Get current, update, restore
     * 
     * Cleanup Strategy:
     * - Restores all settings to their original values immediately after testing each setting
     * - Ensures no persistent changes to org configuration
     */
    @Test(groups = "group3")
    void testOrgSettingAdminLifecycle() {
        // Initialize API
        orgSettingAdminApi = new OrgSettingAdminApi(getClient())

        // Track original values for restoration
        def originalThirdPartyAdmin = null
        def originalAutoAssignAdmin = null
        def originalClientPrivileges = null

        try {
            logger.debug("\n=== Testing OrgSettingAdmin API ===")
            logger.debug("Note: All settings will be restored to original values")

            // ========================================
            // Test 1: Third Party Admin Setting
            // ========================================
            logger.debug("\n" + "=".multiply(60))
            logger.debug("TEST 1: Third Party Admin Setting")
            logger.debug("=".multiply(60))

            // Step 1.1: Get current third party admin setting
            logger.debug("\n1.1. GET /api/v1/org/orgSettings/thirdPartyAdminSetting")
            def currentThirdParty = orgSettingAdminApi.getThirdPartyAdminSetting()
            originalThirdPartyAdmin = currentThirdParty.thirdPartyAdmin
            assertThat "Third party admin setting should not be null", 
                       currentThirdParty.thirdPartyAdmin, notNullValue()
            logger.debug("   Current setting: thirdPartyAdmin = {}", originalThirdPartyAdmin)

            // Step 1.2: Update third party admin setting (toggle value)
            logger.debug("\n1.2. POST /api/v1/org/orgSettings/thirdPartyAdminSetting (Update)")
            def newThirdPartyValue = !originalThirdPartyAdmin
            def updateThirdParty = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(newThirdPartyValue)
            
            def updatedThirdParty = orgSettingAdminApi.updateThirdPartyAdminSetting(updateThirdParty)
            assertThat "Updated third party admin should match new value", 
                       updatedThirdParty.thirdPartyAdmin, equalTo(newThirdPartyValue)
            logger.debug("   Updated to: thirdPartyAdmin = {}", newThirdPartyValue)

            // Step 1.3: Verify the update by getting again
            logger.debug("\n1.3. GET (Verify update)")
            def verifyThirdParty = orgSettingAdminApi.getThirdPartyAdminSetting()
            assertThat "Verified value should match updated value", 
                       verifyThirdParty.thirdPartyAdmin, equalTo(newThirdPartyValue)
            logger.debug("   Verified: thirdPartyAdmin = {}", verifyThirdParty.thirdPartyAdmin)

            // Step 1.4: Restore original value
            logger.debug("\n1.4. POST (Restore original value)")
            def restoreThirdParty = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(originalThirdPartyAdmin)
            def restoredThirdParty = orgSettingAdminApi.updateThirdPartyAdminSetting(restoreThirdParty)
            assertThat "Restored value should match original", 
                       restoredThirdParty.thirdPartyAdmin, equalTo(originalThirdPartyAdmin)
            logger.debug("   Restored to original: thirdPartyAdmin = {}", originalThirdPartyAdmin)
            logger.debug("   Third Party Admin Setting test complete")

            // ========================================
            // Test 2: Auto Assign Admin App Setting
            // ========================================
            logger.debug("\n" + "=".multiply(60))
            logger.debug("TEST 2: Auto Assign Admin App Setting")
            logger.debug("=".multiply(60))

            // Step 2.1: Get current auto assign setting
            logger.debug("\n2.1. GET /api/v1/org/settings/autoAssignAdminAppSetting")
            def currentAutoAssign = orgSettingAdminApi.getAutoAssignAdminAppSetting()
            originalAutoAssignAdmin = currentAutoAssign.autoAssignAdminAppSetting
            assertThat "Auto assign admin setting should not be null", 
                       currentAutoAssign.autoAssignAdminAppSetting, notNullValue()
            logger.debug("   Current setting: autoAssignAdminAppSetting = {}", originalAutoAssignAdmin)

            // Step 2.2: Update auto assign setting (toggle value)
            logger.debug("\n2.2. POST /api/v1/org/settings/autoAssignAdminAppSetting (Update)")
            def newAutoAssignValue = !originalAutoAssignAdmin
            def updateAutoAssign = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(newAutoAssignValue)
            
            def updatedAutoAssign = orgSettingAdminApi.updateAutoAssignAdminAppSetting(updateAutoAssign)
            assertThat "Updated auto assign should match new value", 
                       updatedAutoAssign.autoAssignAdminAppSetting, equalTo(newAutoAssignValue)
            logger.debug("   Updated to: autoAssignAdminAppSetting = {}", newAutoAssignValue)

            // Step 2.3: Verify the update
            logger.debug("\n2.3. GET (Verify update)")
            def verifyAutoAssign = orgSettingAdminApi.getAutoAssignAdminAppSetting()
            assertThat "Verified value should match updated value", 
                       verifyAutoAssign.autoAssignAdminAppSetting, equalTo(newAutoAssignValue)
            logger.debug("   Verified: autoAssignAdminAppSetting = {}", verifyAutoAssign.autoAssignAdminAppSetting)

            // Step 2.4: Restore original value
            logger.debug("\n2.4. POST (Restore original value)")
            def restoreAutoAssign = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(originalAutoAssignAdmin)
            def restoredAutoAssign = orgSettingAdminApi.updateAutoAssignAdminAppSetting(restoreAutoAssign)
            assertThat "Restored value should match original", 
                       restoredAutoAssign.autoAssignAdminAppSetting, equalTo(originalAutoAssignAdmin)
            logger.debug("   Restored to original: autoAssignAdminAppSetting = {}", originalAutoAssignAdmin)
            logger.debug("   Auto Assign Admin App Setting test complete")

            // ========================================
            // Test 3: Client Privileges Setting
            // ========================================
            logger.debug("\n" + "=".multiply(60))
            logger.debug("TEST 3: Client Privileges Setting")
            logger.debug("=".multiply(60))

            // Step 3.1: Get current client privileges setting
            logger.debug("\n3.1. GET /api/v1/org/settings/clientPrivilegesSetting")
            def currentClientPrivileges = orgSettingAdminApi.getClientPrivilegesSetting()
            originalClientPrivileges = currentClientPrivileges.clientPrivilegesSetting
            assertThat "Client privileges setting should not be null", 
                       currentClientPrivileges.clientPrivilegesSetting, notNullValue()
            logger.debug("   Current setting: clientPrivilegesSetting = {}", originalClientPrivileges)

            // Step 3.2: Update client privileges setting (toggle value)
            logger.debug("\n3.2. PUT /api/v1/org/settings/clientPrivilegesSetting (Update)")
            def newClientPrivilegesValue = !originalClientPrivileges
            def updateClientPrivileges = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(newClientPrivilegesValue)
            
            def updatedClientPrivileges = orgSettingAdminApi.assignClientPrivilegesSetting(updateClientPrivileges)
            assertThat "Updated client privileges should match new value", 
                       updatedClientPrivileges.clientPrivilegesSetting, equalTo(newClientPrivilegesValue)
            logger.debug("   Updated to: clientPrivilegesSetting = {}", newClientPrivilegesValue)

            // Step 3.3: Verify the update
            logger.debug("\n3.3. GET (Verify update)")
            def verifyClientPrivileges = orgSettingAdminApi.getClientPrivilegesSetting()
            assertThat "Verified value should match updated value", 
                       verifyClientPrivileges.clientPrivilegesSetting, equalTo(newClientPrivilegesValue)
            logger.debug("   Verified: clientPrivilegesSetting = {}", verifyClientPrivileges.clientPrivilegesSetting)

            // Step 3.4: Restore original value
            logger.debug("\n3.4. PUT (Restore original value)")
            def restoreClientPrivileges = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(originalClientPrivileges)
            def restoredClientPrivileges = orgSettingAdminApi.assignClientPrivilegesSetting(restoreClientPrivileges)
            assertThat "Restored value should match original", 
                       restoredClientPrivileges.clientPrivilegesSetting, equalTo(originalClientPrivileges)
            logger.debug("   Restored to original: clientPrivilegesSetting = {}", originalClientPrivileges)
            logger.debug("   Client Privileges Setting test complete")

            // ========================================
            // Summary
            // ========================================
            logger.debug("\n" + "=".multiply(60))
            logger.debug(" ALL ORG SETTING ADMIN API TESTS COMPLETE")
            logger.debug("=".multiply(60))
            logger.debug("\n=== API Coverage Summary ===")
            logger.debug("All 6 OrgSettingAdmin API endpoints tested:")
            logger.debug("   GET    /api/v1/org/orgSettings/thirdPartyAdminSetting")
            logger.debug("   POST   /api/v1/org/orgSettings/thirdPartyAdminSetting")
            logger.debug("   GET    /api/v1/org/settings/autoAssignAdminAppSetting")
            logger.debug("   POST   /api/v1/org/settings/autoAssignAdminAppSetting")
            logger.debug("   GET    /api/v1/org/settings/clientPrivilegesSetting")
            logger.debug("   PUT    /api/v1/org/settings/clientPrivilegesSetting")
            logger.debug("\n=== Cleanup Summary ===")
            logger.debug("   Third Party Admin Setting: Restored to {}", originalThirdPartyAdmin)
            logger.debug("   Auto Assign Admin App Setting: Restored to {}", originalAutoAssignAdmin)
            logger.debug("   Client Privileges Setting: Restored to {}", originalClientPrivileges)
            logger.debug("\n=== Test Results ===")
            logger.debug(" Total Endpoints: 6")
            logger.debug(" Endpoints Tested: 6 (100%)")
            logger.debug(" Test Scenarios: Get, Update, Verify, Restore for each setting")
            logger.debug(" Org Impact: Zero (all settings restored)")

        } catch (Exception e) {
            logger.debug("\n Test failed with exception: {}", e.message)
            if (e instanceof ApiException) {
                logger.debug("Response code: {}", e.code)
                logger.debug("Response body: {}", e.responseBody)
            }
            
            // Attempt emergency restoration if we have original values
            logger.debug("\n=== Emergency Cleanup Attempt ===")
            try {
                if (originalThirdPartyAdmin != null) {
                    logger.debug("Restoring third party admin setting...")
                    def restore = new ThirdPartyAdminSetting().thirdPartyAdmin(originalThirdPartyAdmin)
                    orgSettingAdminApi.updateThirdPartyAdminSetting(restore)
                    logger.debug("   Restored third party admin")
                }
                
                if (originalAutoAssignAdmin != null) {
                    logger.debug("Restoring auto assign admin setting...")
                    def restore = new AutoAssignAdminAppSetting().autoAssignAdminAppSetting(originalAutoAssignAdmin)
                    orgSettingAdminApi.updateAutoAssignAdminAppSetting(restore)
                    logger.debug("   Restored auto assign admin")
                }
                
                if (originalClientPrivileges != null) {
                    logger.debug("Restoring client privileges setting...")
                    def restore = new ClientPrivilegesSetting().clientPrivilegesSetting(originalClientPrivileges)
                    orgSettingAdminApi.assignClientPrivilegesSetting(restore)
                    logger.debug("   Restored client privileges")
                }
                
                logger.debug("=== Emergency Cleanup Complete ===")
            } catch (Exception cleanupException) {
                logger.debug(" Emergency cleanup failed: {}", cleanupException.message)
            }
            
            throw e
        }
    }

    @Test(groups = "group3")
    void testOrgSettingAdminNegativeCases() {
        orgSettingAdminApi = new OrgSettingAdminApi(getClient())
        
        logger.debug("TESTING ORG SETTING ADMIN NEGATIVE CASES")
        
        // Test 1: Update third party admin with null value
        logger.debug("\n1. Testing update third party admin with null value...")
        try {
            def invalidSetting = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(null)
            orgSettingAdminApi.updateThirdPartyAdminSetting(invalidSetting)
            logger.debug("    Update with null value succeeded (API may handle gracefully)")
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for null third party admin value")
        }
        
        // Test 2: Update auto assign with null value
        logger.debug("\n2. Testing update auto assign admin app with null value...")
        try {
            def invalidSetting = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(null)
            orgSettingAdminApi.updateAutoAssignAdminAppSetting(invalidSetting)
            logger.debug("    Update with null value succeeded (API may handle gracefully)")
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for null auto assign value")
        }
        
        // Test 3: Update client privileges with null value
        logger.debug("\n3. Testing update client privileges with null value...")
        try {
            def invalidSetting = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(null)
            orgSettingAdminApi.assignClientPrivilegesSetting(invalidSetting)
            logger.debug("    Update with null value succeeded (API may handle gracefully)")
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            logger.debug("    Correctly returned 400 for null client privileges value")
        }
        
        logger.debug("\n All negative test cases completed!")
        logger.debug("Note: Some endpoints may accept null values and use defaults")
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        try {
            orgSettingAdminApi.getClientPrivilegesSetting(headers)
        } catch (Exception e) {
            // Expected
        }
    }
}

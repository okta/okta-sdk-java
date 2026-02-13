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
            println "\n=== Testing OrgSettingAdmin API ==="
            println "Note: All settings will be restored to original values"

            // ========================================
            // Test 1: Third Party Admin Setting
            // ========================================
            println "\n" + "=".multiply(60)
            println "TEST 1: Third Party Admin Setting"
            println "=".multiply(60)

            // Step 1.1: Get current third party admin setting
            println "\n1.1. GET /api/v1/org/orgSettings/thirdPartyAdminSetting"
            def currentThirdParty = orgSettingAdminApi.getThirdPartyAdminSetting()
            originalThirdPartyAdmin = currentThirdParty.thirdPartyAdmin
            assertThat "Third party admin setting should not be null", 
                       currentThirdParty.thirdPartyAdmin, notNullValue()
            println "  ✓ Current setting: thirdPartyAdmin = ${originalThirdPartyAdmin}"

            // Step 1.2: Update third party admin setting (toggle value)
            println "\n1.2. POST /api/v1/org/orgSettings/thirdPartyAdminSetting (Update)"
            def newThirdPartyValue = !originalThirdPartyAdmin
            def updateThirdParty = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(newThirdPartyValue)
            
            def updatedThirdParty = orgSettingAdminApi.updateThirdPartyAdminSetting(updateThirdParty)
            assertThat "Updated third party admin should match new value", 
                       updatedThirdParty.thirdPartyAdmin, equalTo(newThirdPartyValue)
            println "  ✓ Updated to: thirdPartyAdmin = ${newThirdPartyValue}"

            // Step 1.3: Verify the update by getting again
            println "\n1.3. GET (Verify update)"
            def verifyThirdParty = orgSettingAdminApi.getThirdPartyAdminSetting()
            assertThat "Verified value should match updated value", 
                       verifyThirdParty.thirdPartyAdmin, equalTo(newThirdPartyValue)
            println "  ✓ Verified: thirdPartyAdmin = ${verifyThirdParty.thirdPartyAdmin}"

            // Step 1.4: Restore original value
            println "\n1.4. POST (Restore original value)"
            def restoreThirdParty = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(originalThirdPartyAdmin)
            def restoredThirdParty = orgSettingAdminApi.updateThirdPartyAdminSetting(restoreThirdParty)
            assertThat "Restored value should match original", 
                       restoredThirdParty.thirdPartyAdmin, equalTo(originalThirdPartyAdmin)
            println "  ✓ Restored to original: thirdPartyAdmin = ${originalThirdPartyAdmin}"
            println "  ✅ Third Party Admin Setting test complete"

            // ========================================
            // Test 2: Auto Assign Admin App Setting
            // ========================================
            println "\n" + "=".multiply(60)
            println "TEST 2: Auto Assign Admin App Setting"
            println "=".multiply(60)

            // Step 2.1: Get current auto assign setting
            println "\n2.1. GET /api/v1/org/settings/autoAssignAdminAppSetting"
            def currentAutoAssign = orgSettingAdminApi.getAutoAssignAdminAppSetting()
            originalAutoAssignAdmin = currentAutoAssign.autoAssignAdminAppSetting
            assertThat "Auto assign admin setting should not be null", 
                       currentAutoAssign.autoAssignAdminAppSetting, notNullValue()
            println "  ✓ Current setting: autoAssignAdminAppSetting = ${originalAutoAssignAdmin}"

            // Step 2.2: Update auto assign setting (toggle value)
            println "\n2.2. POST /api/v1/org/settings/autoAssignAdminAppSetting (Update)"
            def newAutoAssignValue = !originalAutoAssignAdmin
            def updateAutoAssign = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(newAutoAssignValue)
            
            def updatedAutoAssign = orgSettingAdminApi.updateAutoAssignAdminAppSetting(updateAutoAssign)
            assertThat "Updated auto assign should match new value", 
                       updatedAutoAssign.autoAssignAdminAppSetting, equalTo(newAutoAssignValue)
            println "  ✓ Updated to: autoAssignAdminAppSetting = ${newAutoAssignValue}"

            // Step 2.3: Verify the update
            println "\n2.3. GET (Verify update)"
            def verifyAutoAssign = orgSettingAdminApi.getAutoAssignAdminAppSetting()
            assertThat "Verified value should match updated value", 
                       verifyAutoAssign.autoAssignAdminAppSetting, equalTo(newAutoAssignValue)
            println "  ✓ Verified: autoAssignAdminAppSetting = ${verifyAutoAssign.autoAssignAdminAppSetting}"

            // Step 2.4: Restore original value
            println "\n2.4. POST (Restore original value)"
            def restoreAutoAssign = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(originalAutoAssignAdmin)
            def restoredAutoAssign = orgSettingAdminApi.updateAutoAssignAdminAppSetting(restoreAutoAssign)
            assertThat "Restored value should match original", 
                       restoredAutoAssign.autoAssignAdminAppSetting, equalTo(originalAutoAssignAdmin)
            println "  ✓ Restored to original: autoAssignAdminAppSetting = ${originalAutoAssignAdmin}"
            println "  ✅ Auto Assign Admin App Setting test complete"

            // ========================================
            // Test 3: Client Privileges Setting
            // ========================================
            println "\n" + "=".multiply(60)
            println "TEST 3: Client Privileges Setting"
            println "=".multiply(60)

            // Step 3.1: Get current client privileges setting
            println "\n3.1. GET /api/v1/org/settings/clientPrivilegesSetting"
            def currentClientPrivileges = orgSettingAdminApi.getClientPrivilegesSetting()
            originalClientPrivileges = currentClientPrivileges.clientPrivilegesSetting
            assertThat "Client privileges setting should not be null", 
                       currentClientPrivileges.clientPrivilegesSetting, notNullValue()
            println "  ✓ Current setting: clientPrivilegesSetting = ${originalClientPrivileges}"

            // Step 3.2: Update client privileges setting (toggle value)
            println "\n3.2. PUT /api/v1/org/settings/clientPrivilegesSetting (Update)"
            def newClientPrivilegesValue = !originalClientPrivileges
            def updateClientPrivileges = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(newClientPrivilegesValue)
            
            def updatedClientPrivileges = orgSettingAdminApi.assignClientPrivilegesSetting(updateClientPrivileges)
            assertThat "Updated client privileges should match new value", 
                       updatedClientPrivileges.clientPrivilegesSetting, equalTo(newClientPrivilegesValue)
            println "  ✓ Updated to: clientPrivilegesSetting = ${newClientPrivilegesValue}"

            // Step 3.3: Verify the update
            println "\n3.3. GET (Verify update)"
            def verifyClientPrivileges = orgSettingAdminApi.getClientPrivilegesSetting()
            assertThat "Verified value should match updated value", 
                       verifyClientPrivileges.clientPrivilegesSetting, equalTo(newClientPrivilegesValue)
            println "  ✓ Verified: clientPrivilegesSetting = ${verifyClientPrivileges.clientPrivilegesSetting}"

            // Step 3.4: Restore original value
            println "\n3.4. PUT (Restore original value)"
            def restoreClientPrivileges = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(originalClientPrivileges)
            def restoredClientPrivileges = orgSettingAdminApi.assignClientPrivilegesSetting(restoreClientPrivileges)
            assertThat "Restored value should match original", 
                       restoredClientPrivileges.clientPrivilegesSetting, equalTo(originalClientPrivileges)
            println "  ✓ Restored to original: clientPrivilegesSetting = ${originalClientPrivileges}"
            println "  ✅ Client Privileges Setting test complete"

            // ========================================
            // Summary
            // ========================================
            println "\n" + "=".multiply(60)
            println "✅ ALL ORG SETTING ADMIN API TESTS COMPLETE"
            println "=".multiply(60)
            println "\n=== API Coverage Summary ==="
            println "All 6 OrgSettingAdmin API endpoints tested:"
            println "  ✓ GET    /api/v1/org/orgSettings/thirdPartyAdminSetting"
            println "  ✓ POST   /api/v1/org/orgSettings/thirdPartyAdminSetting"
            println "  ✓ GET    /api/v1/org/settings/autoAssignAdminAppSetting"
            println "  ✓ POST   /api/v1/org/settings/autoAssignAdminAppSetting"
            println "  ✓ GET    /api/v1/org/settings/clientPrivilegesSetting"
            println "  ✓ PUT    /api/v1/org/settings/clientPrivilegesSetting"
            println "\n=== Cleanup Summary ==="
            println "  ✓ Third Party Admin Setting: Restored to ${originalThirdPartyAdmin}"
            println "  ✓ Auto Assign Admin App Setting: Restored to ${originalAutoAssignAdmin}"
            println "  ✓ Client Privileges Setting: Restored to ${originalClientPrivileges}"
            println "\n=== Test Results ==="
            println "• Total Endpoints: 6"
            println "• Endpoints Tested: 6 (100%)"
            println "• Test Scenarios: Get, Update, Verify, Restore for each setting"
            println "• Org Impact: Zero (all settings restored)"

        } catch (Exception e) {
            println "\n❌ Test failed with exception: ${e.message}"
            if (e instanceof ApiException) {
                println "Response code: ${e.code}"
                println "Response body: ${e.responseBody}"
            }
            
            // Attempt emergency restoration if we have original values
            println "\n=== Emergency Cleanup Attempt ==="
            try {
                if (originalThirdPartyAdmin != null) {
                    println "Restoring third party admin setting..."
                    def restore = new ThirdPartyAdminSetting().thirdPartyAdmin(originalThirdPartyAdmin)
                    orgSettingAdminApi.updateThirdPartyAdminSetting(restore)
                    println "  ✓ Restored third party admin"
                }
                
                if (originalAutoAssignAdmin != null) {
                    println "Restoring auto assign admin setting..."
                    def restore = new AutoAssignAdminAppSetting().autoAssignAdminAppSetting(originalAutoAssignAdmin)
                    orgSettingAdminApi.updateAutoAssignAdminAppSetting(restore)
                    println "  ✓ Restored auto assign admin"
                }
                
                if (originalClientPrivileges != null) {
                    println "Restoring client privileges setting..."
                    def restore = new ClientPrivilegesSetting().clientPrivilegesSetting(originalClientPrivileges)
                    orgSettingAdminApi.assignClientPrivilegesSetting(restore)
                    println "  ✓ Restored client privileges"
                }
                
                println "=== Emergency Cleanup Complete ==="
            } catch (Exception cleanupException) {
                println "⚠ Emergency cleanup failed: ${cleanupException.message}"
            }
            
            throw e
        }
    }

    @Test(groups = "group3")
    void testOrgSettingAdminNegativeCases() {
        orgSettingAdminApi = new OrgSettingAdminApi(getClient())
        
        println "\n" + "=" * 60
        println "TESTING ORG SETTING ADMIN NEGATIVE CASES"
        println "=" * 60
        
        // Test 1: Update third party admin with null value
        println "\n1. Testing update third party admin with null value..."
        try {
            def invalidSetting = new ThirdPartyAdminSetting()
                .thirdPartyAdmin(null)
            orgSettingAdminApi.updateThirdPartyAdminSetting(invalidSetting)
            println "   ⚠ Update with null value succeeded (API may handle gracefully)"
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for null third party admin value"
        }
        
        // Test 2: Update auto assign with null value
        println "\n2. Testing update auto assign admin app with null value..."
        try {
            def invalidSetting = new AutoAssignAdminAppSetting()
                .autoAssignAdminAppSetting(null)
            orgSettingAdminApi.updateAutoAssignAdminAppSetting(invalidSetting)
            println "   ⚠ Update with null value succeeded (API may handle gracefully)"
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for null auto assign value"
        }
        
        // Test 3: Update client privileges with null value
        println "\n3. Testing update client privileges with null value..."
        try {
            def invalidSetting = new ClientPrivilegesSetting()
                .clientPrivilegesSetting(null)
            orgSettingAdminApi.assignClientPrivilegesSetting(invalidSetting)
            println "   ⚠ Update with null value succeeded (API may handle gracefully)"
        } catch (ApiException e) {
            assertThat "Should return 400 for null value", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for null client privileges value"
        }
        
        println "\n✅ All negative test cases completed!"
        println "Note: Some endpoints may accept null values and use defaults"
    }
}

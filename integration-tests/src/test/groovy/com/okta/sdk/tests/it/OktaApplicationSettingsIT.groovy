/*
 * Copyright (c) 2024-Present, Okta, Inc.
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

import com.okta.sdk.resource.api.OktaApplicationSettingsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.AdminConsoleSettings
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for OktaApplicationSettingsApi - First-Party Okta Application Settings
 * 
 * Tests Okta application settings management:
 * 1. GET /api/v1/apps/{appName}/settings - Get first-party app settings
 * 2. PUT /api/v1/apps/{appName}/settings - Replace first-party app settings
 * 
 * These endpoints manage settings for Okta's own applications like admin-console,
 * including session timeout configurations.
 * 
 * Supported settings:
 * - SessionIdleTimeoutMinutes: 5-120 minutes
 * - SessionMaxLifetimeMinutes: 5-1440 minutes (24 hours)
 * 
 * NOTE: Changes to admin-console settings affect session behavior for admin users.
 * Tests restore original settings after execution.
 */
class OktaApplicationSettingsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(OktaApplicationSettingsIT)
    private static final String APP_NAME = "admin-console"
    
    private OktaApplicationSettingsApi settingsApi
    private AdminConsoleSettings originalSettings
    
    OktaApplicationSettingsIT() {
        settingsApi = new OktaApplicationSettingsApi(getClient())
    }

    @BeforeClass
    void setUp() {
        // Save original settings for restoration
        try {
            originalSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
            logger.info("Saved original settings: idle={}, max={}", 
                originalSettings.getSessionIdleTimeoutMinutes(), 
                originalSettings.getSessionMaxLifetimeMinutes())
        } catch (Exception e) {
            logger.warn("Could not save original settings: {}", e.getMessage())
        }
    }

    @AfterClass
    void tearDown() {
        // Restore original settings
        if (originalSettings != null) {
            try {
                settingsApi.replaceFirstPartyAppSettings(APP_NAME, originalSettings)
                logger.info("Restored original settings")
                TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
            } catch (Exception e) {
                logger.warn("Could not restore original settings: {}", e.getMessage())
            }
        }
    }

    // ========================================
    // Test 1: Get First-Party App Settings
    // ========================================
    @Test
    @Scenario("get-first-party-app-settings")
    void testGetFirstPartyAppSettings() {
        // Get current settings for admin-console
        AdminConsoleSettings settings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        
        // Verify settings structure
        assertThat("Settings should not be null", settings, notNullValue())
        assertThat("Session idle timeout should be set", 
            settings.getSessionIdleTimeoutMinutes(), notNullValue())
        assertThat("Session max lifetime should be set", 
            settings.getSessionMaxLifetimeMinutes(), notNullValue())
        
        // Verify values are within valid ranges
        assertThat("Idle timeout should be in valid range (5-120)", 
            settings.getSessionIdleTimeoutMinutes(), allOf(greaterThanOrEqualTo(5), lessThanOrEqualTo(120)))
        assertThat("Max lifetime should be in valid range (5-1440)", 
            settings.getSessionMaxLifetimeMinutes(), allOf(greaterThanOrEqualTo(5), lessThanOrEqualTo(1440)))
        
        logger.info("Current settings: idle={} min, max={} min", 
            settings.getSessionIdleTimeoutMinutes(), 
            settings.getSessionMaxLifetimeMinutes())
    }

    // ========================================
    // Test 2: Replace App Settings
    // ========================================
    @Test
    @Scenario("replace-app-settings")
    void testReplaceFirstPartyAppSettings() {
        // Get current settings
        AdminConsoleSettings currentSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        
        // Calculate new values (toggle between different valid values)
        Integer newIdleTimeout = currentSettings.getSessionIdleTimeoutMinutes() == 15 ? 30 : 15
        Integer newMaxLifetime = currentSettings.getSessionMaxLifetimeMinutes() == 720 ? 1440 : 720
        
        // Create updated settings
        AdminConsoleSettings updatedSettings = new AdminConsoleSettings()
        updatedSettings.setSessionIdleTimeoutMinutes(newIdleTimeout)
        updatedSettings.setSessionMaxLifetimeMinutes(newMaxLifetime)
        
        // Replace settings
        AdminConsoleSettings result = 
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, updatedSettings)
        
        // Verify result
        assertThat("Result should not be null", result, notNullValue())
        assertThat("Idle timeout should be updated", 
            result.getSessionIdleTimeoutMinutes(), is(newIdleTimeout))
        assertThat("Max lifetime should be updated", 
            result.getSessionMaxLifetimeMinutes(), is(newMaxLifetime))
        
        TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
        
        // Verify persistence with GET
        AdminConsoleSettings verifySettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        assertThat("Persisted idle timeout should match", 
            verifySettings.getSessionIdleTimeoutMinutes(), is(newIdleTimeout))
        assertThat("Persisted max lifetime should match", 
            verifySettings.getSessionMaxLifetimeMinutes(), is(newMaxLifetime))
    }

    // ========================================
    // Test 3: Multiple Updates in Sequence
    // ========================================
    @Test
    @Scenario("multiple-sequential-updates")
    void testMultipleSequentialUpdates() {
        // First update
        AdminConsoleSettings settings1 = new AdminConsoleSettings()
        settings1.setSessionIdleTimeoutMinutes(15)
        settings1.setSessionMaxLifetimeMinutes(720)
        
        AdminConsoleSettings result1 = settingsApi.replaceFirstPartyAppSettings(APP_NAME, settings1)
        assertThat("First update idle timeout should match", result1.getSessionIdleTimeoutMinutes(), is(15))
        assertThat("First update max lifetime should match", result1.getSessionMaxLifetimeMinutes(), is(720))
        
        TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
        
        // Second update
        AdminConsoleSettings settings2 = new AdminConsoleSettings()
        settings2.setSessionIdleTimeoutMinutes(30)
        settings2.setSessionMaxLifetimeMinutes(1440)
        
        AdminConsoleSettings result2 = settingsApi.replaceFirstPartyAppSettings(APP_NAME, settings2)
        assertThat("Second update idle timeout should match", result2.getSessionIdleTimeoutMinutes(), is(30))
        assertThat("Second update max lifetime should match", result2.getSessionMaxLifetimeMinutes(), is(1440))
        
        TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
        
        // Third update
        AdminConsoleSettings settings3 = new AdminConsoleSettings()
        settings3.setSessionIdleTimeoutMinutes(60)
        settings3.setSessionMaxLifetimeMinutes(360)
        
        AdminConsoleSettings result3 = settingsApi.replaceFirstPartyAppSettings(APP_NAME, settings3)
        assertThat("Third update idle timeout should match", result3.getSessionIdleTimeoutMinutes(), is(60))
        assertThat("Third update max lifetime should match", result3.getSessionMaxLifetimeMinutes(), is(360))
        
        // Verify final state (retry for eventual consistency)
        AdminConsoleSettings finalSettings = null
        int maxRetries = 10
        for (int i = 0; i < maxRetries; i++) {
            finalSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
            if (finalSettings.getSessionIdleTimeoutMinutes() == 60) {
                break
            }
            TimeUnit.MILLISECONDS.sleep(2000)
        }
        assertThat("Final idle timeout should match last update", 
            finalSettings.getSessionIdleTimeoutMinutes(), is(60))
        assertThat("Final max lifetime should match last update", 
            finalSettings.getSessionMaxLifetimeMinutes(), is(360))
    }

    // ========================================
    // Test 6: Boundary Values - Minimum
    // ========================================
    @Test
    @Scenario("boundary-values-minimum")
    void testBoundaryValuesMinimum() {
        // Test minimum allowed values
        AdminConsoleSettings minSettings = new AdminConsoleSettings()
        minSettings.setSessionIdleTimeoutMinutes(5)
        minSettings.setSessionMaxLifetimeMinutes(5)
        
        AdminConsoleSettings result = settingsApi.replaceFirstPartyAppSettings(APP_NAME, minSettings)
        
        assertThat("Minimum idle timeout should be accepted", 
            result.getSessionIdleTimeoutMinutes(), is(5))
        assertThat("Minimum max lifetime should be accepted", 
            result.getSessionMaxLifetimeMinutes(), is(5))
        
        TimeUnit.MILLISECONDS.sleep(Math.max(getTestOperationDelay(), 2000))
        
        // Verify persistence (retry for eventual consistency)
        AdminConsoleSettings verifySettings = null
        int maxRetries = 10
        for (int i = 0; i < maxRetries; i++) {
            verifySettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
            if (verifySettings.getSessionIdleTimeoutMinutes() == 5) {
                break
            }
            TimeUnit.MILLISECONDS.sleep(2000)
        }
        assertThat("Persisted minimum idle timeout should match", 
            verifySettings.getSessionIdleTimeoutMinutes(), is(5))
        assertThat("Persisted minimum max lifetime should match", 
            verifySettings.getSessionMaxLifetimeMinutes(), is(5))
    }

    // ========================================
    // Test 7: Boundary Values - Maximum
    // ========================================
    @Test
    @Scenario("boundary-values-maximum")
    void testBoundaryValuesMaximum() {
        // Test maximum allowed values
        AdminConsoleSettings maxSettings = new AdminConsoleSettings()
        maxSettings.setSessionIdleTimeoutMinutes(120)
        maxSettings.setSessionMaxLifetimeMinutes(1440)
        
        AdminConsoleSettings result = settingsApi.replaceFirstPartyAppSettings(APP_NAME, maxSettings)
        
        assertThat("Maximum idle timeout should be accepted", 
            result.getSessionIdleTimeoutMinutes(), is(120))
        assertThat("Maximum max lifetime should be accepted", 
            result.getSessionMaxLifetimeMinutes(), is(1440))
        
        TimeUnit.MILLISECONDS.sleep(Math.max(getTestOperationDelay(), 2000))
        
        // Verify persistence (retry for eventual consistency)
        AdminConsoleSettings verifyMaxSettings = null
        int maxRetries2 = 10
        for (int i = 0; i < maxRetries2; i++) {
            verifyMaxSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
            if (verifyMaxSettings.getSessionIdleTimeoutMinutes() == 120) {
                break
            }
            TimeUnit.MILLISECONDS.sleep(2000)
        }
        assertThat("Persisted maximum idle timeout should match", 
            verifyMaxSettings.getSessionIdleTimeoutMinutes(), is(120))
        assertThat("Persisted maximum max lifetime should match", 
            verifyMaxSettings.getSessionMaxLifetimeMinutes(), is(1440))
    }

    // ========================================
    // Test 8: Default Values
    // ========================================
    @Test
    @Scenario("default-values")
    void testDefaultValues() {
        // Test common default values
        AdminConsoleSettings defaultSettings = new AdminConsoleSettings()
        defaultSettings.setSessionIdleTimeoutMinutes(15)
        defaultSettings.setSessionMaxLifetimeMinutes(720)
        
        AdminConsoleSettings result = settingsApi.replaceFirstPartyAppSettings(APP_NAME, defaultSettings)
        
        assertThat("Default idle timeout should be set", 
            result.getSessionIdleTimeoutMinutes(), is(15))
        assertThat("Default max lifetime should be set", 
            result.getSessionMaxLifetimeMinutes(), is(720))
        
        TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
        
        // Verify persistence
        AdminConsoleSettings verifySettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        assertThat("Default values should persist", 
            verifySettings.getSessionIdleTimeoutMinutes(), is(15))
        assertThat("Default values should persist", 
            verifySettings.getSessionMaxLifetimeMinutes(), is(720))
    }

    // ========================================
    // Test 9: Error - Invalid App Name (GET)
    // ========================================
    @Test
    @Scenario("error-invalid-app-name-get")
    void testGetInvalidAppNameError() {
        String invalidAppName = "invalid-app-name-" + UUID.randomUUID().toString().substring(0, 8)
        
        ApiException exception = null
        try {
            settingsApi.getFirstPartyAppSettings(invalidAppName)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for invalid app name", exception, notNullValue())
        assertThat("Should return 400 or 404 for invalid app", 
            exception.getCode(), anyOf(is(400), is(404)))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
        assertThat("Error response body should not be null", 
            exception.getResponseBody(), notNullValue())
    }

    // ========================================
    // Test 7: Error - Null Settings
    // ========================================
    @Test
    @Scenario("error-null-settings")
    void testReplaceWithNullSettingsError() {
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, null)
        } catch (ApiException e) {
            exception = e
        } catch (IllegalArgumentException e) {
            // Java SDK may throw IllegalArgumentException for null required parameter
            assertThat("Should throw exception for null settings", e, notNullValue())
            assertThat("Error message should mention required parameter", 
                e.getMessage(), containsString("adminConsoleSettings"))
            return
        }
        
        if (exception != null) {
            assertThat("Should return 400 for null settings", exception.getCode(), is(400))
            assertThat("Error message should mention settings", 
                exception.getMessage(), containsString("adminConsoleSettings"))
        }
    }

    // ========================================
    // Test 12: Error - Idle Timeout Below Minimum
    // ========================================
    @Test
    @Scenario("error-idle-timeout-below-minimum")
    void testIdleTimeoutBelowMinimumError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(0)
        invalidSettings.setSessionMaxLifetimeMinutes(720)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for invalid idle timeout", exception, notNullValue())
        assertThat("Should return 400 for below minimum value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 9: Error - Idle Timeout Above Maximum
    // ========================================
    @Test
    @Scenario("error-idle-timeout-above-maximum")
    void testIdleTimeoutAboveMaximumError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(121)
        invalidSettings.setSessionMaxLifetimeMinutes(720)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for invalid idle timeout", exception, notNullValue())
        assertThat("Should return 400 for above maximum value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 10: Error - Max Lifetime Below Minimum
    // ========================================
    @Test
    @Scenario("error-max-lifetime-below-minimum")
    void testMaxLifetimeBelowMinimumError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(15)
        invalidSettings.setSessionMaxLifetimeMinutes(0)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for invalid max lifetime", exception, notNullValue())
        assertThat("Should return 400 for below minimum value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 11: Error - Max Lifetime Above Maximum
    // ========================================
    @Test
    @Scenario("error-max-lifetime-above-maximum")
    void testMaxLifetimeAboveMaximumError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(15)
        invalidSettings.setSessionMaxLifetimeMinutes(1441)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for invalid max lifetime", exception, notNullValue())
        assertThat("Should return 400 for above maximum value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 12: Error - Negative Idle Timeout
    // ========================================
    @Test
    @Scenario("error-negative-idle-timeout")
    void testNegativeIdleTimeoutError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(-1)
        invalidSettings.setSessionMaxLifetimeMinutes(720)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for negative idle timeout", exception, notNullValue())
        assertThat("Should return 400 for negative value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 13: Error - Negative Max Lifetime
    // ========================================
    @Test
    @Scenario("error-negative-max-lifetime")
    void testNegativeMaxLifetimeError() {
        AdminConsoleSettings invalidSettings = new AdminConsoleSettings()
        invalidSettings.setSessionIdleTimeoutMinutes(15)
        invalidSettings.setSessionMaxLifetimeMinutes(-1)
        
        ApiException exception = null
        try {
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, invalidSettings)
        } catch (ApiException e) {
            exception = e
        }
        
        assertThat("Should throw ApiException for negative max lifetime", exception, notNullValue())
        assertThat("Should return 400 for negative value", exception.getCode(), is(400))
        assertThat("Error message should not be empty", 
            exception.getMessage(), not(isEmptyOrNullString()))
    }

    // ========================================
    // Test 14: Settings Restoration
    // ========================================
    @Test
    @Scenario("settings-restoration")
    void testSettingsRestoration() {
        // Get current settings
        AdminConsoleSettings currentSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        AdminConsoleSettings backupSettings = new AdminConsoleSettings()
        backupSettings.setSessionIdleTimeoutMinutes(currentSettings.getSessionIdleTimeoutMinutes())
        backupSettings.setSessionMaxLifetimeMinutes(currentSettings.getSessionMaxLifetimeMinutes())
        
        // Make a change
        AdminConsoleSettings newSettings = new AdminConsoleSettings()
        newSettings.setSessionIdleTimeoutMinutes(45)
        newSettings.setSessionMaxLifetimeMinutes(900)
        
        settingsApi.replaceFirstPartyAppSettings(APP_NAME, newSettings)
        TimeUnit.MILLISECONDS.sleep(Math.max(getTestOperationDelay(), 2000))
        
        // Verify change applied (retry for eventual consistency)
        AdminConsoleSettings changedSettings = null
        int maxRetries = 10
        for (int i = 0; i < maxRetries; i++) {
            changedSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
            if (changedSettings.getSessionIdleTimeoutMinutes() == 45) {
                break
            }
            TimeUnit.MILLISECONDS.sleep(2000)
        }
        assertThat("Changed idle timeout should match", 
            changedSettings.getSessionIdleTimeoutMinutes(), is(45))
        
        // Restore original settings
        AdminConsoleSettings restoredResult = 
            settingsApi.replaceFirstPartyAppSettings(APP_NAME, backupSettings)
        
        assertThat("Restored idle timeout should match backup", 
            restoredResult.getSessionIdleTimeoutMinutes(), 
            is(backupSettings.getSessionIdleTimeoutMinutes()))
        assertThat("Restored max lifetime should match backup", 
            restoredResult.getSessionMaxLifetimeMinutes(), 
            is(backupSettings.getSessionMaxLifetimeMinutes()))
        
        TimeUnit.MILLISECONDS.sleep(getTestOperationDelay())
        
        // Verify restoration persisted
        AdminConsoleSettings finalSettings = settingsApi.getFirstPartyAppSettings(APP_NAME)
        assertThat("Final settings should match backup", 
            finalSettings.getSessionIdleTimeoutMinutes(), 
            is(backupSettings.getSessionIdleTimeoutMinutes()))
    }
}

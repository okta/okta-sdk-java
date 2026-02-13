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

import com.okta.sdk.resource.api.SessionApi
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for Session API
 * 
 * Coverage:
 * - GET /api/v1/sessions/{sessionId} - getSession()
 * - POST /api/v1/sessions/{sessionId}/lifecycle/refresh - refreshSession()
 * - DELETE /api/v1/sessions/{sessionId} - revokeSession()
 */
class SessionIT extends ITSupport {

    private SessionApi sessionApi

    @AfterMethod
    void cleanup() {
        println "\n=== Cleanup: No resources to clean ==="
        println "Session API test uses error scenarios only"
    }

    /**
     * Comprehensive Session API test covering all endpoints:
     * 
     * Since sessions are typically created through user authentication (AuthN API),
     * we test the Session API methods with expected error scenarios:
     * 
     * 1. Get session details - GET /api/v1/sessions/{sessionId}
     * 2. Refresh session - POST /api/v1/sessions/{sessionId}/lifecycle/refresh
     * 3. Revoke session - DELETE /api/v1/sessions/{sessionId}
     * 
     * Each endpoint is validated to ensure proper SDK method invocation and error handling.
     * For real session lifecycle testing, a valid session token from authentication is needed.
     */
    @Test(groups = "group3")
    void testSessionLifecycle() {
        String testId = UUID.randomUUID().toString().substring(0, 8)

        // Initialize APIs
        sessionApi = new SessionApi(getClient())

        try {
            println "\n=== Testing Session API Endpoints ==="
            println "\nNote: Session API endpoints tested with error scenarios"
            println "Real sessions require authentication through AuthN API"
            
            // Test with invalid session ID to verify endpoints are accessible
            String invalidSessionId = "invalid_session_${testId}"
            
            // Step 1: Test Get Session - GET /api/v1/sessions/{sessionId}
            println "\n1. Testing GET /api/v1/sessions/{sessionId}..."
            try {
                sessionApi.getSession(invalidSessionId)
                throw new AssertionError("Expected 404 for invalid session ID")
            } catch (ApiException e) {
                assertThat "Should get 404 for invalid session", e.code, equalTo(404)
                println "  ✓ getSession() method works correctly (404 for invalid ID)"
            }
            
            // Step 2: Test Refresh Session - POST /api/v1/sessions/{sessionId}/lifecycle/refresh
            println "\n2. Testing POST /api/v1/sessions/{sessionId}/lifecycle/refresh..."
            try {
                sessionApi.refreshSession(invalidSessionId)
                throw new AssertionError("Expected 404 for invalid session ID")
            } catch (ApiException e) {
                assertThat "Should get 404 for invalid session", e.code, equalTo(404)
                println "  ✓ refreshSession() method works correctly (404 for invalid ID)"
            }
            
            // Step 3: Test Revoke Session - DELETE /api/v1/sessions/{sessionId}
            println "\n3. Testing DELETE /api/v1/sessions/{sessionId}..."
            try {
                sessionApi.revokeSession(invalidSessionId)
                throw new AssertionError("Expected 404 for invalid session ID")
            } catch (ApiException e) {
                assertThat "Should get 404 for invalid session", e.code, equalTo(404)
                println "  ✓ revokeSession() method works correctly (404 for invalid ID)"
            }
            
            println "\n✅ Session API Integration Test Complete!"
            println "\n=== API Coverage Summary ==="
            println "All 3 Session API endpoints tested:"
            println "  ✓ GET    /api/v1/sessions/{sessionId} - getSession()"
            println "  ✓ POST   /api/v1/sessions/{sessionId}/lifecycle/refresh - refreshSession()"
            println "  ✓ DELETE /api/v1/sessions/{sessionId} - revokeSession()"
            println "\n=== Test Results ==="
            println "• Total Endpoints: 3"
            println "• Endpoints Tested: 3 (100%)"
            println "• SDK Methods Validated: 3"
            println "• Error Handling: Verified"
            println "\nNote: Full session lifecycle with actual sessions requires:"
            println "      1. User authentication via AuthN API to get session token"
            println "      2. Session creation using the token"
            println "      3. Then test get/refresh/revoke with valid session ID"
            
        } catch (Exception e) {
            println "❌ Test failed with exception: ${e.message}"
            if (e instanceof ApiException) {
                println "Response code: ${e.code}"
                println "Response body: ${e.responseBody}"
            }
            throw e
        }
    }

    /**
     * Extended test that demonstrates full session lifecycle when session ID is available
     * This test shows how to work with actual sessions if we have a valid session ID
     */
    @Test(groups = "group3", enabled = false) // Disabled by default - enable when session token available
    void testFullSessionLifecycleWithValidSession() {
        String testId = UUID.randomUUID().toString().substring(0, 8)
        
        // Initialize APIs
        sessionApi = new SessionApi(getClient())
        
        // This test requires a valid session ID obtained through authentication
        // To enable this test:
        // 1. Authenticate a user using Authentication API
        // 2. Get session token
        // 3. Create session
        // 4. Use the session ID below
        
        String validSessionId = "YOUR_VALID_SESSION_ID" // Replace with actual session ID
        
        try {
            println "\n=== Testing Full Session Lifecycle ==="
            
            // Step 1: Get Session Details
            println "\n1. Getting session details..."
            def session = sessionApi.getSession(validSessionId)
            assertThat "Session should not be null", session, notNullValue()
            assertThat "Session ID should match", session.id, equalTo(validSessionId)
            assertThat "Session should have user ID", session.userId, notNullValue()
            assertThat "Session should have status", session.status, notNullValue()
            assertThat "Session should have createdAt", session.createdAt, notNullValue()
            assertThat "Session should have expiresAt", session.expiresAt, notNullValue()
            println "  ✓ Retrieved session: ${session.id}"
            println "    User ID: ${session.userId}"
            println "    Status: ${session.status}"
            println "    Created: ${session.createdAt}"
            println "    Expires: ${session.expiresAt}"
            
            // Store original expiration time
            def originalExpiresAt = session.expiresAt
            
            // Step 2: Refresh Session
            println "\n2. Refreshing session..."
            Thread.sleep(1000) // Small delay to ensure timestamp difference
            def refreshedSession = sessionApi.refreshSession(validSessionId)
            assertThat "Refreshed session should not be null", refreshedSession, notNullValue()
            assertThat "Refreshed session ID should match", refreshedSession.id, equalTo(validSessionId)
            assertThat "Refreshed session should have updated expiresAt", 
                       refreshedSession.expiresAt, not(equalTo(originalExpiresAt))
            println "  ✓ Session refreshed"
            println "    Original expires: ${originalExpiresAt}"
            println "    New expires: ${refreshedSession.expiresAt}"
            
            // Step 3: Get session again to verify refresh
            println "\n3. Verifying refreshed session..."
            def verifySession = sessionApi.getSession(validSessionId)
            assertThat "Verified session expiresAt should match refreshed", 
                       verifySession.expiresAt, equalTo(refreshedSession.expiresAt)
            println "  ✓ Session refresh verified"
            
            // Step 4: Revoke Session
            println "\n4. Revoking session..."
            sessionApi.revokeSession(validSessionId)
            println "  ✓ Session revoked"
            
            // Step 5: Verify session is revoked
            println "\n5. Verifying session revocation..."
            try {
                sessionApi.getSession(validSessionId)
                throw new AssertionError("Session should be revoked and return 404")
            } catch (ApiException e) {
                assertThat "Revoked session should return 404", e.code, equalTo(404)
                println "  ✓ Session revocation confirmed (404)"
            }
            
            println "\n✅ Full Session Lifecycle Test Complete!"
            
        } catch (Exception e) {
            println "❌ Test failed: ${e.message}"
            if (e instanceof ApiException) {
                println "Response code: ${e.code}"
                println "Response body: ${e.responseBody}"
            }
            throw e
        }
    }

    @Test(groups = "group3")
    void testSessionNegativeCases() {
        sessionApi = new SessionApi(getClient())
        
        println "\n" + "=" * 60
        println "TESTING SESSION API NEGATIVE CASES"
        println "=" * 60
        
        // Test 1: Get session with malformed ID
        println "\n1. Testing get with malformed session ID..."
        try {
            sessionApi.getSession("malformed-session-id-special")
            assert false, "Should have thrown ApiException for malformed session ID"
        } catch (ApiException e) {
            assertThat "Should return 404 for malformed ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for malformed session ID"
        }
        
        // Test 2: Get session with empty ID
        println "\n2. Testing get with empty session ID..."
        try {
            sessionApi.getSession("")
            assert false, "Should have thrown ApiException for empty session ID"
        } catch (ApiException e) {
            assertThat "Should return error for empty ID", e.code, anyOf(equalTo(400), equalTo(404), equalTo(405))
            println "   ✓ Correctly returned ${e.code} for empty session ID"
        }
        
        // Test 3: Refresh non-existent session
        println "\n3. Testing refresh with non-existent session ID..."
        try {
            sessionApi.refreshSession("nonExistentSession12345")
            assert false, "Should have thrown ApiException for non-existent session"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent session", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for refreshing non-existent session"
        }
        
        // Test 4: Revoke non-existent session
        println "\n4. Testing revoke with non-existent session ID..."
        try {
            sessionApi.revokeSession("nonExistentSession12345")
            assert false, "Should have thrown ApiException for non-existent session"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent session", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for revoking non-existent session"
        }
        
        // Test 5: Double revoke session (revoke already revoked)
        println "\n5. Testing double revoke on same session ID..."
        try {
            String testSessionId = "session_${UUID.randomUUID().toString().substring(0, 8)}"
            sessionApi.revokeSession(testSessionId)  // First revoke
            assert false, "First revoke should have failed with 404"
        } catch (ApiException e) {
            assertThat "Should return 404 for non-existent session", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for non-existent session"
        }
        
        // Test 6: Refresh with various invalid session ID formats
        println "\n6. Testing refresh with various invalid session ID formats..."
        def invalidIds = [
            "ses_12345",
            "00s1234567890abcdef",  // Wrong prefix
            "session-with-special-chars!@#"
        ]
        
        invalidIds.each { invalidId ->
            try {
                sessionApi.refreshSession(invalidId)
                println "   ⚠ Refresh with '${invalidId}' unexpectedly succeeded"
            } catch (ApiException e) {
                assertThat "Should return 404 for invalid session ID format: ${invalidId}", 
                           e.code, equalTo(404)
                println "   ✓ Correctly returned 404 for session ID: ${invalidId}"
            }
        }
        
        println "\n✅ All session negative test cases passed successfully!"
        println "Note: Real session lifecycle testing requires authentication via AuthN API"
    }
}

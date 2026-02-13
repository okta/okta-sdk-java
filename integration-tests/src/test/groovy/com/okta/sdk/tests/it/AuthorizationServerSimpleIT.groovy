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

import com.okta.sdk.resource.api.AuthorizationServerApi
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.model.AuthorizationServer
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Simple debug test for Authorization Server API
 */
class AuthorizationServerSimpleIT extends ITSupport {

    @Test(groups = "group3")
    @Scenario("authorization-server-debug-test")
    void debugListAuthorizationServers() {
        ApiClient client = getClient()
        AuthorizationServerApi api = new AuthorizationServerApi(client)
        
        println "Calling listAuthorizationServersPaged..."
        def result = []
        for (def server : api.listAuthorizationServersPaged(null, null, null)) {
            result.add(server)
            println "  - Found server: ${server.name} (${server.id})"
        }
        
        println "Total servers found: ${result.size()}"
        
        assertThat "Result should not be empty", result.size(), greaterThan(0)
    }

    @Test(groups = "group3")
    @Scenario("authorization-server-simple-negative-tests")
    void testAuthorizationServerSimpleNegativeCases() {
        ApiClient client = getClient()
        AuthorizationServerApi api = new AuthorizationServerApi(client)
        
        println "\n=========================================="
        println "Test: Authorization Server Simple Negative Cases"
        println "==========================================\n"

        // Test 1: Get with invalid ID
        println "1. Testing get with invalid authorization server ID..."
        try {
            api.getAuthorizationServer("aus_invalid_id_12345")
            assert false, "Should have thrown ApiException for invalid ID"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for invalid authorization server ID"
        }

        // Test 2: Create with empty name
        println "\n2. Testing create with empty name..."
        try {
            def invalidServer = new AuthorizationServer()
                .name("")  // Empty name should fail validation
                .description("Invalid server")
                .audiences(["api://test".toString()])
            api.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for empty name"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 400 for empty name", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for empty authorization server name"
        }

        // Test 3: Create with missing audiences
        println "\n3. Testing create with missing audiences..."
        try {
            def invalidServer = new AuthorizationServer()
                .name("test-server-invalid")
                .description("Invalid server without audiences")
                // Missing audiences
            api.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException for missing audiences"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 400 for missing audiences", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for missing audiences"
        }

        // Test 4: Update with invalid ID
        println "\n4. Testing update with invalid authorization server ID..."
        try {
            def server = new AuthorizationServer()
                .name("test-update-invalid")
                .description("Should fail")
                .audiences(["api://test".toString()])
            api.replaceAuthorizationServer("aus_invalid_id_12345", server)
            assert false, "Should have thrown ApiException for invalid ID"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for updating with invalid ID"
        }

        // Test 5: Activate with invalid ID
        println "\n5. Testing activate with invalid authorization server ID..."
        try {
            api.activateAuthorizationServer("aus_invalid_id_12345")
            assert false, "Should have thrown ApiException for invalid ID"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for activating with invalid ID"
        }

        // Test 6: Deactivate with invalid ID
        println "\n6. Testing deactivate with invalid authorization server ID..."
        try {
            api.deactivateAuthorizationServer("aus_invalid_id_12345")
            assert false, "Should have thrown ApiException for invalid ID"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deactivating with invalid ID"
        }

        // Test 7: Delete with invalid ID
        println "\n7. Testing delete with invalid authorization server ID..."
        try {
            api.deleteAuthorizationServer("aus_invalid_id_12345")
            assert false, "Should have thrown ApiException for invalid ID"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404 for invalid ID", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for deleting with invalid ID"
        }

        println "\n✅ All simple negative test cases passed successfully!"
    }
}

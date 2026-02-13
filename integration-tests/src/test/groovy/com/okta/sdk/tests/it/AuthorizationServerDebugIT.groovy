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
 * Debug test to understand the Authorization Server API behavior
 */
class AuthorizationServerDebugIT extends ITSupport {

    @Test(groups = "group3")
    @Scenario("authorization-server-debug-basic")
    void testBasicAuthServerOperations() {
        ApiClient client = getClient()
        AuthorizationServerApi api = new AuthorizationServerApi(client)
        
        // Test 1: Create an auth server
        println "Creating new authorization server..."
        def authServer = new AuthorizationServer()
            .name("test-server-${UUID.randomUUID().toString().substring(0, 8)}")
            .description("Debug test server")
            .audiences(["api://default"])
        
        def created = api.createAuthorizationServer(authServer)
        println "Created: ${created.id} - ${created.name}"
        assertThat "Should have ID", created.id, notNullValue()
        
        // Test 2: Get the auth server
        def retrieved = api.getAuthorizationServer(created.id)
        println "Retrieved: ${retrieved.id} - ${retrieved.name}"
        assertThat "IDs should match", retrieved.id, equalTo(created.id)
        
        // Test 3: Delete the auth server
        api.deleteAuthorizationServer(created.id)
        println "Deleted: ${created.id}"
        
        println "✅ Basic operations test passed!"
    }

    @Test(groups = "group3")
    @Scenario("authorization-server-debug-negative-tests")
    void testAuthServerNegativeCases() {
        ApiClient client = getClient()
        AuthorizationServerApi api = new AuthorizationServerApi(client)
        
        println "\n" + "=" * 60
        println "TESTING AUTHORIZATION SERVER DEBUG NEGATIVE CASES"
        println "=" * 60
        
        // Test 1: Get with invalid ID
        println "\n1. Testing get with invalid authorization server ID..."
        try {
            api.getAuthorizationServer("invalid_aus_12345")
            assert false, "Should have thrown ApiException"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for invalid ID"
        }
        
        // Test 2: Delete with invalid ID
        println "\n2. Testing delete with invalid authorization server ID..."
        try {
            api.deleteAuthorizationServer("invalid_aus_12345")
            assert false, "Should have thrown ApiException"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 404", e.code, equalTo(404)
            println "   ✓ Correctly returned 404 for delete with invalid ID"
        }
        
        // Test 3: Create with invalid data
        println "\n3. Testing create with missing required fields..."
        try {
            def invalidServer = new AuthorizationServer()
                .name("")  // Empty name
                .description("Invalid")
            api.createAuthorizationServer(invalidServer)
            assert false, "Should have thrown ApiException"
        } catch (com.okta.sdk.resource.client.ApiException e) {
            assertThat "Should return 400", e.code, equalTo(400)
            println "   ✓ Correctly returned 400 for empty name"
        }
        
        println "\n✅ All debug negative test cases passed!"
    }
}

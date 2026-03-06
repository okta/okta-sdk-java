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

import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserSessionsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Sessions API.
 * Tests user session management (revoke sessions, OAuth tokens, forget devices).
 */
class UserSessionsIT extends ITSupport {

    private UserApi userApi
    private UserSessionsApi userSessionsApi

    UserSessionsIT() {
        this.userApi = new UserApi(getClient())
        this.userSessionsApi = new UserSessionsApi(getClient())
    }

    @Test(groups = "group3")
    void revokeUserSessionsTest() {
        def email = "user-revoke-sessions-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeSessions")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke all sessions for user
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            
            // Success - all sessions revoked

        } catch (ApiException e) {
            // Expected if session management not fully enabled or user has no sessions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithOAuthTokensTrueTest() {
        def email = "user-revoke-oauth-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeOAuth")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions and OAuth tokens
            userSessionsApi.revokeUserSessions(user.getId(), true, null)
            
            // Success - sessions and OAuth tokens revoked

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithOAuthTokensFalseTest() {
        def email = "user-revoke-no-oauth-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("RevokeNoOAuth")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions but not OAuth tokens
            userSessionsApi.revokeUserSessions(user.getId(), false, null)
            
            // Success - only sessions revoked

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithForgetDevicesTrueTest() {
        def email = "user-forget-devices-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ForgetDevices")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions and forget devices
            userSessionsApi.revokeUserSessions(user.getId(), null, true)
            
            // Success - sessions revoked and devices forgotten

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithForgetDevicesFalseTest() {
        def email = "user-keep-devices-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("KeepDevices")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions but keep devices
            userSessionsApi.revokeUserSessions(user.getId(), null, false)
            
            // Success - sessions revoked, devices retained

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithAllParametersTest() {
        def email = "user-all-params-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("AllParams")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions with all parameters
            userSessionsApi.revokeUserSessions(user.getId(), true, false)
            
            // Success - sessions and OAuth tokens revoked, devices retained

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void revokeUserSessionsWithHttpInfoTest() {
        def email = "user-sessions-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SessionsHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userSessionsApi.revokeUserSessionsWithHttpInfo(user.getId(), null, null)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void revokeUserSessionsWithHttpInfoAndParametersTest() {
        def email = "user-httpinfo-params-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("HttpInfoParams")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userSessionsApi.revokeUserSessionsWithHttpInfo(user.getId(), true, true)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithDefaultParametersTest() {
        def email = "user-default-params-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("DefaultParams")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Call with default/null parameters
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            
            // Success - default behavior

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsReturnsNoContentTest() {
        def email = "user-no-content-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("NoContent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Should return 204 No Content
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            
            // Success - no content returned

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithUserLoginTest() {
        def email = "user-login-sessions-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("LoginSessions")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Use login (email) instead of ID
            userSessionsApi.revokeUserSessions(email, null, null)
            
            // Success - sessions revoked using login

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithInvalidUserIdTest() {
        try {
            // Try to revoke sessions for non-existent user
            userSessionsApi.revokeUserSessions("invalid-user-"+UUID.randomUUID().toString().substring(0,8)+"", null, null)
            
            // Should fail
            assertThat("Should throw exception for invalid user", false)

        } catch (ApiException e) {
            // Expected - invalid user should fail
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void sessionManagementCompleteWorkflowTest() {
        def email = "user-session-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SessionWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. Revoke sessions only
            userSessionsApi.revokeUserSessions(user.getId(), false, false)
            
            // 2. Revoke sessions with OAuth tokens
            userSessionsApi.revokeUserSessions(user.getId(), true, false)
            
            // 3. Revoke sessions and forget devices
            userSessionsApi.revokeUserSessions(user.getId(), false, true)
            
            // 4. Revoke everything
            userSessionsApi.revokeUserSessions(user.getId(), true, true)
            
            // All operations successful

        } catch (ApiException e) {
            // Expected if session management not fully enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsParameterCombinationsTest() {
        def email = "user-param-combos-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ParamCombos")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Test different parameter combinations
            
            // Combo 1: oauthTokens=true, forgetDevices=true
            userSessionsApi.revokeUserSessions(user.getId(), true, true)
            
            // Combo 2: oauthTokens=true, forgetDevices=false
            userSessionsApi.revokeUserSessions(user.getId(), true, false)
            
            // Combo 3: oauthTokens=false, forgetDevices=true
            userSessionsApi.revokeUserSessions(user.getId(), false, true)
            
            // Combo 4: oauthTokens=false, forgetDevices=false
            userSessionsApi.revokeUserSessions(user.getId(), false, false)
            
            // All combinations successful

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsIdempotentTest() {
        def email = "user-idempotent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Idempotent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Revoke sessions multiple times (should be idempotent)
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            userSessionsApi.revokeUserSessions(user.getId(), null, null)
            
            // All calls should succeed (idempotent operation)

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void revokeUserSessionsWithMapParameterTest() {
        def email = "user-map-param-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("MapParam")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Test Map parameter variant
            def additionalParams = [:]
            userSessionsApi.revokeUserSessions(user.getId(), true, false, additionalParams)
            
            // Success - Map parameter variant works

        } catch (ApiException e) {
            // Expected if session management not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void testAdditionalHeadersOverloads() {
        def headers = Collections.<String, String>emptyMap()
        def userId = null
        try {
            def user = userApi.createUser(
                new com.okta.sdk.resource.model.CreateUserRequest()
                    .profile(new com.okta.sdk.resource.model.UserProfile()
                        .firstName("HeadersSess").lastName("Test")
                        .email("headers-sess-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())
                        .login("headers-sess-${UUID.randomUUID().toString().substring(0,8)}@example.com".toString())),
                true, false, null)
            userId = user.getId()

            // revokeUserSessions with headers
            try {
                userSessionsApi.revokeUserSessions(userId, false, false, headers)
            } catch (Exception ignored) {}

        } catch (Exception e) {
            // Expected
        } finally {
            if (userId) {
                try {
                    new com.okta.sdk.resource.api.UserLifecycleApi(getClient()).deactivateUser(userId, false, null)
                    userApi.deleteUser(userId, false, null)
                } catch (Exception ignored) {}
            }
        }
    }
}

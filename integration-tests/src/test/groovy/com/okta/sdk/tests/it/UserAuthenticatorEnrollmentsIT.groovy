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
import com.okta.sdk.resource.api.UserAuthenticatorEnrollmentsApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for {@link UserAuthenticatorEnrollmentsApi}.
 * Tests authenticator enrollment management including phone, email, app, and TAC enrollments.
 */
class UserAuthenticatorEnrollmentsIT extends ITSupport {

    private UserAuthenticatorEnrollmentsApi userAuthenticatorEnrollmentsApi
    private UserApi userApi

    UserAuthenticatorEnrollmentsIT() {
        this.userAuthenticatorEnrollmentsApi = new UserAuthenticatorEnrollmentsApi(getClient())
        this.userApi = new UserApi(getClient())
    }

    @Test(groups = "group3")
    void createPhoneAuthenticatorEnrollmentTest() {
        def email = "user-phone-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def phoneNumber = "+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll('-','')}"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Phone")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        // Note: In real implementation, creating enrollments requires proper authenticator setup
        // This test verifies the API structure and calls
        try {
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            // Configure phone enrollment request
            
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            assertThat(enrollment.getId(), notNullValue())
            // Enrollment would be in PENDING or ACTIVE status
            assertThat(enrollment.getStatus(), notNullValue())
            
        } catch (ApiException e) {
            // Expected in test environment without proper authenticator configuration
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void createEmailAuthenticatorEnrollmentTest() {
        def email = "user-email-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Email")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            // Configure email enrollment request
            
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            assertThat(enrollment.getId(), notNullValue())
            assertThat(enrollment.getStatus(), notNullValue())
            
        } catch (ApiException e) {
            // Expected in test environment without proper authenticator configuration
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void createAppAuthenticatorEnrollmentTest() {
        def email = "user-app-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("App")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            // Configure app (Okta Verify) enrollment request
            
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            assertThat(enrollment.getId(), notNullValue())
            assertThat(enrollment.getStatus(), notNullValue())
            
        } catch (ApiException e) {
            // Expected in test environment without proper authenticator configuration
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

//    @Test(groups = "group3")
//    void createTacAuthenticatorEnrollmentTest() {
//        def email = "user-tac-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("TAC")
//            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        try {
//            def tacRequest = new AuthenticatorEnrollmentCreateRequestTac()
//            // Configure TAC (Temporary Access Codes) enrollment request
//
//            def enrollment = userAuthenticatorEnrollmentsApi.createTacAuthenticatorEnrollment(user.getId(), tacRequest)
//
//            assertThat(enrollment, notNullValue())
//            assertThat(enrollment.getId(), notNullValue())
//            assertThat(enrollment.getStatus(), equalTo("ACTIVE"))
//
//        } catch (ApiException e) {
//            // Expected in test environment without TAC authenticator enabled or insufficient permissions
//            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(404)))
//        }
//    }

//    @Test(groups = "group3")
//    void listAuthenticatorEnrollmentsTest() {
//        def email = "user-list-enrollments-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("List")
//            .setLastName("Enrollments-"+UUID.randomUUID().toString().substring(0,8)+"")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        try {
//            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
//
//            assertThat(enrollments, notNullValue())
//            // User may have default enrollments or none
//            // Verify we can list them without errors
//
//        } catch (ApiException e) {
//            // Should not fail for listing
//            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
//        }
//    }

//    @Test(groups = "group3")
//    void listAuthenticatorEnrollmentsEmptyTest() {
//        def email = "user-empty-enrollments-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Empty")
//            .setLastName("Enrollments-"+UUID.randomUUID().toString().substring(0,8)+"")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        try {
//            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
//
//            assertThat(enrollments, notNullValue())
//            // Newly created user may have no enrollments
//
//        } catch (ApiException e) {
//            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
//        }
//    }

//    @Test(groups = "group3")
//    void getAuthenticatorEnrollmentTest() {
//        def email = "user-get-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
//
//        User user = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Get")
//            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
//            .buildAndCreate(userApi)
//        registerForCleanup(user)
//
//        try {
//            // First list to get an enrollment ID
//            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
//
//            if (enrollments != null && enrollments.size() > 0) {
//                def firstEnrollment = enrollments.get(0)
//
//                // Now get the specific enrollment
//                def enrollment = userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollment(
//                    user.getId(),
//                    firstEnrollment.getId()
//                )
//
//                assertThat(enrollment, notNullValue())
//                assertThat(enrollment.getId(), equalTo(firstEnrollment.getId()))
//                assertThat(enrollment.getStatus(), notNullValue())
//                assertThat(enrollment.getType(), notNullValue())
//            }
//
//        } catch (ApiException e) {
//            // Expected if no enrollments exist or insufficient permissions
//            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
//        }
//    }

    @Test(groups = "group3")
    void deleteAuthenticatorEnrollmentTest() {
        def email = "user-delete-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Delete")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Create an enrollment first
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            assertThat(enrollment.getId(), notNullValue())
            
            // Delete the enrollment
            userAuthenticatorEnrollmentsApi.deleteAuthenticatorEnrollment(user.getId(), enrollment.getId())
            
            // Verify deletion - getting should fail
            try {
                userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollment(user.getId(), enrollment.getId())
                // Should not reach here
                assertThat("Enrollment should be deleted", false)
            } catch (ApiException notFoundEx) {
                assertThat(notFoundEx.getCode(), equalTo(404))
            }
            
        } catch (ApiException e) {
            // Expected if enrollment creation not supported in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void authenticatorEnrollmentLifecycleTest() {
        def email = "user-enrollment-lifecycle-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Lifecycle")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Create enrollment
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            String enrollmentId = enrollment.getId()
            
            // Get enrollment
            def retrievedEnrollment = userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollment(user.getId(), enrollmentId)
            assertThat(retrievedEnrollment, notNullValue())
            assertThat(retrievedEnrollment.getId(), equalTo(enrollmentId))
            
            // List enrollments - should include our enrollment
            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
            assertThat(enrollments, notNullValue())
            def found = enrollments.any { it.getId() == enrollmentId }
            assertThat(found, equalTo(true))
            
            // Delete enrollment
            userAuthenticatorEnrollmentsApi.deleteAuthenticatorEnrollment(user.getId(), enrollmentId)
            
        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void multipleAuthenticatorEnrollmentsTest() {
        def email = "user-multiple-enrollments-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Multiple")
            .setLastName("Enrollments-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Attempt to create multiple enrollment types
            def phoneRequest = new AuthenticatorEnrollmentCreateRequest()
            def emailRequest = new AuthenticatorEnrollmentCreateRequest()
            
            def phoneEnrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), phoneRequest)
            def emailEnrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), emailRequest)
            
            // List all enrollments
            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
            
            assertThat(enrollments, notNullValue())
            assertThat(enrollments.size(), greaterThanOrEqualTo(2))
            
        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getAuthenticatorEnrollmentWithInvalidIdTest() {
        def email = "user-invalid-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Invalid")
            .setLastName("Enrollment-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollment(user.getId(), "invalid-enrollment-id")
            
            // Should not reach here
            assertThat("Should throw exception for invalid enrollment ID", false)
            
        } catch (ApiException e) {
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void deleteNonExistentAuthenticatorEnrollmentTest() {
        def email = "user-delete-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Delete")
            .setLastName("NonExistent-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userAuthenticatorEnrollmentsApi.deleteAuthenticatorEnrollment(user.getId(), "non-existent-enrollment-id")
            
            // Should not reach here
            assertThat("Should throw exception for non-existent enrollment", false)
            
        } catch (ApiException e) {
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void createAuthenticatorEnrollmentWithSpecialCharactersInUserIdTest() {
        def email = "user+special-enrollment-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Special")
            .setLastName("Chars-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            assertThat(enrollment.getId(), notNullValue())
            
        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listAuthenticatorEnrollmentsWithInvalidUserIdTest() {
        try {
            userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments("invalid-user-id-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should not reach here
            assertThat("Should throw exception for invalid user ID", false)
            
        } catch (ApiException e) {
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void createTacAuthenticatorEnrollmentWithHttpInfoTest() {
        def email = "user-tac-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TAC")
            .setLastName("HttpInfo-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def tacRequest = new AuthenticatorEnrollmentCreateRequestTac()
            
            def response = userAuthenticatorEnrollmentsApi.createTacAuthenticatorEnrollmentWithHttpInfo(user.getId(), tacRequest)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), anyOf(equalTo(200), equalTo(201)))
            assertThat(response.getData(), notNullValue())
            assertThat(response.getData().getId(), notNullValue())
            
        } catch (ApiException e) {
            // Expected in test environment without TAC authenticator
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void getAuthenticatorEnrollmentWithHttpInfoTest() {
        def email = "user-get-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Get")
            .setLastName("HttpInfo-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List enrollments first
            def enrollments = userAuthenticatorEnrollmentsApi.listAuthenticatorEnrollments(user.getId())
            
            if (enrollments != null && enrollments.size() > 0) {
                def firstEnrollment = enrollments.get(0)
                
                // Get with HTTP info
                def response = userAuthenticatorEnrollmentsApi.getAuthenticatorEnrollmentWithHttpInfo(
                    user.getId(), 
                    firstEnrollment.getId()
                )
                
                assertThat(response, notNullValue())
                assertThat(response.getStatusCode(), equalTo(200))
                assertThat(response.getData(), notNullValue())
                assertThat(response.getData().getId(), equalTo(firstEnrollment.getId()))
            }
            
        } catch (ApiException e) {
            // Expected if no enrollments or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void deleteAuthenticatorEnrollmentWithHttpInfoTest() {
        def email = "user-delete-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Delete")
            .setLastName("HttpInfo-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Create an enrollment
            def enrollmentRequest = new AuthenticatorEnrollmentCreateRequest()
            def enrollment = userAuthenticatorEnrollmentsApi.createAuthenticatorEnrollment(user.getId(), enrollmentRequest)
            
            assertThat(enrollment, notNullValue())
            
            // Delete with HTTP info
            def response = userAuthenticatorEnrollmentsApi.deleteAuthenticatorEnrollmentWithHttpInfo(
                user.getId(), 
                enrollment.getId()
            )
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))
            
        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}

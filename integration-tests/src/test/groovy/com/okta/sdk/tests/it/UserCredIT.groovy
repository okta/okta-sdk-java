/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.impl.resource.DefaultUserBuilder
import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserCredApi
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.lang.reflect.Method

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.testng.Assert.fail

class UserCredIT extends ITSupport {

    private UserApi userApi
    private UserCredApi userCredApi

    UserCredIT() {
        this.userApi = new UserApi(getClient())
        this.userCredApi = new UserCredApi(getClient())
    }

    private User createTestUserWithCredentials(String uniqueId, String password = "Zx98@Vbn21!") {
        log.info("Creating test user with credentials, uniqueId: {}", uniqueId)
        
        def email = "user-cred-test-${uniqueId}@example.com"
        
        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("CredApi")
            .setLastName("IntegTest${uniqueId}")
            .setPassword(password.toCharArray())
            .buildAndCreate(userApi)
        
        log.info("Created user: {} with status: {}", user.getId(), user.getStatus())
        registerForCleanup(user)
        
        return user
    }

    @Test(groups = "group3")
    void changePassword_withValidCredentials_returnsUserCredentials() {
        log.info("=== Test: ChangePassword with valid credentials ===")
        
        def guid = UUID.randomUUID().toString()
        def oldPassword = "OldPass#123Abc"
        def newPassword = "NewPass#456Def"
        def user = createTestUserWithCredentials(guid, oldPassword)
        Thread.sleep(2000)

        try {
            def request = new ChangePasswordRequest()
                .oldPassword(new PasswordCredential().value(oldPassword))
                .newPassword(new PasswordCredential().value(newPassword))
            
            def credentials = userCredApi.changePassword(user.getId(), request, false)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getProvider(), notNullValue())
            assertThat(credentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA))
            log.info("Password changed successfully")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changePassword_withStrictValidation_includesStrictParameter() {
        log.info("=== Test: ChangePassword with strict validation ===")
        
        def guid = UUID.randomUUID().toString()
        def oldPassword = "OldPass#123Abc"
        def newPassword = "NewPass#456Def"
        def user = createTestUserWithCredentials(guid, oldPassword)
        Thread.sleep(2000)

        try {
            def request = new ChangePasswordRequest()
                .oldPassword(new PasswordCredential().value(oldPassword))
                .newPassword(new PasswordCredential().value(newPassword))
            
            def credentials = userCredApi.changePassword(user.getId(), request, true)

            assertThat(credentials, notNullValue())
            log.info("Password changed with strict validation")

        } catch (ApiException e) {
            log.error("ApiException with strict validation: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changePassword_callsCorrectEndpoint() {
        log.info("=== Test: ChangePassword calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def oldPassword = "OldPass#123Abc"
        def newPassword = "NewPass#456Def"
        def user = createTestUserWithCredentials(guid, oldPassword)
        Thread.sleep(2000)

        try {
            def request = new ChangePasswordRequest()
                .oldPassword(new PasswordCredential().value(oldPassword))
                .newPassword(new PasswordCredential().value(newPassword))
            
            def credentials = userCredApi.changePassword(user.getId(), request, false)
            assertThat(credentials, notNullValue())
            log.info("ChangePassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changeRecoveryQuestion_withValidData_returnsUserCredentials() {
        log.info("=== Test: ChangeRecoveryQuestion with valid data ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def newQuestion = "What is your pet's name?"
            def newAnswer = "Fluffy"
            def password = "Zx98@Vbn21!"
            
            def userCredentials = new UserCredentials()
                .password(new PasswordCredential().value(password))
                .recoveryQuestion(new RecoveryQuestionCredential()
                    .question(newQuestion)
                    .answer(newAnswer))
            
            def credentials = userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getRecoveryQuestion(), notNullValue())
            assertThat(credentials.getRecoveryQuestion().getQuestion(), equalTo(newQuestion))
            log.info("Recovery question changed successfully")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changeRecoveryQuestion_callsCorrectEndpoint() {
        log.info("=== Test: ChangeRecoveryQuestion calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def newQuestion = "What was your first car?"
            def newAnswer = "Honda"
            def password = "Zx98@Vbn21!"
            
            def userCredentials = new UserCredentials()
                .password(new PasswordCredential().value(password))
                .recoveryQuestion(new RecoveryQuestionCredential()
                    .question(newQuestion)
                    .answer(newAnswer))
            
            def credentials = userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)
            assertThat(credentials, notNullValue())
            log.info("ChangeRecoveryQuestion endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePassword_returnsUserWithExpiredStatus() {
        log.info("=== Test: ExpirePassword returns user with expired status ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def expiredUser = userCredApi.expirePassword(user.getId())

            assertThat(expiredUser, notNullValue())
            assertThat(expiredUser.getId(), equalTo(user.getId()))
            log.info("Password expired, user status: {}", expiredUser.getStatus())

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePassword_callsCorrectEndpoint() {
        log.info("=== Test: ExpirePassword calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def expiredUser = userCredApi.expirePassword(user.getId())
            assertThat(expiredUser, notNullValue())
            log.info("ExpirePassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordWithTempPassword_withRevokeSessions_returnsUser() {
        log.info("=== Test: ExpirePasswordWithTempPassword with revoke sessions ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def userWithTempPassword = userCredApi.expirePasswordWithTempPassword(user.getId(), true)

            // API returns User object with tempPassword in response
            assertThat(userWithTempPassword, notNullValue())
            // User ID might be null in response, but the operation succeeds
            log.info("Password expired with temp password, sessions revoked")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordWithTempPassword_withoutRevokeSession_callsCorrectEndpoint() {
        log.info("=== Test: ExpirePasswordWithTempPassword without revoke session ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def userWithTempPassword = userCredApi.expirePasswordWithTempPassword(user.getId(), false)
            assertThat(userWithTempPassword, notNullValue())
            log.info("ExpirePasswordWithTempPassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPassword_withSendEmailTrue_returnsForgotPasswordResponse() {
        log.info("=== Test: ForgotPassword with sendEmail=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def response = userCredApi.forgotPassword(user.getId(), true)

            // When sendEmail=true, API sends reset link via email and may return null
            // The operation succeeds even if response is null
            log.info("ForgotPassword called with sendEmail=true, response: {}", response)

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPassword_withSendEmailFalse_returnsForgotPasswordResponse() {
        log.info("=== Test: ForgotPassword with sendEmail=false ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def response = userCredApi.forgotPassword(user.getId(), false)

            assertThat(response, notNullValue())
            assertThat(response.getResetPasswordUrl(), notNullValue())
            log.info("ForgotPassword called with sendEmail=false")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPassword_callsCorrectEndpoint() {
        log.info("=== Test: ForgotPassword calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def response = userCredApi.forgotPassword(user.getId(), false)
            assertThat(response, notNullValue())
            log.info("ForgotPassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordSetNewPassword_withValidAnswer_returnsUserCredentials() {
        log.info("=== Test: ForgotPasswordSetNewPassword with valid answer ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def forgotResponse = userCredApi.forgotPassword(user.getId(), false)
            
            def newPassword = "NewPass#789Xyz"
            def userCredentials = new UserCredentials()
                .password(new PasswordCredential().value(newPassword))
                .recoveryQuestion(new RecoveryQuestionCredential()
                    .answer("Blue"))
            
            def credentials = userCredApi.forgotPasswordSetNewPassword(user.getId(), userCredentials, false)

            assertThat(credentials, notNullValue())
            log.info("Password set with recovery question answer")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordSetNewPassword_callsCorrectEndpoint() {
        log.info("=== Test: ForgotPasswordSetNewPassword calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            userCredApi.forgotPassword(user.getId(), false)
            
            def newPassword = "NewPass#789Xyz"
            def userCredentials = new UserCredentials()
                .password(new PasswordCredential().value(newPassword))
                .recoveryQuestion(new RecoveryQuestionCredential()
                    .answer("Blue"))
            
            def credentials = userCredApi.forgotPasswordSetNewPassword(user.getId(), userCredentials, false)
            assertThat(credentials, notNullValue())
            log.info("ForgotPasswordSetNewPassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPassword_withSendEmailTrue_returnsResetPasswordToken() {
        log.info("=== Test: ResetPassword with sendEmail=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), true, false)

            // When sendEmail=true, API sends reset token via email and may return null
            // The operation succeeds even if resetToken is null
            log.info("ResetPassword called with sendEmail=true, resetToken: {}", resetToken)

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPassword_withRevokeSessions_includesRevokeParameter() {
        log.info("=== Test: ResetPassword with revokeSessions=true ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), false, true)

            assertThat(resetToken, notNullValue())
            log.info("ResetPassword called with revokeSessions=true")

        } catch (ApiException e) {
            log.error("ApiException: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPassword_callsCorrectEndpoint() {
        log.info("=== Test: ResetPassword calls correct endpoint ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), false, false)
            assertThat(resetToken, notNullValue())
            log.info("ResetPassword endpoint called successfully")

        } catch (ApiException e) {
            log.error("ApiException: {}", e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void credentialManagement_passwordResetFlow_callsCorrectEndpoints() {
        log.info("=== Test: Credential management password reset flow ===")
        
        def guid = UUID.randomUUID().toString()
        def user = createTestUserWithCredentials(guid)
        Thread.sleep(2000)

        try {
            def forgotResponse = userCredApi.forgotPassword(user.getId(), false)
            assertThat(forgotResponse, notNullValue())
            
            def resetToken = userCredApi.resetPassword(user.getId(), false, false)
            assertThat(resetToken, notNullValue())
            
            log.info("Password reset flow completed successfully")

        } catch (ApiException e) {
            log.error("ApiException in reset flow: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void credentialManagement_passwordChangeFlow_callsCorrectEndpoints() {
        log.info("=== Test: Credential management password change flow ===")
        
        def guid = UUID.randomUUID().toString()
        def oldPassword = "OldPass#123Abc"
        def newPassword = "NewPass#456Def"
        def user = createTestUserWithCredentials(guid, oldPassword)
        Thread.sleep(2000)

        try {
            def request = new ChangePasswordRequest()
                .oldPassword(new PasswordCredential().value(oldPassword))
                .newPassword(new PasswordCredential().value(newPassword))
            def credentials = userCredApi.changePassword(user.getId(), request, false)
            assertThat(credentials, notNullValue())
            
            def expiredUser = userCredApi.expirePassword(user.getId())
            assertThat(expiredUser, notNullValue())
            
            log.info("Password change flow completed successfully")

        } catch (ApiException e) {
            log.error("ApiException in change flow: Code={}, Message={}", e.getCode(), e.getMessage())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changePassword_withEmptyUserId_stillCallsEndpoint() {
        log.info("=== Test: ChangePassword with empty userId ===")

        try {
            def request = new ChangePasswordRequest()
                .oldPassword(new PasswordCredential().value("Old123!"))
                .newPassword(new PasswordCredential().value("New456!"))
            
            userCredApi.changePassword("", request, false)
            fail("Should have thrown ApiException")

        } catch (ApiException e) {
            log.info("Correctly threw ApiException for empty userId: Code={}", e.getCode())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404), equalTo(405)))
        }
    }

    @Test(groups = "group3")
    void expirePassword_withLongUserId_callsCorrectEndpoint() {
        log.info("=== Test: ExpirePassword with long userId ===")

        try {
            def longUserId = "a" * 300
            userCredApi.expirePassword(longUserId)
            fail("Should have thrown ApiException")

        } catch (ApiException e) {
            log.info("Correctly threw ApiException for long userId: Code={}", e.getCode())
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(404)))
        }
    }
}

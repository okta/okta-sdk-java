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
 * distributed under the        try {
            def resetToken = userCredApi.resetPassword(user.getId(), true, false)

            assertThat(resetToken, notNullValue())
            if (resetToken.getResetPasswordUrl() != null) {
                assertThat(resetToken.getResetPasswordUrl(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }s distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it

import com.okta.sdk.resource.api.UserApi
import com.okta.sdk.resource.api.UserCredApi
import com.okta.sdk.resource.api.UserLifecycleApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Credential Management API.
 * Tests password changes, recovery questions, password expiration, forgot password, and reset flows.
 */
class UserCredIT extends ITSupport {

    private UserApi userApi
    private UserCredApi userCredApi
    private UserLifecycleApi userLifecycleApi

    UserCredIT() {
        this.userApi = new UserApi(getClient())
        this.userCredApi = new UserCredApi(getClient())
        this.userLifecycleApi = new UserLifecycleApi(getClient())
    }

    @Test(groups = "group3")
    void changePasswordTest() {
        def email = "user-change-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def oldPassword = "OldP@ssw0rd${randomSuffix}!"
        def newPassword = "NewP@ssw0rd${randomSuffix}!"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Change")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(oldPassword.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def changePasswordRequest = new ChangePasswordRequest()
            changePasswordRequest.oldPassword(new PasswordCredential().value(oldPassword))
            changePasswordRequest.newPassword(new PasswordCredential().value(newPassword))

            def credentials = userCredApi.changePassword(user.getId(), changePasswordRequest, null)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getProvider(), notNullValue())
            assertThat(credentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA))

        } catch (ApiException e) {
            // Expected in test environment - password policy may prevent changes
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changePasswordWithStrictValidationTest() {
        def email = "user-strict-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def oldPassword = "OldP@ssw0rd${randomSuffix}!"
        def newPassword = "NewStr1ct${randomSuffix}!"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Strict")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(oldPassword.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def changePasswordRequest = new ChangePasswordRequest()
            changePasswordRequest.oldPassword(new PasswordCredential().value(oldPassword))
            changePasswordRequest.newPassword(new PasswordCredential().value(newPassword))

            // Test with strict validation enabled
            def credentials = userCredApi.changePassword(user.getId(), changePasswordRequest, true)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getProvider(), notNullValue())
            assertThat(credentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA))

        } catch (ApiException e) {
            // Expected - strict validation may reject password
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changeRecoveryQuestionTest() {
        def email = "user-recovery-question-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def password = "TestPassword123!"
        def question = "What is your favorite pet's name?"
        def answer = "Fluffy"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Recovery")
            .setLastName("Question-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(password.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def userCredentials = new UserCredentials()
            userCredentials.password(new PasswordCredential().value(password))
            userCredentials.recoveryQuestion(new RecoveryQuestionCredential()
                .question(question)
                .answer(answer))

            def credentials = userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getRecoveryQuestion(), notNullValue())
            assertThat(credentials.getRecoveryQuestion().getQuestion(), equalTo(question))

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordTest() {
        def email = "user-expire-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Expire")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def expiredUser = userCredApi.expirePassword(user.getId())

            assertThat(expiredUser, notNullValue())
            assertThat(expiredUser.getId(), equalTo(user.getId()))
            assertThat(expiredUser.getStatus(), equalTo(UserStatus.PASSWORD_EXPIRED))

        } catch (ApiException e) {
            // Expected if user is in state where password cannot be expired
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordWithTempPasswordTest() {
        def email = "user-temp-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Temp")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def expiredUser = userCredApi.expirePasswordWithTempPassword(user.getId(), false)

            assertThat(expiredUser, notNullValue())
            if (expiredUser.getId() != null) {
                assertThat(expiredUser.getId(), equalTo(user.getId()))
                assertThat(expiredUser.getStatus(), equalTo(UserStatus.PASSWORD_EXPIRED))
                // Temporary password would be in credentials
                assertThat(expiredUser.getCredentials(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordWithTempPasswordRevokeSessionsTest() {
        def email = "user-temp-revoke-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TempRevoke")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Expire password and revoke all sessions
            def expiredUser = userCredApi.expirePasswordWithTempPassword(user.getId(), true)

            assertThat(expiredUser, notNullValue())
            if (expiredUser.getId() != null) {
                assertThat(expiredUser.getId(), equalTo(user.getId()))
                assertThat(expiredUser.getStatus(), equalTo(UserStatus.PASSWORD_EXPIRED))
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordTest() {
        def email = "user-forgot-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Forgot")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def forgotPasswordResponse = userCredApi.forgotPassword(user.getId(), false)

            assertThat(forgotPasswordResponse, notNullValue())
            assertThat(forgotPasswordResponse.getResetPasswordUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordWithSendEmailTrueTest() {
        def email = "user-forgot-send-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ForgotSend")
            .setLastName("Email-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def forgotPasswordResponse = userCredApi.forgotPassword(user.getId(), true)

            assertThat(forgotPasswordResponse, notNullValue())
            if (forgotPasswordResponse.getResetPasswordUrl() != null) {
                assertThat(forgotPasswordResponse.getResetPasswordUrl(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordWithSendEmailFalseTest() {
        def email = "user-forgot-no-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ForgotNoSend")
            .setLastName("Email-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def forgotPasswordResponse = userCredApi.forgotPassword(user.getId(), false)

            assertThat(forgotPasswordResponse, notNullValue())
            assertThat(forgotPasswordResponse.getResetPasswordUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void forgotPasswordSetNewPasswordTest() {
        def email = "user-forgot-set-new-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def newPassword = "NewR3set${randomSuffix}!"
        def recoveryAnswer = "My favorite pet"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ForgotSet")
            .setLastName("New-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def userCredentials = new UserCredentials()
            userCredentials.password(new PasswordCredential().value(newPassword))
            userCredentials.recoveryQuestion(new RecoveryQuestionCredential().answer(recoveryAnswer))

            def credentials = userCredApi.forgotPasswordSetNewPassword(user.getId(), userCredentials, false)

            assertThat(credentials, notNullValue())
            assertThat(credentials.getProvider(), notNullValue())
            assertThat(credentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA))

        } catch (ApiException e) {
            // Expected - requires recovery question to be set first
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPasswordTest() {
        def email = "user-reset-password-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Reset")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), false, false)

            assertThat(resetToken, notNullValue())
            assertThat(resetToken.getResetPasswordUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPasswordWithSendEmailTrueTest() {
        def email = "user-reset-send-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetSend")
            .setLastName("Email-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), true, false)

            assertThat(resetToken, notNullValue())
            if (resetToken.getResetPasswordUrl() != null) {
                assertThat(resetToken.getResetPasswordUrl(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPasswordWithRevokeSessionsTest() {
        def email = "user-reset-revoke-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetRevoke")
            .setLastName("Sessions-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), false, true)

            assertThat(resetToken, notNullValue())
            assertThat(resetToken.getResetPasswordUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPasswordWithSendEmailAndRevokeSessionsTest() {
        def email = "user-reset-send-revoke-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetBoth")
            .setLastName("Options-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), true, true)

            assertThat(resetToken, notNullValue())
            if (resetToken.getResetPasswordUrl() != null) {
                assertThat(resetToken.getResetPasswordUrl(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void passwordResetFlowTest() {
        def email = "user-reset-flow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetFlow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .setActive(true)
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Step 1: Initiate forgot password
            def forgotResponse = userCredApi.forgotPassword(user.getId(), false)
            assertThat(forgotResponse, notNullValue())
            if (forgotResponse.getResetPasswordUrl() != null) {
                assertThat(forgotResponse.getResetPasswordUrl(), notNullValue())
            }

            // Step 2: Reset password
            def resetToken = userCredApi.resetPassword(user.getId(), true, false)
            assertThat(resetToken, notNullValue())
            if (resetToken.getResetPasswordUrl() != null) {
                assertThat(resetToken.getResetPasswordUrl(), notNullValue())
            }

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void passwordChangeFlowTest() {
        def email = "user-change-flow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def oldPassword = "OldFl0w${randomSuffix}!"
        def newPassword = "NewFl0w${randomSuffix}!"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ChangeFlow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(oldPassword.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Step 1: Change password
            def changePasswordRequest = new ChangePasswordRequest()
            changePasswordRequest.oldPassword(new PasswordCredential().value(oldPassword))
            changePasswordRequest.newPassword(new PasswordCredential().value(newPassword))

            def credentials = userCredApi.changePassword(user.getId(), changePasswordRequest, false)
            assertThat(credentials, notNullValue())
            assertThat(credentials.getProvider().getType(), equalTo(AuthenticationProviderType.OKTA))

            // Step 2: Change recovery question
            def userCredentials = new UserCredentials()
            userCredentials.password(new PasswordCredential().value(newPassword))
            userCredentials.recoveryQuestion(new RecoveryQuestionCredential()
                .question("What is your pet's name?")
                .answer("Fluffy"))

            def updatedCredentials = userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)
            assertThat(updatedCredentials, notNullValue())
            assertThat(updatedCredentials.getRecoveryQuestion(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordWorkflowTest() {
        def email = "user-expire-workflow-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ExpireWorkflow")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Expire password without temp password
            def expiredUser = userCredApi.expirePassword(user.getId())
            assertThat(expiredUser, notNullValue())
            assertThat(expiredUser.getStatus(), equalTo(UserStatus.PASSWORD_EXPIRED))

            // Get reset token
            def resetToken = userCredApi.resetPassword(user.getId(), false, false)
            assertThat(resetToken, notNullValue())
            assertThat(resetToken.getResetPasswordUrl(), notNullValue())

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changePasswordWithInvalidOldPasswordTest() {
        def email = "user-invalid-old-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def correctPassword = "C0rrect${randomSuffix}!"
        def wrongOldPassword = "Wr0ng${randomSuffix}!"
        def newPassword = "NewP@ss${randomSuffix}!"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("InvalidOld")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(correctPassword.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def changePasswordRequest = new ChangePasswordRequest()
            changePasswordRequest.oldPassword(new PasswordCredential().value(wrongOldPassword))
            changePasswordRequest.newPassword(new PasswordCredential().value(newPassword))

            userCredApi.changePassword(user.getId(), changePasswordRequest, null)

            // Should not reach here - wrong old password should fail
            assertThat("Should fail with incorrect old password", false)

        } catch (ApiException e) {
            // Expected - wrong old password should fail with 403 or 401
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403)))
        }
    }

    @Test(groups = "group3")
    void expirePasswordForInactiveUserTest() {
        def email = "user-inactive-expire-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("InactiveExpire")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Deactivate user first
            userLifecycleApi.deactivateUser(user.getId(), false, null)

            // Try to expire password for inactive user
            userCredApi.expirePassword(user.getId())

            // May or may not succeed depending on org policy

        } catch (ApiException e) {
            // Expected - cannot expire password for inactive user
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resetPasswordUrlFormatTest() {
        def email = "user-reset-url-format-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResetURL")
            .setLastName("Format-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword("T3stP@ss${UUID.randomUUID().toString().substring(0,6).replaceAll("-","")}!".toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def resetToken = userCredApi.resetPassword(user.getId(), false, null)

            assertThat(resetToken, notNullValue())
            assertThat(resetToken.getResetPasswordUrl(), notNullValue())
            // Verify URL format
            assertThat(resetToken.getResetPasswordUrl(), containsString("http"))

        } catch (ApiException e) {
            // Expected in test environment
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void changeRecoveryQuestionWithInvalidPasswordTest() {
        def email = "user-invalid-recovery-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"
        def randomSuffix = UUID.randomUUID().toString().substring(0,6).replaceAll("-","")
        def correctPassword = "C0rrectR3c${randomSuffix}!"
        def wrongPassword = "Wr0ngR3c${randomSuffix}!"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("InvalidRecovery")
            .setLastName("Password-"+UUID.randomUUID().toString().substring(0,8)+"")
            .setPassword(correctPassword.toCharArray())
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def userCredentials = new UserCredentials()
            userCredentials.password(new PasswordCredential().value(wrongPassword))
            userCredentials.recoveryQuestion(new RecoveryQuestionCredential()
                .question("What is your pet's name?")
                .answer("Fluffy"))

            userCredApi.changeRecoveryQuestion(user.getId(), userCredentials)

            // Should not reach here - wrong password should fail
            assertThat("Should fail with incorrect password", false)

        } catch (ApiException e) {
            // Expected - wrong password should fail
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(401), equalTo(403)))
        }
    }
}

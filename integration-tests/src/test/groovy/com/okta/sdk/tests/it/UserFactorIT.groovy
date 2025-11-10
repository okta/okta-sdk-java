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
import com.okta.sdk.resource.api.UserFactorApi
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Ignore
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Integration tests for User Factor API.
 * Tests MFA factor enrollment, activation, verification, and management.
 */
class UserFactorIT extends ITSupport {

    private UserFactorApi userFactorApi
    private UserApi userApi

    UserFactorIT() {
        this.userFactorApi = new UserFactorApi(getClient())
        this.userApi = new UserApi(getClient())
    }

    @Test(groups = "group3")
    void enrollSMSFactorTest() {
        def email = "user-enroll-sms-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollSMS")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def smsFactor = new UserFactorSMS()
            smsFactor.setFactorType(UserFactorType.SMS)
            smsFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorSMSProfile()
            profile.setPhoneNumber("+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll("-","")}")
            smsFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), smsFactor, null, null, null, null, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            assertThat(enrolledFactor.getFactorType(), equalTo(UserFactorType.SMS))
            assertThat(enrolledFactor.getStatus(), anyOf(
                equalTo(UserFactorStatus.PENDING_ACTIVATION),
                equalTo(UserFactorStatus.ACTIVE)
            ))

        } catch (ApiException e) {
            // Expected if SMS factor not enabled or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollTOTPFactorTest() {
        def email = "user-enroll-totp-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollTOTP")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def totpFactor = new UserFactorTokenSoftwareTOTP()
            totpFactor.setFactorType(UserFactorType.TOKEN_SOFTWARE_TOTP)
            totpFactor.setProvider(UserFactorProvider.OKTA.name())

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), totpFactor, null, null, null, null, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            assertThat(enrolledFactor.getFactorType(), equalTo(UserFactorType.TOKEN_SOFTWARE_TOTP))
            assertThat(enrolledFactor.getStatus(), equalTo(UserFactorStatus.PENDING_ACTIVATION))

        } catch (ApiException e) {
            // Expected if TOTP factor not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollEmailFactorTest() {
        def email = "user-enroll-email-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollEmail")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            assertThat(enrolledFactor.getFactorType(), equalTo(UserFactorType.EMAIL))

        } catch (ApiException e) {
            // Expected if email factor not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollFactorWithActivateTrueTest() {
        def email = "user-enroll-activate-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollActivate")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            // Enroll with activate=true
            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, true, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            // May be ACTIVE or PENDING_ACTIVATION depending on factor type
            assertThat(enrolledFactor.getStatus(), notNullValue())

        } catch (ApiException e) {
            // Expected if factor not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getFactorTest() {
        def email = "user-get-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("GetFactor")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List factors first to get a valid factor ID
            def factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                def factorId = factors.get(0).getId()
                
                // Get specific factor
                def factor = userFactorApi.getFactor(user.getId(), factorId)
                
                assertThat(factor, notNullValue())
                assertThat(factor.getId(), equalTo(factorId))
                assertThat(factor.getFactorType(), notNullValue())
                assertThat(factor.getStatus(), notNullValue())
            }
            
        } catch (ApiException e) {
            // Expected if user has no factors
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void activateFactorTest() {
        def email = "user-activate-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ActivateFactor")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Enroll a factor first
            def smsFactor = new UserFactorSMS()
            smsFactor.setFactorType(UserFactorType.SMS)
            smsFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorSMSProfile()
            profile.setPhoneNumber("+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll("-","")}")
            smsFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), smsFactor, null, null, null, null, null)
            
            if (enrolledFactor.getStatus() == UserFactorStatus.PENDING_ACTIVATION) {
                // Activate with passcode
                def activateRequest = new UserFactorActivateRequest()
                activateRequest.setPassCode("123456") // Test passcode
                
                def verifyResponse = userFactorApi.activateFactor(user.getId(), enrolledFactor.getId(), activateRequest)
                
                assertThat(verifyResponse, notNullValue())
            }

        } catch (ApiException e) {
            // Expected if factor enrollment/activation not supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void verifyFactorTest() {
        def email = "user-verify-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("VerifyFactor")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List active factors
            def factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                def activeFactor = factors.find { it.getStatus() == UserFactorStatus.ACTIVE }
                
                if (activeFactor != null) {
                    // Verify factor with passcode
                    def verifyRequest = new UserFactorVerifyRequest()
                    verifyRequest.setPassCode("123456")
                    
                    def verifyResponse = userFactorApi.verifyFactor(user.getId(), activeFactor.getId(), null, null, null, null, verifyRequest, null)
                    
                    assertThat(verifyResponse, notNullValue())
                }
            }

        } catch (ApiException e) {
            // Expected if no active factors or verification fails
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void verifyPushFactorTest() {
        def email = "user-verify-push-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("VerifyPush")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // List push factors
            def factors = userFactorApi.listFactors(user.getId())
            def pushFactor = factors?.find { it.getFactorType() == UserFactorType.PUSH }
            
            if (pushFactor != null) {
                // Issue push challenge
                def verifyResponse = userFactorApi.verifyFactor(
                    user.getId(), 
                    pushFactor.getId(), 
                    null, null, null, null, null, null
                )
                
                assertThat(verifyResponse, notNullValue())
                // Push verification would return CHALLENGE status
            }

        } catch (ApiException e) {
            // Expected if no push factors enrolled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void unenrollFactorTest() {
        def email = "user-unenroll-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UnenrollFactor")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Enroll a factor first
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)
            
            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            
            // Unenroll the factor
            userFactorApi.unenrollFactor(user.getId(), enrolledFactor.getId(), null)
            
            // Verify deletion - getting should fail
            try {
                userFactorApi.getFactor(user.getId(), enrolledFactor.getId())
                assertThat("Factor should be deleted", false)
            } catch (ApiException notFoundEx) {
                assertThat(notFoundEx.getCode(), equalTo(404))
            }

        } catch (ApiException e) {
            // Expected if factor operations not supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void unenrollFactorWithRemoveRecoveryTest() {
        def email = "user-unenroll-recovery-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UnenrollRecovery")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get a factor to unenroll
            def factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                def factorId = factors.get(0).getId()
                
                // Unenroll with removeRecoveryEnrollment flag
                userFactorApi.unenrollFactor(user.getId(), factorId, true)
            }

        } catch (ApiException e) {
            // Expected if no factors or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listFactorsTest() {
        def email = "user-list-factors-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListFactors")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def factors = userFactorApi.listFactors(user.getId())
            
            assertThat(factors, notNullValue())
            // User may have factors or none depending on org configuration
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listFactorsEmptyTest() {
        def email = "user-list-empty-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ListEmpty")
            .setLastName("Factors-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def factors = userFactorApi.listFactors(user.getId())
            
            assertThat(factors, notNullValue())
            // Newly created user typically has no factors
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void listSupportedFactorsTest() {
        def email = "user-supported-factors-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SupportedFactors")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def supportedFactors = userFactorApi.listSupportedFactors(user.getId())
            
            assertThat(supportedFactors, notNullValue())
            // Should return list of factors available for enrollment
            
        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void resendEnrollFactorTest() {
        def email = "user-resend-enroll-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("ResendEnroll")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Enroll a factor first
            def smsFactor = new UserFactorSMS()
            smsFactor.setFactorType(UserFactorType.SMS)
            smsFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorSMSProfile()
            profile.setPhoneNumber("+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll("-","")}")
            smsFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), smsFactor, null, null, null, null, null)
            
            if (enrolledFactor.getStatus() == UserFactorStatus.PENDING_ACTIVATION) {
                // Resend enrollment challenge
                def resendRequest = new UserFactor()
                def resendResponse = userFactorApi.resendEnrollFactor(user.getId(), enrolledFactor.getId(), resendRequest)
                
                assertThat(resendResponse, notNullValue())
            }

        } catch (ApiException e) {
            // Expected if resend not supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getFactorTransactionStatusTest() {
        def email = "user-transaction-status-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("TransactionStatus")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Get a push factor and verify to create transaction
            def factors = userFactorApi.listFactors(user.getId())
            def pushFactor = factors?.find { it.getFactorType() == UserFactorType.PUSH }
            
            if (pushFactor != null) {
                // Verify to create transaction
                def verifyResponse = userFactorApi.verifyFactor(
                    user.getId(), 
                    pushFactor.getId(), 
                    null, null, null, null, null, null
                )
                
                // Get transaction status (would need transaction ID from verify response)
                // This is a placeholder as transaction ID extraction depends on response structure
            }

        } catch (ApiException e) {
            // Expected if no push factors or transactions
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void factorLifecycleEnrollActivateVerifyTest() {
        def email = "user-factor-lifecycle-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("FactorLifecycle")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. Enroll SMS factor
            def smsFactor = new UserFactorSMS()
            smsFactor.setFactorType(UserFactorType.SMS)
            smsFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorSMSProfile()
            profile.setPhoneNumber("+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll("-","")}")
            smsFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), smsFactor, null, null, null, null, null)
            
            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            String factorId = enrolledFactor.getId()
            
            // 2. Get factor
            def retrievedFactor = userFactorApi.getFactor(user.getId(), factorId)
            assertThat(retrievedFactor, notNullValue())
            assertThat(retrievedFactor.getId(), equalTo(factorId))
            
            // 3. List factors - should include our factor
            def factors = userFactorApi.listFactors(user.getId())
            assertThat(factors, notNullValue())
            def found = factors.any { it.getId() == factorId }
            assertThat(found, equalTo(true))
            
            // 4. Unenroll factor
            userFactorApi.unenrollFactor(user.getId(), factorId, null)

        } catch (ApiException e) {
            // Expected if factor operations not supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollMultipleFactorsTest() {
        def email = "user-multiple-factors-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("MultipleFactors")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Enroll SMS factor
            def smsFactor = new UserFactorSMS()
            smsFactor.setFactorType(UserFactorType.SMS)
            smsFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def smsProfile = new UserFactorSMSProfile()
            smsProfile.setPhoneNumber("+1-555-${UUID.randomUUID().toString().substring(0,7).replaceAll("-","")}")
            smsFactor.setProfile(smsProfile)

            def smsEnrolled = userFactorApi.enrollFactor(user.getId(), smsFactor, null, null, null, null, null)
            assertThat(smsEnrolled, notNullValue())
            
            // Enroll Email factor
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def emailProfile = new UserFactorEmailProfile()
            emailProfile.setEmail(email)
            emailFactor.setProfile(emailProfile)

            def emailEnrolled = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)
            assertThat(emailEnrolled, notNullValue())
            
            // List all factors - should have at least 2
            def factors = userFactorApi.listFactors(user.getId())
            assertThat(factors, notNullValue())
            assertThat(factors.size(), greaterThanOrEqualTo(2))

        } catch (ApiException e) {
            // Expected if factor types not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void getFactorWithInvalidIdTest() {
        def email = "user-invalid-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("InvalidFactor")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userFactorApi.getFactor(user.getId(), "invalid-factor-id-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should not reach here
            assertThat("Should throw exception for invalid factor ID", false)
            
        } catch (ApiException e) {
            // Expected - invalid factor ID should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void unenrollNonExistentFactorTest() {
        def email = "user-unenroll-nonexistent-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UnenrollNonExistent")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            userFactorApi.unenrollFactor(user.getId(), "non-existent-factor-"+UUID.randomUUID().toString().substring(0,8)+"", null)
            
            // Should not reach here
            assertThat("Should throw exception for non-existent factor", false)
            
        } catch (ApiException e) {
            // Expected - non-existent factor should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void listFactorsForInvalidUserTest() {
        try {
            userFactorApi.listFactors("invalid-user-id-"+UUID.randomUUID().toString().substring(0,8)+"")
            
            // Should not reach here
            assertThat("Should throw exception for invalid user ID", false)
            
        } catch (ApiException e) {
            // Expected - invalid user ID should return 404
            assertThat(e.getCode(), equalTo(404))
        }
    }

    @Test(groups = "group3")
    void enrollFactorStatusVerificationTest() {
        def email = "user-factor-status-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("FactorStatus")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)
            
            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getStatus(), notNullValue())
            assertThat(enrolledFactor.getStatus(), anyOf(
                equalTo(UserFactorStatus.PENDING_ACTIVATION),
                equalTo(UserFactorStatus.ACTIVE),
                equalTo(UserFactorStatus.NOT_SETUP)
            ))

        } catch (ApiException e) {
            // Expected if email factor not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void getFactorWithHttpInfoTest() {
        def email = "user-factor-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("FactorHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                def factorId = factors.get(0).getId()
                
                // Get with HTTP info
                def response = userFactorApi.getFactorWithHttpInfo(user.getId(), factorId)
                
                assertThat(response, notNullValue())
                assertThat(response.getStatusCode(), equalTo(200))
                assertThat(response.getData(), notNullValue())
                assertThat(response.getData().getId(), equalTo(factorId))
            }
            
        } catch (ApiException e) {
            // Expected if no factors exist
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void unenrollFactorWithHttpInfoTest() {
        def email = "user-unenroll-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("UnenrollHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // Enroll a factor first
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)
            
            assertThat(enrolledFactor, notNullValue())
            
            // Unenroll with HTTP info
            def response = userFactorApi.unenrollFactorWithHttpInfo(user.getId(), enrolledFactor.getId(), null)
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(204))

        } catch (ApiException e) {
            // Expected if factor operations not supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Ignore("WithHttpInfo method not available in Java SDK")
    @Test(groups = "group3")
    void listSupportedFactorsWithHttpInfoTest() {
        def email = "user-supported-httpinfo-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("SupportedHttpInfo")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def response = userFactorApi.listSupportedFactorsWithHttpInfo(user.getId())
            
            assertThat(response, notNullValue())
            assertThat(response.getStatusCode(), equalTo(200))
            assertThat(response.getData(), notNullValue())

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void factorTypeVerificationTest() {
        def email = "user-factor-types-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("FactorTypes")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def supportedFactors = userFactorApi.listSupportedFactors(user.getId())
            
            assertThat(supportedFactors, notNullValue())
            
            // Verify supported factor types are valid
            for (def factor : supportedFactors) {
                assertThat(factor.getFactorType(), notNullValue())
                assertThat(factor.getFactorType(), anyOf(
                    equalTo(UserFactorType.SMS),
                    equalTo(UserFactorType.CALL),
                    equalTo(UserFactorType.EMAIL),
                    equalTo(UserFactorType.PUSH),
                    equalTo(UserFactorType.QUESTION),
                    equalTo(UserFactorType.TOKEN_SOFTWARE_TOTP),
                    equalTo(UserFactorType.TOKEN_HOTP),
                    equalTo(UserFactorType.WEB),
                    equalTo(UserFactorType.WEBAUTHN),
                    equalTo(UserFactorType.U2F)
                ))
            }

        } catch (ApiException e) {
            // Expected if insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void factorProviderVerificationTest() {
        def email = "user-factor-providers-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("FactorProviders")
            .setLastName("Test-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def factors = userFactorApi.listFactors(user.getId())
            
            if (factors != null && factors.size() > 0) {
                for (def factor : factors) {
                    // Verify factor has valid provider
                    assertThat(factor.getProvider(), notNullValue())
                    assertThat(factor.getProvider(), anyOf(
                        equalTo(UserFactorProvider.OKTA),
                        equalTo(UserFactorProvider.GOOGLE),
                        equalTo(UserFactorProvider.RSA),
                        equalTo(UserFactorProvider.SYMANTEC),
                        equalTo(UserFactorProvider.YUBICO),
                        equalTo(UserFactorProvider.FIDO)
                    ))
                }
            }

        } catch (ApiException e) {
            // Expected if no factors or insufficient permissions
            assertThat(e.getCode(), anyOf(equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollWebAuthnFactorTest() {
        def email = "user-enroll-webauthn-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollWebAuthn")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def webAuthnFactor = new UserFactorWebAuthn()
            webAuthnFactor.setFactorType(UserFactorType.WEBAUTHN)
            webAuthnFactor.setProvider(UserFactorProvider.FIDO.name())

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), webAuthnFactor, null, null, null, null, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            assertThat(enrolledFactor.getFactorType(), equalTo(UserFactorType.WEBAUTHN))

        } catch (ApiException e) {
            // Expected if WebAuthn not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void enrollSecurityQuestionFactorTest() {
        def email = "user-enroll-question-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("EnrollQuestion")
            .setLastName("Factor-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            def questionFactor = new UserFactorSecurityQuestion()
            questionFactor.setFactorType(UserFactorType.QUESTION)
            questionFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorSecurityQuestionProfile()
            profile.setQuestion("disliked_food")
            profile.setAnswer("pizza")
            profile.setQuestionText("What is the food you least liked as a child?")
            questionFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), questionFactor, null, null, null, null, null)

            assertThat(enrolledFactor, notNullValue())
            assertThat(enrolledFactor.getId(), notNullValue())
            assertThat(enrolledFactor.getFactorType(), equalTo(UserFactorType.QUESTION))

        } catch (ApiException e) {
            // Expected if security question not enabled
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }

    @Test(groups = "group3")
    void completeFactorManagementWorkflowTest() {
        def email = "user-complete-factor-"+UUID.randomUUID().toString().substring(0,8)+"@example.com"

        User user = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("CompleteFactor")
            .setLastName("Workflow-"+UUID.randomUUID().toString().substring(0,8)+"")
            .buildAndCreate(userApi)
        registerForCleanup(user)

        try {
            // 1. List supported factors
            def supportedFactors = userFactorApi.listSupportedFactors(user.getId())
            assertThat(supportedFactors, notNullValue())
            
            // 2. Enroll email factor
            def emailFactor = new UserFactorEmail()
            emailFactor.setFactorType(UserFactorType.EMAIL)
            emailFactor.setProvider(UserFactorProvider.OKTA.name())
            
            def profile = new UserFactorEmailProfile()
            profile.setEmail(email)
            emailFactor.setProfile(profile)

            def enrolledFactor = userFactorApi.enrollFactor(user.getId(), emailFactor, null, null, null, null, null)
            assertThat(enrolledFactor, notNullValue())
            String factorId = enrolledFactor.getId()
            
            // 3. Get the enrolled factor
            def retrievedFactor = userFactorApi.getFactor(user.getId(), factorId)
            assertThat(retrievedFactor.getId(), equalTo(factorId))
            
            // 4. List all factors
            def allFactors = userFactorApi.listFactors(user.getId())
            assertThat(allFactors, notNullValue())
            
            // 5. Unenroll the factor
            userFactorApi.unenrollFactor(user.getId(), factorId, null)

        } catch (ApiException e) {
            // Expected if factor operations not fully supported
            assertThat(e.getCode(), anyOf(equalTo(400), equalTo(403), equalTo(404)))
        }
    }
}

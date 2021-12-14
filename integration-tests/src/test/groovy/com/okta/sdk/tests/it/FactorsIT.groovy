/*
 * Copyright 2017-Present Okta, Inc.
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

import com.google.common.collect.Lists

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.factor.ActivateFactorRequest
import com.okta.sdk.resource.user.factor.CallUserFactor
import com.okta.sdk.resource.user.factor.EmailUserFactor
import com.okta.sdk.resource.user.factor.EmailUserFactorProfile
import com.okta.sdk.resource.user.factor.FactorProvider
import com.okta.sdk.resource.user.factor.FactorStatus
import com.okta.sdk.resource.user.factor.FactorType
import com.okta.sdk.resource.user.factor.PushUserFactor
import com.okta.sdk.resource.user.factor.SecurityQuestionUserFactor
import com.okta.sdk.resource.user.factor.SecurityQuestionList
import com.okta.sdk.resource.user.factor.SmsUserFactor
import com.okta.sdk.resource.user.factor.TokenUserFactor
import com.okta.sdk.resource.user.factor.TotpUserFactor
import com.okta.sdk.resource.user.factor.UserFactor
import com.okta.sdk.resource.user.factor.UserFactorList
import com.okta.sdk.resource.user.factor.VerifyFactorRequest
import com.okta.sdk.resource.user.factor.VerifyUserFactorResponse
import com.okta.sdk.resource.user.factor.VerifyUserFactorResult
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.it.util.ITSupport
import org.jboss.aerogear.security.otp.Totp
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.assertThat

class FactorsIT extends ITSupport {

    private String smsTestNumber = "185 635 15491"

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void factorListTest() {

        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SmsUserFactor smsUserFactor = client.instantiate(SmsUserFactor)
        smsUserFactor.getProfile().setPhoneNumber(smsTestNumber)
        user.enrollFactor(smsUserFactor)

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(SecurityQuestionUserFactor)
        securityQuestionUserFactor.getProfile()
            .setQuestion("disliked_food")
            .setAnswer("pizza")
        user.enrollFactor(securityQuestionUserFactor)

        UserFactorList factorsList = user.listFactors()
        List<UserFactor> factorsArrayList = Lists.newArrayList(factorsList)
        assertThat factorsArrayList, allOf(hasSize(2), containsInAnyOrder(
            allOf(
                instanceOf(SmsUserFactor),
                hasProperty("id", is(smsUserFactor.getId()))),
            allOf(
                instanceOf(SecurityQuestionUserFactor),
                hasProperty("id", is(securityQuestionUserFactor.getId())))))
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testSecurityQuestionFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(SecurityQuestionUserFactor)
        securityQuestionUserFactor.getProfile()
            .setQuestion("disliked_food")
            .setAnswer("pizza")

        assertThat securityQuestionUserFactor.id, nullValue()
        assertThat securityQuestionUserFactor, sameInstance(user.enrollFactor(securityQuestionUserFactor))
        assertThat securityQuestionUserFactor.id, notNullValue()
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testCallFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        CallUserFactor callUserFactor = client.instantiate(CallUserFactor)
        callUserFactor.getProfile().setPhoneNumber(smsTestNumber)

        assertThat callUserFactor.id, nullValue()
        assertThat callUserFactor, sameInstance(user.enrollFactor(callUserFactor))
        assertThat callUserFactor.id, notNullValue()
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testSmsFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SmsUserFactor smsUserFactor = client.instantiate(SmsUserFactor)
        smsUserFactor.getProfile().setPhoneNumber(smsTestNumber)

        assertThat smsUserFactor.id, nullValue()
        assertThat smsUserFactor, sameInstance(user.enrollFactor(smsUserFactor))
        assertThat smsUserFactor.id, notNullValue()
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testPushFactorCreation() {
        Client client = getClient()
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()

        PushUserFactor pushUserFactor = client.instantiate(PushUserFactor)
        assertThat pushUserFactor.id, nullValue()
        assertThat pushUserFactor, sameInstance(user.enrollFactor(pushUserFactor))
        assertThat pushUserFactor.id, notNullValue()
    }

    @Test (groups = "group2")
    void testListSecurityQuestionsNotEmpty() {
        User user = randomUser()
        SecurityQuestionList securityQuestions = user.listSupportedSecurityQuestions()
        assertThat securityQuestions, iterableWithSize(greaterThan(1))
    }

    @Test (groups = "group2")
    void testAvailableFactorsNotEmpty() {
        User user = randomUser()
        UserFactorList factors = user.listSupportedFactors()
        assertThat factors, iterableWithSize(greaterThan(1))
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void activateTotpFactor() {
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()
        TotpUserFactor totpUserFactor = client.instantiate(TotpUserFactor)
        user.enrollFactor(totpUserFactor)

        assertThat totpUserFactor.getStatus(), is(FactorStatus.PENDING_ACTIVATION)
        Totp totp = new Totp(totpUserFactor.getEmbedded().get("activation").get("sharedSecret"))

        ActivateFactorRequest activateFactorRequest = client.instantiate(ActivateFactorRequest)
        activateFactorRequest.setPassCode(totp.now())
        UserFactor factorResult = totpUserFactor.activate(activateFactorRequest)
        assertThat factorResult.getStatus(), is(FactorStatus.ACTIVE)
        assertThat factorResult, instanceOf(TotpUserFactor)
    }

    @Test (groups = "group2")
    void verifyQuestionFactor() {
        User user = randomUser()

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(SecurityQuestionUserFactor)
        securityQuestionUserFactor.getProfile()
            .setQuestion("disliked_food")
            .setAnswer("pizza")
        user.enrollFactor(securityQuestionUserFactor)

        VerifyFactorRequest request = client.instantiate(VerifyFactorRequest)
        request.setAnswer("pizza")
        VerifyUserFactorResponse response =
            securityQuestionUserFactor.verify(request, null, null, null, null, null)
        assertThat response.getFactorResult(), is(VerifyUserFactorResult.SUCCESS)
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testEmailUserFactor() {
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()

        EmailUserFactor emailUserFactor = client.instantiate(EmailUserFactor)
            .setFactorType(FactorType.EMAIL)
            .setProvider(FactorProvider.OKTA)
            .setProfile(client.instantiate(EmailUserFactorProfile)
                .setEmail(user.getProfile().getEmail()))

        assertThat emailUserFactor.id, nullValue()
        // enroll and activate
        assertThat emailUserFactor, sameInstance(user.enrollFactor(emailUserFactor, false, null, null, true))
        assertThat emailUserFactor.getStatus(), is(FactorStatus.ACTIVE)
        assertThat emailUserFactor.id, notNullValue()

        VerifyFactorRequest request = client.instantiate(VerifyFactorRequest)
        VerifyUserFactorResponse response =
            emailUserFactor.verify(request, null, null, null, null, null)
        assertThat response.getFactorResult(), is(VerifyUserFactorResult.CHALLENGE)
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void testGoogleTotpUserFactorCreation() {
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()

        TokenUserFactor tokenUserFactor = client.instantiate(TokenUserFactor)
            .setFactorType(FactorType.TOKEN_SOFTWARE_TOTP)
            .setProvider(FactorProvider.GOOGLE)

        assertThat tokenUserFactor.id, nullValue()
        assertThat tokenUserFactor, sameInstance(user.enrollFactor(tokenUserFactor))
        assertThat tokenUserFactor.id, notNullValue()
        assertThat tokenUserFactor.getStatus(), is(FactorStatus.PENDING_ACTIVATION)
    }

    @NonOIEEnvironmentOnly
    @Test (groups = "group2")
    void deleteFactorTest() {

        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()
        TotpUserFactor totpUserFactor = client.instantiate(TotpUserFactor)
        totpUserFactor.setProvider(FactorProvider.OKTA)
        user.enrollFactor(totpUserFactor)
        totpUserFactor.delete()
    }
}

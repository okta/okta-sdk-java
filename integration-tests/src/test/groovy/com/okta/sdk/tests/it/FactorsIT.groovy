/*
 * Copyright 2017 Okta
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
import com.okta.sdk.impl.resource.user.factor.DefaultActivateFactorRequest
import com.okta.sdk.impl.resource.user.factor.DefaultCallUserFactor
import com.okta.sdk.impl.resource.user.factor.DefaultPushUserFactor
import com.okta.sdk.impl.resource.user.factor.DefaultVerifyFactorRequest
import com.okta.sdk.resource.VerifyUserFactorResponse
import com.okta.sdk.resource.user.User
import com.okta.sdk.impl.resource.user.factor.DefaultSmsUserFactor
import com.okta.sdk.resource.user.factor.ActivateFactorRequest
import com.okta.sdk.resource.user.factor.CallUserFactor
import com.okta.sdk.resource.user.factor.Factor
import com.okta.sdk.resource.user.factor.FactorProvider
import com.okta.sdk.resource.user.factor.FactorResultType
import com.okta.sdk.resource.user.factor.FactorStatus
import com.okta.sdk.resource.user.factor.PushUserFactor
import com.okta.sdk.resource.user.factor.SecurityQuestionUserFactor
import com.okta.sdk.impl.resource.user.factor.DefaultSecurityQuestionUserFactor
import com.okta.sdk.resource.user.factor.SecurityQuestionList
import com.okta.sdk.resource.user.factor.SmsUserFactor
import com.okta.sdk.resource.user.factor.TotpUserFactor
import com.okta.sdk.impl.resource.user.factor.DefaultTotpUserFactor
import com.okta.sdk.resource.user.factor.UserFactor
import com.okta.sdk.resource.user.factor.UserFactorList
import com.okta.sdk.resource.user.factor.VerifyFactorRequest
import com.okta.sdk.resource.user.factor.VerifyFactorResponse
import com.okta.sdk.tests.it.util.ITSupport
import org.jboss.aerogear.security.otp.Totp
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.assertThat

class FactorsIT extends ITSupport {


    private String smsTestNumber = "162 840 01133"

    @Test
    void factorListTest() {

        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SmsUserFactor smsUserFactor = client.instantiate(DefaultSmsUserFactor)
        smsUserFactor.getProfile().setPhoneNumber(smsTestNumber)
        user.enrollFactor(smsUserFactor)

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(DefaultSecurityQuestionUserFactor)
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

    @Test
    void testSecurityQuestionFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(DefaultSecurityQuestionUserFactor)
        securityQuestionUserFactor.getProfile()
                .setQuestion("disliked_food")
                .setAnswer("pizza")

        assertThat securityQuestionUserFactor.id, nullValue()
        assertThat securityQuestionUserFactor, sameInstance(user.enrollFactor(securityQuestionUserFactor))
        assertThat securityQuestionUserFactor.id, notNullValue()
    }

    @Test
    void testCallFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        CallUserFactor callUserFactor = client.instantiate(DefaultCallUserFactor)
        callUserFactor.getProfile().setPhoneNumber(smsTestNumber)

        assertThat callUserFactor.id, nullValue()
        assertThat callUserFactor, sameInstance(user.enrollFactor(callUserFactor))
        assertThat callUserFactor.id, notNullValue()
    }

    @Test
    void testSmsFactorCreation() {
        Client client = getClient()
        User user = randomUser()

        assertThat user.listFactors(), emptyIterable()

        SmsUserFactor smsUserFactor = client.instantiate(DefaultSmsUserFactor)
        smsUserFactor.getProfile().setPhoneNumber(smsTestNumber)

        assertThat smsUserFactor.id, nullValue()
        assertThat smsUserFactor, sameInstance(user.enrollFactor(smsUserFactor))
        assertThat smsUserFactor.id, notNullValue()
    }

    @Test
    void testPushFactorCreation() {
        Client client = getClient()
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()

        PushUserFactor pushUserFactor = client.instantiate(DefaultPushUserFactor)
        assertThat pushUserFactor.id, nullValue()
        assertThat pushUserFactor, sameInstance(user.enrollFactor(pushUserFactor))
        assertThat pushUserFactor.id, notNullValue()
    }

    @Test
    void testListSecurityQuestionsNotEmpty() {
        User user = randomUser()
        SecurityQuestionList securityQuestions = user.listSupportedSecurityQuestions()
        assertThat securityQuestions, iterableWithSize(greaterThan(1))
    }

    @Test
    void testAvailableFactorsNotEmpty() {
        User user = randomUser()
        UserFactorList factors = user.listSupportedFactors()
        assertThat factors, iterableWithSize(greaterThan(1))
    }

    @Test //TODO: check why this fails
    void activateTotpFactor() {
        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()
        TotpUserFactor totpUserFactor = client.instantiate(DefaultTotpUserFactor)
        user.enrollFactor(totpUserFactor)

        assertThat totpUserFactor.getStatus(), is(FactorStatus.PENDING_ACTIVATION)
        Totp totp = new Totp(totpUserFactor.getEmbedded().get("activation").get("sharedSecret"))

        ActivateFactorRequest activateFactorRequest = client.instantiate(DefaultActivateFactorRequest)
        activateFactorRequest.setPassCode(totp.now())
        UserFactor factorResult = totpUserFactor.activate(activateFactorRequest)
        assertThat factorResult.getStatus(), is(FactorStatus.ACTIVE)
        assertThat factorResult, instanceOf(TotpUserFactor)
    }

    @Test
    void verifyQuestionFactor() {
        User user = randomUser()

        SecurityQuestionUserFactor securityQuestionUserFactor = client.instantiate(DefaultSecurityQuestionUserFactor)
        securityQuestionUserFactor.getProfile()
                .setQuestion("disliked_food")
                .setAnswer("pizza")
        user.enrollFactor(securityQuestionUserFactor)

        VerifyFactorRequest request = client.instantiate(DefaultVerifyFactorRequest)
        request.setAnswer("pizza")
        VerifyUserFactorResponse response = securityQuestionUserFactor.verify(request, null, null)
        assertThat response.getFactorResult(), is(VerifyUserFactorResponse.FactorResultEnum.SUCCESS)
    }

    @Test
    void deleteFactorTest() {

        User user = randomUser()
        assertThat user.listFactors(), emptyIterable()
        TotpUserFactor totpUserFactor = client.instantiate(DefaultTotpUserFactor)
        totpUserFactor.setProvider(FactorProvider.OKTA)
        user.enrollFactor(totpUserFactor)
        totpUserFactor.delete()
    }
}

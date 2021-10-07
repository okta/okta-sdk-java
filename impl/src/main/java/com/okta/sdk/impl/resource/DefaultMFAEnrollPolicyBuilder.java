/*
 * Copyright 2021-Present Okta, Inc.
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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.MFAEnrollPolicy;
import com.okta.sdk.resource.policy.MFAEnrollPolicyBuilder;
import com.okta.sdk.resource.policy.MFAEnrollPolicyFactorsSettings;
import com.okta.sdk.resource.policy.MFAEnrollPolicySettings;
import com.okta.sdk.resource.policy.PolicyMFAFactor;
import com.okta.sdk.resource.policy.PolicyType;

import java.util.Objects;

public class DefaultMFAEnrollPolicyBuilder
    extends DefaultPolicyBuilder<MFAEnrollPolicyBuilder> implements MFAEnrollPolicyBuilder {

    private PolicyMFAFactor duo;
    private PolicyMFAFactor fidoU2f;
    private PolicyMFAFactor fidoWebauthn;
    private PolicyMFAFactor googleOtp;
    private PolicyMFAFactor oktaCall;
    private PolicyMFAFactor oktaEmail;
    private PolicyMFAFactor oktaOtp;
    private PolicyMFAFactor oktaPassword;
    private PolicyMFAFactor oktaPush;
    private PolicyMFAFactor oktaQuestion;
    private PolicyMFAFactor oktaSms;
    private PolicyMFAFactor rsaToken;
    private PolicyMFAFactor symantecVip;
    private PolicyMFAFactor yubikeyToken;

    public DefaultMFAEnrollPolicyBuilder() {
        this.policyType = PolicyType.MFA_ENROLL;
    }

    @Override
    public MFAEnrollPolicy buildAndCreate(Client client) {
        return (MFAEnrollPolicy) client.createPolicy(build(client), isActive);
    }

    private MFAEnrollPolicy build(Client client) {
        MFAEnrollPolicy policy = client.instantiate(MFAEnrollPolicy.class);
        MFAEnrollPolicySettings policySettings = client.instantiate(MFAEnrollPolicySettings.class);
        MFAEnrollPolicyFactorsSettings policyFactorsSettings = client.instantiate(MFAEnrollPolicyFactorsSettings.class);

        policy.setSettings(policySettings.setFactors(policyFactorsSettings));

        if (PolicyType.MFA_ENROLL.equals(policyType)) {
            policy.setType(policyType);
        } else {
            throw new IllegalArgumentException("PolicyType should be 'MFA_ENROLL', please use PolicyBuilder for other policy types.");
        }
        if (Strings.hasText(name)) policy.setName(name);
        if (Strings.hasText(description)) policy.setDescription(description);
        if (priority != null) policy.setPriority(priority);
        if (Objects.nonNull(status)) policy.setStatus(status);
        if (Objects.nonNull(duo)) policyFactorsSettings.setDuo(duo);
        if (Objects.nonNull(fidoU2f)) policyFactorsSettings.setFidoU2f(fidoU2f);
        if (Objects.nonNull(fidoWebauthn)) policyFactorsSettings.setFidoWebauthn(fidoWebauthn);
        if (Objects.nonNull(googleOtp)) policyFactorsSettings.setGoogleOtp(googleOtp);
        if (Objects.nonNull(oktaCall)) policyFactorsSettings.setOktaCall(oktaCall);
        if (Objects.nonNull(oktaEmail)) policyFactorsSettings.setOktaEmail(oktaEmail);
        if (Objects.nonNull(oktaOtp)) policyFactorsSettings.setOktaOtp(oktaOtp);
        if (Objects.nonNull(oktaPassword)) policyFactorsSettings.setOktaPassword(oktaPassword);
        if (Objects.nonNull(oktaPush)) policyFactorsSettings.setOktaPush(oktaPush);
        if (Objects.nonNull(oktaQuestion)) policyFactorsSettings.setOktaQuestion(oktaQuestion);
        if (Objects.nonNull(oktaSms)) policyFactorsSettings.setOktaSms(oktaSms);
        if (Objects.nonNull(rsaToken)) policyFactorsSettings.setRsaToken(rsaToken);
        if (Objects.nonNull(symantecVip)) policyFactorsSettings.setSymantecVip(symantecVip);
        if (Objects.nonNull(yubikeyToken)) policyFactorsSettings.setYubikeyToken(yubikeyToken);

        return policy;
    }

    @Override
    public MFAEnrollPolicyBuilder setDuo(PolicyMFAFactor duo) {
        this.duo = duo;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setFidoU2f(PolicyMFAFactor fidoU2f) {
        this.fidoU2f = fidoU2f;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setFidoWebauthn(PolicyMFAFactor fidoWebauthn) {
        this.fidoWebauthn = fidoWebauthn;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setGoogleOtp(PolicyMFAFactor googleOtp) {
        this.googleOtp = googleOtp;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaCall(PolicyMFAFactor oktaCall) {
        this.oktaCall = oktaCall;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaEmail(PolicyMFAFactor oktaEmail) {
        this.oktaEmail = oktaEmail;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaOtp(PolicyMFAFactor oktaOtp) {
        this.oktaOtp = oktaOtp;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaPassword(PolicyMFAFactor oktaPassword) {
        this.oktaPassword = oktaPassword;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaPush(PolicyMFAFactor oktaPush) {
        this.oktaPush = oktaPush;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaQuestion(PolicyMFAFactor oktaQuestion) {
        this.oktaQuestion = oktaQuestion;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setOktaSms(PolicyMFAFactor oktaSms) {
        this.oktaSms = oktaSms;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setRsaToken(PolicyMFAFactor rsaToken) {
        this.rsaToken = rsaToken;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setSymantecVip(PolicyMFAFactor symantecVip) {
        this.symantecVip = symantecVip;
        return this;
    }

    @Override
    public MFAEnrollPolicyBuilder setYubikeyToken(PolicyMFAFactor yubikeyToken) {
        this.yubikeyToken = yubikeyToken;
        return this;
    }
}

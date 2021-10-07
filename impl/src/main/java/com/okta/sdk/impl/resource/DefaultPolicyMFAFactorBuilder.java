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
import com.okta.sdk.resource.policy.PolicyFactorConsent;
import com.okta.sdk.resource.policy.PolicyFactorConsentTerms;
import com.okta.sdk.resource.policy.PolicyFactorEnroll;
import com.okta.sdk.resource.policy.PolicyMFAFactor;
import com.okta.sdk.resource.policy.PolicyMFAFactorBuilder;

public class DefaultPolicyMFAFactorBuilder implements PolicyMFAFactorBuilder {

    private PolicyFactorConsentTerms.FormatEnum format;
    private String value;
    private PolicyFactorConsent.TypeEnum type = PolicyFactorConsent.TypeEnum.NONE;
    private PolicyFactorEnroll.SelfEnum self = PolicyFactorEnroll.SelfEnum.NOT_ALLOWED;

    @Override
    public PolicyMFAFactor build(Client client) {
        PolicyMFAFactor policyMFAFactor = client.instantiate(PolicyMFAFactor.class);
        PolicyFactorConsent factorConsent = client.instantiate(PolicyFactorConsent.class);
        PolicyFactorEnroll factorEnroll = client.instantiate(PolicyFactorEnroll.class);

        if (format != null) getConsentTerms(client, factorConsent).setFormat(format);
        if (Strings.hasText(value)) getConsentTerms(client, factorConsent).setValue(value);

        policyMFAFactor.setConsent(factorConsent.setType(type));
        policyMFAFactor.setEnroll(factorEnroll.setSelf(self));
        return policyMFAFactor;
    }

    private PolicyFactorConsentTerms getConsentTerms(Client client, PolicyFactorConsent factorConsent) {
        if (factorConsent.getTerms() == null) {
            factorConsent.setTerms(client.instantiate(PolicyFactorConsentTerms.class));
        }
        return factorConsent.getTerms();
    }

    @Override
    public PolicyMFAFactorBuilder setFormat(PolicyFactorConsentTerms.FormatEnum format) {
        this.format = format;
        return this;
    }

    @Override
    public PolicyMFAFactorBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public PolicyMFAFactorBuilder setType(PolicyFactorConsent.TypeEnum type) {
        this.type = type;
        return this;
    }

    @Override
    public PolicyMFAFactorBuilder setSelf(PolicyFactorEnroll.SelfEnum self) {
        this.self = self;
        return this;
    }
}

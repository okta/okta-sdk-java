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
package com.okta.sdk.impl.resource.builder;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.LifecycleStatus;
import com.okta.sdk.resource.PolicyRuleType;
import com.okta.sdk.resource.Policy;
import com.okta.sdk.resource.PolicyRule;
import com.okta.sdk.resource.builder.PolicyRuleBuilder;

import java.util.Objects;

public class DefaultPolicyRuleBuilder<T extends PolicyRuleBuilder> implements PolicyRuleBuilder<T> {

    protected Integer priority;
    protected PolicyRuleType type;
    protected LifecycleStatus status;

    DefaultPolicyRuleBuilder(){ this.type = PolicyRuleType.SIGN_ON; }

    @Override
    public T setPriority(Integer priority) {
        this.priority = priority;
        return self();
    }

    @Override
    public T setStatus(LifecycleStatus status) {
        this.status = status;
        return self();
    }

    @Override
    public T setType(PolicyRuleType type) {
        this.type = type;
        return self();
    }

    @Override
    public PolicyRule buildAndCreate(Client client, Policy policy) {
        return client.createPolicyRule(build(client), policy.getId());
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    private PolicyRule build(Client client){
        PolicyRule policyRule = client.instantiate(PolicyRule.class);

        if (Objects.nonNull(priority)) policyRule.setPriority(priority);

        if (Objects.nonNull(status)) policyRule.setStatus(status);

        if (Objects.nonNull(type)) policyRule.setType(type);

        return policyRule;
    }
}

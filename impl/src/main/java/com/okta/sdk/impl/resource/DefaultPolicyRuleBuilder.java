/*
 * Copyright 2020 Okta
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
import com.okta.sdk.resource.policy.Policy;
import com.okta.sdk.resource.policy.PolicyRule;
import com.okta.sdk.resource.policy.rule.PolicyRuleBuilder;

import java.util.Objects;

public class DefaultPolicyRuleBuilder<T extends PolicyRuleBuilder> implements PolicyRuleBuilder<T> {

    protected String id;
    protected Integer priority;
    protected PolicyRule.TypeEnum type;
    protected PolicyRule.StatusEnum status;
    protected Boolean isActive = true;

    DefaultPolicyRuleBuilder(){ this.type = PolicyRule.TypeEnum.SIGN_ON; }

    @Override
    public T setId(String id) {
        this.id = id;
        return self();
    }

    @Override
    public T setPriority(Integer priority) {
        this.priority = priority;
        return self();
    }

    @Override
    public T setStatus(PolicyRule.StatusEnum status) {
        this.status = status;
        if ((PolicyRule.StatusEnum.ACTIVE).equals(status))
            isActive = true;
        else
            isActive = false;
        return self();
    }

    @Override
    public T setType(PolicyRule.TypeEnum type) {
        this.type = type;
        return self();
    }

    @Override
    public PolicyRule buildAndCreate(Client client, Policy policy) {
        return (PolicyRule)policy.createRule(build(client), isActive);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    private PolicyRule build(Client client){
        PolicyRule policyRule = client.instantiate(PolicyRule.class);

        if (Strings.hasText(id)) policyRule.setId(id);

        if (Objects.nonNull(priority)) policyRule.setPriority(priority);

        if (Objects.nonNull(status)) policyRule.setStatus(status);

        if (Objects.nonNull(type)) policyRule.setType(type);

        return policyRule;
    }
}

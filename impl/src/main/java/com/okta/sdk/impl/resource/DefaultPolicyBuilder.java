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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.Policy;
import com.okta.sdk.resource.policy.PolicyBuilder;
import com.okta.sdk.resource.policy.PolicyType;

import java.util.Objects;


public class DefaultPolicyBuilder implements PolicyBuilder {

    private String name;
    private String description;
    private PolicyType policyType;
    private Integer priority;
    private Policy.StatusEnum status;

    @Override
    public PolicyBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public PolicyBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public PolicyBuilder setType(PolicyType policyType) {
        this.policyType = policyType;
        return this;
    }

    @Override
    public PolicyBuilder setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public PolicyBuilder setStatus(Policy.StatusEnum status) {
        this.status = status;
        return this;
    }

    @Override
    public Policy buildAndCreate(Client client) {
        return client.createPolicy(build(client));
    }

    private Policy build(Client client) {
        Policy policy = client.instantiate(Policy.class);

        if (Strings.hasText(name)) policy.setName(name);
        if (Strings.hasText(description)) policy.setDescription(description);
        if (priority != null)
            policy.setPriority(priority);

        if (Objects.nonNull(policyType))
            policy.setType(policyType);
        else
            throw new IllegalArgumentException("PolicyType cannot be blank, needs to be specified.");

        if (Strings.hasText(status.toString()))
            policy.setStatus(status);

        return policy;
    }

}

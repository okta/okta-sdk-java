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

import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.LifecycleStatus;
import com.okta.sdk.resource.PolicyType;
import com.okta.sdk.resource.Policy;
import com.okta.sdk.resource.builder.PolicyBuilder;

import java.util.Objects;

public class DefaultPolicyBuilder<T extends PolicyBuilder> implements PolicyBuilder<T> {

    protected String name;
    protected String description;
    protected PolicyType policyType;
    protected Integer priority;
    protected LifecycleStatus status;
    protected Boolean isActive = true;

    DefaultPolicyBuilder(){
        this.status = LifecycleStatus.ACTIVE;
    }

    @Override
    public T setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public T setDescription(String description) {
        this.description = description;
        return self();
    }

    @Override
    public T setType(PolicyType policyType) {
        this.policyType = policyType;
        return self();
    }

    @Override
    public T setPriority(Integer priority) {
        this.priority = priority;
        return self();
    }

    @Override
    public T setStatus(LifecycleStatus status) {
        this.status = status;
        if (LifecycleStatus.ACTIVE.equals(status))
            this.isActive = true;
        else
            this.isActive = false;
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public Policy buildAndCreate(Client client) {
        return client.createPolicy(build(client), isActive);
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

        if (Objects.nonNull(status))
            policy.setStatus(status);

        return policy;
    }

}

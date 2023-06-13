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
package com.okta.sdk.resource.policy;

import com.okta.commons.lang.Classes;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PolicyApi;
import org.openapitools.client.model.LifecycleStatus;
import org.openapitools.client.model.Policy;
import org.openapitools.client.model.PolicyType;

public interface PolicyBuilder<T extends PolicyBuilder> {

    static PolicyBuilder<PolicyBuilder> instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPolicyBuilder");
    }

    T setName(String name);

    T setDescription(String description);

    T setType(PolicyType policyType);

    T setPriority(Integer priority);

    T setStatus(LifecycleStatus status);

    Policy buildAndCreate(PolicyApi client) throws ApiException;
}

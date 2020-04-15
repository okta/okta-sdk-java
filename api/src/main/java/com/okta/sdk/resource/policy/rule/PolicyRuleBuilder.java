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
package com.okta.sdk.resource.policy.rule;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.Policy;
import com.okta.sdk.resource.policy.PolicyRule;

public interface PolicyRuleBuilder<T extends PolicyRuleBuilder> {

    static PolicyRuleBuilder<PolicyRuleBuilder> instance(){
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPolicyRuleBuilder");
    }

    T setId(String id);

    T setPriority(Integer priority);

    T setStatus(PolicyRule.StatusEnum status);

    T setType(PolicyRule.TypeEnum type);

    PolicyRule buildAndCreate(Client client, Policy policy);

}

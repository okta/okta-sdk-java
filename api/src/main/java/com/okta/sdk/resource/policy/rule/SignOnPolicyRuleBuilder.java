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
package com.okta.sdk.resource.policy.rule;

import com.okta.commons.lang.Classes;
import com.okta.sdk.resource.authorization.server.OktaSignOnPolicyFactorPromptMode;
import com.okta.sdk.resource.authorization.server.PolicyAccess;
import com.okta.sdk.resource.authorization.server.PolicyNetworkConnection;
import com.okta.sdk.resource.authorization.server.PolicyRuleAuthContextType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface SignOnPolicyRuleBuilder extends PolicyRuleBuilder<SignOnPolicyRuleBuilder> {
    static SignOnPolicyRuleBuilder instance(){
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultSignOnPolicyRuleBuilder");
    }

    SignOnPolicyRuleBuilder setName(String name);

    SignOnPolicyRuleBuilder setAccess(PolicyAccess access);

    SignOnPolicyRuleBuilder setFactorLifetime(Integer factorLifetime);

    SignOnPolicyRuleBuilder setFactorPromptMode(OktaSignOnPolicyFactorPromptMode factorPromptMode);

    SignOnPolicyRuleBuilder setRememberDeviceByDefault(Boolean rememberDeviceByDefault);

    SignOnPolicyRuleBuilder setRequireFactor(Boolean requireFactor);

    SignOnPolicyRuleBuilder setMaxSessionIdleMinutes(Integer maxSessionIdleMinutes);

    SignOnPolicyRuleBuilder setMaxSessionLifetimeMinutes(Integer maxSessionLifetimeMinutes);

    SignOnPolicyRuleBuilder setUsePersistentCookie(Boolean usePersistentCookie);

    SignOnPolicyRuleBuilder setAuthType(PolicyRuleAuthContextType authType);

    SignOnPolicyRuleBuilder setNetworkConnection(PolicyNetworkConnection connection);

    default SignOnPolicyRuleBuilder setUsers(String... userIds) {
        return setUsers(Arrays.stream(userIds).collect(Collectors.toList()));
    }

    SignOnPolicyRuleBuilder setUsers(List<String> userIds);

    SignOnPolicyRuleBuilder addUser(String userId);

    default SignOnPolicyRuleBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    SignOnPolicyRuleBuilder setGroups(List<String> groupIds);

    SignOnPolicyRuleBuilder addGroup(String groupId);


}

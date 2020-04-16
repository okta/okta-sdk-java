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
import com.okta.sdk.resource.policy.PasswordPolicyRuleAction;
import com.okta.sdk.resource.policy.PolicyNetworkCondition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PasswordPolicyRuleBuilder extends PolicyRuleBuilder<PasswordPolicyRuleBuilder> {

    static PasswordPolicyRuleBuilder instance(){
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultPasswordPolicyRuleBuilder");
    }

    PasswordPolicyRuleBuilder setName(String name);

    PasswordPolicyRuleBuilder setNetworkConnection(PolicyNetworkCondition.ConnectionEnum connection);

    default PasswordPolicyRuleBuilder setUsers(String... userIds) {
        return setUsers(Arrays.stream(userIds).collect(Collectors.toList()));
    }

    PasswordPolicyRuleBuilder setUsers(List<String> userIds);

    PasswordPolicyRuleBuilder addUser(String userId);

    default PasswordPolicyRuleBuilder setGroups(String... groupIds) {
        return setGroups(Arrays.stream(groupIds).collect(Collectors.toList()));
    }

    PasswordPolicyRuleBuilder setGroups(List<String> groupIds);

    PasswordPolicyRuleBuilder addGroup(String groupId);

    PasswordPolicyRuleBuilder setSelfServiceUnlockAccess(PasswordPolicyRuleAction.AccessEnum unlockAccess);

    PasswordPolicyRuleBuilder setSelfServicePasswordResetAccess(PasswordPolicyRuleAction.AccessEnum passwordResetAccess);

    PasswordPolicyRuleBuilder setPasswordChangeAccess(PasswordPolicyRuleAction.AccessEnum passwordChangeAccess);

}

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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.*;
import com.okta.sdk.resource.policy.rule.PasswordPolicyRuleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultPasswordPolicyRuleBuilder extends DefaultPolicyRuleBuilder<PasswordPolicyRuleBuilder> implements PasswordPolicyRuleBuilder {

    private String name;
    private PolicyNetworkCondition.ConnectionEnum connection;
    private List<String> userIds = new ArrayList<>();
    private List<String> groupIds = new ArrayList<>();
    private PasswordPolicyRuleAction.AccessEnum unlockAccess;
    private PasswordPolicyRuleAction.AccessEnum passwordResetAccess;
    private PasswordPolicyRuleAction.AccessEnum passwordChangeAccess;



    @Override
    public PasswordPolicyRuleBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setNetworkConnection(PolicyNetworkCondition.ConnectionEnum connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setUsers(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder addUser(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setGroups(List<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setSelfServiceUnlockAccess(PasswordPolicyRuleAction.AccessEnum unlockAccess) {
        this.unlockAccess = unlockAccess;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setSelfServicePasswordResetAccess(PasswordPolicyRuleAction.AccessEnum passwordResetAccess) {
        this.passwordResetAccess = passwordResetAccess;
        return this;
    }

    @Override
    public PasswordPolicyRuleBuilder setPasswordChangeAccess(PasswordPolicyRuleAction.AccessEnum passwordChangeAccess) {
        this.passwordChangeAccess = passwordChangeAccess;
        return this;
    }

    @Override
    public PasswordPolicyRule buildAndCreate(Client client, Policy policy){
        return (PasswordPolicyRule) policy.createRule(build(client));
    }

    private PasswordPolicyRule build(Client client){
        PasswordPolicyRule policyRule = client.instantiate(PasswordPolicyRule.class);

        if (Objects.nonNull(priority)) policyRule.setPriority(priority);

        if (Objects.nonNull(status)) policyRule.setStatus(status);

        if (Strings.hasText(name)) policyRule.setName(name);

        if (Objects.nonNull(type))
            if(type.equals(PolicyRule.TypeEnum.PASSWORD))
                policyRule.setType(type);
            else
                throw new IllegalArgumentException("Type should be specified as PASSWORD while using PasswordPolicyRuleBuilder.");


        // Actions
        policyRule.setActions(client.instantiate(PasswordPolicyRuleActions.class));
        PasswordPolicyRuleActions passwordPolicyRuleActions = policyRule.getActions();

        if (Objects.nonNull(passwordChangeAccess))
            passwordPolicyRuleActions.setPasswordChange(client.instantiate(PasswordPolicyRuleAction.class).setAccess(passwordChangeAccess));
        if (Objects.nonNull(passwordResetAccess))
            passwordPolicyRuleActions.setSelfServicePasswordReset(client.instantiate(PasswordPolicyRuleAction.class).setAccess(passwordResetAccess));
        if (Objects.nonNull(unlockAccess))
            passwordPolicyRuleActions.setSelfServiceUnlock(client.instantiate(PasswordPolicyRuleAction.class).setAccess(unlockAccess));

        // Conditions
        policyRule.setConditions(client.instantiate(PasswordPolicyRuleConditions.class));
        PasswordPolicyRuleConditions passwordPolicyRuleConditions = policyRule.getConditions();
        PolicyNetworkCondition policyNetworkCondition = client.instantiate(PolicyNetworkCondition.class);
        PolicyPeopleCondition policyPeopleCondition = client.instantiate(PolicyPeopleCondition.class);

        if (Objects.nonNull(connection))
            passwordPolicyRuleConditions.setNetwork(policyNetworkCondition.setConnection(connection));

        if (!Collections.isEmpty(userIds))
            passwordPolicyRuleConditions.setPeople(policyPeopleCondition
                .setUsers(client.instantiate(UserCondition.class).setInclude(userIds)));
        if (!Collections.isEmpty(groupIds))
            passwordPolicyRuleConditions.setPeople(policyPeopleCondition
                .setGroups(client.instantiate(GroupCondition.class).setInclude(groupIds)));

        return policyRule;
    }
}

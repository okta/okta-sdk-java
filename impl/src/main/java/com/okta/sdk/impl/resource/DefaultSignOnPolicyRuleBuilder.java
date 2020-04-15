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

import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.*;
import com.okta.sdk.resource.policy.rule.SignOnPolicyRuleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultSignOnPolicyRuleBuilder extends DefaultPolicyRuleBuilder<SignOnPolicyRuleBuilder> implements SignOnPolicyRuleBuilder {

    private String name;
    private List<String> groupIds = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();
    private OktaSignOnPolicyRuleSignonActions.AccessEnum access;
    private Integer factorLifetime;
    private OktaSignOnPolicyRuleSignonActions.FactorPromptModeEnum factorPromptMode;
    private Boolean rememberDeviceByDefault;
    private Boolean requireFactor;
    private Integer maxSessionIdleMinutes;
    private Integer maxSessionLifetimeMinutes;
    private Boolean usePersistentCookie;
    private PolicyRuleAuthContextCondition.AuthTypeEnum authType;
    private PolicyNetworkCondition.ConnectionEnum connection;



    @Override
    public SignOnPolicyRuleBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum access) {
        this.access = access;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setFactorLifetime(Integer factorLifetime) {
        this.factorLifetime = factorLifetime;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setFactorPromptMode(OktaSignOnPolicyRuleSignonActions.FactorPromptModeEnum factorPromptMode) {
        this.factorPromptMode = factorPromptMode;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setRememberDeviceByDefault(Boolean rememberDeviceByDefault) {
        this.rememberDeviceByDefault = rememberDeviceByDefault;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setRequireFactor(Boolean requireFactor) {
        this.requireFactor = requireFactor;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setMaxSessionIdleMinutes(Integer maxSessionIdleMinutes) {
        this.maxSessionIdleMinutes = maxSessionIdleMinutes;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setMaxSessionLifetimeMinutes(Integer maxSessionLifetimeMinutes) {
        this.maxSessionLifetimeMinutes = maxSessionLifetimeMinutes;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setUsePersistentCookie(Boolean usePersistentCookie) {
        this.usePersistentCookie = usePersistentCookie;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum authType) {
        this.authType = authType;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setNetworkConnection(PolicyNetworkCondition.ConnectionEnum connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setUsers(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder addUser(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder setGroups(List<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    @Override
    public SignOnPolicyRuleBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public OktaSignOnPolicyRule buildAndCreate(Client client, Policy policy) {
        return (OktaSignOnPolicyRule) policy.createRule(build(client), isActive);
    }

    private OktaSignOnPolicyRule build(Client client){
        OktaSignOnPolicyRule policyRule = client.instantiate(OktaSignOnPolicyRule.class);

        if (Strings.hasText(name)) policyRule.setName(name);

        if (Strings.hasText(id)) policyRule.setId(id);

        if (Objects.nonNull(priority)) policyRule.setPriority(priority);

        if (Objects.nonNull(status)) policyRule.setStatus(status);

        if (Objects.nonNull(type))
            if(type.equals(PolicyRule.TypeEnum.SIGN_ON))
                policyRule.setType(type);
            else
                throw new IllegalArgumentException("Type should be SIGN_ON while using SignOnPolicyRuleBuilder.");


        // Actions
        policyRule.setActions(client.instantiate(OktaSignOnPolicyRuleActions.class));
        OktaSignOnPolicyRuleActions oktaSignOnPolicyRuleActions = policyRule.getActions();
        OktaSignOnPolicyRuleSignonActions oktaSignOnPolicyRuleSignonActions = client.instantiate(OktaSignOnPolicyRuleSignonActions.class);

        if (Objects.nonNull(access))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setAccess(access));
        if (Objects.nonNull(factorLifetime))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setFactorLifetime(factorLifetime));
        if (Objects.nonNull(factorPromptMode))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setFactorPromptMode(factorPromptMode));
        if (Objects.nonNull(requireFactor))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setRequireFactor(requireFactor));
        if (Objects.nonNull(rememberDeviceByDefault))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setRememberDeviceByDefault(rememberDeviceByDefault));

        OktaSignOnPolicyRuleSignonSessionActions oktaSignOnPolicyRuleSignonSessionActions = client.instantiate(OktaSignOnPolicyRuleSignonSessionActions.class);

        if (Objects.nonNull(maxSessionIdleMinutes))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setSession(oktaSignOnPolicyRuleSignonSessionActions
                .setMaxSessionIdleMinutes(maxSessionIdleMinutes)));
        if (Objects.nonNull(maxSessionLifetimeMinutes))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setSession(oktaSignOnPolicyRuleSignonSessionActions
                .setMaxSessionLifetimeMinutes(maxSessionLifetimeMinutes)));
        if (Objects.nonNull(usePersistentCookie))
            oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions.setSession(oktaSignOnPolicyRuleSignonSessionActions
                .setUsePersistentCookie(usePersistentCookie)));

        // Conditions
        policyRule.setConditions(client.instantiate(OktaSignOnPolicyRuleConditions.class));
        OktaSignOnPolicyRuleConditions oktaSignOnPolicyRuleConditions = policyRule.getConditions();

        if (Objects.nonNull(authType))
            oktaSignOnPolicyRuleConditions.setAuthContext(client.instantiate(PolicyRuleAuthContextCondition.class)
                .setAuthType(authType));

        PolicyNetworkCondition policyNetworkCondition = client.instantiate(PolicyNetworkCondition.class);
        PolicyPeopleCondition policyPeopleCondition = client.instantiate(PolicyPeopleCondition.class);

        if (Objects.nonNull(connection))
            oktaSignOnPolicyRuleConditions.setNetwork(policyNetworkCondition.setConnection(connection));

        if (!Collections.isEmpty(userIds))
            oktaSignOnPolicyRuleConditions.setPeople(policyPeopleCondition
                .setUsers(client.instantiate(UserCondition.class).setInclude(userIds)));
        if (!Collections.isEmpty(groupIds))
            oktaSignOnPolicyRuleConditions.setPeople(policyPeopleCondition
                .setGroups(client.instantiate(GroupCondition.class).setInclude(groupIds)));

        return policyRule;
    }
}


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
import com.okta.sdk.resource.group.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultGroupRuleBuilder implements GroupRuleBuilder {

    private String name;
    private String type;
    private List<String> assignUserToGroups = new ArrayList<>();
    private List<String> groupIds = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();
    private String groupRuleExpressionType;
    private String groupRuleExpressionValue;

    @Override
    public GroupRuleBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GroupRuleBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public GroupRuleBuilder setAssignUserToGroups(List<String> assignUserToGroups) {
        this.assignUserToGroups = assignUserToGroups;
        return this;
    }

    @Override
    public GroupRuleBuilder setGroups(List<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    @Override
    public GroupRuleBuilder addGroup(String groupId) {
        this.groupIds.add(groupId);
        return this;
    }

    @Override
    public GroupRuleBuilder setUsers(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    @Override
    public GroupRuleBuilder addUser(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public GroupRuleBuilder setGroupRuleExpressionType(String groupRuleExpressionType) {
        this.groupRuleExpressionType = groupRuleExpressionType;
        return this;
    }

    @Override
    public GroupRuleBuilder setGroupRuleExpressionValue(String groupRuleExpressionValue) {
        this.groupRuleExpressionValue = groupRuleExpressionValue;
        return this;
    }

    @Override
    public GroupRule buildAndCreate(Client client) {
        return client.createGroupRule(build(client));
    }

    private GroupRule build(Client client){
        GroupRule groupRule = client.instantiate(GroupRule.class);

        if (Strings.hasText(name)) groupRule.setName(name);

        if (Strings.hasText(type)) groupRule.setType(type);

        // Actions
        groupRule.setActions(client.instantiate(GroupRuleAction.class));
        GroupRuleAction groupRuleActions = groupRule.getActions();

        if (!Collections.isEmpty(assignUserToGroups))
            groupRuleActions.setAssignUserToGroups(client.instantiate(GroupRuleGroupAssignment.class)
                .setGroupIds(assignUserToGroups));

        // Conditions
        groupRule.setConditions(client.instantiate(GroupRuleConditions.class));
        GroupRuleConditions groupRuleConditions = groupRule.getConditions();
        GroupRulePeopleCondition groupRulePeopleCondition = client.instantiate(GroupRulePeopleCondition.class);

        if (!Collections.isEmpty(groupIds))
            groupRuleConditions.setPeople(groupRulePeopleCondition
                .setGroups(client.instantiate(GroupRuleGroupCondition.class)
                    .setInclude(groupIds)));

        if (!Collections.isEmpty(userIds))
            groupRuleConditions.setPeople(groupRulePeopleCondition
                .setUsers(client.instantiate(GroupRuleUserCondition.class)
                    .setInclude(userIds)));

        GroupRuleExpression groupRuleExpression = client.instantiate(GroupRuleExpression.class);

        if (Strings.hasText(groupRuleExpressionType))
            groupRuleConditions.setExpression(groupRuleExpression.setType(groupRuleExpressionType));

        if (Strings.hasText(groupRuleExpressionValue))
            groupRuleConditions.setExpression(groupRuleExpression.setValue(groupRuleExpressionValue));

        return groupRule;
    }
}

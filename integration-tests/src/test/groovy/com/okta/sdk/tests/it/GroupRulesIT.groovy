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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.rule.GroupRule
import com.okta.sdk.resource.group.rule.GroupRuleAction
import com.okta.sdk.resource.group.rule.GroupRuleConditions
import com.okta.sdk.resource.group.rule.GroupRuleExpression
import com.okta.sdk.resource.group.rule.GroupRuleGroupAssignment
import com.okta.sdk.resource.group.rule.GroupRuleStatus

/**
 * Tests for /api/v1/groups/rules
 * @since 0.5.0
 */
class GroupRulesIT implements CrudTestSupport {

    Group group

    void preTestSetup(Client client) {
        group = GroupBuilder.instance()
                .setName("my-user-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)
    }

    @Override
    def create(Client client) {

        GroupRule rule = client.instantiate(GroupRule)
        rule.setName("rule+" + UUID.randomUUID().toString() )
        rule.setType("group_rule")
        rule.setStatus(GroupRuleStatus.ACTIVE)

        rule.setConditions(client.instantiate(GroupRuleConditions))
        rule.getConditions().setExpression(client.instantiate(GroupRuleExpression))
        rule.getConditions().getExpression().setValue("user.firstName==\"Joe\"")

        rule.setActions(client.instantiate(GroupRuleAction))
        rule.getActions().setAssignUserToGroups(client.instantiate(GroupRuleGroupAssignment))
        rule.getActions().getAssignUserToGroups().setGroupIds(Collections.singletonList(group.id))

        return client.createRule(rule)
    }

    @Override
    def read(Client client, String id) {
        return client.getRule(id)
    }

    @Override
    void update(Client client, def rule) {
        rule.setName(rule.name +"-2")
        rule.update()
    }

    @Override
    void delete(Client client, def rule) {
        rule.delete()
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listRules().iterator()
    }
}

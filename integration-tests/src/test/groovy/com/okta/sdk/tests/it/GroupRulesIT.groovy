/*
 * Copyright 2018-Present Okta, Inc.
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
import com.okta.sdk.resource.group.rule.GroupRuleGroupCondition
import com.okta.sdk.resource.group.rule.GroupRulePeopleCondition
import com.okta.sdk.resource.group.rule.GroupRuleStatus
import com.okta.sdk.resource.group.rule.GroupRuleUserCondition
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.validateUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/groups/rules}.
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

        rule.setConditions(client.instantiate(GroupRuleConditions))
        rule.getConditions().setExpression(client.instantiate(GroupRuleExpression))
        rule.getConditions().getExpression().setValue("user.firstName==\"Joe\"")

        rule.setActions(client.instantiate(GroupRuleAction))
        rule.getActions().setAssignUserToGroups(client.instantiate(GroupRuleGroupAssignment))
        rule.getActions().getAssignUserToGroups().setGroupIds(Collections.singletonList(group.getId()))

        rule = client.createGroupRule(rule)
        registerForCleanup(rule)

        return rule
    }

    @Override
    def read(Client client, String id) {
        return client.getGroupRule(id)
    }

    @Override
    void update(Client client, def rule) {
        rule.setName(rule.name +"-2")
        rule.update()
    }

    @Override
    void assertUpdate(Client client, def resource) {
        assertThat resource.name, matchesPattern('^rule.*-2$')
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listGroupRules().iterator()
    }

    @Test
    @Scenario("group-rule-operations")
    @TestResources(rules = ["Test group rule", "Test group rule updated"])
    void groupRuleCrudTest() {

        def password = 'Passw0rd!2@3#'
        def firstName = 'John'
        def lastName = 'With-Group-Rule'
        def email = "john-${uniqueTestName}@example.com"
        def groupName = "Group-Rule API Test Group ${uniqueTestName}"
        def groupRuleName = ("Group-Rule ${uniqueTestName}").substring(0, 49)

        // 1. Create a user and a group
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password.toCharArray())
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)
        validateUser(user, firstName, lastName, email)

        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)

        // 2. Create a group rule and verify rule executes
        GroupRule rule = client.instantiate(GroupRule)
                .setType('group_rule')
                .setName(groupRuleName)
                .setConditions(client.instantiate(GroupRuleConditions)
                        .setPeople(client.instantiate(GroupRulePeopleCondition)
                            .setUsers(client.instantiate(GroupRuleUserCondition)
                                    .setExclude([]))
                            .setGroups(client.instantiate(GroupRuleGroupCondition)
                                    .setExclude([])))
                        .setExpression(client.instantiate(GroupRuleExpression)
                                .setValue("user.lastName==\"${user.getProfile().lastName}\"".toString())
                                .setType('urn:okta:expression:1.0')))
                .setActions(client.instantiate(GroupRuleAction)
                        .setAssignUserToGroups(client.instantiate(GroupRuleGroupAssignment)
                                .setGroupIds(Collections.singletonList(group.getId()))))
        rule = client.createGroupRule(rule)
        registerForCleanup(rule)
        rule.activate()

        GroupRule readRule = client.getGroupRule(rule.getId())
        assertThat readRule.getStatus(), equalTo(GroupRuleStatus.ACTIVE)

        // 3. List group rules
        assertPresent(client.listGroupRules(), rule)

        // 4. Deactivate the rule and update it
        rule.deactivate()

        rule.name = 'Test group rule updated ${uniqueTestName}'
        rule.getConditions().getExpression().value = 'user.lastName==\"incorrect\"'
        rule.update()
        rule.activate()

        readRule = client.getGroupRule(rule.getId())
        assertThat readRule.getStatus(), equalTo(GroupRuleStatus.ACTIVE)

        // 5. delete rule
        rule.deactivate()
        rule.delete()
    }
}

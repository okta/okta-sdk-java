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
package com.okta.sdk.tests.it.spec

import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.group.rule.*
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.TestResources
import com.okta.sdk.tests.it.util.ClientProvider
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.*

class GroupRuleIT implements ClientProvider {

    @Test
    @Scenario("group-rule-operations")
    @TestResources(
            users = "john-with-group-rule@example.com",
            groups = "Group-Rule API Test Group",
            rules = ["Test group rule", "Test group rule updated"])
    void groupRuleCrudTest() {

        def password = 'Abcd1234'
        def firstName = 'John'
        def lastName = 'With-Group-Rule'
        def email = 'john-with-group-rule@example.com'
        def groupName = 'Group-Rule API Test Group'

        // 1. Create a user and a group
        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .buildAndCreate(client, true)
        registerForCleanup(user)

        validateUser(user, firstName, lastName, email)

        Group group = GroupBuilder.instance()
                .setName(groupName)
                .buildAndCreate(client)
        registerForCleanup(group)

        // 2. Create a group rule and verify rule executes
        GroupRule rule = client.instantiate(GroupRule)
                .setType('group_rule')
                .setName('Test group rule')
                .setConditions(client.instantiate(GroupRuleConditions)
                        .setPeople(client.instantiate(GroupRulePeopleCondition)
                            .setUsers(client.instantiate(GroupRuleUserCondition)
                                    .setExclude([]))
                            .setGroups(client.instantiate(GroupRuleGroupCondition)
                                    .setExclude([])))
                        .setExpression(client.instantiate(GroupRuleExpression)
                                .setValue("user.lastName==\"${user.profile.lastName}\"".toString())
                                .setType('urn:okta:expression:1.0')))
                .setActions(client.instantiate(GroupRuleAction)
                        .setAssignUserToGroups(client.instantiate(GroupRuleGroupAssignment)
                                .setGroupIds(Collections.singletonList(group.id))))
        rule = client.createRule(rule)
        registerForCleanup(rule)
        rule.activate()

        // Delay is needed as there is some time between activating the rule and triggering it
        sleep(30000)
        assertUserInGroup(user, group)

        // 3. List group rules
        assertPresent(client.listRules(), rule)

        // 4. Deactivate the rule and update it
        rule.deactivate()

        rule.name = 'Test group rule updated'
        rule.conditions.expression.value = 'user.lastName==\"incorrect\"'
        rule.update()
        rule.activate()

        // Triggering the updated rule will remove the user from group
        sleep(10000)
        assertUserNotInGroup(user, group)
    }
}

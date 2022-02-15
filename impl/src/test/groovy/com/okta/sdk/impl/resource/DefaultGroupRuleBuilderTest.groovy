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
package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.impl.resource.builder.DefaultGroupRuleBuilder
import com.okta.sdk.resource.GroupRule
import com.okta.sdk.resource.GroupRuleAction
import com.okta.sdk.resource.GroupRuleConditions
import com.okta.sdk.resource.GroupRuleExpression
import com.okta.sdk.resource.GroupRuleGroupAssignment
import com.okta.sdk.resource.GroupRuleGroupCondition
import com.okta.sdk.resource.GroupRulePeopleCondition
import com.okta.sdk.resource.GroupRuleUserCondition
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultGroupRuleBuilderTest {

    @Test
    void basicUsage(){
        def client  = mock(Client)
        def groupRule = mock(GroupRule)
        def groupRuleAction = mock(GroupRuleAction)
        def groupRuleConditions = mock(GroupRuleConditions)
        def groupRuleExpression = mock(GroupRuleExpression)
        def groupRulePeopleCondition = mock(GroupRulePeopleCondition)
        def groupRuleGroupCondition = mock(GroupRuleGroupCondition)
        def groupRuleUserCondition = mock(GroupRuleUserCondition)
        def groupRuleGroupAssignment = mock(GroupRuleGroupAssignment)

        when(client.instantiate(GroupRule.class)).thenReturn(groupRule)
        when(client.instantiate(GroupRuleAction.class)).thenReturn(groupRuleAction)
        when(groupRule.getActions()).thenReturn(groupRuleAction)
        when(client.instantiate(GroupRuleConditions.class)).thenReturn(groupRuleConditions)
        when(groupRule.getConditions()).thenReturn(groupRuleConditions)
        when(client.instantiate(GroupRuleExpression.class)).thenReturn(groupRuleExpression)
        when(client.instantiate(GroupRulePeopleCondition.class)).thenReturn(groupRulePeopleCondition)
        when(client.instantiate(GroupRuleGroupCondition.class)).thenReturn(groupRuleGroupCondition)
        when(client.instantiate(GroupRuleUserCondition.class)).thenReturn(groupRuleUserCondition)
        when(client.instantiate(GroupRuleGroupAssignment.class)).thenReturn(groupRuleGroupAssignment)

        new DefaultGroupRuleBuilder()
            .setName("dummy rule")
            .setType("group_rule")
            .addGroup("ajhd1234kak")
        .buildAndCreate(client)

        verify(client).createGroupRule(eq(groupRule))
        verify(groupRule).setName("dummy rule")
        verify(groupRule).setType("group_rule")
        verify(groupRuleGroupCondition).setInclude(Arrays.asList("ajhd1234kak"))

    }
}

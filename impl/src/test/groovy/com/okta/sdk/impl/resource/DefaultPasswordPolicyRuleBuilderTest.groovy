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
import com.okta.sdk.resource.policy.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultPasswordPolicyRuleBuilderTest {

    @Test
    void basicUsage(){
        def client = mock(Client)
        def policy = mock(Policy)
        def passwordPolicyRule = mock(PasswordPolicyRule)
        def passwordPolicyRuleActions = mock(PasswordPolicyRuleActions)
        def passwordPolicyRuleConditions = mock(PasswordPolicyRuleConditions)
        def passwordPolicyRuleAction = mock(PasswordPolicyRuleAction)
        def policyNetworkCondition = mock(PolicyNetworkCondition)
        def policyPeopleCondition = mock(PolicyPeopleCondition)
        def groupCondition = mock(GroupCondition)
        def userCondition = mock(UserCondition)

        when(client.instantiate(PasswordPolicyRule.class)).thenReturn(passwordPolicyRule)
        when(client.instantiate(PasswordPolicyRuleActions.class)).thenReturn(passwordPolicyRuleActions)
        when(passwordPolicyRule.getActions()).thenReturn(passwordPolicyRuleActions)
        when(client.instantiate(PasswordPolicyRuleConditions.class)).thenReturn(passwordPolicyRuleConditions)
        when(passwordPolicyRule.getConditions()).thenReturn(passwordPolicyRuleConditions)
        when(client.instantiate(PasswordPolicyRuleAction.class)).thenReturn(passwordPolicyRuleAction)
        when(client.instantiate(PolicyNetworkCondition.class)).thenReturn(policyNetworkCondition)
        when(client.instantiate(PolicyPeopleCondition.class)).thenReturn(policyPeopleCondition)
        when(client.instantiate(GroupCondition.class))thenReturn(groupCondition)
        when(client.instantiate(UserCondition.class)).thenReturn(userCondition)

        new DefaultPasswordPolicyRuleBuilder()
            .setName("name here")
            .setStatus(LifecycleStatus.ACTIVE)
            .setType(PolicyRuleType.PASSWORD)
            .setPriority(1)
            .addGroup("sdjfsdyfjsfsj")
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setSelfServicePasswordResetAccess(PolicyAccess.ALLOW)
            .setSelfServiceUnlockAccess(PolicyAccess.ALLOW)
            .setSelfServiceUnlockAccess(PolicyAccess.DENY)
        .buildAndCreate(client, policy)

        verify(policy).createRule(eq(passwordPolicyRule))
        verify(passwordPolicyRule).setName("name here")
        verify(passwordPolicyRule).setType(PolicyRuleType.PASSWORD)
        verify(passwordPolicyRuleActions).setSelfServicePasswordReset(passwordPolicyRuleAction.setAccess(PolicyAccess.ALLOW))
        verify(passwordPolicyRuleConditions).setNetwork(policyNetworkCondition.setConnection(PolicyNetworkConnection.ANYWHERE))
    }

    @Test
    void createWithoutType(){
        def client = mock(Client)
        def policy = mock(Policy)
        def passwordPolicyRule = mock(PasswordPolicyRule)

        when(client.instantiate(PasswordPolicyRule.class)).thenReturn(passwordPolicyRule)
        expect IllegalArgumentException, {
            new DefaultPasswordPolicyRuleBuilder()
                .setName("test rule")
                .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(client, policy)
        }
    }

    @Test
    void createWithSignOnType(){
        def client = mock(Client)
        def policy = mock(Policy)
        def passwordPolicyRule = mock(PasswordPolicyRule)

        when(client.instantiate(PasswordPolicyRule.class)).thenReturn(passwordPolicyRule)
        expect IllegalArgumentException, {
            new DefaultPasswordPolicyRuleBuilder()
                .setName("test rule")
                .setStatus(LifecycleStatus.ACTIVE)
                .setType(PolicyRuleType.SIGN_ON)
                .buildAndCreate(client, policy)
        }
    }
}

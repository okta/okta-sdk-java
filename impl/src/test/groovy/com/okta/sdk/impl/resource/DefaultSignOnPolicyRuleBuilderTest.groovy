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
import com.okta.sdk.resource.authorization.server.*
import com.okta.sdk.resource.*
import com.okta.sdk.resource.common.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultSignOnPolicyRuleBuilderTest {

    @Test
    void basicUsage(){
        def client = mock(Client)
        def policy = mock(Policy)
        def signOnPolicyRule = mock(OktaSignOnPolicyRule)
        def oktaSignOnPolicyRuleActions = mock(OktaSignOnPolicyRuleActions)
        def oktaSignOnPolicyRuleConditions = mock(OktaSignOnPolicyRuleConditions)
        def oktaSignOnPolicyRuleSignonActions = mock(OktaSignOnPolicyRuleSignonActions)
        def policyNetworkCondition = mock(PolicyNetworkCondition)
        def policyPeopleCondition = mock(PolicyPeopleCondition)
        def groupCondition = mock(GroupCondition)
        def userCondition = mock(UserCondition)
        def oktaSignOnPolicyRuleSignonSessionActions = mock(OktaSignOnPolicyRuleSignonSessionActions)
        def policyRuleAuthContextCondition = mock(PolicyRuleAuthContextCondition)

        when(client.instantiate(OktaSignOnPolicyRule.class)).thenReturn(signOnPolicyRule)
        when(client.instantiate(OktaSignOnPolicyRuleActions.class)).thenReturn(oktaSignOnPolicyRuleActions)
        when(signOnPolicyRule.getActions()).thenReturn(oktaSignOnPolicyRuleActions)
        when(client.instantiate(PasswordPolicyRuleConditions.class)).thenReturn(oktaSignOnPolicyRuleConditions)
        when(signOnPolicyRule.getConditions()).thenReturn(oktaSignOnPolicyRuleConditions)
        when(client.instantiate(OktaSignOnPolicyRuleSignonActions.class)).thenReturn(oktaSignOnPolicyRuleSignonActions)
        when(client.instantiate(PolicyNetworkCondition.class)).thenReturn(policyNetworkCondition)
        when(client.instantiate(PolicyPeopleCondition.class)).thenReturn(policyPeopleCondition)
        when(client.instantiate(GroupCondition.class))thenReturn(groupCondition)
        when(client.instantiate(UserCondition.class)).thenReturn(userCondition)
        when(client.instantiate(OktaSignOnPolicyRuleSignonSessionActions.class)).thenReturn(oktaSignOnPolicyRuleSignonSessionActions)
        when(client.instantiate(PolicyRuleAuthContextCondition.class)).thenReturn(policyRuleAuthContextCondition)

        new DefaultSignOnPolicyRuleBuilder()
            .setName("name here")
            .setStatus(LifecycleStatus.ACTIVE)
            .setType(PolicyRuleType.SIGN_ON)
            .setPriority(1)
            .addGroup("sdjfsdyfjsfsj")
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setAccess(PolicyAccess.ALLOW)
            .setAuthType(PolicyRuleAuthContextType.ANY)
            .setFactorLifetime(1)
            .setFactorPromptMode(OktaSignOnPolicyFactorPromptMode.SESSION)
            .setRequireFactor(true)
            .setUsePersistentCookie(true)
            .setRememberDeviceByDefault(true)
            .setMaxSessionLifetimeMinutes(10)
            .setMaxSessionIdleMinutes(5)
            .buildAndCreate(client, policy)

        verify(policy).createRule(eq(signOnPolicyRule))
        verify(signOnPolicyRule).setName("name here")
        verify(signOnPolicyRule).setType(PolicyRuleType.SIGN_ON)
        verify(oktaSignOnPolicyRuleSignonActions).setFactorLifetime(1)
        verify(oktaSignOnPolicyRuleSignonActions).setRememberDeviceByDefault(true)
        verify(oktaSignOnPolicyRuleSignonActions).setRequireFactor(true)
        verify(oktaSignOnPolicyRuleSignonActions).setFactorPromptMode(OktaSignOnPolicyFactorPromptMode.SESSION)
        verify(oktaSignOnPolicyRuleSignonSessionActions).setMaxSessionIdleMinutes(5)
        verify(oktaSignOnPolicyRuleSignonSessionActions).setMaxSessionLifetimeMinutes(10)
        verify(policyRuleAuthContextCondition).setAuthType(PolicyRuleAuthContextType.ANY)
        verify(oktaSignOnPolicyRuleSignonActions).setAccess(PolicyAccess.ALLOW)
    }

    @Test
    void createWithPasswordType(){
        def client = mock(Client)
        def policy = mock(Policy)
        def signOnPolicyRule = mock(OktaSignOnPolicyRule)

        when(client.instantiate(OktaSignOnPolicyRule.class)).thenReturn(signOnPolicyRule)
        expect IllegalArgumentException, {
            new DefaultSignOnPolicyRuleBuilder()
                .setName("test name")
                .setStatus(LifecycleStatus.ACTIVE)
                .setType(PolicyRuleType.PASSWORD)
            .buildAndCreate(client, policy)
        }

    }
}

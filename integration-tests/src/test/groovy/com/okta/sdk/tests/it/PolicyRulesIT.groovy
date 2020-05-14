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
import com.okta.sdk.resource.policy.GroupCondition
import com.okta.sdk.resource.policy.OktaSignOnPolicy
import com.okta.sdk.resource.policy.OktaSignOnPolicyRule
import com.okta.sdk.resource.policy.OktaSignOnPolicyRuleActions
import com.okta.sdk.resource.policy.OktaSignOnPolicyRuleConditions
import com.okta.sdk.resource.policy.OktaSignOnPolicyRuleSignonActions
import com.okta.sdk.resource.policy.OktaSignOnPolicyRuleSignonSessionActions
import com.okta.sdk.resource.policy.PasswordPolicyRule
import com.okta.sdk.resource.policy.PasswordPolicyRuleAction
import com.okta.sdk.resource.policy.PasswordPolicyRuleActions
import com.okta.sdk.resource.policy.PasswordPolicyRuleConditions
import com.okta.sdk.resource.policy.PolicyNetworkCondition
import com.okta.sdk.resource.policy.PolicyPeopleCondition
import com.okta.sdk.resource.policy.PolicyRule
import com.okta.sdk.resource.policy.PolicyRuleAuthContextCondition
import com.okta.sdk.resource.policy.UserCondition
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class PolicyRulesIT extends ITSupport implements CrudTestSupport {

    private OktaSignOnPolicy crudTestPolicy

    @Override
    def create(Client client) {

        def group = randomGroup()
        crudTestPolicy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = crudTestPolicy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setName(policyRuleName)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
                    .setRequireFactor(false))))

        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.ACTIVE))

        return policyRule
    }

    @Override
    def read(Client client, String id) {
        return crudTestPolicy.getPolicyRule(id)
    }

    @Override
    void update(Client client, def policyRule) {
        policyRule.setName(policyRule.name.replaceFirst("policyRule+", "updated+"))
        policyRule.update()
    }

    @Override
    void assertUpdate(Client client, def policyRule) {
        assertThat policyRule.name, startsWith("updated+")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return crudTestPolicy.listPolicyRules().iterator()
    }

    @Test
    void deactivateTest() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setName(policyRuleName)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
                    .setRequireFactor(false))))
        registerForCleanup(policyRule)

        // policy rule is ACTIVE by default
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.ACTIVE))

        // deactivate
        policyRule.deactivate()
        policyRule = policy.getPolicyRule(policyRule.getId())
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.INACTIVE))
    }

    @Test
    void createPasswordPolicyRule() {

        def group = randomGroup()
        def policy = randomPasswordPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        PasswordPolicyRule policyRule = policy.createRule(client.instantiate(PasswordPolicyRule)
            .setConditions(client.instantiate(PasswordPolicyRuleConditions)
                .setPeople(client.instantiate(PolicyPeopleCondition)
                    .setUsers(client.instantiate(UserCondition)
                        .setExclude(Collections.emptyList())))
                .setNetwork(client.instantiate(PolicyNetworkCondition)
                    .setConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)))
                .setActions(client.instantiate(PasswordPolicyRuleActions)
                    .setPasswordChange(client.instantiate(PasswordPolicyRuleAction)
                        .setAccess(PasswordPolicyRuleAction.AccessEnum.ALLOW))
                    .setSelfServicePasswordReset(client.instantiate(PasswordPolicyRuleAction)
                        .setAccess(PasswordPolicyRuleAction.AccessEnum.ALLOW))
                    .setSelfServiceUnlock(client.instantiate(PasswordPolicyRuleAction)
                        .setAccess(PasswordPolicyRuleAction.AccessEnum.DENY)))
            .setName(policyRuleName))
        registerForCleanup(policyRule)

        assertThat policyRule.getName(), is(policyRuleName)
        assertThat policyRule.getActions().getPasswordChange().getAccess(), is(PasswordPolicyRuleAction.AccessEnum.ALLOW)
        assertThat policyRule.getActions().getSelfServicePasswordReset().getAccess(), is(PasswordPolicyRuleAction.AccessEnum.ALLOW)
        assertThat policyRule.getActions().getSelfServiceUnlock().getAccess(), is(PasswordPolicyRuleAction.AccessEnum.DENY)
        assertThat policyRule.getConditions().getPeople().getUsers().getExclude(), hasSize(0)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
    }

    @Test
    void createOktaSignOnOnPremPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
                    .setRequireFactor(false)
                    .setRememberDeviceByDefault(false)
                    .setSession(client.instantiate(OktaSignOnPolicyRuleSignonSessionActions)
                        .setUsePersistentCookie(false)
                        .setMaxSessionIdleMinutes(720)
                        .setMaxSessionLifetimeMinutes(0))))
            .setConditions(client.instantiate(OktaSignOnPolicyRuleConditions)
                .setAuthContext(client.instantiate(PolicyRuleAuthContextCondition)
                    .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)))
            .setName(policyRuleName)
            .setType(PolicyRule.TypeEnum.SIGN_ON))
        registerForCleanup(policyRule)

        assertThat policyRule.getActions().getSignon().getAccess(), is(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(false)
        assertThat policyRule.getActions().getSignon().getRememberDeviceByDefault(), is(false)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionIdleMinutes(), is(720)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionLifetimeMinutes(), is(0)
        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
    }

    @Test
    void createOktaSignOnRadiusPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
                    .setRequireFactor(true)
                    .setFactorPromptMode(OktaSignOnPolicyRuleSignonActions.FactorPromptModeEnum.ALWAYS)
                    .setRememberDeviceByDefault(false)
                    .setSession(client.instantiate(OktaSignOnPolicyRuleSignonSessionActions)
                        .setUsePersistentCookie(false)
                        .setMaxSessionIdleMinutes(720)
                        .setMaxSessionLifetimeMinutes(0))))
            .setConditions(client.instantiate(OktaSignOnPolicyRuleConditions)
                .setNetwork(client.instantiate(PolicyNetworkCondition)
                    .setConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE))
                .setAuthContext(client.instantiate(PolicyRuleAuthContextCondition)
                    .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.RADIUS)))
            .setName(policyRuleName))
        registerForCleanup(policyRule)

        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.RADIUS)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
    }

    @Test
    void createOktaSignOnCloudPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
                    .setRequireFactor(true)
                    .setFactorPromptMode(OktaSignOnPolicyRuleSignonActions.FactorPromptModeEnum.ALWAYS)
                    .setRememberDeviceByDefault(false)
                    .setSession(client.instantiate(OktaSignOnPolicyRuleSignonSessionActions)
                        .setUsePersistentCookie(false)
                        .setMaxSessionIdleMinutes(720)
                        .setMaxSessionLifetimeMinutes(0))))
            .setConditions(client.instantiate(OktaSignOnPolicyRuleConditions)
                .setNetwork(client.instantiate(PolicyNetworkCondition)
                    .setConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE))
                .setAuthContext(client.instantiate(PolicyRuleAuthContextCondition)
                    .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY))
                .setPeople(client.instantiate(PolicyPeopleCondition)
                    .setUsers(client.instantiate(UserCondition)
                        .setInclude(Collections.emptyList())
                        .setExclude(Collections.emptyList()))
                    .setGroups(client.instantiate(GroupCondition)
                        .setInclude(Collections.emptyList())
                        .setExclude(Collections.emptyList()))))
            .setName(policyRuleName))
        registerForCleanup(policyRule)

        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
        assertThat policyRule.getConditions().getPeople().getUsers().getInclude(), is(Collections.emptyList())
        assertThat policyRule.getConditions().getPeople().getUsers().getExclude(), is(Collections.emptyList())
        assertThat policyRule.getConditions().getPeople().getGroups().getInclude(), is(Collections.emptyList())
        assertThat policyRule.getConditions().getPeople().getGroups().getExclude(), is(Collections.emptyList())
        assertThat policyRule.getActions().getSignon().getAccess(), is(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(true)
        assertThat policyRule.getActions().getSignon().getRememberDeviceByDefault(), is(false)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
        assertThat policyRule.getActions().getSignon().getSession().getUsePersistentCookie(), is(false)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionLifetimeMinutes(), is(0)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionIdleMinutes(), is(720)
    }

    @Test
    void createOktaSignOnDenyPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
                    .setRequireFactor(false)))
            .setConditions(client.instantiate(OktaSignOnPolicyRuleConditions)
                .setNetwork(client.instantiate(PolicyNetworkCondition)
                    .setConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE))
                .setAuthContext(client.instantiate(PolicyRuleAuthContextCondition)
                    .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)))
            .setName(policyRuleName))
        registerForCleanup(policyRule)

        assertThat policyRule.getType(), is(PolicyRule.TypeEnum.SIGN_ON)
        assertThat policyRule.getActions().getSignon().getAccess(), is(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(false)
        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
    }
}
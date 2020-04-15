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
import com.okta.sdk.resource.policy.*
import com.okta.sdk.resource.policy.rule.PasswordPolicyRuleBuilder
import com.okta.sdk.resource.policy.rule.SignOnPolicyRuleBuilder
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
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
            .setRequireFactor(false)
        .buildAndCreate(client, crudTestPolicy);

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
    void activateDeactivateTest() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
            .setRequireFactor(false)
            .setStatus(PolicyRule.StatusEnum.INACTIVE)
        .buildAndCreate(client, policy);

        registerForCleanup(policyRule)
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.INACTIVE))

        // activate
        policyRule.activate()
        policyRule = policy.getPolicyRule(policyRule.getId())
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
        PasswordPolicyRule policyRule = PasswordPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setSelfServiceUnlockAccess(PasswordPolicyRuleAction.AccessEnum.DENY)
            .setSelfServicePasswordResetAccess(PasswordPolicyRuleAction.AccessEnum.ALLOW)
            .setPasswordChangeAccess(PasswordPolicyRuleAction.AccessEnum.ALLOW)
            .setNetworkConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
        .buildAndCreate(client, policy)
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
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setType(PolicyRule.TypeEnum.SIGN_ON)
            .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
            .setMaxSessionLifetimeMinutes(0)
            .setMaxSessionIdleMinutes(720)
            .setUsePersistentCookie(false)
            .setRememberDeviceByDefault(false)
            .setRequireFactor(false)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
        .buildAndCreate(client, policy)
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
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.RADIUS)
            .setNetworkConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
            .setMaxSessionIdleMinutes(720)
            .setMaxSessionLifetimeMinutes(0)
            .setUsePersistentCookie(false)
            .setRememberDeviceByDefault(false)
            .setRequireFactor(false)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
            .buildAndCreate(client, policy)

        registerForCleanup(policyRule)

        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.RADIUS)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
    }

    @Test
    void createOktaSignOnCloudPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
            .setNetworkConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
            .setUsePersistentCookie(false)
            .setMaxSessionIdleMinutes(720)
            .setMaxSessionLifetimeMinutes(0)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
            .setRequireFactor(true)
            .setFactorPromptMode(OktaSignOnPolicyRuleSignonActions.FactorPromptModeEnum.ALWAYS)
            .setRememberDeviceByDefault(false)
        .buildAndCreate(client, policy)
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
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
            .setNetworkConnection(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
            .setRequireFactor(false)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
        .buildAndCreate(client, policy)

        registerForCleanup(policyRule)

        assertThat policyRule.getType(), is(PolicyRule.TypeEnum.SIGN_ON)
        assertThat policyRule.getActions().getSignon().getAccess(), is(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(false)
        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextCondition.AuthTypeEnum.ANY)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkCondition.ConnectionEnum.ANYWHERE)
    }
}
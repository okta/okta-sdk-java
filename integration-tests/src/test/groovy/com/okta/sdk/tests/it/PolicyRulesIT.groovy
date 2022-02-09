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
package com.okta.sdk.tests.it

import com.okta.sdk.client.Client
import com.okta.sdk.resource.*
import com.okta.sdk.resource.authorization.server.LifecycleStatus
import com.okta.sdk.resource.authorization.server.OktaSignOnPolicyFactorPromptMode
import com.okta.sdk.resource.authorization.server.PolicyAccess
import com.okta.sdk.resource.authorization.server.PolicyNetworkConnection
import com.okta.sdk.resource.authorization.server.PolicyRuleAuthContextType
import com.okta.sdk.resource.authorization.server.PolicyRuleType
import com.okta.sdk.resource.common.Policy
import com.okta.sdk.resource.policy.rule.PasswordPolicyRuleBuilder
import com.okta.sdk.resource.policy.rule.SignOnPolicyRuleBuilder
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.matchesPattern
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue

class PolicyRulesIT extends ITSupport implements CrudTestSupport {

    private OktaSignOnPolicy crudTestPolicy

    @Override
    def create(Client client) {

        def group = randomGroup()
        crudTestPolicy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAccess(PolicyAccess.ALLOW)
            .setRequireFactor(false)
        .buildAndCreate(client, crudTestPolicy) as OktaSignOnPolicyRule

        assertThat(policyRule.getStatus(), is(LifecycleStatus.ACTIVE))

        return policyRule
    }

    @Override
    def read(Client client, String id) {
        return client.getPolicyRule(crudTestPolicy.getId(), id)
    }

    @Override
    void update(Client client, def policyRule) {
        policyRule.setName(policyRule.name +"-2")
        client.updatePolicyRule(policyRule, crudTestPolicy.getId(), policyRule.id)
    }

    @Override
    void assertUpdate(Client client, def policyRule) {
        assertThat policyRule.name, matchesPattern('^java-sdk-it-.*-2$')
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listPolicyRules(crudTestPolicy.getId()).iterator()
    }

    @Test (groups = "group2")
    void deactivateTest() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        def policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAccess(PolicyAccess.ALLOW)
            .setRequireFactor(false)
            .setStatus(LifecycleStatus.ACTIVE)
        .buildAndCreate(client, policy)
        registerForCleanup(policyRule)

        // policy rule is ACTIVE by default
        assertThat(policyRule.getStatus(), is(LifecycleStatus.ACTIVE))

        // deactivate
        client.deactivatePolicyRule(policy.getId(), policyRule.getId())

        policyRule = client.getPolicyRule(policy.getId(), policyRule.getId())
        assertThat(policyRule.getStatus(), is(LifecycleStatus.INACTIVE))
    }

    @Test (groups = "group2")
    void listPolicyRulesTest() {
        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        client.listPolicyRules(policy.getId()).forEach({policyItem ->
            assertThat(policyItem, notNullValue())
            assertThat(policyItem.getId(), notNullValue())
            assertThat(policyItem, instanceOf(Policy.class))
        })
    }

    @Test (groups = "group2")
    void createPasswordPolicyRule() {

        def group = randomGroup()
        def policy = randomPasswordPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        PasswordPolicyRule policyRule = PasswordPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setType(PolicyRuleType.PASSWORD)
            .setSelfServiceUnlockAccess(PolicyAccess.DENY)
            .setSelfServicePasswordResetAccess(PolicyAccess.ALLOW)
            .setPasswordChangeAccess(PolicyAccess.ALLOW)
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
        .buildAndCreate(client, policy) as PasswordPolicyRule
        registerForCleanup(policyRule)

        assertThat policyRule.getName(), is(policyRuleName)
        assertThat policyRule.getActions().getPasswordChange().getAccess(), is(PolicyAccess.ALLOW)
        assertThat policyRule.getActions().getSelfServicePasswordReset().getAccess(), is(PolicyAccess.ALLOW)
        assertThat policyRule.getActions().getSelfServiceUnlock().getAccess(), is(PolicyAccess.DENY)
        assertThat policyRule.getConditions().getPeople().getUsers().getExclude(), hasSize(0)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkConnection.ANYWHERE)
    }

    @Test (groups = "group2")
    void createOktaSignOnOnPremPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setType(PolicyRuleType.SIGN_ON)
            .setAuthType(PolicyRuleAuthContextType.ANY)
            .setMaxSessionLifetimeMinutes(0)
            .setMaxSessionIdleMinutes(720)
            .setUsePersistentCookie(false)
            .setRememberDeviceByDefault(false)
            .setRequireFactor(false)
            .setAccess(PolicyAccess.ALLOW)
        .buildAndCreate(client, policy) as OktaSignOnPolicyRule
        registerForCleanup(policyRule)

        assertThat policyRule.getActions().getSignon().getAccess(), is(PolicyAccess.ALLOW)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(false)
        assertThat policyRule.getActions().getSignon().getRememberDeviceByDefault(), is(false)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionIdleMinutes(), is(720)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionLifetimeMinutes(), is(0)
        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextType.ANY)
    }

    // We do not support RADIUS rules in the Okta SoP anymore.
    // “Authenticates via RADIUS” has been deprecated for a long time and we have removed it completely in OIE.
    // https://oktainc.atlassian.net/browse/OKTA-431665?focusedCommentId=2166278
    @Test (groups = "group2", enabled = false)
    void createOktaSignOnRadiusPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextType.RADIUS)
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setMaxSessionIdleMinutes(720)
            .setMaxSessionLifetimeMinutes(0)
            .setUsePersistentCookie(false)
            .setRememberDeviceByDefault(false)
            .setRequireFactor(false)
            .setAccess(PolicyAccess.ALLOW)
            .buildAndCreate(client, policy) as OktaSignOnPolicyRule
        registerForCleanup(policyRule)

        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextType.RADIUS)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkConnection.ANYWHERE)
    }

    @Test (groups = "group2")
    void createOktaSignOnCloudPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextType.ANY)
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setUsePersistentCookie(false)
            .setMaxSessionIdleMinutes(720)
            .setMaxSessionLifetimeMinutes(0)
            .setAccess(PolicyAccess.ALLOW)
            .setRequireFactor(true)
            .setFactorPromptMode(OktaSignOnPolicyFactorPromptMode.ALWAYS)
            .setRememberDeviceByDefault(false)
        .buildAndCreate(client, policy) as OktaSignOnPolicyRule
        registerForCleanup(policyRule)

        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextType.ANY)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkConnection.ANYWHERE)
        assertThat policyRule.getConditions().getPeople().getUsers().getInclude(), nullValue()
        assertThat policyRule.getConditions().getPeople().getUsers().getExclude(), is(Collections.emptyList())
        assertThat policyRule.getConditions().getPeople().getGroups(), nullValue()
        assertThat policyRule.getActions().getSignon().getAccess(), is(PolicyAccess.ALLOW)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(true)
        assertThat policyRule.getActions().getSignon().getRememberDeviceByDefault(), is(false)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkConnection.ANYWHERE)
        assertThat policyRule.getActions().getSignon().getSession().getUsePersistentCookie(), is(false)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionLifetimeMinutes(), is(0)
        assertThat policyRule.getActions().getSignon().getSession().getMaxSessionIdleMinutes(), is(720)
    }

    @Test (groups = "bacon")
    void createOktaSignOnDenyPolicyRule() {

        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAuthType(PolicyRuleAuthContextType.ANY)
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setRequireFactor(false)
            .setAccess(PolicyAccess.DENY)
        .buildAndCreate(client, policy) as OktaSignOnPolicyRule

        registerForCleanup(policyRule)

        assertThat policyRule.getType(), is(PolicyRuleType.SIGN_ON)
        assertThat policyRule.getActions().getSignon().getAccess(), is(PolicyAccess.DENY)
        assertThat policyRule.getActions().getSignon().getRequireFactor(), is(false)
        assertThat policyRule.getConditions().getAuthContext().getAuthType(), is(PolicyRuleAuthContextType.ANY)
        assertThat policyRule.getConditions().getNetwork().getConnection(), is(PolicyNetworkConnection.ANYWHERE)
    }

    @Test
    void testActivate(){
        def group = randomGroup()
        def policy = randomSignOnPolicy(group.getId())

        def policyRuleName = "java-sdk-it-" + UUID.randomUUID().toString()
        def policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
            .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
            .setRequireFactor(false)
            .setStatus(PolicyRule.StatusEnum.INACTIVE)
            .buildAndCreate(client, policy)
        registerForCleanup(policyRule)

        // policy rule is ACTIVE by default
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.ACTIVE))

        // deactivate
        policyRule.deactivate()
        policyRule = policy.getPolicyRule(policyRule.getId())
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.INACTIVE))

        policyRule.activate()
        policyRule = policy.getPolicyRule(policyRule.getId())
        assertThat(policyRule.getStatus(), is(PolicyRule.StatusEnum.ACTIVE))
    }
}

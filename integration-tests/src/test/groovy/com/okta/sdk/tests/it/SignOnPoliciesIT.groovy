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
import com.okta.sdk.resource.policy.*
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class SignOnPoliciesIT implements CrudTestSupport {

    @Override
    def create(Client client) {
        Policy policy = client.createPolicy(client.instantiate(OktaSignOnPolicy)
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - signOn CRUD"))

        assertThat policy.getStatus(), is(Policy.StatusEnum.ACTIVE)
        return policy
    }

    @Override
    def read(Client client, String id) {
        return client.getPolicy(id)
    }

    @Override
    void update(Client client, def policy) {
        policy.setDescription("IT created Policy - Updated")
        policy.update()
    }

    @Override
    void assertUpdate(Client client, def policy) {
        assertThat policy.description, is("IT created Policy - Updated")
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listPolicies(PolicyType.OKTA_SIGN_ON.toString()).iterator()
    }

    @Test
    void signOnPolicyWithGroupConditions() {

        def group = GroupBuilder.instance()
                .setName("group-" + UUID.randomUUID().toString())
                .buildAndCreate(client)
        registerForCleanup(group)

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
                .setName("policy+" + UUID.randomUUID().toString())
                .setStatus(Policy.StatusEnum.ACTIVE)
                .setDescription("IT created Policy - signOnPolicyWithGroupConditions")
                .setType(PolicyType.OKTA_SIGN_ON)
                .addGroup(group.getId())
        .buildAndCreate(client)

        registerForCleanup(policy)

        assertThat policy.getId(), notNullValue()
        assertThat policy.getConditions().getPeople().getGroups().getInclude(), is(Collections.singletonList(group.getId()))
        assertThat policy.getConditions().getPeople().getGroups().getExclude(), nullValue()
    }

    @Test
    void signOnActionsTest() {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - signOnActionsTest")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(Policy.StatusEnum.ACTIVE)
            .buildAndCreate(client);

        registerForCleanup(policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
        OktaSignOnPolicyRule policyRule = policy.createRule(client.instantiate(OktaSignOnPolicyRule)
            .setName(policyRuleName)
            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
                    .setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.DENY)
                    .setRequireFactor(false))))
        registerForCleanup(policyRule)

        assertThat(policyRule.getId(), notNullValue())
        assertThat(policyRule.name, is(policyRuleName))
    }

    @Test
    void activateDeactivateTest() {

        def policy = OktaSignOnPolicyBuilder.instance()
                .setName("policy+" + UUID.randomUUID().toString())
                .setDescription("IT created Policy - activateDeactivateTest")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(Policy.StatusEnum.INACTIVE)
        .buildAndCreate(client)

        registerForCleanup(policy)

        assertThat(policy.getStatus(), is(Policy.StatusEnum.INACTIVE))

        // activate
        policy.activate()
        policy = client.getPolicy(policy.getId())
        assertThat(policy.getStatus(), is(Policy.StatusEnum.ACTIVE))

        // deactivate
        policy.deactivate()
        policy = client.getPolicy(policy.getId())
        assertThat(policy.getStatus(), is(Policy.StatusEnum.INACTIVE))
    }

    @Test
    void expandTest() {
        def resource = create(client)
        registerForCleanup(resource)

        // verify a regular get does NOT return the embedded map with "rules"
        assertRulesNotExpanded(client.getPolicy(resource.getId()))

        // verify a regular get DOES return the embedded map with "rules"
        assertRulesExpanded(client.getPolicy(resource.getId(), "rules"))
    }

    @Test
    void listPoliciesWithParams() {
        def resource = create(client)
        registerForCleanup(resource)

        def policies = client.listPolicies(PolicyType.OKTA_SIGN_ON.toString())
        assertThat policies, not(empty())
        policies.stream()
                .limit(5)
                .forEach { assertRulesNotExpanded(it) }

        policies = client.listPolicies(PolicyType.OKTA_SIGN_ON.toString(), Policy.StatusEnum.ACTIVE.toString(), "rules")
        assertThat policies, not(empty())
        policies.stream()
                .limit(5)
                .forEach { assertRulesExpanded(it) }
    }

    static void assertRulesNotExpanded(Policy policy) {
        assertThat policy.getEmbedded(), anyOf(nullValue(), not(hasKey("rules")))
    }

    static void assertRulesExpanded(Policy policy) {
        assertThat policy.getEmbedded(), allOf(notNullValue(), hasKey("rules"))
    }
}
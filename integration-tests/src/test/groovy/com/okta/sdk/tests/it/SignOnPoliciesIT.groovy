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
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.application.OAuthGrantType
import com.okta.sdk.resource.application.OAuthResponseType
import com.okta.sdk.resource.application.OIDCApplicationBuilder
import com.okta.sdk.resource.application.OpenIdConnectApplication
import com.okta.sdk.resource.application.OpenIdConnectApplicationType
import com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy
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

    @Test (groups = "group2")
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

    // disable running them in bacon
    @Test (groups = "bacon")
    void createProfileEnrollmentPolicy() {

        Policy profileEnrollmentPolicy = client.createPolicy(client.instantiate(ProfileEnrollmentPolicy)
            .setName("policy+" + UUID.randomUUID().toString())
            .setType(PolicyType.PROFILE_ENROLLMENT)
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setDescription("IT created Policy - createProfileEnrollmentPolicy"))
        registerForCleanup(profileEnrollmentPolicy)

        assertThat profileEnrollmentPolicy, notNullValue()
        assertThat profileEnrollmentPolicy.getName(), notNullValue()
        assertThat profileEnrollmentPolicy.getType(), is(PolicyType.PROFILE_ENROLLMENT)
        assertThat profileEnrollmentPolicy.getStatus(), is(Policy.StatusEnum.ACTIVE)
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createAccessPolicyRule() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Application oidcApp = OIDCApplicationBuilder.instance()
            .setName(name)
            .setLabel(name)
            .addRedirectUris("http://www.example.com")
            .setPostLogoutRedirectUris(Collections.singletonList("http://www.example.com/logout"))
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("http://www.myapp.com")
            .setErrorRedirectUrl("http://www.myapp.com/error")
            .buildAndCreate(client)
        registerForCleanup(oidcApp)

        assertThat(oidcApp, notNullValue())
        assertThat(oidcApp.getLinks(), notNullValue())
        assertThat(oidcApp.getLinks().get("accessPolicy"), notNullValue())

        // accessPolicy:[href:https://example.com/api/v1/policies/rst412ay22NkOdJJr0g7]
        String accessPolicyId = oidcApp.getLinks().get("accessPolicy").toString().replaceAll("]", "").tokenize("/")[-1]

        Policy accessPolicy = client.getPolicy(accessPolicyId)
        assertThat(accessPolicy, notNullValue())

        AccessPolicyRule accessPolicyRule = client.instantiate(AccessPolicyRule)
            .setName(name)
            .setActions(client.instantiate(AccessPolicyRuleActions)
               .setAppSignOn(client.instantiate(AccessPolicyRuleApplicationSignOn)
                  .setAccess("DENY")
                  .setVerificationMethod(client.instantiate(VerificationMethod)
                     .setType("ASSURANCE")
                     .setFactorMode("1FA")
                     .setReauthenticateIn("PT43800H"))))

        PolicyRule createdAccessPolicyRule = accessPolicy.createRule(accessPolicyRule)
        registerForCleanup(createdAccessPolicyRule)

        assertThat(createdAccessPolicyRule, notNullValue())
        assertThat(createdAccessPolicyRule.getName(), is(name))

        AccessPolicyRuleActions accessPolicyRuleActions = createdAccessPolicyRule.getActions() as AccessPolicyRuleActions

        assertThat(accessPolicyRuleActions.getAppSignOn().getAccess(), is("DENY"))
        assertThat(accessPolicyRuleActions.getAppSignOn().getVerificationMethod().getType(), is("ASSURANCE"))
        assertThat(accessPolicyRuleActions.getAppSignOn().getVerificationMethod().getFactorMode(), is("1FA"))
        assertThat(accessPolicyRuleActions.getAppSignOn().getVerificationMethod().getReauthenticateIn(), is("PT43800H"))
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createProfileEnrollmentPolicyRule() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy profileEnrollmentPolicy = client.instantiate(Policy)
            .setName(name)
            .setType(PolicyType.PROFILE_ENROLLMENT)
            .setStatus(Policy.StatusEnum.ACTIVE)
            .setDescription("IT created Policy - createProfileEnrollmentPolicyRule")

        Policy createdProfileEnrollmentPolicy = client.createPolicy(profileEnrollmentPolicy)
        assertThat(createdProfileEnrollmentPolicy, notNullValue())

        PolicyRuleList defaultPolicyRuleList = createdProfileEnrollmentPolicy.listPolicyRules()
        assertThat(defaultPolicyRuleList.size(), greaterThanOrEqualTo(1))

        PolicyRule defaultPolicyRule = defaultPolicyRuleList.first()

        ProfileEnrollmentPolicyRuleProfileAttribute policyRuleProfileAttribute =
            client.instantiate(ProfileEnrollmentPolicyRuleProfileAttribute)
                .setName("email")
                .setLabel("Email")
                .setRequired(true)

        ProfileEnrollmentPolicyRuleActions profileEnrollmentPolicyRuleActions =
            defaultPolicyRule.getActions() as ProfileEnrollmentPolicyRuleActions
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setAccess("ALLOW")
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setPreRegistrationInlineHooks(null)
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setUnknownUserAction("DENY")
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setTargetGroupIds(null)
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setProfileAttributes([policyRuleProfileAttribute])
        profileEnrollmentPolicyRuleActions.getProfileEnrollment().setActivationRequirements(
            client.instantiate(ProfileEnrollmentPolicyRuleActivationRequirement)
                .setEmailVerification(true))

        defaultPolicyRule.setActions(profileEnrollmentPolicyRuleActions)

        PolicyRule updatePolicyRule = defaultPolicyRule.update()
        assertThat(updatePolicyRule, notNullValue())
        assertThat(updatePolicyRule.getName(), is(defaultPolicyRule.getName()))
        ProfileEnrollmentPolicyRuleActions updateProfileEnrollmentPolicyRuleActions =
            updatePolicyRule.getActions() as ProfileEnrollmentPolicyRuleActions
        assertThat(updateProfileEnrollmentPolicyRuleActions, notNullValue())
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment(), notNullValue())
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getAccess(), is("ALLOW"))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getPreRegistrationInlineHooks(), nullValue())
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getUnknownUserAction(), is("DENY"))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getTargetGroupIds(), nullValue())
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getActivationRequirements().getEmailVerification(), is(true))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getProfileAttributes(), hasSize(1))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getProfileAttributes().first().getName(), is("email"))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getProfileAttributes().first().getLabel(), is("Email"))
        assertThat(updateProfileEnrollmentPolicyRuleActions.getProfileEnrollment().getProfileAttributes().first().getRequired(), is(true))
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

    static void assertRulesNotExpanded(AuthorizationServerPolicy policy) {
        assertThat policy.getEmbedded(), anyOf(nullValue(), not(hasKey("rules")))
    }

    static void assertRulesExpanded(Policy policy) {
        assertThat policy.getEmbedded(), allOf(notNullValue(), hasKey("rules"))
    }

    static void assertRulesExpanded(AuthorizationServerPolicy policy) {
        assertThat policy.getEmbedded(), allOf(notNullValue(), hasKey("rules"))
    }
}

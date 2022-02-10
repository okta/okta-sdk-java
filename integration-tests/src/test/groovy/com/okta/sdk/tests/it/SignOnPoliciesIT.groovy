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
import com.okta.sdk.resource.AccessPolicyRule
import com.okta.sdk.resource.AccessPolicyRuleActions
import com.okta.sdk.resource.AccessPolicyRuleApplicationSignOn
import com.okta.sdk.resource.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.OAuthGrantType
import com.okta.sdk.resource.OAuthResponseType
import com.okta.sdk.resource.OktaSignOnPolicy
import com.okta.sdk.resource.OktaSignOnPolicyRule
import com.okta.sdk.resource.OpenIdConnectApplicationType
import com.okta.sdk.resource.ProfileEnrollmentPolicy
import com.okta.sdk.resource.ProfileEnrollmentPolicyRuleActions
import com.okta.sdk.resource.ProfileEnrollmentPolicyRuleActivationRequirement
import com.okta.sdk.resource.ProfileEnrollmentPolicyRuleProfileAttribute
import com.okta.sdk.resource.VerificationMethod
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.ApplicationSignOnMode
import com.okta.sdk.resource.application.OIDCApplicationBuilder
import com.okta.sdk.resource.authorization.server.PolicyAccess
import com.okta.sdk.resource.authorization.server.PolicyNetworkConnection
import com.okta.sdk.resource.authorization.server.PolicyType
import com.okta.sdk.resource.common.Policy
import com.okta.sdk.resource.authorization.server.LifecycleStatus
import com.okta.sdk.resource.common.PolicyList
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.resource.policy.PolicyRule
import com.okta.sdk.resource.policy.PolicyRuleList
import com.okta.sdk.resource.policy.rule.SignOnPolicyRuleBuilder
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class SignOnPoliciesIT implements CrudTestSupport {

    @Override
    def create(Client client) {
        OktaSignOnPolicy policy = client.createPolicy(client.instantiate(OktaSignOnPolicy)
            .setName("policy+" + UUID.randomUUID().toString())
            .setStatus(LifecycleStatus.ACTIVE)
            .setType(PolicyType.OKTA_SIGN_ON)
            .setDescription("IT created Policy - signOn CRUD")) as OktaSignOnPolicy

        assertThat policy.getStatus(), is(LifecycleStatus.ACTIVE)
        return policy
    }

    @Override
    def read(Client client, String id) {
        return client.getPolicy(id)
    }

    @Override
    void update(Client client, def policy) {
        policy.setDescription("IT created Policy - Updated")
        client.updatePolicy(policy, policy.id)
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
                .setStatus(LifecycleStatus.ACTIVE)
                .setDescription("IT created Policy - signOnPolicyWithGroupConditions")
                .setType(PolicyType.OKTA_SIGN_ON)
                .addGroup(group.getId())
        .buildAndCreate(client) as OktaSignOnPolicy

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
            .setStatus(LifecycleStatus.ACTIVE)
            .setDescription("IT created Policy - createProfileEnrollmentPolicy"))
        registerForCleanup(profileEnrollmentPolicy)

        assertThat profileEnrollmentPolicy, notNullValue()
        assertThat profileEnrollmentPolicy.getName(), notNullValue()
        assertThat profileEnrollmentPolicy.getType(), is(PolicyType.PROFILE_ENROLLMENT)
        assertThat profileEnrollmentPolicy.getStatus(), is(LifecycleStatus.ACTIVE)
    }

    // disable running them in bacon
    @Test (groups = "bacon", enabled = false)
    void createAccessPolicyRule() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Application oidcApp = OIDCApplicationBuilder.instance()
            .setName(name)
            .setLabel(name)
            .addRedirectUris("https://www.example.com")
            .setPostLogoutRedirectUris(Collections.singletonList("https://www.example.com/logout"))
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("https://www.myapp.com")
            .setErrorRedirectUrl("https://www.myapp.com/error")
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

        PolicyRule createdAccessPolicyRule = client.createPolicyRule(accessPolicyRule, accessPolicyId)
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
    @Test (groups = "bacon", enabled = false)
    void createProfileEnrollmentPolicyRule() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy profileEnrollmentPolicy = client.instantiate(Policy)
            .setName(name)
            .setType(PolicyType.PROFILE_ENROLLMENT)
            .setStatus(LifecycleStatus.ACTIVE)
            .setDescription("IT created Policy - createProfileEnrollmentPolicyRule")

        Policy createdProfileEnrollmentPolicy = client.createPolicy(profileEnrollmentPolicy)
        assertThat(createdProfileEnrollmentPolicy, notNullValue())

        PolicyRuleList defaultPolicyRuleList = client.listPolicyRules(createdProfileEnrollmentPolicy.getId())
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

        PolicyRule updatePolicyRule = client.updatePolicyRule(defaultPolicyRule, createdProfileEnrollmentPolicy.getId(), defaultPolicyRule.getId())

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
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(client) as OktaSignOnPolicy

        registerForCleanup(policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()
//        OktaSignOnPolicyRule policyRule = client.createPolicyRule(client.instantiate(OktaSignOnPolicyRule)
//            .setName(policyRuleName)
//            .setActions(client.instantiate(OktaSignOnPolicyRuleActions)
//                .setSignon(client.instantiate(OktaSignOnPolicyRuleSignonActions)
//                    .setAccess(PolicyAccess.DENY)
//                    .setRequireFactor(false))), policy.getId()) as OktaSignOnPolicyRule
        OktaSignOnPolicyRule policyRule = SignOnPolicyRuleBuilder.instance()
            .setName(policyRuleName)
//            .setAuthType(PolicyRuleAuthContextType.RADIUS)
            .setNetworkConnection(PolicyNetworkConnection.ANYWHERE)
            .setMaxSessionIdleMinutes(720)
            .setMaxSessionLifetimeMinutes(0)
            .setUsePersistentCookie(false)
            .setRememberDeviceByDefault(false)
            .setRequireFactor(false)
            .setAccess(PolicyAccess.ALLOW)
            .buildAndCreate(client, policy) as OktaSignOnPolicyRule
        registerForCleanup(policyRule)

        assertThat(policyRule.getId(), notNullValue())
        assertThat(policyRule.name, is(policyRuleName))
    }

    @Test
    void activateDeactivateTest() {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
                .setName("policy+" + UUID.randomUUID().toString())
                .setDescription("IT created Policy - activateDeactivateTest")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.INACTIVE)
        .buildAndCreate(client) as OktaSignOnPolicy

        registerForCleanup(policy)

        assertThat(policy.getStatus(), is(LifecycleStatus.INACTIVE))

        // activate
        client.activatePolicy(policy.getId())
        policy = client.getPolicy(policy.getId()) as OktaSignOnPolicy
        assertThat(policy.getStatus(), is(LifecycleStatus.ACTIVE))

        // deactivate
        client.deactivatePolicy(policy.getId())
        policy = client.getPolicy(policy.getId()) as OktaSignOnPolicy
        assertThat(policy.getStatus(), is(LifecycleStatus.INACTIVE))
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
        OktaSignOnPolicy resource = create(client)
        registerForCleanup(resource)

        PolicyList policies = client.listPolicies(PolicyType.OKTA_SIGN_ON.toString())
        assertThat policies, not(empty())
        policies.stream()
                .limit(5)
                .forEach { assertRulesNotExpanded(it) }

        policies = client.listPolicies(PolicyType.OKTA_SIGN_ON.toString(), LifecycleStatus.ACTIVE.toString(), "rules")
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

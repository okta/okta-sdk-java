/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.okta.sdk.helper.PolicyApiHelper
import com.okta.sdk.resource.application.OIDCApplicationBuilder
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.resource.policy.PasswordPolicyBuilder
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.api.ApplicationApi
import org.openapitools.client.api.GroupApi
import org.openapitools.client.api.PolicyApi
import org.openapitools.client.model.*
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
/**
 * Tests for {@code /api/v1/policies}.
 * @since 0.5.0
 */
class PoliciesIT extends ITSupport {

    @Test (groups = "group2")
    void signOnPolicyWithGroupConditions() {

        GroupApi groupApi = new GroupApi(getClient())
        PolicyApi policyApi = new PolicyApi(getClient())

        def group = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(groupApi)
        registerForCleanup(group)

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setStatus(LifecycleStatus.ACTIVE)
            .setDescription("IT created Policy - signOnPolicyWithGroupConditions")
            .setType(PolicyType.OKTA_SIGN_ON)
            .addGroup(group.getId())
            .buildAndCreate(policyApi)

        registerForCleanup(policy)

        assertThat policy.getId(), notNullValue()
        assertThat policy.getConditions().getPeople().getGroups().getInclude(), is(Collections.singletonList(group.getId()))
        assertThat policy.getConditions().getPeople().getGroups().getExclude(), nullValue()
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createProfileEnrollmentPolicy() {

        PolicyApi policyApi = new PolicyApi(getClient())

        ProfileEnrollmentPolicy profileEnrollmentPolicy = new ProfileEnrollmentPolicy()
        profileEnrollmentPolicy.name("policy+" + UUID.randomUUID().toString())
            .type(PolicyType.PROFILE_ENROLLMENT)
            .status(LifecycleStatus.ACTIVE)
            .description("IT created Policy - createProfileEnrollmentPolicy")

        Policy createdProfileEnrollmentPolicy =
            policyApi.createPolicy(ProfileEnrollmentPolicy.class, profileEnrollmentPolicy, false)

        registerForCleanup(createdProfileEnrollmentPolicy)

        assertThat createdProfileEnrollmentPolicy, notNullValue()
        assertThat createdProfileEnrollmentPolicy.getName(), notNullValue()
        assertThat createdProfileEnrollmentPolicy.getType(), equalTo(PolicyType.PROFILE_ENROLLMENT)
        assertThat createdProfileEnrollmentPolicy.getStatus(), equalTo(LifecycleStatus.ACTIVE)
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createAccessPolicyRule() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        ApplicationApi applicationApi = new ApplicationApi(getClient())
        PolicyApi policyApi = new PolicyApi(getClient())

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
            .buildAndCreate(applicationApi)
        registerForCleanup(oidcApp)

        assertThat(oidcApp, notNullValue())
        assertThat(oidcApp.getLinks(), notNullValue())
        assertThat(oidcApp.getLinks().getAccessPolicy(), notNullValue())

        // accessPolicy:[href:https://example.com/api/v1/policies/rst412ay22NkOdJJr0g7]
        String accessPolicyId = oidcApp.getLinks().getAccessPolicy().getHref().replaceAll("]", "").tokenize("/")[-1]

        Policy accessPolicy = policyApi.getPolicy(accessPolicyId, null)
        assertThat(accessPolicy, notNullValue())

        AccessPolicyRule accessPolicyRule = new AccessPolicyRule()
        accessPolicyRule.name(name)
        accessPolicyRule.setType(PolicyRuleType.ACCESS_POLICY)

        AccessPolicyRuleActions accessPolicyRuleActions = new AccessPolicyRuleActions()
        AccessPolicyRuleApplicationSignOn accessPolicyRuleApplicationSignOn = new AccessPolicyRuleApplicationSignOn()
        accessPolicyRuleApplicationSignOn.access("DENY")
        VerificationMethod verificationMethod = new VerificationMethod()
        verificationMethod.type("ASSURANCE")
            .factorMode("1FA")
            .reauthenticateIn("PT43800H")
        accessPolicyRuleApplicationSignOn.verificationMethod(verificationMethod)
        accessPolicyRuleActions.appSignOn(accessPolicyRuleApplicationSignOn)
        accessPolicyRule.actions(accessPolicyRuleActions)

        AccessPolicyRule createdAccessPolicyRule = policyApi.createPolicyRule(AccessPolicyRule.class, accessPolicy.getId(), accessPolicyRule)
        //registerForCleanup(createdAccessPolicyRule)

        assertThat(createdAccessPolicyRule, notNullValue())
        assertThat(createdAccessPolicyRule.getName(), is(name))

        AccessPolicyRuleActions createdAccessPolicyRuleActions = createdAccessPolicyRule.getActions()

        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getAccess(), is("DENY"))
        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getType(), is("ASSURANCE"))
        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getFactorMode(), is("1FA"))
        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getReauthenticateIn(), is("PT43800H"))

        policyApi.deactivatePolicyRule(accessPolicy.getId(), createdAccessPolicyRule.getId())
        policyApi.deletePolicyRule(accessPolicy.getId(), createdAccessPolicyRule.getId())
    }

    @Test
    void signOnActionsTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - signOnActionsTest")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()

        OktaSignOnPolicyRule oktaSignOnPolicyRule = new OktaSignOnPolicyRule()
        oktaSignOnPolicyRule.name(policyRuleName)
        oktaSignOnPolicyRule.type(PolicyRuleType.SIGN_ON)
        OktaSignOnPolicyRuleActions oktaSignOnPolicyRuleActions = new OktaSignOnPolicyRuleActions()
        OktaSignOnPolicyRuleSignonActions oktaSignOnPolicyRuleSignonActions = new OktaSignOnPolicyRuleSignonActions()
        oktaSignOnPolicyRuleSignonActions.setAccess(PolicyAccess.DENY)
        oktaSignOnPolicyRuleSignonActions.setRequireFactor(false)
        oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions)
        oktaSignOnPolicyRule.actions(oktaSignOnPolicyRuleActions)
        //registerForCleanup(policyRule)

        OktaSignOnPolicyRule createdPolicyRule = policyApi.createPolicyRule(OktaSignOnPolicyRule, policy.getId(), oktaSignOnPolicyRule)

        assertThat(createdPolicyRule.getId(), notNullValue())
        assertThat(createdPolicyRule.name, is(policyRuleName))

        policyApi.deactivatePolicyRule(policy.getId(), createdPolicyRule.getId())
        policyApi.deletePolicyRule(policy.getId(), createdPolicyRule.getId())
    }

    @Test
    void activateDeactivateTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - activateDeactivateTest")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.INACTIVE)
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        assertThat(policy.getStatus(), is(LifecycleStatus.INACTIVE))

        // activate
        policyApi.activatePolicy(policy.getId())

        policy = policyApi.getPolicy(policy.getId(), null)

        assertThat(policy.getStatus(), is(LifecycleStatus.ACTIVE))

        // deactivate
        policyApi.deactivatePolicy(policy.getId())

        policy = policyApi.getPolicy(policy.getId(), null)

        assertThat(policy.getStatus(), is(LifecycleStatus.INACTIVE))
    }

    @Test
    @NonOIEEnvironmentOnly
    void expandTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - expandTest")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.INACTIVE)
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        // verify a regular get does NOT return the embedded map with "rules"
        assertRulesNotExpanded(policyApi.getPolicy(policy.getId(), null))

        // verify a regular get DOES return the embedded map with "rules"
        assertRulesExpanded(policyApi.getPolicy(policy.getId(), "rules"))
    }

    @Test
    @NonOIEEnvironmentOnly
    void listPoliciesWithParams() {

        PolicyApi policyApi = new PolicyApi(getClient())

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - listPoliciesWithParams")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.INACTIVE)
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        def policies= policyApi.listPolicies(PolicyType.OKTA_SIGN_ON.name(), LifecycleStatus.INACTIVE.name(), null)

        assertThat policies, not(empty())
        policies.stream()
            .limit(5)
            .forEach { assertRulesNotExpanded(it) }

        policies = policyApi.listPolicies(PolicyType.OKTA_SIGN_ON.name(), LifecycleStatus.ACTIVE.name(), "rules")

        assertThat policies, not(empty())
        policies.stream()
            .limit(5)
            .forEach { assertRulesExpanded(it) }
    }

    @Test
    void testPolicyApiHelper() {

        PolicyApi policyApi = new PolicyApi(getClient())
        GroupApi groupApi = new GroupApi(getClient())

        def group = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(groupApi)
        registerForCleanup(group)

        PasswordPolicy policy = PasswordPolicyBuilder.instance()
            .setAuthProvider(PasswordPolicyAuthenticationProviderType.OKTA)
            .setExcludePasswordDictionary(false)
            .setExcludeUserNameInPassword(false)
            .setMinPasswordLength(8)
            .setMinLowerCase(1)
            .setMinUpperCase(1)
            .setMinNumbers(1)
            .setMinSymbols(1)
            .addGroup(group.getId())
            .setSkipUnlock(false)
            .setPasswordExpireWarnDays(85)
            .setPasswordHistoryCount(5)
            .setPasswordMaxAgeDays(90)
            .setPasswordMinMinutes(2)
            .setPasswordAutoUnlockMinutes(5)
            .setPasswordMaxAttempts(3)
            .setShowLockoutFailures(true)
            .setType(PolicyType.PASSWORD)
            .setStatus(LifecycleStatus.ACTIVE)
            .setPriority(1)
            .setDescription("Dummy policy for sdk test")
            .setName("SDK policy "+ UUID.randomUUID().toString())
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        // get policy (sub-typed)
        PasswordPolicy passwordPolicy = PolicyApiHelper.getPolicy(getClient(), policy.getId(), null)
        assertThat(passwordPolicy, notNullValue())
        assertThat(passwordPolicy.getType(), is(PolicyType.PASSWORD))
        assertThat(passwordPolicy.getStatus(), is(LifecycleStatus.ACTIVE))

        // list policies
        def policies= PolicyApiHelper.listPolicies(getClient(), PolicyType.PASSWORD.name(), LifecycleStatus.ACTIVE.name(), null)
        assertThat policies, not(empty())
    }

    static void assertRulesNotExpanded(Policy policy) {
        assertThat policy.getEmbedded(), anyOf(nullValue(), not(hasKey("rules")))
    }

    static void assertRulesExpanded(Policy policy) {
        assertThat policy.getEmbedded(), allOf(notNullValue(), hasKey("rules"))
    }

    @Test (groups = "group2")
    void listPolicyRulesTest() {

        Group group = randomGroup()
        Policy policy = randomSignOnPolicy(group.getId())

        PolicyApi policyApi = new PolicyApi(getClient())

        policyApi.listPolicyRules(policy.getId()).forEach({policyItem ->
            assertThat(policyItem, notNullValue())
            assertThat(policyItem.getId(), notNullValue())
            assertThat(policyItem, instanceOf(Policy.class))
        })
    }
}
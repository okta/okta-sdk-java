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

import com.okta.sdk.resource.application.OIDCApplicationBuilder
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.api.ApplicationApi
import org.openapitools.client.api.GroupApi
import org.openapitools.client.api.PolicyApi
import org.openapitools.client.api.UserApi
import org.openapitools.client.model.AccessPolicyRule
import org.openapitools.client.model.AccessPolicyRuleActions
import org.openapitools.client.model.AccessPolicyRuleApplicationSignOn
import org.openapitools.client.model.Application
import org.openapitools.client.model.AuthorizationServerPolicy
import org.openapitools.client.model.Group
import org.openapitools.client.model.OktaSignOnPolicyRule
import org.openapitools.client.model.OktaSignOnPolicyRuleActions
import org.openapitools.client.model.OktaSignOnPolicyRuleSignonActions
import org.openapitools.client.model.OpenIdConnectApplication
import org.openapitools.client.model.OpenIdConnectApplicationSettingsClient
import org.openapitools.client.model.Policy
import org.openapitools.client.model.OktaSignOnPolicy
import org.openapitools.client.model.PolicyRule
import org.openapitools.client.model.ProfileEnrollmentPolicy
import org.openapitools.client.model.ProfileEnrollmentPolicyRule
import org.openapitools.client.model.ProfileEnrollmentPolicyRuleActions
import org.openapitools.client.model.ProfileEnrollmentPolicyRuleActivationRequirement
import org.openapitools.client.model.ProfileEnrollmentPolicyRuleProfileAttribute
import org.openapitools.client.model.VerificationMethod
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.anyOf
import static org.hamcrest.Matchers.anyOf
import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThanOrEqualTo
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue

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
            .setStatus("ACTIVE")
            .setDescription("IT created Policy - signOnPolicyWithGroupConditions")
            .setType("OKTA_SIGN_ON")
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
            .type("PROFILE_ENROLLMENT")
            .status("ACTIVE")
            .description("IT created Policy - createProfileEnrollmentPolicy")

        Policy createdProfileEnrollmentPolicy =
            policyApi.createPolicy(ProfileEnrollmentPolicy.class, profileEnrollmentPolicy, false)

        registerForCleanup(createdProfileEnrollmentPolicy)

        assertThat createdProfileEnrollmentPolicy, notNullValue()
        assertThat createdProfileEnrollmentPolicy.getName(), notNullValue()
        assertThat createdProfileEnrollmentPolicy.getType(), equalTo("PROFILE_ENROLLMENT")
        assertThat createdProfileEnrollmentPolicy.getStatus(), equalTo("ACTIVE")
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
            .setResponseTypes(Arrays.asList("token", "code"))
            .setGrantTypes(Arrays.asList("implicit", "authorization_code"))
            .setApplicationType("native")
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod("none")
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("http://www.myapp.com")
            .setErrorRedirectUrl("http://www.myapp.com/error")
            .buildAndCreate(applicationApi)
        registerForCleanup(oidcApp)

        assertThat(oidcApp, notNullValue())
        assertThat(oidcApp.getLinks(), notNullValue())
        assertThat(oidcApp.getLinks().get("accessPolicy"), notNullValue())

        // accessPolicy:[href:https://example.com/api/v1/policies/rst412ay22NkOdJJr0g7]
        String accessPolicyId = oidcApp.getLinks().get("accessPolicy").toString().replaceAll("]", "").tokenize("/")[-1]

        Policy accessPolicy = policyApi.getPolicy(accessPolicyId, null)
        assertThat(accessPolicy, notNullValue())

        AccessPolicyRule accessPolicyRule = new AccessPolicyRule()
        accessPolicyRule.name(name)
        accessPolicyRule.setType("ACCESS_POLICY")
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
    }

    @Test
    void signOnActionsTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - signOnActionsTest")
            .setType("OKTA_SIGN_ON")
            .setStatus("ACTIVE")
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()

        OktaSignOnPolicyRule oktaSignOnPolicyRule = new OktaSignOnPolicyRule()
        oktaSignOnPolicyRule.name(policyRuleName)
        oktaSignOnPolicyRule.type("SIGN_ON")
        OktaSignOnPolicyRuleActions oktaSignOnPolicyRuleActions = new OktaSignOnPolicyRuleActions()
        OktaSignOnPolicyRuleSignonActions oktaSignOnPolicyRuleSignonActions = new OktaSignOnPolicyRuleSignonActions()
        oktaSignOnPolicyRuleSignonActions.setAccess("DENY")
        oktaSignOnPolicyRuleSignonActions.setRequireFactor(false)
        oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignonActions)
        oktaSignOnPolicyRule.actions(oktaSignOnPolicyRuleActions)
        //registerForCleanup(policyRule)

        OktaSignOnPolicyRule createdPolicyRule = policyApi.createPolicyRule(OktaSignOnPolicyRule, policy.getId(), oktaSignOnPolicyRule)

        assertThat(createdPolicyRule.getId(), notNullValue())
        assertThat(createdPolicyRule.name, is(policyRuleName))
    }

    @Test
    void activateDeactivateTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - activateDeactivateTest")
            .setType("OKTA_SIGN_ON")
            .setStatus("INACTIVE")
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        assertThat(policy.getStatus(), is("INACTIVE"))

        // activate
        policyApi.activatePolicy(policy.getId())

        policy = policyApi.getPolicy(policy.getId(), null)

        assertThat(policy.getStatus(), is("ACTIVE"))

        // deactivate
        policyApi.deactivatePolicy(policy.getId())

        policy = policyApi.getPolicy(policy.getId(), null)

        assertThat(policy.getStatus(), is("INACTIVE"))
    }

    @Test
    @NonOIEEnvironmentOnly
    void expandTest() {

        PolicyApi policyApi = new PolicyApi(getClient())

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - expandTest")
            .setType("OKTA_SIGN_ON")
            .setStatus("INACTIVE")
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
            .setType("OKTA_SIGN_ON")
            .setStatus("INACTIVE")
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        def policies= policyApi.listPolicies("OKTA_SIGN_ON", "INACTIVE", null)

        assertThat policies, not(empty())
        policies.stream()
            .limit(5)
            .forEach { assertRulesNotExpanded(it) }

        policies = policyApi.listPolicies("OKTA_SIGN_ON", "ACTIVE", "rules")

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

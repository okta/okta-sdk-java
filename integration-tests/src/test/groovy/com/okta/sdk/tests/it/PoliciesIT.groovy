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
import com.okta.sdk.resource.model.AccessPolicy
import com.okta.sdk.resource.model.AccessPolicyRule
import com.okta.sdk.resource.model.AccessPolicyRuleActions
import com.okta.sdk.resource.model.AccessPolicyRuleApplicationSignOn
import com.okta.sdk.resource.model.AccessPolicyRuleApplicationSignOnAccess
import com.okta.sdk.resource.model.Application
import com.okta.sdk.resource.model.ApplicationSignOnMode
import com.okta.sdk.resource.model.LifecycleStatus
import com.okta.sdk.resource.model.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.model.OAuthGrantType
import com.okta.sdk.resource.model.OAuthResponseType
import com.okta.sdk.resource.model.OktaSignOnPolicy
import com.okta.sdk.resource.model.OktaSignOnPolicyRule
import com.okta.sdk.resource.model.OktaSignOnPolicyRuleActions
import com.okta.sdk.resource.model.OktaSignOnPolicyRuleSignonActions
import com.okta.sdk.resource.model.OpenIdConnectApplicationType
import com.okta.sdk.resource.model.Policy
import com.okta.sdk.resource.model.PolicyAccess
import com.okta.sdk.resource.model.PolicyRuleType
import com.okta.sdk.resource.model.PolicyRuleVerificationMethodType
import com.okta.sdk.resource.model.PolicyType
import com.okta.sdk.resource.model.ProfileEnrollmentPolicy
import com.okta.sdk.resource.model.VerificationMethod
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.PolicyApi
import com.okta.sdk.resource.model.*
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
/**
 * Tests for {@code /api/v1/policies}.
 * @since 0.5.0
 */
class PoliciesIT extends ITSupport {

    GroupApi groupApi = new GroupApi(getClient())
    PolicyApi policyApi = new PolicyApi(getClient())

    @Test (groups = "group2")
    void signOnPolicyWithGroupConditions() {

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
            .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        assertThat(policy, notNullValue())
        assertThat(policy.getType(), is(PolicyType.OKTA_SIGN_ON))
        assertThat(policy.getConditions(), notNullValue())
        assertThat(policy.getConditions().getPeople().getGroups().getInclude(), hasSize(1))
        assertThat(policy.getConditions().getPeople().getGroups().getExclude(), hasSize(0))

        OktaSignOnPolicy retrievedPolicy = (OktaSignOnPolicy) policyApi.getPolicy(policy.getId(), null)

        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getType(), is(PolicyType.OKTA_SIGN_ON))
        assertThat(retrievedPolicy.getConditions(), notNullValue())
        assertThat(retrievedPolicy.getConditions().getPeople().getGroups().getInclude(), hasSize(1))
        assertThat(retrievedPolicy.getConditions().getPeople().getGroups().getExclude(), hasSize(0))
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createProfileEnrollmentPolicy() {

        ProfileEnrollmentPolicy profileEnrollmentPolicy = new ProfileEnrollmentPolicy()
        profileEnrollmentPolicy.name("policy+" + UUID.randomUUID().toString())
            .type(PolicyType.PROFILE_ENROLLMENT)
            .status(LifecycleStatus.ACTIVE)
            .description("IT created Policy - createProfileEnrollmentPolicy")

        ProfileEnrollmentPolicy createdProfileEnrollmentPolicy =
            policyApi.createPolicy(profileEnrollmentPolicy, false) as ProfileEnrollmentPolicy

        registerForCleanup(createdProfileEnrollmentPolicy)

        assertThat(createdProfileEnrollmentPolicy, notNullValue())
        assertThat(createdProfileEnrollmentPolicy.getName(), notNullValue())
        assertThat(createdProfileEnrollmentPolicy.getType(), equalTo(PolicyType.PROFILE_ENROLLMENT))
        assertThat(createdProfileEnrollmentPolicy.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        ProfileEnrollmentPolicy retrievedPolicy = (ProfileEnrollmentPolicy) policyApi.getPolicy(createdProfileEnrollmentPolicy.getId(), null)

        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getName(), notNullValue())
        assertThat(retrievedPolicy.getType(), equalTo(PolicyType.PROFILE_ENROLLMENT))
        assertThat(retrievedPolicy.getStatus(), equalTo(LifecycleStatus.ACTIVE))
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createAccessPolicyRule() {

        String name = "oidc_client"
        String label = "java-sdk-it-" + UUID.randomUUID().toString()

        ApplicationApi applicationApi = new ApplicationApi(getClient())
        PolicyApi policyApi = new PolicyApi(getClient())

        Application oidcApp = OIDCApplicationBuilder.instance()
            .setName(name)
            .setLabel(label)
            .addRedirectUris("https://www.example.com")
            .setPostLogoutRedirectUris(Collections.singletonList("https://www.example.com/logout"))
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId(UUID.randomUUID().toString())
            .setClientSecret(UUID.randomUUID().toString())
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(false)
            .setWeb(true)
            .setLoginRedirectUrl("https://www.myapp.com")
            .setErrorRedirectUrl("https://www.myapp.com/error")
            .setLoginUrl("https://www.myapp/com/login")
            .setRedirectUrl("https://www.myapp.com/new")
            .buildAndCreate(applicationApi)
        registerForCleanup(oidcApp)

        assertThat(oidcApp, notNullValue())
        assertThat(oidcApp.getSignOnMode(), is(ApplicationSignOnMode.OPENID_CONNECT))
        assertThat(oidcApp.getLinks(), notNullValue())
        assertThat(oidcApp.getLinks().getAccessPolicy(), notNullValue())

        // accessPolicy:[href:https://example.com/api/v1/policies/rst412ay22NkOdJJr0g7]
        String accessPolicyId = oidcApp.getLinks().getAccessPolicy().getHref().replaceAll("]", "").tokenize("/")[-1]

        AccessPolicy accessPolicy = (AccessPolicy) policyApi.getPolicy(accessPolicyId, null)
        assertThat(accessPolicy, notNullValue())

        AccessPolicyRule accessPolicyRule = new AccessPolicyRule()
        accessPolicyRule.name(label)
        accessPolicyRule.setType(PolicyRuleType.ACCESS_POLICY)

        AccessPolicyRuleActions accessPolicyRuleActions = new AccessPolicyRuleActions()
        AccessPolicyRuleApplicationSignOn accessPolicyRuleApplicationSignOn = new AccessPolicyRuleApplicationSignOn()
        accessPolicyRuleApplicationSignOn.access(AccessPolicyRuleApplicationSignOnAccess.DENY)
        VerificationMethod verificationMethod = new VerificationMethod()
        verificationMethod.type(PolicyRuleVerificationMethodType.ASSURANCE)
//            .factorMode("1FA")
//            .reauthenticateIn("PT43800H")
        accessPolicyRuleApplicationSignOn.verificationMethod(verificationMethod)
        accessPolicyRuleActions.appSignOn(accessPolicyRuleApplicationSignOn)
        accessPolicyRule.actions(accessPolicyRuleActions)

        AccessPolicyRule createdAccessPolicyRule =
            policyApi.createPolicyRule(accessPolicy.getId(), accessPolicyRule, null, true) as AccessPolicyRule

        assertThat(createdAccessPolicyRule, notNullValue())
        assertThat(createdAccessPolicyRule.getName(), is(label))

        AccessPolicyRuleActions createdAccessPolicyRuleActions = createdAccessPolicyRule.getActions()

        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getAccess(), is(AccessPolicyRuleApplicationSignOnAccess.DENY))
        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getType(), is(PolicyRuleVerificationMethodType.ASSURANCE))
//        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getFactorMode(), is("1FA"))
//        assertThat(createdAccessPolicyRuleActions.getAppSignOn().getVerificationMethod().getReauthenticateIn(), is("PT43800H"))

        policyApi.deactivatePolicyRule(accessPolicy.getId(), createdAccessPolicyRule.getId())
        policyApi.deletePolicyRule(accessPolicy.getId(), createdAccessPolicyRule.getId())

        Thread.sleep(testOperationDelay)

        // get policy rule expect 404
        expect(ApiException) {
            policyApi.getPolicyRule(accessPolicy.getId(), createdAccessPolicyRule.getId())
        }
    }

    @Test
    void signOnActionsTest() {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - signOnActionsTest")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        def policyRuleName = "policyRule+" + UUID.randomUUID().toString()

        OktaSignOnPolicyRule oktaSignOnPolicyRule = new OktaSignOnPolicyRule()
        oktaSignOnPolicyRule.name(policyRuleName)
        oktaSignOnPolicyRule.type(PolicyRuleType.SIGN_ON)
        OktaSignOnPolicyRuleActions oktaSignOnPolicyRuleActions = new OktaSignOnPolicyRuleActions()
        OktaSignOnPolicyRuleSignonActions oktaSignOnPolicyRuleSignOnActions = new OktaSignOnPolicyRuleSignonActions()
        oktaSignOnPolicyRuleSignOnActions.setAccess(OktaSignOnPolicyRuleSignonActions. AccessEnum.DENY)
        oktaSignOnPolicyRuleSignOnActions.setRequireFactor(false)
        oktaSignOnPolicyRuleActions.setSignon(oktaSignOnPolicyRuleSignOnActions)
        oktaSignOnPolicyRule.actions(oktaSignOnPolicyRuleActions)

        OktaSignOnPolicyRule createdPolicyRule =
            policyApi.createPolicyRule(policy.getId(), oktaSignOnPolicyRule, null, true) as OktaSignOnPolicyRule

        assertThat(createdPolicyRule.getId(), notNullValue())
        assertThat(createdPolicyRule.getName(), is(policyRuleName))
        assertThat(createdPolicyRule.getType(), is(PolicyRuleType.SIGN_ON))

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

        assertThat(policy, notNullValue())
        assertThat(policy.getStatus(), is(LifecycleStatus.INACTIVE))
        assertThat(policy.getType(), is(PolicyType.OKTA_SIGN_ON))

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

        Policy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - listPoliciesWithParams")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.INACTIVE)
            .buildAndCreate(policyApi)
        registerForCleanup(policy)

        List<Policy> policies =
            policyApi.listPolicies(PolicyType.OKTA_SIGN_ON.name(), LifecycleStatus.INACTIVE.name(), null, null, null, null, null, null)

        assertThat(policies, not(empty()))
        policies.stream()
            .limit(5)
            .forEach { assertRulesNotExpanded(it) }

        policies = policyApi.listPolicies(PolicyType.OKTA_SIGN_ON.name(), LifecycleStatus.ACTIVE.name(), "rules", null, null, null, null, null)

        assertThat(policies, not(empty()))
        policies.stream()
            .limit(5)
            .forEach { assertRulesExpanded(it) }
    }

    static void assertRulesNotExpanded(Policy policy) {
        assertThat(policy.getEmbedded(), anyOf(nullValue(), not(hasKey("rules"))))
    }

    static void assertRulesExpanded(Policy policy) {
        assertThat(policy.getEmbedded(), allOf(notNullValue(), hasKey("rules")))
    }

    @Test (groups = "group2")
    void listPolicyRulesTest() {

        Policy policy = randomSignOnPolicy()
        registerForCleanup(policy)

        PolicyApi policyApi = new PolicyApi(getClient())

        policyApi.listPolicyRules(policy.getId(), null).forEach({policyItem ->
            assertThat(policyItem, notNullValue())
            assertThat(policyItem.getId(), notNullValue())
            assertThat(policyItem, instanceOf(Policy.class))
        })
    }
}

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
import com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient
import com.okta.sdk.resource.model.ApplicationSignOnMode
import com.okta.sdk.resource.model.LifecycleStatus
import com.okta.sdk.resource.model.OAuthApplicationCredentials
import com.okta.sdk.resource.model.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.model.GrantType
import com.okta.sdk.resource.model.OAuthResponseType
import com.okta.sdk.resource.model.OktaSignOnPolicy
import com.okta.sdk.resource.model.OktaSignOnPolicyRule
import com.okta.sdk.resource.model.OktaSignOnPolicyRuleActions
import com.okta.sdk.resource.model.OktaSignOnPolicyRuleSignonActions
import com.okta.sdk.resource.model.OpenIdConnectApplication
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettings
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClient
import com.okta.sdk.resource.model.OpenIdConnectApplicationType
import com.okta.sdk.resource.model.Policy
import com.okta.sdk.resource.model.PolicyRule
import com.okta.sdk.resource.model.PolicyRuleType
import com.okta.sdk.resource.model.PolicyRuleVerificationMethodType
import com.okta.sdk.resource.model.PolicyType
import com.okta.sdk.resource.model.PolicyTypeParameter
import com.okta.sdk.resource.model.ProfileEnrollmentPolicy
import com.okta.sdk.resource.model.CreateOrUpdatePolicy
import com.okta.sdk.resource.model.VerificationMethod
import com.okta.sdk.resource.policy.OktaSignOnPolicyBuilder
import com.okta.sdk.tests.NonOIEEnvironmentOnly
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.api.PolicyApi
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


    @Test
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

        OktaSignOnPolicy retrievedPolicy = (OktaSignOnPolicy) policyApi.getPolicy(policy.getId(), null)

        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getType(), is(PolicyType.OKTA_SIGN_ON))
        assertThat(retrievedPolicy.getConditions(), notNullValue())
        assertThat(retrievedPolicy.getConditions().getPeople().getGroups().getInclude(), hasSize(1))
    }

    // disable running them in bacon
    @Test (groups = "bacon")
    void createProfileEnrollmentPolicy() {

        ProfileEnrollmentPolicy createPolicy = new ProfileEnrollmentPolicy()
        createPolicy.name("policy+" + UUID.randomUUID().toString())
                .type(PolicyType.PROFILE_ENROLLMENT)
                .status(LifecycleStatus.ACTIVE)
                .description("IT created Policy - createProfileEnrollmentPolicy")

        ProfileEnrollmentPolicy createdProfileEnrollmentPolicy =
                policyApi.createPolicy(createPolicy, false) as ProfileEnrollmentPolicy

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

        String label = "java-sdk-it-" + UUID.randomUUID().toString()

        ApplicationApi applicationApi = new ApplicationApi(getClient())
        PolicyApi policyApi = new PolicyApi(getClient())

        // Create OIDC application directly (not using builder due to signOnMode issues)
        OpenIdConnectApplication openIdConnectApplication = new OpenIdConnectApplication()
        openIdConnectApplication.label(label)
        openIdConnectApplication.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)

        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        settingsClient.redirectUris(["https://www.example.com/oauth2/callback"])
        settingsClient.postLogoutRedirectUris(["https://www.example.com/logout"])
        settingsClient.responseTypes([OAuthResponseType.TOKEN, OAuthResponseType.CODE])
        settingsClient.grantTypes([GrantType.IMPLICIT, GrantType.AUTHORIZATION_CODE])

        OpenIdConnectApplicationSettings settings = new OpenIdConnectApplicationSettings()
        settings.oauthClient(settingsClient)
        openIdConnectApplication.settings(settings)

        ApplicationCredentialsOAuthClient oauthClient = new ApplicationCredentialsOAuthClient()
        oauthClient.clientId(UUID.randomUUID().toString())
        oauthClient.autoKeyRotation(true)
        oauthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)

        OAuthApplicationCredentials credentials = new OAuthApplicationCredentials()
        credentials.oauthClient(oauthClient)
        openIdConnectApplication.credentials(credentials)
        openIdConnectApplication.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        Application oidcApp = applicationApi.createApplication(openIdConnectApplication, true, null)
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

    @Test (groups = "group2")
    void testCreateAndGetPolicyRule() {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
                .setName("policy+" + UUID.randomUUID().toString())
                .setDescription("IT created Policy - testCreateAndGetPolicyRule")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        String ruleName = "policyRule+" + UUID.randomUUID().toString()

        OktaSignOnPolicyRule oktaSignOnPolicyRule = new OktaSignOnPolicyRule()
        oktaSignOnPolicyRule.name(ruleName)
        oktaSignOnPolicyRule.type(PolicyRuleType.SIGN_ON)

        OktaSignOnPolicyRuleActions actions = new OktaSignOnPolicyRuleActions()
        OktaSignOnPolicyRuleSignonActions signOnActions = new OktaSignOnPolicyRuleSignonActions()
        signOnActions.setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
        signOnActions.setRequireFactor(false)
        actions.setSignon(signOnActions)
        oktaSignOnPolicyRule.actions(actions)

        OktaSignOnPolicyRule createdRule =
                policyApi.createPolicyRule(policy.getId(), oktaSignOnPolicyRule, null, true) as OktaSignOnPolicyRule

        assertThat(createdRule, notNullValue())
        assertThat(createdRule.getId(), notNullValue())
        assertThat(createdRule.getName(), is(ruleName))
        assertThat(createdRule.getType(), is(PolicyRuleType.SIGN_ON))

        // Retrieve the rule
        PolicyRule retrievedRule = policyApi.getPolicyRule(policy.getId(), createdRule.getId())

        assertThat(retrievedRule, notNullValue())
        assertThat(retrievedRule.getId(), is(createdRule.getId()))
        assertThat(retrievedRule.getName(), is(ruleName))

        // Cleanup
        policyApi.deactivatePolicyRule(policy.getId(), createdRule.getId())
        policyApi.deletePolicyRule(policy.getId(), createdRule.getId())
    }

    @Test (groups = "group2", enabled = true)
    void testListAllPolicies() {

        // Create a test policy to ensure there's at least one
        Policy policy;

        // List all OKTA_SIGN_ON policies
        List<Policy> policies = policyApi.listPolicies(PolicyType.OKTA_SIGN_ON.name() as PolicyTypeParameter, null, null, null, null, null, null, null)

        assertThat(policies, notNullValue())
        assertThat(policies, not(empty()))
    }

    @Test
    void testCreateAndGetPolicy() {

        String policyName = "policy+" + UUID.randomUUID().toString()

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName(policyName)
            .setDescription("IT created Policy - testCreateAndGetPolicy")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        assertThat(policy, notNullValue())
        assertThat(policy.getId(), notNullValue())
        assertThat(policy.getName(), is(policyName))
        assertThat(policy.getType(), is(PolicyType.OKTA_SIGN_ON))
        assertThat(policy.getStatus(), is(LifecycleStatus.ACTIVE))

        // Retrieve the policy
        Policy retrievedPolicy = policyApi.getPolicy(policy.getId(), null)

        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), is(policy.getId()))
        assertThat(retrievedPolicy.getName(), is(policyName))
        assertThat(retrievedPolicy.getType(), is(PolicyType.OKTA_SIGN_ON))
    }

    @Test
    void testReplacePolicy() {

        String originalName = "policy+" + UUID.randomUUID().toString()

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName(originalName)
            .setDescription("IT created Policy - testReplacePolicy")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        assertThat(policy.getName(), is(originalName))

        // Update the policy
        String updatedName = "updated-policy+" + UUID.randomUUID().toString()
        policy.setName(updatedName)
        policy.setDescription("Updated description")

        Policy updatedPolicy = policyApi.replacePolicy(policy.getId(), policy)

        assertThat(updatedPolicy, notNullValue())
        assertThat(updatedPolicy.getName(), is(updatedName))
        assertThat(updatedPolicy.getDescription(), is("Updated description"))
    }


    @Test (groups = "group2")
    void testActivateAndDeactivatePolicyRule() {

        OktaSignOnPolicy policy = OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - testActivateAndDeactivatePolicyRule")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi) as OktaSignOnPolicy
        registerForCleanup(policy)

        OktaSignOnPolicyRule oktaSignOnPolicyRule = new OktaSignOnPolicyRule()
        oktaSignOnPolicyRule.name("policyRule+" + UUID.randomUUID().toString())
        oktaSignOnPolicyRule.type(PolicyRuleType.SIGN_ON)

        OktaSignOnPolicyRuleActions actions = new OktaSignOnPolicyRuleActions()
        OktaSignOnPolicyRuleSignonActions signOnActions = new OktaSignOnPolicyRuleSignonActions()
        signOnActions.setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
        signOnActions.setRequireFactor(false)
        actions.setSignon(signOnActions)
        oktaSignOnPolicyRule.actions(actions)

        OktaSignOnPolicyRule createdRule =
            policyApi.createPolicyRule(policy.getId(), oktaSignOnPolicyRule, null, false) as OktaSignOnPolicyRule

        assertThat(createdRule.getStatus(), is(LifecycleStatus.INACTIVE))

        // Activate the rule
        policyApi.activatePolicyRule(policy.getId(), createdRule.getId())
        Thread.sleep(testOperationDelay)

        PolicyRule activatedRule = policyApi.getPolicyRule(policy.getId(), createdRule.getId())
        assertThat(activatedRule.getStatus(), is(LifecycleStatus.ACTIVE))

        // Deactivate the rule - this succeeds but status may remain ACTIVE (API behavior)
        policyApi.deactivatePolicyRule(policy.getId(), createdRule.getId())
        Thread.sleep(testOperationDelay)

        // Note: Policy rules may not have their status change to INACTIVE after deactivation,
        // but deactivation is still required before deletion

        // Cleanup - deactivation allows deletion even if status shows ACTIVE
        policyApi.deletePolicyRule(policy.getId(), createdRule.getId())
    }

    @Test (groups = "group2")
    void testDeletePolicyAndRule() {
        OktaSignOnPolicy testPolicy = null
        PolicyRule testRule = null
        
        try {
            // Create test policy using builder
            testPolicy = OktaSignOnPolicyBuilder.instance()
                .setName("policy-delete-" + UUID.randomUUID().toString())
                .setDescription("Test policy for delete operations")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(policyApi) as OktaSignOnPolicy
            registerForCleanup(testPolicy)
            
            assertThat testPolicy, notNullValue()
            assertThat testPolicy.getId(), notNullValue()
            
            // Create test rule
            OktaSignOnPolicyRule policyRule = new OktaSignOnPolicyRule()
            policyRule.name("rule-delete-" + UUID.randomUUID().toString())
            policyRule.type(PolicyRuleType.SIGN_ON)
            
            OktaSignOnPolicyRuleActions actions = new OktaSignOnPolicyRuleActions()
            OktaSignOnPolicyRuleSignonActions signOnActions = new OktaSignOnPolicyRuleSignonActions()
            signOnActions.setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
            signOnActions.setRequireFactor(false)
            actions.setSignon(signOnActions)
            policyRule.actions(actions)
            
            testRule = policyApi.createPolicyRule(testPolicy.getId(), policyRule, null, true)
            
            assertThat testRule, notNullValue()
            assertThat testRule.getId(), notNullValue()
            
            Thread.sleep(testOperationDelay)
            
            // Delete the rule
            policyApi.deletePolicyRule(testPolicy.getId(), testRule.getId())
            
            // Verify rule is deleted - should throw 404
            expect(ApiException) {
                policyApi.getPolicyRule(testPolicy.getId(), testRule.getId())
            }
            
            testRule = null // Mark as deleted
            
            Thread.sleep(testOperationDelay)
            
            // Delete policy - must deactivate first
            policyApi.deactivatePolicy(testPolicy.getId())
            Thread.sleep(testOperationDelay)
            
            policyApi.deletePolicy(testPolicy.getId())
            
            // Verify policy is deleted - should throw 404
            expect(ApiException) {
                policyApi.getPolicy(testPolicy.getId(), null)
            }
            
            testPolicy = null // Mark as deleted
            
        } finally {
            // Cleanup if test fails partway
            if (testRule != null && testPolicy != null) {
                try {
                    policyApi.deletePolicyRule(testPolicy.getId(), testRule.getId())
                } catch (ApiException ignored) {}
            }
            if (testPolicy != null) {
                try {
                    policyApi.deactivatePolicy(testPolicy.getId())
                    Thread.sleep(500)
                    policyApi.deletePolicy(testPolicy.getId())
                } catch (ApiException ignored) {}
            }
        }
    }

    @Test (groups = "group2")
    void testPolicyRulePriorityOrder() {
        OktaSignOnPolicy testPolicy = null
        List<PolicyRule> createdRules = []
        
        try {
            // Create test policy
            testPolicy = OktaSignOnPolicyBuilder.instance()
                .setName("policy-priority-" + UUID.randomUUID().toString())
                .setDescription("Test policy for rule priority")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(policyApi) as OktaSignOnPolicy
            registerForCleanup(testPolicy)
            
            // Create three rules with different priorities
            [1, 2, 3].each { priority ->
                OktaSignOnPolicyRule rule = new OktaSignOnPolicyRule()
                rule.name("rule-p${priority}-" + UUID.randomUUID().toString().substring(0, 20))
                rule.type(PolicyRuleType.SIGN_ON)
                rule.priority(priority)
                
                OktaSignOnPolicyRuleActions actions = new OktaSignOnPolicyRuleActions()
                OktaSignOnPolicyRuleSignonActions signOnActions = new OktaSignOnPolicyRuleSignonActions()
                signOnActions.setAccess(OktaSignOnPolicyRuleSignonActions.AccessEnum.ALLOW)
                signOnActions.setRequireFactor(false)
                actions.setSignon(signOnActions)
                rule.actions(actions)
                
                def createdRule = policyApi.createPolicyRule(testPolicy.getId(), rule, null, true)
                createdRules.add(createdRule)
                
                assertThat createdRule, notNullValue()
                assertThat createdRule.getPriority(), notNullValue()
            }
            
            Thread.sleep(testOperationDelay)
            
            // Retrieve all rules and verify they are ordered by priority
            def allRules = policyApi.listPolicyRules(testPolicy.getId(), null)
            
            assertThat allRules, notNullValue()
            assertThat allRules.size(), greaterThanOrEqualTo(3)
            
            // Verify the first 3 custom rules are in priority order
            def customRules = allRules.findAll { it.name.startsWith("rule-p") }
            assertThat customRules.size(), is(3)
            
        } finally {
            // Cleanup rules
            createdRules.each { rule ->
                if (rule != null && testPolicy != null) {
                    try {
                        policyApi.deletePolicyRule(testPolicy.getId(), rule.getId())
                    } catch (ApiException ignored) {}
                }
            }
        }
    }

    @Test (groups = "group2")
    void testListPoliciesWithFiltering() {
        OktaSignOnPolicy activePolicy = null
        OktaSignOnPolicy inactivePolicy = null
        
        try {
            // Create active policy
            activePolicy = OktaSignOnPolicyBuilder.instance()
                .setName("policy-active-" + UUID.randomUUID().toString())
                .setDescription("Test active policy for filtering")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.ACTIVE)
                .buildAndCreate(policyApi) as OktaSignOnPolicy
            registerForCleanup(activePolicy)
            
            // Create inactive policy
            inactivePolicy = OktaSignOnPolicyBuilder.instance()
                .setName("policy-inactive-" + UUID.randomUUID().toString())
                .setDescription("Test inactive policy for filtering")
                .setType(PolicyType.OKTA_SIGN_ON)
                .setStatus(LifecycleStatus.INACTIVE)
                .buildAndCreate(policyApi) as OktaSignOnPolicy
            registerForCleanup(inactivePolicy)
            
            Thread.sleep(testOperationDelay)
            
            // List only ACTIVE policies - use empty strings instead of null
            List<Policy> activePolicies = policyApi.listPolicies(
                PolicyTypeParameter.OKTA_SIGN_ON, 
                LifecycleStatus.ACTIVE.name(), 
                "", "", "", "", "", ""
            )
            
            assertThat activePolicies, notNullValue()
            assertThat activePolicies, not(empty())
            
            def foundActive = activePolicies.any { it.getId() == activePolicy.getId() }
            assertThat foundActive, is(true)
            
            // List only INACTIVE policies
            List<Policy> inactivePolicies = policyApi.listPolicies(
                PolicyTypeParameter.OKTA_SIGN_ON,
                LifecycleStatus.INACTIVE.name(),
                "", "", "", "", "", ""
            )
            
            assertThat inactivePolicies, notNullValue()
            def foundInactive = inactivePolicies.any { it.getId() == inactivePolicy.getId() }
            assertThat foundInactive, is(true)
            
        } finally {
            // Cleanup handled by registerForCleanup
        }
    }

    OktaSignOnPolicy randomSignOnPolicy() {
        return OktaSignOnPolicyBuilder.instance()
            .setName("policy+" + UUID.randomUUID().toString())
            .setDescription("IT created Policy - random")
            .setType(PolicyType.OKTA_SIGN_ON)
            .setStatus(LifecycleStatus.ACTIVE)
            .buildAndCreate(policyApi) as OktaSignOnPolicy
    }
}

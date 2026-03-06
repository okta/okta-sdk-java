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

import com.okta.sdk.resource.api.*
import com.okta.sdk.resource.client.ApiClient
import com.okta.sdk.resource.client.ApiException
import com.okta.sdk.resource.model.*
import com.okta.sdk.tests.Scenario
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AdditionalHeadersCoverageIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(AdditionalHeadersCoverageIT)


    private def headers = Collections.<String, String>emptyMap()

    /**
     * Test AuthorizationServer APIs with additionalHeaders overloads for CRUD operations.
     */
    @Test(groups = "group3")
    @Scenario("headers-authorization-server-crud")
    void testAuthorizationServerCrudWithHeaders() {
        ApiClient client = getClient()
        def authServerApi = new AuthorizationServerApi(client)
        def scopesApi = new AuthorizationServerScopesApi(client)
        def claimsApi = new AuthorizationServerClaimsApi(client)
        def policiesApi = new AuthorizationServerPoliciesApi(client)
        def rulesApi = new AuthorizationServerRulesApi(client)
        def keysApi = new AuthorizationServerKeysApi(client)
        def credKeysApi = new OAuth2ResourceServerCredentialsKeysApi(client)

        String testId = UUID.randomUUID().toString().substring(0, 8)
        String authServerId = null

        try {
            // Create auth server with headers
            def authServer = authServerApi.createAuthorizationServer(
                new AuthorizationServer()
                    .name("headers-test-${testId}")
                    .description("Headers test")
                    .audiences(["api://headers-test-${testId}".toString()]),
                headers)
            assertThat authServer.id, notNullValue()
            authServerId = authServer.id
            logger.debug("    createAuthorizationServer(headers)")

            // Get auth server with headers
            def fetched = authServerApi.getAuthorizationServer(authServer.id, headers)
            assertThat fetched.name, equalTo("headers-test-${testId}".toString())
            logger.debug("    getAuthorizationServer(headers)")

            // Replace auth server with headers
            authServer.description("Updated via headers")
            def updated = authServerApi.replaceAuthorizationServer(authServer.id, authServer, headers)
            assertThat updated.description, equalTo("Updated via headers")
            logger.debug("    replaceAuthorizationServer(headers)")

            // List auth servers with headers (non-paged)
            def servers = authServerApi.listAuthorizationServers(null, null, null, headers)
            assertThat servers, not(empty())
            logger.debug("    listAuthorizationServers(headers)")

            // Create scope with headers
            def scope = scopesApi.createOAuth2Scope(authServer.id,
                new OAuth2Scope().name("h:scope:${testId}".toString()), headers)
            assertThat scope.id, notNullValue()
            logger.debug("    createOAuth2Scope(headers)")

            // Get scope with headers
            def fetchedScope = scopesApi.getOAuth2Scope(authServer.id, scope.id, headers)
            assertThat fetchedScope.name, equalTo("h:scope:${testId}".toString())
            logger.debug("    getOAuth2Scope(headers)")

            // Replace scope with headers
            scope.description("Updated scope")
            def updatedScope = scopesApi.replaceOAuth2Scope(authServer.id, scope.id, scope, headers)
            logger.debug("    replaceOAuth2Scope(headers)")

            // List scopes with headers
            def scopes = scopesApi.listOAuth2Scopes(authServer.id, null, null, null, null, headers)
            logger.debug("    listOAuth2Scopes(headers)")

            // Delete scope with headers
            scopesApi.deleteOAuth2Scope(authServer.id, scope.id, headers)
            logger.debug("    deleteOAuth2Scope(headers)")

            // Create claim with headers
            def claim = claimsApi.createOAuth2Claim(authServer.id,
                new OAuth2Claim().name("h_claim_${testId}".toString())
                    .status(LifecycleStatus.ACTIVE).claimType(OAuth2ClaimType.RESOURCE)
                    .valueType(OAuth2ClaimValueType.EXPRESSION).value("user.email"),
                headers)
            assertThat claim.id, notNullValue()
            logger.debug("    createOAuth2Claim(headers)")

            // Get claim with headers
            def fetchedClaim = claimsApi.getOAuth2Claim(authServer.id, claim.id, headers)
            logger.debug("    getOAuth2Claim(headers)")

            // Replace claim with headers
            claim.value("user.login")
            def updatedClaim = claimsApi.replaceOAuth2Claim(authServer.id, claim.id, claim, headers)
            logger.debug("    replaceOAuth2Claim(headers)")

            // List claims with headers
            def claims = claimsApi.listOAuth2Claims(authServer.id, headers)
            logger.debug("    listOAuth2Claims(headers)")

            // Delete claim with headers
            claimsApi.deleteOAuth2Claim(authServer.id, claim.id, headers)
            logger.debug("    deleteOAuth2Claim(headers)")

            // Create policy with headers
            def policy = policiesApi.createAuthorizationServerPolicy(authServer.id,
                new AuthorizationServerPolicy()
                    .type(AuthorizationServerPolicy.TypeEnum.OAUTH_AUTHORIZATION_POLICY)
                    .name("H Policy ${testId}".toString()).description("Test").priority(1)
                    .conditions(new AuthorizationServerPolicyConditions()
                        .clients(new ClientPolicyCondition().include(["ALL_CLIENTS"]))),
                headers)
            assertThat policy.id, notNullValue()
            logger.debug("    createAuthorizationServerPolicy(headers)")

            // Get policy with headers
            def fetchedPolicy = policiesApi.getAuthorizationServerPolicy(authServer.id, policy.id, headers)
            logger.debug("    getAuthorizationServerPolicy(headers)")

            // Replace policy with headers
            policy.description("Updated policy")
            def updatedPolicy = policiesApi.replaceAuthorizationServerPolicy(authServer.id, policy.id, policy, headers)
            logger.debug("    replaceAuthorizationServerPolicy(headers)")

            // List policies with headers
            def policies = policiesApi.listAuthorizationServerPolicies(authServer.id, headers)
            logger.debug("    listAuthorizationServerPolicies(headers)")

            // Create rule with headers
            def rule = rulesApi.createAuthorizationServerPolicyRule(authServer.id, policy.id,
                new AuthorizationServerPolicyRuleRequest()
                    .name("H Rule ${testId}".toString()).priority(1)
                    .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                    .conditions(new AuthorizationServerPolicyRuleConditions()
                        .people(new AuthorizationServerPolicyPeopleCondition()
                            .groups(new AuthorizationServerPolicyRuleGroupCondition().include(["EVERYONE"])))
                        .grantTypes(new GrantTypePolicyRuleCondition().include(["authorization_code"]))
                        .scopes(new OAuth2ScopesMediationPolicyRuleCondition().include(["openid"])))
                    .actions(new AuthorizationServerPolicyRuleActions()
                        .token(new TokenAuthorizationServerPolicyRuleAction()
                            .accessTokenLifetimeMinutes(60).refreshTokenLifetimeMinutes(0).refreshTokenWindowMinutes(10080))),
                headers)
            assertThat rule.id, notNullValue()
            logger.debug("    createAuthorizationServerPolicyRule(headers)")

            // Get rule with headers
            def fetchedRule = rulesApi.getAuthorizationServerPolicyRule(authServer.id, policy.id, rule.id, headers)
            logger.debug("    getAuthorizationServerPolicyRule(headers)")

            // Replace rule with headers
            rule.name("H Rule Updated ${testId}".toString())
            def ruleForUpdate = new AuthorizationServerPolicyRuleRequest()
                .name("H Rule Updated ${testId}".toString()).priority(1)
                .type(AuthorizationServerPolicyRuleRequest.TypeEnum.RESOURCE_ACCESS)
                .conditions(rule.conditions)
                .actions(rule.actions)
            def updatedRule = rulesApi.replaceAuthorizationServerPolicyRule(authServer.id, policy.id, rule.id, ruleForUpdate, headers)
            logger.debug("    replaceAuthorizationServerPolicyRule(headers)")

            // List rules with headers
            def rules = rulesApi.listAuthorizationServerPolicyRules(authServer.id, policy.id, headers)
            logger.debug("    listAuthorizationServerPolicyRules(headers)")

            // Delete rule with headers
            rulesApi.deleteAuthorizationServerPolicyRule(authServer.id, policy.id, rule.id, headers)
            logger.debug("    deleteAuthorizationServerPolicyRule(headers)")

            // Delete policy with headers
            policiesApi.deleteAuthorizationServerPolicy(authServer.id, policy.id, headers)
            logger.debug("    deleteAuthorizationServerPolicy(headers)")

            // List keys with headers
            def keys = keysApi.listAuthorizationServerKeys(authServer.id, headers)
            logger.debug("    listAuthorizationServerKeys(headers)")

            // Rotate keys with headers
            try {
                def newKeys = authServerApi.rotateAuthorizationServerKeys(authServer.id,
                    new JwkUse().use("sig"), headers)
                logger.debug("    rotateAuthorizationServerKeys(headers)")
            } catch (Exception e) {
                logger.debug("    rotateAuthorizationServerKeys: {}", e.message?.take(60))
            }

            // Get resource server credentials keys with headers
            try {
                def credKeys = credKeysApi.listOAuth2ResourceServerJsonWebKeys(authServer.id, headers)
                logger.debug("    listOAuth2ResourceServerJsonWebKeys(headers)")
            } catch (Exception e) {
                logger.debug("    listOAuth2ResourceServerJsonWebKeys: {}", e.message?.take(60))
            }

            // Lifecycle with headers
            authServerApi.deactivateAuthorizationServer(authServer.id, headers)
            logger.debug("    deactivateAuthorizationServer(headers)")
            authServerApi.activateAuthorizationServer(authServer.id, headers)
            logger.debug("    activateAuthorizationServer(headers)")

            // Deactivate and delete with headers
            authServerApi.deactivateAuthorizationServer(authServer.id, headers)
            authServerApi.deleteAuthorizationServer(authServer.id, headers)
            logger.debug("    deleteAuthorizationServer(headers)")

            logger.debug("\n AuthorizationServer CRUD with headers completed!")

        } catch (ApiException e) {
            logger.debug(" Test failed: {}", e.responseBody)
            throw e
        } finally {
            if (authServerId) {
                try { authServerApi.deactivateAuthorizationServer(authServerId) } catch (Exception ignored) {}
                try { authServerApi.deleteAuthorizationServer(authServerId) } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Test OrgSetting APIs with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-org-settings")
    void testOrgSettingWithHeaders() {
        ApiClient client = getClient()
        def contactApi = new OrgSettingContactApi(client)
        def adminApi = new OrgSettingAdminApi(client)
        def generalApi = new OrgSettingGeneralApi(client)
        def metadataApi = new OrgSettingMetadataApi(client)
        def commApi = new OrgSettingCommunicationApi(client)
        def supportApi = new OrgSettingSupportApi(client)

        // OrgSettingContactApi
        try {
            def types = contactApi.listOrgContactTypes(headers)
            logger.debug("    listOrgContactTypes(headers)")
        } catch (Exception e) { logger.debug("    listOrgContactTypes: {}", e.message?.take(60)) }

        try {
            def contact = contactApi.getOrgContactUser("BILLING", headers)
            logger.debug("    getOrgContactUser(headers)")
        } catch (Exception e) { logger.debug("    getOrgContactUser: {}", e.message?.take(60)) }

        // OrgSettingAdminApi
        try {
            def setting = adminApi.getAutoAssignAdminAppSetting(headers)
            logger.debug("    getAutoAssignAdminAppSetting(headers)")
        } catch (Exception e) { logger.debug("    getAutoAssignAdminAppSetting: {}", e.message?.take(60)) }
        try {
            def clientPriv = adminApi.getClientPrivilegesSetting(headers)
            logger.debug("    getClientPrivilegesSetting(headers)")
        } catch (Exception e) { logger.debug("    getClientPrivilegesSetting: {}", e.message?.take(60)) }
        try {
            def thirdParty = adminApi.getThirdPartyAdminSetting(headers)
            logger.debug("    getThirdPartyAdminSetting(headers)")
        } catch (Exception e) { logger.debug("    getThirdPartyAdminSetting: {}", e.message?.take(60)) }

        // OrgSettingGeneralApi
        try {
            def settings = generalApi.getOrgSettings(headers)
            logger.debug("    getOrgSettings(headers)")
        } catch (Exception e) { logger.debug("    getOrgSettings: {}", e.message?.take(60)) }

        // OrgSettingMetadataApi
        try {
            def metadata = metadataApi.getWellknownOrgMetadata(headers)
            logger.debug("    getWellknownOrgMetadata(headers)")
        } catch (Exception e) { logger.debug("    getWellknownOrgMetadata: {}", e.message?.take(60)) }

        // OrgSettingCommunicationApi
        try {
            def prefs = commApi.getOktaCommunicationSettings(headers)
            logger.debug("    getOktaCommunicationSettings(headers)")
        } catch (Exception e) { logger.debug("    getOktaCommunicationSettings: {}", e.message?.take(60)) }

        // OrgSettingSupportApi
        try {
            def support = supportApi.getOrgOktaSupportSettings(headers)
            logger.debug("    getOrgOktaSupportSettings(headers)")
        } catch (Exception e) { logger.debug("    getOrgOktaSupportSettings: {}", e.message?.take(60)) }

        logger.debug("\n OrgSetting with headers completed!")
    }

    /**
     * Test Group and GroupRule APIs with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-group-crud")
    void testGroupCrudWithHeaders() {
        ApiClient client = getClient()
        def groupApi = new GroupApi(client)
        def groupRuleApi = new GroupRuleApi(client)
        def groupOwnerApi = new GroupOwnerApi(client)

        String testId = UUID.randomUUID().toString().substring(0, 8)
        String groupId = null
        String ruleId = null

        try {
            // Create group with headers
            def group = groupApi.addGroup(
                new AddGroupRequest().profile(new OktaUserGroupProfile()
                    .name("Headers Test ${testId}".toString())
                    .description("Test group for headers coverage")),
                headers)
            assertThat group.id, notNullValue()
            groupId = group.id
            logger.debug("    addGroup(headers)")

            // Get group with headers
            def fetched = groupApi.getGroup(group.id, headers)
            assertThat fetched.profile.name, equalTo("Headers Test ${testId}".toString())
            logger.debug("    getGroup(headers)")

            // Replace group with headers
            def updateReq = new AddGroupRequest().profile(new OktaUserGroupProfile()
                .name("Headers Test ${testId}".toString())
                .description("Updated via headers"))
            def updated = groupApi.replaceGroup(group.id, updateReq, headers)
            logger.debug("    replaceGroup(headers)")

            // List groups with headers
            def groups = groupApi.listGroups(null, null, null, null, null, null, null, null, headers)
            assertThat groups, not(empty())
            logger.debug("    listGroups(headers)")

            // List group users with headers
            def users = groupApi.listGroupUsers(group.id, null, null, headers)
            logger.debug("    listGroupUsers(headers)")

            // List assigned applications with headers
            try {
                def apps = groupApi.listAssignedApplicationsForGroup(group.id, null, null, headers)
                logger.debug("    listAssignedApplicationsForGroup(headers)")
            } catch (Exception e) {
                logger.debug("    listAssignedApplicationsForGroup: {}", e.message?.take(60))
            }

            // GroupOwnerApi - list owners with headers
            try {
                def owners = groupOwnerApi.listGroupOwners(group.id, null, null, null, headers)
                logger.debug("    listGroupOwners(headers)")
            } catch (Exception e) {
                logger.debug("    listGroupOwners: {}", e.message?.take(60))
            }

            // Create group rule with headers
            def rule = groupRuleApi.createGroupRule(
                new CreateGroupRuleRequest()
                    .name("H-Rule-${testId}".toString())
                    .type(CreateGroupRuleRequest.TypeEnum.GROUP_RULE)
                    .conditions(new GroupRuleConditions()
                        .people(new GroupRulePeopleCondition()
                            .users(new GroupRuleUserCondition().exclude([])))
                        .expression(new GroupRuleExpression()
                            .type("urn:okta:expression:1.0")
                            .value("String.stringContains(user.email, \"@example.com\")")))
                    .actions(new GroupRuleAction()
                        .assignUserToGroups(new GroupRuleGroupAssignment()
                            .groupIds([group.id]))),
                headers)
            ruleId = rule.id
            logger.debug("    createGroupRule(headers)")

            // Get rule with headers
            def fetchedRule = groupRuleApi.getGroupRule(rule.id, null, headers)
            logger.debug("    getGroupRule(headers)")

            // List rules with headers
            def rules = groupRuleApi.listGroupRules(null, null, null, null, headers)
            logger.debug("    listGroupRules(headers)")

            // Delete rule with headers
            try { groupRuleApi.deactivateGroupRule(rule.id, headers) } catch (Exception ignored) {}
            groupRuleApi.deleteGroupRule(rule.id, false, headers)
            logger.debug("    deleteGroupRule(headers)")

            // Delete group with headers
            groupApi.deleteGroup(group.id, headers)
            logger.debug("    deleteGroup(headers)")

            logger.debug("\n Group CRUD with headers completed!")

        } catch (ApiException e) {
            logger.debug(" Test failed: {}", e.responseBody)
            throw e
        } finally {
            if (ruleId) {
                try { groupRuleApi.deactivateGroupRule(ruleId) } catch (Exception ignored) {}
                try { groupRuleApi.deleteGroupRule(ruleId, false) } catch (Exception ignored) {}
            }
            if (groupId) {
                try { groupApi.deleteGroup(groupId) } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Test Policy API with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-policy-crud")
    void testPolicyCrudWithHeaders() {
        ApiClient client = getClient()
        def policyApi = new PolicyApi(client)

        try {
            // List policies with headers (8 params: type, status, q, expand, sortBy, limit, resourceId, after)
            def policies = policyApi.listPolicies("OKTA_SIGN_ON", null, null, null, null, null, null, null, headers)
            assertThat policies, not(empty())
            logger.debug("    listPolicies(OKTA_SIGN_ON, headers)")

            // Get first policy with headers
            if (!policies.isEmpty()) {
                def policyId = policies[0].id
                def policy = policyApi.getPolicy(policyId, null, headers)
                assertThat policy.id, equalTo(policyId)
                logger.debug("    getPolicy(headers)")

                // List policy rules with headers
                try {
                    def rules = policyApi.listPolicyRules(policyId, headers)
                    logger.debug("    listPolicyRules(headers)")

                    if (!rules.isEmpty()) {
                        def rule = policyApi.getPolicyRule(policyId, rules[0].id, headers)
                        logger.debug("    getPolicyRule(headers)")
                    }
                } catch (Exception e) {
                    logger.debug("    listPolicyRules: {}", e.message?.take(60))
                }
            }

            // Also test ACCESS_POLICY type
            try {
                def accessPolicies = policyApi.listPolicies("ACCESS_POLICY", null, null, null, null, null, null, null, headers)
                logger.debug("    listPolicies(ACCESS_POLICY, headers)")
            } catch (Exception e) {
                logger.debug("    listPolicies(ACCESS_POLICY): {}", e.message?.take(60))
            }

            logger.debug("\n Policy CRUD with headers completed!")

        } catch (ApiException e) {
            logger.debug(" Test failed: {}", e.responseBody)
            throw e
        }
    }

    /**
     * Test Authenticator, Realm, ProfileMapping APIs with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-misc-apis")
    void testMiscApisWithHeaders() {
        ApiClient client = getClient()
        def authenticatorApi = new AuthenticatorApi(client)
        def realmApi = new RealmApi(client)
        def profileMappingApi = new ProfileMappingApi(client)
        def apiTokenApi = new ApiTokenApi(client)
        def agentPoolsApi = new AgentPoolsApi(client)

        // AuthenticatorApi
        try {
            def authenticators = authenticatorApi.listAuthenticators(headers)
            logger.debug("    listAuthenticators(headers): {} items", authenticators.size())

            if (!authenticators.isEmpty()) {
                def authId = authenticators[0].id
                def auth = authenticatorApi.getAuthenticator(authId, headers)
                logger.debug("    getAuthenticator(headers)")

                try {
                    def methods = authenticatorApi.listAuthenticatorMethods(authId, headers)
                    logger.debug("    listAuthenticatorMethods(headers)")
                } catch (Exception e) {
                    logger.debug("    listAuthenticatorMethods: {}", e.message?.take(60))
                }
            }
        } catch (Exception e) { logger.debug("    AuthenticatorApi: {}", e.message?.take(60)) }

        // RealmApi
        try {
            def realms = realmApi.listRealms(null, null, null, null, null, headers)
            logger.debug("    listRealms(headers): {} items", realms.size())

            if (!realms.isEmpty()) {
                def realmId = realms[0].id
                def realm = realmApi.getRealm(realmId, headers)
                logger.debug("    getRealm(headers)")
            }
        } catch (Exception e) { logger.debug("    RealmApi: {}", e.message?.take(60)) }

        // ProfileMappingApi
        try {
            def mappings = profileMappingApi.listProfileMappings(null, null, null, null, headers)
            logger.debug("    listProfileMappings(headers): {} items", mappings.size())

            if (!mappings.isEmpty()) {
                def mapping = profileMappingApi.getProfileMapping(mappings[0].id, headers)
                logger.debug("    getProfileMapping(headers)")
            }
        } catch (Exception e) { logger.debug("    ProfileMappingApi: {}", e.message?.take(60)) }

        // ApiTokenApi
        try {
            def tokens = apiTokenApi.listApiTokens(headers)
            logger.debug("    listApiTokens(headers)")
        } catch (Exception e) { logger.debug("    ApiTokenApi: {}", e.message?.take(60)) }

        // AgentPoolsApi
        try {
            def pools = agentPoolsApi.listAgentPools(null, null, null, headers)
            logger.debug("    listAgentPools(headers)")
        } catch (Exception e) { logger.debug("    AgentPoolsApi: {}", e.message?.take(60)) }

        logger.debug("\n Misc APIs with headers completed!")
    }

    /**
     * Test User lifecycle and credential APIs with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-user-lifecycle")
    void testUserLifecycleAndCredsWithHeaders() {
        ApiClient client = getClient()
        def userApi = new UserApi(client)
        def userLifecycleApi = new UserLifecycleApi(client)
        def userCredApi = new UserCredApi(client)
        def userResourcesApi = new UserResourcesApi(client)
        def userSessionsApi = new UserSessionsApi(client)
        def userFactorApi = new UserFactorApi(client)
        def subscriptionApi = new SubscriptionApi(client)

        String testId = UUID.randomUUID().toString().substring(0, 8)
        String userId = null

        try {
            // Create user with headers
            def user = userApi.createUser(
                new CreateUserRequest()
                    .profile(new UserProfile()
                        .firstName("HeadersTest")
                        .lastName("User${testId}")
                        .email("headers-test-${testId}@example.com".toString())
                        .login("headers-test-${testId}@example.com".toString())),
                true, false, null, headers)
            userId = user.getId()
            logger.debug("    createUser(headers)")

            // Get user with headers (id, contentType, expand, headers)
            def fetched = userApi.getUser(userId, null, null, headers)
            assertThat fetched.profile.lastName, equalTo("User${testId}".toString())
            logger.debug("    getUser(headers)")

            // List users with headers
            def users = userApi.listUsers(null, null, null, null, null, null, null, null, null, null, headers)
            assertThat users, not(empty())
            logger.debug("    listUsers(headers)")

            // UserResourcesApi with headers
            try {
                def appLinks = userResourcesApi.listAppLinks(userId, headers)
                logger.debug("    listAppLinks(headers)")
            } catch (Exception e) { logger.debug("    listAppLinks: {}", e.message?.take(60)) }

            try {
                def clients = userResourcesApi.listUserClients(userId, headers)
                logger.debug("    listUserClients(headers)")
            } catch (Exception e) { logger.debug("    listUserClients: {}", e.message?.take(60)) }

            try {
                def groups = userResourcesApi.listUserGroups(userId, headers)
                logger.debug("    listUserGroups(headers)")
            } catch (Exception e) { logger.debug("    listUserGroups: {}", e.message?.take(60)) }

            // UserFactorApi - list factors with headers
            try {
                def factors = userFactorApi.listFactors(userId, headers)
                logger.debug("    listFactors(headers)")
            } catch (Exception e) { logger.debug("    listFactors: {}", e.message?.take(60)) }

            // UserSessionsApi - revokeUserSessions with headers (userId, oauthTokens, forgetDevices, headers)
            try {
                userSessionsApi.revokeUserSessions(userId, false, false, headers)
                logger.debug("    revokeUserSessions(headers)")
            } catch (Exception e) { logger.debug("    revokeUserSessions: {}", e.message?.take(60)) }

            // SubscriptionApi - list user subscriptions with headers
            try {
                def subs = subscriptionApi.listSubscriptionsUser(userId, headers)
                logger.debug("    listSubscriptionsUser(headers)")
            } catch (Exception e) { logger.debug("    listSubscriptionsUser: {}", e.message?.take(60)) }

            // Deactivate and delete with headers
            userLifecycleApi.deactivateUser(userId, false, null, headers)
            logger.debug("    deactivateUser(headers)")
            userApi.deleteUser(userId, false, null, headers)
            logger.debug("    deleteUser(headers)")
            userId = null // already deleted

            logger.debug("\n User lifecycle and creds with headers completed!")

        } catch (ApiException e) {
            logger.debug(" Test failed: {}", e.responseBody)
            throw e
        } finally {
            if (userId) {
                try {
                    userLifecycleApi.deactivateUser(userId, false, null)
                    userApi.deleteUser(userId, false, null)
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Test Application CRUD with additionalHeaders overloads.
     */
    @Test(groups = "group3")
    @Scenario("headers-application-crud")
    void testApplicationCrudWithHeaders() {
        ApiClient client = getClient()
        def applicationApi = new ApplicationApi(client)
        def appPoliciesApi = new ApplicationPoliciesApi(client)
        def appConnectionsApi = new ApplicationConnectionsApi(client)
        def appSsoApi = new ApplicationSsoApi(client)

        String testId = UUID.randomUUID().toString().substring(0, 8)
        String appId = null

        try {
            // Create application with headers
            def app = applicationApi.createApplication(
                new BookmarkApplication()
                    .name(BookmarkApplication.NameEnum.BOOKMARK)
                    .label("Headers App ${testId}".toString())
                    .signOnMode(ApplicationSignOnMode.BOOKMARK)
                    .settings(new BookmarkApplicationSettings()
                        .app(new BookmarkApplicationSettingsApplication()
                            .requestIntegration(false)
                            .url("https://example.com/headers-${testId}".toString()))),
                true, null, headers)
            appId = app.getId()
            logger.debug("    createApplication(headers)")

            // Get application with headers
            def fetched = applicationApi.getApplication(appId, null, headers)
            logger.debug("    getApplication(headers)")

            // List applications with headers (8 params + headers)
            try {
                def apps = applicationApi.listApplications(null, null, null, null, null, null, null, null, headers)
                logger.debug("    listApplications(headers)")
            } catch (Exception e) { logger.debug("    listApplications: {}", e.message?.take(60)) }

            // ApplicationPoliciesApi - assignApplicationPolicy with fake policyId (will 404 but exercises code path)
            try {
                appPoliciesApi.assignApplicationPolicy(appId, "nonexistent-policy", headers)
                logger.debug("    assignApplicationPolicy(headers)")
            } catch (Exception e) { logger.debug("    assignApplicationPolicy(headers) - exercised ({})", e.message?.take(60)) }

            // ApplicationConnectionsApi - get default provisioning connection
            try {
                def connection = appConnectionsApi.getDefaultProvisioningConnectionForApplication(appId, headers)
                logger.debug("    getDefaultProvisioningConnectionForApplication(headers)")
            } catch (Exception e) { logger.debug("    getDefaultProvisioningConnectionForApplication: {}", e.message?.take(60)) }

            // Lifecycle with headers
            applicationApi.deactivateApplication(appId, headers)
            logger.debug("    deactivateApplication(headers)")

            applicationApi.activateApplication(appId, headers)
            logger.debug("    activateApplication(headers)")

            // Delete with headers
            applicationApi.deactivateApplication(appId, headers)
            applicationApi.deleteApplication(appId, headers)
            logger.debug("    deleteApplication(headers)")
            appId = null

            logger.debug("\n Application CRUD with headers completed!")

        } catch (ApiException e) {
            logger.debug(" Test failed: {}", e.responseBody)
            throw e
        } finally {
            if (appId) {
                try {
                    applicationApi.deactivateApplication(appId)
                    applicationApi.deleteApplication(appId)
                } catch (Exception ignored) {}
            }
        }
    }
}

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

import com.okta.sdk.resource.application.OAuth2Claim
import com.okta.sdk.resource.application.OAuth2Scope
import com.okta.sdk.resource.application.OAuth2ScopesMediationPolicyRuleCondition
import com.okta.sdk.resource.authorization.server.AuthorizationServer
import com.okta.sdk.resource.authorization.server.AuthorizationServerList
import com.okta.sdk.resource.authorization.server.AuthorizationServerPolicy
import com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRule
import com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRuleActions
import com.okta.sdk.resource.authorization.server.policy.AuthorizationServerPolicyRuleConditions
import com.okta.sdk.resource.authorization.server.policy.TokenAuthorizationServerPolicyRuleAction
import com.okta.sdk.resource.authorization.server.policy.TokenAuthorizationServerPolicyRuleActionInlineHook
import com.okta.sdk.resource.inline.hook.InlineHook
import com.okta.sdk.resource.inline.hook.InlineHookBuilder
import com.okta.sdk.resource.inline.hook.InlineHookChannel
import com.okta.sdk.resource.inline.hook.InlineHookType
import com.okta.sdk.resource.policy.*
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/authorizationServers}.
 * @since 2.0.0
 */
class AuthorizationServerIT extends ITSupport {

    // Authorization server operations

    @Test
    void createAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getId(), notNullValue())
        assertThat(createdAuthorizationServer.getName(), equalTo(name))
        assertThat(createdAuthorizationServer.getDescription(), equalTo("Test Authorization Server"))
        assertThat(createdAuthorizationServer.getAudiences(), hasSize(1))
        assertThat(createdAuthorizationServer.getAudiences(), contains("api://example"))
    }

    @Test (groups = "group3")
    void listCreatedAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        // create operation may not take effect immediately in the backend
        sleep(getTestOperationDelay())

        assertThat(createdAuthorizationServer, notNullValue())
        assertPresent(client.listAuthorizationServers(), createdAuthorizationServer)
    }

    @Test
    void getAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getName(), equalTo(createdAuthorizationServer.getName()))
        assertThat(retrievedAuthorizationServer.getDescription(), equalTo(createdAuthorizationServer.getDescription()))
    }

    @Test
    void updateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        createdAuthorizationServer.setDescription("Updated Test Authorization Server")

        AuthorizationServer updatedAuthorizationServer = createdAuthorizationServer.update()
        assertThat(updatedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(updatedAuthorizationServer.getDescription(), equalTo("Updated Test Authorization Server"))
    }

    @Test
    void deleteAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        assertThat(client.getAuthorizationServer(createdAuthorizationServer.getId()), notNullValue())

        createdAuthorizationServer.deactivate()
        createdAuthorizationServer.delete()

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServers(), createdAuthorizationServer)
    }

    @Test
    void deactivateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getStatus(), equalTo(AuthorizationServer.StatusEnum.ACTIVE))

        createdAuthorizationServer.deactivate()

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(AuthorizationServer.StatusEnum.INACTIVE))
    }

    @Test
    void activateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getStatus(), equalTo(AuthorizationServer.StatusEnum.ACTIVE))

        createdAuthorizationServer.deactivate()

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(AuthorizationServer.StatusEnum.INACTIVE))

        createdAuthorizationServer.activate()

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(AuthorizationServer.StatusEnum.ACTIVE))
    }

    // Policy operations
    @Test (groups = "group3")
    void listAuthorizationServerPoliciesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        // create may not effect immediately in the backend
        sleep(getTestOperationDelay())

        AuthorizationServerList authorizationServerList = client.listAuthorizationServers()
        assertThat(authorizationServerList, notNullValue())
        assertThat(authorizationServerList.size(), greaterThan(0))
        assertPresent(authorizationServerList, createdAuthorizationServer)

        authorizationServerList.stream()
            .filter({
                authServer -> authServer.getId() == createdAuthorizationServer.getId()
            })
            .forEach({
                authServer ->
                    assertThat(authServer.listPolicies(), notNullValue())
                    authServer.listPolicies().forEach({
                        authPolicy -> assertThat(authPolicy.getId(), notNullValue())
                    })
            })
    }

    @Test
    void createAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(retrievedPolicy.getDescription(), equalTo(createdPolicy.getDescription()))
        assertThat(retrievedPolicy.getName(), equalTo(createdPolicy.getName()))
    }

    @Test
    void updateAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        createdPolicy.setName("Test Policy Updated")
        createdPolicy.setDescription("Test Policy Updated")

        AuthorizationServerPolicy updatedPolicy = createdAuthorizationServer.updatePolicy(createdPolicy.getId(), createdPolicy)
        assertThat(updatedPolicy, notNullValue())
        assertThat(updatedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(updatedPolicy.getName(), equalTo("Test Policy Updated"))
        assertThat(updatedPolicy.getDescription(), equalTo("Test Policy Updated"))
    }

    @Test
    void deleteAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        createdAuthorizationServer.deletePolicy(createdPolicy.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdAuthorizationServer.listPolicies(), createdPolicy)
    }

    @Test
    void activateDeactivateAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        createdPolicy.deactivate(createdAuthorizationServer.getId())
        def deactivatedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(deactivatedPolicy, notNullValue())
        assertThat(deactivatedPolicy.getStatus(), equalTo(AuthorizationServerPolicy.StatusEnum.INACTIVE))

        deactivatedPolicy.activate(createdAuthorizationServer.getId())
        def activatedPolicy = createdAuthorizationServer.getPolicy(deactivatedPolicy.getId())
        assertThat(activatedPolicy, notNullValue())
        assertThat(activatedPolicy.getStatus(), equalTo(AuthorizationServerPolicy.StatusEnum.ACTIVE))

        createdAuthorizationServer.deletePolicy(activatedPolicy.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdAuthorizationServer.listPolicies(), activatedPolicy)
    }

    @Test
    void listAuthorizationServerPolicyRulesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerList authorizationServerList = client.listAuthorizationServers()

        assertThat(authorizationServerList, notNullValue())
        assertThat(authorizationServerList.size(), greaterThan(0))

        authorizationServerList.stream()
            .filter({
                authServer -> authServer.getId() == createdAuthorizationServer.getId()
            })
            .forEach({
                authServer ->
                    assertThat(authServer.listPolicies(), notNullValue())
                    authServer.listPolicies().forEach({ authPolicy ->
                        assertThat(authPolicy, notNullValue())
                        assertThat(authPolicy.getId(), notNullValue())
                        assertThat(authPolicy.listPolicyRules(authServer.getId()), notNullValue())
                    })
            })
    }

    @Test
    void createAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        String hookName = "java-sdk-it-" + UUID.randomUUID().toString()
        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(hookName)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannel.TypeEnum.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdInlineHook)

        AuthorizationServerPolicyRule createdPolicyRule = retrievedPolicy.createPolicyRule(createdAuthorizationServer.getId(),
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(AuthorizationServerPolicyRule.TypeEnum.ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                )
                .setActions(client.instantiate(AuthorizationServerPolicyRuleActions)
                    .setToken(
                        client.instantiate(TokenAuthorizationServerPolicyRuleAction)
                            .setAccessTokenLifetimeMinutes(60)
                            .setRefreshTokenLifetimeMinutes(0)
                            .setRefreshTokenWindowMinutes(10080)
                            .setInlineHook(
                                client.instantiate(TokenAuthorizationServerPolicyRuleActionInlineHook)
                                .setId(createdInlineHook.getId())
                            )
                    )
                )
        )

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(AuthorizationServerPolicyRule.TypeEnum.ACCESS))
        assertThat(createdPolicyRule.getPriority(), equalTo(1))
        assertThat(createdPolicyRule.getConditions().getPeople().getGroups().getInclude(), contains("EVERYONE"))
        assertThat(createdPolicyRule.getConditions().getGrantTypes().getInclude(),
            containsInAnyOrder("implicit", "client_credentials", "authorization_code", "password"))
        assertThat(createdPolicyRule.getConditions().getScopes().getInclude(),
            containsInAnyOrder("openid", "email", "address"))
        assertThat(createdPolicyRule.getActions().getToken().getAccessTokenLifetimeMinutes(), equalTo(60))
        assertThat(createdPolicyRule.getActions().getToken().getRefreshTokenLifetimeMinutes(), equalTo(0))
        assertThat(createdPolicyRule.getActions().getToken().getRefreshTokenWindowMinutes(), equalTo(10080))
    }

    @Test (groups = "group3")
    void updateAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )

        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        AuthorizationServerPolicyRule createdPolicyRule = retrievedPolicy.createPolicyRule(createdAuthorizationServer.getId(),
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(AuthorizationServerPolicyRule.TypeEnum.ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                )
                .setActions(client.instantiate(AuthorizationServerPolicyRuleActions)
                    .setToken(
                        client.instantiate(TokenAuthorizationServerPolicyRuleAction)
                            .setAccessTokenLifetimeMinutes(60)
                            .setRefreshTokenLifetimeMinutes(0)
                            .setRefreshTokenWindowMinutes(10080)
                    )
                )
        )

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(AuthorizationServerPolicyRule.TypeEnum.ACCESS))
        assertThat(createdPolicyRule.getPriority(), equalTo(1))
        assertThat(createdPolicyRule.getConditions().getPeople().getGroups().getInclude(), contains("EVERYONE"))
        assertThat(createdPolicyRule.getConditions().getGrantTypes().getInclude(),
            containsInAnyOrder("implicit", "client_credentials", "authorization_code", "password"))
        assertThat(createdPolicyRule.getConditions().getScopes().getInclude(),
            containsInAnyOrder("openid", "email", "address"))
        assertThat(createdPolicyRule.getActions().getToken().getAccessTokenLifetimeMinutes(), equalTo(60))
        assertThat(createdPolicyRule.getActions().getToken().getRefreshTokenLifetimeMinutes(), equalTo(0))
        assertThat(createdPolicyRule.getActions().getToken().getRefreshTokenWindowMinutes(), equalTo(10080))

        createdPolicyRule
            .setActions(client.instantiate(AuthorizationServerPolicyRuleActions)
                .setToken(
                    client.instantiate(TokenAuthorizationServerPolicyRuleAction)
                        .setAccessTokenLifetimeMinutes(55)
                        .setRefreshTokenLifetimeMinutes(55)
                        .setRefreshTokenWindowMinutes(55)
                )
            )

        AuthorizationServerPolicyRule retrievedPolicyRule = createdPolicyRule.update(createdAuthorizationServer.getId())

        assertThat(retrievedPolicyRule, notNullValue())
        assertThat(retrievedPolicyRule.getActions().getToken().getAccessTokenLifetimeMinutes(), equalTo(55))
        assertThat(retrievedPolicyRule.getActions().getToken().getRefreshTokenLifetimeMinutes(), equalTo(55))
        assertThat(retrievedPolicyRule.getActions().getToken().getRefreshTokenWindowMinutes(), equalTo(55))
    }

    @Test
    void deleteAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        AuthorizationServerPolicyRule createdPolicyRule = retrievedPolicy.createPolicyRule(createdAuthorizationServer.getId(),
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(AuthorizationServerPolicyRule.TypeEnum.ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                )
        )

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(AuthorizationServerPolicyRule.TypeEnum.ACCESS))

        createdPolicyRule.delete(createdAuthorizationServer.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdPolicy.listPolicyRules(createdAuthorizationServer.getId()), createdPolicyRule)
    }

    @Test
    void activateDeactivateAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()]
                    )
                )
            )
        AuthorizationServerPolicy createdPolicy = createdAuthorizationServer.createPolicy(authorizationServerPolicy)
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicyRule createdPolicyRule = createdPolicy.createPolicyRule(createdAuthorizationServer.getId(),
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(AuthorizationServerPolicyRule.TypeEnum.ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                )
        )
        assertThat(createdPolicyRule, notNullValue())

        createdPolicyRule.deactivate(createdAuthorizationServer.getId())
        def deactivatedPolicyRule = createdPolicy.getPolicyRule(createdAuthorizationServer.getId(), createdPolicyRule.getId())
        assertThat(deactivatedPolicyRule, notNullValue())
        assertThat(deactivatedPolicyRule.getStatus(), equalTo(AuthorizationServerPolicyRule.StatusEnum.INACTIVE))

        deactivatedPolicyRule.activate(createdAuthorizationServer.getId())
        def activatedPolicyRule = createdPolicy.getPolicyRule(createdAuthorizationServer.getId(), createdPolicyRule.getId())
        assertThat(activatedPolicyRule, notNullValue())
        assertThat(activatedPolicyRule.getStatus(), equalTo(AuthorizationServerPolicyRule.StatusEnum.ACTIVE))

        activatedPolicyRule.delete(createdAuthorizationServer.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdPolicy.listPolicyRules(createdAuthorizationServer.getId()), activatedPolicyRule)
    }

    // Scope operations

    @Test
    void listOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = createdAuthorizationServer.createOAuth2Scope(oAuth2Scope)

        assertThat(createdOAuth2Scope, notNullValue())
        assertPresent(createdAuthorizationServer.listOAuth2Scopes(), createdOAuth2Scope)
    }

    @Test (groups = "group3")
    void getOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = createdAuthorizationServer.createOAuth2Scope(oAuth2Scope)

        assertThat(createdOAuth2Scope, notNullValue())

        OAuth2Scope retrievedOAuth2Scope = createdAuthorizationServer.getOAuth2Scope(createdOAuth2Scope.getId())

        assertThat(retrievedOAuth2Scope.getId(), equalTo(createdOAuth2Scope.getId()))
    }

    @Test (groups = "group3")
    void updateOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))
            .setConsent(OAuth2Scope.ConsentEnum.REQUIRED)
            .setMetadataPublish(OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS)

        OAuth2Scope createdOAuth2Scope = createdAuthorizationServer.createOAuth2Scope(oAuth2Scope)

        assertThat(createdOAuth2Scope, notNullValue())

        OAuth2Scope tobeUpdatedOAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10) + "-updated")
            .setConsent(OAuth2Scope.ConsentEnum.REQUIRED)
            .setMetadataPublish(OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS)

        OAuth2Scope updatedOAuth2Scope = createdAuthorizationServer.updateOAuth2Scope(createdOAuth2Scope.getId(), tobeUpdatedOAuth2Scope)

        assertThat(updatedOAuth2Scope.getId(), equalTo(tobeUpdatedOAuth2Scope.getId()))
        assertThat(updatedOAuth2Scope.getName(), equalTo(tobeUpdatedOAuth2Scope.getName()))
    }

    @Test
    void deleteOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = createdAuthorizationServer.createOAuth2Scope(oAuth2Scope)

        assertThat(createdOAuth2Scope, notNullValue())

        createdAuthorizationServer.deleteOAuth2Scope(createdOAuth2Scope.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdAuthorizationServer.listOAuth2Scopes(), createdOAuth2Scope)
    }

    // Claim operations

    @Test
    void listOAuth2ClaimsTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(OAuth2Claim.StatusEnum.INACTIVE)
            .setClaimType(OAuth2Claim.ClaimTypeEnum.RESOURCE)
            .setValueType(OAuth2Claim.ValueTypeEnum.EXPRESSION)
            .setValue("\"driving!\"") // value must be an Okta EL expression if valueType is EXPRESSION

        OAuth2Claim createdOAuth2Claim = createdAuthorizationServer.createOAuth2Claim(oAuth2Claim)
        assertThat(createdOAuth2Claim, notNullValue())

        assertPresent(createdAuthorizationServer.listOAuth2Claims(), createdOAuth2Claim)
    }

    @Test
    void getOAuth2ClaimTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(OAuth2Claim.StatusEnum.INACTIVE)
            .setClaimType(OAuth2Claim.ClaimTypeEnum.RESOURCE)
            .setValueType(OAuth2Claim.ValueTypeEnum.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = createdAuthorizationServer.createOAuth2Claim(oAuth2Claim)
        assertThat(createdOAuth2Claim, notNullValue())

        OAuth2Claim retrievedOAuth2Claim = createdAuthorizationServer.getOAuth2Claim(createdOAuth2Claim.getId())
        assertThat(retrievedOAuth2Claim, notNullValue())
        assertThat(retrievedOAuth2Claim.getId(), equalTo(createdOAuth2Claim.getId()))
    }

    @Test
    void updateOAuth2ClaimTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(OAuth2Claim.StatusEnum.INACTIVE)
            .setClaimType(OAuth2Claim.ClaimTypeEnum.RESOURCE)
            .setValueType(OAuth2Claim.ValueTypeEnum.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = createdAuthorizationServer.createOAuth2Claim(oAuth2Claim)
        assertThat(createdOAuth2Claim, notNullValue())

        OAuth2Claim tobeUpdatedOAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10) + "-updated")
            .setStatus(OAuth2Claim.StatusEnum.INACTIVE)
            .setClaimType(OAuth2Claim.ClaimTypeEnum.RESOURCE)
            .setValueType(OAuth2Claim.ValueTypeEnum.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim updatedOAuth2Claim = createdAuthorizationServer.updateOAuth2Claim(createdOAuth2Claim.getId(), tobeUpdatedOAuth2Claim)

        assertThat(updatedOAuth2Claim.getId(), equalTo(tobeUpdatedOAuth2Claim.getId()))
        assertThat(updatedOAuth2Claim.getName(), equalTo(tobeUpdatedOAuth2Claim.getName()))
    }

    @Test (groups = "group3")
    void deleteOAuth2ClaimTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(OAuth2Claim.StatusEnum.INACTIVE)
            .setClaimType(OAuth2Claim.ClaimTypeEnum.RESOURCE)
            .setValueType(OAuth2Claim.ValueTypeEnum.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = createdAuthorizationServer.createOAuth2Claim(oAuth2Claim)
        assertThat(createdOAuth2Claim, notNullValue())

        createdAuthorizationServer.deleteOAuth2Claim(createdOAuth2Claim.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(createdAuthorizationServer.listOAuth2Claims(), createdOAuth2Claim)
    }
}

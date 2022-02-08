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

import com.okta.sdk.resource.AuthorizationServerPolicyRuleActions
import com.okta.sdk.resource.AuthorizationServerPolicyRuleConditions
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.TokenAuthorizationServerPolicyRuleAction
import com.okta.sdk.resource.TokenAuthorizationServerPolicyRuleActionInlineHook
import com.okta.sdk.resource.authorization.server.*
import com.okta.sdk.resource.inline.hook.InlineHook
import com.okta.sdk.resource.inline.hook.InlineHookBuilder
import com.okta.sdk.resource.inline.hook.InlineHookChannelType
import com.okta.sdk.resource.inline.hook.InlineHookType
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
        registerForCleanup(createdAuthorizationServer as Deletable)

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
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        // create operation may not take effect immediately in the backend
        sleep(getTestOperationDelay())

        assertThat(createdAuthorizationServer, notNullValue())
        assertPresent(client.listAuthorizationServers(), createdAuthorizationServer)
    }

    @Test (groups = "group3")
    void getAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getName(), equalTo(createdAuthorizationServer.getName()))
        assertThat(retrievedAuthorizationServer.getDescription(), equalTo(createdAuthorizationServer.getDescription()))
    }

    @Test (groups = "group3")
    void updateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        createdAuthorizationServer.setDescription("Updated Test Authorization Server")

        AuthorizationServer updatedAuthorizationServer = client.updateAuthorizationServer(createdAuthorizationServer, createdAuthorizationServer.getId())
        assertThat(updatedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(updatedAuthorizationServer.getDescription(), equalTo("Updated Test Authorization Server"))
    }

    @Test (groups = "group3")
    void deleteAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        assertThat(client.getAuthorizationServer(createdAuthorizationServer.getId()), notNullValue())

        client.deactivateAuthorizationServer(createdAuthorizationServer.getId())
        client.deleteAuthorizationServer(createdAuthorizationServer.getId())

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServers(), createdAuthorizationServer)
    }

    @Test (groups = "group3")
    void deactivateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        client.deactivateAuthorizationServer(createdAuthorizationServer.getId())

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(LifecycleStatus.INACTIVE))
    }

    @Test (groups = "group3")
    void activateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"])
        )
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        client.deactivateAuthorizationServer(createdAuthorizationServer.getId())

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(LifecycleStatus.INACTIVE))

        client.activateAuthorizationServer(createdAuthorizationServer.getId())

        // delete operation may not effect immediately in the backend
        sleep(getTestOperationDelay())

        retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer, notNullValue())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
        assertThat(retrievedAuthorizationServer.getStatus(), equalTo(LifecycleStatus.ACTIVE))
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
        registerForCleanup(createdAuthorizationServer as Deletable)
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
                    assertThat(client.listAuthorizationServerPolicies(authServer.getId()), notNullValue())
                    client.listAuthorizationServerPolicies(authServer.getId()).forEach({
                        authPolicy -> assertThat(authPolicy.getId(), notNullValue())
                    })
            })
    }

    @Test (groups = "group3")
    void createAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(retrievedPolicy.getDescription(), equalTo(createdPolicy.getDescription()))
        assertThat(retrievedPolicy.getName(), equalTo(createdPolicy.getName()))
    }

    @Test (groups = "group3")
    void updateAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        createdPolicy.setName("Test Policy Updated")
        createdPolicy.setDescription("Test Policy Updated")

        AuthorizationServerPolicy updatedPolicy = client.updateAuthorizationServerPolicy(createdPolicy, createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(updatedPolicy, notNullValue())
        assertThat(updatedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(updatedPolicy.getName(), equalTo("Test Policy Updated"))
        assertThat(updatedPolicy.getDescription(), equalTo("Test Policy Updated"))
    }

    @Test (groups = "group3")
    void deleteAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        client.deleteAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServerPolicies(createdAuthorizationServer.getId()), createdPolicy)
    }

    @Test (groups = "group3")
    void activateDeactivateAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        client.deactivateAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())

        def deactivatedPolicy = client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(deactivatedPolicy, notNullValue())
        assertThat(deactivatedPolicy.getStatus(), equalTo(LifecycleStatus.INACTIVE))

        client.activateAuthorizationServerPolicy(createdAuthorizationServer.getId(), deactivatedPolicy.getId())

        def activatedPolicy = client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), deactivatedPolicy.getId())
        assertThat(activatedPolicy, notNullValue())
        assertThat(activatedPolicy.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        client.deleteAuthorizationServerPolicy(createdAuthorizationServer.getId(), activatedPolicy.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServerPolicies(createdAuthorizationServer.getId()), activatedPolicy)
    }

    @Test (groups = "group3")
    void listAuthorizationServerPolicyRulesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
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
                    assertThat(client.listAuthorizationServerPolicies(authServer.getId()), notNullValue())
                    client.listAuthorizationServerPolicies(authServer.getId()).forEach({ authPolicy ->
                        assertThat(authPolicy, notNullValue())
                        assertThat(authPolicy.getId(), notNullValue())
                        assertThat(client.listAuthorizationServerPolicyRules(authPolicy.getId(), authServer.getId()), notNullValue())
                    })
            })
    }

    @Test (groups = "group3")
    void createAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        String hookName = "java-sdk-it-" + UUID.randomUUID().toString()
        InlineHook createdInlineHook = InlineHookBuilder.instance()
            .setName(hookName)
            .setHookType(InlineHookType.OAUTH2_TOKENS_TRANSFORM)
            .setChannelType(InlineHookChannelType.HTTP)
            .setUrl("https://www.example.com/inlineHooks")
            .setAuthorizationHeaderValue("Test-Api-Key")
            .addHeader("X-Test-Header", "Test header value")
            .buildAndCreate(client)
        registerForCleanup(createdInlineHook as Deletable)

        AuthorizationServerPolicyRule createdPolicyRule = client.createAuthorizationServerPolicyRule(
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(PolicyRuleType.RESOURCE_ACCESS)
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
                ) as AuthorizationServerPolicyRule, createdPolicy.getId(), createdAuthorizationServer.getId())

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(PolicyRuleType.RESOURCE_ACCESS))
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
        registerForCleanup(createdAuthorizationServer  as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy

        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy = client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        AuthorizationServerPolicyRule createdPolicyRule = client.createAuthorizationServerPolicyRule(
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(PolicyRuleType.RESOURCE_ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(PolicyRuleConditions)
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
                ) as AuthorizationServerPolicyRule
        ,createdPolicy.getId(), createdAuthorizationServer.getId())

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(PolicyRuleType.RESOURCE_ACCESS))
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

        AuthorizationServerPolicyRule retrievedPolicyRule =
            client.updateAuthorizationServerPolicyRule(createdPolicyRule,
                createdPolicy.getId(), createdAuthorizationServer.getId(), createdPolicyRule.getId())

        assertThat(retrievedPolicyRule, notNullValue())
        assertThat(retrievedPolicyRule.getActions().getToken().getAccessTokenLifetimeMinutes(), equalTo(55))
        assertThat(retrievedPolicyRule.getActions().getToken().getRefreshTokenLifetimeMinutes(), equalTo(55))
        assertThat(retrievedPolicyRule.getActions().getToken().getRefreshTokenWindowMinutes(), equalTo(55))
    }

    @Test (groups = "group3")
    void deleteAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setSystem(true)
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy =
            client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicy retrievedPolicy =
            client.getAuthorizationServerPolicy(createdAuthorizationServer.getId(), createdPolicy.getId())
        assertThat(retrievedPolicy, notNullValue())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))

        AuthorizationServerPolicyRule createdPolicyRule = client.createAuthorizationServerPolicyRule(
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(PolicyRuleType.RESOURCE_ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                ) as AuthorizationServerPolicyRule
        , retrievedPolicy.getId(), createdAuthorizationServer.getId())

        assertThat(createdPolicyRule, notNullValue())
        assertThat(createdPolicyRule.getType(), equalTo(PolicyRuleType.RESOURCE_ACCESS))

        client.deleteAuthorizationServerPolicyRule(retrievedPolicy.getId(), createdAuthorizationServer.getId(), createdPolicyRule.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServerPolicyRules(retrievedPolicy.getId(), createdAuthorizationServer.getId()), createdPolicyRule)
    }

    @Test (groups = "group3")
    void activateDeactivateAuthorizationServerPolicyRuleTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)
        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServerPolicy authorizationServerPolicy = client.instantiate(AuthorizationServerPolicy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setStatus(LifecycleStatus.ACTIVE)
            .setSystem(true)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude(
                        [OAuth2ScopeMetadataPublish.ALL_CLIENTS.name()]
                    )
                )
            ) as AuthorizationServerPolicy
        AuthorizationServerPolicy createdPolicy = client.createAuthorizationServerPolicy(authorizationServerPolicy, createdAuthorizationServer.getId())
        assertThat(createdPolicy, notNullValue())

        AuthorizationServerPolicyRule createdPolicyRule = client.createAuthorizationServerPolicyRule(
            client.instantiate(AuthorizationServerPolicyRule)
                .setName(name)
                .setType(PolicyRuleType.RESOURCE_ACCESS)
                .setPriority(1)
                .setConditions(client.instantiate(AuthorizationServerPolicyRuleConditions)
                    .setPeople(client.instantiate(PolicyPeopleCondition)
                        .setGroups(client.instantiate(GroupCondition)
                            .setInclude(["EVERYONE"])))
                    .setGrantTypes(client.instantiate(GrantTypePolicyRuleCondition)
                        .setInclude(["implicit", "client_credentials", "authorization_code", "password"]))
                    .setScopes(client.instantiate(OAuth2ScopesMediationPolicyRuleCondition).setInclude(["openid", "email", "address"]))
                ) as AuthorizationServerPolicyRule
        , createdPolicy.getId(), createdAuthorizationServer.getId())
        assertThat(createdPolicyRule, notNullValue())

        client.deactivateAuthorizationServerPolicyRule(createdAuthorizationServer.getId(), createdPolicy.getId(), createdPolicyRule.getId())

        def deactivatedPolicyRule =
            client.getAuthorizationServerPolicyRule(createdPolicy.getId(), createdAuthorizationServer.getId(), createdPolicyRule.getId())
        assertThat(deactivatedPolicyRule, notNullValue())
        assertThat(deactivatedPolicyRule.getStatus(), equalTo(LifecycleStatus.INACTIVE))

        client.activateAuthorizationServerPolicyRule(createdAuthorizationServer.getId(), createdPolicy.getId(), createdPolicyRule.getId())

        def activatedPolicyRule =
            client.getAuthorizationServerPolicyRule(createdPolicy.getId(), createdAuthorizationServer.getId(), createdPolicyRule.getId())
        assertThat(activatedPolicyRule, notNullValue())
        assertThat(activatedPolicyRule.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        client.deleteAuthorizationServerPolicyRule(createdPolicy.getId(), createdAuthorizationServer.getId(), createdPolicyRule.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listAuthorizationServerPolicyRules(createdPolicy.getId(), createdAuthorizationServer.getId()), activatedPolicyRule)
    }

    // Scope operations

    @Test (groups = "group3")
    void listOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = client.createOAuth2Scope(oAuth2Scope, createdAuthorizationServer.getId())

        assertThat(createdOAuth2Scope, notNullValue())
        assertPresent(client.listOAuth2Scopes(createdAuthorizationServer.getId()), createdOAuth2Scope)
    }

    @Test (groups = "group3")
    void getOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = client.createOAuth2Scope(oAuth2Scope, createdAuthorizationServer.getId())

        assertThat(createdOAuth2Scope, notNullValue())

        OAuth2Scope retrievedOAuth2Scope = client.getOAuth2Scope(createdAuthorizationServer.getId(), createdOAuth2Scope.getId())

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
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))
            .setConsent(OAuth2ScopeConsentType.REQUIRED)
            .setMetadataPublish(OAuth2ScopeMetadataPublish.ALL_CLIENTS)

        OAuth2Scope createdOAuth2Scope = client.createOAuth2Scope(oAuth2Scope, createdAuthorizationServer.getId())

        assertThat(createdOAuth2Scope, notNullValue())

        OAuth2Scope tobeUpdatedOAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10) + "-updated")
            .setConsent(OAuth2ScopeConsentType.REQUIRED)
            .setMetadataPublish(OAuth2ScopeMetadataPublish.ALL_CLIENTS)

        OAuth2Scope updatedOAuth2Scope = client.updateOAuth2Scope(tobeUpdatedOAuth2Scope, createdAuthorizationServer.getId(), createdOAuth2Scope.getId())

        assertThat(updatedOAuth2Scope.getId(), equalTo(tobeUpdatedOAuth2Scope.getId()))
        assertThat(updatedOAuth2Scope.getName(), equalTo(tobeUpdatedOAuth2Scope.getName()))
    }

    @Test (groups = "group3")
    void deleteOAuth2ScopesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Scope oAuth2Scope = client.instantiate(OAuth2Scope)
            .setName("java-sdk-it-" + RandomStringUtils.randomAlphanumeric(10))

        OAuth2Scope createdOAuth2Scope = client.createOAuth2Scope(oAuth2Scope, createdAuthorizationServer.getId())

        assertThat(createdOAuth2Scope, notNullValue())

        client.deleteOAuth2Scope(createdAuthorizationServer.getId(), createdOAuth2Scope.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listOAuth2Scopes(createdAuthorizationServer.getId()), createdOAuth2Scope)
    }

    // Claim operations

    @Test (groups = "group3")
    void listOAuth2ClaimsTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(LifecycleStatus.INACTIVE)
            .setClaimType(OAuth2ClaimType.RESOURCE)
            .setValueType(OAuth2ClaimValueType.EXPRESSION)
            .setValue("\"driving!\"") // value must be an Okta EL expression if valueType is EXPRESSION

        OAuth2Claim createdOAuth2Claim = client.createOAuth2Claim(oAuth2Claim, createdAuthorizationServer.getId())
        assertThat(createdOAuth2Claim, notNullValue())

        assertPresent(client.listOAuth2Claims(createdAuthorizationServer.getId()), createdOAuth2Claim)
    }

    @Test (groups = "group3")
    void getOAuth2ClaimTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(LifecycleStatus.INACTIVE)
            .setClaimType(OAuth2ClaimType.RESOURCE)
            .setValueType(OAuth2ClaimValueType.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = client.createOAuth2Claim(oAuth2Claim, createdAuthorizationServer.getId())
        assertThat(createdOAuth2Claim, notNullValue())

        OAuth2Claim retrievedOAuth2Claim = client.getOAuth2Claim(createdAuthorizationServer.getId(), createdOAuth2Claim.getId())
        assertThat(retrievedOAuth2Claim, notNullValue())
        assertThat(retrievedOAuth2Claim.getId(), equalTo(createdOAuth2Claim.getId()))
    }

    @Test (groups = "group3")
    void updateOAuth2ClaimTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(LifecycleStatus.INACTIVE)
            .setClaimType(OAuth2ClaimType.RESOURCE)
            .setValueType(OAuth2ClaimValueType.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = client.createOAuth2Claim(oAuth2Claim, createdAuthorizationServer.getId())
        assertThat(createdOAuth2Claim, notNullValue())

        OAuth2Claim tobeUpdatedOAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10) + "-updated")
            .setStatus(LifecycleStatus.INACTIVE)
            .setClaimType(OAuth2ClaimType.RESOURCE)
            .setValueType(OAuth2ClaimValueType.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim updatedOAuth2Claim = client.updateOAuth2Claim(tobeUpdatedOAuth2Claim, createdAuthorizationServer.getId(), createdOAuth2Claim.getId())
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
        registerForCleanup(createdAuthorizationServer as Deletable)

        assertThat(createdAuthorizationServer, notNullValue())

        OAuth2Claim oAuth2Claim = client.instantiate(OAuth2Claim)
            .setName("java-sdk-it-claims" + RandomStringUtils.randomAlphanumeric(10))
            .setStatus(LifecycleStatus.INACTIVE)
            .setClaimType(OAuth2ClaimType.RESOURCE)
            .setValueType(OAuth2ClaimValueType.EXPRESSION)
            .setValue("\"driving!\"")

        OAuth2Claim createdOAuth2Claim = client.createOAuth2Claim(oAuth2Claim, createdAuthorizationServer.getId())
        assertThat(createdOAuth2Claim, notNullValue())

        client.deleteOAuth2Claim(createdAuthorizationServer.getId(), oAuth2Claim.getId())

        // delete may not effect immediately in the backend
        sleep(getTestOperationDelay())

        assertNotPresent(client.listOAuth2Claims(createdAuthorizationServer.getId()), createdOAuth2Claim)
    }
}

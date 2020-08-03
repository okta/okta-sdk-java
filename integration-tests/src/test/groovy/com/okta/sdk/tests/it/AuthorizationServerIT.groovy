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

import com.okta.sdk.resource.policy.ClientPolicyCondition
import com.okta.sdk.resource.application.OAuth2Claim
import com.okta.sdk.resource.application.OAuth2Scope
import com.okta.sdk.resource.authorization.server.AuthorizationServer
import com.okta.sdk.resource.policy.Policy
import com.okta.sdk.resource.policy.PolicyRuleConditions
import com.okta.sdk.resource.policy.PolicyType
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test
import wiremock.org.apache.commons.lang3.RandomStringUtils

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.assertNotPresent

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
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())
        assertThat(createdAuthorizationServer.getId(), notNullValue())
        assertThat(createdAuthorizationServer.getName(), equalTo(name))
        assertThat(createdAuthorizationServer.getDescription(), equalTo("Test Authorization Server"))
        assertThat(createdAuthorizationServer.getAudiences(), hasSize(1))
        assertThat(createdAuthorizationServer.getAudiences(), contains("api://example"))
    }

    @Test
    void listCreatedAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

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
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        AuthorizationServer retrievedAuthorizationServer = client.getAuthorizationServer(createdAuthorizationServer.getId())
        assertThat(retrievedAuthorizationServer.getId(), equalTo(createdAuthorizationServer.getId()))
    }

    @Test
    void updateAuthorizationServerTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
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
        sleep(getTestDelay())

        assertNotPresent(client.listAuthorizationServers(), createdAuthorizationServer)
    }

    // Policy operations

    @Test
    void listAuthorizationServerPoliciesTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy policy = client.instantiate(Policy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude([OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()])))

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        Policy createdPolicy = createdAuthorizationServer.createPolicy(policy)
        registerForCleanup(createdPolicy)

        assertPresent(createdAuthorizationServer.listPolicies(), createdPolicy)
    }

    @Test
    void getAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy policy = client.instantiate(Policy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude([OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()])))
        registerForCleanup(policy)

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        Policy createdPolicy = createdAuthorizationServer.createPolicy(policy)
        registerForCleanup(createdPolicy)

        Policy retrievedPolicy = createdAuthorizationServer.getPolicy(createdPolicy.getId())
        assertThat(retrievedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(retrievedPolicy.getDescription(), equalTo(createdPolicy.getDescription()))
        assertThat(retrievedPolicy.getName(), equalTo(createdPolicy.getName()))
    }

    @Test
    void updateAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy policy = client.instantiate(Policy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition)
                    .setInclude([OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()])))
        registerForCleanup(policy)

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        Policy createdPolicy = createdAuthorizationServer.createPolicy(policy)
        registerForCleanup(createdPolicy)

        assertThat(createdPolicy, notNullValue())

        createdPolicy.setName("Test Policy Updated")
        createdPolicy.setDescription("Test Policy Updated")

        Policy updatedPolicy = createdAuthorizationServer.updatePolicy(createdPolicy.getId(), createdPolicy)
        assertThat(updatedPolicy, notNullValue())
        assertThat(updatedPolicy.getId(), equalTo(createdPolicy.getId()))
        assertThat(updatedPolicy.getName(), equalTo(createdPolicy.getName()))
        assertThat(updatedPolicy.getDescription(), equalTo(createdPolicy.getDescription()))
    }

    @Test
    void deleteAuthorizationServerPolicyTest() {
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        Policy policy = client.instantiate(Policy)
            .setType(PolicyType.OAUTH_AUTHORIZATION_POLICY)
            .setName("Test Policy")
            .setDescription("Test Policy")
            .setPriority(1)
            .setConditions(client.instantiate(PolicyRuleConditions)
                .setClients(client.instantiate(ClientPolicyCondition).setInclude([OAuth2Scope.MetadataPublishEnum.ALL_CLIENTS.name()])))
        registerForCleanup(policy)

        AuthorizationServer createdAuthorizationServer = client.createAuthorizationServer(
            client.instantiate(AuthorizationServer)
                .setName(name)
                .setDescription("Test Authorization Server")
                .setAudiences(["api://example"]))
        registerForCleanup(createdAuthorizationServer)

        assertThat(createdAuthorizationServer, notNullValue())

        Policy createdPolicy = createdAuthorizationServer.createPolicy(policy)
        registerForCleanup(createdPolicy)

        assertThat(createdPolicy, notNullValue())

        createdAuthorizationServer.deletePolicy(createdPolicy.getId())

        // delete may not effect immediately in the backend
        sleep(getTestDelay())

        assertNotPresent(createdAuthorizationServer.listPolicies(), createdPolicy)
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

    @Test
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

    @Test
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
        sleep(getTestDelay())

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

    @Test
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
        sleep(getTestDelay())

        assertNotPresent(createdAuthorizationServer.listOAuth2Claims(), createdOAuth2Claim)
    }
}

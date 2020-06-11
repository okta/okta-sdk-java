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

import com.okta.sdk.resource.*
import com.okta.sdk.resource.identity.provider.IdentityProvider
import com.okta.sdk.resource.policy.IdentityProviderPolicy
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/idps}.
 * @since 2.0.0
 */
class IdpIT extends ITSupport {

    @Test
    void idpLifecycleTest() {

        // create idp
        IdentityProvider createdIdp = client.createIdentityProvider(client.instantiate(IdentityProvider)
            .setType(IdentityProvider.TypeEnum.OIDC)
            .setName("Mock OpenID Connect Id")
            .setIssuerMode(IdentityProvider.IssuerModeEnum.ORG_URL)
            .setProtocol(client.instantiate(Protocol)
                .setAlgorithms(client.instantiate(ProtocolAlgorithms)
                    .setRequest(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)))
                    .setResponse(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignature.ScopeEnum.ANY))))
                .setEndpoints(client.instantiate(ProtocolEndpoints)
                    .setAcs(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.POST)
                        .setType(ProtocolEndpoint.TypeEnum.INSTANCE))
                    .setAuthorization(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/authorize"))
                    .setToken(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.POST)
                        .setUrl("https://idp.example.com/token"))
                    .setUserInfo(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/userinfo"))
                    .setJwks(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/keys")))
                .setScopes(["openid", "profile", "email"])
                .setType(Protocol.TypeEnum.OIDC)
                .setCredentials(client.instantiate(IdentityProviderCredentials)
                    .setClient(client.instantiate(IdentityProviderCredentialsClient)
                        .setClientId("your-client-id")
                        .setClientSecret("your-client-secret")))
                .setIssuer(client.instantiate(ProtocolEndpoint)
                    .setUrl("https://idp.example.com")))
            .setPolicy(client.instantiate(IdentityProviderPolicy)
                .setAccountLink(client.instantiate(PolicyAccountLink)
                    .setAction(PolicyAccountLink.ActionEnum.AUTO)
                    .setFilter(null))
                .setProvisioning(client.instantiate(Provisioning)
                    .setAction(Provisioning.ActionEnum.AUTO)
                    .setConditions(client.instantiate(ProvisioningConditions)
                        .setDeprovisioned(client.instantiate(ProvisioningDeprovisionedCondition)
                            .setAction(ProvisioningDeprovisionedCondition.ActionEnum.NONE))
                        .setSuspended(client.instantiate(ProvisioningSuspendedCondition)
                            .setAction(ProvisioningSuspendedCondition.ActionEnum.NONE)))
                    .setGroups(client.instantiate(ProvisioningGroups)
                        .setAction(ProvisioningGroups.ActionEnum.NONE)))
                .setMaxClockSkew(120000)
                .setSubject(client.instantiate(PolicySubject)
                    .setUserNameTemplate(client.instantiate(PolicyUserNameTemplate)
                        .setTemplate("idpuser.email"))
                    .setMatchType(PolicySubjectMatchType.USERNAME))))
        registerForCleanup(createdIdp)

        assertThat(createdIdp, notNullValue())
        assertThat(createdIdp.getId(), notNullValue())
        assertThat(createdIdp.getStatus(), equalTo(IdentityProvider.StatusEnum.ACTIVE))

        // get
        IdentityProvider retrievedIdp = client.getIdentityProvider(createdIdp.getId())
        assertThat(retrievedIdp.getId(), equalTo(createdIdp.getId()))

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        // earlier operation may not complete immediately, so sleep for 2s
        sleep(2000)

        expect(ResourceException) {
            client.getIdentityProvider(createdIdp.getId())
        }

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test
    void idpUserTest() {

        // create user
        def email = "joe.coder+${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .buildAndCreate(client)
        registerForCleanup(createdUser)

        // create idp
        IdentityProvider createdIdp = client.createIdentityProvider(client.instantiate(IdentityProvider)
            .setType(IdentityProvider.TypeEnum.OIDC)
            .setName("Mock OpenID Connect Id")
            .setIssuerMode(IdentityProvider.IssuerModeEnum.ORG_URL)
            .setProtocol(client.instantiate(Protocol)
                .setAlgorithms(client.instantiate(ProtocolAlgorithms)
                    .setRequest(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)))
                    .setResponse(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignature.ScopeEnum.ANY))))
                .setEndpoints(client.instantiate(ProtocolEndpoints)
                    .setAcs(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.POST)
                        .setType(ProtocolEndpoint.TypeEnum.INSTANCE))
                    .setAuthorization(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/authorize"))
                    .setToken(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.POST)
                        .setUrl("https://idp.example.com/token"))
                    .setUserInfo(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/userinfo"))
                    .setJwks(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/keys")))
                .setScopes(["openid", "profile", "email"])
                .setType(Protocol.TypeEnum.OIDC)
                .setCredentials(client.instantiate(IdentityProviderCredentials)
                    .setClient(client.instantiate(IdentityProviderCredentialsClient)
                        .setClientId("your-client-id")
                        .setClientSecret("your-client-secret")))
                .setIssuer(client.instantiate(ProtocolEndpoint)
                    .setUrl("https://idp.example.com")))
            .setPolicy(client.instantiate(IdentityProviderPolicy)
                .setAccountLink(client.instantiate(PolicyAccountLink)
                    .setAction(PolicyAccountLink.ActionEnum.AUTO)
                    .setFilter(null))
                .setProvisioning(client.instantiate(Provisioning)
                    .setAction(Provisioning.ActionEnum.AUTO)
                    .setConditions(client.instantiate(ProvisioningConditions)
                        .setDeprovisioned(client.instantiate(ProvisioningDeprovisionedCondition)
                            .setAction(ProvisioningDeprovisionedCondition.ActionEnum.NONE))
                        .setSuspended(client.instantiate(ProvisioningSuspendedCondition)
                            .setAction(ProvisioningSuspendedCondition.ActionEnum.NONE)))
                    .setGroups(client.instantiate(ProvisioningGroups)
                        .setAction(ProvisioningGroups.ActionEnum.NONE)))
                .setMaxClockSkew(120000)
                .setSubject(client.instantiate(PolicySubject)
                    .setUserNameTemplate(client.instantiate(PolicyUserNameTemplate)
                        .setTemplate("idpuser.email"))
                    .setMatchType(PolicySubjectMatchType.USERNAME))))
        registerForCleanup(createdIdp)

        // list linked idp users
        assertThat(createdIdp.listUsers(), iterableWithSize(0))

        // link user
        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"))

        assertThat(createdIdp.listUsers(), iterableWithSize(1))
        assertPresent(createdIdp.listUsers(), idpAppUser)

        // unlink user
        createdIdp.unlinkUser(createdUser.getId())

        // list linked idp users
        assertThat(createdIdp.listUsers(), iterableWithSize(0))

        // list social auth tokens
        SocialAuthTokenList socialAuthTokenList = createdIdp.listSocialAuthTokens(createdUser.getId())
        assertThat(socialAuthTokenList, iterableWithSize(0))

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()
    }
}

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

import com.okta.sdk.resource.identity.provider.*
import com.okta.sdk.resource.policy.*
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
    void oidcIdpLifecycleTest() {

        // create idp
        IdentityProvider createdIdp = IdentityProviderBuilders.oidc()
            .setName("Mock OpenID Connect Id")
            .setIssuerMode(IdentityProvider.IssuerModeEnum.ORG_URL)
            .setRequestSignatureAlgorithm("SHA-256")
            .setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)
            .setResponseSignatureAlgorithm("SHA-256")
            .setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.ANY)
            .setAcsEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
            .setAcsEndpointType(ProtocolEndpoint.TypeEnum.INSTANCE)
            .setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setAuthorizationEndpointUrl("https://idp.example.com/authorize")
            .setTokenEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
            .setTokenEndpointUrl("https://idp.example.com/token")
            .setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setUserInfoEndpointUrl("https://idp.example.com/userinfo")
            .setJwksEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setJwksEndpointUrl("https://idp.example.com/keys")
            .setScopes(["openid", "profile", "email"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .setIssuerUrl("https://idp.example.com")
            .setMaxClockSkew(120000)
            .setUserName("idpuser.email")
            .setMatchType(PolicySubjectMatchType.USERNAME)
            .buildAndCreate(client)
        registerForCleanup(createdIdp)

        assertThat(createdIdp, notNullValue())
        assertThat(createdIdp.getId(), notNullValue())
        assertThat(createdIdp.getStatus(), equalTo(IdentityProvider.StatusEnum.ACTIVE))

        // get
        IdentityProvider retrievedIdp = client.getIdentityProvider(createdIdp.getId())
        assertThat(retrievedIdp.getId(), equalTo(createdIdp.getId()))

        // update
        IdentityProvider updatedIdp = createdIdp.update(client.instantiate(IdentityProvider)
            .setType(IdentityProvider.TypeEnum.OIDC)
            .setName("Mock OpenID Connect Id - NEW")
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
                        .setUrl("https://idp.example.com/authorize_new"))
                    .setToken(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.POST)
                        .setUrl("https://idp.example.com/token_new"))
                    .setUserInfo(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/userinfo_new"))
                    .setJwks(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
                        .setUrl("https://idp.example.com/keys_new")))
                .setScopes(["openid", "profile", "email"])
                .setType(Protocol.TypeEnum.OIDC)
                .setCredentials(client.instantiate(IdentityProviderCredentials)
                    .setClient(client.instantiate(IdentityProviderCredentialsClient)
                        .setClientId("your-new-client-id")
                        .setClientSecret("your-new-client-secret")))
                .setIssuer(client.instantiate(ProtocolEndpoint)
                    .setUrl("https://idp.example.com/new")))
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
        registerForCleanup(updatedIdp)

        IdentityProvider retrievedUpdatedIdp = client.getIdentityProvider(updatedIdp.getId())
        assertThat(retrievedUpdatedIdp.getId(), equalTo(createdIdp.getId()))
        assertThat(retrievedUpdatedIdp.getName(), equalTo("Mock OpenID Connect Id - NEW"))
        assertThat(retrievedUpdatedIdp.getProtocol().getEndpoints().getAuthorization().getUrl(),
            equalTo("https://idp.example.com/authorize_new"))
        assertThat(retrievedUpdatedIdp.getProtocol().getEndpoints().getToken().getUrl(),
            equalTo("https://idp.example.com/token_new"))
        assertThat(retrievedUpdatedIdp.getProtocol().getEndpoints().getUserInfo().getUrl(),
            equalTo("https://idp.example.com/userinfo_new"))
        assertThat(retrievedUpdatedIdp.getProtocol().getEndpoints().getJwks().getUrl(),
            equalTo("https://idp.example.com/keys_new"))
        assertThat(retrievedUpdatedIdp.getProtocol().getCredentials().getClient().getClientId(),
            equalTo("your-new-client-id"))
        assertThat(retrievedUpdatedIdp.getProtocol().getCredentials().getClient().getClientSecret(),
            equalTo("your-new-client-secret"))
        assertThat(retrievedUpdatedIdp.getProtocol().getIssuer().getUrl(),
            equalTo("https://idp.example.com/new"))

        assertPresent(client.listIdentityProviders(), updatedIdp)

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        // earlier operation may not complete immediately
        sleep(2000)

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test
    void oidcIdpUserTest() {

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
        IdentityProvider createdIdp = IdentityProviderBuilders.oidc()
            .setName("Mock OpenID Connect Id")
            .setIssuerMode(IdentityProvider.IssuerModeEnum.ORG_URL)
            .setRequestSignatureAlgorithm("SHA-256")
            .setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)
            .setResponseSignatureAlgorithm("SHA-256")
            .setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.ANY)
            .setAcsEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
            .setAcsEndpointType(ProtocolEndpoint.TypeEnum.INSTANCE)
            .setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setAuthorizationEndpointUrl("https://idp.example.com/authorize")
            .setTokenEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
            .setTokenEndpointUrl("https://idp.example.com/token")
            .setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setUserInfoEndpointUrl("https://idp.example.com/userinfo")
            .setJwksEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
            .setJwksEndpointUrl("https://idp.example.com/keys")
            .setScopes(["openid", "profile", "email"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .setIssuerUrl("https://idp.example.com")
            .setMaxClockSkew(120000)
            .setUserName("idpuser.email")
            .setMatchType(PolicySubjectMatchType.USERNAME)
            .buildAndCreate(client)
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

    @Test
    void googleIdpTest() {

        // create user
        def email = "joe.coder+${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .buildAndCreate(client)
        registerForCleanup(createdUser)

        // create Google idp
        IdentityProvider createdIdp = IdentityProviderBuilders.google()
            .setName("Mock Google IdP")
            .setScopes(["openid", "profile", "email"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .isProfileMaster(true)
            .setMaxClockSkew(120000)
            .setUserName("idpuser.email")
            .setMatchType(PolicySubjectMatchType.USERNAME)
            .buildAndCreate(client)
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

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test
    void facebookIdpTest() {

        // create user
        def email = "joe.coder+${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .buildAndCreate(client)
        registerForCleanup(createdUser)

        // create Facebook idp
        IdentityProvider createdIdp = IdentityProviderBuilders.facebook()
            .setName("Mock Facebook IdP")
            .setScopes(["public_profile", "email"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .isProfileMaster(true)
            .setMaxClockSkew(120000)
            .setUserName("idpuser.email")
            .setMatchType(PolicySubjectMatchType.USERNAME)
            .buildAndCreate(client)
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

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test
    void microsoftIdpTest() {

        // create user
        def email = "joe.coder+${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .buildAndCreate(client)
        registerForCleanup(createdUser)

        // create Microsoft idp
        IdentityProvider createdIdp = IdentityProviderBuilders.microsoft()
            .setName("Mock Microsoft IdP")
            .setScopes(["openid", "email", "profile", "https://graph.microsoft.com/User.Read"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .isProfileMaster(true)
            .setMaxClockSkew(120000)
            .setUserName("idpuser.userPrincipalName")
            .setMatchType(PolicySubjectMatchType.USERNAME)
            .buildAndCreate(client)
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

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test
    void linkedInIdpTest() {

        // create user
        def email = "joe.coder+${uniqueTestName}@example.com"
        User createdUser = UserBuilder.instance()
            .setEmail(email)
            .setFirstName("Joe")
            .setLastName("Code")
            .setPassword("Password1".toCharArray())
            .buildAndCreate(client)
        registerForCleanup(createdUser)

        // create Linkedin idp
        IdentityProvider createdIdp = IdentityProviderBuilders.linkedin()
            .setName("Mock LinkedIn IdP")
            .setScopes(["r_basicprofile", "r_emailaddress"])
            .setClientId("your-client-id")
            .setClientSecret("your-client-secret")
            .isProfileMaster(true)
            .setMaxClockSkew(120000)
            .setUserName("idpuser.email")
            .setMatchType(PolicySubjectMatchType.EMAIL)
            .buildAndCreate(client)
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

        // deactivate
        createdIdp.deactivate()

        // delete
        createdIdp.delete()

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }
}

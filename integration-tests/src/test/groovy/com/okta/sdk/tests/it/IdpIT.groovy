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


import com.okta.sdk.resource.PolicyAccountLink
import com.okta.sdk.resource.PolicyAccountLinkAction
import com.okta.sdk.resource.PolicySubject
import com.okta.sdk.resource.PolicySubjectMatchType
import com.okta.sdk.resource.PolicyUserNameTemplate
import com.okta.sdk.resource.Provisioning
import com.okta.sdk.resource.ProvisioningAction
import com.okta.sdk.resource.ProvisioningConditions
import com.okta.sdk.resource.ProvisioningDeprovisionedAction
import com.okta.sdk.resource.ProvisioningDeprovisionedCondition
import com.okta.sdk.resource.ProvisioningGroups
import com.okta.sdk.resource.ProvisioningGroupsAction
import com.okta.sdk.resource.ProvisioningSuspendedAction
import com.okta.sdk.resource.ProvisioningSuspendedCondition
import com.okta.sdk.resource.authorization.server.IssuerMode
import com.okta.sdk.resource.authorization.server.LifecycleStatus
import com.okta.sdk.resource.group.User
import com.okta.sdk.resource.identity.provider.*
import com.okta.sdk.resource.user.UserBuilder
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/idps}.
 * @since 2.0.0
 */
class IdpIT extends ITSupport {

    @Test (groups = "group2")
    void oidcIdpLifecycleTest() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        // create idp
        IdentityProvider createdIdp = IdentityProviderBuilders.oidc()
            .setName(name)
            .setIssuerMode(IssuerMode.ORG_URL)
            .setRequestSignatureAlgorithm("SHA-256")
            .setRequestSignatureScope(ProtocolAlgorithmTypeSignatureScope.REQUEST)
            .setResponseSignatureAlgorithm("SHA-256")
            .setResponseSignatureScope(ProtocolAlgorithmTypeSignatureScope.ANY)
            .setAcsEndpointBinding(ProtocolEndpointBinding.POST)
            .setAcsEndpointType(ProtocolEndpointType.INSTANCE)
            .setAuthorizationEndpointBinding(ProtocolEndpointBinding.REDIRECT)
            .setAuthorizationEndpointUrl("https://idp.example.com/authorize")
            .setTokenEndpointBinding(ProtocolEndpointBinding.POST)
            .setTokenEndpointUrl("https://idp.example.com/token")
            .setUserInfoEndpointBinding(ProtocolEndpointBinding.REDIRECT)
            .setUserInfoEndpointUrl("https://idp.example.com/userinfo")
            .setJwksEndpointBinding(ProtocolEndpointBinding.REDIRECT)
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
        assertThat(createdIdp.getName(), equalTo(name))
        assertThat(createdIdp.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        // get
        IdentityProvider retrievedIdp = client.getIdentityProvider(createdIdp.getId())
        assertThat(retrievedIdp.getId(), equalTo(createdIdp.getId()))

        // update
        String newName = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider updatedIdp = client.updateIdentityProvider(client.instantiate(IdentityProvider)
            .setType(IdentityProvider.TypeValues.OIDC)
            .setName(newName)
            .setIssuerMode(IssuerMode.ORG_URL)
            .setProtocol(client.instantiate(Protocol)
                .setAlgorithms(client.instantiate(ProtocolAlgorithms)
                    .setRequest(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignatureScope.REQUEST)))
                    .setResponse(client.instantiate(ProtocolAlgorithmType)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature)
                            .setAlgorithm("SHA-256")
                            .setScope(ProtocolAlgorithmTypeSignatureScope.ANY))))
                .setEndpoints(client.instantiate(ProtocolEndpoints)
                    .setAcs(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpointBinding.POST)
                        .setType(ProtocolEndpointType.INSTANCE))
                    .setAuthorization(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpointBinding.REDIRECT)
                        .setUrl("https://idp.example.com/authorize_new"))
                    .setToken(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpointBinding.POST)
                        .setUrl("https://idp.example.com/token_new"))
                    .setUserInfo(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpointBinding.REDIRECT)
                        .setUrl("https://idp.example.com/userinfo_new"))
                    .setJwks(client.instantiate(ProtocolEndpoint)
                        .setBinding(ProtocolEndpointBinding.REDIRECT)
                        .setUrl("https://idp.example.com/keys_new")))
                .setScopes(["openid", "profile", "email"])
                .setType(ProtocolType.OIDC)
                .setCredentials(client.instantiate(IdentityProviderCredentials)
                    .setClient(client.instantiate(IdentityProviderCredentialsClient)
                        .setClientId("your-new-client-id")
                        .setClientSecret("your-new-client-secret")))
                .setIssuer(client.instantiate(ProtocolEndpoint)
                    .setUrl("https://idp.example.com/new")))
            .setPolicy(client.instantiate(IdentityProviderPolicy)
                .setAccountLink(client.instantiate(PolicyAccountLink)
                    .setAction(PolicyAccountLinkAction.AUTO)
                    .setFilter(null))
                .setProvisioning(client.instantiate(Provisioning)
                    .setAction(ProvisioningAction.AUTO)
                    .setConditions(client.instantiate(ProvisioningConditions)
                        .setDeprovisioned(client.instantiate(ProvisioningDeprovisionedCondition)
                            .setAction(ProvisioningDeprovisionedAction.NONE))
                        .setSuspended(client.instantiate(ProvisioningSuspendedCondition)
                            .setAction(ProvisioningSuspendedAction.NONE)))
                    .setGroups(client.instantiate(ProvisioningGroups)
                        .setAction(ProvisioningGroupsAction.NONE)))
                .setMaxClockSkew(120000)
                .setSubject(client.instantiate(PolicySubject)
                    .setUserNameTemplate(client.instantiate(PolicyUserNameTemplate)
                        .setTemplate("idpuser.email"))
                    .setMatchType(PolicySubjectMatchType.USERNAME))), createdIdp.getId())
        registerForCleanup(updatedIdp)

        IdentityProvider retrievedUpdatedIdp = client.getIdentityProvider(updatedIdp.getId())
        assertThat(retrievedUpdatedIdp.getId(), equalTo(createdIdp.getId()))
        assertThat(retrievedUpdatedIdp.getName(), equalTo(newName))
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
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())

        // earlier operation may not complete immediately
        sleep(getTestOperationDelay())

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test (groups = "group2")
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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.oidc()
            .setName(name)
            .setIssuerMode(IssuerMode.ORG_URL)
            .setRequestSignatureAlgorithm("SHA-256")
            .setRequestSignatureScope(ProtocolAlgorithmTypeSignatureScope.REQUEST)
            .setResponseSignatureAlgorithm("SHA-256")
            .setResponseSignatureScope(ProtocolAlgorithmTypeSignatureScope.ANY)
            .setAcsEndpointBinding(ProtocolEndpointBinding.POST)
            .setAcsEndpointType(ProtocolEndpointType.INSTANCE)
            .setAuthorizationEndpointBinding(ProtocolEndpointBinding.REDIRECT)
            .setAuthorizationEndpointUrl("https://idp.example.com/authorize")
            .setTokenEndpointBinding(ProtocolEndpointBinding.POST)
            .setTokenEndpointUrl("https://idp.example.com/token")
            .setUserInfoEndpointBinding(ProtocolEndpointBinding.REDIRECT)
            .setUserInfoEndpointUrl("https://idp.example.com/userinfo")
            .setJwksEndpointBinding(ProtocolEndpointBinding.REDIRECT)
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
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // link user
        IdentityProviderApplicationUser idpAppUser = client.linkUserToIdentityProvider(
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"), createdIdp.getId(), createdUser.getId())

        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(1))
        assertPresent(client.listIdentityProviderApplicationUsers(createdIdp.getId()), idpAppUser)

        // unlink user
        client.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // list social auth tokens
        SocialAuthTokenList socialAuthTokenList = client.listSocialAuthTokens(createdIdp.getId(), createdUser.getId())
        assertThat(socialAuthTokenList, iterableWithSize(0))

        // deactivate
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2")
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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.google()
            .setName(name)
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
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // link user
        IdentityProviderApplicationUser idpAppUser = client.linkUserToIdentityProvider(
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"), createdIdp.getId(), createdUser.getId())

        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(1))
        assertPresent(client.listIdentityProviderApplicationUsers(createdIdp.getId()), idpAppUser)

        // unlink user
        client.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // deactivate
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test (groups = "group2")
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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.facebook()
            .setName(name)
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
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // link user
        IdentityProviderApplicationUser idpAppUser = client.linkUserToIdentityProvider(
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"), createdIdp.getId(), createdUser.getId())

        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(1))
        assertPresent(client.listIdentityProviderApplicationUsers(createdIdp.getId()), idpAppUser)

        // unlink user
        client.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // deactivate
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    // Remove this groups tag after OKTA-329987 is resolved (Adding this tag disables the test in bacon PDV)
    @Test (groups = "bacon")
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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.microsoft()
            .setName(name)
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
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // link user
        IdentityProviderApplicationUser idpAppUser = client.linkUserToIdentityProvider(
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"), createdIdp.getId(), createdUser.getId())

        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(1))
        assertPresent(client.listIdentityProviderApplicationUsers(createdIdp.getId()), idpAppUser)

        // unlink user
        client.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // deactivate
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test (groups = "group2")
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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.linkedin()
            .setName(name)
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
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        IdentityProviderApplicationUser idpAppUser = client.linkUserToIdentityProvider(
            client.instantiate(UserIdentityProviderLinkRequest)
                .setExternalId("externalId"), createdIdp.getId(), createdUser.getId())

        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(1))
        assertPresent(client.listIdentityProviderApplicationUsers(createdIdp.getId()), idpAppUser)

        // unlink user
        client.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(client.listIdentityProviderApplicationUsers(createdIdp.getId()), iterableWithSize(0))

        // deactivate
        client.deactivateIdentityProvider(createdIdp.getId())

        // delete
        client.deleteIdentityProvider(createdIdp.getId())

        assertNotPresent(client.listIdentityProviders(), createdIdp)
    }

    @Test (groups = "group2")
    void idpWithStringTypeTest() {

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
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider createdIdp = IdentityProviderBuilders.ofType("GOOGLE")
            .setName(name)
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

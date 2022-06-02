/*
 * Copyright 2020-Present Okta, Inc.
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

import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.api.IdentityProviderApi
import org.openapitools.client.model.*
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/idps}.
 * @since 2.0.0
 */
class IdpIT extends ITSupport {

    @Test (groups = "group2")
    void oidcIdpLifecycleTest() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        // create
        IdentityProviderApi identityProviderApi = new IdentityProviderApi(getClient())
        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType("OIDC")
        idp.setIssuerMode(IssuerMode.ORG_URL)
        Protocol protocol = new Protocol()

        ProtocolAlgorithmType protocolAlgorithmTypeReq = new ProtocolAlgorithmType()
        ProtocolAlgorithmType protocolAlgorithmTypeRes = new ProtocolAlgorithmType()
        ProtocolAlgorithmTypeSignature protocolAlgorithmTypeSignature_Req = new ProtocolAlgorithmTypeSignature()
        protocolAlgorithmTypeSignature_Req.setScope(ProtocolAlgorithmTypeSignatureScope.REQUEST)
        protocolAlgorithmTypeSignature_Req.setAlgorithm("SHA-256")
        protocolAlgorithmTypeReq.setSignature(protocolAlgorithmTypeSignature_Req)
        ProtocolAlgorithmTypeSignature protocolAlgorithmTypeSignature_Res = new ProtocolAlgorithmTypeSignature()
        protocolAlgorithmTypeSignature_Res.setScope(ProtocolAlgorithmTypeSignatureScope.RESPONSE)
        protocolAlgorithmTypeSignature_Res.setAlgorithm("SHA-256")
        protocolAlgorithmTypeRes.setSignature(protocolAlgorithmTypeSignature_Res)

        ProtocolAlgorithms protocolAlgorithms = new ProtocolAlgorithms()
        protocolAlgorithms.setRequest(protocolAlgorithmTypeReq)
        protocolAlgorithms.setResponse(protocolAlgorithmTypeRes)
        protocol.setAlgorithms(protocolAlgorithms)
        List<String> scopes = new ArrayList<>()
        scopes.add("openid")
        scopes.add("email")
        protocol.setScopes(scopes)
        protocol.setType(ProtocolType.OIDC)

        ProtocolEndpoint protocolEndpointIssuer = new ProtocolEndpoint()
        protocolEndpointIssuer.setUrl("https://idp.example.com")
        protocol.setIssuer(protocolEndpointIssuer)
        IdentityProviderCredentials identityProviderCredentials = new IdentityProviderCredentials()
        IdentityProviderCredentialsClient identityProviderCredentialsClient = new IdentityProviderCredentialsClient()
        identityProviderCredentialsClient.setClientId("your-client-id")
        identityProviderCredentialsClient.setClientSecret("your-client-secret")
        identityProviderCredentials.setClient(identityProviderCredentialsClient)
        protocol.setCredentials(identityProviderCredentials)

        ProtocolEndpoints protocolEndpoints = new ProtocolEndpoints()
        ProtocolEndpoint protocolEndpointAcs = new ProtocolEndpoint()
        protocolEndpointAcs.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointAcs.setType(ProtocolEndpointType.INSTANCE)

        ProtocolEndpoint protocolEndpointAuthorization = new ProtocolEndpoint()
        protocolEndpointAuthorization.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointAuthorization.setUrl("https://idp.example.com/authorize")

        ProtocolEndpoint protocolEndpointToken = new ProtocolEndpoint()
        protocolEndpointToken.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointToken.setUrl("https://idp.example.com/token")

        ProtocolEndpoint protocolEndpointUserInfo = new ProtocolEndpoint()
        protocolEndpointUserInfo.setBinding(ProtocolEndpointBinding.REDIRECT)
        protocolEndpointUserInfo.setUrl("https://idp.example.com/userinfo")

        ProtocolEndpoint protocolEndpointJwks = new ProtocolEndpoint()
        protocolEndpointJwks.setBinding(ProtocolEndpointBinding.REDIRECT)
        protocolEndpointJwks.setUrl("https://idp.example.com/keys")

        protocolEndpoints.setAcs(protocolEndpointAcs)
        protocolEndpoints.setAuthorization(protocolEndpointAuthorization)
        protocolEndpoints.setToken(protocolEndpointToken)
        protocolEndpoints.setUserInfo(protocolEndpointUserInfo)
        protocolEndpoints.setJwks(protocolEndpointJwks)
        protocol.setEndpoints(protocolEndpoints)
        idp.setProtocol(protocol)

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy()
        PolicyAccountLink policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        Provisioning provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        ProvisioningConditions provisioningConditions = new ProvisioningConditions()
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(1000)
        ProvisioningGroups provisioningGroups = new ProvisioningGroups()
        provisioningGroups.setAction(ProvisioningGroupsAction.NONE)
        provisioning.setGroups(provisioningGroups)
        identityProviderPolicy.setProvisioning(provisioning)
        PolicySubject policySubject = new PolicySubject()
        PolicyUserNameTemplate policyUserNameTemplate = new PolicyUserNameTemplate()
        policyUserNameTemplate.setTemplate("idpuser.email")
        policySubject.setUserNameTemplate(policyUserNameTemplate)
        policySubject.setMatchType(PolicySubjectMatchType.USERNAME)
        identityProviderPolicy.setSubject(policySubject)
        idp.setPolicy(identityProviderPolicy)

        IdentityProvider createdIdp = identityProviderApi.createIdentityProvider(idp)
        registerForCleanup(createdIdp)

        assertThat(createdIdp, notNullValue())
        assertThat(createdIdp.getId(), notNullValue())
        assertThat(createdIdp.getName(), equalTo(name))
        assertThat(createdIdp.getStatus(), equalTo(LifecycleStatus.ACTIVE))

        // retrieve
        IdentityProvider retrievedIdp = identityProviderApi.getIdentityProvider(createdIdp.getId())
        assertThat(retrievedIdp.getId(), equalTo(createdIdp.getId()))

        // update
        String newName = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider newIdp = new IdentityProvider()
        newIdp.setName(name)
        newIdp.setType("OIDC")
        newIdp.setIssuerMode(IssuerMode.ORG_URL)
        protocol = new Protocol()

        protocolAlgorithmTypeReq = new ProtocolAlgorithmType()
        protocolAlgorithmTypeRes = new ProtocolAlgorithmType()
        protocolAlgorithmTypeSignature_Req = new ProtocolAlgorithmTypeSignature()
        protocolAlgorithmTypeSignature_Req.setScope(ProtocolAlgorithmTypeSignatureScope.REQUEST)
        protocolAlgorithmTypeSignature_Req.setAlgorithm("SHA-256")
        protocolAlgorithmTypeReq.setSignature(protocolAlgorithmTypeSignature_Req)
        protocolAlgorithmTypeSignature_Res = new ProtocolAlgorithmTypeSignature()
        protocolAlgorithmTypeSignature_Res.setScope(ProtocolAlgorithmTypeSignatureScope.RESPONSE)
        protocolAlgorithmTypeSignature_Res.setAlgorithm("SHA-256")
        protocolAlgorithmTypeRes.setSignature(protocolAlgorithmTypeSignature_Res)

        protocolAlgorithms = new ProtocolAlgorithms()
        protocolAlgorithms.setRequest(protocolAlgorithmTypeReq)
        protocolAlgorithms.setResponse(protocolAlgorithmTypeRes)
        protocol.setAlgorithms(protocolAlgorithms)
        scopes = new ArrayList<>()
        scopes.add("openid")
        scopes.add("email")
        protocol.setScopes(scopes)
        protocol.setType(ProtocolType.OIDC)

        protocolEndpointIssuer = new ProtocolEndpoint()
        protocolEndpointIssuer.setUrl("https://idp.example.com/new")
        protocol.setIssuer(protocolEndpointIssuer)
        identityProviderCredentials = new IdentityProviderCredentials()
        identityProviderCredentialsClient = new IdentityProviderCredentialsClient()
        identityProviderCredentialsClient.setClientId("your-new-client-id")
        identityProviderCredentialsClient.setClientSecret("your-new-client-secret")
        identityProviderCredentials.setClient(identityProviderCredentialsClient)
        protocol.setCredentials(identityProviderCredentials)

        protocolEndpoints = new ProtocolEndpoints()
        protocolEndpointAcs = new ProtocolEndpoint()
        protocolEndpointAcs.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointAcs.setType(ProtocolEndpointType.INSTANCE)

        protocolEndpointAuthorization = new ProtocolEndpoint()
        protocolEndpointAuthorization.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointAuthorization.setUrl("https://idp.example.com/authorize_new")

        protocolEndpointToken = new ProtocolEndpoint()
        protocolEndpointToken.setBinding(ProtocolEndpointBinding.POST)
        protocolEndpointToken.setUrl("https://idp.example.com/token_new")

        protocolEndpointUserInfo = new ProtocolEndpoint()
        protocolEndpointUserInfo.setBinding(ProtocolEndpointBinding.REDIRECT)
        protocolEndpointUserInfo.setUrl("https://idp.example.com/userinfo_new")

        protocolEndpointJwks = new ProtocolEndpoint()
        protocolEndpointJwks.setBinding(ProtocolEndpointBinding.REDIRECT)
        protocolEndpointJwks.setUrl("https://idp.example.com/keys_new")

        protocolEndpoints.setAcs(protocolEndpointAcs)
        protocolEndpoints.setAuthorization(protocolEndpointAuthorization)
        protocolEndpoints.setToken(protocolEndpointToken)
        protocolEndpoints.setUserInfo(protocolEndpointUserInfo)
        protocolEndpoints.setJwks(protocolEndpointJwks)
        protocol.setEndpoints(protocolEndpoints)
        newIdp.setProtocol(protocol)

        identityProviderPolicy = new IdentityProviderPolicy()
        policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        provisioningConditions = new ProvisioningConditions()
        provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(1000)
        provisioningGroups = new ProvisioningGroups()
        provisioningGroups.setAction(ProvisioningGroupsAction.NONE)
        provisioning.setGroups(provisioningGroups)
        identityProviderPolicy.setProvisioning(provisioning)
        policySubject = new PolicySubject()
        policyUserNameTemplate = new PolicyUserNameTemplate()
        policyUserNameTemplate.setTemplate("idpuser.email")
        policySubject.setUserNameTemplate(policyUserNameTemplate)
        policySubject.setMatchType(PolicySubjectMatchType.USERNAME)
        identityProviderPolicy.setSubject(policySubject)
        newIdp.setPolicy(identityProviderPolicy)

        IdentityProvider updatedIdp = identityProviderApi.updateIdentityProvider(createdIdp.getId(), newIdp)

        registerForCleanup(updatedIdp)

        IdentityProvider retrievedUpdatedIdp = identityProviderApi.getIdentityProvider(updatedIdp.getId())

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

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

//    @Test (groups = "group2")
//    void oidcIdpUserTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.oidc()
//            .setName(name)
//            .setIssuerMode(IdentityProvider.IssuerModeEnum.ORG_URL)
//            .setRequestSignatureAlgorithm("SHA-256")
//            .setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)
//            .setResponseSignatureAlgorithm("SHA-256")
//            .setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum.ANY)
//            .setAcsEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
//            .setAcsEndpointType(ProtocolEndpoint.TypeEnum.INSTANCE)
//            .setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
//            .setAuthorizationEndpointUrl("https://idp.example.com/authorize")
//            .setTokenEndpointBinding(ProtocolEndpoint.BindingEnum.POST)
//            .setTokenEndpointUrl("https://idp.example.com/token")
//            .setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
//            .setUserInfoEndpointUrl("https://idp.example.com/userinfo")
//            .setJwksEndpointBinding(ProtocolEndpoint.BindingEnum.REDIRECT)
//            .setJwksEndpointUrl("https://idp.example.com/keys")
//            .setScopes(["openid", "profile", "email"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .setIssuerUrl("https://idp.example.com")
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.email")
//            .setMatchType(PolicySubjectMatchType.USERNAME)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // list social auth tokens
//        SocialAuthTokenList socialAuthTokenList = createdIdp.listSocialAuthTokens(createdUser.getId())
//        assertThat(socialAuthTokenList, iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//    }
//
//    @Test (groups = "group2")
//    void googleIdpTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create Google idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.google()
//            .setName(name)
//            .setScopes(["openid", "profile", "email"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .isProfileMaster(true)
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.email")
//            .setMatchType(PolicySubjectMatchType.USERNAME)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//
//        assertNotPresent(client.listIdentityProviders(), createdIdp)
//    }
//
//    @Test (groups = "group2")
//    void facebookIdpTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create Facebook idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.facebook()
//            .setName(name)
//            .setScopes(["public_profile", "email"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .isProfileMaster(true)
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.email")
//            .setMatchType(PolicySubjectMatchType.USERNAME)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//
//        assertNotPresent(client.listIdentityProviders(), createdIdp)
//    }
//
//    // Remove this groups tag after OKTA-329987 is resolved (Adding this tag disables the test in bacon PDV)
//    @Test (groups = "bacon")
//    void microsoftIdpTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create Microsoft idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.microsoft()
//            .setName(name)
//            .setScopes(["openid", "email", "profile", "https://graph.microsoft.com/User.Read"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .isProfileMaster(true)
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.userPrincipalName")
//            .setMatchType(PolicySubjectMatchType.USERNAME)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//
//        assertNotPresent(client.listIdentityProviders(), createdIdp)
//    }
//
//    @Test (groups = "group2")
//    void linkedInIdpTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create Linkedin idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.linkedin()
//            .setName(name)
//            .setScopes(["r_basicprofile", "r_emailaddress"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .isProfileMaster(true)
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.email")
//            .setMatchType(PolicySubjectMatchType.EMAIL)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//
//        assertNotPresent(client.listIdentityProviders(), createdIdp)
//    }
//
//    @Test (groups = "group2")
//    void idpWithStringTypeTest() {
//
//        // create user
//        def email = "joe.coder+${uniqueTestName}@example.com"
//        User createdUser = UserBuilder.instance()
//            .setEmail(email)
//            .setFirstName("Joe")
//            .setLastName("Code")
//            .setPassword("Password1".toCharArray())
//            .buildAndCreate(client)
//        registerForCleanup(createdUser)
//
//        // create Linkedin idp
//        String name = "java-sdk-it-" + UUID.randomUUID().toString()
//
//        IdentityProvider createdIdp = IdentityProviderBuilders.ofType("GOOGLE")
//            .setName(name)
//            .setScopes(["r_basicprofile", "r_emailaddress"])
//            .setClientId("your-client-id")
//            .setClientSecret("your-client-secret")
//            .isProfileMaster(true)
//            .setMaxClockSkew(120000)
//            .setUserName("idpuser.email")
//            .setMatchType(PolicySubjectMatchType.EMAIL)
//            .buildAndCreate(client)
//        registerForCleanup(createdIdp)
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // link user
//        IdentityProviderApplicationUser idpAppUser = createdIdp.linkUser(createdUser.getId(),
//            client.instantiate(UserIdentityProviderLinkRequest)
//                .setExternalId("externalId"))
//
//        assertThat(createdIdp.listUsers(), iterableWithSize(1))
//        assertPresent(createdIdp.listUsers(), idpAppUser)
//
//        // unlink user
//        createdIdp.unlinkUser(createdUser.getId())
//
//        // list linked idp users
//        assertThat(createdIdp.listUsers(), iterableWithSize(0))
//
//        // deactivate
//        createdIdp.deactivate()
//
//        // delete
//        createdIdp.delete()
//
//        assertNotPresent(client.listIdentityProviders(), createdIdp)
//    }
}
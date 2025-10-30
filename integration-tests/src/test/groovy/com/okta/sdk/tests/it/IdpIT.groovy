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

import com.okta.sdk.resource.api.IdentityProviderUsersApi
import com.okta.sdk.resource.model.IdentityProvider
import com.okta.sdk.resource.model.IdentityProviderIssuerMode
import com.okta.sdk.resource.model.IdentityProviderPolicy
import com.okta.sdk.resource.model.IdentityProviderProtocol
import com.okta.sdk.resource.model.IdentityProviderType
import com.okta.sdk.resource.model.LifecycleStatus
import com.okta.sdk.resource.model.IDVCredentials
import com.okta.sdk.resource.model.IDVCredentialsClient
import com.okta.sdk.resource.model.IDVEndpoints
import com.okta.sdk.resource.model.IDVAuthorizationEndpoint
import com.okta.sdk.resource.model.IDVTokenEndpoint
import com.okta.sdk.resource.model.OAuthAuthorizationEndpoint
import com.okta.sdk.resource.model.OAuthCredentials
import com.okta.sdk.resource.model.OAuthCredentialsClient
import com.okta.sdk.resource.model.OAuthEndpoints
import com.okta.sdk.resource.model.OAuthTokenEndpoint
import com.okta.sdk.resource.model.OidcAlgorithms
import com.okta.sdk.resource.model.OidcJwksEndpoint
import com.okta.sdk.resource.model.OidcSettings
import com.okta.sdk.resource.model.OidcUserInfoEndpoint
import com.okta.sdk.resource.model.PolicyAccountLink
import com.okta.sdk.resource.model.PolicyAccountLinkAction
import com.okta.sdk.resource.model.PolicySubject
import com.okta.sdk.resource.model.PolicySubjectMatchType
import com.okta.sdk.resource.model.PolicyUserNameTemplate
import com.okta.sdk.resource.model.ProtocolAlgorithmRequestScope
import com.okta.sdk.resource.model.ProtocolAlgorithmResponseScope
import com.okta.sdk.resource.model.ProtocolEndpointBinding
import com.okta.sdk.resource.model.ProtocolOAuth
import com.okta.sdk.resource.model.ProtocolOidc
import com.okta.sdk.resource.model.Provisioning
import com.okta.sdk.resource.model.ProvisioningAction
import com.okta.sdk.resource.model.ProvisioningConditions
import com.okta.sdk.resource.model.ProvisioningDeprovisionedAction
import com.okta.sdk.resource.model.ProvisioningDeprovisionedCondition
import com.okta.sdk.resource.model.ProvisioningGroups
import com.okta.sdk.resource.model.ProvisioningGroupsAction
import com.okta.sdk.resource.model.ProvisioningSuspendedAction
import com.okta.sdk.resource.model.ProvisioningSuspendedCondition
import com.okta.sdk.resource.model.SocialAuthToken
import com.okta.sdk.resource.model.User
import com.okta.sdk.resource.model.UserIdentityProviderLinkRequest
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.api.IdentityProviderApi
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/idps}.
 * @since 2.0.0
 */
class IdpIT extends ITSupport {

    IdentityProviderApi identityProviderApi = new IdentityProviderApi(getClient())
    IdentityProviderUsersApi identityProviderUsersApi = new IdentityProviderUsersApi(getClient())

    @Test (groups = "group2", enabled = false)
    void oidcIdpLifecycleTest() {

        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        // create oidc idp
        // NOTE: This test is disabled due to API model limitations:
        // The Okta API requires protocol.endpoints.jwks for OIDC IdPs, but the generated
        // IdentityProviderProtocol class only supports IDVEndpoints which doesn't have a jwks field.
        // IDVEndpoints only has: authorization, par, and token endpoints.
        // 
        // API Error: "The protocol.endpoints.jwks object cannot be left blank."
        //
        // Possible solutions:
        // 1. Wait for API code regeneration with corrected OpenAPI spec
        // 2. Use raw JSON manipulation to bypass type safety
        // 3. Create OIDC IdPs manually in the test org and use them for user linking tests only
        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.OIDC)
        idp.setIssuerMode(IdentityProviderIssuerMode.ORG_URL)
        
        // Create protocol with credentials - using IdentityProviderProtocol as the wrapper
        IdentityProviderProtocol protocol = new IdentityProviderProtocol()
        protocol.setType(IdentityProviderProtocol.TypeEnum.OIDC)
        protocol.setScopes(Arrays.asList("openid", "profile", "email"))
        
        // Set up IDV credentials with client info
        IDVCredentials idvCredentials = new IDVCredentials()
        IDVCredentialsClient idvClient = new IDVCredentialsClient()
        idvClient.setClientId("test_client_id_" + UUID.randomUUID().toString())
        idvClient.setClientSecret("test_client_secret_" + UUID.randomUUID().toString())
        idvCredentials.setClient(idvClient)
        protocol.setCredentials(idvCredentials)
        
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
        assertThat(createdIdp.getLinks(), notNullValue())

        // retrieve
        IdentityProvider retrievedIdp = identityProviderApi.getIdentityProvider(createdIdp.getId())
        assertThat(retrievedIdp.getId(), equalTo(createdIdp.getId()))
        assertThat(retrievedIdp.getLinks(), notNullValue())
        assertThat(retrievedIdp.getLinks().getAuthorize(), notNullValue())
        assertThat(retrievedIdp.getLinks().getAuthorize().getHints(), notNullValue())
        assertThat(retrievedIdp.getLinks().getAuthorize().getHref(), notNullValue())
        assertThat(retrievedIdp.getLinks().getClientRedirectUri(), notNullValue())
        assertThat(retrievedIdp.getLinks().getClientRedirectUri().getHints(), notNullValue())
        assertThat(retrievedIdp.getLinks().getClientRedirectUri().getHref(), notNullValue())

        // update
        String newName = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider newIdp = new IdentityProvider()
        newIdp.setName(newName)
        newIdp.setType(IdentityProviderType.OIDC)
        newIdp.setIssuerMode(IdentityProviderIssuerMode.ORG_URL)
        
        // Configure updated OIDC protocol using IdentityProviderProtocol
        IdentityProviderProtocol newProtocol = new IdentityProviderProtocol()
        newProtocol.setType(IdentityProviderProtocol.TypeEnum.OIDC)
        newProtocol.setScopes(Arrays.asList("openid", "profile", "email"))
        newProtocol.setOktaIdpOrgUrl("https://idp.example.com/new")
        newIdp.setProtocol(newProtocol)

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

        IdentityProvider updatedIdp = identityProviderApi.replaceIdentityProvider(createdIdp.getId(), newIdp)

        // retrieve
        IdentityProvider retrievedUpdatedIdp = identityProviderApi.getIdentityProvider(updatedIdp.getId())

        assertThat(retrievedUpdatedIdp.getId(), equalTo(createdIdp.getId()))
        assertThat(retrievedUpdatedIdp.getName(), equalTo(newName))
        assertThat(retrievedUpdatedIdp.getProtocol().getOktaIdpOrgUrl(), equalTo("https://idp.example.com/new"))
        assertThat(retrievedUpdatedIdp.getLinks(), notNullValue())

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2", enabled = false)
    void oidcIdpUserTest() {

        // create user
        // NOTE: This test is disabled because it requires valid OIDC credentials
        User createdUser = randomUser()
        registerForCleanup(createdUser)

        // create oidc idp
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.OIDC)
        idp.setIssuerMode(IdentityProviderIssuerMode.ORG_URL)
        
        // Protocol configuration with credentials is required but not set here

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

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), hasSize(0))

        // link user
        UserIdentityProviderLinkRequest userIdentityProviderLinkRequest = new UserIdentityProviderLinkRequest()
        userIdentityProviderLinkRequest.setExternalId("external-id")
        identityProviderUsersApi.linkUserToIdentityProvider(createdIdp.getId(), createdUser.getId(), userIdentityProviderLinkRequest)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), hasSize(1))

        // unlink user
        identityProviderUsersApi.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), hasSize(0))

        // list social auth tokens
        List<SocialAuthToken> socialAuthTokenList = identityProviderUsersApi.listSocialAuthTokens(createdIdp.getId(), createdUser.getId())
        assertThat(socialAuthTokenList, iterableWithSize(0))

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2", enabled = false)
    void googleIdpTest() {

        // create user
        // NOTE: This test is disabled because it requires valid Google OAuth2 credentials
        User createdUser = randomUser()
        registerForCleanup(createdUser)

        // create Google idp
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.GOOGLE)
        
        // Protocol configuration with credentials is required but not set here

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy()
        PolicyAccountLink policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        Provisioning provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        provisioning.setProfileMaster(Boolean.TRUE)
        ProvisioningConditions provisioningConditions = new ProvisioningConditions()
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(120000)
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

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // link user
        UserIdentityProviderLinkRequest userIdentityProviderLinkRequest = new UserIdentityProviderLinkRequest()
        userIdentityProviderLinkRequest.setExternalId("external-id")
        identityProviderUsersApi.linkUserToIdentityProvider(createdIdp.getId(), createdUser.getId(), userIdentityProviderLinkRequest)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(1))

        // unlink user
        identityProviderUsersApi.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2", enabled = false)
    void facebookIdpTest() {

        // create user
        // NOTE: This test is disabled because it requires valid Facebook OAuth2 credentials
        User createdUser = randomUser()
        registerForCleanup(createdUser)

        // create Facebook idp
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.FACEBOOK)
        
        // Protocol configuration with credentials is required but not set here

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy()
        PolicyAccountLink policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        Provisioning provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        provisioning.setProfileMaster(Boolean.TRUE)
        ProvisioningConditions provisioningConditions = new ProvisioningConditions()
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(0)
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

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // link user
        UserIdentityProviderLinkRequest userIdentityProviderLinkRequest = new UserIdentityProviderLinkRequest()
        userIdentityProviderLinkRequest.setExternalId("external-id")
        identityProviderUsersApi.linkUserToIdentityProvider(createdIdp.getId(), createdUser.getId(), userIdentityProviderLinkRequest)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(1))

        // unlink user
        identityProviderUsersApi.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2", enabled = false)
    void microsoftIdpTest() {

        // create user
        // NOTE: This test is disabled because it requires valid Microsoft OAuth2 credentials
        User createdUser = randomUser()
        registerForCleanup(createdUser)

        // create Microsoft idp
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.MICROSOFT)
        
        // Protocol configuration with credentials is required but not set here

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy()
        PolicyAccountLink policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        Provisioning provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        provisioning.setProfileMaster(Boolean.TRUE)
        ProvisioningConditions provisioningConditions = new ProvisioningConditions()
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(0)
        ProvisioningGroups provisioningGroups = new ProvisioningGroups()
        provisioningGroups.setAction(ProvisioningGroupsAction.NONE)
        provisioning.setGroups(provisioningGroups)
        identityProviderPolicy.setProvisioning(provisioning)
        PolicySubject policySubject = new PolicySubject()
        PolicyUserNameTemplate policyUserNameTemplate = new PolicyUserNameTemplate()
        policyUserNameTemplate.setTemplate("idpuser.userPrincipalName")
        policySubject.setUserNameTemplate(policyUserNameTemplate)
        policySubject.setMatchType(PolicySubjectMatchType.USERNAME)
        identityProviderPolicy.setSubject(policySubject)
        idp.setPolicy(identityProviderPolicy)

        IdentityProvider createdIdp = identityProviderApi.createIdentityProvider(idp)
        registerForCleanup(createdIdp)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // link user
        UserIdentityProviderLinkRequest userIdentityProviderLinkRequest = new UserIdentityProviderLinkRequest()
        userIdentityProviderLinkRequest.setExternalId("external-id")
        identityProviderUsersApi.linkUserToIdentityProvider(createdIdp.getId(), createdUser.getId(), userIdentityProviderLinkRequest)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(1))

        // unlink user
        identityProviderUsersApi.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }

    @Test (groups = "group2", enabled = false)
    void linkedInIdpTest() {

        // create user
        // NOTE: This test is disabled because it requires valid LinkedIn OAuth2 credentials
        User createdUser = randomUser()
        registerForCleanup(createdUser)

        // create LinkedIn idp
        String name = "java-sdk-it-" + UUID.randomUUID().toString()

        IdentityProvider idp = new IdentityProvider()
        idp.setName(name)
        idp.setType(IdentityProviderType.LINKEDIN)
        
        // Protocol configuration with credentials is required but not set here

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy()
        PolicyAccountLink policyAccountLink = new PolicyAccountLink()
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO)
        identityProviderPolicy.setAccountLink(policyAccountLink)
        Provisioning provisioning = new Provisioning()
        provisioning.setAction(ProvisioningAction.AUTO)
        provisioning.setProfileMaster(Boolean.TRUE)
        ProvisioningConditions provisioningConditions = new ProvisioningConditions()
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition()
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE)
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov)
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition()
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE)
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp)
        provisioning.setConditions(provisioningConditions)
        identityProviderPolicy.setProvisioning(provisioning)
        identityProviderPolicy.setMaxClockSkew(0)
        ProvisioningGroups provisioningGroups = new ProvisioningGroups()
        provisioningGroups.setAction(ProvisioningGroupsAction.NONE)
        provisioning.setGroups(provisioningGroups)
        identityProviderPolicy.setProvisioning(provisioning)
        PolicySubject policySubject = new PolicySubject()
        PolicyUserNameTemplate policyUserNameTemplate = new PolicyUserNameTemplate()
        policyUserNameTemplate.setTemplate("idpuser.email")
        policySubject.setUserNameTemplate(policyUserNameTemplate)
        policySubject.setMatchType(PolicySubjectMatchType.EMAIL)
        identityProviderPolicy.setSubject(policySubject)
        idp.setPolicy(identityProviderPolicy)

        IdentityProvider createdIdp = identityProviderApi.createIdentityProvider(idp)
        registerForCleanup(createdIdp)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // link user
        UserIdentityProviderLinkRequest userIdentityProviderLinkRequest = new UserIdentityProviderLinkRequest()
        userIdentityProviderLinkRequest.setExternalId("external-id")
        identityProviderUsersApi.linkUserToIdentityProvider(createdIdp.getId(), createdUser.getId(), userIdentityProviderLinkRequest)

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(1))

        // unlink user
        identityProviderUsersApi.unlinkUserFromIdentityProvider(createdIdp.getId(), createdUser.getId())

        // list linked idp users
        assertThat(identityProviderUsersApi.listIdentityProviderApplicationUsers(createdIdp.getId(), null, null, null, null), iterableWithSize(0))

        // deactivate
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId())

        // delete
        identityProviderApi.deleteIdentityProvider(createdIdp.getId())
    }
}
/*
 * Copyright 2017 Okta
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

import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.application.AppUser
import com.okta.sdk.resource.application.AppUserCredentials
import com.okta.sdk.resource.application.AppUserList
import com.okta.sdk.resource.application.AppUserPasswordCredential
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.ApplicationCredentialsOAuthClient
import com.okta.sdk.resource.application.ApplicationCredentialsScheme
import com.okta.sdk.resource.application.ApplicationGroupAssignment
import com.okta.sdk.resource.application.ApplicationGroupAssignmentList
import com.okta.sdk.resource.application.ApplicationVisibility
import com.okta.sdk.resource.application.ApplicationVisibilityHide
import com.okta.sdk.resource.application.AutoLoginApplication
import com.okta.sdk.resource.application.AutoLoginApplicationSettings
import com.okta.sdk.resource.application.AutoLoginApplicationSettingsSignOn
import com.okta.sdk.resource.application.BasicApplicationSettings
import com.okta.sdk.resource.application.BasicApplicationSettingsApplication
import com.okta.sdk.resource.application.BasicAuthApplication
import com.okta.sdk.resource.application.BookmarkApplication
import com.okta.sdk.resource.application.BookmarkApplicationSettings
import com.okta.sdk.resource.application.BookmarkApplicationSettingsApplication
import com.okta.sdk.resource.application.JsonWebKey
import com.okta.sdk.resource.application.JsonWebKeyList
import com.okta.sdk.resource.application.OAuthApplicationCredentials
import com.okta.sdk.resource.application.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.application.OAuthGrantType
import com.okta.sdk.resource.application.OAuthResponseType
import com.okta.sdk.resource.application.OpenIdConnectApplication
import com.okta.sdk.resource.application.OpenIdConnectApplicationSettings
import com.okta.sdk.resource.application.OpenIdConnectApplicationSettingsClient
import com.okta.sdk.resource.application.OpenIdConnectApplicationType
import com.okta.sdk.resource.application.SamlApplication
import com.okta.sdk.resource.application.SamlApplicationSettings
import com.okta.sdk.resource.application.SamlApplicationSettingsSignOn
import com.okta.sdk.resource.application.SamlAttributeStatement
import com.okta.sdk.resource.application.SchemeApplicationCredentials
import com.okta.sdk.resource.application.SecurePasswordStoreApplication
import com.okta.sdk.resource.application.SecurePasswordStoreApplicationSettings
import com.okta.sdk.resource.application.SecurePasswordStoreApplicationSettingsApplication
import com.okta.sdk.resource.application.SwaApplication
import com.okta.sdk.resource.application.SwaApplicationSettings
import com.okta.sdk.resource.application.SwaApplicationSettingsApplication
import com.okta.sdk.resource.application.WsFederationApplication
import com.okta.sdk.resource.application.WsFederationApplicationSettings
import com.okta.sdk.resource.application.WsFederationApplicationSettingsApplication
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.User
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for /api/v1/apps.
 * @since 0.9.0
 */
class ApplicationsIT extends ITSupport {

    void doCrudTest(Application app) {

        Client client = Clients.builder().build()

        // get the size of initial list
        int preCount = getCount(client)
        slowItDown()

        // Create a resource
        def resource = create(client, app)
        registerForCleanup(resource)
        slowItDown()

        // count the resource after creating
        int count = getCount(client)
        assertThat "More then one resource was created", count, is(preCount + 1)
        slowItDown()

//        // getting the resource again should result in the same object
//        assertThat read(client, resource.id), equalTo(resource)

        // update the resource
        update(client, resource)
        slowItDown()

        count = getCount(client)
        assertThat "New resource was created while attempting to update", count, is(preCount + 1)
        slowItDown()

        // delete the resource
        delete(resource)
        slowItDown()

        count = getCount(client)
        assertThat "Resource was not deleted", count, is(preCount)
        slowItDown(1000)
    }

    def create(Client client, Application app) {
        app.setLabel("app-${UUID.randomUUID().toString()}")
        return client.createApplication(app)
    }

    def read(Client client, String id) {
        return client.getApplication(id)
    }

    void update(Client client, def resource) {
        String newLabel = resource.label + "-update"
        resource.label = newLabel
        resource.update()

        // make sure the label was updated
        assertThat read(client, resource.id).label, equalTo(newLabel)
    }

    void delete(def resource) {
        resource.deactivate()
        resource.delete()
    }

    int getCount(Client client) {
        return getResourceCollectionIterator(client).size()
    }

    Iterator getResourceCollectionIterator(Client client) {
        return client.listApplications().iterator()
    }

    @Test
    void crudOpenIdConnect() {
        doCrudTest(client.instantiate(OpenIdConnectApplication)
                        .setSettings(client.instantiate(OpenIdConnectApplicationSettings)
                            .setOAuthClient(client.instantiate(OpenIdConnectApplicationSettingsClient)
                                .setClientUri("https://example.com/client")
                                .setLogoUri("https://example.com/assets/images/logo-new.png")
                                .setRedirectUris(["https://example.com/oauth2/callback",
                                                  "myapp://callback"])
                                .setResponseTypes([OAuthResponseType.TOKEN,
                                                   OAuthResponseType.ID_TOKEN,
                                                   OAuthResponseType.CODE])
                                .setGrantTypes([OAuthGrantType.IMPLICIT,
                                                OAuthGrantType.AUTHORIZATION_CODE])
                                .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                                .setTosUri("https://example.com/client/tos")
                                .setPolicyUri("https://example.com/client/policy")))
                        .setCredentials(client.instantiate(OAuthApplicationCredentials)
                            .setOAuthClient(client.instantiate(ApplicationCredentialsOAuthClient)
                                .setClientId(UUID.randomUUID().toString())
                                .setAutoKeyRotation(true)
                                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST))))
    }

    @Test
    void crudAutoLogin() {
        doCrudTest(client.instantiate(AutoLoginApplication)
                        .setVisibility(client.instantiate(ApplicationVisibility)
                            .setAutoSubmitToolbar(false)
                            .setHide(client.instantiate(ApplicationVisibilityHide)
                                .setIOS(false)
                                .setWeb(false)))
                        .setSettings(client.instantiate(AutoLoginApplicationSettings)
                            .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                                .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                                .setLoginUrl("http://swaprimaryloginurl.okta.com"))))
    }

    @Test
    void crudWsFed() {
        doCrudTest(client.instantiate(WsFederationApplication)
                        .setSettings(client.instantiate(WsFederationApplicationSettings)
                            .setApp(client.instantiate(WsFederationApplicationSettingsApplication)
                                .setAudienceRestriction( "urn:example:app")
                                .setGroupName(null)
                                .setGroupValueFormat("windowsDomainQualifiedName")
                                .setRealm("urn:example:app")
                                .setWReplyURL("https://example.com/")
                                .setAttributeStatements(null)
                                .setNameIDFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")
                                .setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")
                                .setSiteURL("https://example.com")
                                .setWReplyOverride(false)
                                .setGroupFilter(null)
                                .setUsernameAttribute("username"))))
    }

    @Test
    void crudSaml20() {
        doCrudTest(client.instantiate(SamlApplication)
                        .setVisibility(client.instantiate(ApplicationVisibility)
                            .setAutoSubmitToolbar(false)
                            .setHide(client.instantiate(ApplicationVisibilityHide)
                            .setIOS(false)
                            .setWeb(false)))
                        .setSettings(client.instantiate(SamlApplicationSettings)
                            .setSignOn(client.instantiate(SamlApplicationSettingsSignOn)
                                .setDefaultRelayState("")
                                .setSsoAcsUrl("http://testorgone.okta")
                                .setIdpIssuer('http://www.okta.com/${org.externalKey}')
                                .setAudience("asdqwe123")
                                .setRecipient("http://testorgone.okta")
                                .setDestination("http://testorgone.okta")
                                .setSubjectNameIdTemplate('${user.userName}')
                                .setSubjectNameIdFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")
                                .setResponseSigned(true)
                                .setAssertionSigned(true)
                                .setSignatureAlgorithm("RSA_SHA256")
                                .setDigestAlgorithm("SHA256")
                                .setHonorForceAuthn(true)
                                .setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")
                                .setSpIssuer(null)
                                .setRequestCompressed(false)
                                .setAttributeStatements(new ArrayList<SamlAttributeStatement>([
                                        client.instantiate(SamlAttributeStatement)
                                                .setType("EXPRESSION")
                                                .setName("Attribute")
                                                .setNamespace("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified")
                                                .setValues(["Value"])])))))
    }

    @Test
    void crudSecurePasswordStore() {
        doCrudTest(client.instantiate(SecurePasswordStoreApplication)
                        .setSettings(client.instantiate(SecurePasswordStoreApplicationSettings)
                            .setApp(client.instantiate(SecurePasswordStoreApplicationSettingsApplication)
                                .setUrl("https://example.com/login.html")
                                .setPasswordField("#txtbox-password")
                                .setUsernameField("#txtbox-username")
                                .setOptionalField1("param1")
                                .setOptionalField1Value("somevalue")
                                .setOptionalField2("param2")
                                .setOptionalField2Value("yetanothervalue")
                                .setOptionalField3("param3")
                                .setOptionalField3Value("finalvalue"))))
    }

    @Test
    void crudBrowserPlugin() {
        doCrudTest(client.instantiate(SwaApplication)
                        .setSettings(client.instantiate(SwaApplicationSettings)
                            .setApp(client.instantiate(SwaApplicationSettingsApplication)
                                .setButtonField("btn-login")
                                .setPasswordField("txtbox-password")
                                .setUsernameField("txtbox-username")
                                .setUrl("https://example.com/login.html"))))
//                                        .setLoginUrlRegex("REGEX_EXPRESSION"))
    }

    @Test
    void crudBasicAuth() {
        doCrudTest(client.instantiate(BasicAuthApplication)
                        .setSettings(client.instantiate(BasicApplicationSettings)
                            .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                .setAuthURL("https://example.com/auth.html")
                                .setUrl("https://example.com/login.html"))))
    }

    @Test
    void crudBasicAuth_editUsernameAndPassword() {
        doCrudTest(client.instantiate(BasicAuthApplication)
                        .setCredentials(client.instantiate(SchemeApplicationCredentials)
                            .setScheme(ApplicationCredentialsScheme.EDIT_USERNAME_AND_PASSWORD))
                        .setSettings(client.instantiate(BasicApplicationSettings)
                            .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                .setAuthURL("https://example.com/auth.html")
                                .setUrl("https://example.com/login.html"))))
    }

    @Test
    void crudBasicAuth_adminSetsCredentials() {
        doCrudTest(client.instantiate(BasicAuthApplication)
                        .setCredentials(client.instantiate(SchemeApplicationCredentials)
                            .setScheme(ApplicationCredentialsScheme.ADMIN_SETS_CREDENTIALS)
                            .setRevealPassword(true))
                        .setSettings(client.instantiate(BasicApplicationSettings)
                            .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                .setAuthURL("https://example.com/auth.html")
                                .setUrl("https://example.com/login.html"))))
    }

    @Test
    void crudBookmark() {
        doCrudTest(client.instantiate(BookmarkApplication)
                        .setSettings(client.instantiate(BookmarkApplicationSettings)
                            .setApp(client.instantiate(BookmarkApplicationSettingsApplication)
                                .setRequestIntegration(false)
                                .setUrl("https://example.com/bookmark.htm"))))
    }

    @Test
    void applicationKeysTest() {

        Client client = getClient()

        Application app1 = client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))

        Application app2 = client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(app1)
        registerForCleanup(app1)
        slowItDown()
        client.createApplication(app2)
        registerForCleanup(app2)
        slowItDown()

        JsonWebKeyList app1Keys = app1.listKeys()
        assertThat(app1Keys.size(), equalTo(1))
        slowItDown()

        JsonWebKey webKey = app1.generateApplicationKey(5)
        assertThat(webKey, notNullValue())
        assertThat(app1.listKeys().size(), equalTo(2))
        slowItDown()

        JsonWebKey readWebKey = app1.getApplicationKey(webKey.getKid())
        assertThat(webKey, equalTo(readWebKey))
        slowItDown()

        JsonWebKey clonedWebKey = app1.cloneApplicationKey(webKey.getKid(), app2.getId())
        assertThat(clonedWebKey, notNullValue())
        slowItDown()

        JsonWebKeyList app2Keys = app2.listKeys()
        assertThat(app2Keys.size(), equalTo(2))
        slowItDown()
    }

    @Test
    void deactivateActivateTest() {
        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))

        registerForCleanup(app)

        assertThat(app.status, equalTo(Application.StatusEnum.ACTIVE))
        slowItDown()

        app.deactivate()
        assertThat(client.getApplication(app.getId()).status, equalTo(Application.StatusEnum.INACTIVE))
        slowItDown()

        app.activate()
        assertThat(client.getApplication(app.getId()).status, equalTo(Application.StatusEnum.ACTIVE))
        slowItDown()
    }

//    @Test
//    void groupAssignmentWithNullBodyTest() {
//
//        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
//                .setLabel("app-${UUID.randomUUID().toString()}")
//                .setVisibility(client.instantiate(ApplicationVisibility)
//                    .setAutoSubmitToolbar(false)
//                    .setHide(client.instantiate(ApplicationVisibilityHide)
//                        .setIOS(false)
//                        .setWeb(false)))
//                .setSettings(client.instantiate(AutoLoginApplicationSettings)
//                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
//                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
//                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))
//
//        Group group = GroupBuilder.instance()
//                .setName("app-test-group-" + UUID.randomUUID().toString())
//                .setDescription("IT created Group")
//                .buildAndCreate(client)
//
//        registerForCleanup(app)
//        registerForCleanup(group)
//
//        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.id, null)
//        assertThat(groupAssignment, notNullValue())
//    }

    @Test
    void groupAssignmentTest() {

        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${UUID.randomUUID().toString()}")
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com"))))

        Group group = GroupBuilder.instance()
                .setName("app-test-group-" + UUID.randomUUID().toString())
                .setDescription("IT created Group")
                .buildAndCreate(client)

        registerForCleanup(app)
        registerForCleanup(group)
        slowItDown()

        assertThat(app.listGroupAssignments().iterator().size(), equalTo(0))

        ApplicationGroupAssignment aga = client.instantiate(ApplicationGroupAssignment)
                                            .setPriority(2)

        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.id, aga)
        assertThat(groupAssignment, notNullValue())
        assertThat(groupAssignment.priority, equalTo(2))
        assertThat(app.listGroupAssignments().iterator().size(), equalTo(1))
        slowItDown()

        // delete the assignment
        groupAssignment.delete()
        assertThat(app.listGroupAssignments().iterator().size(), equalTo(0))
    }

    @Test
    void associateUserWithApplication() {

        Client client = getClient()
        User user1 = randomUser()
        slowItDown()
        User user2 = randomUser()
        slowItDown()

        String label = "app-${UUID.randomUUID().toString()}"
        Application app = client.instantiate(AutoLoginApplication)
                .setLabel(label)
                .setVisibility(client.instantiate(ApplicationVisibility)
                    .setAutoSubmitToolbar(false)
                    .setHide(client.instantiate(ApplicationVisibilityHide)
                        .setIOS(false)
                        .setWeb(false)))
                .setSettings(client.instantiate(AutoLoginApplicationSettings)
                    .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                        .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                        .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(app)
        registerForCleanup(app)
        slowItDown()

        AppUserList appUserList = app.listApplicationUsers()
        assertThat appUserList.iterator().size(), equalTo(0)

        AppUser appUser1 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user1.id)
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user1.getProfile().getEmail())
                .setPassword(client.instantiate(AppUserPasswordCredential)
                    .setValue("super-secret1")))
        app.assignUserToApplication(appUser1)
        slowItDown()

        AppUser appUser2 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user2.id)
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user2.getProfile().getEmail())
                .setPassword(client.instantiate(AppUserPasswordCredential)
                    .setValue("super-secret2")))

        // FIXME: returned object should be the same
        appUser1 = app.assignUserToApplication(appUser1)
        appUser2 = app.assignUserToApplication(appUser2)
        slowItDown()

        // now we should have 2
        assertThat appUserList.iterator().size(), equalTo(2)

        // delete just one
        appUser1.delete()
        slowItDown()

        // now we should have 1
        assertThat appUserList.iterator().size(), equalTo(1)

        appUser2.getCredentials().setUserName("updated-"+user2.getProfile().getEmail())
        appUser2.update()

        AppUser readAppUser = app.getApplicationUser(appUser2.id)
        assertThat readAppUser.getCredentials().getUserName(), equalTo("updated-"+user2.getProfile().getEmail())
    }
}
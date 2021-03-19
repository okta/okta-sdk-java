/*
 * Copyright 2017-Present Okta, Inc.
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

import com.okta.commons.http.DefaultRequest
import com.okta.commons.http.HttpMethod
import com.okta.commons.http.MediaType
import com.okta.commons.http.Response
import com.okta.sdk.client.Client
import com.okta.sdk.impl.ds.DefaultDataStore
import com.okta.sdk.resource.Resource
import com.okta.sdk.resource.ResourceException
import com.okta.sdk.resource.application.*
import com.okta.sdk.resource.group.Group
import com.okta.sdk.resource.group.GroupBuilder
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.schema.UserSchema
import com.okta.sdk.resource.user.schema.UserSchemaDefinitions
import com.okta.sdk.resource.user.schema.UserSchemaPublic
import com.okta.sdk.tests.it.util.ITSupport
import org.testng.Assert
import org.testng.annotations.Test
import wiremock.net.minidev.json.JSONObject

import java.nio.charset.Charset
import javax.xml.parsers.DocumentBuilderFactory

import static com.okta.sdk.tests.it.util.Util.assertNotPresent
import static com.okta.sdk.tests.it.util.Util.assertPresent
import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.9.0
 */
class ApplicationsIT extends ITSupport {

    void doCrudTest(Application app) {

        // Create a resource
        def resource = create(client, app)
        registerForCleanup(resource)

        // getting the resource again should result in the same object
        def readResource = read(client, resource.getId())
        assertThat readResource, notNullValue()

        // OpenIdConnectApplication contains a password when created, so it will not be the same when retrieved)
        if (!(app instanceof OpenIdConnectApplication)) {
            // getting the resource again should result in the same object
            assertThat readResource, equalTo(resource)
        }

        // update the resource
        updateAndValidate(client, resource)

        // delete the resource
        deleteAndValidate(resource)
    }

    def create(Client client, Application app) {
        app.setLabel("java-sdk-it-" + UUID.randomUUID().toString())
        registerForCleanup(app)
        return client.createApplication(app)
    }

    def read(Client client, String id) {
        return client.getApplication(id)
    }

    void updateAndValidate(Client client, def resource) {
        String newLabel = resource.label + "-update"
        resource.label = newLabel
        resource.update()

        // make sure the label was updated
        assertThat read(client, resource.getId()).label, equalTo(newLabel)
    }

    void deleteAndValidate(def resource) {
        resource.deactivate()
        resource.delete()

        try {
            read(client, resource.getId())
            Assert.fail("Expected ResourceException (404)")
        } catch (ResourceException e) {
            assertThat e.status, equalTo(404)
        }
    }

    @Test
    void basicListTest() {
        // Create a resource
        def resource = create(client, client.instantiate(AutoLoginApplication)
                                            .setVisibility(client.instantiate(ApplicationVisibility)
                                                .setAutoSubmitToolbar(false)
                                                .setHide(client.instantiate(ApplicationVisibilityHide)
                                                    .setIOS(false)
                                                    .setWeb(false)))
                                            .setSettings(client.instantiate(AutoLoginApplicationSettings)
                                                .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                                                    .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                                                    .setLoginUrl("http://swaprimaryloginurl.okta.com"))))
        // search the resource collection looking for the new resource
        Optional optional = client.listApplications().stream()
                                .filter {it.getId() == resource.getId()}
                                .findFirst()

        // make sure it exists
        assertThat "New resource with id ${resource.getId()} was not found in list resource.", optional.isPresent()
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

    @Test (groups = "bacon")
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
                        .setLabel(uniqueTestName)
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
    void invalidApplicationCredentialsSchemeTest() {
        expect(IllegalArgumentException, {
            client.instantiate(BasicAuthApplication)
                .setCredentials(client.instantiate(SchemeApplicationCredentials)
                    .setScheme(ApplicationCredentialsScheme.SDK_UNKNOWN)
                    .setRevealPassword(true))
                .setSettings(client.instantiate(BasicApplicationSettings)
                    .setApp(client.instantiate(BasicApplicationSettingsApplication)
                        .setAuthURL("https://example.com/auth.html")
                        .setUrl("https://example.com/login.html")))
        })
    }

    @Test
    void invalidOAuthEndpointAuthenticationMethodTest() {
        expect(IllegalArgumentException ,{client.instantiate(OpenIdConnectApplication)
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
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.SDK_UNKNOWN)))})
    }

    @Test
    void invalidOAuthResponseTypeTest() {
        expect(IllegalArgumentException ,{client.instantiate(OpenIdConnectApplication)
            .setSettings(client.instantiate(OpenIdConnectApplicationSettings)
                .setOAuthClient(client.instantiate(OpenIdConnectApplicationSettingsClient)
                    .setClientUri("https://example.com/client")
                    .setLogoUri("https://example.com/assets/images/logo-new.png")
                    .setRedirectUris(["https://example.com/oauth2/callback",
                                      "myapp://callback"])
                    .setResponseTypes([OAuthResponseType.TOKEN,
                                       OAuthResponseType.ID_TOKEN,
                                       OAuthResponseType.CODE,
                                       OAuthResponseType.SDK_UNKNOWN])
                    .setGrantTypes([OAuthGrantType.IMPLICIT,
                                    OAuthGrantType.AUTHORIZATION_CODE])
                    .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                    .setTosUri("https://example.com/client/tos")
                    .setPolicyUri("https://example.com/client/policy")))
            .setCredentials(client.instantiate(OAuthApplicationCredentials)
                .setOAuthClient(client.instantiate(ApplicationCredentialsOAuthClient)
                    .setClientId(UUID.randomUUID().toString())
                    .setAutoKeyRotation(true)
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)))})
    }

    @Test
    void invalidOAuthGrantTypeTest() {
        expect(IllegalArgumentException, {client.instantiate(OpenIdConnectApplication)
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
                                    OAuthGrantType.AUTHORIZATION_CODE,
                                    OAuthGrantType.SDK_UNKNOWN])
                    .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                    .setTosUri("https://example.com/client/tos")
                    .setPolicyUri("https://example.com/client/policy")))
            .setCredentials(client.instantiate(OAuthApplicationCredentials)
                .setOAuthClient(client.instantiate(ApplicationCredentialsOAuthClient)
                    .setClientId(UUID.randomUUID().toString())
                    .setAutoKeyRotation(true)
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)))
        })
    }

    @Test
    void invalidOpenIdConnectApplicationTypeTest() {
        expect(IllegalArgumentException ,{
            client.instantiate(OpenIdConnectApplication)
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
                    .setApplicationType(OpenIdConnectApplicationType.SDK_UNKNOWN)
                    .setTosUri("https://example.com/client/tos")
                    .setPolicyUri("https://example.com/client/policy")))
            .setCredentials(client.instantiate(OAuthApplicationCredentials)
                .setOAuthClient(client.instantiate(ApplicationCredentialsOAuthClient)
                    .setClientId(UUID.randomUUID().toString())
                    .setAutoKeyRotation(true)
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)))
        })
    }

    @Test
    void crudBookmark() {
        doCrudTest(client.instantiate(BookmarkApplication)
                        .setSettings(client.instantiate(BookmarkApplicationSettings)
                            .setApp(client.instantiate(BookmarkApplicationSettingsApplication)
                                .setRequestIntegration(false)
                                .setUrl("https://example.com/bookmark.htm"))))
    }

    @Test(enabled = false) // OKTA-75280
    void applicationKeysTest() {

        Client client = getClient()

        Application app1 = client.instantiate(AutoLoginApplication)
                .setLabel("app-${uniqueTestName}")
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
                .setLabel("app-${uniqueTestName}")
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
        client.createApplication(app2)
        registerForCleanup(app2)

        JsonWebKeyList app1Keys = app1.listKeys()
        assertThat(app1Keys.size(), equalTo(1))

        JsonWebKey webKey = app1.generateApplicationKey(5)
        assertThat(webKey, notNullValue())
        assertThat(app1.listKeys().size(), equalTo(2))

        JsonWebKey readWebKey = app1.getApplicationKey(webKey.getKid())
        assertThat(webKey, equalTo(readWebKey))

        JsonWebKey clonedWebKey = app1.cloneApplicationKey(webKey.getKid(), app2.getId())
        assertThat(clonedWebKey, notNullValue())

        JsonWebKeyList app2Keys = app2.listKeys()
        assertThat(app2Keys.size(), equalTo(2))
    }

    @Test
    void deactivateActivateTest() {
        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${uniqueTestName}")
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

        assertThat(app.getStatus(), equalTo(Application.StatusEnum.ACTIVE))

        app.deactivate()
        assertThat(client.getApplication(app.getId()).getStatus(), equalTo(Application.StatusEnum.INACTIVE))

        app.activate()
        assertThat(client.getApplication(app.getId()).getStatus(), equalTo(Application.StatusEnum.ACTIVE))
    }

    @Test
    void groupAssignmentWithNullBodyTest() {

        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${uniqueTestName}")
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
                .setName("app-test-group-${uniqueTestName}")
                .setDescription("IT created Group")
                .buildAndCreate(client)

        registerForCleanup(app)
        registerForCleanup(group)

        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.getId())
        assertThat(groupAssignment, notNullValue())
    }

    @Test
    void groupAssignmentTest() {

        Application app = client.createApplication(client.instantiate(AutoLoginApplication)
                .setLabel("app-${uniqueTestName}")
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
                .setName("app-test-group-${uniqueTestName}")
                .setDescription("IT created Group")
                .buildAndCreate(client)

        registerForCleanup(app)
        registerForCleanup(group)

        assertThat(app.listGroupAssignments().iterator().size(), equalTo(0))

        ApplicationGroupAssignment aga = client.instantiate(ApplicationGroupAssignment)
                                            .setPriority(2)

        ApplicationGroupAssignment groupAssignment = app.createApplicationGroupAssignment(group.getId(), aga)
        assertThat(groupAssignment, notNullValue())
        assertThat(groupAssignment.priority, equalTo(2))
        assertThat(app.listGroupAssignments().iterator().size(), equalTo(1))

        ApplicationGroupAssignment receivedGroupAssignment = app.getApplicationGroupAssignment(group.getId())
        assertThat(groupAssignment.getId(), equalTo(receivedGroupAssignment.getId()))
        assertThat(groupAssignment.getPriority(), equalTo(receivedGroupAssignment.getPriority()))

        // delete the assignment
        groupAssignment.delete()
        assertThat(app.listGroupAssignments().iterator().size(), equalTo(0))
    }

    // Remove this groups tag after OKTA-337342 is resolved (Adding this tag disables the test in bacon PDV)
    @Test (groups = "bacon")
    void associateUserWithApplication() {

        Client client = getClient()
        User user1 = randomUser()
        User user2 = randomUser()

        String label = "app-${uniqueTestName}"
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

        // issue: listApplicationUsers() occasionally throws HTTP 404, Okta E0000007 - Resource not found error.
        // adding a sleep after createApplication() helps resolve the above issue.
        sleep(getTestOperationDelay())

        AppUserList appUserList = app.listApplicationUsers()
        assertThat appUserList.iterator().size(), equalTo(0)

        AppUser appUser1 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user1.getId())
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user1.getProfile().getEmail())
                .setPassword(client.instantiate(AppUserPasswordCredential)
                    .setValue("super-secret1".toCharArray())))
        app.assignUserToApplication(appUser1)

        AppUser appUser2 = client.instantiate(AppUser)
            .setScope("USER")
            .setId(user2.getId())
            .setCredentials(client.instantiate(AppUserCredentials)
                .setUserName(user2.getProfile().getEmail())
                .setPassword(client.instantiate(AppUserPasswordCredential)
                    .setValue("super-secret2".toCharArray())))

        assertThat(app.assignUserToApplication(appUser1), sameInstance(appUser1))
        assertThat(app.assignUserToApplication(appUser2), sameInstance(appUser2))

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        // now we should have 2
        assertThat appUserList.iterator().size(), equalTo(2)

        // delete just one
        appUser1.delete()

        // fix flakiness seen in PDV tests
        Thread.sleep(getTestOperationDelay())

        // now we should have 1
        assertThat appUserList.iterator().size(), equalTo(1)

        appUser2.getCredentials().setUserName("updated-"+user2.getProfile().getEmail())
        appUser2.update()

        AppUser readAppUser = app.getApplicationUser(appUser2.getId())
        assertThat readAppUser.getCredentials().getUserName(), equalTo("updated-"+user2.getProfile().getEmail())
    }

    @Test
    void csrTest() {
        Client client = getClient()

        String label = "app-${uniqueTestName}"
        Application app = client.instantiate(OpenIdConnectApplication)
            .setLabel(label)
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
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)))
        client.createApplication(app)
        registerForCleanup(app)

        assertThat(app.getStatus(), equalTo(Application.StatusEnum.ACTIVE))

        // create csr metadata
        CsrMetadata csrMetadata = client.instantiate(CsrMetadata)
              .setSubject(client.instantiate(CsrMetadataSubject)
                  .setCountryName("US")
                  .setStateOrProvinceName("California")
                  .setLocalityName("San Francisco")
                  .setOrganizationName("Okta, Inc.")
                  .setOrganizationalUnitName("Dev")
                  .setCommonName("SP Issuer"))
              .setSubjectAltNames(client.instantiate(CsrMetadataSubjectAltNames)
                  .setDnsNames(["dev.okta.com"]))

        // generate csr with metadata
        Csr csr = app.generateCsr(csrMetadata)

        // verify
        assertPresent(app.listCsrs(), csr)

        // revoke csr
        app.revokeCsr(csr.getId())

        // verify
        assertNotPresent(app.listCsrs(), csr)
    }

    @Test
    void oAuth2ScopeConsentGrantTest() {
        Client client = getClient()

        String label = "app-${uniqueTestName}"
        Application app = client.instantiate(OpenIdConnectApplication)
            .setLabel(label)
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
                    .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_POST)))
        client.createApplication(app)
        registerForCleanup(app)

        assertThat(app.getStatus(), equalTo(Application.StatusEnum.ACTIVE))

        // grant consent
        OAuth2ScopeConsentGrant oAuth2ScopeConsentGrant = app.grantConsentToScope(client.instantiate(OAuth2ScopeConsentGrant)
            .setIssuer(client.dataStore.baseUrlResolver.baseUrl)
            .setScopeId("okta.apps.manage"))

        // verify
        assertPresent(app.listScopeConsentGrants(), app.getScopeConsentGrant(oAuth2ScopeConsentGrant.getId()))

        // revoke consent
        app.revokeScopeConsentGrant(oAuth2ScopeConsentGrant.getId())

        // verify
        assertNotPresent(app.listScopeConsentGrants(), app.getScopeConsentGrant(oAuth2ScopeConsentGrant.getId()))
    }

    @Test
    void testExecuteWithoutAcceptHeader() {
        def app = client.instantiate(SamlApplication)
            .setVisibility(client.instantiate(ApplicationVisibility))
            .setSettings(client.instantiate(SamlApplicationSettings)
                .setSignOn(client.instantiate(SamlApplicationSettingsSignOn)
                    .setSsoAcsUrl("http://testorgone.okta")
                    .setAudience("asdqwe123")
                    .setRecipient("http://testorgone.okta")
                    .setDestination("http://testorgone.okta")
                    .setAssertionSigned(true)
                    .setSignatureAlgorithm("RSA_SHA256")
                    .setDigestAlgorithm("SHA256")
                )
            )
        def dataStore = (DefaultDataStore) client.getDataStore()
        def resource = create(client, app)
        def url = resource.getLinks().get("users")["href"]
        registerForCleanup(resource)

        Resource response = dataStore.getResource(url as String, Application.class)

        assertThat(response.isEmpty(), is(false))
        assertThat(response.size(), is(3))
    }

    @Test
    void testExecuteAcceptIonPlusJson() {
        def app = client.instantiate(SamlApplication)
            .setVisibility(client.instantiate(ApplicationVisibility))
            .setSettings(client.instantiate(SamlApplicationSettings)
                .setSignOn(client.instantiate(SamlApplicationSettingsSignOn)
                    .setSsoAcsUrl("http://testorgone.okta")
                    .setAudience("asdqwe123")
                    .setRecipient("http://testorgone.okta")
                    .setDestination("http://testorgone.okta")
                    .setAssertionSigned(true)
                    .setSignatureAlgorithm("RSA_SHA256")
                    .setDigestAlgorithm("SHA256")
                )
            )
        def dataStore = (DefaultDataStore) client.getDataStore()
        def resource = create(client, app)
        def url = resource.getLinks().get("users")["href"]
        def headers = Collections.singletonMap("Accept", Collections.singletonList("application/ion+json"))
        registerForCleanup(resource)

        Resource response = dataStore.getResource(url as String, Application.class, null, headers)

        assertThat(response.isEmpty(), is(false))
        assertThat(response.size(), is(3))
    }

    @Test
    void testGetRawResponse() {
        def app = client.instantiate(SamlApplication)
            .setVisibility(client.instantiate(ApplicationVisibility))
            .setSettings(client.instantiate(SamlApplicationSettings)
                .setSignOn(client.instantiate(SamlApplicationSettingsSignOn)
                    .setSsoAcsUrl("http://testorgone.okta")
                    .setAudience("asdqwe123")
                    .setRecipient("http://testorgone.okta")
                    .setDestination("http://testorgone.okta")
                    .setAssertionSigned(true)
                    .setSignatureAlgorithm("RSA_SHA256")
                    .setDigestAlgorithm("SHA256")
                )
            )
        def dataStore = (DefaultDataStore) client.getDataStore()
        def resource = create(client, app)
        def url = resource.getLinks().get("metadata")["href"]
        def headers = Collections.singletonMap("Accept", Collections.singletonList(MediaType.APPLICATION_XML as String))
        registerForCleanup(resource)

        InputStream response = dataStore.getRawResponse(url as String, null, headers)

        assertThat(resource.isEmpty(), is(false))
        String x509Certificate = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(response)
            .getElementsByTagName("ds:X509Certificate")
            .item(0)
            .getFirstChild()
            .getNodeValue()
        assertThat(x509Certificate.isBlank(), is(false))
    }

    @Test
    void getApplicationUserSchemaTest() {

        Application createdApp = client.instantiate(AutoLoginApplication)
            .setLabel("app-${uniqueTestName}")
            .setVisibility(client.instantiate(ApplicationVisibility)
                .setAutoSubmitToolbar(false)
                .setHide(client.instantiate(ApplicationVisibilityHide)
                    .setIOS(false)
                    .setWeb(false)))
            .setSettings(client.instantiate(AutoLoginApplicationSettings)
                .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                    .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                    .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(createdApp)
        registerForCleanup(createdApp)

        def userSchema = client.getApplicationUserSchema(createdApp.getId())
        assertThat(userSchema, notNullValue())
        assertThat(userSchema.getName(), notNullValue())
        assertThat(userSchema.getTitle(), notNullValue())
        assertThat(userSchema.getType(), notNullValue())
        assertThat(userSchema.getDefinitions(), notNullValue())

        def userSchemaBase = userSchema.getDefinitions().getBase()
        assertThat(userSchemaBase, notNullValue())
        userSchemaBase.getRequired().forEach({ requiredItem ->
            assertThat(userSchemaBase.getProperties().containsKey(requiredItem), equalTo(true))
        })
    }

    @Test
    void updateApplicationUserProfileTest() {

        Application createdApp = client.instantiate(AutoLoginApplication)
            .setLabel("app-${uniqueTestName}")
            .setVisibility(client.instantiate(ApplicationVisibility)
                .setAutoSubmitToolbar(false)
                .setHide(client.instantiate(ApplicationVisibilityHide)
                    .setIOS(false)
                    .setWeb(false)))
            .setSettings(client.instantiate(AutoLoginApplicationSettings)
                .setSignOn(client.instantiate(AutoLoginApplicationSettingsSignOn)
                    .setRedirectUrl("http://swasecondaryredirecturl.okta.com")
                    .setLoginUrl("http://swaprimaryloginurl.okta.com")))
        client.createApplication(createdApp)
        registerForCleanup(createdApp)

        def userSchema = client.getApplicationUserSchema(createdApp.getId())
        assertThat(userSchema, notNullValue())
        assertThat(userSchema.getDefinitions(), notNullValue())

        def userSchemaToUpdate = client.instantiate(UserSchema)

        userSchemaToUpdate.setDefinitions(
            client.instantiate(UserSchemaDefinitions)
                .setCustom(
                    client.instantiate(UserSchemaPublic)
                        .setProperties(new LinkedHashMap() {
                            {
                                put("twitterUserName",
                                    new LinkedHashMap() {
                                        {
                                            put("title", "Twitter username")
                                            put("description", "Username for twitter.com")
                                            put("type", "string")
                                            put("required", "false")
                                            put("minLength", 1)
                                            put("maxLength", 20)
                                        }
                                    })
                            }
                        })
                )
        )

        def updatedUserSchema = client.updateApplicationUserProfile(createdApp.getId(), userSchemaToUpdate)
        assertThat(updatedUserSchema, notNullValue())
        assertThat(updatedUserSchema.getDefinitions().getCustom(), notNullValue())

        def userSchemaPublic = updatedUserSchema.getDefinitions().getCustom()
        assertThat(userSchemaPublic.getProperties().containsKey("twitterUserName"), equalTo(true))

        def customPropertyMap = userSchemaPublic.getProperties().get("twitterUserName")
        assertThat(customPropertyMap["title"], equalTo("Twitter username"))
        assertThat(customPropertyMap["description"], equalTo("Username for twitter.com"))
        assertThat(customPropertyMap["type"], equalTo("string"))
        assertThat(customPropertyMap["required"], equalTo("false"))
        assertThat(customPropertyMap["minLength"], equalTo(1))
        assertThat(customPropertyMap["maxLength"], equalTo(20))
    }
}

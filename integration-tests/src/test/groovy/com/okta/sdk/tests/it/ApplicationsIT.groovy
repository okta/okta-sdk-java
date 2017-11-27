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
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.application.ApplicationCredentialsOAuthClient
import com.okta.sdk.resource.application.ApplicationCredentialsScheme
import com.okta.sdk.resource.application.ApplicationSignOnMode
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
import org.testng.ITest
import org.testng.annotations.DataProvider
import org.testng.annotations.Factory

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo

/**
 * Tests for /api/v1/apps
 * @since 0.9.0
 */
class ApplicationsIT implements CrudTestSupport, ITest {

    private final ApplicationCreator applicationCreator
    private final String name

    @Factory(dataProvider = "appCreator")
    ApplicationsIT(String name, ApplicationCreator applicationCreator) {
        this.applicationCreator = applicationCreator
        this.name = name
    }

    @Override
    String getTestName() {
        return name
    }

    @Override
    def create(Client client) {
        return client.createApplication(applicationCreator.createApplication(client))
    }

    @Override
    def read(Client client, String id) {
        return client.getApplication(id)
    }

    @Override
    void update(Client client, def resource) {
        String newLabel = resource.label + "-update"
        resource.label = newLabel
        resource.update()

        // make sure the label was updated
        assertThat read(client, resource.id).label, equalTo(newLabel)
    }

    @Override
    void delete(Client client, def resource) {
        resource.deactivate()
        resource.delete()
    }

    @Override
    Iterator getResourceCollectionIterator(Client client) {
        return client.listApplications().iterator()
    }

    @DataProvider
    static Object[][] appCreator() {
        return [
                [ ApplicationSignOnMode.OPENID_CONNECT.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"

                        return client.instantiate(OpenIdConnectApplication)
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
                    }
                } ],
                [ ApplicationSignOnMode.AUTO_LOGIN.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(AutoLoginApplication)
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
                    }
                } ],
                [ ApplicationSignOnMode.WS_FEDERATION.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(WsFederationApplication)
                                .setLabel(label)
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
                                        .setUsernameAttribute("username")))
                    }
                } ],
                [ ApplicationSignOnMode.SAML_2_0.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(SamlApplication)
                                .setLabel(label)
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
                                                        .setValues(["Value"])
                                            ]))))
                    }
                } ],
                [ ApplicationSignOnMode.SECURE_PASSWORD_STORE.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(SecurePasswordStoreApplication)
                            .setLabel(label)
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
                                    .setOptionalField3Value("finalvalue")))
                    }
                } ],
                [ ApplicationSignOnMode.BROWSER_PLUGIN.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(SwaApplication)
                                .setLabel(label)
                                .setSettings(client.instantiate(SwaApplicationSettings)
                                    .setApp(client.instantiate(SwaApplicationSettingsApplication)
                                        .setButtonField("btn-login")
                                        .setPasswordField("txtbox-password")
                                        .setUsernameField("txtbox-username")
                                        .setUrl("https://example.com/login.html")))
//                                        .setLoginUrlRegex("REGEX_EXPRESSION")
                    }
                } ],
                [ ApplicationSignOnMode.BASIC_AUTH.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(BasicAuthApplication)
                                .setLabel(label)
                                .setSettings(client.instantiate(BasicApplicationSettings)
                                    .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                        .setAuthURL("https://example.com/auth.html")
                                        .setUrl("https://example.com/login.html")))
                    }
                } ],
                [ ApplicationSignOnMode.BASIC_AUTH.toString() +"--"+ ApplicationCredentialsScheme.EDIT_USERNAME_AND_PASSWORD.toString(),
                  new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(BasicAuthApplication)
                                .setLabel(label)
                                .setCredentials(client.instantiate(SchemeApplicationCredentials)
                                    .setScheme(ApplicationCredentialsScheme.EDIT_USERNAME_AND_PASSWORD))
                                .setSettings(client.instantiate(BasicApplicationSettings)
                                    .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                        .setAuthURL("https://example.com/auth.html")
                                        .setUrl("https://example.com/login.html")))
                    }
                } ],
                [ ApplicationSignOnMode.BASIC_AUTH.toString()+"--"+ ApplicationCredentialsScheme.ADMIN_SETS_CREDENTIALS.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(BasicAuthApplication)
                                .setLabel(label)
                                .setCredentials(client.instantiate(SchemeApplicationCredentials)
                                    .setScheme(ApplicationCredentialsScheme.ADMIN_SETS_CREDENTIALS)
                                    .setRevealPassword(true))
                                .setSettings(client.instantiate(BasicApplicationSettings)
                                    .setApp(client.instantiate(BasicApplicationSettingsApplication)
                                        .setAuthURL("https://example.com/auth.html")
                                        .setUrl("https://example.com/login.html")))
                    }
                } ],
                [ ApplicationSignOnMode.BOOKMARK.toString(), new ApplicationCreator() {
                    @Override
                    Application createApplication(Client client) {
                        String label = "app-${UUID.randomUUID().toString()}"
                        return client.instantiate(BookmarkApplication)
                                .setLabel(label)
                                .setSettings(client.instantiate(BookmarkApplicationSettings)
                                    .setApp(client.instantiate(BookmarkApplicationSettingsApplication)
                                        .setRequestIntegration(false)
                                        .setUrl("https://example.com/bookmark.htm")))
                    }
                } ],
        ]
    }
}

interface ApplicationCreator {
    Application createApplication(Client client)
}
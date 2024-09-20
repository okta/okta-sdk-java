/*
 * Copyright 2017-Present Okta, Inc.
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

import com.google.common.net.HttpHeaders
import com.okta.sdk.resource.model.Application
import com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient
import com.okta.sdk.resource.model.ApplicationLifecycleStatus
import com.okta.sdk.resource.model.ApplicationSignOnMode
import com.okta.sdk.resource.model.ApplicationVisibility
import com.okta.sdk.resource.model.ApplicationVisibilityHide
import com.okta.sdk.resource.model.BasicApplicationSettings
import com.okta.sdk.resource.model.BasicApplicationSettingsApplication
import com.okta.sdk.resource.model.BasicAuthApplication
import com.okta.sdk.resource.model.BookmarkApplication
import com.okta.sdk.resource.model.BookmarkApplicationSettings
import com.okta.sdk.resource.model.BookmarkApplicationSettingsApplication
import com.okta.sdk.resource.model.BrowserPluginApplication
import com.okta.sdk.resource.model.InlineHook
import com.okta.sdk.resource.model.InlineHookChannelConfig
import com.okta.sdk.resource.model.InlineHookChannelConfigAuthScheme
import com.okta.sdk.resource.model.InlineHookChannelConfigHeaders
import com.okta.sdk.resource.model.InlineHookChannelHttp
import com.okta.sdk.resource.model.InlineHookChannelType
import com.okta.sdk.resource.model.InlineHookType
import com.okta.sdk.resource.model.OAuthApplicationCredentials
import com.okta.sdk.resource.model.OAuthEndpointAuthenticationMethod
import com.okta.sdk.resource.model.OAuthGrantType
import com.okta.sdk.resource.model.OAuthResponseType
import com.okta.sdk.resource.model.OpenIdConnectApplication
import com.okta.sdk.resource.model.OpenIdConnectApplicationConsentMethod
import com.okta.sdk.resource.model.OpenIdConnectApplicationIssuerMode
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettings
import com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClient
import com.okta.sdk.resource.model.OpenIdConnectApplicationType
import com.okta.sdk.resource.model.SamlApplication
import com.okta.sdk.resource.model.SamlApplicationSettings
import com.okta.sdk.resource.model.SamlApplicationSettingsSignOn
import com.okta.sdk.resource.model.SamlAttributeStatement
import com.okta.sdk.resource.model.SignOnInlineHook
import com.okta.sdk.resource.model.SwaApplicationSettings
import com.okta.sdk.resource.model.SwaApplicationSettingsApplication
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.InlineHookApi
import com.okta.sdk.resource.model.*
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.5.0
 */
class AppsIT extends ITSupport {

    String prefix = "java-sdk-it-"

    ApplicationApi applicationApi = new ApplicationApi(getClient())

    @Test
    void basicAuthAppTest() {

        BasicAuthApplication basicAuthApplication = new BasicAuthApplication()
        basicAuthApplication.name(BasicAuthApplication.NameEnum.TEMPLATE_BASIC_AUTH)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BASIC_AUTH)
        BasicApplicationSettingsApplication basicApplicationSettingsApplication =
            new BasicApplicationSettingsApplication()
        basicApplicationSettingsApplication.url("https://example.com/login.html")
            .authURL("https://example.com/auth.html")
        BasicApplicationSettings basicApplicationSettings = new BasicApplicationSettings()
        basicApplicationSettings.app(basicApplicationSettingsApplication)
        basicAuthApplication.settings(basicApplicationSettings)

        BasicAuthApplication createdApp =
            applicationApi.createApplication(basicAuthApplication, true, null) as BasicAuthApplication
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(basicAuthApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BASIC_AUTH))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    @Test
    void bookmarkAppTest() {

        BookmarkApplication bookmarkApplication = new BookmarkApplication()
        bookmarkApplication.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        BookmarkApplicationSettingsApplication bookmarkApplicationSettingsApplication =
            new BookmarkApplicationSettingsApplication()
        bookmarkApplicationSettingsApplication.url("https://example.com/bookmark.htm")
            .requestIntegration(false)
        BookmarkApplicationSettings bookmarkApplicationSettings = new BookmarkApplicationSettings()
        bookmarkApplicationSettings.app(bookmarkApplicationSettingsApplication)
        bookmarkApplication.settings(bookmarkApplicationSettings)

        // create
        BookmarkApplication createdApp =
            applicationApi.createApplication(bookmarkApplication, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(bookmarkApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BOOKMARK))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = bookmarkApplication.label("updated-" + bookmarkApplication.getLabel())
        BookmarkApplication updatedApp =
            applicationApi.replaceApplication(createdApp.getId(), toBeUpdatedApp) as BookmarkApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        BookmarkApplication retrievedApp = (BookmarkApplication) applicationApi.getApplication(createdApp.getId(), null)

        assertThat(retrievedApp, notNullValue())
        assertThat(retrievedApp.getId(), equalTo(updatedApp.getId()))
        assertThat(retrievedApp.getLabel(), equalTo(updatedApp.getLabel()))
        assertThat(retrievedApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BOOKMARK))
        assertThat(retrievedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    @Test
    void browserPluginAppTest() {

        SwaApplicationSettingsApplication swaApplicationSettingsApplication = new SwaApplicationSettingsApplication()
        swaApplicationSettingsApplication.buttonField("btn-login")
            .passwordField("txtbox-password")
            .usernameField("txtbox-username")
            .url("https://example.com/login.html")
        SwaApplicationSettings swaApplicationSettings = new SwaApplicationSettings()
        swaApplicationSettings.app(swaApplicationSettingsApplication)
        BrowserPluginApplication browserPluginApplication = new BrowserPluginApplication()
        browserPluginApplication.name(BrowserPluginApplication.NameEnum.SWA)
        browserPluginApplication.label(prefix + UUID.randomUUID().toString())
        browserPluginApplication.settings(swaApplicationSettings)

        // create
        BrowserPluginApplication createdApp =
            applicationApi.createApplication(browserPluginApplication, true, null) as BrowserPluginApplication
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(browserPluginApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BROWSER_PLUGIN))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = browserPluginApplication.label("updated-" + browserPluginApplication.getLabel())
        BrowserPluginApplication updatedApp =
            applicationApi.replaceApplication(createdApp.getId(), toBeUpdatedApp) as BrowserPluginApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        BrowserPluginApplication retrievedApp = (BrowserPluginApplication) applicationApi.getApplication(createdApp.getId(), null)

        assertThat(retrievedApp, notNullValue())
        assertThat(retrievedApp.getId(), equalTo(updatedApp.getId()))
        assertThat(retrievedApp.getLabel(), equalTo(updatedApp.getLabel()))
        assertThat(retrievedApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BROWSER_PLUGIN))
        assertThat(retrievedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    @Test
    void oidcAppTest() {

        OpenIdConnectApplication openIdConnectApplication = new OpenIdConnectApplication()
        openIdConnectApplication.label(prefix + UUID.randomUUID().toString())
        openIdConnectApplication.name("oidc_client")
        OpenIdConnectApplicationSettingsClient openIdConnectApplicationSettingsClient =
            new OpenIdConnectApplicationSettingsClient()
        openIdConnectApplicationSettingsClient.applicationType(OpenIdConnectApplicationType.WEB)
        openIdConnectApplicationSettingsClient.consentMethod(OpenIdConnectApplicationConsentMethod.REQUIRED)
        openIdConnectApplicationSettingsClient.clientUri("https://example.com/client")
        openIdConnectApplicationSettingsClient.logoUri("https://example.com/assets/images/logo-new.png")
        openIdConnectApplicationSettingsClient.redirectUris(["https://example.com/oauth2/callback",
                                                                "myapp://callback"])
        openIdConnectApplicationSettingsClient.responseTypes([OAuthResponseType.TOKEN, OAuthResponseType.ID_TOKEN, OAuthResponseType.CODE])
        openIdConnectApplicationSettingsClient.issuerMode(OpenIdConnectApplicationIssuerMode.ORG_URL)
        openIdConnectApplicationSettingsClient.grantTypes([OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE])
        openIdConnectApplicationSettingsClient.applicationType(OpenIdConnectApplicationType.NATIVE)
        openIdConnectApplicationSettingsClient.tosUri("https://example.com/client/tos")
        openIdConnectApplicationSettingsClient.policyUri("https://example.com/client/policy")
        OpenIdConnectApplicationSettings openIdConnectApplicationSettings =
            new OpenIdConnectApplicationSettings()
        openIdConnectApplicationSettings.oauthClient(openIdConnectApplicationSettingsClient)
        openIdConnectApplication.settings(openIdConnectApplicationSettings)

        ApplicationCredentialsOAuthClient applicationCredentialsOAuthClient = new ApplicationCredentialsOAuthClient()
        applicationCredentialsOAuthClient.clientId(UUID.randomUUID().toString())
        applicationCredentialsOAuthClient.autoKeyRotation(true)
        applicationCredentialsOAuthClient.tokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.CLIENT_SECRET_BASIC)
        OAuthApplicationCredentials oAuthApplicationCredentials =
            new OAuthApplicationCredentials()
        oAuthApplicationCredentials.oauthClient(applicationCredentialsOAuthClient)
        openIdConnectApplication.credentials(oAuthApplicationCredentials)
        openIdConnectApplication.signOnMode(ApplicationSignOnMode.OPENID_CONNECT)

        // create
        OpenIdConnectApplication createdApp =
            applicationApi.createApplication(openIdConnectApplication, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(openIdConnectApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.OPENID_CONNECT))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = openIdConnectApplication.label("updated-" + openIdConnectApplication.getLabel())
        OpenIdConnectApplication updatedApp =
            applicationApi.replaceApplication(createdApp.getId(), toBeUpdatedApp) as OpenIdConnectApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        OpenIdConnectApplication retrievedApp = (OpenIdConnectApplication) applicationApi.getApplication(createdApp.getId(), null)

        assertThat(retrievedApp, notNullValue())
        assertThat(retrievedApp.getId(), equalTo(updatedApp.getId()))
        assertThat(retrievedApp.getLabel(), equalTo(updatedApp.getLabel()))
        assertThat(retrievedApp.getSignOnMode(), equalTo(ApplicationSignOnMode.OPENID_CONNECT))
        assertThat(retrievedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    @Test
    void samlAppTest() {

        String name = prefix + UUID.randomUUID().toString()
        String version = "1.0.0"

        InlineHookChannelConfigAuthScheme inlineHookChannelConfigAuthScheme = new InlineHookChannelConfigAuthScheme()
        inlineHookChannelConfigAuthScheme.type("HEADER")
        inlineHookChannelConfigAuthScheme.key(HttpHeaders.AUTHORIZATION)
        inlineHookChannelConfigAuthScheme.value("Test-Api-Key")

        InlineHookChannelConfigHeaders inlineHookChannelConfigHeaders = new InlineHookChannelConfigHeaders()
        inlineHookChannelConfigHeaders.key("X-Test-Header")
            .value("Test header value")

        List<InlineHookChannelConfigHeaders> headers = new ArrayList<InlineHookChannelConfigHeaders>()
        headers.add(inlineHookChannelConfigHeaders)

        InlineHookChannelConfig inlineHookChannelConfig = new InlineHookChannelConfig()
            .uri("https://www.example.com/inlineHooks")
            .headers(headers)
            .authScheme(inlineHookChannelConfigAuthScheme)

        InlineHookChannelHttp inlineHookChannel = new InlineHookChannelHttp()
        inlineHookChannel.type(InlineHookChannelType.HTTP)
        inlineHookChannel.version(version)
        inlineHookChannel.config(inlineHookChannelConfig)

        InlineHookApi inlineHookApi = new InlineHookApi(getClient())
        InlineHook inlineHook = new InlineHook()
        inlineHook.name(name)
        inlineHook.type(InlineHookType.SAML_TOKENS_TRANSFORM)
        inlineHook.version(version)
        inlineHook.channel(inlineHookChannel)

        InlineHook createdInlineHook = inlineHookApi.createInlineHook(inlineHook)
        registerForCleanup(createdInlineHook)

        SamlApplication samlApplication = new SamlApplication()
        samlApplication.label(prefix + UUID.randomUUID().toString())

        ApplicationVisibility applicationVisibility = new ApplicationVisibility()
        applicationVisibility.autoSubmitToolbar(false)
        ApplicationVisibilityHide applicationVisibilityHide = new ApplicationVisibilityHide()
        applicationVisibilityHide.iOS(false)
            .web(false)
        applicationVisibility.hide(applicationVisibilityHide)

        SamlAttributeStatement samlAttributeStatement = new SamlAttributeStatement()
        samlAttributeStatement.type("EXPRESSION")
            .name("Attribute")
            .namespace("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified")
            .values(["Value"])

        List<SamlAttributeStatement> samlAttributeStatementList = new ArrayList<>()
        samlAttributeStatementList.add(samlAttributeStatement)

        SamlApplicationSettings samlApplicationSettings = new SamlApplicationSettings()
        SamlApplicationSettingsSignOn samlApplicationSettingsSignOn = new SamlApplicationSettingsSignOn()
        samlApplicationSettingsSignOn.defaultRelayState("")
            .ssoAcsUrl("http://testorgone.okta")
            .idpIssuer('https://www.okta.com/${org.externalKey}')
            .audience("asdqwe123")
            .recipient("http://testorgone.okta")
            .destination("http://testorgone.okta")
            .subjectNameIdTemplate('${user.userName}')
            .subjectNameIdFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")
            .responseSigned(true)
            .assertionSigned(true)
            .signatureAlgorithm("RSA_SHA256")
            .digestAlgorithm("SHA256")
            .honorForceAuthn(true)
            .authnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")
            .spIssuer(null)
            .requestCompressed(false)
            .attributeStatements(samlAttributeStatementList)

        SignOnInlineHook signOnInlineHook = new SignOnInlineHook()
        signOnInlineHook.id(createdInlineHook.getId())

        samlApplicationSettings.signOn(samlApplicationSettingsSignOn)
        samlApplication.visibility(applicationVisibility)
        samlApplication.settings(samlApplicationSettings)
        samlApplication.signOnMode(ApplicationSignOnMode.SAML_2_0)

        // create
        SamlApplication createdApp =
            applicationApi.createApplication(samlApplication, true, null) as SamlApplication
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(samlApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.SAML_2_0))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = samlApplication.label("updated-" + samlApplication.getLabel())
        SamlApplication updatedApp =
            applicationApi.replaceApplication(createdApp.getId(), toBeUpdatedApp) as SamlApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        SamlApplication retrievedApp = (SamlApplication) applicationApi.getApplication(createdApp.getId(), null)

        assertThat(retrievedApp, notNullValue())
        assertThat(retrievedApp.getId(), equalTo(updatedApp.getId()))
        assertThat(retrievedApp.getLabel(), equalTo(updatedApp.getLabel()))
        assertThat(retrievedApp.getSignOnMode(), equalTo(ApplicationSignOnMode.SAML_2_0))
        assertThat(retrievedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }
}

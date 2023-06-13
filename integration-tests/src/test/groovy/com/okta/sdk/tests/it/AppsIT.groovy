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
import com.okta.sdk.helper.ApplicationApiHelper
import com.okta.sdk.tests.it.util.ITSupport
import org.openapitools.client.api.ApplicationApi
import org.openapitools.client.api.InlineHookApi
import org.openapitools.client.model.*
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThanOrEqualTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.5.0
 */
class AppsIT extends ITSupport {

    String prefix = "java-sdk-it-"

    ApplicationApiHelper<Application> applicationApiHelper = new ApplicationApiHelper<>(new ApplicationApi(getClient()))

    @Test
    void basicAuthAppTest() {

        BasicAuthApplication basicAuthApplication = new BasicAuthApplication()
        basicAuthApplication.name("template_basic_auth")
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
            applicationApiHelper.createApplicationOfType(BasicAuthApplication.class, basicAuthApplication, true, null)
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
        bookmarkApplication.name("bookmark")
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
            applicationApiHelper.createApplicationOfType(BookmarkApplication.class, bookmarkApplication, true, null)
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(bookmarkApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BOOKMARK))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = bookmarkApplication.label("updated-" + bookmarkApplication.getLabel())
        BookmarkApplication updatedApp =
            applicationApiHelper.replaceApplicationOfType(BookmarkApplication.class, createdApp.getId(), toBeUpdatedApp) as BookmarkApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        Application retrievedApp = applicationApiHelper.getApplication(createdApp.getId(), null)

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
        browserPluginApplication.name("template_swa")
        browserPluginApplication.label(prefix + UUID.randomUUID().toString())
        browserPluginApplication.settings(swaApplicationSettings)

        // create
        BrowserPluginApplication createdApp =
            applicationApiHelper.createApplicationOfType(BrowserPluginApplication.class, browserPluginApplication, true, null)
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(browserPluginApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BROWSER_PLUGIN))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = browserPluginApplication.label("updated-" + browserPluginApplication.getLabel())
        BrowserPluginApplication updatedApp =
            applicationApiHelper.replaceApplicationOfType(BrowserPluginApplication.class, createdApp.getId(), toBeUpdatedApp) as BrowserPluginApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        Application retrievedApp = applicationApiHelper.getApplication(createdApp.getId(), null)

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
            applicationApiHelper.createApplicationOfType(OpenIdConnectApplication.class, openIdConnectApplication, true, null)
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(openIdConnectApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.OPENID_CONNECT))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = openIdConnectApplication.label("updated-" + openIdConnectApplication.getLabel())
        OpenIdConnectApplication updatedApp =
            applicationApiHelper.replaceApplicationOfType(OpenIdConnectApplication.class, createdApp.getId(), toBeUpdatedApp) as OpenIdConnectApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        Application retrievedApp = applicationApiHelper.getApplication(createdApp.getId(), null)

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
            applicationApiHelper.createApplicationOfType(SamlApplication.class, samlApplication, true, null)
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(samlApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.SAML_2_0))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // update
        Application toBeUpdatedApp = samlApplication.label("updated-" + samlApplication.getLabel())
        SamlApplication updatedApp =
            applicationApiHelper.replaceApplicationOfType(SamlApplication.class, createdApp.getId(), toBeUpdatedApp) as SamlApplication

        assertThat(updatedApp.getId(), equalTo(createdApp.getId()))

        // retrieve
        Application retrievedApp = applicationApiHelper.getApplication(createdApp.getId(), null)

        assertThat(retrievedApp, notNullValue())
        assertThat(retrievedApp.getId(), equalTo(updatedApp.getId()))
        assertThat(retrievedApp.getLabel(), equalTo(updatedApp.getLabel()))
        assertThat(retrievedApp.getSignOnMode(), equalTo(ApplicationSignOnMode.SAML_2_0))
        assertThat(retrievedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    //TODO: fix me
    @Test
    void testUploadApplicationLogo() {
        /**
         * Currently there is no way to check the logo.
         * Just make sure that no exception was thrown during the upload.
         */

        SamlApplication org2OrgApplication = new SamlApplication()
        org2OrgApplication.name("okta_org2org")
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.SAML_2_0)

        SamlApplicationSettingsApplication samlApplicationSettingsApplication = new SamlApplicationSettingsApplication()
        samlApplicationSettingsApplication.setAcsUrl("https://example.com/acs.html")
        samlApplicationSettingsApplication.setAudRestriction("https://example.com/login.html")
        samlApplicationSettingsApplication.setBaseUrl("https://example.com/home.html")
        SamlApplicationSettings samlApplicationSettings = new SamlApplicationSettings()
        samlApplicationSettings.app(samlApplicationSettingsApplication)
        org2OrgApplication.settings(samlApplicationSettings)

        SamlApplication createdApp = applicationApiHelper.createApplicationOfType(SamlApplication.class, org2OrgApplication, true, null)
        registerForCleanup(createdApp)

//        File file = new File("/tmp/okta_logo_favicon.png")
//        println("Uploading logo file " + file.getName() + " of size: " + file.size())
//
//        applicationApi.uploadApplicationLogo(createdApp.getId(), file)
    }

    @Test
    void testApplicationApiHelper() {

        BookmarkApplication bookmarkApplication = new BookmarkApplication()
        bookmarkApplication.name("bookmark")
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
            applicationApiHelper.createApplicationOfType(BookmarkApplication.class, bookmarkApplication, true, null)
        registerForCleanup(createdApp)

        assertThat(createdApp, notNullValue())
        assertThat(createdApp.getId(), notNullValue())
        assertThat(createdApp.getLabel(), equalTo(bookmarkApplication.getLabel()))
        assertThat(createdApp.getSignOnMode(), equalTo(ApplicationSignOnMode.BOOKMARK))
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // retrieve app (sub-typed)
        BookmarkApplication retrievedBookmarkApplication =
            applicationApiHelper.getApplication(createdApp.getId(), null) as BookmarkApplication
        assertThat(retrievedBookmarkApplication, notNullValue())
        assertThat(retrievedBookmarkApplication.getId(), equalTo(createdApp.getId()))
        assertThat(retrievedBookmarkApplication.getLabel(), equalTo(createdApp.getLabel()))
        assertThat(retrievedBookmarkApplication.getSignOnMode(), equalTo(createdApp.getSignOnMode()))
        assertThat(retrievedBookmarkApplication.getStatus(), equalTo(createdApp.getStatus()))

        List<Application> retrievedApplications =
            applicationApiHelper.listApplications(null, null, null, null, null, true)
        assertThat(retrievedApplications, notNullValue())
        assertThat(retrievedApplications, hasSize(greaterThanOrEqualTo(1)))
    }
}

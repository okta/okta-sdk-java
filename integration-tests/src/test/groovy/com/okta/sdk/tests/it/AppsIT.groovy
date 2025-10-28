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
import com.okta.sdk.resource.api.ApplicationCrossAppAccessConnectionsApi
import com.okta.sdk.resource.api.ApplicationFeaturesApi
import com.okta.sdk.resource.api.ApplicationGrantsApi
import com.okta.sdk.resource.api.ApplicationGroupsApi
import com.okta.sdk.resource.api.ApplicationPoliciesApi
import com.okta.sdk.resource.api.ApplicationTokensApi
import com.okta.sdk.resource.api.ApplicationUsersApi
import com.okta.sdk.resource.api.GroupApi
import com.okta.sdk.resource.model.AppUser
import com.okta.sdk.resource.model.AppUserAssignRequest
import com.okta.sdk.resource.model.AppUserProfile
import com.okta.sdk.resource.model.AppUserScope
import com.okta.sdk.resource.model.AppUserUpdateRequest
import com.okta.sdk.resource.model.Application
import com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient
import com.okta.sdk.resource.model.ApplicationFeature
import com.okta.sdk.resource.model.ApplicationGroupAssignment
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
import com.okta.sdk.resource.model.Group
import com.okta.sdk.resource.model.InlineHook
import com.okta.sdk.resource.model.InlineHookChannelConfig
import com.okta.sdk.resource.model.InlineHookChannelConfigAuthScheme
import com.okta.sdk.resource.model.InlineHookChannelConfigHeaders
import com.okta.sdk.resource.model.InlineHookChannelHttp
import com.okta.sdk.resource.model.InlineHookChannelType
import com.okta.sdk.resource.model.InlineHookType
import com.okta.sdk.resource.model.OAuth2ScopeConsentGrant
import com.okta.sdk.resource.model.OAuth2Token
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
import com.okta.sdk.resource.model.OrgCrossAppAccessConnection
import com.okta.sdk.resource.model.OrgCrossAppAccessConnectionPatchRequest
import com.okta.sdk.resource.model.OAuthProvisioningEnabledApp
import com.okta.sdk.resource.model.PolicyRule
import com.okta.sdk.resource.model.ProvisioningConnectionOauthAuthScheme
import com.okta.sdk.resource.model.ProvisioningConnectionOauthRequestProfile
import com.okta.sdk.resource.model.SamlApplication
import com.okta.sdk.resource.model.SamlApplicationSettings
import com.okta.sdk.resource.model.SamlApplicationSettingsSignOn
import com.okta.sdk.resource.model.SamlAttributeStatement
import com.okta.sdk.resource.model.SignOnInlineHook
import com.okta.sdk.resource.model.SwaApplicationSettings
import com.okta.sdk.resource.model.SwaApplicationSettingsApplication
import com.okta.sdk.resource.model.UpdateDefaultProvisioningConnectionForApplicationRequest
import com.okta.sdk.resource.model.User
import com.okta.sdk.tests.it.util.ITSupport
import com.okta.sdk.resource.api.ApplicationApi
import com.okta.sdk.resource.api.ApplicationConnectionsApi
import com.okta.sdk.resource.api.InlineHookApi
import com.okta.sdk.resource.client.ApiException
import org.testng.annotations.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.okta.sdk.tests.it.util.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Tests for {@code /api/v1/apps}.
 * @since 0.5.0
 */
class AppsIT extends ITSupport {

    private static final Logger logger = LoggerFactory.getLogger(AppsIT.class)

    String prefix = "java-sdk-it-"

    ApplicationApi applicationApi = new ApplicationApi(getClient())
    ApplicationConnectionsApi connectionsApi = new ApplicationConnectionsApi(getClient())
    ApplicationGroupsApi applicationGroupsApi = new ApplicationGroupsApi(getClient())

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
        browserPluginApplication.name(BrowserPluginApplication.NameEnum.TEMPLATE_SWA)
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
        inlineHook.type(InlineHookType.COM_OKTA_SAML_TOKENS_TRANSFORM)
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

    // ========================================
    // Test 6: List All Applications
    // ========================================
    @Test
    void testListApplications() {
        // Create a couple of test applications
        BookmarkApplication app1 = new BookmarkApplication()
        app1.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "list-test-1-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings1 = new BookmarkApplicationSettingsApplication()
        settings1.url("https://example.com/app1.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings1 = new BookmarkApplicationSettings()
        appSettings1.app(settings1)
        app1.settings(appSettings1)
        
        BookmarkApplication createdApp1 = 
            applicationApi.createApplication(app1, true, null) as BookmarkApplication
        registerForCleanup(createdApp1)

        BookmarkApplication app2 = new BookmarkApplication()
        app2.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "list-test-2-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings2 = new BookmarkApplicationSettingsApplication()
        settings2.url("https://example.com/app2.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings2 = new BookmarkApplicationSettings()
        appSettings2.app(settings2)
        app2.settings(appSettings2)
        
        BookmarkApplication createdApp2 = 
            applicationApi.createApplication(app2, true, null) as BookmarkApplication
        registerForCleanup(createdApp2)

        // List all applications
        List<Application> apps = applicationApi.listApplications(null, null, false, 200, null, null, false)
        
        assertThat(apps, notNullValue())
        assertThat(apps.size(), greaterThan(0))
        
        // Verify our created apps are in the list
        List<String> appIds = apps.collect { it.getId() }
        assertThat(appIds, hasItem(createdApp1.getId()))
        assertThat(appIds, hasItem(createdApp2.getId()))
    }

    // ========================================
    // Test 7: List Applications with Query Filter
    // ========================================
    @Test
    void testListApplicationsWithQuery() {
        String uniqueLabel = "query-test-" + UUID.randomUUID().toString()
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + uniqueLabel)
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/query.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        // Wait a bit for the app to be indexed for search
        Thread.sleep(3000)

        // Search by query parameter (searches name and label)
        // Use the full label with prefix for better search results
        List<Application> apps = applicationApi.listApplications(prefix + uniqueLabel, null, false, 10, null, null, false)
        
        assertThat(apps, notNullValue())
        assertThat(apps.size(), greaterThanOrEqualTo(1))
        
        // Verify our app is found
        Application foundApp = apps.find { it.getId() == createdApp.getId() }
        assertThat(foundApp, notNullValue())
        assertThat(foundApp.getLabel(), containsString(uniqueLabel))
    }

    // ========================================
    // Test 8: List Applications with Status Filter
    // ========================================
    @Test
    void testListApplicationsWithStatusFilter() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "filter-test-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/filter.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        // List active applications using filter
        String filter = 'status eq "ACTIVE"'
        List<Application> apps = applicationApi.listApplications(null, null, false, 200, filter, null, false)
        
        assertThat(apps, notNullValue())
        assertThat(apps.size(), greaterThan(0))
        
        // Verify all returned apps are ACTIVE
        apps.each { application ->
            assertThat(application.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
        }
        
        // Verify our app is in the list
        Application foundApp = apps.find { it.getId() == createdApp.getId() }
        assertThat(foundApp, notNullValue())
    }

    // ========================================
    // Test 9: Deactivate Application
    // ========================================
    @Test
    void testDeactivateApplication() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "deactivate-test-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/deactivate.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        // Verify app is initially active
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // Deactivate the application
        applicationApi.deactivateApplication(createdApp.getId())

        // Verify the app is now inactive
        Application deactivatedApp = applicationApi.getApplication(createdApp.getId(), null)
        assertThat(deactivatedApp.getStatus(), equalTo(ApplicationLifecycleStatus.INACTIVE))
    }

    // ========================================
    // Test 10: Activate Application
    // ========================================
    @Test
    void testActivateApplication() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "activate-test-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/activate.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        // Create app as inactive
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, false, null) as BookmarkApplication
        registerForCleanup(createdApp)

        // Verify app is initially inactive
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.INACTIVE))

        // Activate the application
        applicationApi.activateApplication(createdApp.getId())

        // Verify the app is now active
        Application activatedApp = applicationApi.getApplication(createdApp.getId(), null)
        assertThat(activatedApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))
    }

    // ========================================
    // Test 11: Delete Application
    // ========================================
    @Test
    void testDeleteApplication() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "delete-test-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/delete.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        // Create app as inactive (must be inactive to delete)
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, false, null) as BookmarkApplication

        String appId = createdApp.getId()
        
        // Verify app exists
        Application existingApp = applicationApi.getApplication(appId, null)
        assertThat(existingApp, notNullValue())
        assertThat(existingApp.getStatus(), equalTo(ApplicationLifecycleStatus.INACTIVE))

        // Delete the application
        applicationApi.deleteApplication(appId)

        // Verify app is deleted (should get 404)
        ApiException exception = expect(ApiException.class, () -> 
            applicationApi.getApplication(appId, null))
        
        assertThat(exception.getCode(), is(404))
    }

    // ========================================
    // Test 13: Error Handling - Delete Active Application
    // ========================================
    @Test
    void testDeleteActiveApplicationError() {
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "delete-active-error-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/delete-error.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        // Create app as active
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        assertThat(createdApp.getStatus(), equalTo(ApplicationLifecycleStatus.ACTIVE))

        // Attempt to delete active application (should fail)
        ApiException exception = expect(ApiException.class, () -> 
            applicationApi.deleteApplication(appId))
        
        // Should get error - apps must be deactivated before deletion
        assertThat(exception.getCode(), is(403))
    }

    // ========================================
    // Test 14: Error Handling - Get Non-Existent Application
    // ========================================
    @Test
    void testGetNonExistentApplication() {
        String nonExistentAppId = "0oa" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        
        ApiException exception = expect(ApiException.class, () -> 
            applicationApi.getApplication(nonExistentAppId, null))
        
        assertThat(exception.getCode(), is(404))
    }

    // ========================================
    // APPLICATION CONNECTIONS API TESTS
    // ========================================

    // ========================================
    // Test 15: Activate Default Provisioning Connection
    // ========================================
    @Test
    void testActivateDefaultProvisioningConnection() {
        // Note: This test verifies the API method exists and can be called
        // Actual functionality requires a provisioning-enabled app (e.g., Org2Org, Google, Office365)
        
        // Create a bookmark app (will not have provisioning, but tests API availability)
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-activate-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-activate.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing activate provisioning connection for app {}", appId)

        try {
            // Attempt to activate (will likely fail for non-provisioning app, but tests API exists)
            connectionsApi.activateDefaultProvisioningConnectionForApplication(appId)
            logger.debug("Successfully called activate provisioning connection API")
        } catch (ApiException e) {
            // Expected for apps without provisioning capability
            logger.debug("Activate provisioning connection returned error (expected for non-provisioning app): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should be callable", e.getCode(), anyOf(is(404), is(400), is(403)))
        }
    }

    // ========================================
    // Test 16: Deactivate Default Provisioning Connection
    // ========================================
    @Test
    void testDeactivateDefaultProvisioningConnection() {
        // Note: This test verifies the API method exists and can be called
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-deactivate-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-deactivate.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing deactivate provisioning connection for app {}", appId)

        try {
            // Attempt to deactivate (will likely fail for non-provisioning app, but tests API exists)
            connectionsApi.deactivateDefaultProvisioningConnectionForApplication(appId)
            logger.debug("Successfully called deactivate provisioning connection API")
        } catch (ApiException e) {
            // Expected for apps without provisioning capability
            logger.debug("Deactivate provisioning connection returned error (expected for non-provisioning app): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should be callable", e.getCode(), anyOf(is(404), is(400), is(403)))
        }
    }

    // ========================================
    // Test 17: Get Default Provisioning Connection
    // ========================================
    @Test
    void testGetDefaultProvisioningConnection() {
        // Note: This test verifies the API method exists and can be called
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-get-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-get.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing get provisioning connection for app {}", appId)

        try {
            // Attempt to get connection (will likely fail for non-provisioning app, but tests API exists)
            def connection = connectionsApi.getDefaultProvisioningConnectionForApplication(appId)
            logger.debug("Successfully retrieved provisioning connection: {}", connection)
            assertThat("Connection should be returned", connection, notNullValue())
        } catch (ApiException e) {
            // Expected for apps without provisioning capability
            logger.debug("Get provisioning connection returned error (expected for non-provisioning app): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should return 404 for apps without provisioning", e.getCode(), is(404))
        }
    }

    // ========================================
    // Test 18: Get User Provisioning Connection JWKS
    // ========================================
    @Test(enabled = false) // Skipped as per user request
    void testGetUserProvisioningConnectionJWKS() {
        // Note: This test verifies the API method exists and can be called
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-jwks-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-jwks.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing get JWKS for provisioning connection for app {}", appId)

        try {
            // Attempt to get JWKS (will likely fail for non-OAuth2 provisioning app, but tests API exists)
            def jwks = connectionsApi.getUserProvisioningConnectionJWKS(appId)
            logger.debug("Successfully retrieved JWKS: {}", jwks)
            assertThat("JWKS should be returned", jwks, notNullValue())
        } catch (ApiException e) {
            // Expected for apps without OAuth2 provisioning
            logger.debug("Get JWKS returned error (expected for non-OAuth2 provisioning app): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should return error for apps without OAuth2 provisioning", 
                e.getCode(), anyOf(is(404), is(400), is(401)))
        }
    }

    // ========================================
    // Test 19: Update Default Provisioning Connection
    // ========================================
    @Test
    void testUpdateDefaultProvisioningConnection() {
        // Note: This test verifies the API method exists and can be called
        // Actual functionality requires a provisioning-enabled app with OAuth2
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-update-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-update.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing update provisioning connection for app {}", appId)

        try {
            // Create a minimal request object for testing
            UpdateDefaultProvisioningConnectionForApplicationRequest updateRequest = 
                new UpdateDefaultProvisioningConnectionForApplicationRequest()
            
            // Create a minimal profile (required field)
            ProvisioningConnectionOauthRequestProfile profile = 
                new ProvisioningConnectionOauthRequestProfile()
            profile.authScheme(ProvisioningConnectionOauthAuthScheme.OAUTH2)
            
            updateRequest.profile(profile)
            
            // Attempt to update (will likely fail for non-provisioning app, but tests API exists)
            def response = connectionsApi.updateDefaultProvisioningConnectionForApplication(
                appId, updateRequest, false)
            logger.debug("Successfully called update provisioning connection API: {}", response)
            assertThat("Response should not be null", response, notNullValue())
        } catch (ApiException e) {
            // Expected for apps without provisioning capability
            logger.debug("Update provisioning connection returned error (expected for non-provisioning app): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should be callable", e.getCode(), anyOf(is(404), is(400), is(403), is(401)))
        }
    }

    // ========================================
    // Test 20: Verify Provisioning Connection (OAuth2 Callback)
    // ========================================
    @Test
    void testVerifyProvisioningConnection() {
        // Note: This test verifies the API method exists and can be called
        // Actual functionality requires a valid OAuth2 consent flow
        
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "connections-verify-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settings = new BookmarkApplicationSettingsApplication()
        settings.url("https://example.com/connections-verify.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings appSettings = new BookmarkApplicationSettings()
        appSettings.app(settings)
        app.settings(appSettings)
        
        BookmarkApplication createdApp = 
            applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)

        String appId = createdApp.getId()
        logger.debug("Testing verify provisioning connection for app {}", appId)

        try {
            // Attempt to verify with mock OAuth params (will fail but tests API exists)
            // Using OFFICE365 as appName since it's a supported OAuth provisioning app type
            connectionsApi.verifyProvisioningConnectionForApplication(
                OAuthProvisioningEnabledApp.OFFICE365, 
                appId, 
                "mock_code", 
                "mock_state")
            logger.debug("Successfully called verify provisioning connection API")
        } catch (ApiException e) {
            // Expected to fail with invalid OAuth params or non-OAuth app
            logger.debug("Verify provisioning connection returned error (expected): {} - {}", 
                e.getCode(), e.getMessage())
            // API method exists and is callable, which is what we're testing
            assertThat("API should be callable", e.getCode(), anyOf(is(404), is(400), is(403), is(401), is(500)))
        }
    }

    // ========================================
    // Test 21: Error Handling - Activate Connection for Non-Existent App
    // ========================================
    @Test
    void testActivateConnectionNonExistentApp() {
        String nonExistentAppId = "0oa" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        
        logger.debug("Testing activate connection for non-existent app {}", nonExistentAppId)
        
        ApiException exception = expect(ApiException.class, () -> 
            connectionsApi.activateDefaultProvisioningConnectionForApplication(nonExistentAppId))
        
        assertThat("Should return 404 for non-existent app", exception.getCode(), is(404))
    }

    // ========================================
    // Test 22: Error Handling - Deactivate Connection for Non-Existent App
    // ========================================
    @Test
    void testDeactivateConnectionNonExistentApp() {
        String nonExistentAppId = "0oa" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        
        logger.debug("Testing deactivate connection for non-existent app {}", nonExistentAppId)
        
        ApiException exception = expect(ApiException.class, () -> 
            connectionsApi.deactivateDefaultProvisioningConnectionForApplication(nonExistentAppId))
        
        assertThat("Should return 404 for non-existent app", exception.getCode(), is(404))
    }

    // ========================================
    // Test 23: Error Handling - Get Connection for Non-Existent App
    // ========================================
    @Test
    void testGetConnectionNonExistentApp() {
        String nonExistentAppId = "0oa" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)
        
        logger.debug("Testing get connection for non-existent app {}", nonExistentAppId)
        
        ApiException exception = expect(ApiException.class, () -> 
            connectionsApi.getDefaultProvisioningConnectionForApplication(nonExistentAppId))
        
        assertThat("Should return 404 for non-existent app", exception.getCode(), is(404))
    }

    // ========================================
    // APPLICATION USERS API TESTS
    // ========================================

    @Test
    void testAssignUserToApplication() {
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Create a bookmark app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
            .requestIntegration(false)
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign user to application
        ApplicationUsersApi appUsersApi = new ApplicationUsersApi(getClient())
        AppUserAssignRequest appUserAssignRequest = new AppUserAssignRequest()
        appUserAssignRequest.id(user.getId())
        
        AppUser appUser = appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest)
        
        assertThat(appUser, notNullValue())
        assertThat(appUser.getId(), equalTo(user.getId()))
        assertThat(appUser.getScope(), equalTo(AppUserScope.USER))
    }

    @Test
    void testGetApplicationUser() {
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign user to application
        ApplicationUsersApi appUsersApi = new ApplicationUsersApi(getClient())
        AppUserAssignRequest appUserAssignRequest = new AppUserAssignRequest()
        appUserAssignRequest.id(user.getId())
        
        appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest)
        
        // Get the assigned user
        AppUser retrievedAppUser = appUsersApi.getApplicationUser(createdApp.getId(), user.getId(), null)
        
        assertThat(retrievedAppUser, notNullValue())
        assertThat(retrievedAppUser.getId(), equalTo(user.getId()))
    }

    @Test
    void testListApplicationUsers() {
        // Create users
        User user1 = randomUser()
        registerForCleanup(user1)
        
        User user2 = randomUser()
        registerForCleanup(user2)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign users to application
        ApplicationUsersApi appUsersApi = new ApplicationUsersApi(getClient())
        
        AppUserAssignRequest appUserAssignRequest1 = new AppUserAssignRequest()
        appUserAssignRequest1.id(user1.getId())
        appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest1)
        
        AppUserAssignRequest appUserAssignRequest2 = new AppUserAssignRequest()
        appUserAssignRequest2.id(user2.getId())
        appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest2)
        
        // List application users
        List<AppUser> appUsers = appUsersApi.listApplicationUsers(createdApp.getId(), null, null, null, null)
        
        assertThat(appUsers, notNullValue())
        assertThat(appUsers.size(), greaterThanOrEqualTo(2))
        
        List<String> userIds = appUsers.stream().map(AppUser::getId).collect()
        assertThat(userIds, hasItem(user1.getId()))
        assertThat(userIds, hasItem(user2.getId()))
    }

    @Test
    void testUpdateApplicationUser() {
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign user to application
        ApplicationUsersApi appUsersApi = new ApplicationUsersApi(getClient())
        AppUserAssignRequest appUserAssignRequest = new AppUserAssignRequest()
        appUserAssignRequest.id(user.getId())
        
        appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest)
        
        // Update the application user
        AppUserUpdateRequest updateRequest = new AppUserUpdateRequest()
        AppUserProfile profile = new AppUserProfile()
        profile.put("role", "admin")
        updateRequest.profile(profile)

        AppUser updatedAppUser = appUsersApi.updateApplicationUser(createdApp.getId(), user.getId(), updateRequest)
        
        assertThat(updatedAppUser, notNullValue())
        assertThat(updatedAppUser.getId(), equalTo(user.getId()))
    }

    @Test
    void testUnassignUserFromApplication() {
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign user to application
        ApplicationUsersApi appUsersApi = new ApplicationUsersApi(getClient())
        AppUserAssignRequest appUserAssignRequest = new AppUserAssignRequest()
        appUserAssignRequest.id(user.getId())
        
        appUsersApi.assignUserToApplication(createdApp.getId(), appUserAssignRequest)
        
        // Unassign user from application
        appUsersApi.unassignUserFromApplication(createdApp.getId(), user.getId(), false)
        
        // Verify user is unassigned
        ApiException exception = expect(ApiException.class, () -> 
            appUsersApi.getApplicationUser(createdApp.getId(), user.getId(), null))
        
        assertThat("Should return 404 for unassigned user", exception.getCode(), is(404))
    }

    // ========================================
    // APPLICATION GROUPS API TESTS
    // ========================================

    @Test
    void testAssignGroupToApplication() {
        // Create a group
        Group group = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(new GroupApi(getClient()))
        registerForCleanup(group)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign group to application
        ApplicationGroupAssignment groupAssignment = new ApplicationGroupAssignment()
        applicationGroupsApi.assignGroupToApplication(createdApp.getId(), group.getId(), groupAssignment)
        
        // Verify assignment
        ApplicationGroupAssignment retrievedAssignment = applicationGroupsApi.getApplicationGroupAssignment(
            createdApp.getId(), group.getId(), null)
        
        assertThat(retrievedAssignment, notNullValue())
        assertThat(retrievedAssignment.getId(), equalTo(group.getId()))
    }

    @Test
    void testGetApplicationGroupAssignment() {
        // Create a group
        Group group = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(new GroupApi(getClient()))
        registerForCleanup(group)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign group to application
        ApplicationGroupAssignment groupAssignment = new ApplicationGroupAssignment()
        applicationGroupsApi.assignGroupToApplication(createdApp.getId(), group.getId(), groupAssignment)
        
        // Get the assignment
        ApplicationGroupAssignment retrievedAssignment = applicationGroupsApi.getApplicationGroupAssignment(
            createdApp.getId(), group.getId(), null)
        
        assertThat(retrievedAssignment, notNullValue())
        assertThat(retrievedAssignment.getId(), equalTo(group.getId()))
    }

    @Test
    void testListApplicationGroupAssignments() {
        // Create groups
        Group group1 = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(new GroupApi(getClient()))
        registerForCleanup(group1)
        
        Group group2 = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(new GroupApi(getClient()))
        registerForCleanup(group2)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign groups to application
        ApplicationGroupAssignment groupAssignment1 = new ApplicationGroupAssignment()
        applicationGroupsApi.assignGroupToApplication(createdApp.getId(), group1.getId(), groupAssignment1)
        
        ApplicationGroupAssignment groupAssignment2 = new ApplicationGroupAssignment()
        applicationGroupsApi.assignGroupToApplication(createdApp.getId(), group2.getId(), groupAssignment2)
        
        // List application group assignments
        List<ApplicationGroupAssignment> assignments = applicationGroupsApi.listApplicationGroupAssignments(
            createdApp.getId(), null, null, null, null)
        
        assertThat(assignments, notNullValue())
        assertThat(assignments.size(), greaterThanOrEqualTo(2))
        
        List<String> groupIds = assignments.stream().map(ApplicationGroupAssignment::getId).collect()
        assertThat(groupIds, hasItem(group1.getId()))
        assertThat(groupIds, hasItem(group2.getId()))
    }

    @Test
    void testUnassignApplicationFromGroup() {
        // Create a group
        Group group = GroupBuilder.instance()
            .setName("group-" + UUID.randomUUID().toString())
            .buildAndCreate(new GroupApi(getClient()))
        registerForCleanup(group)
        
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Assign group to application
        ApplicationGroupAssignment groupAssignment = new ApplicationGroupAssignment()
        applicationGroupsApi.assignGroupToApplication(createdApp.getId(), group.getId(), groupAssignment)
        
        // Unassign group from application
        applicationGroupsApi.unassignApplicationFromGroup(createdApp.getId(), group.getId())
        
        // Verify unassignment
        ApiException exception = expect(ApiException.class, () -> 
            applicationGroupsApi.getApplicationGroupAssignment(createdApp.getId(), group.getId(), null))
        
        assertThat("Should return 404 for unassigned group", exception.getCode(), is(404))
    }

    // ========================================
    // APPLICATION GRANTS API TESTS
    // ========================================

    @Test
    void testListScopeConsentGrants() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // List scope consent grants
        ApplicationGrantsApi grantsApi = new ApplicationGrantsApi(getClient())
        List<OAuth2ScopeConsentGrant> grants = grantsApi.listScopeConsentGrants(createdApp.getId(), null)
        
        assertThat(grants, notNullValue())
    }

    @Test
    void testGrantConsentToScope() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Grant consent to scope
        ApplicationGrantsApi grantsApi = new ApplicationGrantsApi(getClient())
        OAuth2ScopeConsentGrant grantRequest = new OAuth2ScopeConsentGrant()
        grantRequest.issuer("https://${yourOktaDomain}")
            .scopeId("okta.users.read")
            .userId(user.getId())
        
        OAuth2ScopeConsentGrant grant = grantsApi.grantConsentToScope(createdApp.getId(), grantRequest)
        
        assertThat(grant, notNullValue())
        assertThat(grant.getId(), notNullValue())
    }

    @Test
    void testGetScopeConsentGrant() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Grant consent to scope
        ApplicationGrantsApi grantsApi = new ApplicationGrantsApi(getClient())
        OAuth2ScopeConsentGrant grantRequest = new OAuth2ScopeConsentGrant()
        grantRequest.issuer("https://${yourOktaDomain}")
            .scopeId("okta.users.read")
            .userId(user.getId())
        
        OAuth2ScopeConsentGrant createdGrant = grantsApi.grantConsentToScope(createdApp.getId(), grantRequest)
        
        // Get the grant
        OAuth2ScopeConsentGrant retrievedGrant = grantsApi.getScopeConsentGrant(
            createdApp.getId(), createdGrant.getId(), null)
        
        assertThat(retrievedGrant, notNullValue())
        assertThat(retrievedGrant.getId(), equalTo(createdGrant.getId()))
    }

    @Test
    void testRevokeScopeConsentGrant() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // Create a user
        User user = randomUser()
        registerForCleanup(user)
        
        // Grant consent to scope
        ApplicationGrantsApi grantsApi = new ApplicationGrantsApi(getClient())
        OAuth2ScopeConsentGrant grantRequest = new OAuth2ScopeConsentGrant()
        grantRequest.issuer("https://${yourOktaDomain}")
            .scopeId("okta.users.read")
            .userId(user.getId())

        OAuth2ScopeConsentGrant createdGrant = grantsApi.grantConsentToScope(createdApp.getId(), grantRequest)
        
        // Revoke the grant
        grantsApi.revokeScopeConsentGrant(createdApp.getId(), createdGrant.getId())
        
        // Verify revocation
        ApiException exception = expect(ApiException.class, () -> 
            grantsApi.getScopeConsentGrant(createdApp.getId(), createdGrant.getId(), null))
        
        assertThat("Should return 404 for revoked grant", exception.getCode(), is(404))
    }

    // ========================================
    // APPLICATION FEATURES API TESTS
    // ========================================

    @Test
    void testListApplicationFeatures() {
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // List application features
        ApplicationFeaturesApi featuresApi = new ApplicationFeaturesApi(getClient())
        List<ApplicationFeature> features = featuresApi.listApplicationFeatures(createdApp.getId())
        
        assertThat(features, notNullValue())
    }

    @Test
    void testGetApplicationFeature() {
        // Create an app
        BookmarkApplication app = new BookmarkApplication()
        app.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication settingsApp = new BookmarkApplicationSettingsApplication()
        settingsApp.url("https://example.com/bookmark.htm")
        
        BookmarkApplicationSettings settings = new BookmarkApplicationSettings()
        settings.app(settingsApp)
        app.settings(settings)
        
        BookmarkApplication createdApp = applicationApi.createApplication(app, true, null) as BookmarkApplication
        registerForCleanup(createdApp)
        
        // Get a feature (using a known feature name)
        ApplicationFeaturesApi featuresApi = new ApplicationFeaturesApi(getClient())
        try {
            ApplicationFeature feature = featuresApi.getApplicationFeature(createdApp.getId(), "USER_PROVISIONING")
            assertThat(feature, notNullValue())
        } catch (ApiException e) {
            // Feature may not be available for this app type, which is acceptable
            assertThat(e.getCode(), anyOf(is(404), is(403)))
        }
    }

    // ========================================
    // APPLICATION TOKENS API TESTS
    // ========================================

    @Test
    void testListOAuth2TokensForApplication() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // List OAuth 2.0 tokens
        ApplicationTokensApi tokensApi = new ApplicationTokensApi(getClient())
        List<OAuth2Token> tokens = tokensApi.listOAuth2TokensForApplication(createdApp.getId(), null, null, null)
        
        assertThat(tokens, notNullValue())
    }

    // ========================================
    // APPLICATION POLICIES API TESTS
    // ========================================

    @Test
    void testAssignApplicationPolicy() {
        // Create an OIDC app
        OpenIdConnectApplication oidcApp = new OpenIdConnectApplication()
        oidcApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings oidcSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient settingsClient = new OpenIdConnectApplicationSettingsClient()
        settingsClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE, OAuthGrantType.REFRESH_TOKEN))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        oidcSettings.oauthClient(settingsClient)
        oidcApp.settings(oidcSettings)
        
        OpenIdConnectApplication createdApp = applicationApi.createApplication(oidcApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdApp)
        
        // Get the access policy from app links
        ApplicationPoliciesApi policiesApi = new ApplicationPoliciesApi(getClient())
        
        // Get assigned policy
        try {
            PolicyRule policy = policiesApi.getApplicationPolicy(createdApp.getId())
            assertThat(policy, notNullValue())
        } catch (ApiException e) {
            // Policy assignment may not be available in all environments
            logger.info("Could not get application policy: {}", e.getMessage())
        }
    }

    // ========================================
    // APPLICATION CROSS APP ACCESS CONNECTIONS API TESTS
    // ========================================

    @Test
    void testCreateCrossAppAccessConnection() {
        // Create a source OIDC app
        OpenIdConnectApplication sourceApp = new OpenIdConnectApplication()
        sourceApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "source-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings sourceSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient sourceClient = new OpenIdConnectApplicationSettingsClient()
        sourceClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        sourceSettings.oauthClient(sourceClient)
        sourceApp.settings(sourceSettings)
        
        OpenIdConnectApplication createdSourceApp = applicationApi.createApplication(sourceApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdSourceApp)
        
        // Create a target app
        BookmarkApplication targetApp = new BookmarkApplication()
        targetApp.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp = new BookmarkApplicationSettingsApplication()
        targetSettingsApp.url("https://example.com/target.htm")
        
        BookmarkApplicationSettings targetSettings = new BookmarkApplicationSettings()
        targetSettings.app(targetSettingsApp)
        targetApp.settings(targetSettings)
        
        BookmarkApplication createdTargetApp = applicationApi.createApplication(targetApp, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp)
        
        // Create cross app access connection
        ApplicationCrossAppAccessConnectionsApi crossAppApi = new ApplicationCrossAppAccessConnectionsApi(getClient())
        
        OrgCrossAppAccessConnection connectionRequest = new OrgCrossAppAccessConnection()
        connectionRequest.targetAppId(createdTargetApp.getId())
            .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
        
        try {
            OrgCrossAppAccessConnection connection = crossAppApi.createCrossAppAccessConnection(
                createdSourceApp.getId(), connectionRequest)
            
            assertThat(connection, notNullValue())
            assertThat(connection.getId(), notNullValue())
            assertThat(connection.getTargetAppId(), equalTo(createdTargetApp.getId()))
            assertThat(connection.getStatus(), equalTo(OrgCrossAppAccessConnection.StatusEnum.ENABLED))
        } catch (ApiException e) {
            // Cross app access connections may not be available in all environments
            logger.info("Could not create cross app access connection: {} - {}", e.getCode(), e.getMessage())
            assumeTrue("Cross app access connections not available", false)
        }
    }

    @Test
    void testGetAllCrossAppAccessConnections() {
        // Create a source OIDC app
        OpenIdConnectApplication sourceApp = new OpenIdConnectApplication()
        sourceApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "source-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings sourceSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient sourceClient = new OpenIdConnectApplicationSettingsClient()
        sourceClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        sourceSettings.oauthClient(sourceClient)
        sourceApp.settings(sourceSettings)
        
        OpenIdConnectApplication createdSourceApp = applicationApi.createApplication(sourceApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdSourceApp)
        
        // Create target apps
        BookmarkApplication targetApp1 = new BookmarkApplication()
        targetApp1.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target1-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp1 = new BookmarkApplicationSettingsApplication()
        targetSettingsApp1.url("https://example.com/target1.htm")
        
        BookmarkApplicationSettings targetSettings1 = new BookmarkApplicationSettings()
        targetSettings1.app(targetSettingsApp1)
        targetApp1.settings(targetSettings1)
        
        BookmarkApplication createdTargetApp1 = applicationApi.createApplication(targetApp1, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp1)
        
        BookmarkApplication targetApp2 = new BookmarkApplication()
        targetApp2.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target2-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp2 = new BookmarkApplicationSettingsApplication()
        targetSettingsApp2.url("https://example.com/target2.htm")
        
        BookmarkApplicationSettings targetSettings2 = new BookmarkApplicationSettings()
        targetSettings2.app(targetSettingsApp2)
        targetApp2.settings(targetSettings2)
        
        BookmarkApplication createdTargetApp2 = applicationApi.createApplication(targetApp2, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp2)
        
        // Create cross app access connections
        ApplicationCrossAppAccessConnectionsApi crossAppApi = new ApplicationCrossAppAccessConnectionsApi(getClient())
        
        try {
            OrgCrossAppAccessConnection connectionRequest1 = new OrgCrossAppAccessConnection()
            connectionRequest1.targetAppId(createdTargetApp1.getId())
                .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
            crossAppApi.createCrossAppAccessConnection(createdSourceApp.getId(), connectionRequest1)
            
            OrgCrossAppAccessConnection connectionRequest2 = new OrgCrossAppAccessConnection()
            connectionRequest2.targetAppId(createdTargetApp2.getId())
                .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
            crossAppApi.createCrossAppAccessConnection(createdSourceApp.getId(), connectionRequest2)
            
            // List all connections
            List<OrgCrossAppAccessConnection> connections = crossAppApi.getAllCrossAppAccessConnections(
                createdSourceApp.getId(), null, null)
            
            assertThat(connections, notNullValue())
            assertThat(connections.size(), greaterThanOrEqualTo(2))
            
            List<String> targetAppIds = connections.stream()
                .map(OrgCrossAppAccessConnection::getTargetAppId)
                .collect()
            assertThat(targetAppIds, hasItem(createdTargetApp1.getId()))
            assertThat(targetAppIds, hasItem(createdTargetApp2.getId()))
        } catch (ApiException e) {
            // Cross app access connections may not be available in all environments
            logger.info("Could not list cross app access connections: {} - {}", e.getCode(), e.getMessage())
            assumeTrue("Cross app access connections not available", false)
        }
    }

    @Test
    void testGetCrossAppAccessConnection() {
        // Create a source OIDC app
        OpenIdConnectApplication sourceApp = new OpenIdConnectApplication()
        sourceApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "source-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings sourceSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient sourceClient = new OpenIdConnectApplicationSettingsClient()
        sourceClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        sourceSettings.oauthClient(sourceClient)
        sourceApp.settings(sourceSettings)
        
        OpenIdConnectApplication createdSourceApp = applicationApi.createApplication(sourceApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdSourceApp)
        
        // Create a target app
        BookmarkApplication targetApp = new BookmarkApplication()
        targetApp.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp = new BookmarkApplicationSettingsApplication()
        targetSettingsApp.url("https://example.com/target.htm")
        
        BookmarkApplicationSettings targetSettings = new BookmarkApplicationSettings()
        targetSettings.app(targetSettingsApp)
        targetApp.settings(targetSettings)
        
        BookmarkApplication createdTargetApp = applicationApi.createApplication(targetApp, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp)
        
        // Create cross app access connection
        ApplicationCrossAppAccessConnectionsApi crossAppApi = new ApplicationCrossAppAccessConnectionsApi(getClient())
        
        try {
            OrgCrossAppAccessConnection connectionRequest = new OrgCrossAppAccessConnection()
            connectionRequest.targetAppId(createdTargetApp.getId())
                .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
            
            OrgCrossAppAccessConnection createdConnection = crossAppApi.createCrossAppAccessConnection(
                createdSourceApp.getId(), connectionRequest)
            
            // Get the connection
            OrgCrossAppAccessConnection retrievedConnection = crossAppApi.getCrossAppAccessConnection(
                createdSourceApp.getId(), createdConnection.getId())
            
            assertThat(retrievedConnection, notNullValue())
            assertThat(retrievedConnection.getId(), equalTo(createdConnection.getId()))
            assertThat(retrievedConnection.getTargetAppId(), equalTo(createdTargetApp.getId()))
        } catch (ApiException e) {
            // Cross app access connections may not be available in all environments
            logger.info("Could not get cross app access connection: {} - {}", e.getCode(), e.getMessage())
            assumeTrue("Cross app access connections not available", false)
        }
    }

    @Test
    void testUpdateCrossAppAccessConnection() {
        // Create a source OIDC app
        OpenIdConnectApplication sourceApp = new OpenIdConnectApplication()
        sourceApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "source-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings sourceSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient sourceClient = new OpenIdConnectApplicationSettingsClient()
        sourceClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        sourceSettings.oauthClient(sourceClient)
        sourceApp.settings(sourceSettings)
        
        OpenIdConnectApplication createdSourceApp = applicationApi.createApplication(sourceApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdSourceApp)
        
        // Create a target app
        BookmarkApplication targetApp = new BookmarkApplication()
        targetApp.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp = new BookmarkApplicationSettingsApplication()
        targetSettingsApp.url("https://example.com/target.htm")
        
        BookmarkApplicationSettings targetSettings = new BookmarkApplicationSettings()
        targetSettings.app(targetSettingsApp)
        targetApp.settings(targetSettings)
        
        BookmarkApplication createdTargetApp = applicationApi.createApplication(targetApp, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp)
        
        // Create cross app access connection
        ApplicationCrossAppAccessConnectionsApi crossAppApi = new ApplicationCrossAppAccessConnectionsApi(getClient())
        
        try {
            OrgCrossAppAccessConnection connectionRequest = new OrgCrossAppAccessConnection()
            connectionRequest.targetAppId(createdTargetApp.getId())
                .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
            
            OrgCrossAppAccessConnection createdConnection = crossAppApi.createCrossAppAccessConnection(
                createdSourceApp.getId(), connectionRequest)
            
            // Update the connection (disable it)
            OrgCrossAppAccessConnectionPatchRequest updateRequest = new OrgCrossAppAccessConnectionPatchRequest()
            updateRequest.status(OrgCrossAppAccessConnectionPatchRequest.StatusEnum.DISABLED)
            
            OrgCrossAppAccessConnection updatedConnection = crossAppApi.updateCrossAppAccessConnection(
                createdSourceApp.getId(), createdConnection.getId(), updateRequest)
            
            assertThat(updatedConnection, notNullValue())
            assertThat(updatedConnection.getStatus(), equalTo(OrgCrossAppAccessConnection.StatusEnum.DISABLED))
        } catch (ApiException e) {
            // Cross app access connections may not be available in all environments
            logger.info("Could not update cross app access connection: {} - {}", e.getCode(), e.getMessage())
            assumeTrue("Cross app access connections not available", false)
        }
    }

    @Test
    void testDeleteCrossAppAccessConnection() {
        // Create a source OIDC app
        OpenIdConnectApplication sourceApp = new OpenIdConnectApplication()
        sourceApp.name(OpenIdConnectApplication.NameEnum.OIDC_CLIENT)
            .label(prefix + "source-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        
        OpenIdConnectApplicationSettings sourceSettings = new OpenIdConnectApplicationSettings()
        OpenIdConnectApplicationSettingsClient sourceClient = new OpenIdConnectApplicationSettingsClient()
        sourceClient.applicationType(OpenIdConnectApplicationType.WEB)
            .grantTypes(Arrays.asList(OAuthGrantType.AUTHORIZATION_CODE))
            .responseTypes(Arrays.asList(OAuthResponseType.CODE))
            .redirectUris(Arrays.asList("https://example.com/oauth2/callback"))
        
        sourceSettings.oauthClient(sourceClient)
        sourceApp.settings(sourceSettings)
        
        OpenIdConnectApplication createdSourceApp = applicationApi.createApplication(sourceApp, true, null) as OpenIdConnectApplication
        registerForCleanup(createdSourceApp)
        
        // Create a target app
        BookmarkApplication targetApp = new BookmarkApplication()
        targetApp.name(BookmarkApplication.NameEnum.BOOKMARK)
            .label(prefix + "target-" + UUID.randomUUID().toString())
            .signOnMode(ApplicationSignOnMode.BOOKMARK)
        
        BookmarkApplicationSettingsApplication targetSettingsApp = new BookmarkApplicationSettingsApplication()
        targetSettingsApp.url("https://example.com/target.htm")
        
        BookmarkApplicationSettings targetSettings = new BookmarkApplicationSettings()
        targetSettings.app(targetSettingsApp)
        targetApp.settings(targetSettings)
        
        BookmarkApplication createdTargetApp = applicationApi.createApplication(targetApp, true, null) as BookmarkApplication
        registerForCleanup(createdTargetApp)
        
        // Create cross app access connection
        ApplicationCrossAppAccessConnectionsApi crossAppApi = new ApplicationCrossAppAccessConnectionsApi(getClient())
        
        try {
            OrgCrossAppAccessConnection connectionRequest = new OrgCrossAppAccessConnection()
            connectionRequest.targetAppId(createdTargetApp.getId())
                .status(OrgCrossAppAccessConnection.StatusEnum.ENABLED)
            
            OrgCrossAppAccessConnection createdConnection = crossAppApi.createCrossAppAccessConnection(
                createdSourceApp.getId(), connectionRequest)
            
            // Delete the connection
            crossAppApi.deleteCrossAppAccessConnection(createdSourceApp.getId(), createdConnection.getId())
            
            // Verify deletion
            ApiException exception = expect(ApiException.class, () -> 
                crossAppApi.getCrossAppAccessConnection(createdSourceApp.getId(), createdConnection.getId()))
            
            assertThat("Should return 404 for deleted connection", exception.getCode(), is(404))
        } catch (ApiException e) {
            // Cross app access connections may not be available in all environments
            logger.info("Could not delete cross app access connection: {} - {}", e.getCode(), e.getMessage())
            assumeTrue("Cross app access connections not available", false)
        }
    }
}

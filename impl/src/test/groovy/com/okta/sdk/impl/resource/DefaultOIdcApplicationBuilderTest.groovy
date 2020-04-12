package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.resource.application.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultOIdcApplicationBuilderTest {

    @Test
    void basicUsage() {
        def client = mock(Client)
        def application = mock(OpenIdConnectApplication)
        def applicationAccessibility = mock(ApplicationAccessibility)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def applicationVisibility = mock(ApplicationVisibility)
        def openIdConnectApplicationSettingsClient = mock(OpenIdConnectApplicationSettingsClient)
        def applicationCredentialsOAuthClient = mock(ApplicationCredentialsOAuthClient)
        def openIdConnectApplicationSettings = mock(OpenIdConnectApplicationSettings)
        def oAuthApplicationCredentials = mock(OAuthApplicationCredentials)

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application);
        when(client.instantiate(ApplicationAccessibility.class)).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(application.getAccessibility()).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibility.class)).thenReturn(applicationVisibility)
        when(application.getVisibility())thenReturn(applicationVisibility)
        when(client.instantiate(OpenIdConnectApplicationSettingsClient.class))thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(ApplicationCredentialsOAuthClient.class))thenReturn(applicationCredentialsOAuthClient)
        //when(client.instantiate(OpenIdConnectApplicationSettings.class))thenReturn(applicationCredentialsOAuthClient)
        when(application.getSettings()).thenReturn(openIdConnectApplicationSettings)
        when(application.getCredentials())thenReturn(oAuthApplicationCredentials)

        new DefaultOIdCApplicationBuilder()
            .setName("oidc_client")
            .setLabel("test_app")
            .addRedirectUris("http://www.google.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId("dummy_id")
            .setClientSecret("dummy_shgfhsgdhfgwg")
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(true)
            .setWeb(false)
            .setLoginRedirectUrl("http://www.myApp.com")
            .setErrorRedirectUrl("http://www.myApp.com/errror")
            .setSelfService(false)
            .buildAndCreate(client);

        verify(client).createApplication(eq(application))
        verify(application).setLabel("test_app")
        verify(applicationAccessibility).setSelfService(false)
        verify(applicationAccessibility).setErrorRedirectUrl("http://www.myApp.com/errror")
        verify(applicationAccessibility).setLoginRedirectUrl("http://www.myApp.com")
        verify(applicationVisibilityHide).setWeb(false)
        verify(applicationVisibilityHide).setIOS(true)
        verify(openIdConnectApplicationSettingsClient).setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
    }

    @Test
    void createWithoutResponseType(){

        def client = mock(Client)
        def application = mock(OpenIdConnectApplication)
        def applicationAccessibility = mock(ApplicationAccessibility)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def applicationVisibility = mock(ApplicationVisibility)
        def openIdConnectApplicationSettingsClient = mock(OpenIdConnectApplicationSettingsClient)
        def applicationCredentialsOAuthClient = mock(ApplicationCredentialsOAuthClient)
        def openIdConnectApplicationSettings = mock(OpenIdConnectApplicationSettings)
        def oAuthApplicationCredentials = mock(OAuthApplicationCredentials)

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application);
        when(client.instantiate(ApplicationAccessibility.class)).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(application.getAccessibility()).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibility.class)).thenReturn(applicationVisibility)
        when(application.getVisibility())thenReturn(applicationVisibility)
        when(client.instantiate(OpenIdConnectApplicationSettingsClient.class))thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(ApplicationCredentialsOAuthClient.class))thenReturn(applicationCredentialsOAuthClient)
        when(application.getSettings()).thenReturn(openIdConnectApplicationSettings)
        when(application.getCredentials())thenReturn(oAuthApplicationCredentials)

        expect IllegalArgumentException, {
            new DefaultOIdCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("http://www.google.com")
                .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
                .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("http://www.myApp.com")
                .setErrorRedirectUrl("http://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client);
        }

    }

    @Test
    void createWithoutGrantType(){

        def client = mock(Client)
        def application = mock(OpenIdConnectApplication)
        def applicationAccessibility = mock(ApplicationAccessibility)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def applicationVisibility = mock(ApplicationVisibility)
        def openIdConnectApplicationSettingsClient = mock(OpenIdConnectApplicationSettingsClient)
        def applicationCredentialsOAuthClient = mock(ApplicationCredentialsOAuthClient)
        def openIdConnectApplicationSettings = mock(OpenIdConnectApplicationSettings)
        def oAuthApplicationCredentials = mock(OAuthApplicationCredentials)

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application);
        when(client.instantiate(ApplicationAccessibility.class)).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(application.getAccessibility()).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibility.class)).thenReturn(applicationVisibility)
        when(application.getVisibility())thenReturn(applicationVisibility)
        when(client.instantiate(OpenIdConnectApplicationSettingsClient.class))thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(ApplicationCredentialsOAuthClient.class))thenReturn(applicationCredentialsOAuthClient)
        when(application.getSettings()).thenReturn(openIdConnectApplicationSettings)
        when(application.getCredentials())thenReturn(oAuthApplicationCredentials)

        expect IllegalArgumentException, {
            new DefaultOIdCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("http://www.google.com")
                .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
                .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("http://www.myApp.com")
                .setErrorRedirectUrl("http://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client);
        }

    }

    @Test
    void createWithoutApplicationType(){

        def client = mock(Client)
        def application = mock(OpenIdConnectApplication)
        def applicationAccessibility = mock(ApplicationAccessibility)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def applicationVisibility = mock(ApplicationVisibility)
        def openIdConnectApplicationSettingsClient = mock(OpenIdConnectApplicationSettingsClient)
        def applicationCredentialsOAuthClient = mock(ApplicationCredentialsOAuthClient)
        def openIdConnectApplicationSettings = mock(OpenIdConnectApplicationSettings)
        def oAuthApplicationCredentials = mock(OAuthApplicationCredentials)

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application);
        when(client.instantiate(ApplicationAccessibility.class)).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(application.getAccessibility()).thenReturn(applicationAccessibility)
        when(client.instantiate(ApplicationVisibility.class)).thenReturn(applicationVisibility)
        when(application.getVisibility())thenReturn(applicationVisibility)
        when(client.instantiate(OpenIdConnectApplicationSettingsClient.class))thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(ApplicationCredentialsOAuthClient.class))thenReturn(applicationCredentialsOAuthClient)
        when(application.getSettings()).thenReturn(openIdConnectApplicationSettings)
        when(application.getCredentials())thenReturn(oAuthApplicationCredentials)

        expect IllegalArgumentException, {
            new DefaultOIdCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("http://www.google.com")
                .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
                .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("http://www.myApp.com")
                .setErrorRedirectUrl("http://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client);
        }

    }
}

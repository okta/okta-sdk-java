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
package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.impl.resource.builder.DefaultOIDCApplicationBuilder
import com.okta.sdk.resource.*
import org.testng.annotations.Test

import static com.okta.sdk.impl.Util.expect
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DefaultOIDCApplicationBuilderTest {

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

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application)
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

        new DefaultOIDCApplicationBuilder()
            .setName("oidc_client")
            .setLabel("test_app")
            .addRedirectUris("https://www.google.com")
            .setPostLogoutRedirectUris(Collections.singletonList("https://www.example.com/logout"))
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setClientId("dummy_id")
            .setClientSecret("dummy_shgfhsgdhfgwg")
            .setAutoKeyRotation(true)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
            .setIOS(true)
            .setWeb(false)
            .setLoginRedirectUrl("https://www.myApp.com")
            .setErrorRedirectUrl("https://www.myApp.com/errror")
            .setSelfService(false)
            .buildAndCreate(client)

        verify(client).createApplication(eq(application))
        verify(application).setLabel("test_app")
        verify(applicationAccessibility).setSelfService(false)
        verify(applicationAccessibility).setErrorRedirectUrl("https://www.myApp.com/errror")
        verify(applicationAccessibility).setLoginRedirectUrl("https://www.myApp.com")
        verify(applicationVisibilityHide).setWeb(false)
        verify(applicationVisibilityHide).setIOS(true)
        verify(openIdConnectApplicationSettingsClient).setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
        verify(openIdConnectApplicationSettingsClient).setPostLogoutRedirectUris(Collections.singletonList("https://www.example.com/logout"))
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

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application)
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
            new DefaultOIDCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("https://www.google.com")
                .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
                .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("https://www.myApp.com")
                .setErrorRedirectUrl("https://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client)
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

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application)
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
            new DefaultOIDCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("https://www.google.com")
                .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
                .setApplicationType(OpenIdConnectApplicationType.NATIVE)
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("https://www.myApp.com")
                .setErrorRedirectUrl("https://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client)
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

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application)
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
            new DefaultOIDCApplicationBuilder()
                .setName("oidc_client")
                .setLabel("test_app")
                .addRedirectUris("https://www.google.com")
                .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
                .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
                .setClientId("dummy_id")
                .setClientSecret("dummy_shgfhsgdhfgwg")
                .setAutoKeyRotation(true)
                .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.NONE)
                .setIOS(true)
                .setWeb(false)
                .setLoginRedirectUrl("https://www.myApp.com")
                .setErrorRedirectUrl("https://www.myApp.com/errror")
                .setSelfService(false)
                .buildAndCreate(client)
        }

    }

    @Test
    void createOIDCApplicationWithPrivateKeyJwtTest(){

        def client = mock(Client)
        def application = mock(OpenIdConnectApplication)
        def applicationVisibilityHide = mock(ApplicationVisibilityHide)
        def openIdConnectApplicationSettingsClient = mock(OpenIdConnectApplicationSettingsClient)
        def applicationCredentialsOAuthClient = mock(ApplicationCredentialsOAuthClient)
        def openIdConnectApplicationSettings = mock(OpenIdConnectApplicationSettings)
        def clientKeys = mock(OpenIdConnectApplicationSettingsClientKeys)
        def oAuthApplicationCredentials = mock(OAuthApplicationCredentials)
        def jsonWebKey = mock(JsonWebKey)

        jsonWebKey.setKid("kid_value")
        jsonWebKey.setKty("kty_value")
        jsonWebKey.setE("e_value")
        jsonWebKey.setN("n_value")

        when(client.instantiate(OpenIdConnectApplication.class)).thenReturn(application)
        when(client.instantiate(ApplicationVisibilityHide.class)).thenReturn(applicationVisibilityHide)
        when(client.instantiate(OpenIdConnectApplicationSettingsClient.class))thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(ApplicationCredentialsOAuthClient.class))thenReturn(applicationCredentialsOAuthClient)
        when(application.getSettings()).thenReturn(openIdConnectApplicationSettings)
        when(application.getSettings().getOAuthClient()).thenReturn(openIdConnectApplicationSettingsClient)
        when(client.instantiate(OpenIdConnectApplicationSettingsClientKeys.class)).thenReturn(clientKeys)
        when(application.getCredentials())thenReturn(oAuthApplicationCredentials)

        new DefaultOIDCApplicationBuilder()
            .setName("oidc_client")
            .setLabel("test_app")
            .setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
            .setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.PRIVATE_KEY_JWT)
            .addRedirectUris("https://www.example.com")
            .setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
            .setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
            .setApplicationType(OpenIdConnectApplicationType.NATIVE)
            .setJwks(Arrays.asList(jsonWebKey))
            .buildAndCreate(client)

        verify(client).createApplication(eq(application))
        verify(application).setLabel("test_app")
        verify(application).setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
        verify(applicationCredentialsOAuthClient).setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod.PRIVATE_KEY_JWT)
        verify(openIdConnectApplicationSettingsClient).setRedirectUris(Arrays.asList("https://www.example.com"))
        verify(openIdConnectApplicationSettingsClient).setResponseTypes(Arrays.asList(OAuthResponseType.TOKEN, OAuthResponseType.CODE))
        verify(openIdConnectApplicationSettingsClient).setGrantTypes(Arrays.asList(OAuthGrantType.IMPLICIT, OAuthGrantType.AUTHORIZATION_CODE))
        verify(openIdConnectApplicationSettingsClient).setApplicationType(OpenIdConnectApplicationType.NATIVE)
        verify(clientKeys).setKeys(Arrays.asList(jsonWebKey))
    }
}

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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.application.OIDCApplicationBuilder;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.api.ApplicationApi;
import com.okta.sdk.resource.model.AutoLoginApplicationSettingsSignOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultOIDCApplicationBuilder extends DefaultApplicationBuilder<OIDCApplicationBuilder> implements OIDCApplicationBuilder {
    private com.okta.sdk.resource.model.OpenIdConnectApplicationType applicationType;
    private String clientUri;
    private com.okta.sdk.resource.model.OpenIdConnectApplicationConsentMethod consentMethod;
    private List<com.okta.sdk.resource.model.OAuthGrantType> grantTypes = new ArrayList<>();
    private String logoUri;
    private String policyUri;
    private String loginUrl;
    private String redirectUrl;
    private List<String> postLogoutRedirectUris = new ArrayList<>();
    private List<String> redirectUris = new ArrayList<>();
    private List<com.okta.sdk.resource.model.OAuthResponseType> responseTypes = new ArrayList<>();
    private String tosUri;
    private String clientId;
    private String clientSecret;
    private Boolean autoKeyRotation;
    private com.okta.sdk.resource.model.OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod;
    private List<com.okta.sdk.resource.model.SchemasJsonWebKey> schemasJsonWebKeys = new ArrayList<>();
    private Boolean isImplicitAssignment;
    private String inlineHookId;

    @Override
    public OIDCApplicationBuilder setApplicationType(com.okta.sdk.resource.model.OpenIdConnectApplicationType applicationType) {
        this.applicationType = applicationType;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setClientUri(String clientUri) {
        this.clientUri = clientUri;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setConsentMethod(com.okta.sdk.resource.model.OpenIdConnectApplicationConsentMethod consentMethod) {
        this.consentMethod = consentMethod;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setGrantTypes(List<com.okta.sdk.resource.model.OAuthGrantType> grantTypes) {
        this.grantTypes = grantTypes;
        return this;
    }

    @Override
    public OIDCApplicationBuilder addGrantTypes(com.okta.sdk.resource.model.OAuthGrantType grantType) {
        this.grantTypes.add(grantType);
        return this;
    }

    @Override
    public OIDCApplicationBuilder setLogoUri(String logoUri) {
        this.logoUri = logoUri;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setPolicyUri(String policyUri) {
        this.policyUri = policyUri;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setPostLogoutRedirectUris(List<String> postLogoutRedirectUris) {
        this.postLogoutRedirectUris = postLogoutRedirectUris;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
        return this;
    }

    @Override
    public OIDCApplicationBuilder addRedirectUris(String redirectUri) {
        this.redirectUris.add(redirectUri);
        return this;
    }

    @Override
    public OIDCApplicationBuilder setResponseTypes(List<com.okta.sdk.resource.model.OAuthResponseType> responseTypes) {
        this.responseTypes = responseTypes;
        return this;
    }

    @Override
    public OIDCApplicationBuilder addResponseTypes(com.okta.sdk.resource.model.OAuthResponseType responseType) {
        this.responseTypes.add(responseType);
        return this;
    }

    @Override
    public OIDCApplicationBuilder setTosUri(String tosUri) {
        this.tosUri = tosUri;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setAutoKeyRotation(Boolean autoKeyRotation) {
        this.autoKeyRotation = autoKeyRotation;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setTokenEndpointAuthMethod(com.okta.sdk.resource.model.OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setJwks(List<com.okta.sdk.resource.model.SchemasJsonWebKey> schemasJsonWebKeys) {
        this.schemasJsonWebKeys = schemasJsonWebKeys;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setImplicitAssignment(Boolean isImplicitAssignment) {
        this.isImplicitAssignment = isImplicitAssignment;
        return this;
    }

    @Override
    public OIDCApplicationBuilder setInlineHookId(String inlineHookId) {
        this.inlineHookId = inlineHookId;
        return this;
    }

    @Override
    public com.okta.sdk.resource.model.OpenIdConnectApplication buildAndCreate(ApplicationApi client) throws ApiException {
        return (com.okta.sdk.resource.model.OpenIdConnectApplication) new ApplicationApi(client.getApiClient())
                .createApplication(build(), false, null);
    }

    private com.okta.sdk.resource.model.OpenIdConnectApplication build() {
        com.okta.sdk.resource.model.OpenIdConnectApplication application = new com.okta.sdk.resource.model.OpenIdConnectApplication();

        if (Strings.hasText(label)) application.setLabel(label);

        if (Strings.hasText(name)) application.setName(name);

        application.setSignOnMode(com.okta.sdk.resource.model.ApplicationSignOnMode.OPENID_CONNECT);

        // Accessibility
        com.okta.sdk.resource.model.ApplicationAccessibility applicationAccessibility = new com.okta.sdk.resource.model.ApplicationAccessibility();

        if (Strings.hasText(errorRedirectUrl))
            applicationAccessibility.setErrorRedirectUrl(errorRedirectUrl);

        if (Objects.nonNull(selfService))
            applicationAccessibility.setSelfService(selfService);

        application.setAccessibility(applicationAccessibility);

        // Visibility
        com.okta.sdk.resource.model.ApplicationVisibility applicationVisibility = new com.okta.sdk.resource.model.ApplicationVisibility();

        com.okta.sdk.resource.model.ApplicationVisibilityHide applicationVisibilityHide = new com.okta.sdk.resource.model.ApplicationVisibilityHide();

        if (Objects.nonNull(iOS))
            applicationVisibilityHide.setiOS(iOS);

        if (Objects.nonNull(web))
           applicationVisibilityHide.setWeb(web);

        applicationVisibility.setHide(applicationVisibilityHide);

        application.setVisibility(applicationVisibility);

        // Settings
        com.okta.sdk.resource.model.OpenIdConnectApplicationSettings openIdConnectApplicationSettings = new com.okta.sdk.resource.model.OpenIdConnectApplicationSettings();

        com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClient openIdConnectApplicationSettingsClient = new com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClient();

        if (Strings.hasText(clientUri))
            openIdConnectApplicationSettingsClient.setClientUri(clientUri);

        if (Strings.hasText(logoUri))
            openIdConnectApplicationSettingsClient.setLogoUri(logoUri);

        if (Strings.hasText(policyUri))
            openIdConnectApplicationSettingsClient.setPolicyUri(policyUri);

        if (Strings.hasText(tosUri))
            openIdConnectApplicationSettingsClient.setTosUri(tosUri);

        if (Objects.nonNull(postLogoutRedirectUris) && !postLogoutRedirectUris.isEmpty())
            openIdConnectApplicationSettingsClient.setPostLogoutRedirectUris(postLogoutRedirectUris);

        if (Objects.nonNull(redirectUris))
            openIdConnectApplicationSettingsClient.setRedirectUris(redirectUris);

        if (Objects.nonNull(responseTypes) && !responseTypes.isEmpty())
            openIdConnectApplicationSettingsClient.setResponseTypes(responseTypes);
        else
            throw new IllegalArgumentException("Response Type cannot be null, value should be of type OAuthResponseType");

        if (Objects.nonNull(grantTypes) && !grantTypes.isEmpty())
            openIdConnectApplicationSettingsClient.setGrantTypes(grantTypes);
        else
            throw new IllegalArgumentException("Grant Type cannot be null, value should be of type OAuthGrantType");

        if (Objects.nonNull(consentMethod))
            openIdConnectApplicationSettingsClient.setConsentMethod(consentMethod);

        if (Objects.nonNull(applicationType))
            openIdConnectApplicationSettingsClient.setApplicationType(applicationType);
        else
            throw new IllegalArgumentException("Application Type cannot be null, value should be of type OpenIdConnectApplicationType");

        if (Objects.nonNull(isImplicitAssignment))
            openIdConnectApplicationSettings.setImplicitAssignment(isImplicitAssignment);

        if (Objects.nonNull(inlineHookId))
            openIdConnectApplicationSettings.setInlineHookId(inlineHookId);

        // Credentials
        com.okta.sdk.resource.model.OAuthApplicationCredentials oAuthApplicationCredentials = new com.okta.sdk.resource.model.OAuthApplicationCredentials();
        com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient applicationCredentialsOAuthClient = new com.okta.sdk.resource.model.ApplicationCredentialsOAuthClient();

        if (Strings.hasText(clientId))
            applicationCredentialsOAuthClient.setClientId(clientId);

        if (Strings.hasText(clientSecret))
            applicationCredentialsOAuthClient.setClientSecret(clientSecret);

        if (Objects.nonNull(autoKeyRotation))
            applicationCredentialsOAuthClient.setAutoKeyRotation(autoKeyRotation);

        if (Objects.nonNull(tokenEndpointAuthMethod))
            applicationCredentialsOAuthClient.setTokenEndpointAuthMethod(tokenEndpointAuthMethod);
        else
            throw new IllegalArgumentException("Token Endpoint Auth Method cannot be null, value should be of type OAuthEndpointAuthenticationMethod");

        oAuthApplicationCredentials.setOauthClient(applicationCredentialsOAuthClient);

        application.setCredentials(oAuthApplicationCredentials);

        openIdConnectApplicationSettings.setOauthClient(openIdConnectApplicationSettingsClient);

        AutoLoginApplicationSettingsSignOn autoLoginApplicationSettingsSignOn = new AutoLoginApplicationSettingsSignOn();
        if (Strings.hasText(loginUrl))
            autoLoginApplicationSettingsSignOn.setLoginUrl(loginUrl);

        if (Strings.hasText(redirectUrl))
            autoLoginApplicationSettingsSignOn.setRedirectUrl(redirectUrl);

        if (Strings.hasText(autoLoginApplicationSettingsSignOn.getLoginUrl()))
            openIdConnectApplicationSettings.setSignOn(autoLoginApplicationSettingsSignOn);

        if (!schemasJsonWebKeys.isEmpty()) {
            com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClientKeys openIdConnectApplicationSettingsClientKeys = new com.okta.sdk.resource.model.OpenIdConnectApplicationSettingsClientKeys();
            openIdConnectApplicationSettingsClientKeys.setKeys(schemasJsonWebKeys);
            openIdConnectApplicationSettings.getOauthClient().setJwks(openIdConnectApplicationSettingsClientKeys);
        }

        application.setSettings(openIdConnectApplicationSettings);
        return application;
    }
}

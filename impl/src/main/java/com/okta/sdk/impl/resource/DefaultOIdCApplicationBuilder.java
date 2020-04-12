package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Collections;
import com.okta.commons.lang.Strings;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.application.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultOIdCApplicationBuilder extends DefaultApplicationBuilder<OIdCApplicationBuilder> implements OIdCApplicationBuilder {

    private OpenIdConnectApplicationType applicationType;
    private String clientUri;
    private OpenIdConnectApplicationConsentMethod consentMethod;
    private List<OAuthGrantType> grantTypes = new ArrayList<>();
    private String logoUri;
    private String policyUri;
    private List<String> redirectUris = new ArrayList<>();
    private List<OAuthResponseType> responseTypes = new ArrayList<>();
    private String tosUri;
    private String clientId;
    private String clientSecret;
    private Boolean autoKeyRotation;
    private OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod;


    @Override
    public OIdCApplicationBuilder setApplicationType(OpenIdConnectApplicationType applicationType) {
        this.applicationType = applicationType;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setClientUri(String clientUri) {
        this.clientUri = clientUri;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setConsentMethod(OpenIdConnectApplicationConsentMethod consentMethod) {
        this.consentMethod = consentMethod;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setGrantTypes(List<OAuthGrantType> grantTypes) {
        this.grantTypes = grantTypes;
        return this;
    }

    @Override
    public OIdCApplicationBuilder addGrantTypes(OAuthGrantType grantType) {
        this.grantTypes.add(grantType);
        return this;
    }

    @Override
    public OIdCApplicationBuilder setLogoUri(String logoUri) {
        this.logoUri = logoUri;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setPolicyUri(String policyUri) {
        this.policyUri = policyUri;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
        return this;
    }

    @Override
    public OIdCApplicationBuilder addRedirectUris(String redirectUri) {
        this.redirectUris.add(redirectUri);
        return this;
    }

    @Override
    public OIdCApplicationBuilder setResponseTypes(List<OAuthResponseType> responseTypes) {
        this.responseTypes = responseTypes;
        return this;
    }

    @Override
    public OIdCApplicationBuilder addResponseTypes(OAuthResponseType responseType) {
        this.responseTypes.add(responseType);
        return this;
    }

    @Override
    public OIdCApplicationBuilder setTosUri(String tosUri) {
        this.tosUri = tosUri;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setAutoKeyRotation(Boolean autoKeyRotation) {
        this.autoKeyRotation = autoKeyRotation;
        return this;
    }

    @Override
    public OIdCApplicationBuilder setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
        return this;
    }

    @Override
    public OpenIdConnectApplication buildAndCreate(Client client){ return (OpenIdConnectApplication) client.createApplication(build(client)); }

    private Application build(Client client){
        OpenIdConnectApplication application = client.instantiate(OpenIdConnectApplication.class);

        if (Strings.hasText(label)) application.setLabel(label);

        if (Objects.nonNull(signOnMode)) application.setSignOnMode(signOnMode);

        // Accessibility
        application.setAccessibility(client.instantiate(ApplicationAccessibility.class));
        ApplicationAccessibility applicationAccessibility = application.getAccessibility();

        if (Strings.hasText(loginRedirectUrl))
            applicationAccessibility.setLoginRedirectUrl(loginRedirectUrl);

        if (Strings.hasText(errorRedirectUrl))
            applicationAccessibility.setErrorRedirectUrl(errorRedirectUrl);

        if (Objects.nonNull(selfService))
            applicationAccessibility.setSelfService(selfService);

        // Visibility
        application.setVisibility(client.instantiate(ApplicationVisibility.class));
        ApplicationVisibility applicationVisibility = application.getVisibility();
        ApplicationVisibilityHide applicationVisibilityHide = client.instantiate(ApplicationVisibilityHide.class);

        if(Objects.nonNull(iOS))
            applicationVisibility.setHide(applicationVisibilityHide
                .setIOS(iOS));

        if(Objects.nonNull(web))
            applicationVisibility.setHide(applicationVisibilityHide
                .setWeb(web));

        // Settings
        application.setSettings(client.instantiate(OpenIdConnectApplicationSettings.class));
        OpenIdConnectApplicationSettings openIdConnectApplicationSettings = application.getSettings();
        OpenIdConnectApplicationSettingsClient openIdConnectApplicationSettingsClient= client.instantiate(OpenIdConnectApplicationSettingsClient.class);

        if (Strings.hasText(clientUri))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setClientUri(clientUri));

        if (Strings.hasText(logoUri))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setLogoUri(logoUri));

        if (Strings.hasText(policyUri))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setPolicyUri(policyUri));

        if (Strings.hasText(tosUri))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setTosUri(tosUri));

        if (!Collections.isEmpty(redirectUris))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setRedirectUris(redirectUris));

        if (!Collections.isEmpty(responseTypes))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setResponseTypes(responseTypes));
        else
            throw new IllegalArgumentException("Response Type cannot be null, value should be of type OAuthResponseType");

        if (!Collections.isEmpty(grantTypes))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setGrantTypes(grantTypes));
        else
            throw new IllegalArgumentException("Grant Type cannot be null, value should be of type OAuthGrantType");

        if (Objects.nonNull(consentMethod))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setConsentMethod(consentMethod));

        if (Objects.nonNull(applicationType))
            openIdConnectApplicationSettings.setOAuthClient(openIdConnectApplicationSettingsClient.setApplicationType(applicationType));
        else
            throw new IllegalArgumentException("Application Type cannot be null, value should be of type OpenIdConnectApplicationType");

        // Credentials
        application.setCredentials(client.instantiate(OAuthApplicationCredentials.class));
        OAuthApplicationCredentials oAuthApplicationCredentials = application.getCredentials();
        ApplicationCredentialsOAuthClient applicationCredentialsOAuthClient = client.instantiate(ApplicationCredentialsOAuthClient.class);

        if (Strings.hasText(clientId))
            oAuthApplicationCredentials.setOAuthClient(applicationCredentialsOAuthClient.setClientId(clientId));

        if (Strings.hasText(clientSecret))
            oAuthApplicationCredentials.setOAuthClient(applicationCredentialsOAuthClient.setClientSecret(clientSecret));

        if (Objects.nonNull(autoKeyRotation))
            oAuthApplicationCredentials.setOAuthClient(applicationCredentialsOAuthClient.setAutoKeyRotation(autoKeyRotation));

        if (Objects.nonNull(tokenEndpointAuthMethod))
            oAuthApplicationCredentials.setOAuthClient(applicationCredentialsOAuthClient.setTokenEndpointAuthMethod(tokenEndpointAuthMethod));
        else
            throw new IllegalArgumentException("Token Endpoint Auth Method cannot be null, value should be of type OAuthEndpointAuthenticationMethod");

        return application;
    }
}

package com.okta.sdk.resource.application;

import com.okta.commons.lang.Classes;

import java.util.List;

;

public interface OIdCApplicationBuilder extends ApplicationBuilder<OIdCApplicationBuilder> {

    static OIdCApplicationBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultOIdCApplicationBuilder");
    }

    OIdCApplicationBuilder setApplicationType(OpenIdConnectApplicationType applicationType);

    OIdCApplicationBuilder setClientUri(String clientUri);

    OIdCApplicationBuilder setConsentMethod(OpenIdConnectApplicationConsentMethod consentMethod);

    OIdCApplicationBuilder setGrantTypes(List<OAuthGrantType> grantTypes);

    OIdCApplicationBuilder addGrantTypes(OAuthGrantType grantType);

    OIdCApplicationBuilder setLogoUri(String logoUri);

    OIdCApplicationBuilder setPolicyUri(String policyUri);

    OIdCApplicationBuilder setRedirectUris(List<String> redirectUris);

    OIdCApplicationBuilder addRedirectUris(String redirectUri);

    OIdCApplicationBuilder setResponseTypes(List<OAuthResponseType> responseTypes);

    OIdCApplicationBuilder addResponseTypes(OAuthResponseType responseType);

    OIdCApplicationBuilder setTosUri(String tosUri);

    OIdCApplicationBuilder setClientId(String clientId);

    OIdCApplicationBuilder setClientSecret(String clientSecret);

    OIdCApplicationBuilder setAutoKeyRotation(Boolean autoKeyRotation);

    OIdCApplicationBuilder setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod);


}

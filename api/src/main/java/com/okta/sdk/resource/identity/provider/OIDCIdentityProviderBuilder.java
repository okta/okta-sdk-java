package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;

public interface OIDCIdentityProviderBuilder extends IdentityProviderBuilder<OIDCIdentityProviderBuilder> {

    static OIDCIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultOIDCIdentityProviderBuilder");
    }

    OIDCIdentityProviderBuilder setIssuerMode(IdentityProvider.IssuerModeEnum issuerMode);

    OIDCIdentityProviderBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm);

    OIDCIdentityProviderBuilder setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum requestSignatureScope);

    OIDCIdentityProviderBuilder setResponseSignatureAlgorithm(String responseSignatureAlgorithm);

    OIDCIdentityProviderBuilder setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum responseSignatureScope);

    OIDCIdentityProviderBuilder setAcsEndpointBinding(ProtocolEndpoint.BindingEnum acsEndpointBinding);

    OIDCIdentityProviderBuilder setAcsEndpointType(ProtocolEndpoint.TypeEnum acsEndpointType);

    OIDCIdentityProviderBuilder setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum authorizationEndpointBinding);

    OIDCIdentityProviderBuilder setAuthorizationEndpointUrl(String authorizationEndpointUrl);

    OIDCIdentityProviderBuilder setTokenEndpointBinding(ProtocolEndpoint.BindingEnum tokenEndpointBinding);

    OIDCIdentityProviderBuilder setTokenEndpointUrl(String tokenEndpointUrl);

    OIDCIdentityProviderBuilder setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum userInfoEndpointBinding);

    OIDCIdentityProviderBuilder setUserInfoEndpointUrl(String userInfoEndpointUrl);

    OIDCIdentityProviderBuilder setJwksEndpointBinding(ProtocolEndpoint.BindingEnum jwksEndpointBinding);

    OIDCIdentityProviderBuilder setJwksEndpointUrl(String jwksEndpointUrl);

    OIDCIdentityProviderBuilder setBaseUrl(String baseUrl);

    OIDCIdentityProviderBuilder setSubjectTemplate(String subjectTemplate);
}

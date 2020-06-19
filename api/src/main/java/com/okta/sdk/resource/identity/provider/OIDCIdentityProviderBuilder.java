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
package com.okta.sdk.resource.identity.provider;

import com.okta.commons.lang.Classes;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.policy.PolicySubjectMatchType;

import java.util.List;

/**
 * Builder to add Generic OIDC Identity Provider.
 * @since 2.0.0
 */
public interface OIDCIdentityProviderBuilder {

    OIDCIdentityProviderBuilder setName(String name);

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

    OIDCIdentityProviderBuilder setScopes(List<String> scopes);

    OIDCIdentityProviderBuilder setProtocolType(Protocol.TypeEnum protocoltype);

    OIDCIdentityProviderBuilder setClientId(String clientId);

    OIDCIdentityProviderBuilder setClientSecret(String clientSecret);

    OIDCIdentityProviderBuilder setBaseUrl(String baseUrl);

    OIDCIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

    OIDCIdentityProviderBuilder setSubjectTemplate(String subjectTemplate);

    OIDCIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);
    
    IdentityProvider buildAndCreate(Client client);

    static OIDCIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultOIDCIdentityProviderBuilder");
    }
}

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

public interface IdentityProviderBuilder {

    IdentityProviderBuilder setName(String name);

    IdentityProviderBuilder setType(IdentityProvider.TypeEnum type);

    IdentityProviderBuilder setIssuerMode(IdentityProvider.IssuerModeEnum issuerMode);

    IdentityProviderBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm);

    IdentityProviderBuilder setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum requestSignatureScope);

    IdentityProviderBuilder setResponseSignatureAlgorithm(String responseSignatureAlgorithm);

    IdentityProviderBuilder setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum responseSignatureScope);

    IdentityProviderBuilder setAcsEndpointBinding(ProtocolEndpoint.BindingEnum acsEndpointBinding);

    IdentityProviderBuilder setAcsEndpointType(ProtocolEndpoint.TypeEnum acsEndpointType);

    IdentityProviderBuilder setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum authorizationEndpointBinding);

    IdentityProviderBuilder setAuthorizationEndpointUrl(String authorizationEndpointUrl);

    IdentityProviderBuilder setTokenEndpointBinding(ProtocolEndpoint.BindingEnum tokenEndpointBinding);

    IdentityProviderBuilder setTokenEndpointUrl(String tokenEndpointUrl);

    IdentityProviderBuilder setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum userInfoEndpointBinding);

    IdentityProviderBuilder setUserInfoEndpointUrl(String userInfoEndpointUrl);

    IdentityProviderBuilder setJwksEndpointBinding(ProtocolEndpoint.BindingEnum jwksEndpointBinding);

    IdentityProviderBuilder setJwksEndpointUrl(String jwksEndpointUrl);

    IdentityProviderBuilder setScopes(List<String> scopes);

    IdentityProviderBuilder setProtocolType(Protocol.TypeEnum protocoltype);

    IdentityProviderBuilder setClientId(String clientId);

    IdentityProviderBuilder setClientSecret(String clientSecret);

    IdentityProviderBuilder setBaseUrl(String baseUrl);

    IdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

    IdentityProviderBuilder setSubjectTemplate(String subjectTemplate);

    IdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);
    
    IdentityProvider buildAndCreate(Client client);

    static IdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultIdentityProviderBuilder");
    }
}

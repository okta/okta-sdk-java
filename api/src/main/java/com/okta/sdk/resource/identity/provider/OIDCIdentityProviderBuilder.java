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

import com.okta.sdk.resource.policy.PolicySubjectMatchType;

public interface OIDCIdentityProviderBuilder extends IdentityProviderBuilder<OIDCIdentityProviderBuilder> {

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

    OIDCIdentityProviderBuilder setIssuerUrl(String issuerUrl);

    OIDCIdentityProviderBuilder setUserName(String userName);

    OIDCIdentityProviderBuilder setMatchType(PolicySubjectMatchType matchType);
}

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
package com.okta.sdk.resource.builder;

import com.okta.sdk.resource.IssuerMode;
import com.okta.sdk.resource.PolicySubjectMatchType;
import com.okta.sdk.resource.ProtocolAlgorithmTypeSignatureScope;
import com.okta.sdk.resource.ProtocolEndpointBinding;
import com.okta.sdk.resource.ProtocolEndpointType;

public interface OIDCIdentityProviderBuilder extends IdentityProviderBuilder<OIDCIdentityProviderBuilder> {

    OIDCIdentityProviderBuilder setIssuerMode(IssuerMode issuerMode);

    OIDCIdentityProviderBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm);

    OIDCIdentityProviderBuilder setRequestSignatureScope(ProtocolAlgorithmTypeSignatureScope requestSignatureScope);

    OIDCIdentityProviderBuilder setResponseSignatureAlgorithm(String responseSignatureAlgorithm);

    OIDCIdentityProviderBuilder setResponseSignatureScope(ProtocolAlgorithmTypeSignatureScope responseSignatureScope);

    OIDCIdentityProviderBuilder setAcsEndpointBinding(ProtocolEndpointBinding acsEndpointBinding);

    OIDCIdentityProviderBuilder setAcsEndpointType(ProtocolEndpointType acsEndpointType);

    OIDCIdentityProviderBuilder setAuthorizationEndpointBinding(ProtocolEndpointBinding authorizationEndpointBinding);

    OIDCIdentityProviderBuilder setAuthorizationEndpointUrl(String authorizationEndpointUrl);

    OIDCIdentityProviderBuilder setTokenEndpointBinding(ProtocolEndpointBinding tokenEndpointBinding);

    OIDCIdentityProviderBuilder setTokenEndpointUrl(String tokenEndpointUrl);

    OIDCIdentityProviderBuilder setUserInfoEndpointBinding(ProtocolEndpointBinding userInfoEndpointBinding);

    OIDCIdentityProviderBuilder setUserInfoEndpointUrl(String userInfoEndpointUrl);

    OIDCIdentityProviderBuilder setJwksEndpointBinding(ProtocolEndpointBinding jwksEndpointBinding);

    OIDCIdentityProviderBuilder setJwksEndpointUrl(String jwksEndpointUrl);

    OIDCIdentityProviderBuilder setIssuerUrl(String issuerUrl);

    OIDCIdentityProviderBuilder setUserName(String userName);

    OIDCIdentityProviderBuilder setMatchType(PolicySubjectMatchType matchType);
}

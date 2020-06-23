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

    interface OIDCIdentityProviderBuilder {
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

        OIDCIdentityProviderBuilder setClientId(String clientId);

        OIDCIdentityProviderBuilder setClientSecret(String clientSecret);

        OIDCIdentityProviderBuilder setBaseUrl(String baseUrl);

        OIDCIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

        OIDCIdentityProviderBuilder setSubjectTemplate(String subjectTemplate);

        OIDCIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

        IdentityProvider buildAndCreate(Client client);
    }

    interface GoogleIdentityProviderBuilder {
        GoogleIdentityProviderBuilder setName(String name);

        GoogleIdentityProviderBuilder setScopes(List<String> scopes);

        GoogleIdentityProviderBuilder setClientId(String clientId);

        GoogleIdentityProviderBuilder setClientSecret(String clientSecret);

        GoogleIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

        GoogleIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

        GoogleIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);

        GoogleIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

        IdentityProvider buildAndCreate(Client client);
    }

    interface FacebookIdentityProviderBuilder {
        FacebookIdentityProviderBuilder setName(String name);

        FacebookIdentityProviderBuilder setScopes(List<String> scopes);

        FacebookIdentityProviderBuilder setClientId(String clientId);

        FacebookIdentityProviderBuilder setClientSecret(String clientSecret);

        FacebookIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

        FacebookIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

        FacebookIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);

        FacebookIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

        IdentityProvider buildAndCreate(Client client);
    }

    interface MicrosoftIdentityProviderBuilder {
        MicrosoftIdentityProviderBuilder setName(String name);

        MicrosoftIdentityProviderBuilder setScopes(List<String> scopes);

        MicrosoftIdentityProviderBuilder setClientId(String clientId);

        MicrosoftIdentityProviderBuilder setClientSecret(String clientSecret);

        MicrosoftIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

        MicrosoftIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

        MicrosoftIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);

        MicrosoftIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

        IdentityProvider buildAndCreate(Client client);
    }

    interface LinkedInIdentityProviderBuilder {
        LinkedInIdentityProviderBuilder setName(String name);

        LinkedInIdentityProviderBuilder setScopes(List<String> scopes);

        LinkedInIdentityProviderBuilder setClientId(String clientId);

        LinkedInIdentityProviderBuilder setClientSecret(String clientSecret);

        LinkedInIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

        LinkedInIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

        LinkedInIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);

        LinkedInIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

        IdentityProvider buildAndCreate(Client client);
    }

    static OIDCIdentityProviderBuilder oidc() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultOIDCIdentityProviderBuilder");
    }

    static GoogleIdentityProviderBuilder google() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultGoogleIdentityProviderBuilder");
    }

    static FacebookIdentityProviderBuilder facebook() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultFacebookIdentityProviderBuilder");
    }

    static MicrosoftIdentityProviderBuilder microsoft() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultMicrosoftIdentityProviderBuilder");
    }

    static LinkedInIdentityProviderBuilder linkedin() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultLinkedInIdentityProviderBuilder");
    }
}

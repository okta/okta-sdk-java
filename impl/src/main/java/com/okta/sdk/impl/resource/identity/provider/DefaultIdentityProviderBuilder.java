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
package com.okta.sdk.impl.resource.identity.provider;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.identity.provider.IdentityProvider;
import com.okta.sdk.resource.identity.provider.IdentityProviderBuilder;
import com.okta.sdk.resource.identity.provider.IdentityProviderCredentials;
import com.okta.sdk.resource.identity.provider.IdentityProviderCredentialsClient;
import com.okta.sdk.resource.identity.provider.Protocol;
import com.okta.sdk.resource.identity.provider.ProtocolAlgorithmType;
import com.okta.sdk.resource.identity.provider.ProtocolAlgorithmTypeSignature;
import com.okta.sdk.resource.identity.provider.ProtocolAlgorithms;
import com.okta.sdk.resource.identity.provider.ProtocolEndpoint;
import com.okta.sdk.resource.identity.provider.ProtocolEndpoints;
import com.okta.sdk.resource.identity.provider.Provisioning;
import com.okta.sdk.resource.identity.provider.ProvisioningConditions;
import com.okta.sdk.resource.identity.provider.ProvisioningDeprovisionedCondition;
import com.okta.sdk.resource.identity.provider.ProvisioningGroups;
import com.okta.sdk.resource.identity.provider.ProvisioningSuspendedCondition;
import com.okta.sdk.resource.policy.IdentityProviderPolicy;
import com.okta.sdk.resource.policy.PolicyAccountLink;
import com.okta.sdk.resource.policy.PolicySubject;
import com.okta.sdk.resource.policy.PolicySubjectMatchType;
import com.okta.sdk.resource.policy.PolicyUserNameTemplate;

import java.util.List;

/**
 * Builder for {@link IdentityProvider}.
 * @since 2.0.0
 */
public class DefaultIdentityProviderBuilder implements IdentityProviderBuilder {

    private String name;
    private IdentityProvider.TypeEnum type;
    private IdentityProvider.IssuerModeEnum issuerMode;
    private String requestSignatureAlgorithm;
    private ProtocolAlgorithmTypeSignature.ScopeEnum requestSignatureScope;
    private String responseSignatureAlgorithm;
    private ProtocolAlgorithmTypeSignature.ScopeEnum responseSignatureScope;
    private ProtocolEndpoint.BindingEnum acsEndpointBinding;
    private ProtocolEndpoint.TypeEnum acsEndpointType;
    private ProtocolEndpoint.BindingEnum authorizationEndpointBinding;
    private String authorizationEndpointUrl;
    private ProtocolEndpoint.BindingEnum tokenEndpointBinding;
    private String tokenEndpointUrl;
    private ProtocolEndpoint.BindingEnum userInfoEndpointBinding;
    private String userInfoEndpointUrl;
    private ProtocolEndpoint.BindingEnum jwksEndpointBinding;
    private String jwksEndpointUrl;
    private List<String> scopes;
    private Protocol.TypeEnum protocolType;
    private String clientId;
    private String clientSecret;
    private String baseUrl;
    private Integer maxClockSkew;
    private String subjectTemplate;
    private PolicySubjectMatchType policySubjectMatchType;

    @Override
    public IdentityProviderBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public IdentityProviderBuilder setType(IdentityProvider.TypeEnum type) {
        this.type = type;
        return this;
    }

    @Override
    public IdentityProviderBuilder setIssuerMode(IdentityProvider.IssuerModeEnum issuerMode) {
        this.issuerMode = issuerMode;
        return this;
    }

    @Override
    public IdentityProviderBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm) {
        this.requestSignatureAlgorithm = requestSignatureAlgorithm;
        return this;
    }

    @Override
    public IdentityProviderBuilder setRequestSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum requestSignatureScope) {
        this.requestSignatureScope = requestSignatureScope;
        return this;
    }

    @Override
    public IdentityProviderBuilder setResponseSignatureAlgorithm(String responseSignatureAlgorithm) {
        this.responseSignatureAlgorithm = responseSignatureAlgorithm;
        return this;
    }

    @Override
    public IdentityProviderBuilder setResponseSignatureScope(ProtocolAlgorithmTypeSignature.ScopeEnum responseSignatureScope) {
        this.responseSignatureScope = responseSignatureScope;
        return this;
    }

    @Override
    public IdentityProviderBuilder setAcsEndpointBinding(ProtocolEndpoint.BindingEnum acsEndpointBinding) {
        this.acsEndpointBinding = acsEndpointBinding;
        return this;
    }

    @Override
    public IdentityProviderBuilder setAcsEndpointType(ProtocolEndpoint.TypeEnum acsEndpointType) {
        this.acsEndpointType = acsEndpointType;
        return this;
    }

    @Override
    public IdentityProviderBuilder setAuthorizationEndpointBinding(ProtocolEndpoint.BindingEnum authorizationEndpointBinding) {
        this.authorizationEndpointBinding = authorizationEndpointBinding;
        return this;
    }

    @Override
    public IdentityProviderBuilder setAuthorizationEndpointUrl(String authorizationEndpointUrl) {
        this.authorizationEndpointUrl = authorizationEndpointUrl;
        return this;
    }

    @Override
    public IdentityProviderBuilder setTokenEndpointBinding(ProtocolEndpoint.BindingEnum tokenEndpointBinding) {
        this.tokenEndpointBinding = tokenEndpointBinding;
        return this;
    }

    @Override
    public IdentityProviderBuilder setTokenEndpointUrl(String tokenEndpointUrl) {
        this.tokenEndpointUrl = tokenEndpointUrl;
        return this;
    }

    @Override
    public IdentityProviderBuilder setUserInfoEndpointBinding(ProtocolEndpoint.BindingEnum userInfoEndpointBinding) {
        this.userInfoEndpointBinding = userInfoEndpointBinding;
        return this;
    }

    @Override
    public IdentityProviderBuilder setUserInfoEndpointUrl(String userInfoEndpointUrl) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        return this;
    }

    @Override
    public IdentityProviderBuilder setJwksEndpointBinding(ProtocolEndpoint.BindingEnum jwksEndpointBinding) {
        this.jwksEndpointBinding = jwksEndpointBinding;
        return this;
    }

    @Override
    public IdentityProviderBuilder setJwksEndpointUrl(String jwksEndpointUrl) {
        this.jwksEndpointUrl = jwksEndpointUrl;
        return this;
    }

    @Override
    public IdentityProviderBuilder setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    @Override
    public IdentityProviderBuilder setProtocolType(Protocol.TypeEnum protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    @Override
    public IdentityProviderBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public IdentityProviderBuilder setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public IdentityProviderBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public IdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
        return this;
    }

    @Override
    public IdentityProviderBuilder setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
        return this;
    }

    @Override
    public IdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType) {
        this.policySubjectMatchType = policySubjectMatchType;
        return this;
    }

    @Override
    public IdentityProvider buildAndCreate(Client client) {
        IdentityProvider createdIdp = client.createIdentityProvider(client.instantiate(IdentityProvider.class)
            .setType(type)
            .setName(name)
            .setIssuerMode(issuerMode)
            .setProtocol(client.instantiate(Protocol.class)
                .setAlgorithms(client.instantiate(ProtocolAlgorithms.class)
                    .setRequest(client.instantiate(ProtocolAlgorithmType.class)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature.class)
                            .setAlgorithm(requestSignatureAlgorithm)
                            .setScope(ProtocolAlgorithmTypeSignature.ScopeEnum.REQUEST)))
                    .setResponse(client.instantiate(ProtocolAlgorithmType.class)
                        .setSignature(client.instantiate(ProtocolAlgorithmTypeSignature.class)
                            .setAlgorithm(responseSignatureAlgorithm)
                            .setScope(responseSignatureScope))))
                .setEndpoints(client.instantiate(ProtocolEndpoints.class)
                    .setAcs(client.instantiate(ProtocolEndpoint.class)
                        .setBinding(acsEndpointBinding)
                        .setType(acsEndpointType))
                    .setAuthorization(client.instantiate(ProtocolEndpoint.class)
                        .setBinding(authorizationEndpointBinding)
                        .setUrl(authorizationEndpointUrl))
                    .setToken(client.instantiate(ProtocolEndpoint.class)
                        .setBinding(tokenEndpointBinding)
                        .setUrl(tokenEndpointUrl))
                    .setUserInfo(client.instantiate(ProtocolEndpoint.class)
                        .setBinding(userInfoEndpointBinding)
                        .setUrl(userInfoEndpointUrl))
                    .setJwks(client.instantiate(ProtocolEndpoint.class)
                        .setBinding(jwksEndpointBinding)
                        .setUrl(jwksEndpointUrl)))
                .setScopes(scopes)
                .setType(protocolType)
            .setCredentials(client.instantiate(IdentityProviderCredentials.class)
                .setClient(client.instantiate(IdentityProviderCredentialsClient.class)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)))
            .setIssuer(client.instantiate(ProtocolEndpoint.class)
                .setUrl(baseUrl)))
            .setPolicy(client.instantiate(IdentityProviderPolicy.class)
            .setAccountLink(client.instantiate(PolicyAccountLink.class)
                .setAction(PolicyAccountLink.ActionEnum.AUTO)
                .setFilter(null))
            .setProvisioning(client.instantiate(Provisioning.class)
                .setAction(Provisioning.ActionEnum.AUTO)
                .setConditions(client.instantiate(ProvisioningConditions.class)
                    .setDeprovisioned(client.instantiate(ProvisioningDeprovisionedCondition.class)
                        .setAction(ProvisioningDeprovisionedCondition.ActionEnum.NONE))
                    .setSuspended(client.instantiate(ProvisioningSuspendedCondition.class)
                        .setAction(ProvisioningSuspendedCondition.ActionEnum.NONE)))
                .setGroups(client.instantiate(ProvisioningGroups.class)
                    .setAction(ProvisioningGroups.ActionEnum.NONE)))
            .setMaxClockSkew(maxClockSkew)
            .setSubject(client.instantiate(PolicySubject.class)
                .setUserNameTemplate(client.instantiate(PolicyUserNameTemplate.class)
                    .setTemplate(subjectTemplate))
                .setMatchType(policySubjectMatchType))));

        return createdIdp;
    }
}

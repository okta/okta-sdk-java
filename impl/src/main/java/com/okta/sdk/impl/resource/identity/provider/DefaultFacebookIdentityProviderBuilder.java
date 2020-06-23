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

public class DefaultFacebookIdentityProviderBuilder implements IdentityProviderBuilder.FacebookIdentityProviderBuilder {

    private String name;
    private List<String> scopes;
    private String clientId;
    private String clientSecret;
    private Boolean isProfileMaster;
    private Integer maxClockSkew;
    private String userNameTemplate;
    private PolicySubjectMatchType policySubjectMatchType;

    @Override
    public DefaultFacebookIdentityProviderBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster) {
        this.isProfileMaster = isProfileMaster;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setUserNameTemplate(String userNameTemplate) {
        this.userNameTemplate = userNameTemplate;
        return this;
    }

    @Override
    public DefaultFacebookIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType) {
        this.policySubjectMatchType = policySubjectMatchType;
        return this;
    }

    @Override
    public IdentityProvider buildAndCreate(Client client) {

        IdentityProvider createdIdp = client.createIdentityProvider(client.instantiate(IdentityProvider.class)
            .setType(IdentityProvider.TypeEnum.FACEBOOK)
            .setName(name)
            .setProtocol(client.instantiate(Protocol.class)
                .setType(Protocol.TypeEnum.OAUTH2)
                .setScopes(scopes)
                .setCredentials(client.instantiate(IdentityProviderCredentials.class)
                    .setClient(client.instantiate(IdentityProviderCredentialsClient.class)
                        .setClientId(clientId)
                        .setClientSecret(clientSecret))))
            .setPolicy(client.instantiate(IdentityProviderPolicy.class)
                .setProvisioning(client.instantiate(Provisioning.class)
                    .setAction(Provisioning.ActionEnum.AUTO)
                    .setProfileMaster(isProfileMaster)
                    .setGroups(client.instantiate(ProvisioningGroups.class)
                        .setAction(ProvisioningGroups.ActionEnum.NONE))
                    .setConditions(client.instantiate(ProvisioningConditions.class)
                        .setDeprovisioned(client.instantiate(ProvisioningDeprovisionedCondition.class)
                            .setAction(ProvisioningDeprovisionedCondition.ActionEnum.NONE))
                        .setSuspended(client.instantiate(ProvisioningSuspendedCondition.class)
                            .setAction(ProvisioningSuspendedCondition.ActionEnum.NONE))))
                .setAccountLink(client.instantiate(PolicyAccountLink.class)
                    .setFilter(null)
                    .setAction(PolicyAccountLink.ActionEnum.AUTO))
                .setSubject(client.instantiate(PolicySubject.class)
                    .setUserNameTemplate(client.instantiate(PolicyUserNameTemplate.class)
                        .setTemplate(userNameTemplate))
                    .setMatchType(policySubjectMatchType))
                .setMaxClockSkew(maxClockSkew)));

        return createdIdp;
    }
}

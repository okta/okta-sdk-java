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
package com.okta.sdk.impl.resource.builder;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.PolicySubjectMatchType;
import com.okta.sdk.resource.IdentityProvider;
import com.okta.sdk.resource.builder.IdentityProviderBuilder;

import java.util.List;

@SuppressWarnings("rawtypes")
public class DefaultIdentityProviderBuilder<T extends IdentityProviderBuilder> implements IdentityProviderBuilder<T> {

    protected String name;
    protected String clientId;
    protected String clientSecret;
    protected List<String> scopes;
    protected Integer maxClockSkew;
    protected String userName;
    protected PolicySubjectMatchType matchType;
    protected Boolean isProfileMaster;

    @Override
    public T setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public T setClientId(String clientId) {
        this.clientId = clientId;
        return self();
    }

    @Override
    public T setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return self();
    }

    @Override
    public T setScopes(List<String> scopes) {
        this.scopes = scopes;
        return self();
    }

    @Override
    public T setMaxClockSkew(Integer maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
        return self();
    }

    @Override
    public T setUserName(String userName) {
        this.userName = userName;
        return self();
    }

    @Override
    public T setMatchType(PolicySubjectMatchType matchType) {
        this.matchType = matchType;
        return self();
    }

    @Override
    public T setIsProfileMaster(Boolean isProfileMaster) {
        this.isProfileMaster = isProfileMaster;
        return self();
    }

    @Override
    public T isProfileMaster(Boolean isProfileMaster) {
        return setIsProfileMaster(isProfileMaster);
    }

    @Override
    public IdentityProvider buildAndCreate(Client client) {
        return client.createIdentityProvider(client.instantiate(IdentityProvider.class));
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}

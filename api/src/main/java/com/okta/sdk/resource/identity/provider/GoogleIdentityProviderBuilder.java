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
 * Builder to add Google Identity Provider.
 * @since 2.0.0
 */
public interface GoogleIdentityProviderBuilder {

    GoogleIdentityProviderBuilder setName(String name);

    GoogleIdentityProviderBuilder setScopes(List<String> scopes);

    GoogleIdentityProviderBuilder setProtocolType(Protocol.TypeEnum protocolType);

    GoogleIdentityProviderBuilder setClientId(String clientId);

    GoogleIdentityProviderBuilder setClientSecret(String clientSecret);

    GoogleIdentityProviderBuilder setIsProfileMaster(Boolean isProfileMaster);

    GoogleIdentityProviderBuilder setMaxClockSkew(Integer maxClockSkew);

    GoogleIdentityProviderBuilder setUserNameTemplate(String userNameTemplate);

    GoogleIdentityProviderBuilder setPolicySubjectMatchType(PolicySubjectMatchType policySubjectMatchType);

    IdentityProvider buildAndCreate(Client client);

    static GoogleIdentityProviderBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.identity.provider.DefaultGoogleIdentityProviderBuilder");
    }
}

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

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.PolicySubjectMatchType;

import java.util.List;

public interface IdentityProviderBuilder<T extends IdentityProviderBuilder> {

    T setName(String name);

    T setClientId(String clientId);

    T setClientSecret(String clientSecret);

    T setScopes(List<String> scopes);

    T setMaxClockSkew(Integer maxClockSkew);

    T setUserName(String userName);

    T setMatchType(PolicySubjectMatchType policySubjectMatchType);

    T setIsProfileMaster(Boolean isProfileMaster);

    T isProfileMaster(Boolean isProfileMaster);

    IdentityProvider buildAndCreate(Client client);
}

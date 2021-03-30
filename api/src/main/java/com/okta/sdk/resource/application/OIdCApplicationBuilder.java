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
package com.okta.sdk.resource.application;

import com.okta.commons.lang.Classes;

import java.util.List;

public interface OIdCApplicationBuilder extends ApplicationBuilder<OIdCApplicationBuilder> {

    static OIdCApplicationBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultOIdCApplicationBuilder");
    }

    OIdCApplicationBuilder setApplicationType(OpenIdConnectApplicationType applicationType);

    OIdCApplicationBuilder setClientUri(String clientUri);

    OIdCApplicationBuilder setConsentMethod(OpenIdConnectApplicationConsentMethod consentMethod);

    OIdCApplicationBuilder setGrantTypes(List<OAuthGrantType> grantTypes);

    OIdCApplicationBuilder addGrantTypes(OAuthGrantType grantType);

    OIdCApplicationBuilder setLogoUri(String logoUri);

    OIdCApplicationBuilder setPolicyUri(String policyUri);

    OIdCApplicationBuilder setRedirectUris(List<String> redirectUris);

    OIdCApplicationBuilder addRedirectUris(String redirectUri);

    OIdCApplicationBuilder setResponseTypes(List<OAuthResponseType> responseTypes);

    OIdCApplicationBuilder addResponseTypes(OAuthResponseType responseType);

    OIdCApplicationBuilder setTosUri(String tosUri);

    OIdCApplicationBuilder setClientId(String clientId);

    OIdCApplicationBuilder setClientSecret(String clientSecret);

    OIdCApplicationBuilder setAutoKeyRotation(Boolean autoKeyRotation);

    OIdCApplicationBuilder setTokenEndpointAuthMethod(OAuthEndpointAuthenticationMethod tokenEndpointAuthMethod);

    OIdCApplicationBuilder setJwks(List<JsonWebKey> jsonWebKeyList);
}

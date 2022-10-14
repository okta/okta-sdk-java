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
import org.openapitools.client.model.JsonWebKey;

import java.util.List;

public interface OIDCApplicationBuilder extends ApplicationBuilder<OIDCApplicationBuilder> {

    static OIDCApplicationBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultOIDCApplicationBuilder");
    }

    OIDCApplicationBuilder setApplicationType(String applicationType);

    OIDCApplicationBuilder setClientUri(String clientUri);

    OIDCApplicationBuilder setConsentMethod(String consentMethod);

    OIDCApplicationBuilder setGrantTypes(List<String> grantTypes);

    OIDCApplicationBuilder addGrantTypes(String grantType);

    OIDCApplicationBuilder setLogoUri(String logoUri);

    OIDCApplicationBuilder setPolicyUri(String policyUri);

    OIDCApplicationBuilder setPostLogoutRedirectUris(List<String> postLogoutRedirectUris);

    OIDCApplicationBuilder setRedirectUris(List<String> redirectUris);

    OIDCApplicationBuilder addRedirectUris(String redirectUri);

    OIDCApplicationBuilder setResponseTypes(List<String> responseTypes);

    OIDCApplicationBuilder addResponseTypes(String responseType);

    OIDCApplicationBuilder setTosUri(String tosUri);

    OIDCApplicationBuilder setClientId(String clientId);

    OIDCApplicationBuilder setClientSecret(String clientSecret);

    OIDCApplicationBuilder setAutoKeyRotation(Boolean autoKeyRotation);

    OIDCApplicationBuilder setTokenEndpointAuthMethod(String tokenEndpointAuthMethod);

    OIDCApplicationBuilder setJwks(List<JsonWebKey> jsonWebKeyList);

    OIDCApplicationBuilder setImplicitAssignment(Boolean isImplicitAssignment);

    OIDCApplicationBuilder setInlineHookId(String inlineHookId);
}

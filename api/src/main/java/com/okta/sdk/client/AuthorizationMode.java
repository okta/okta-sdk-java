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
package com.okta.sdk.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration that defines the mapping between available Authentication schemes and Authorization modes.
 */
public enum AuthorizationMode {

    SSWS("SSWS", AuthenticationScheme.SSWS), // SSWS
    PRIVATE_KEY("PrivateKey", AuthenticationScheme.OAUTH2_PRIVATE_KEY), // OAuth2
    NONE("NONE", AuthenticationScheme.NONE); // None

    private final String label;
    private final AuthenticationScheme authenticationScheme;

    private static final Map<AuthenticationScheme, AuthorizationMode> lookup = new HashMap<>();

    static {
        for (AuthorizationMode authorizationMode : AuthorizationMode.values()) {
            lookup.put(authorizationMode.getAuthenticationScheme(), authorizationMode);
        }
    }

    AuthorizationMode(String label, AuthenticationScheme authenticationScheme) {
        this.label = label;
        this.authenticationScheme = authenticationScheme;
    }

    public String getLabel() {
        return this.label;
    }

    public AuthenticationScheme getAuthenticationScheme() {
        return this.authenticationScheme;
    }

    public static AuthorizationMode get(AuthenticationScheme authenticationScheme) {
        return lookup.get(authenticationScheme);
    }

    public static AuthorizationMode getAuthorizationMode(String label) {
        for (AuthorizationMode authorizationMode : values()) {
            if (authorizationMode.getLabel().equals(label)) {
                return authorizationMode;
            }
        }
        throw new IllegalArgumentException();
    }
}

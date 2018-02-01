/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.authc.credentials;

import com.okta.sdk.lang.Strings;

/**
 * This implementation represents the api key that is used to authenticate a Tenant in Okta.
 * @since 0.5.0
 */
public class TokenClientCredentials implements ClientCredentials<String> {

    private final String secret;

    public TokenClientCredentials(String secret) {
        if (!Strings.hasText(secret)) {
            throw new IllegalArgumentException("API token cannot be null or empty.");
        }
        this.secret = secret;
    }

    public String getCredentials() {
        return secret;
    }

    @Override
    public String toString() {
        return "<TokenClientCredentials>"; //never ever print the secret
    }

    @Override
    public int hashCode() {
        return secret != null ? secret.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TokenClientCredentials) {
            TokenClientCredentials other = (TokenClientCredentials)o;
            return (secret != null ? secret.equals(other.secret) : other.secret == null);
        }

        return false;
    }
}

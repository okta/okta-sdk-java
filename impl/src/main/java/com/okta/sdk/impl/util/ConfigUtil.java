/*
 * Copyright 2018-Present Okta, Inc.
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
package com.okta.sdk.impl.util;

public class ConfigUtil {

    public static final String RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----";
    public static final String EC_PRIVATE_KEY_HEADER = "-----BEGIN EC PRIVATE KEY-----";
    public static final String EC_PRIVATE_KEY_FOOTER = "-----END EC PRIVATE KEY-----";

    /**
     * Check if the private key PEM has BEGIN content wrapper.
     *
     * @param key the supplied private key PEM string.
     * @return true if the supplied private key has the BEGIN content wrapper, false otherwise.
     */
    public static boolean hasPrivateKeyContentWrapper(String key) {
        return key.startsWith("-----BEGIN");
    }
}

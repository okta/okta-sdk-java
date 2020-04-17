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
package com.okta.sdk.impl.oauth2;

import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * Abstraction for OAuth2 access token retrieval service function.
 *
 * @since 1.6.0
 */
public interface AccessTokenRetrieverService {
    /**
     * Obtain OAuth2 access token from Authorization Server endpoint.
     *
     * @return {@link OAuth2AccessToken}
     * @throws IOException if problems are encountered extracting the input private key
     * @throws InvalidKeyException if supplied private key is invalid
     * @throws OAuth2TokenRetrieverException if token could not be retrieved
     */
    OAuth2AccessToken getOAuth2AccessToken() throws IOException, InvalidKeyException, OAuth2TokenRetrieverException;
}

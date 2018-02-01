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
package com.okta.sdk.impl.http.authc;

import com.okta.sdk.impl.http.Request;
import com.okta.sdk.impl.http.support.RequestAuthenticationException;

/**
 * Interface to be implemented by HTTP authentication schemes. Such scheme defines the way the communication with
 * the Okta API server will be authenticated.
 *
 * @see com.okta.sdk.client.AuthenticationScheme
 * @since 0.5.0
 */
public interface RequestAuthenticator {
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Implementations of this operation will prepare the authentication information as expected by the Okta API server.
     *
     * @param request the request that will be sent to Okta API server, it shall be modified by the implementating classes
     *                in order to insert here the authentication information
     * @throws RequestAuthenticationException when the authentication request cannot be created
     */
    void authenticate(Request request) throws RequestAuthenticationException;

}

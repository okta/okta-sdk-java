/*
 * Copyright 2014 Stormpath, Inc.
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
package com.okta.sdk.authc;


/**
 * An authentication request represents all necessary information to authenticate a specific account.
 *
 * <h4>Usage</h4>
 *
 * <p>While there can be multiple implementations of this interface (typically constructed by type-specific builders)
 * to reflect different type of authentication attempts, the most common scenario is when a user logs in to your
 * application with username-password authentication.  For example:</p>
 *
 * <pre>
 * String username = getUsername(httpServletRequest); //implement me
 * String password = getPassword(httpServletRequest); //implement me
 *
 * AuthenticationRequest authcRequest = {@link UsernamePasswordRequests#builder() UsernamePasswordRequests.builder()}.setUsername(username).setPassword(password).build();
 *
 * myApplication.{@link com.okta.sdk.application.Application#authenticateAccount(AuthenticationRequest)
 * authenticateAccount}(authcRequest);
 *
 * </pre>
 *
 * @since 1.0.0
 */
public interface AuthenticationRequest<P, C> {

    /**
     * Returns the principal(s) (identifying information) that reflects the specific Account to be authenticated.  For
     * example, a username or email address.
     *
     * @return the principal(s) (identifying information) that reflects the specific Account to be authenticated.
     */
    P getPrincipals();

    /**
     * Returns the credentials (information that proves authenticity) of the the specific Account to be authenticated.
     * For example, a password.
     *
     * @return the credentials (information that proves authenticity) of the the specific Account to be authenticated.
     */
    C getCredentials();

    /**
     * Returns the host address (name or ip address) from where the authentication attempt is initiated.
     *
     * @return the host address (name or ip address) from where the authentication attempt is initiated.
     */
    String getHost();

    /**
     * Clears out (nulls) any identifying state, such as password bytes ({@code 0x00}), keys, etc, to eliminate the
     * possibility of memory access at a later time.
     */
    void clear();

    /**
     * Returns the {@link AuthenticationOptions} to be used in this AuthenticationRequest used to customize the response.
     *
     * @return the {@code AuthenticationOptions} that will be used to customize the response.
     * @since 1.0.RC5
     */
    AuthenticationOptions getResponseOptions();

}

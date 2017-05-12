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
 * A Builder to construct username/password-based {@link AuthenticationRequest}s.
 *
 * @see UsernamePasswordRequests#builder() UsernamePasswordRequests.builder()
 * @since 1.0.0
 */
public interface UsernamePasswordRequestBuilder extends AuthenticationRequestBuilder<UsernamePasswordRequestBuilder> {

    /**
     * Specifies the username or email that will be used to authenticate an account.
     *
     * @param usernameOrEmail the username or email that will be used to authenticate an account.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setUsernameOrEmail(String usernameOrEmail);

    /**
     * Specifies the password of the account that will be authenticated.
     *
     * @param password the account's raw password.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setPassword(String password);

    /**
     * Specifies the password of the account that will be authenticated.
     *
     * @param password the account's raw password.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setPassword(char[] password);

    /**
     * Specifies the host from where the end-user is accessing your application. This property is optional.
     *
     * @param host the host from where the end-user is accessing your application.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder setHost(String host);

    /**
     * Ensures that when the response is obtained, it will be retrieved with the specified options. This enhances performance
     * by leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param options the specific {@code BasicAuthenticationOptions} that will be used to customize the authentication response.
     * @return this instance for method chaining.
     */
    UsernamePasswordRequestBuilder withResponseOptions(BasicAuthenticationOptions options);

}

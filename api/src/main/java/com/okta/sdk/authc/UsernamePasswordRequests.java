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

import com.okta.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with username/password-based {@link AuthenticationRequest}s.  Most methods
 * are <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Application-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * AuthenticationRequest request = <b>UsernamePasswordRequests.builder()</b>
 *                         .setUsernameOrEmail(username)
 *                         .setPassword(submittedRawPlaintextPassword)
 *                         .build();
 * Account authenticated = application.authenticateAccount(request).getAccount();
 * </pre>
 *
 * @see UsernamePasswordRequestBuilder
 * @since 1.0.0
 */
public class UsernamePasswordRequests {

    /**
     * Returns a new {@link UsernamePasswordRequestBuilder} instance, used to construct username/password-based {@link AuthenticationRequest}s.
     *
     * @return a new {@link UsernamePasswordRequestBuilder} instance, used to construct username/password-based {@link AuthenticationRequest}s.
     */
    public static UsernamePasswordRequestBuilder builder() {
        return (UsernamePasswordRequestBuilder) Classes.newInstance("com.okta.sdk.impl.authc.DefaultUsernamePasswordRequestBuilder");
    }

    /**
     * Returns a new {@link BasicAuthenticationOptions} instance, that may be used to customize the {@link com.okta.sdk.authc.AuthenticationResult
     * AuthenticationResult} resource that will be obtained after a successful authentication.  For example:
     * <pre>
     * AuthenticationRequest request = UsernamePasswordRequests.builder()
     *                         .setUsernameOrEmail(username)
     *                         .setPassword(submittedRawPlaintextPassword)
     *                         <b>.withResponseOptions(UsernamePasswordRequests.options().withAccount())</b>
     *                         .build();
     * Account authenticated = application.authenticateAccount(request).getAccount();
     * </pre>
     *
     * @return a new {@link BasicAuthenticationOptions} instance, that may be used to customize the {@link com.okta.sdk.authc.AuthenticationResult
     * AuthenticationResult} resource that will be obtained after a successful authentication.
     * @see com.okta.sdk.authc.UsernamePasswordRequestBuilder#withResponseOptions(BasicAuthenticationOptions)
     */
    public static BasicAuthenticationOptions options() {
        return (BasicAuthenticationOptions) Classes.newInstance("com.okta.sdk.impl.authc.DefaultBasicAuthenticationOptions");
    }
}

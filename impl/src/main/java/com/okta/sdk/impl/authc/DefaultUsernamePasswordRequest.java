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
package com.okta.sdk.impl.authc;

import com.okta.sdk.authc.AuthenticationRequest;
import com.okta.sdk.authc.BasicAuthenticationOptions;
import com.okta.sdk.lang.Assert;

/**
 * @since 1.0.RC5
 */
public class DefaultUsernamePasswordRequest implements AuthenticationRequest<String, char[]> {

    private String username;
    private char[] password;
    private String host;
    private BasicAuthenticationOptions authenticationOptions;

    /**
     * Constructs a new {@code UsernamePasswordRequest} with the specified {@code usernameOrEmail} and {@code password}.
     *
     * @param usernameOrEmail the account's username or email address
     * @param password        the account's raw password
     */
    public DefaultUsernamePasswordRequest(String usernameOrEmail, char[] password) {
        Assert.hasText(usernameOrEmail, "usernameOrEmail cannot be null or empty.");
        this.username = usernameOrEmail;
        this.password = password;
    }

    @Override
    public String getPrincipals() {
        return username;
    }

    @Override
    public char[] getCredentials() {
        return password;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    public DefaultUsernamePasswordRequest setHost(String host) {
        Assert.hasText(host, "host cannot be null or empty.");
        this.host = host;
        return this;
    }

    @Override
    public BasicAuthenticationOptions getResponseOptions() {
        return this.authenticationOptions;
    }

    public DefaultUsernamePasswordRequest setResponseOptions(BasicAuthenticationOptions options) {
        Assert.notNull(options, "options cannot be null.");
        this.authenticationOptions = options;
        return this;
    }

    /**
     * Clears out (nulls) the username, password, host, accountStore and options.  The password bytes are explicitly set to
     * <tt>0x00</tt> to eliminate the possibility of memory access at a later time.
     */
    @Override
    public void clear() {
        this.username = null;
        this.host = null;
        this.authenticationOptions = null;

        char[] password = this.password;
        this.password = null;

        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = 0x00;
            }
        }

    }
}

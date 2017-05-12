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

import com.okta.sdk.api.ApiAuthenticationResult;

/**
 * A <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a> interface that allows one to
 * react to different types of authentication results, particularly those reflecting successfully authenticated API
 * requests (e.g. using {@link com.okta.sdk.api.ApiKey ApiKeys}.
 *
 * <p>Instead of implementing this interface directly, you might consider using the {@code
 * AuthenticationResultVisitorAdaptor}, which allows you to implement only the methods you are interested in.</p>
 *
 * <pre>
 *  AuthenticationResult authResult = application.authenticateApiRequest(httpRequest).execute();
 *
 *  authResult.accept(new AuthenticationResultVisitorAdapter() {
 *
 *      &#64;Override
 *      public void visit(ApiAuthenticationResult result) {
 *          Account account = result.getAccount();
 *          ApiKey apiKey = result.getApiKey();
 *          //do stuff
 *      }
 *
 *      &#64;Override
 *      public void visit(AccessTokenResult result) {
 *          TokenResponse tokenResponse = result.getTokenResponse();
 *          //do stuff
 *      }
 *  });
 * </pre>
 *
 * @see com.okta.sdk.api.ApiKey ApiKey
 * @see com.okta.sdk.api.ApiAuthenticationResult
 * @see com.okta.sdk.oauth.OAuthAuthenticationResult
 * @see com.okta.sdk.oauth.AccessTokenResult
 * @see com.okta.sdk.authc.AuthenticationResultVisitorAdapter
 * @since 1.0.0
 */
public interface AuthenticationResultVisitor {

    /**
     * Allows handling of a successful (usually username/password)-based authentication attempt.
     *
     * @param result the {@link AuthenticationResult} instance reflecting a successful username/password authentication
     *               attempt.
     */
    void visit(AuthenticationResult result);

    /**
     * Invoked when an API request has been successfully authenticated with an {@link com.okta.sdk.api.ApiKey
     * ApiKey}, usually as a result of HTTP Basic Authentication.
     *
     * @param result the {@link com.okta.sdk.api.ApiAuthenticationResult} representing the successful API
     *               authentication attempt.
     */
    void visit(ApiAuthenticationResult result);
}

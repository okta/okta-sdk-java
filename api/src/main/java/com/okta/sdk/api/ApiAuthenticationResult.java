/*
 * Copyright 2017 Okta
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
package com.okta.sdk.api;

import com.okta.sdk.authc.AuthenticationResult;

/**
 * An {@code AuthenticationResult} that indicates a client authenticated with your server-side API (eg
 * REST API) using an ApiKey.  The ApiKey used to authenticate the request can be obtained via {@link #getApiKey()}.
 *
 * <h3>Different Results</h3>
 *
 * <p>The actual runtime type of of an {@code ApiAuthenticationResult} might be more specific, for example, an
 * {@link com.okta.sdk.oauth.OAuthAuthenticationResult OAthAuthenticationResult}.  If you need to react
 * to different authentication result types, you can use an {@link com.okta.sdk.authc.AuthenticationResultVisitor}
 * to perform type-specific logic.  For example:</p>
 *
 * <pre>
 * ApiAuthenticationResult result = application.authenticateApiRequest(request);
 *
 * result.accept(new {@link com.okta.sdk.authc.AuthenticationResultVisitor AuthenticationResultVisitor}() {
 *
 *     &#64;Override
 *     public void visit(ApiAuthenticationResult result) {
 *         //the request was authenticated with HTTP Basic authentication
 *     }
 *
 *     &#64;Override
 *     public void visit(OAuthAuthenticationResult result) {
 *         //the request was authenticated using OAuth
 *     }
 *
 *     ... etc ...
 * });
 * </pre>
 *
 * @since 1.0.RC
 */
public interface ApiAuthenticationResult extends AuthenticationResult {

    /**
     * Returns the {@link ApiKey} used to authenticate the API request.
     *
     * @return the {@link ApiKey} used to authenticate the API request.
     */
    ApiKey getApiKey();

}

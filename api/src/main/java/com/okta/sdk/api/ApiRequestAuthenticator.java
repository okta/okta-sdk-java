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
package com.okta.sdk.api;

import com.okta.sdk.http.HttpRequest;

/**
 * Authenticates an API HTTP Request and returns a {@link ApiAuthenticationResult result}.
 *
 * @see com.okta.sdk.application.Application#authenticateAccount(AuthenticationRequest)
 * @see #authenticate(HttpRequest)
 * @since 1.0.RC
 */
public interface ApiRequestAuthenticator {

    /**
     * Authenticates an HTTP request submitted to your application's API, returning a result that reflects the
     * successfully authenticated {@link com.okta.sdk.account.Account} that made the request and the {@link ApiKey} used to authenticate
     * the request.  Throws a {@link com.okta.sdk.resource.ResourceException} if the request cannot be authenticated.
     * <p>
     * This method will automatically authenticate <em>both</em> HTTP Basic and OAuth 2 requests.  However, if you
     * require more specific or customized OAuth request processing, use the
     * {@link com.okta.sdk.oauth.OAuthApiRequestAuthenticator #authenticate(com.okta.sdk.http.HttpRequest)} method instead. That method allows you to customize how an OAuth request
     * is processed.  For example, you will likely want to call {@link com.okta.sdk.oauth.OAuthApiRequestAuthenticator#authenticate(com.okta.sdk.http.HttpRequest)} for requests
     * directed to your application's specific OAuth 2 token and authorization urls (often referenced as
     * {@code /oauth2/token} and {@code /oauth2/authorize} in OAuth 2 documentation).
     * </p>
     *
     * <h3>Example</h3>
     * <p>If your application does not run in a Servlet environment - for example, maybe you use a custom HTTP
     * framework, or Netty, or Play!, you can use the {@link com.okta.sdk.http.HttpRequestBuilder HttpRequestBuilder}
     * to represent your framework-specific HTTP request object into a format the Okta SDK understands.  For
     * example:</p>
     * <pre>
     * //assume a request to, say, https://api.mycompany.com/foo:
     *
     * public void onApiRequest(MyFrameworkHttpRequest request) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    <b>// Convert the framework-specific HTTP Request into a format the Okta SDK understands:
     *    {@link com.okta.sdk.http.HttpRequest HttpRequest} request = {@link com.okta.sdk.http.HttpRequests HttpRequests}.method(frameworkSpecificRequest.getMethod())
     *        .headers(frameworkSpecificRequest.getHeaders())
     *        .queryParameters(frameworkSpecificRequest.getQueryParameters())
     *        .build();</b>
     *
     *    ApiAuthenticationResult result = Applications.apiRequestAuthenticator(application).authenticate(request);
     *
     *    Account account = result.getAccount();
     *
     *    // Check to see that account is allowed to make this request or not before processing
     *    // the request.  For example, by checking the account's {@link com.okta.sdk.account.Account#getGroups() groups} or any of your own
     *    // application-specific permissions that might exist in the group's or account's {@link com.okta.sdk.account.Account#getCustomData() customData}.
     *    assertAuthorized(account); //implement the 'assertAuthorized' method yourself.
     *
     *    //process request here
     * }
     * </pre>
     *
     *
     * @param httpRequest a manually-constructed {@link com.okta.sdk.http.HttpRequest} instance
     *                    An argument of a different type will throw an IllegalArgumentException.
     * @return an {@link ApiAuthenticationResult} that represents the result of the authentication attempt.
     * @throws IllegalArgumentException if the method argument is null or is not a {@link com.okta.sdk.http.HttpRequest} instance.
     * @throws com.okta.sdk.resource.ResourceException if unable to authenticate the request
     * @see com.okta.sdk.oauth.OAuthApiRequestAuthenticator#authenticate(com.okta.sdk.http.HttpRequest)
     *
     * @since 1.0.RC4.6
     */
    ApiAuthenticationResult authenticate(HttpRequest httpRequest);
}

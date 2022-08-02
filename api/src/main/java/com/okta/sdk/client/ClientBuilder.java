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
package com.okta.sdk.client;

import com.okta.commons.http.config.Proxy;
import com.okta.sdk.authc.credentials.ClientCredentials;
import org.openapitools.client.ApiClient;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.Set;

/**
 *
 * <p>The {@code ClientBuilder} is used to construct Client instances with Okta credentials,
 * Proxy and Cache configuration.  Understanding caching is extremely important when creating a Client instance, so
 * please ensure you read the <em>Caching</em> section below.</p>
 *
 * Usage:
 *
 * <p>The simplest usage is to just call the {@link #build() build()} method, for example:</p>
 *
 * <pre>
 * Client client = {@link Clients Clients}.builder().{@link #build() build()};
 * </pre>
 *
 * <p>This will:</p>
 * <ul>
 *   <li>Automatically attempt to find your API credentials values in a number of default/conventional locations and then use
 *       the discovered values. Without any other configuration, the following locations will be each be checked,
 *       in order:</li>
 * </ul>
 *
 * <ol>
 *     <li>The environment variable {@code OKTA_CLIENT_TOKEN}.  If either of
 *         these values are present, they override any previously discovered value.</li>
 *     <li>The system properties {@code okta.client.token}.  If this value is present, it will override any
 *     previously discovered values.</li>
 * </ol>
 *
 * <p><b>SECURITY NOTICE:</b> While the {@code okta.client.token} system property or environment variable {@code OKTA_CLIENT_TOKEN}
 * may be used to represent your API Key Secret as mentioned above, this is not recommended: process listings on a machine
 * will expose process arguments (like system properties) or environment variables, thus exposing the secret value
 * to anyone that can read process listings.  As always, secret values should never be exposed to anyone other
 * than the person that owns the API Key.</p>
 *
 * @since 0.5.0
 */
public interface ClientBuilder {

    String DEFAULT_CLIENT_API_TOKEN_PROPERTY_NAME = "okta.client.token";
    String DEFAULT_CLIENT_ORG_URL_PROPERTY_NAME = "okta.client.orgUrl";
    String DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME = "okta.client.connectionTimeout";
    String DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME = "okta.client.authenticationScheme";
    String DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME = "okta.client.proxy.port";
    String DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME = "okta.client.proxy.host";
    String DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME = "okta.client.proxy.username";
    String DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME = "okta.client.proxy.password";
    String DEFAULT_CLIENT_AUTHORIZATION_MODE_PROPERTY_NAME = "okta.client.authorizationMode";
    String DEFAULT_CLIENT_ID_PROPERTY_NAME = "okta.client.clientId";
    String DEFAULT_CLIENT_SCOPES_PROPERTY_NAME = "okta.client.scopes";
    String DEFAULT_CLIENT_PRIVATE_KEY_PROPERTY_NAME = "okta.client.privateKey";
    String DEFAULT_CLIENT_KID_PROPERTY_NAME = "okta.client.kid";
    String DEFAULT_CLIENT_REQUEST_TIMEOUT_PROPERTY_NAME = "okta.client.requestTimeout";
    String DEFAULT_CLIENT_RETRY_MAX_ATTEMPTS_PROPERTY_NAME = "okta.client.rateLimit.maxRetries";
    String DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME = "okta.testing.disableHttpsCheck";

    /**
     * Allows specifying an {@code ApiKey} instance directly instead of relying on the
     * default location + override/fallback behavior defined in the {@link ClientBuilder documentation above}.
     *
     * Currently you should use a com.okta.sdk.impl.api.TokenClientCredentials (if you are NOT using an okta.yaml file)
     *
     * @param clientCredentials the token to use to authenticate requests to the Okta API server.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setClientCredentials(ClientCredentials clientCredentials);

    /**
     * Sets the HTTP proxy to be used when communicating with the Okta API server.  For example:
     *
     * <pre>
     * Proxy proxy = new Proxy("whatever.domain.com", 443);
     * Client client = {@link Clients Clients}.builder().setProxy(proxy).build();
     * </pre>
     *
     * @param proxy the {@code Proxy} you need to use.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setProxy(Proxy proxy);

    /**
     * Overrides the default (very secure)
     * <a href="https://developer.okta.com/docs/api/getting_started/design_principles#authentication">Okta SSWS Digest
     * Authentication Scheme</a> used to authenticate every request sent to the Okta API server.
     *
     * <pre>
     * Client client = Clients.builder()...
     *    // setApiKey, etc...
     *    .setAuthorizationMode(AuthorizationMode.SSWS) //set the SSWS authentication mode
     *    .build(); //build the Client
     * </pre>
     *
     * @param authorizationMode mode of authorization for requests to the Okta API server.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 1.6.0
     */
    ClientBuilder setAuthorizationMode(AuthorizationMode authorizationMode);

    /**
     * Allows specifying a list of scopes directly instead of relying on the
     * default location + override/fallback behavior defined in the {@link ClientBuilder documentation above}.
     *
     * @param scopes set of scopes for which the client requests access.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 1.6.0
     */
    ClientBuilder setScopes(Set<String> scopes);

    /**
     * Allows specifying the private key (PEM file) path (for private key jwt authentication) directly instead
     * of relying on the default location + override/fallback behavior defined
     * in the {@link ClientBuilder documentation above}.
     *
     * @param privateKey either the fully qualified string path to the private key PEM file (or)
     *                   the full PEM payload content.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 1.6.0
     */
    ClientBuilder setPrivateKey(String privateKey);

    /**
     * Allows specifying the private key (PEM file) path (for private key jwt authentication) directly instead
     * of relying on the default location + override/fallback behavior defined
     * in the {@link ClientBuilder documentation above}.
     *
     * @param privateKeyPath representing the path to private key PEM file.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 3.0.0
     */
    ClientBuilder setPrivateKey(Path privateKeyPath);

    /**
     * Allows specifying the private key (PEM file) path (for private key jwt authentication) directly instead
     * of relying on the default location + override/fallback behavior defined
     * in the {@link ClientBuilder documentation above}.
     *
     * @param privateKeyInputStream representing an InputStream with private key PEM file content.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 3.0.0
     */
    ClientBuilder setPrivateKey(InputStream privateKeyInputStream);

    /**
     * Allows specifying the private key (PEM file) path (for private key jwt authentication) directly instead
     * of relying on the default location + override/fallback behavior defined
     * in the {@link ClientBuilder documentation above}.
     *
     * @param privateKey the {@link PrivateKey} instance.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 3.0.0
     */
    ClientBuilder setPrivateKey(PrivateKey privateKey);

    /**
     * Allows specifying the client ID instead of relying on the default location + override/fallback behavior defined
     * in the {@link ClientBuilder documentation above}.
     *
     * @param clientId string representing the client ID.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 1.6.0
     */
    ClientBuilder setClientId(String clientId);

    /**
     * Allows specifying the Key ID (kid) instead of relying on the YAML config.
     *
     * @param kid string representing the Key ID.
     * @return the ClientBuilder instance for method chaining.
     *
     * @since 4.0.1
     */
    ClientBuilder setKid(String kid);

    /**
     * Sets both the timeout until a connection is established and the socket timeout (i.e. a maximum period of inactivity
     * between two consecutive data packets).  A timeout value of zero is interpreted as an infinite timeout.
     *
     * @param timeout connection and socket timeout in seconds
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setConnectionTimeout(int timeout);

    /**
     * Sets the base URL of the Okta REST API to use.  If unspecified, this value defaults to
     * {@code https://api.okta.com/v1} - the most common use case for Okta's public SaaS cloud.
     *
     * <p>Customers using Okta's Enterprise HA cloud might need to configure this to be
     * {@code https://enterprise.okta.io/v1} for example.</p>
     *
     * @param baseUrl the base URL of the Okta REST API to use.
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setOrgUrl(String baseUrl);

    /**
     * Sets the maximum number of seconds to wait when retrying before giving up.
     *
     * @param maxElapsed retry max elapsed duration in seconds
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setRetryMaxElapsed(int maxElapsed);

    /**
     * Sets the maximum number of attempts to retrying before giving up.
     *
     * @param maxAttempts retry max attempts
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setRetryMaxAttempts(int maxAttempts);

    /**
     * Constructs a new {@link org.openapitools.client.ApiClient} instance based on the ClientBuilder's current configuration state.
     *
     * @return a new {@link org.openapitools.client.ApiClient} instance based on the ClientBuilder's current configuration state.
     */
    ApiClient build();
}

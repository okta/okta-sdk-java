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

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.cache.CacheManager;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link com.okta.sdk.client.Client} instances.
 *
 * <p>The {@code ClientBuilder} is used to construct Client instances with Okta credentials,
 * Proxy and Cache configuration.  Understanding caching is extremely important when creating a Client instance, so
 * please ensure you read the <em>Caching</em> section below.</p>
 *
 * <h1>Usage</h1>
 *
 * <p>The simplest usage is to just call the {@link #build() build()} method, for example:</p>
 *
 * <pre>
 * Client client = {@link Clients Clients}.builder().{@link #build() build()};
 * </pre>
 *
 * <p>This will:</p>
 * <ul>
 *   <li>Automatically enable a simple in-memory {@link CacheManager} for enhanced performance (but please read
 *       the <em>Caching</em> section below for effects/warnings).</li>
 *   <li>Automatically attempt to find your API credentials values in a number of default/conventional locations and then use
 *       the discovered values. Without any other configuration, the following locations will be each be checked,
 *       in order:</li>
 * </ul>
 *
 * <ol>
 *     <li>The environment variable {@code OKTA_CLIENT_TOKEN}.  If either of
 *         these values are present, they override any previously discovered value.</li>
 *     <li>A yaml file that exists at the file path or URL specified by the {@code okta.client.file}
 *         system property.  If this file exists and any values are present, the values override any
 *         previously discovered value.  The {@code okta.client.file} system property String can be an
 *         absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
 *         {@code classpath:} prefixes respectively. Default value is {code ~/.okta/okta.yaml}. </li>
 *     <li>The system properties {@code okta.client.token}.  If this value is present, it will override any
 *     previously discovered values.</li>
 * </ol>
 *
 * <p><b>SECURITY NOTICE:</b> While the {@code okta.client.token} system property may be used to represent your
 * API Key Secret as mentioned above, this is not recommended: process listings on a machine will expose process
 * arguments (like system properties) and expose the secret value to anyone that can read process listings.  As
 * always, secret values should never be exposed to anyone other than the person that owns the API Key.</p>
 *
 * <p>While an API Key ID may be configured anywhere (and be visible by anyone), it is recommended to use a private
 * read-only file or an environment variable to represent API Key secrets.  <b>Never</b> commit secrets to source code
 * or version control.</p>
 *
 * <h2>Explicit API Key Configuration</h2>
 *
 * <p>The above default API Key searching heuristics may not be suitable to your needs.  In that case, you will likely
 * need to explicitly configure your API Key.  For example:</p>
 *
 * <pre>
 * ClientCredentials clientCredentials = new TokenClientCredentials("apiToken");
 *
 * Client client = {@link Clients Clients}.builder().setClientCredentials(clientCredentials).build();
 * </pre>
 * *
 * <h1>Caching</h1>
 *
 * NOTE: Caching support is currently in Alpha status.
 *
 * <p>By default, a simple production-grade in-memory {@code CacheManager} will be enabled when the Client instance is
 * created.  This {@code CacheManager} implementation has the following characteristics:</p>
 *
 * <ul>
 *     <li>It assumes a default time-to-live and time-to-idle of 1 hour for all cache entries.</li>
 *     <li>It auto-sizes itself based on your application's memory usage.  It will not cause OutOfMemoryExceptions.
 *         (It does this by retaining only 100 strong references to cached objects.  Additional cached objects are
 *          weakly referenced, ensuring the garbage collector can evict weakly referenced cache entries if it needs
 *          more memory for your application.).
 *     </li>
 * </ul>
 *
 * <p>but, please note:</p>
 *
 * <p><b>The default cache manager is not suitable for an application deployed across multiple JVMs.</b></p>
 *
 * <p>This is because the default implementation is 100% in-memory (in-process) in the current JVM.  If more than one
 * JVM is deployed with the same application codebase - for example, a web application deployed on multiple identical
 * hosts for scaling or high availability - each JVM would have it's own in-memory cache.  Multiple disconnected caches
 * for the same data will cause cache coherency problems and likely cause errors in your application!</p>
 *
 * <p>As a result, if your application that uses a Okta Client instance is deployed across multiple JVMs, you
 * SHOULD ensure that the Client is configured with a {@code CacheManager} implementation that uses coherent and
 * clustered/distributed memory.</p>
 *
 * <h2>Custom CacheManager</h2>
 *
 * <p>If you want to specify a custom {@code CacheManager} implementation:</p>
 *
 * <pre>
 * CacheManager cacheManager = new MyCacheManagerImplementation();
 * Client client = {@link com.okta.sdk.client.Clients Clients}.builder().<b>setCacheManager(cacheManager)</b>.build();
 * </pre>
 *
 * <h3>Application deployed on a single JVM</h3>
 *
 * <p>If your application is deployed on a <b>single JVM</b> and you still want to use the default
 * {@code CacheManager} implementation, but the default cache configuration does not meet your
 * needs, you can specify a different configuration.  For example:</p>
 *
 * <pre>
 * import static com.okta.sdk.cache.Caches.*;
 *
 * ...
 *
 * {@link com.okta.sdk.cache.Caches Caches}.{@link com.okta.sdk.cache.Caches#newCacheManager() newCacheManager()}
 *     .withDefaultTimeToLive(300, TimeUnit.SECONDS) // default
 *     .withDefaultTimeToIdle(300, TimeUnit.SECONDS) //general default
 *     .withCache({@link com.okta.sdk.cache.Caches#forResource(Class) forResource}(Account.class) //Account-specific cache settings
 *         .withTimeToLive(1, TimeUnit.HOURS)
 *         .withTimeToIdle(30, TimeUnit.MINUTES))
 *     .withCache({@link com.okta.sdk.cache.Caches#forResource(Class) forResource}(Group.class) //Group-specific cache settings
 *         .withTimeToLive(2, TimeUnit.HOURS))
 *
 *     //... etc ...
 *
 *     .build(); //build the CacheManager
 * </pre>
 *
 * <p>See the {@link com.okta.sdk.cache.Caches Caches} utility class and the
 * {@link com.okta.sdk.cache.CacheManagerBuilder CacheManagerBuilder} docs for more information.</p>
 *
 * <h3>Application deployed across multiple JVMs</h3>
 *
 * <p>If your application is deployed across multiple JVMs (for example a web app deployed on multiple web nodes for
 * scale and/or high availability), you will likely need to specify a custom
 * {@code CacheManager} implementation that is based on network distributed/coherent memory.  For example, an
 * implementation might delegate to a <a href="http://hazelcast.org/">Hazelcast</a> or
 * <a href="http://redis.io/">Redis</a> cluster. For example, if using the out-of-the-box Hazelcast plugin:</p>
 *
 * <pre>
 * import com.okta.sdk.hazelcast.HazelcastCacheManager;
 * // ... etc ...
 *
 * //Get a HazelcastInstance from your app/config.  This can be a HazelcastClient instance too:
 * HazelcastInstance hazelcastInstance = getHazelcastInstanceOrHazelcastClient();
 *
 * CacheManager cacheManager = new HazelcastCacheManager(hazelcastInstance);
 * Client client = {@link com.okta.sdk.client.Clients Clients}.builder().setCacheManager(cacheManager).build();
 * </pre>
 *
 * <p><b>NOTE</b>: it should be noted that <a href="http://memcached.org/">Memcache</a> <em>DOES NOT</em> guarantee cache
 * coherency.  It is strongly recommended that you do not use Memcache as your clustered caching solution (memcache
 * is fine for caching files, etc, but not data that is expected to be coherent across multiple cluster nodes).</p>
 *
 * <h4>Disable Caching</h4>
 *
 * <p>While production applications will usually enable a working CacheManager as described above, you might wish to disable caching
 * entirely when testing or debugging to remove 'moving parts' for better clarity into request/response
 * behavior.  You can do this by configuring a <em>disabled</em> {@code CacheManager} instance. For example:</p>
 *
 * <pre>
 * Client client = {@link com.okta.sdk.client.Clients Clients}.builder().setCacheManager(
 *     <b>{@code Caches.newDisabledCacheManager()}</b>
 * ).build();
 * </pre>
 *
 * <h1>Single Instance</h1>
 *
 * <p>Finally, it should be noted that, after building a {@code client} instance, that same instance should be used
 * everywhere in your application. Creating multiple client instances in a single application could have
 * negative side effects:</p>
 *
 * <p>As mentioned above, a client has a {@link #setCacheManager(com.okta.sdk.cache.CacheManager) CacheManager}
 * reference.  If your application uses multiple client instances, each client's referenced CacheManager would likely
 * become out of sync with the others, making your cache
 * <a href="http://en.wikipedia.org/wiki/Cache_coherence">incoherent</a>.  This will likely
 * result in exposing stale data to your application and could data errors.</p>
 *
 * <p>If you must have multiple {@code Client} instances in your application, you should ensure that each client
 * references the same exact {@code CacheManager} instance to guarantee cache coherency.</p>
 *
 * @since 0.5.0
 */
public interface ClientBuilder {

    String DEFAULT_CLIENT_API_TOKEN_PROPERTY_NAME = "okta.client.token";
    String DEFAULT_CLIENT_CACHE_ENABLED_PROPERTY_NAME = "okta.client.cache.enabled";
    String DEFAULT_CLIENT_CACHE_TTL_PROPERTY_NAME = "okta.client.cache.defaultTtl";
    String DEFAULT_CLIENT_CACHE_TTI_PROPERTY_NAME = "okta.client.cache.defaultTti";
    String DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME = "okta.client.cache.caches";
    String DEFAULT_CLIENT_ORG_URL_PROPERTY_NAME = "okta.client.orgUrl";
    String DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME = "okta.client.connectionTimeout";
    String DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME = "okta.client.authenticationScheme";
    String DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME = "okta.client.proxy.port";
    String DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME = "okta.client.proxy.host";
    String DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME = "okta.client.proxy.username";
    String DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME = "okta.client.proxy.password";

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
     * Sets the {@link CacheManager} that should be used to cache Okta REST resources, reducing round-trips to the
     * Okta API server and enhancing application performance.
     *
     * <h1>Single JVM Applications</h1>
     *
     * <p>If your application runs on a single JVM-based applications, the
     * {@link com.okta.sdk.cache.CacheManagerBuilder CacheManagerBuilder} should be sufficient for your needs. You
     * create a {@code CacheManagerBuilder} by using the {@link com.okta.sdk.cache.Caches Caches} utility class,
     * for example:</p>
     *
     * <pre>
     * import static com.okta.sdk.cache.Caches.*;
     *
     * ...
     *
     * Client client = Clients.builder()...
     *     .setCacheManager(
     *         {@link com.okta.sdk.cache.Caches#newCacheManager() newCacheManager()}
     *         .withDefaultTimeToLive(1, TimeUnit.DAYS) //general default
     *         .withDefaultTimeToIdle(2, TimeUnit.HOURS) //general default
     *         .withCache({@link com.okta.sdk.cache.Caches#forResource(Class) forResource}(Account.class) //Account-specific cache settings
     *             .withTimeToLive(1, TimeUnit.HOURS)
     *             .withTimeToIdle(30, TimeUnit.MINUTES))
     *         .withCache({@link com.okta.sdk.cache.Caches#forResource(Class) forResource}(Group.class) //Group-specific cache settings
     *             .withTimeToLive(2, TimeUnit.HOURS))
     *         .build() //build the CacheManager
     *     )
     *     .build(); //build the Client
     * </pre>
     *
     * <p><em>The above TTL and TTI times are just examples showing API usage - the times themselves are not
     * recommendations.  Choose TTL and TTI times based on your application requirements.</em></p>
     *
     * <h1>Multi-JVM / Clustered Applications</h1>
     *
     * <p>The default {@code CacheManager} instances returned by the
     * {@link com.okta.sdk.cache.CacheManagerBuilder CacheManagerBuilder} might not be sufficient for a
     * multi-instance application that runs on multiple JVMs and/or hosts/servers, as there could be cache-coherency
     * problems across the JVMs.  See the {@link com.okta.sdk.cache.CacheManagerBuilder CacheManagerBuilder}
     * JavaDoc for additional information.</p>
     *
     * <p>In these multi-JVM environments, you will likely want to create a simple CacheManager implementation that
     * wraps your distributed Caching API/product of choice and then plug that implementation in to the Okta SDK
     * via this method.  Hazelcast is one known cluster-safe caching product, and the Okta SDK has out-of-the-box
     * support for this as an extension module.  See the top-level class JavaDoc for a Hazelcast configuration
     * example.</p>
     *
     * @param cacheManager the {@link CacheManager} that should be used to cache Okta REST resources, reducing
     *                     round-trips to the Okta API server and enhancing application performance.
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setCacheManager(CacheManager cacheManager);

    /**
     * Overrides the default (very secure)
     * <a href="https://developer.okta.com/docs/api/getting_started/design_principles#authentication">Okta SSWS Digest
     * Authentication Scheme</a> used to authenticate every request sent to the Okta API server.
     *
     * <p>It is not recommended that you override this setting <em>unless</em> your application is deployed in an
     * environment that - outside of your application's control - manipulates request headers on outgoing HTTP requests.
     * Google App Engine is one such environment, for example.</p>
     *
     * <pre>
     * Client client = Clients.builder()...
     *    // setApiKey, etc...
     *    .setAuthenticationScheme(AuthenticationScheme.SSWS) //set the SSWS authentication scheme
     *    .build(); //build the Client
     * </pre>
     *
     * @param authenticationScheme the type of authentication to be used for communication with the Okta API server.
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme);

    /**
     * Sets both the timeout until a connection is established and the socket timeout (i.e. a maximum period of inactivity
     * between two consecutive data packets).  A timeout value of zero is interpreted as an infinite timeout.
     *
     * @param timeout connection and socket timeout in milliseconds
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
     * Constructs a new {@link Client} instance based on the ClientBuilder's current configuration state.
     *
     * @return a new {@link Client} instance based on the ClientBuilder's current configuration state.
     */
    Client build();
}

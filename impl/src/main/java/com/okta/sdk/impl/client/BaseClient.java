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
package com.okta.sdk.impl.client;

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.Proxy;
import com.okta.sdk.ds.DataStore;
import com.okta.sdk.ds.RequestBuilder;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.impl.ds.DefaultDataStore;
import com.okta.sdk.impl.ds.InternalDataStore;
import com.okta.sdk.impl.http.RequestExecutor;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.util.BaseUrlResolver;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Classes;
import com.okta.sdk.resource.Resource;

import java.lang.reflect.Constructor;

/**
 * The base client class.
 * @since 1.1.0
 */
public abstract class BaseClient implements DataStore {

    private final InternalDataStore dataStore;

    /**
     * Instantiates a new Client instance that will communicate with the Okta REST API.  See the class-level
     * JavaDoc for a usage example.
     *
     * @param clientCredentialsResolver       Okta API Key resolver
     * @param baseUrlResolver      Okta base URL resolver
     * @param proxy                the HTTP proxy to be used when communicating with the Okta API server (can be
     *                             null)
     * @param cacheManager         the {@link CacheManager} that should be used to cache
     *                             Okta REST resources (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Okta API
     *                             server (can be null)
     * @param requestAuthenticatorFactory factory used to handle creating autentication requests
     * @param connectionTimeout    connection timeout in seconds
     */
    public BaseClient(ClientCredentialsResolver clientCredentialsResolver, BaseUrlResolver baseUrlResolver, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {
        Assert.notNull(clientCredentialsResolver, "clientCredentialsResolver argument cannot be null.");
        Assert.notNull(baseUrlResolver, "baseUrlResolver argument cannot be null.");
        Assert.isTrue(connectionTimeout >= 0, "connectionTimeout cannot be a negative number.");
        RequestExecutor requestExecutor = createRequestExecutor(clientCredentialsResolver.getClientCredentials(), proxy, authenticationScheme, requestAuthenticatorFactory, connectionTimeout);
        this.dataStore = createDataStore(requestExecutor, baseUrlResolver, clientCredentialsResolver, cacheManager);
    }


    protected InternalDataStore createDataStore(RequestExecutor requestExecutor, BaseUrlResolver baseUrlResolver, ClientCredentialsResolver clientCredentialsResolver, CacheManager cacheManager) {
        return new DefaultDataStore(requestExecutor, baseUrlResolver, clientCredentialsResolver, cacheManager);
    }

    @Override
    public ClientCredentials getClientCredentials() {
        return this.dataStore.getClientCredentials();
    }

    @Override
    public CacheManager getCacheManager() {
        return this.dataStore.getCacheManager();
    }

    public InternalDataStore getDataStore() {
        return this.dataStore;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected RequestExecutor createRequestExecutor(ClientCredentials clientCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, int connectionTimeout) {

        String className = "com.okta.sdk.impl.http.httpclient.HttpClientRequestExecutor";

        Class requestExecutorClass;

        if (Classes.isAvailable(className)) {
            requestExecutorClass = Classes.forName(className);
        } else {
            //we might be able to check for other implementations in the future, but for now, we only support
            //HTTP calls via the HttpClient.  Throw an exception:

            String msg = "Unable to find the '" + className + "' implementation on the classpath.  Please ensure you " +
                    "have added the okta-sdk-httpclient .jar file to your runtime classpath.";
            throw new RuntimeException(msg);
        }

        Constructor<RequestExecutor> ctor = Classes.getConstructor(requestExecutorClass, ClientCredentials.class, Proxy.class, AuthenticationScheme.class, RequestAuthenticatorFactory.class, Integer.class);

        return Classes.instantiate(ctor, clientCredentials, proxy, authenticationScheme, requestAuthenticatorFactory, connectionTimeout);
    }

    // ========================================================================
    // DataStore methods (delegate to underlying DataStore instance)
    // ========================================================================

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to instantiate Resource.
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @return a new instance of the specified Resource.
     */
    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.dataStore.instantiate(clazz);
    }

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to look up a Resource.
     *
     * @param href  the resource URL of the resource to retrieve
     * @param clazz the {@link Resource} sub-interface to instantiate
     * @param <T>   type parameter indicating the returned value is a {@link Resource} instance.
     * @return an instance of the specified class based on the data returned from the specified {@code href} URL.
     */
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return this.dataStore.getResource(href, clazz);
    }

    @Override
    public <T extends Resource> T create(String parentHref, T resource) {
        return this.dataStore.create(parentHref, resource);
    }

    @Override
    public <T extends Resource> void save(String href, T resource) {
        this.dataStore.save(href, resource);
    }

    @Override
    public <T extends Resource> void delete(String href, T resource) {
        this.dataStore.delete(href, resource);
    }

    @Override
    public RequestBuilder http() {
        return this.dataStore.http();
    }
}
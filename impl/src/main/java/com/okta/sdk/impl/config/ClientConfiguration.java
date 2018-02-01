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
package com.okta.sdk.impl.config;

import com.okta.sdk.cache.CacheConfigurationBuilder;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.util.BaseUrlResolver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds the default configuration properties.
 *
 * During application initialization all the properties found in the pre-defined locations that are
 * defined by the user will be added here in the order defined in {@link com.okta.sdk.impl.client.DefaultClientBuilder}.
 * Unset values will use default values from {@code com/okta/sdk/config/okta.yaml}.
 *
 * @since 0.5.0
 */
public class ClientConfiguration {

    private String apiToken;
    private ClientCredentialsResolver clientCredentialsResolver;
    private boolean cacheManagerEnabled;
    private long cacheManagerTtl;
    private long cacheManagerTti;
    private Map<String, CacheConfigurationBuilder> cacheManagerCaches = new LinkedHashMap<>();
    private String baseUrl;
    private int connectionTimeout;
    private AuthenticationScheme authenticationScheme;
    private RequestAuthenticatorFactory requestAuthenticatorFactory;
    private int proxyPort;
    private String proxyHost;
    private String proxyUsername;
    private String proxyPassword;
    private BaseUrlResolver baseUrlResolver;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public ClientCredentialsResolver getClientCredentialsResolver() {
        return clientCredentialsResolver;
    }

    public void setClientCredentialsResolver(ClientCredentialsResolver clientCredentialsResolver) {
        this.clientCredentialsResolver = clientCredentialsResolver;
    }

    public AuthenticationScheme getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public RequestAuthenticatorFactory getRequestAuthenticatorFactory() {
        return requestAuthenticatorFactory;
    }

    public void setRequestAuthenticatorFactory(RequestAuthenticatorFactory requestAuthenticatorFactory) {
        this.requestAuthenticatorFactory = requestAuthenticatorFactory;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isCacheManagerEnabled() {
        return cacheManagerEnabled;
    }

    public void setCacheManagerEnabled(boolean cacheManagerEnabled) {
        this.cacheManagerEnabled = cacheManagerEnabled;
    }

    public Map<String, CacheConfigurationBuilder> getCacheManagerCaches() {
        return cacheManagerCaches;
    }

    public void setCacheManagerCaches(Map<String, CacheConfigurationBuilder> cacheManagerCaches) {
        this.cacheManagerCaches = cacheManagerCaches;
    }

    /**
     * Time to idle for cache manager in seconds
     * @return seconds until time to idle expires
     */
    public long getCacheManagerTti() {
        return cacheManagerTti;
    }

    /**
     * The cache manager's time to idle in seconds
     * @param cacheManagerTti the time to idle in seconds
     */
    public void setCacheManagerTti(long cacheManagerTti) {
        this.cacheManagerTti = cacheManagerTti;
    }

    /**
     * Time to live for cache manager in seconds
     * @return seconds until time to live expires
     */
    public long getCacheManagerTtl() {
        return cacheManagerTtl;
    }

    /**
     * The cache manager's time to live in seconds
     * @param cacheManagerTtl the time to live in seconds
     */
    public void setCacheManagerTtl(long cacheManagerTtl) {
        this.cacheManagerTtl = cacheManagerTtl;
    }

    /**
     * Connection timeout in seconds
     * @return seconds until connection timeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Connection timeout in seconds.
     *
     * @param connectionTimeout the timeout value in seconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public BaseUrlResolver getBaseUrlResolver() {
        return baseUrlResolver;
    }

    public void setBaseUrlResolver(BaseUrlResolver baseUrlResolver) {
        this.baseUrlResolver = baseUrlResolver;
    }

    @Override
    public String toString() {
        return "ClientConfiguration{" +
                ", cacheManagerTtl=" + cacheManagerTtl +
                ", cacheManagerTti=" + cacheManagerTti +
                ", cacheManagerCaches=" + cacheManagerCaches +
                ", baseUrl='" + baseUrl + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", authenticationScheme=" + authenticationScheme +
                ", proxyPort=" + proxyPort +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyUsername='" + proxyUsername + '\'' +
                ", proxyPassword='" + proxyPassword + '\'' +
                '}';
    }
}

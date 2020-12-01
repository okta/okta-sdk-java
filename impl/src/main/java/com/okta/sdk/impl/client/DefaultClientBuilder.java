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

import com.okta.commons.configcheck.ConfigurationValidator;
import com.okta.commons.http.config.BaseUrlResolver;
import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Classes;
import com.okta.commons.lang.Strings;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.cache.CacheConfigurationBuilder;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.cache.CacheManagerBuilder;
import com.okta.sdk.cache.Caches;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.commons.http.config.Proxy;
import com.okta.sdk.impl.api.ClientCredentialsResolver;
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.config.EnvironmentVariablesPropertiesSource;
import com.okta.sdk.impl.config.OptionalPropertiesSource;
import com.okta.sdk.impl.config.PropertiesSource;
import com.okta.sdk.impl.config.ResourcePropertiesSource;
import com.okta.sdk.impl.config.SystemPropertiesSource;
import com.okta.sdk.impl.config.YAMLPropertiesSource;
import com.okta.sdk.impl.http.authc.RequestAuthenticatorFactory;
import com.okta.sdk.impl.io.ClasspathResource;
import com.okta.sdk.impl.io.DefaultResourceFactory;
import com.okta.sdk.impl.io.Resource;
import com.okta.sdk.impl.io.ResourceFactory;
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverService;
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverServiceImpl;
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials;
import com.okta.sdk.impl.util.ConfigUtil;
import com.okta.sdk.impl.util.DefaultBaseUrlResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>The default {@link ClientBuilder} implementation. This looks for configuration files
 * in the following locations and order of precedence (last one wins).</p>
 * <ul>
 * <li>classpath:com/okta/sdk/config/okta.properties</li>
 * <li>classpath:com/okta/sdk/config/okta.yaml</li>
 * <li>classpath:okta.properties</li>
 * <li>classpath:okta.yaml</li>
 * <li>~/.okta/okta.yaml</li>
 * <li>Environment Variables (with dot notation converted to uppercase + underscores)</li>
 * <li>System Properties</li>
 * <li>Programmatically</li>
 * </ul>
 *
 * Please be aware that, in general, loading secrets (such as api-keys or PEM-content) from environment variables
 * or system properties can lead to those secrets being leaked.
 *
 * @since 0.5.0
 */
public class DefaultClientBuilder implements ClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(DefaultClientBuilder.class);

    private static final String ENVVARS_TOKEN   = "envvars";
    private static final String SYSPROPS_TOKEN  = "sysprops";
    private static final String OKTA_CONFIG_CP  = "com/okta/sdk/config/";
    private static final String OKTA_YAML       = "okta.yaml";
    private static final String OKTA_PROPERTIES = "okta.properties";

    private CacheManager cacheManager;
    private ClientCredentials clientCredentials;
    private boolean allowNonHttpsForTesting = false;

    private ClientConfiguration clientConfig = new ClientConfiguration();

    private AccessTokenRetrieverService accessTokenRetrieverService;

    public DefaultClientBuilder() {
        this(new DefaultResourceFactory());
    }

    DefaultClientBuilder(ResourceFactory resourceFactory) {
        Collection<PropertiesSource> sources = new ArrayList<>();

        for (String location : configSources()) {

            if (ENVVARS_TOKEN.equalsIgnoreCase(location)) {
                sources.add(EnvironmentVariablesPropertiesSource.oktaFilteredPropertiesSource());
            }
            else if (SYSPROPS_TOKEN.equalsIgnoreCase(location)) {
                sources.add(SystemPropertiesSource.oktaFilteredPropertiesSource());
            }
            else {
                Resource resource = resourceFactory.createResource(location);

                PropertiesSource wrappedSource;
                if (Strings.endsWithIgnoreCase(location, ".yaml")) {
                    wrappedSource = new YAMLPropertiesSource(resource);
                } else {
                    wrappedSource = new ResourcePropertiesSource(resource);
                }

                PropertiesSource propertiesSource = new OptionalPropertiesSource(wrappedSource);
                sources.add(propertiesSource);
            }
        }

        Map<String, String> props = new LinkedHashMap<>();

        for (PropertiesSource source : sources) {
            Map<String, String> srcProps = source.getProperties();
            props.putAll(srcProps);
        }

        // check to see if property value is null before setting value
        // if != null, allow it to override previously set values
        if (Strings.hasText(props.get(DEFAULT_CLIENT_API_TOKEN_PROPERTY_NAME))) {
            clientConfig.setApiToken(props.get(DEFAULT_CLIENT_API_TOKEN_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_CACHE_ENABLED_PROPERTY_NAME))) {
            clientConfig.setCacheManagerEnabled(Boolean.valueOf(props.get(DEFAULT_CLIENT_CACHE_ENABLED_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_CACHE_TTL_PROPERTY_NAME))) {
            clientConfig.setCacheManagerTtl(Long.parseLong(props.get(DEFAULT_CLIENT_CACHE_TTL_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_CACHE_TTI_PROPERTY_NAME))) {
            clientConfig.setCacheManagerTti(Long.parseLong(props.get(DEFAULT_CLIENT_CACHE_TTI_PROPERTY_NAME)));
        }

        for (String prop : props.keySet()) {
            boolean isPrefix = prop.length() == DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME.length();
            if (!isPrefix && prop.startsWith(DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME)) {
                // get class from prop name
                String cacheClass = prop.substring(DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME.length() + 1, prop.length() - 4);
                String cacheTti = props.get(DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME + "." + cacheClass + ".tti");
                String cacheTtl = props.get(DEFAULT_CLIENT_CACHE_CACHES_PROPERTY_NAME + "." + cacheClass + ".ttl");
                CacheConfigurationBuilder cacheBuilder = Caches.forResource(Classes.forName(cacheClass));
                if (Strings.hasText(cacheTti)) {
                    cacheBuilder.withTimeToIdle(Long.parseLong(cacheTti), TimeUnit.SECONDS);
                }
                if (Strings.hasText(cacheTtl)) {
                    cacheBuilder.withTimeToLive(Long.parseLong(cacheTtl), TimeUnit.SECONDS);
                }
                if (!clientConfig.getCacheManagerCaches().containsKey(cacheClass)) {
                    clientConfig.getCacheManagerCaches().put(cacheClass, cacheBuilder);
                }
            }
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME))) {
            allowNonHttpsForTesting = Boolean.valueOf(props.get(DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_ORG_URL_PROPERTY_NAME))) {
            String baseUrl = props.get(DEFAULT_CLIENT_ORG_URL_PROPERTY_NAME);
            // remove backslashes that can end up in file when it's written programmatically, e.g. in a test
            baseUrl = baseUrl.replace("\\:", ":");
            ConfigurationValidator.assertOrgUrl(baseUrl, allowNonHttpsForTesting);
            clientConfig.setBaseUrl(baseUrl);
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME))) {
            clientConfig.setConnectionTimeout(Integer.parseInt(props.get(DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME))) {
            clientConfig.setAuthenticationScheme(Enum.valueOf(AuthenticationScheme.class, props.get(DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME))) {
            clientConfig.setProxyPort(Integer.parseInt(props.get(DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME))) {
            clientConfig.setProxyHost(props.get(DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME))) {
            clientConfig.setProxyUsername(props.get(DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME))) {
            clientConfig.setProxyPassword(props.get(DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_AUTHORIZATION_MODE_PROPERTY_NAME))) {
            clientConfig.setAuthorizationMode(AuthorizationMode.getAuthorizationMode(props.get(DEFAULT_CLIENT_AUTHORIZATION_MODE_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_ID_PROPERTY_NAME))) {
            clientConfig.setClientId(props.get(DEFAULT_CLIENT_ID_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_SCOPES_PROPERTY_NAME))) {
            Set<String> scopes = new HashSet<>(Arrays.asList(props.get(DEFAULT_CLIENT_SCOPES_PROPERTY_NAME).split("[\\s,]+")));
            clientConfig.setScopes(scopes);
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_PRIVATE_KEY_PROPERTY_NAME))) {
            clientConfig.setPrivateKey(props.get(DEFAULT_CLIENT_PRIVATE_KEY_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_REQUEST_TIMEOUT_PROPERTY_NAME))) {
            clientConfig.setRetryMaxElapsed(Integer.parseInt(props.get(DEFAULT_CLIENT_REQUEST_TIMEOUT_PROPERTY_NAME)));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_RETRY_MAX_ATTEMPTS_PROPERTY_NAME))) {
            clientConfig.setRetryMaxAttempts(Integer.parseInt(props.get(DEFAULT_CLIENT_RETRY_MAX_ATTEMPTS_PROPERTY_NAME)));
        }
    }

    @Override
    public ClientBuilder setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        clientConfig.setProxyHost(proxy.getHost());
        clientConfig.setProxyPort(proxy.getPort());
        clientConfig.setProxyUsername(proxy.getUsername());
        clientConfig.setProxyPassword(proxy.getPassword());
        return this;
    }

    @Override
    public ClientBuilder setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }

    @Override
    public ClientBuilder setConnectionTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout cannot be a negative number.");
        this.clientConfig.setConnectionTimeout(timeout);
        return this;
    }

    @Override
    public ClientBuilder setClientCredentials(ClientCredentials clientCredentials) {
        Assert.isInstanceOf(ClientCredentials.class, clientCredentials);
        this.clientCredentials = clientCredentials;
        return this;
    }

    public ClientBuilder setRequestAuthenticatorFactory(RequestAuthenticatorFactory factory) {
        Assert.notNull(factory, "factory argument cannot be null");
        this.clientConfig.setRequestAuthenticatorFactory(factory);
        return this;
    }

    public ClientBuilder setClientCredentialsResolver(ClientCredentialsResolver clientCredentialsResolver) {
        Assert.notNull(clientCredentialsResolver, "clientCredentialsResolver must not be null.");
        this.clientConfig.setClientCredentialsResolver(clientCredentialsResolver);
        return this;
    }

    public ClientBuilder setBaseUrlResolver(BaseUrlResolver baseUrlResolver) {
        Assert.notNull(baseUrlResolver, "baseUrlResolver must not be null");
        this.clientConfig.setBaseUrlResolver(baseUrlResolver);
        return this;
    }

    @Override
    public ClientBuilder setRetryMaxElapsed(int maxElapsed) {
        this.clientConfig.setRetryMaxElapsed(maxElapsed);
        return this;
    }

    @Override
    public ClientBuilder setRetryMaxAttempts(int maxAttempts) {
        this.clientConfig.setRetryMaxAttempts(maxAttempts);
        return this;
    }

    @Override
    public Client build() {
        if (!this.clientConfig.isCacheManagerEnabled()) {
            log.debug("CacheManager disabled. Defaulting to DisabledCacheManager");
            this.cacheManager = Caches.newDisabledCacheManager();
        } else if (this.cacheManager == null) {
            log.debug("No CacheManager configured. Defaulting to in-memory CacheManager with default TTL and TTI of five minutes.");

            CacheManagerBuilder cacheManagerBuilder = Caches.newCacheManager()
                    .withDefaultTimeToIdle(this.clientConfig.getCacheManagerTti(), TimeUnit.SECONDS)
                    .withDefaultTimeToLive(this.clientConfig.getCacheManagerTtl(), TimeUnit.SECONDS);
            if (this.clientConfig.getCacheManagerCaches().size() > 0) {
                for (CacheConfigurationBuilder builder : this.clientConfig.getCacheManagerCaches().values()) {
                    cacheManagerBuilder.withCache(builder);
                }
            }

            this.cacheManager = cacheManagerBuilder.build();
        }

        if (this.clientConfig.getBaseUrlResolver() == null) {
            ConfigurationValidator.assertOrgUrl(this.clientConfig.getBaseUrl(), allowNonHttpsForTesting);
            this.clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver(this.clientConfig.getBaseUrl()));
        }

        if (!isOAuth2Flow()) {
            if (this.clientConfig.getClientCredentialsResolver() == null && this.clientCredentials != null) {
                this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientCredentials));
            } else if (this.clientConfig.getClientCredentialsResolver() == null) {
                this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientConfig));
            }
        } else {
            this.clientConfig.setAuthenticationScheme(AuthenticationScheme.OAUTH2_PRIVATE_KEY);

            validateOAuth2ClientConfig(this.clientConfig);

            accessTokenRetrieverService = new AccessTokenRetrieverServiceImpl(clientConfig);

            OAuth2ClientCredentials oAuth2ClientCredentials =
                new OAuth2ClientCredentials(accessTokenRetrieverService);

            this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(oAuth2ClientCredentials));
        }

        return new DefaultClient(clientConfig, cacheManager);
    }

    /**
     * @since 1.6.0
     */
    private void validateOAuth2ClientConfig(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration.getClientId(), "clientId cannot be null");
        Assert.isTrue(clientConfiguration.getScopes() != null && !clientConfiguration.getScopes().isEmpty(),
            "At least one scope is required");
        String privateKey = clientConfiguration.getPrivateKey();
        Assert.hasText(privateKey, "privateKey cannot be null (either PEM file path (or) full PEM content must be supplied)");

        if (!ConfigUtil.hasPrivateKeyContentWrapper(privateKey)) {
            // privateKey is a file path, check if the file exists
            Path privateKeyPemFilePath;
            try {
                privateKeyPemFilePath = Paths.get(privateKey);
            } catch (InvalidPathException ipe) {
                throw new IllegalArgumentException("Invalid privateKey file path", ipe);
            }
            boolean privateKeyPemFileExists = Files.exists(privateKeyPemFilePath, LinkOption.NOFOLLOW_LINKS);
            Assert.isTrue(privateKeyPemFileExists, "privateKey file does not exist");
        }
    }

    @Override
    public ClientBuilder setOrgUrl(String baseUrl) {
        ConfigurationValidator.assertOrgUrl(baseUrl, allowNonHttpsForTesting);
        this.clientConfig.setBaseUrl(baseUrl);
        return this;
    }

    @Override
    public ClientBuilder setAuthorizationMode(AuthorizationMode authorizationMode) {
        this.clientConfig.setAuthorizationMode(authorizationMode);
        this.clientConfig.setAuthenticationScheme(authorizationMode.getAuthenticationScheme());
        return this;
    }

    @Override
    public ClientBuilder setScopes(Set<String> scopes) {
        if (isOAuth2Flow()) {
            Assert.isTrue(scopes != null && !scopes.isEmpty(), "At least one scope is required");
            this.clientConfig.setScopes(scopes);
        }
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(String privateKey) {
        if (isOAuth2Flow()) {
            Assert.notNull(privateKey, "Missing privateKey");
            this.clientConfig.setPrivateKey(privateKey);
        }
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(Path privateKeyPath) {
        if (isOAuth2Flow()) {
            Assert.notNull(privateKeyPath, "Missing privateKeyFile");
            this.clientConfig.setPrivateKey(getFileContent(privateKeyPath));
        }
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(InputStream privateKeyStream) {
        if (isOAuth2Flow()) {
            Assert.notNull(privateKeyStream, "Missing privateKeyFile");
            this.clientConfig.setPrivateKey(getFileContent(privateKeyStream));
        }
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(PrivateKey privateKey) {
        if (isOAuth2Flow()) {
            Assert.notNull(privateKey, "Missing privateKeyFile");
            String encodedString = "-----BEGIN PRIVATE KEY-----\n";
            encodedString = encodedString + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n";
            encodedString = encodedString + "-----END PRIVATE KEY-----\n";
            this.clientConfig.setPrivateKey(encodedString);
        }
        return this;
    }

    private String getFileContent(File file) {
        try {
            return readFromInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getFileContent(Path path) {
        return getFileContent(path.getFileName().toFile());
    }

    private String getFileContent(InputStream privateKeyStream) {
        try {
            return readFromInputStream(privateKeyStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream, "InputStream cannot be null.");
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    @Override
    public ClientBuilder setClientId(String clientId) {
        ConfigurationValidator.assertClientId(clientId);
        this.clientConfig.setClientId(clientId);
        return this;
    }

    boolean isOAuth2Flow() {
        return this.getClientConfiguration().getAuthorizationMode() == AuthorizationMode.PRIVATE_KEY;
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfig;
    }

    private static String[] configSources() {

        // lazy load the config sources as the user.home system prop could change for testing
        return new String[] {
            ClasspathResource.SCHEME_PREFIX + OKTA_CONFIG_CP + OKTA_PROPERTIES,
            ClasspathResource.SCHEME_PREFIX + OKTA_CONFIG_CP + OKTA_YAML,
            ClasspathResource.SCHEME_PREFIX + OKTA_PROPERTIES,
            ClasspathResource.SCHEME_PREFIX + OKTA_YAML,
            System.getProperty("user.home") + File.separatorChar + ".okta" + File.separatorChar + OKTA_YAML,
            ENVVARS_TOKEN,
            SYSPROPS_TOKEN
        };
    }

}

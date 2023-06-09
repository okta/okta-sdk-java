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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.okta.commons.configcheck.ConfigurationValidator;
import com.okta.commons.http.config.Proxy;
import com.okta.commons.lang.ApplicationInfo;
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
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver;
import com.okta.sdk.impl.config.*;
import com.okta.sdk.impl.deserializer.UserProfileDeserializer;
import com.okta.sdk.impl.io.ClasspathResource;
import com.okta.sdk.impl.io.DefaultResourceFactory;
import com.okta.sdk.impl.io.Resource;
import com.okta.sdk.impl.io.ResourceFactory;
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverService;
import com.okta.sdk.impl.oauth2.AccessTokenRetrieverServiceImpl;
import com.okta.sdk.impl.oauth2.OAuth2ClientCredentials;
import com.okta.sdk.impl.serializer.UserProfileSerializer;
import com.okta.sdk.impl.util.ConfigUtil;
import com.okta.sdk.impl.util.DefaultBaseUrlResolver;

import com.okta.sdk.impl.retry.OktaHttpRequestRetryStrategy;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.DefaultBackoffStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.util.Timeout;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import org.openapitools.client.ApiClient;

import org.openapitools.client.model.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            clientConfig.setCacheManagerEnabled(Boolean.parseBoolean(props.get(DEFAULT_CLIENT_CACHE_ENABLED_PROPERTY_NAME)));
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
            allowNonHttpsForTesting = Boolean.parseBoolean(props.get(DEFAULT_CLIENT_TESTING_DISABLE_HTTPS_CHECK_PROPERTY_NAME));
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

        if (Strings.hasText(props.get(DEFAULT_CLIENT_OAUTH2_ACCESS_TOKEN_PROPERTY_NAME))) {
            clientConfig.setOAuth2AccessToken(props.get(DEFAULT_CLIENT_OAUTH2_ACCESS_TOKEN_PROPERTY_NAME));
        }

        if (Strings.hasText(props.get(DEFAULT_CLIENT_KID_PROPERTY_NAME))) {
            clientConfig.setKid(props.get(DEFAULT_CLIENT_KID_PROPERTY_NAME));
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
    public ApiClient build() {

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

        HttpClientBuilder clientBuilder = HttpClients.custom();

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(clientConfig.getConnectionTimeout()))
            .setResponseTimeout(Timeout.ofSeconds(clientConfig.getConnectionTimeout()))
            .setConnectionRequestTimeout(Timeout.ofSeconds(clientConfig.getConnectionTimeout()))
            .build();

        if (clientConfig.getProxy() != null) {
            clientBuilder.useSystemProperties();
            clientBuilder.setProxy(new HttpHost(clientConfig.getProxyHost(), clientConfig.getProxyPort()));
            if (clientConfig.getProxyUsername() != null) {
                final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                AuthScope authScope = new AuthScope(clientConfig.getProxyHost(), clientConfig.getProxyPort());
                UsernamePasswordCredentials usernamePasswordCredentials =
                    new UsernamePasswordCredentials(clientConfig.getProxyUsername(), clientConfig.getProxyPassword().toCharArray());
                credentialsProvider.setCredentials(authScope, usernamePasswordCredentials);
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                clientBuilder.setProxyAuthenticationStrategy(new DefaultAuthenticationStrategy());
            }
        }

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

        CloseableHttpClient httpclient = clientBuilder
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setRetryStrategy(new OktaHttpRequestRetryStrategy(this.clientConfig.getRetryMaxAttempts()))
            .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
            .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
            .disableCookieManagement()
            .build();

        ApiClient apiClient = new ApiClient(httpclient, this.cacheManager, this.clientConfig);
        apiClient.setBasePath(this.clientConfig.getBaseUrl());

        String userAgentValue = ApplicationInfo.get().entrySet().stream()
            .map(entry -> entry.getKey() + "/" + entry.getValue())
            .collect(Collectors.joining(" "));
        apiClient.setUserAgent(userAgentValue);

        addCustomSerializerAndDeserializers(apiClient);

        if (!isOAuth2Flow()) {
            if (this.clientConfig.getClientCredentialsResolver() == null && this.clientCredentials != null) {
                this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientCredentials));
            } else if (this.clientConfig.getClientCredentialsResolver() == null) {
                this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientConfig));
            }

            apiClient.setApiKeyPrefix(AuthenticationScheme.SSWS.name());
            apiClient.setApiKey((String) this.clientConfig.getClientCredentialsResolver().getClientCredentials().getCredentials());
        } else {
            this.clientConfig.setAuthenticationScheme(AuthenticationScheme.OAUTH2_PRIVATE_KEY);

            validateOAuth2ClientConfig(this.clientConfig);

            if (Strings.hasText(this.clientConfig.getOAuth2AccessToken())) {
                log.debug("Will use client provided Access token for OAuth2 authentication (private key, if supplied would be ignored)");
                apiClient.setAccessToken(this.clientConfig.getOAuth2AccessToken());
            } else {
                log.debug("Will retrieve Access Token automatically from Okta for OAuth2 authentication");
                accessTokenRetrieverService = new AccessTokenRetrieverServiceImpl(clientConfig, apiClient);

                OAuth2ClientCredentials oAuth2ClientCredentials =
                    new OAuth2ClientCredentials(accessTokenRetrieverService);

                this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(oAuth2ClientCredentials));
            }
        }

        return apiClient;
    }

    /**
     * @since 1.6.0
     */
    private void validateOAuth2ClientConfig(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration.getClientId(), "clientId cannot be null");
        Assert.isTrue(clientConfiguration.getScopes() != null && !clientConfiguration.getScopes().isEmpty(),
            "At least one scope is required");
        String privateKey = clientConfiguration.getPrivateKey();
        String oAuth2AccessToken = clientConfiguration.getOAuth2AccessToken();
        Assert.isTrue(Objects.nonNull(privateKey) || Objects.nonNull(oAuth2AccessToken),
            "Either Private Key (or) Access Token must be supplied for OAuth2 Authentication mode");

        if (Strings.hasText(privateKey) && !ConfigUtil.hasPrivateKeyContentWrapper(privateKey)) {
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

    private void addCustomSerializerAndDeserializers(ApiClient apiClient) {
        ObjectMapper mapper = apiClient.getObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(UserProfile.class, new UserProfileSerializer());
        module.addDeserializer(UserProfile.class, new UserProfileDeserializer());
        mapper.registerModule(module);
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
        Assert.isTrue(scopes != null && !scopes.isEmpty(), "At least one scope is required");
        this.clientConfig.setScopes(scopes);
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(String privateKey) {
        Assert.notNull(privateKey, "Missing privateKey");
        this.clientConfig.setPrivateKey(privateKey);
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(Path privateKeyPath) {
        Assert.notNull(privateKeyPath, "Missing privateKeyPath");
        this.clientConfig.setPrivateKey(getFileContent(privateKeyPath));
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(InputStream privateKeyStream) {
        Assert.notNull(privateKeyStream, "Missing privateKeyStream");
        this.clientConfig.setPrivateKey(getFileContent(privateKeyStream));
        return this;
    }

    @Override
    public ClientBuilder setPrivateKey(PrivateKey privateKey) {
        Assert.notNull(privateKey, "Missing privateKey");
        String algorithm = privateKey.getAlgorithm();
        if (algorithm.equals("RSA")) {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
            try {
                ASN1Primitive primitive = privateKeyInfo.parsePrivateKey().toASN1Primitive();
                String encodedString = ConfigUtil.RSA_PRIVATE_KEY_HEADER + "\n"
                    + Base64.getEncoder().encodeToString(primitive.getEncoded()) + "\n"
                    + ConfigUtil.RSA_PRIVATE_KEY_FOOTER;
                this.clientConfig.setPrivateKey(encodedString);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not parse private key");
            }
        } else if(algorithm.equals("EC")) {
            String encodedString = ConfigUtil.EC_PRIVATE_KEY_HEADER + "\n"
                + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
                + ConfigUtil.EC_PRIVATE_KEY_FOOTER;
            this.clientConfig.setPrivateKey(encodedString);
        } else {
            throw new IllegalArgumentException("Supplied privateKey is not an RSA or EC key - " + algorithm);
        }
        return this;
    }

    private String getFileContent(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return readFromInputStream(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read from supplied private key file");
        }
    }

    private String getFileContent(Path path) {
        Assert.notNull(path, "The path to the privateKey cannot be null.");
        return getFileContent(path.toFile());
    }

    private String getFileContent(InputStream privateKeyStream) {
        try {
            return readFromInputStream(privateKeyStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read from supplied privateKey input stream");
        }
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream, "InputStream cannot be null.");
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            inputStream, StandardCharsets.UTF_8))) {
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

    @Override
    public ClientBuilder setOAuth2AccessToken(String oAuth2AccessToken) {
        Assert.notNull(oAuth2AccessToken, "oAuth2AccessToken cannot be null.");
        this.clientConfig.setOAuth2AccessToken(oAuth2AccessToken);
        return this;
    }

    @Override
    public ClientBuilder setKid(String kid) {
        Assert.notNull(kid, "kid cannot be null.");
        this.clientConfig.setKid(kid);
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

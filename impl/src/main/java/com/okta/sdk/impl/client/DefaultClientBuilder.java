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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.okta.commons.configcheck.ConfigurationValidator;
import com.okta.commons.http.config.Proxy;
import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Strings;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.error.ErrorHandler;
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.config.EnvironmentVariablesPropertiesSource;
import com.okta.sdk.impl.config.OptionalPropertiesSource;
import com.okta.sdk.impl.config.PropertiesSource;
import com.okta.sdk.impl.config.ResourcePropertiesSource;
import com.okta.sdk.impl.config.SystemPropertiesSource;
import com.okta.sdk.impl.config.YAMLPropertiesSource;
import com.okta.sdk.impl.io.ClasspathResource;
import com.okta.sdk.impl.io.DefaultResourceFactory;
import com.okta.sdk.impl.io.Resource;
import com.okta.sdk.impl.io.ResourceFactory;
import com.okta.sdk.impl.util.ConfigUtil;
import com.okta.sdk.impl.util.DefaultBaseUrlResolver;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.openapitools.client.ApiClient;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String ENVVARS_TOKEN   = "envvars";
    private static final String SYSPROPS_TOKEN  = "sysprops";
    private static final String OKTA_CONFIG_CP  = "com/okta/sdk/config/";
    private static final String OKTA_YAML       = "okta.yaml";
    private static final String OKTA_PROPERTIES = "okta.properties";

    private ClientCredentials clientCredentials;
    private boolean allowNonHttpsForTesting = false;

    private ClientConfiguration clientConfig = new ClientConfiguration();

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

        if (this.clientConfig.getBaseUrlResolver() == null) {
            ConfigurationValidator.validateOrgUrl(this.clientConfig.getBaseUrl(), allowNonHttpsForTesting);
            this.clientConfig.setBaseUrlResolver(new DefaultBaseUrlResolver(this.clientConfig.getBaseUrl()));
        }

        if (this.clientConfig.getClientCredentialsResolver() == null && this.clientCredentials != null) {
            this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientCredentials));
        } else if (this.clientConfig.getClientCredentialsResolver() == null) {
            this.clientConfig.setClientCredentialsResolver(new DefaultClientCredentialsResolver(this.clientConfig));
        }

        ApiClient apiClient = new ApiClient(buildRestTemplate());
        apiClient.setBasePath(this.clientConfig.getBaseUrl());
        apiClient.setApiKey((String) this.clientConfig.getClientCredentialsResolver().getClientCredentials().getCredentials());
        // for now (beta release) we support only SSWS, OAuth2 support to be added soon
        apiClient.setApiKeyPrefix("SSWS");
        return apiClient;
    }

    private static RestTemplate buildRestTemplate() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectMapper mapper = messageConverter.getObjectMapper();
        messageConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.parseMediaType("application/x-pem-file"),
            MediaType.parseMediaType("application/x-x509-ca-cert"),
            MediaType.parseMediaType("application/pkix-cert")));
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(messageConverter);

        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.setErrorHandler(new ErrorHandler());

        // allows us to read the response more than once - necessary for debugging
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        return restTemplate;
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
        try (InputStream inputStream = new FileInputStream(file)) {
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

    @Override
    public ClientBuilder setKid(String kid) {
        Assert.notNull(kid, "kid cannot be null.");
        this.clientConfig.setKid(kid);
        return this;
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

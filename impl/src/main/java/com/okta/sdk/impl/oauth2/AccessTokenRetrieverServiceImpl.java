/*
 * Copyright 2020-Present Okta, Inc.
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
package com.okta.sdk.impl.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.commons.http.MediaType;
import com.okta.commons.http.authc.DisabledAuthenticator;
import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Strings;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.util.ConfigUtil;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.model.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementation of {@link AccessTokenRetrieverService} interface.
 * This has logic to fetch OAuth2 access token from the Authorization server endpoint.
 * @since 1.6.0
 */
public class AccessTokenRetrieverServiceImpl implements AccessTokenRetrieverService {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenRetrieverServiceImpl.class);

    private static final String TOKEN_URI  = "/oauth2/v1/token";

    private final ClientConfiguration tokenClientConfiguration;
    private final ApiClient apiClient;

    public AccessTokenRetrieverServiceImpl(ClientConfiguration apiClientConfiguration, ApiClient apiClient) {
        Assert.notNull(apiClientConfiguration, "apiClientConfiguration must not be null.");
        Assert.notNull(apiClient, "apiClient must not be null.");
        this.apiClient = apiClient;
        this.tokenClientConfiguration = constructTokenClientConfig(apiClientConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2AccessToken getOAuth2AccessToken() throws IOException, InvalidKeyException, OAuth2TokenRetrieverException {
        log.debug("Attempting to get OAuth2 access token for client id {} from {}",
            tokenClientConfiguration.getClientId(), tokenClientConfiguration.getBaseUrl() + TOKEN_URI);

        String signedJwt = createSignedJWT();
        String scope = String.join(" ", tokenClientConfiguration.getScopes());

        try {
            List<Pair> queryParams = new LinkedList<>();
            queryParams.add(new Pair("grant_type", "client_credentials"));
            queryParams.add(new Pair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
            queryParams.add(new Pair("client_assertion", signedJwt));
            queryParams.add(new Pair("scope", URLEncoder.encode(scope, StandardCharsets.UTF_8.toString())));

            OAuth2AccessToken oAuth2AccessToken = apiClient.invokeAPI(
                TOKEN_URI,
                HttpMethod.POST.name(),
                queryParams,
                new LinkedList<>(),
                null,
                null,
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                new String[] { "oauth2" },
                new TypeReference<OAuth2AccessToken>() {});

            log.debug("Got OAuth2 access token for client id {} from {}",
                tokenClientConfiguration.getClientId(), tokenClientConfiguration.getBaseUrl() + TOKEN_URI);

            apiClient.setAccessToken(oAuth2AccessToken.getAccessToken());

            return oAuth2AccessToken;
        } catch (ApiException e) {
            throw new OAuth2HttpException(e.getMessage(), e, e.getCode() == 401);
        } catch (Exception e) {
            throw new OAuth2TokenRetrieverException("Exception while trying to get " +
                "OAuth2 access token for client id " + tokenClientConfiguration.getClientId(), e);
        }
    }

    /**
     * Create signed JWT string with the supplied token client configuration details.
     *
     * Expiration value should be not more than one hour in the future.
     * We use 50 minutes in order to have a 10 minutes leeway in case of clock skew.
     *
     * @return signed JWT string
     * @throws InvalidKeyException if the supplied key is invalid
     * @throws IOException if the key could not be read
     */
    String createSignedJWT() throws InvalidKeyException, IOException {
        String clientId = tokenClientConfiguration.getClientId();
        PrivateKey privateKey = parsePrivateKey(getPemReader());
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
            .setAudience(tokenClientConfiguration.getBaseUrl() + TOKEN_URI)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(50, ChronoUnit.MINUTES)))             // see Javadoc
            .setIssuer(clientId)
            .setSubject(clientId)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(privateKey);

        if (Strings.hasText(tokenClientConfiguration.getKid())) {
            builder.setHeaderParam("kid", tokenClientConfiguration.getKid());
        }

        return builder.compact();
    }

    /**
     * Parse private key from the supplied configuration.
     *
     * @param pemReader a {@link Reader} that has access to a full PEM resource
     * @return {@link PrivateKey}
     * @throws IOException if the private key could not be read
     * @throws InvalidKeyException if the supplied key is invalid
     */
    PrivateKey parsePrivateKey(Reader pemReader) throws IOException, InvalidKeyException {

        PrivateKey privateKey = getPrivateKeyFromPEM(pemReader);
        String algorithm = privateKey.getAlgorithm();

        if (!algorithm.equals("RSA") &&
            !algorithm.equals("EC")) {
            throw new InvalidKeyException("Supplied privateKey is not an RSA or EC key - " + algorithm);
        }

        return privateKey;
    }

    private Reader getPemReader() throws IOException {
        String privateKey = tokenClientConfiguration.getPrivateKey();
        if (ConfigUtil.hasPrivateKeyContentWrapper(privateKey)) {
            return new StringReader(privateKey);
        } else {
            return Files.newBufferedReader(Paths.get(privateKey), Charset.defaultCharset());
        }
    }

    /**
     * Get Private key from input PEM file.
     *
     * @param reader the reader instance
     * @return {@link PrivateKey} private key instance
     * @throws IOException if the parser could not read the reader object
     */
    PrivateKey getPrivateKeyFromPEM(Reader reader) throws IOException {
        PrivateKey privateKey;

        try (PEMParser pemParser = new PEMParser(reader)) {
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            Object pemContent = pemParser.readObject();

            if (pemContent == null) {
                throw new IllegalArgumentException("Invalid Private Key PEM file");
            }

            if (pemContent instanceof PEMKeyPair) {
                PEMKeyPair pemKeyPair = (PEMKeyPair) pemContent;
                KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);
                privateKey = keyPair.getPrivate();
            } else if (pemContent instanceof PrivateKeyInfo) {
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemContent;
                privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
            } else {
                throw new IllegalArgumentException("Unsupported Private Key format '" +
                    pemContent.getClass().getSimpleName() + '"');
            }
        }

        return privateKey;
    }

    /**
     * Create token client config from the supplied API client config.
     *
     * Token client needs to retry http 401 errors only once which is not the case with the API client.
     * We therefore effect this token client specific config by setting 'retryMaxElapsed'
     * & 'retryMaxAttempts' fields.
     *
     * @param apiClientConfiguration supplied in the client configuration.
     * @return ClientConfiguration to be used by token retrieval client.
     */
    ClientConfiguration constructTokenClientConfig(ClientConfiguration apiClientConfiguration) {
        ClientConfiguration tokenClientConfiguration = new ClientConfiguration();

        tokenClientConfiguration.setClientCredentialsResolver(
            new DefaultClientCredentialsResolver(Optional::empty));

        tokenClientConfiguration.setRequestAuthenticator(new DisabledAuthenticator());

        // TODO: set this to false explicitly when caching is implemented in OASv3 SDK
        //tokenClientConfiguration.setCacheManagerEnabled(false);

        if (apiClientConfiguration.getBaseUrlResolver() != null)
            tokenClientConfiguration.setBaseUrlResolver(apiClientConfiguration.getBaseUrlResolver());

        if (apiClientConfiguration.getProxy() != null)
            tokenClientConfiguration.setProxy(apiClientConfiguration.getProxy());

        tokenClientConfiguration.setBaseUrl(apiClientConfiguration.getBaseUrl());
        tokenClientConfiguration.setAuthenticationScheme(AuthenticationScheme.OAUTH2_PRIVATE_KEY);
        tokenClientConfiguration.setAuthorizationMode(AuthorizationMode.get(tokenClientConfiguration.getAuthenticationScheme()));
        tokenClientConfiguration.setClientId(apiClientConfiguration.getClientId());
        tokenClientConfiguration.setScopes(apiClientConfiguration.getScopes());
        tokenClientConfiguration.setPrivateKey(apiClientConfiguration.getPrivateKey());
        tokenClientConfiguration.setKid(apiClientConfiguration.getKid());

        // setting this to '0' will disable this check and only 'retryMaxAttempts' will be effective
        tokenClientConfiguration.setRetryMaxElapsed(0);
        // retry only once for token requests (http 401 errors)
        tokenClientConfiguration.setRetryMaxAttempts(1);

        return tokenClientConfiguration;
    }

}

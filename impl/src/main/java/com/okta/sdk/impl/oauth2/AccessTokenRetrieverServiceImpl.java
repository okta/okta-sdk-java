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
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.HttpMethod;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.SecureRequest;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.VerifySecureDigestRequest;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
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

    private static final KeyPair DUMMY_KEY_PAIR = Jwts.SIG.RS256.keyPair().build();

    /**
     * Custom SecureDigestAlgorithm that delegates signature to the jwtSigner in tokenClientConfiguration
     */
    private class CustomJwtSigningAlgorithm implements SecureDigestAlgorithm<PrivateKey, Key> {
        @Override
        public byte[] digest(SecureRequest<InputStream, PrivateKey> request) throws SecurityException {
            try {
                byte[] bytes = readAllBytes(request.getPayload());
                return tokenClientConfiguration.getJwtSigner().apply(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //to replace with InputStream.readAllBytes after migrating to Java 9+
        private byte[] readAllBytes(InputStream payload) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = payload.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }

        @Override
        public boolean verify(VerifySecureDigestRequest<Key> request) throws SecurityException {
            //no need to verify JWTs
            throw new UnsupportedOperationException();
        }

        @Override
        public String getId() {
            return tokenClientConfiguration.getJwtSigningAlgorithm();
        }
    }

    private final ClientConfiguration tokenClientConfiguration;
    private final ApiClient apiClient;
    private final CustomJwtSigningAlgorithm customJwtSigningAlgorithm = new CustomJwtSigningAlgorithm();

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
            Map<String, Object> formParameters = new HashMap<>();
            formParameters.put("grant_type", "client_credentials");
            formParameters.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
            formParameters.put("client_assertion", signedJwt);
            formParameters.put("scope", scope);

            OAuth2AccessToken oAuth2AccessToken = apiClient.invokeAPI(
                TOKEN_URI,
                HttpMethod.POST.name(),
                new LinkedList<>(),
                new LinkedList<>(),
                null,
                null,
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                formParameters,
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
     * Expiration value should be not more than one hour in the future.
     * We use 50 minutes in order to have a 10 minutes leeway in case of clock skew.
     *
     * @return signed JWT string
     * @throws InvalidKeyException if the supplied key is invalid
     * @throws IOException if the key could not be read
     */
    String createSignedJWT() throws InvalidKeyException, IOException {
        String clientId = tokenClientConfiguration.getClientId();
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
            .audience().add(tokenClientConfiguration.getBaseUrl() + TOKEN_URI).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(50, ChronoUnit.MINUTES)))             // see Javadoc
            .issuer(clientId)
            .subject(clientId)
            .claim("jti", UUID.randomUUID().toString());

        if (tokenClientConfiguration.hasCustomJwtSigner()) {
            //JwtBuilder requires a key to be passed, even if it's actually not used by the algorithm
            builder.signWith(DUMMY_KEY_PAIR.getPrivate(), customJwtSigningAlgorithm);
        } else {
            builder = builder.signWith(parsePrivateKey(getPemReader()));
        }

        if (Strings.hasText(tokenClientConfiguration.getKid())) {
            builder.header().add("kid", tokenClientConfiguration.getKid());
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
        tokenClientConfiguration.setJwtSigner(apiClientConfiguration.getJwtSigner(), apiClientConfiguration.getJwtSigningAlgorithm());
        tokenClientConfiguration.setKid(apiClientConfiguration.getKid());

        // setting this to '0' will disable this check and only 'retryMaxAttempts' will be effective
        tokenClientConfiguration.setRetryMaxElapsed(0);
        // retry only once for token requests (http 401 errors)
        tokenClientConfiguration.setRetryMaxAttempts(1);

        return tokenClientConfiguration;
    }

}

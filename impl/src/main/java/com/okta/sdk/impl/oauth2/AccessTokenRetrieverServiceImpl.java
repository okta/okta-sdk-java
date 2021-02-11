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

import com.okta.commons.http.MediaType;
import com.okta.commons.http.authc.DisabledAuthenticator;
import com.okta.commons.lang.Assert;
import com.okta.sdk.client.AuthenticationScheme;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.impl.api.DefaultClientCredentialsResolver;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.error.DefaultError;
import com.okta.sdk.impl.util.ConfigUtil;
import com.okta.sdk.resource.ExtensibleResource;
import com.okta.sdk.resource.ResourceException;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link AccessTokenRetrieverService} interface.
 *
 * This has logic to fetch OAuth2 access token from the Authorization server endpoint.
 *
 * @since 1.6.0
 */
public class AccessTokenRetrieverServiceImpl implements AccessTokenRetrieverService {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenRetrieverServiceImpl.class);

    private static final String TOKEN_URI  = "/oauth2/v1/token";

    private final ClientConfiguration tokenClientConfiguration;
    private final OAuth2TokenClient tokenClient;

    public AccessTokenRetrieverServiceImpl(ClientConfiguration apiClientConfiguration) {
        Assert.notNull(apiClientConfiguration, "apiClientConfiguration must not be null.");
        ClientConfiguration tokenClientConfig = constructTokenClientConfig(apiClientConfiguration);
        this.tokenClient = new OAuth2TokenClient(tokenClientConfig);
        this.tokenClientConfiguration = tokenClientConfig;
    }

    public AccessTokenRetrieverServiceImpl(ClientConfiguration apiClientConfiguration, OAuth2TokenClient tokenClient) {
        Assert.notNull(apiClientConfiguration, "apiClientConfiguration must not be null.");
        Assert.notNull(tokenClient, "tokenClient must not be null.");
        this.tokenClient = tokenClient;
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
            ExtensibleResource accessTokenResponse = tokenClient.http()
                .addHeaderParameter("Accept", MediaType.APPLICATION_JSON_VALUE)
                .addHeaderParameter("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                .addQueryParameter("client_assertion", signedJwt)
                .addQueryParameter("scope", scope)
                .post(TOKEN_URI, ExtensibleResource.class);

            OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken();
            oAuth2AccessToken.setTokenType(accessTokenResponse.getString(OAuth2AccessToken.TOKEN_TYPE_KEY));
            oAuth2AccessToken.setExpiresIn(accessTokenResponse.getInteger(OAuth2AccessToken.EXPIRES_IN_KEY));
            oAuth2AccessToken.setAccessToken(accessTokenResponse.getString(OAuth2AccessToken.ACCESS_TOKEN_KEY));
            oAuth2AccessToken.setScope(accessTokenResponse.getString(OAuth2AccessToken.SCOPE_KEY));

            log.debug("Got OAuth2 access token for client id {} from {}",
                tokenClientConfiguration.getClientId(), tokenClientConfiguration.getBaseUrl() + TOKEN_URI);

            return oAuth2AccessToken;
        } catch (ResourceException e) {
            //TODO: clean up the ugly casting and refactor code around it.
            DefaultError defaultError = (DefaultError) e.getError();
            String errorMessage = defaultError.getString(OAuth2AccessToken.ERROR_KEY);
            String errorDescription = defaultError.getString(OAuth2AccessToken.ERROR_DESCRIPTION);
            defaultError.setMessage(errorMessage + " - " + errorDescription);
            throw new OAuth2HttpException(defaultError, e, e.getStatus() == 401);
        } catch (Exception e) {
            throw new OAuth2TokenRetrieverException("Exception while trying to get " +
                "OAuth2 access token for client id " + tokenClientConfiguration.getClientId(), e);
        }
    }

    /**
     * Create signed JWT string with the supplied token client configuration details.
     *
     * @return signed JWT string
     * @throws InvalidKeyException
     * @throws IOException
     */
    String createSignedJWT() throws InvalidKeyException, IOException {
        String clientId = tokenClientConfiguration.getClientId();
        PrivateKey privateKey = parsePrivateKey(getPemReader());
        Instant now = Instant.now();

        return Jwts.builder()
            .setAudience(tokenClientConfiguration.getBaseUrl() + TOKEN_URI)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(50, ChronoUnit.MINUTES)))
            .setIssuer(clientId)
            .setSubject(clientId)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(privateKey)
            .compact();
    }

    /**
     * Parse private key from the supplied configuration.
     *
     * @param pemReader a {@link Reader} that has access to a full PEM resource
     * @return {@link PrivateKey}
     * @throws IOException
     * @throws InvalidKeyException
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
     * @param reader
     * @return {@link PrivateKey}
     * @throws IOException
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
            new DefaultClientCredentialsResolver(() -> Optional.empty()));

        tokenClientConfiguration.setRequestAuthenticator(new DisabledAuthenticator());

        tokenClientConfiguration.setCacheManagerEnabled(false);

        if (apiClientConfiguration.getBaseUrlResolver() != null)
            tokenClientConfiguration.setBaseUrlResolver(apiClientConfiguration.getBaseUrlResolver());

        if (apiClientConfiguration.getProxy() != null)
            tokenClientConfiguration.setProxy(apiClientConfiguration.getProxy());

        tokenClientConfiguration.setAuthenticationScheme(AuthenticationScheme.NONE);
        tokenClientConfiguration.setAuthorizationMode(AuthorizationMode.get(tokenClientConfiguration.getAuthenticationScheme()));
        tokenClientConfiguration.setClientId(apiClientConfiguration.getClientId());
        tokenClientConfiguration.setScopes(apiClientConfiguration.getScopes());
        tokenClientConfiguration.setPrivateKey(apiClientConfiguration.getPrivateKey());

        // setting this to '0' will disable this check and only 'retryMaxAttempts' will be effective
        tokenClientConfiguration.setRetryMaxElapsed(0);
        // retry only once for token requests (http 401 errors)
        tokenClientConfiguration.setRetryMaxAttempts(1);

        return tokenClientConfiguration;
    }

}

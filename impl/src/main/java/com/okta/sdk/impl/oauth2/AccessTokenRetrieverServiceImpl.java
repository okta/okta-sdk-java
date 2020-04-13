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
import com.okta.commons.lang.Assert;
import com.okta.sdk.cache.Caches;
import com.okta.sdk.error.authc.InvalidAuthenticationException;
import com.okta.sdk.impl.client.BaseClient;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.impl.error.DefaultError;
import com.okta.sdk.resource.ExtensibleResource;
import com.okta.sdk.resource.ResourceException;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.ACCESS_TOKEN_KEY;
import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.ERROR_DESCRIPTION;
import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.ERROR_KEY;
import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.EXPIRES_IN_KEY;
import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.SCOPE_KEY;
import static com.okta.sdk.impl.oauth2.OAuth2AccessToken.TOKEN_TYPE_KEY;

public class AccessTokenRetrieverServiceImpl implements AccessTokenRetrieverService {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenRetrieverServiceImpl.class);

    private ClientConfiguration clientConfiguration;

    public AccessTokenRetrieverServiceImpl(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");
        this.clientConfiguration = clientConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2AccessToken getOAuth2AccessToken() throws IOException, InvalidKeyException, OAuth2TokenRetrieverException {
        log.info("Getting OAuth2 access token for client id {} from {}",
            clientConfiguration.getClientId(), clientConfiguration.getBaseUrl());

        String signedJwt = createSignedJWT();
        String scope = String.join(" ", clientConfiguration.getScopes());

        BaseClient tokenClient = new BaseClient(clientConfiguration, Caches.newDisabledCacheManager()) {};

        try {
            ExtensibleResource accessTokenResponse = tokenClient.http()
                .addHeaderParameter("Accept", MediaType.APPLICATION_JSON_VALUE)
                .addHeaderParameter("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                .addQueryParameter("client_assertion", signedJwt)
                .addQueryParameter("scope", scope)
                .post(clientConfiguration.getBaseUrl() + "/oauth2/v1/token", ExtensibleResource.class);

            OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken();
            oAuth2AccessToken.setTokenType(accessTokenResponse.getString(TOKEN_TYPE_KEY));
            oAuth2AccessToken.setExpiresIn(accessTokenResponse.getInteger(EXPIRES_IN_KEY));
            oAuth2AccessToken.setAccessToken(accessTokenResponse.getString(ACCESS_TOKEN_KEY));
            oAuth2AccessToken.setScope(accessTokenResponse.getString(SCOPE_KEY));

            return oAuth2AccessToken;

        } catch (ResourceException e) {
            //TODO: clean up the ugly casting and refactor code around it.
            DefaultError error = (DefaultError) e.getError();
            String errorMessage = error.getString(ERROR_KEY);
            String errorDescription = error.getString(ERROR_DESCRIPTION);
            error.setMessage(errorMessage + " - " + errorDescription);
            throw new InvalidAuthenticationException(error);
        } catch (Exception e) {
            throw new OAuth2TokenRetrieverException("Unexpected error while trying to retrieve OAuth2 access token", e);
        }
    }

    /**
     * Create signed JWT string with the supplied {@link ClientConfiguration} details.
     *
     * @return signed JWT string
     * @throws InvalidKeyException
     * @throws IOException
     */
    String createSignedJWT() throws InvalidKeyException, IOException {
        String clientId = clientConfiguration.getClientId();
        PrivateKey privateKey = parsePrivateKey(clientConfiguration.getPrivateKey());
        Instant now = Instant.now();

        String jwt = Jwts.builder()
            .setAudience(clientConfiguration.getBaseUrl() + "/oauth2/v1/token")
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
            .setIssuer(clientId)
            .setSubject(clientId)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(privateKey)
            .compact();

        return jwt;
    }

    /**
     * Parse private key from the supplied path.
     *
     * @param privateKeyFilePath
     * @return PrivateKey
     * @throws IOException
     * @throws InvalidKeyException
     */
    PrivateKey parsePrivateKey(String privateKeyFilePath) throws IOException, InvalidKeyException {
        Assert.notNull(privateKeyFilePath, "privateKeyFilePath may not be null");

        File privateKeyFile = new File(privateKeyFilePath);
        if (!privateKeyFile.exists()) {
            throw new IllegalArgumentException("privateKeyFile " + privateKeyFile + " does not exist");
        }

        Reader reader = new InputStreamReader(new FileInputStream(privateKeyFilePath), "UTF-8");

        PrivateKey privateKey = getPrivateKeyFromPEM(reader);
        String algorithm = privateKey.getAlgorithm();

        if (!algorithm.equals("RSA") &&
            !algorithm.equals("EC")) {
            throw new InvalidKeyException("Supplied privateKey is not an RSA or EC key - " + algorithm);
        }

        return privateKey;
    }

    /**
     * Get Private key from input PEM file.
     *
     * @param reader
     * @return {@link PrivateKey} object
     * @throws {@link IOException}
     */
    PrivateKey getPrivateKeyFromPEM(Reader reader) throws IOException {
        PrivateKey privateKey;

        try (PEMParser pemParser = new PEMParser(reader)) {
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            Object pemContent = pemParser.readObject();

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

}

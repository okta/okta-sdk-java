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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.http.DefaultRequest;
import com.okta.commons.http.HttpException;
import com.okta.commons.http.HttpHeaders;
import com.okta.commons.http.HttpMethod;
import com.okta.commons.http.MediaType;
import com.okta.commons.http.QueryString;
import com.okta.commons.http.Request;
import com.okta.commons.http.RequestExecutor;
import com.okta.commons.http.Response;
import com.okta.commons.http.authc.RequestAuthenticationException;
import com.okta.commons.http.okhttp.OkHttpRequestExecutorFactory;
import com.okta.commons.lang.Assert;
import com.okta.sdk.impl.config.ClientConfiguration;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class AccessTokenRetrieverServiceImpl implements AccessTokenRetrieverService {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenRetrieverServiceImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ClientConfiguration clientConfiguration;

    public AccessTokenRetrieverServiceImpl(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");
        this.clientConfiguration = clientConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2AccessToken getOAuth2AccessToken() throws IOException, InvalidKeyException, RequestAuthenticationException {
        log.debug("Getting OAuth2 access token for client id {} from {}",
            clientConfiguration.getClientId(), clientConfiguration.getBaseUrl());

        String signedJwt = createSignedJWT(clientConfiguration);
        String scope = String.join(" ", clientConfiguration.getScopes());

        QueryString queryString = new QueryString();
        queryString.put("grant_type", "client_credentials");
        queryString.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        queryString.put("client_assertion", signedJwt);
        queryString.put("scope", scope);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        Request accessTokenRequest = new DefaultRequest(
            HttpMethod.POST,
            clientConfiguration.getBaseUrl() + "/oauth2/v1/token",
            queryString,
            httpHeaders,
            new ByteArrayInputStream("".getBytes("UTF-8")),
            0);

        RequestExecutor requestExecutor = new OkHttpRequestExecutorFactory().create(clientConfiguration);

        Response accessTokenResponse = requestExecutor.executeRequest(accessTokenRequest);

        if (accessTokenResponse != null && accessTokenResponse.getBody() != null) {
            Reader reader = new InputStreamReader(accessTokenResponse.getBody(), StandardCharsets.UTF_8);

            if (accessTokenResponse.getHttpStatus() == HttpURLConnection.HTTP_OK) {
                log.debug("OAuth2 access token request was successful");
                return objectMapper.readValue(reader, OAuth2AccessToken.class);
            } else {
                log.debug("OAuth2 access token request failed with HTTP {} error", accessTokenResponse.getHttpStatus());
                OAuth2ErrorResponse errorResponse = objectMapper.readValue(reader, OAuth2ErrorResponse.class);
                String errorDetails = errorResponse.getError() + ":" + errorResponse.getErrorDescription();
                throw new RequestAuthenticationException(errorDetails);
            }
        } else {
            throw new HttpException("Could not retrieve access token from Authorization Server");
        }
    }

    /**
     * Create signed JWT string with the supplied {@link ClientConfiguration} details.
     *
     * @param clientConfiguration
     * @return signed JWT string
     * @throws InvalidKeyException
     * @throws IOException
     */
    private String createSignedJWT(ClientConfiguration clientConfiguration)
        throws InvalidKeyException, IOException {
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
    private PrivateKey parsePrivateKey(String privateKeyFilePath) throws IOException, InvalidKeyException {
        File privateKeyFile = new File(privateKeyFilePath);
        if (!privateKeyFile.exists()) {
            throw new FileNotFoundException("privateKeyFile " + privateKeyFile + " does not exist");
        }

        Reader reader = new InputStreamReader(new FileInputStream(clientConfiguration.getPrivateKey()), "UTF-8");

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
    private PrivateKey getPrivateKeyFromPEM(Reader reader) throws IOException {
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

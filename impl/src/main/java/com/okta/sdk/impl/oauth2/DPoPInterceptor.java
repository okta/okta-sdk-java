/*
 * Copyright 2024-Present Okta, Inc.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PrivateJwk;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.okta.sdk.impl.oauth2.AccessTokenRetrieverServiceImpl.TOKEN_URI;

/**
 * Interceptor that handle DPoP handshake during auth and adds DPoP header to regular requests.
 * It is always enabled, but is only active when a DPoP error is received during auth.
 *
 * @see <a href="https://developer.okta.com/docs/guides/dpop/oktaresourceserver/main/">documentation</a>
 */
public class DPoPInterceptor implements ExecChainHandler {

    private static final Logger log = LoggerFactory.getLogger(DPoPInterceptor.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DPOP_HEADER = "DPoP";
    //nonce is valid for 24 hours, but can only refresh it when doing a token request => start refreshing after 22 hours
    private static final int NONCE_VALID_SECONDS = 60 * 60 * 22;
    //MessageDigest is not thread-safe, need one per thread
    private static final ThreadLocal<MessageDigest> SHA256 = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    //if null, means dpop is not enabled yet
    private PrivateJwk<PrivateKey, PublicKey, ?> jwk;
    private String nonce;
    private Instant nonceValidUntil;

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain execChain)
        throws IOException, HttpException {
        boolean tokenRequest = request.getRequestUri().equals(TOKEN_URI);
        if (tokenRequest && nonce != null && nonceValidUntil.isBefore(Instant.now())) {
            log.debug("DPoP nonce expired, will refresh it");
            nonce = null;
            nonceValidUntil = null;
        }
        if (jwk != null) {
            processRequest(request, tokenRequest);
        }
        ClassicHttpResponse response = execChain.proceed(request, scope);
        if (tokenRequest) {
            if (response.getCode() == 200 && nonce != null) {
                log.info("DPoP handshake successful");
            }
            if (response.getCode() == 400) {
                JsonNode errorBody = OBJECT_MAPPER.readTree(response.getEntity().getContent());
                Header nonceHeader = response.getFirstHeader("dpop-nonce");
                DPopHandshakeState handshakeState = handleHandshakeResponse(errorBody.get("error"), nonceHeader);
                throw new DPoPHandshakeException(handshakeState, OBJECT_MAPPER.writeValueAsString(errorBody));
            }
        }
        return response;
    }

    private void processRequest(HttpRequest request, boolean tokenRequest) {
        JwtBuilder builder = Jwts.builder()
            .header()
            .type("dpop+jwt")
            .jwk(jwk.toPublicJwk())
            .and()
            .claim("htm", request.getMethod())
            .claim("htu", getUriWithoutQueryString(request))
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(new Date());
        Header authorization = request.getFirstHeader("Authorization");
        if (authorization != null) {
            //already authenticated, need to replace Authorization header prefix and set ath claim
            //the DPoP prefix might already be set if the request is retried
            String token = StringUtils.substringAfter(authorization.getValue(), " ");
            request.setHeader("Authorization", DPOP_HEADER + " " + token);
            byte[] ath = SHA256.get().digest(token.getBytes(StandardCharsets.US_ASCII));
            builder.claim("ath", Encoders.BASE64URL.encode(ath));
        } else if (tokenRequest && nonce != null) {
            //still in handshake, need to set nonce
            builder.claim("nonce", nonce);
        }
        request.addHeader(DPOP_HEADER, builder.signWith(jwk.toKeyPair().getPrivate()).compact());
    }

    private String getUriWithoutQueryString(HttpRequest request) {
        try {
            String urlWithoutQueryString = StringUtils.substringBefore(request.getUri().toString(), "?");
            return URLDecoder.decode(urlWithoutQueryString, StandardCharsets.UTF_8.name())
                .replace("%", "%25") //must be replaced first
                .replace(" ", "%20")
                .replace("\"", "%22")
                .replace("#", "%23");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DPopHandshakeState handleHandshakeResponse(JsonNode errorField, Header nonceHeader) {
        if (errorField != null && errorField.isTextual()) {
            switch (errorField.textValue()) {
                case "invalid_dpop_proof": {
                    if (jwk != null) {
                        return DPopHandshakeState.REPEATED_INVALID_DPOP_PROOF;
                    }
                    log.info("DPoP detected, beginning handshake");
                    this.jwk = Jwks.builder().keyPair(Jwts.SIG.ES256.keyPair().build()).build();
                    return DPopHandshakeState.FIRST_INVALID_DPOP_PROOF;
                }
                case "use_dpop_nonce": {
                    if (nonce != null) {
                        return DPopHandshakeState.REPEATED_USE_DPOP_NONCE;
                    }
                    if (nonceHeader == null) {
                        return DPopHandshakeState.MISSING_DPOP_NONCE_HEADER;
                    }
                    log.info("DPoP nonce obtained, finalizing handshake");
                    this.nonce = nonceHeader.getValue();
                    this.nonceValidUntil = Instant.now().plusSeconds(NONCE_VALID_SECONDS);
                    return DPopHandshakeState.FIRST_USE_DPOP_NONCE;
                }
                default:
                    return DPopHandshakeState.UNEXPECTED_STATE;
            }
        }
        return DPopHandshakeState.UNEXPECTED_STATE;
    }

}

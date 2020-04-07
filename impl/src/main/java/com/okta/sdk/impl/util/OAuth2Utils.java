package com.okta.sdk.impl.util;

import com.okta.sdk.impl.config.ClientConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class OAuth2Utils {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Utils.class);

    /**
     * Create signed JWT string with the supplied {@link ClientConfiguration} details.
     *
     * @param {@link ClientConfiguration} object
     * @return signed JWT string
     * @throws {@link InvalidKeySpecException}
     * @throws {@link NoSuchAlgorithmException}
     * @throws {@link IOException}
     */
    public static String createSignedJWT(ClientConfiguration clientConfiguration)
        throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String clientId = clientConfiguration.getClientId();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(clientConfiguration.getAlgorithm());
        PrivateKey privateKey = createPrivateKeyFromPemFilePath(clientConfiguration.getKeyFilePath());
        Instant now = Instant.now();

        String jwt = Jwts.builder()
            .setAudience(clientConfiguration.getBaseUrl() + "/oauth2/v1/token")
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
            .setIssuer(clientId)
            .setSubject(clientId)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(signatureAlgorithm, privateKey)
            .compact();

        return jwt;
    }

    /**
     * Create {@link PrivateKey} instance from a given pem key file path.
     *
     * @param pemFilePath string
     * @return {@link PrivateKey} instance
     * @throws {@link IOException}
     * @throws {@link InvalidKeySpecException} if private key is not RSA or EC based.
     * @throws {@link NoSuchAlgorithmException} if an unsupported algorithm is detected.
     */
    static PrivateKey createPrivateKeyFromPemFilePath(final String pemFilePath)
        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        log.debug("Creating private key from pem file {}", pemFilePath);
        File privateKeyFile = new File(pemFilePath);
        if (!privateKeyFile.exists()) {
            throw new FileNotFoundException(pemFilePath);
        }

        PemReader pemReader = new PemReader(new FileReader(pemFilePath));
        PemObject pemObject = pemReader.readPemObject();
        byte[] pemContent = pemObject.getContent();
        pemReader.close();
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);

        try {
            return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
        } catch (InvalidKeySpecException ex) {
            log.warn("Supplied private key is not RSA based. Will check if it is EC based...");
            try {
                return KeyFactory.getInstance("EC").generatePrivate(encodedKeySpec);
            } catch (InvalidKeySpecException e) {
                throw new InvalidKeySpecException("Supplied private key must be either RSA or EC based", e);
            }
        }
    }
}

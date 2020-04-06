package com.okta.sdk.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.http.HttpException;
import com.okta.commons.lang.Strings;
import com.okta.sdk.impl.config.ClientConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class OAuth2Utils {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Utils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static KeyFactory getKeyFactoryInstance() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }

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
     * Obtain OAuth2 access token from Authorization Server endpoint.
     *
     * @param {@link ClientConfiguration} object
     * @return signed JWT string
     * @throws {@link IOException}
     * @throws {@link InvalidKeySpecException}
     * @throws {@link NoSuchAlgorithmException}
     */
    public static String getOAuth2AccessToken(ClientConfiguration clientConfiguration)
        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String signedJwt = createSignedJWT(clientConfiguration);
        String scopes = String.join(" ", clientConfiguration.getScopes());

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "");

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request accessTokenRequest = new Request.Builder()
            .url("https://java-sdk.oktapreview.com/oauth2/v1/token?grant_type=client_credentials" +
                "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer" +
                "&client_assertion=" + signedJwt + "&scope=" + scopes)
            .method("POST", body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();

        okhttp3.Response accessTokenResponse = client.newCall(accessTokenRequest).execute();

        if (accessTokenResponse != null && accessTokenResponse.body() != null) {
            Reader reader = new InputStreamReader(accessTokenResponse.body().byteStream(), StandardCharsets.UTF_8);
            Map<String, String> responseMap = objectMapper.readValue(reader, Map.class);

            if (accessTokenResponse.code() == HttpURLConnection.HTTP_OK) {
                return responseMap.get("access_token");
            }
            else {
                String error = responseMap.get("error");
                String errorDescription = responseMap.get("error_description");
                if (!Strings.isEmpty(error) ||
                    !Strings.isEmpty(errorDescription)) {
                    throw new HttpException("Received HTTP " + accessTokenResponse.code() +
                        " response from Authorization Server. error - " + error +
                        ", error_description - " + errorDescription);
                }
                else {
                    throw new HttpException("Received HTTP " + accessTokenResponse.code() +
                        " response from Authorization Server");
                }
            }
        }
        else {
            throw new HttpException("Could not retrieve access token from Authorization Server");
        }
    }

    /**
     * Create {@link PrivateKey} instance from a given pem key file path.
     *
     * @param pemFilePath string
     * @return {@link PrivateKey} instance
     * @throws {@link IOException}
     * @throws {@link InvalidKeySpecException}
     * @throws {@link NoSuchAlgorithmException}
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
        KeyFactory keyFactory = getKeyFactoryInstance();
        return keyFactory.generatePrivate(encodedKeySpec);
    }

}

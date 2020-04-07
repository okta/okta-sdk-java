package com.okta.sdk.impl.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.http.HttpException;
import com.okta.commons.http.HttpMethod;
import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Strings;
import com.okta.sdk.impl.config.ClientConfiguration;
import com.okta.sdk.oauth2.ErrorResponse;
import com.okta.sdk.oauth2.OAuth2AccessToken;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.okta.sdk.impl.util.OAuth2Utils.createSignedJWT;

public class AccessTokenRetrieverServiceImpl implements AccessTokenRetrieverService {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenRetrieverServiceImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private ClientConfiguration clientConfiguration;

    public AccessTokenRetrieverServiceImpl(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2AccessToken getOAuth2AccessToken()
        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String signedJwt = createSignedJWT(clientConfiguration);
        String scope = String.join(" ", clientConfiguration.getScopes());

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "");

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request accessTokenRequest = new Request.Builder()
            .url(clientConfiguration.getBaseUrl() +
                "/oauth2/v1/token" +
                "?grant_type=client_credentials" +
                "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer" +
                "&client_assertion=" + signedJwt +
                "&scope=" + scope)
            .method(HttpMethod.POST.name(), body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();

        Response accessTokenResponse = client.newCall(accessTokenRequest).execute();

        if (accessTokenResponse != null && accessTokenResponse.body() != null) {
            Reader reader = new InputStreamReader(accessTokenResponse.body().byteStream(), StandardCharsets.UTF_8);

            if (accessTokenResponse.isSuccessful()) {
                log.debug("Access token request was successful");
                return objectMapper.readValue(reader, OAuth2AccessToken.class);
            }
            else {
                ErrorResponse errorResponse = objectMapper.readValue(reader, ErrorResponse.class);
                log.debug("Access token request failed with error {}", errorResponse);

                if (!Strings.isEmpty(errorResponse.getError()) ||
                    !Strings.isEmpty(errorResponse.getErrorDescription())) {
                    throw new HttpException("Received HTTP " + accessTokenResponse.code() +
                        " response from Authorization Server. error - " + errorResponse.getError() +
                        ", error description - " + errorResponse.getErrorDescription());
                } else {
                    throw new HttpException("Received HTTP " + accessTokenResponse.code() +
                        " response from Authorization Server");
                }
            }
        }
        else {
            throw new HttpException("Could not retrieve access token from Authorization Server");
        }
    }
}

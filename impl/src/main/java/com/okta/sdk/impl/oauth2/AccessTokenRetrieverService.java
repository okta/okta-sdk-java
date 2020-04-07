package com.okta.sdk.impl.oauth2;

import com.okta.sdk.oauth2.OAuth2AccessToken;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface AccessTokenRetrieverService {
    /**
     * Obtain OAuth2 access token from Authorization Server endpoint.
     *
     * @return {@link OAuth2AccessToken} object
     * @throws {@link IOException}
     * @throws {@link InvalidKeySpecException}
     * @throws {@link NoSuchAlgorithmException}
     */
    OAuth2AccessToken getOAuth2AccessToken() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException;
}

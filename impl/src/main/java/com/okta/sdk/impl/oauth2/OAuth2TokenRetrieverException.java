package com.okta.sdk.impl.oauth2;

public class OAuth2TokenRetrieverException extends RuntimeException {

    public OAuth2TokenRetrieverException(Throwable cause) {
        super(cause);
    }

    public OAuth2TokenRetrieverException(String s, Throwable cause) {
        super(s, cause);
    }
}

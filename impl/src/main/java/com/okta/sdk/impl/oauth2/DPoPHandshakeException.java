package com.okta.sdk.impl.oauth2;

public class DPoPHandshakeException extends RuntimeException {

    final boolean continueHandshake;
    final String responseBody;

    DPoPHandshakeException(DPopHandshakeState status, String error) {
        super(status.message + ". Error response body: " + error);
        this.continueHandshake = status.continueHandshake;
        this.responseBody = error;
    }

}

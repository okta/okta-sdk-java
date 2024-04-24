package com.okta.sdk.impl.oauth2;

enum DPopHandshakeState {
    
    //invalid states
    REPEATED_INVALID_DPOP_PROOF(false, "Invalid sequence, already received invalid_dpop_proof error"),
    REPEATED_USE_DPOP_NONCE(false, "Invalid sequence, already received use_dpop_nonce error"),
    MISSING_DPOP_NONCE_HEADER(false, "Invalid sequence, missing dpop-nonce header on use_dpop_nonce error response"),
    UNEXPECTED_STATE(false, "Unexpected authentication error"),
    
    //valid states
    FIRST_INVALID_DPOP_PROOF(true, "Received invalid_dpop_proof, will provide DPoP header"),
    FIRST_USE_DPOP_NONCE(true, "Received use_dpop_nonce, will provide nonce");

    final boolean continueHandshake;
    final String message;

    DPopHandshakeState(boolean continueHandshake, String message) {
        this.continueHandshake = continueHandshake;
        this.message = message;
    }
    
}

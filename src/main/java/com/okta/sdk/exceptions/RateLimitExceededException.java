package com.okta.sdk.exceptions;

import com.okta.sdk.framework.ErrorResponse;

public class RateLimitExceededException extends ApiException {
    public RateLimitExceededException(ErrorResponse errorResponse) {
        super(429, errorResponse);
    }
}

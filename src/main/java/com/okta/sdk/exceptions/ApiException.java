package com.okta.sdk.exceptions;

import com.okta.sdk.framework.ErrorCause;
import com.okta.sdk.framework.ErrorResponse;

import java.io.IOException;
import java.util.List;

public class ApiException extends IOException {

    private int statusCode;
    private ErrorResponse errorResponse;

    public ApiException(int statusCode, ErrorResponse errorResponse) {
        super(String.format("ApiException - errorCode: %s | errorSummary: %s | errorId: %s | errorCauses: %s",
                errorResponse == null ? "" : errorResponse.getErrorCode(),
                errorResponse == null ? "" : errorResponse.getErrorSummary(),
                errorResponse == null ? "" : errorResponse.getErrorId(),
                errorResponse == null ? "" : printErrorCauses(errorResponse.getErrorCauses())));
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }

    private static String printErrorCauses(List<ErrorCause> errorCauses) {
        StringBuilder sb = new StringBuilder();
        if (!errorCauses.isEmpty()) {
            for (ErrorCause errorCause : errorCauses) {
                sb.append(System.getProperty("line.separator"));
                sb.append(errorCause.getErrorSummary());
            }
        }
        return sb.toString();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

}

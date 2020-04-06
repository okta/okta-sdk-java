package com.okta.sdk.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "ErrorResponse [error=" + error + ", errorDescription=" + errorDescription + "]";
    }
}

package com.okta.sdk.framework;

import java.util.LinkedList;
import java.util.List;

public class ErrorResponse {

    private String errorCode;
    private String errorSummary;
    private String errorLink;
    private String errorId;
    private List<ErrorCause> errorCauses;

    public ErrorResponse() {
        this.errorCauses = new LinkedList<ErrorCause>();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public String getErrorLink() {
        return errorLink;
    }

    public void setErrorLink(String errorLink) {
        this.errorLink = errorLink;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public List<ErrorCause> getErrorCauses() {
        return this.errorCauses;
    }

    public void setErrorCauses(List<ErrorCause> errorCauses) {
        this.errorCauses = errorCauses;
    }

    public void addCause(String cause) {
        ErrorCause errorCause = new ErrorCause();
        errorCause.setErrorSummary(cause);
        this.errorCauses.add(errorCause);
    }

}

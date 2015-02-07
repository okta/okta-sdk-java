package com.okta.sdk.models.factors;

import com.okta.sdk.framework.ApiObject;

public class Verification extends ApiObject {

    private String activationToken;

    private String answer;

    private String passCode;

    private String nextPassCode;

    /**
     * Gets activationToken
     */
    public String getActivationToken() {
        return this.activationToken;
    }

    /**
     * Sets activationToken
     */
    public void setActivationToken(String val) {
        this.activationToken = val;
    }

    /**
     * Gets answer
     */
    public String getAnswer() {
        return this.answer;
    }

    /**
     * Sets answer
     */
    public void setAnswer(String val) {
        this.answer = val;
    }

    /**
     * Gets passCode
     */
    public String getPassCode() {
        return this.passCode;
    }

    /**
     * Sets passCode
     */
    public void setPassCode(String val) {
        this.passCode = val;
    }

    /**
     * Gets nextPassCode
     */
    public String getNextPassCode() {
        return this.nextPassCode;
    }

    /**
     * Sets nextPassCode
     */
    public void setNextPassCode(String val) {
        this.nextPassCode = val;
    }
}
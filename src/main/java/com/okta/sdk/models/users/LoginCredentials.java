package com.okta.sdk.models.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

public class LoginCredentials extends ApiObject {

    private Password password;

    @JsonProperty(value = "recovery_question")
    private RecoveryQuestion recoveryQuestion;

    private Provider provider;

    /**
     * Gets password
     */
    public Password getPassword() {
        return this.password;
    }

    /**
     * Sets password
     */
    public void setPassword(Password val) {
        this.password = val;
    }

    /**
     * Gets recoveryQuestion
     */
    public RecoveryQuestion getRecoveryQuestion() {
        return this.recoveryQuestion;
    }

    /**
     * Sets recoveryQuestion
     */
    public void setRecoveryQuestion(RecoveryQuestion val) {
        this.recoveryQuestion = val;
    }

    /**
     * Gets provider
     */
    public Provider getProvider() {
        return this.provider;
    }

    /**
     * Sets provider
     */
    public void setProvider(Provider val) {
        this.provider = val;
    }
}
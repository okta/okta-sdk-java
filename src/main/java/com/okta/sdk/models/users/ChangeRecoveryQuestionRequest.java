package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class ChangeRecoveryQuestionRequest extends ApiObject {

    private Password password;

    private RecoveryQuestion recoveryQuestion;

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
}
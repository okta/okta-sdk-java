package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class RecoveryQuestion extends ApiObject {

    private String question;

    private String answer;

    /**
     * Gets question
     */
    public String getQuestion() {
        return this.question;
    }

    /**
     * Sets question
     */
    public void setQuestion(String val) {
        this.question = val;
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
}
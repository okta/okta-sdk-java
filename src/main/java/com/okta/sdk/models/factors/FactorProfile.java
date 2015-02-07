package com.okta.sdk.models.factors;

import com.okta.sdk.framework.ApiObject;
import java.util.Map;

public class FactorProfile extends ApiObject {

    /**
     * unique key for question
     */
    private String question;

    /**
     * display text for question
     */
    private String questionText;

    /**
     * answer to question
     */
    private String answer;

    /**
     * phone number of mobile device
     */
    private String phoneNumber;

    /**
     * unique id for instance
     */
    private String credentialId;

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
     * Gets questionText
     */
    public String getQuestionText() {
        return this.questionText;
    }

    /**
     * Sets questionText
     */
    public void setQuestionText(String val) {
        this.questionText = val;
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
     * Gets phoneNumber
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets phoneNumber
     */
    public void setPhoneNumber(String val) {
        this.phoneNumber = val;
    }

    /**
     * Gets credentialId
     */
    public String getCredentialId() {
        return this.credentialId;
    }

    /**
     * Sets credentialId
     */
    public void setCredentialId(String val) {
        this.credentialId = val;
    }
}
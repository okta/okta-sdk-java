/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.models.factors;

import com.okta.sdk.framework.ApiObject;

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
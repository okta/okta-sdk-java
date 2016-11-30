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
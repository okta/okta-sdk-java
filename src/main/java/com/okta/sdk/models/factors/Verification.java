/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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

    /**
     * Activation token for the factor.
     */
    private String activationToken;

    /**
     * Answer for the factor.
     */
    private String answer;

    /**
     * Passcode for the Symantec VIP factor.
     */
    private String passCode;

    /**
     * NextPassCode for the Symantec VIP factor
     */
    private String nextPassCode;

    /**
     * Returns the activationToken.
     * @return {@link String}
     */
    public String getActivationToken() {
        return this.activationToken;
    }

    /**
     * Sets the activationToken.
     * @param val {@link String}
     */
    public void setActivationToken(String val) {
        this.activationToken = val;
    }

    /**
     * Returns the answer.
     * @return {@link String}
     */
    public String getAnswer() {
        return this.answer;
    }

    /**
     * Sets the answer.
     * @param val {@link String}
     */
    public void setAnswer(String val) {
        this.answer = val;
    }

    /**
     * Returns the passCode.
     * @return {@link String}
     */
    public String getPassCode() {
        return this.passCode;
    }

    /**
     * Sets the passCode.
     * @param val {@link String}
     */
    public void setPassCode(String val) {
        this.passCode = val;
    }

    /**
     * Returns the nextPassCode.
     * @return {@link String}
     */
    public String getNextPassCode() {
        return this.nextPassCode;
    }

    /**
     * Sets the nextPassCode.
     * @param val {@link String}
     */
    public void setNextPassCode(String val) {
        this.nextPassCode = val;
    }
}

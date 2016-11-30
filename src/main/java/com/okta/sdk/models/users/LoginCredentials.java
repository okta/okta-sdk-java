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
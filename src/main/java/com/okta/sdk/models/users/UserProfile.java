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

import com.okta.sdk.framework.ApiObject;

public class UserProfile extends ApiObject {

    private String login;

    private String email;

    private String secondEmail;

    private String firstName;

    private String lastName;

    private String mobilePhone;

    /**
     * Gets login
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Sets login
     */
    public void setLogin(String val) {
        this.login = val;
    }

    /**
     * Gets email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets email
     */
    public void setEmail(String val) {
        this.email = val;
    }

    /**
     * Gets secondEmail
     */
    public String getSecondEmail() {
        return this.secondEmail;
    }

    /**
     * Sets secondEmail
     */
    public void setSecondEmail(String val) {
        this.secondEmail = val;
    }

    /**
     * Gets firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets firstName
     */
    public void setFirstName(String val) {
        this.firstName = val;
    }

    /**
     * Gets lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets lastName
     */
    public void setLastName(String val) {
        this.lastName = val;
    }

    /**
     * Gets mobilePhone
     */
    public String getMobilePhone() {
        return this.mobilePhone;
    }

    /**
     * Sets mobilePhone
     */
    public void setMobilePhone(String val) {
        this.mobilePhone = val;
    }
}
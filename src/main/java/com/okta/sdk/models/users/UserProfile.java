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

package com.okta.sdk.models.users;

import com.okta.sdk.framework.ApiObject;

public class UserProfile extends ApiObject {

    /**
     * The login of the User.
     */
    private String login;

    /**
     * The email of the User
     */
    private String email;

    /**
     * The secondary email of the User.
     */
    private String secondEmail;

    /**
     * The first name of the User.
     */
    private String firstName;

    /**
     * The last name of the User.
     */
    private String lastName;

    /**
     * The mobile phone of the User.
     */
    private String mobilePhone;

    /**
     * Returns the login.
     * @return {@link String}
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Sets the login.
     * @param val {@link String}
     */
    public void setLogin(String val) {
        this.login = val;
    }

    /**
     * Returns the email.
     * @return {@link String}
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email.
     * @param val {@link String}
     */
    public void setEmail(String val) {
        this.email = val;
    }

    /**
     * Returns the secondEmail.
     * @return {@link String}
     */
    public String getSecondEmail() {
        return this.secondEmail;
    }

    /**
     * Sets the secondEmail.
     * @param val {@link String}
     */
    public void setSecondEmail(String val) {
        this.secondEmail = val;
    }

    /**
     * Returns the firstName.
     * @return {@link String}
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the firstName.
     * @param val {@link String}
     */
    public void setFirstName(String val) {
        this.firstName = val;
    }

    /**
     * Returns the lastName.
     * @return {@link String}
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the lastName.
     * @param val {@link String}
     */
    public void setLastName(String val) {
        this.lastName = val;
    }

    /**
     * Returns the mobilePhone.
     * @return {@link String}
     */
    public String getMobilePhone() {
        return this.mobilePhone;
    }

    /**
     * Sets the mobilePhone.
     * @param val {@link String}
     */
    public void setMobilePhone(String val) {
        this.mobilePhone = val;
    }
}

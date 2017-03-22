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

package com.okta.sdk.models.apps;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.users.Password;

public class AppCredentials extends ApiObject {

    /**
     * Determines how credentials are managed for the signOnMode.
     */
    private String scheme;

    /**
     * Template used to generate a user’s username when the application is assigned via a group or directly to a user.
     */
    private UserNameTemplate userNameTemplate;

    /**
     * Shared username for app.
     */
    private String userName;

    /**
     * Shared password for app.
     */
    private Password password;

    /**
     * Toggle for reveal password.
     */
    private Boolean revealPassword;

    /**
     * Token signing reference object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Signing signing = new Signing();

    /**
     * Returns the scheme.
     * @return {@link String}
     */
    public String getScheme() {
        return this.scheme;
    }

    /**
     * Sets the scheme.
     * @param val {@link String}
     */
    public void setScheme(String val) {
        this.scheme = val;
    }

    /**
     * Returns the userNameTemplate.
     * @return {@link UserNameTemplate}
     */
    public UserNameTemplate getUserNameTemplate() {
        return this.userNameTemplate;
    }

    /**
     * Sets the userNameTemplate.
     * @param val {@link UserNameTemplate}
     */
    public void setUserNameTemplate(UserNameTemplate val) {
        this.userNameTemplate = val;
    }

    /**
     * Returns the userName.
     * @return {@link String}
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the userName.
     * @param val {@link String}
     */
    public void setUserName(String val) {
        this.userName = val;
    }

    /**
     * Returns the password.
     * @return {@link Password}
     */
    public Password getPassword() {
        return this.password;
    }

    /**
     * Sets the password.
     * @param val {@link Password}
     */
    public void setPassword(Password val) {
        this.password = val;
    }

    /**
     * Returns the revealPassword.
     * @return {@link Boolean}
     */
    public Boolean getRevealPassword() {
        return this.revealPassword;
    }

    /**
     * Sets the revealPassword.
     * @param val {@link Boolean}
     */
    public void setRevealPassword(Boolean val) {
        this.revealPassword = val;
    }

    /**
     * Returns the signing object.
     * @return {@link Signing}
     */
    public Signing getSigning() {
        return signing;
    }

    /**
     * Sets the signing object.
     * @param val {@link Signing}
     */
    public void setSigning(Signing val) {
        this.signing = val;
    }
}

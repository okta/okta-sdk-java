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

package com.okta.sdk.models.apps;

import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.users.Password;

public class AppCredentials extends ApiObject {

    /**
     * Determines how credentials are managed for the signOnMode
     */
    private String scheme;

    /**
     * Template used to generate a user’s username when the application is assigned via a group or directly to a user
     */
    private UserNameTemplate userNameTemplate;

    /**
     * Shared username for app
     */
    private String userName;

    /**
     * Shared password for app
     */
    private Password password;

    /**
     * Gets scheme
     */
    public String getScheme() {
        return this.scheme;
    }

    /**
     * Sets scheme
     */
    public void setScheme(String val) {
        this.scheme = val;
    }

    /**
     * Gets userNameTemplate
     */
    public UserNameTemplate getUserNameTemplate() {
        return this.userNameTemplate;
    }

    /**
     * Sets userNameTemplate
     */
    public void setUserNameTemplate(UserNameTemplate val) {
        this.userNameTemplate = val;
    }

    /**
     * Gets userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets userName
     */
    public void setUserName(String val) {
        this.userName = val;
    }

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
}
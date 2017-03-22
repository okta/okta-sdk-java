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

package com.okta.sdk.models.sessions;

import com.okta.sdk.framework.ApiObject;

public class Credentials extends ApiObject {

    /**
     * User's login name.
     */
    private String username;

    /**
     * User's plaintext password.
     */
    private String password;

    /**
     * Returns the username.
     * @return {@link String}
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username.
     * @param val {@link String}
     */
    public void setUsername(String val) {
        this.username = val;
    }

    /**
     * Returns the password.
     * @return {@link String}
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password.
     * @param val {@link String}
     */
    public void setPassword(String val) {
        this.password = val;
    }
}

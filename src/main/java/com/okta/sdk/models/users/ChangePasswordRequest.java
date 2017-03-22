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

public class ChangePasswordRequest extends ApiObject {

    /**
     * Previous password.
     */
    private Password oldPassword;

    /**
     * New password.
     */
    private Password newPassword;

    /**
     * Returns the oldPassword.
     * @return {@link Password}
     */
    public Password getOldPassword() {
        return this.oldPassword;
    }

    /**
     * Sets the oldPassword.
     * @param val {@link Password}
     */
    public void setOldPassword(Password val) {
        this.oldPassword = val;
    }

    /**
     * Returns the newPassword.
     * @return {@link Password}
     */
    public Password getNewPassword() {
        return this.newPassword;
    }

    /**
     * Sets the newPassword.
     * @param val {@link Password}
     */
    public void setNewPassword(Password val) {
        this.newPassword = val;
    }
}

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

import com.okta.sdk.framework.ApiObject;

public class UserNameTemplate extends ApiObject {

    /**
     * <apping expression for username.
     */
    private String template;

    /**
     * Type of mapping expression.
     */
    private String type;

    /**
     * Suffix for built-in mapping expressions.
     */
    private String userSuffix;

    /**
     * Returns the template.
     * @return {@link String}
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * Sets the template.
     * @param val {@link String}
     */
    public void setTemplate(String val) {
        this.template = val;
    }

    /**
     * Returns the type.
     * @return {@link String}
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the type.
     * @param val {@link String}
     */
    public void setType(String val) {
        this.type = val;
    }

    /**
     * Returns the userSuffix.
     * @return {@link String}
     */
    public String getUserSuffix() {
        return this.userSuffix;
    }

    /**
     * Sets the userSuffix.
     * @param val {@link String}
     */
    public void setUserSuffix(String val) {
        this.userSuffix = val;
    }
}

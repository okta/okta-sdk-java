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

package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;

public class Actor extends ApiObject {

    /**
     * Unique key for actor.
     */
    private String id;

    /**
     * Name of actor used for display purposes.
     */
    private String displayName;

    /**
     * User or Client.
     */
    private String objectType;

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the ID.
     * @param val {@link String}
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the displayName.
     * @return {@link String}
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets the displayName.
     * @param val {@link String}
     */
    public void setDisplayName(String val) {
        this.displayName = val;
    }

    /**
     * Returns the objectType.
     * @return {@link String}
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Sets the objectType.
     * @param val {@link String}
     */
    public void setObjectType(String val) {
        this.objectType = val;
    }
}

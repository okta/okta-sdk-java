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

public class Action extends ApiObject {

    /**
     * Description of an action.
     */
    private String message;

    /**
     * Categories for an action.
     */
    private String[] categories;

    /**
     * Identifies the unique type of an action.
     */
    private String objectType;

    /**
     * Relative uri of the request that generated the event.
     */
    private String requestUri;

    /**
     * Returns the message.
     * @return {@link String}
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message.
     * @param val {@link String}
     */
    public void setMessage(String val) {
        this.message = val;
    }

    /**
     * Returns the categories.
     * @return List of Strings
     */
    public String[] getCategories() {
        return this.categories;
    }

    /**
     * Sets the categories.
     * @param val List of Strings
     */
    public void setCategories(String[] val) {
        this.categories = val;
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

    /**
     * Returns the requestUri.
     * @return {@link String}
     */
    public String getRequestUri() {
        return this.requestUri;
    }

    /**
     * Sets the requestUri.
     * @param val {@link String}
     */
    public void setRequestUri(String val) {
        this.requestUri = val;
    }
}

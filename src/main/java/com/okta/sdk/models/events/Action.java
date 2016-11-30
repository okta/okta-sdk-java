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

package com.okta.sdk.models.events;

import com.okta.sdk.framework.ApiObject;

public class Action extends ApiObject {

    /**
     * Description of an action
     */
    private String message;

    /**
     * Categories for an action
     */
    private String[] categories;

    /**
     * Identifies the unique type of an action
     */
    private String objectType;

    /**
     * Relative uri of the request that generated the event.
     */
    private String requestUri;

    /**
     * Gets message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets message
     */
    public void setMessage(String val) {
        this.message = val;
    }

    /**
     * Gets categories
     */
    public String[] getCategories() {
        return this.categories;
    }

    /**
     * Sets categories
     */
    public void setCategories(String[] val) {
        this.categories = val;
    }

    /**
     * Gets objectType
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Sets objectType
     */
    public void setObjectType(String val) {
        this.objectType = val;
    }

    /**
     * Gets requestUri
     */
    public String getRequestUri() {
        return this.requestUri;
    }

    /**
     * Sets requestUri
     */
    public void setRequestUri(String val) {
        this.requestUri = val;
    }
}
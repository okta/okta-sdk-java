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
import org.joda.time.DateTime;

public class AppGroup extends ApiObject {

    /**
     * unique key of group
     */
    private String id;

    /**
     * timestamp when app group was last updated
     */
    private DateTime lastUpdated;

    /**
     * priority of group assignment
     */
    private Integer priority;

    /**
     * Gets id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets id
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Gets lastUpdated
     */
    public DateTime getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets lastUpdated
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Gets priority
     */
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * Sets priority
     */
    public void setPriority(Integer val) {
        this.priority = val;
    }
}
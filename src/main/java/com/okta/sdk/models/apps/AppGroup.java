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
import org.joda.time.DateTime;

public class AppGroup extends ApiObject {

    /**
     * Unique key of group.
     */
    private String id;

    /**
     * Timestamp when app group was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Priority of group assignment.
     */
    private Integer priority;

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
     * Returns the lastUpdated time.
     * @return {@link DateTime}
     */
    public DateTime getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets the lastUpdated time.
     * @param val {@link DateTime}
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Returns the priority.
     * @return {@link Integer}
     */
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * Sets the priority.
     * @param val {@link Integer}
     */
    public void setPriority(Integer val) {
        this.priority = val;
    }
}

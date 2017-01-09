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

package com.okta.sdk.models.usergroups;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

public class UserGroup extends ApiObject {

    /**
     * unique key for group
     */
    private String id;

    /**
     * timestamp when user was created
     */
    private DateTime created;

    /**
     * timestamp when user was last updated
     */
    private DateTime lastUpdated;

    /**
     * timestamp when last membership was updated
     */
    private DateTime lastMembershipUpdated;

    /**
     * determines the group’s profile
     */
    private String[] objectClass;

    /**
     * determines the group's type
     */
    private String type;

    /**
     * the group’s profile attributes
     */
    private UserGroupProfile profile;

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
     * Gets created
     */
    public DateTime getCreated() {
        return this.created;
    }

    /**
     * Sets created
     */
    public void setCreated(DateTime val) {
        this.created = val;
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
     * Gets lastMembershipUpdated
     */
    public DateTime getLastMembershipUpdated() {
        return this.lastMembershipUpdated;
    }

    /**
     * Sets lastMembershipUpdated
     */
    public void setLastMembershipUpdated(DateTime val) {
        this.lastMembershipUpdated = val;
    }

    /**
     * Gets objectClass
     */
    public String[] getObjectClass() {
        return this.objectClass;
    }

    /**
     * Sets objectClass
     */
    public void setObjectClass(String[] val) {
        this.objectClass = val;
    }

    /**
     * Gets profile
     */
    public UserGroupProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets profile
     */
    public void setProfile(UserGroupProfile val) {
        this.profile = val;
    }

    /**
     * Gets type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type
     */
    public void setType(String type) {
        this.type = type;
    }
}
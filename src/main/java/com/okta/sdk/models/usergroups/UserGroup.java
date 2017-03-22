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

package com.okta.sdk.models.usergroups;

import com.okta.sdk.framework.ApiObject;
import org.joda.time.DateTime;

public class UserGroup extends ApiObject {

    /**
     * Unique key for group.
     */
    private String id;

    /**
     * Timestamp when user was created.
     */
    private DateTime created;

    /**
     * Timestamp when user was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Timestamp when last membership was updated.
     */
    private DateTime lastMembershipUpdated;

    /**
     * Determines the group’s profile.
     */
    private String[] objectClass;

    /**
     * Determines the group's type.
     */
    private String type;

    /**
     * The group’s profile attributes.
     */
    private UserGroupProfile profile;

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the ID.
     * @param val {@link String} unique ID of group
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the created time.
     * @return {@link DateTime}
     */
    public DateTime getCreated() {
        return this.created;
    }

    /**
     * Sets the created time.
     * @param val {@link DateTime} when group was created
     */
    public void setCreated(DateTime val) {
        this.created = val;
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
     * @param val {@link DateTime} when group was last updated
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Returns the lastMembershipUpdated time.
     * @return {@link DateTime}
     */
    public DateTime getLastMembershipUpdated() {
        return this.lastMembershipUpdated;
    }

    /**
     * Sets the lastMembershipUpdated time.
     * @param val {@link DateTime} when membership was last updated
     */
    public void setLastMembershipUpdated(DateTime val) {
        this.lastMembershipUpdated = val;
    }

    /**
     * Returns the objectClass.
     * @return list of object classes as String[]
     */
    public String[] getObjectClass() {
        return this.objectClass;
    }

    /**
     * Sets the objectClass.
     * @param val list of object classes
     */
    public void setObjectClass(String[] val) {
        this.objectClass = val;
    }

    /**
     * Returns the profile.
     * @return {@link UserGroupProfile}
     */
    public UserGroupProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets the profile.
     * @param val {@link UserGroupProfile}
     */
    public void setProfile(UserGroupProfile val) {
        this.profile = val;
    }

    /**
     * Returns the type.
     * @return {@link String}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param val {@link String}
     */
    public void setType(String val) {
        this.type = val;
    }
}

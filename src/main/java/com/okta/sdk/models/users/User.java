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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public class User extends ApiObject {

    /**
     * Unique key for user.
     */
    private String id;

    /**
     * Current status of user.
     */
    private String status;

    /**
     * Timestamp when user was created.
     */
    private DateTime created;

    /**
     * Timestamp when transition to ACTIVE status completed.
     */
    private DateTime activated;

    /**
     * Timestamp when status last changed.
     */
    private DateTime statusChanged;

    /**
     * Timestamp of last login.
     */
    private DateTime lastLogin;

    /**
     * Timestamp when user was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Timestamp when password last changed.
     */
    private DateTime passwordChanged;

    /**
     * Target status of an in progress asynchronous status transition.
     */
    private String transitioningToStatus;

    /**
     * Profile of the User.
     */
    private UserProfile profile;

    /**
     * Login credentials of the User.
     */
    private LoginCredentials credentials;

    /**
     * HAL links the of User response.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * Used only for creation of new Users. These are the groups that the new User will be immediately added to.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private List<String> groupIds;

    /**
     * Returns the ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the ID.
     * @param val {@link String} of the user unique ID
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the status.
     * @return {@link String}
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status.
     * @param val {@link String} of the new user status
     */
    public void setStatus(String val) {
        this.status = val;
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
     * @param val {@link DateTime} the user was created
     */
    public void setCreated(DateTime val) {
        this.created = val;
    }

    /**
     * Returns the activated time.
     * @return {@link DateTime}
     */
    public DateTime getActivated() {
        return this.activated;
    }

    /**
     * Sets the activated time.
     * @param val {@link DateTime} the user was activated
     */
    public void setActivated(DateTime val) {
        this.activated = val;
    }

    /**
     * Returns the statusChanged time.
     * @return {@link DateTime}
     */
    public DateTime getStatusChanged() {
        return this.statusChanged;
    }

    /**
     * Sets the statusChanged time.
     * @param val {@link DateTime} the status changed
     */
    public void setStatusChanged(DateTime val) {
        this.statusChanged = val;
    }

    /**
     * Returns the lastLogin time.
     * @return {@link DateTime}
     */
    public DateTime getLastLogin() {
        return this.lastLogin;
    }

    /**
     * Sets the lastLogin time.
     * @param val {@link DateTime} the user last logged in
     */
    public void setLastLogin(DateTime val) {
        this.lastLogin = val;
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
     * @param val {@link DateTime} the user was last updated
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Returns the passwordChanged time.
     * @return {@link DateTime}
     */
    public DateTime getPasswordChanged() {
        return this.passwordChanged;
    }

    /**
     * Sets the passwordChanged time.
     * @param val {@link DateTime} the password was last changed
     */
    public void setPasswordChanged(DateTime val) {
        this.passwordChanged = val;
    }

    /**
     * Returns the transitioningToStatus.
     * @return {@link String}
     */
    public String getTransitioningToStatus() {
        return this.transitioningToStatus;
    }

    /**
     * Sets the transitioningToStatus.
     * @param val {@link String} current status of user lifecycle
     */
    public void setTransitioningToStatus(String val) {
        this.transitioningToStatus = val;
    }

    /**
     * Returns the user profile.
     * @return {@link UserProfile}
     */
    public UserProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets the user profile.
     * @param val {@link UserProfile}
     */
    public void setProfile(UserProfile val) {
        this.profile = val;
    }

    /**
     * Returns the user's credentials.
     * @return {@link LoginCredentials}
     */
    public LoginCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets the user's credentials.
     * @param val {@link LoginCredentials}
     */
    public void setCredentials(LoginCredentials val) {
        this.credentials = val;
    }


    /**
     * Returns the HAL links.
     * @return {@link Map} of HAL links associated with {@link User}
     */
    public Map<String, LinksUnion> getLinks() {
        return links;
    }

    /**
     * Sets the links.
     * @param links {@link Map} of HAL links
     */
    public void setLinks(Map<String, LinksUnion> links) {
        this.links = links;
    }

    /** Returns the group IDs.
     * Used only for creation of new Users. These are the groups that the new User will be immediately added to.
     * @return list of group ids that new user will be added to.
     */
    public List<String> getGroupIds() {
        return this.groupIds;
    }

    /**
     * Sets group IDs.
     * Used only for creation of new Users. These are the groups that the new User will be immediately added to.
     * @param groupIds list of group ids that new user will be added to
     */
    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}

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
     * unique key for user
     */
    private String id;

    /**
     * current status of user
     */
    private String status;

    /**
     * timestamp when user was created
     */
    private DateTime created;

    /**
     * timestamp when transition to ACTIVE status completed
     */
    private DateTime activated;

    /**
     * timestamp when status last changed
     */
    private DateTime statusChanged;

    /**
     * timestamp of last login
     */
    private DateTime lastLogin;

    /**
     * timestamp when user was last updated
     */
    private DateTime lastUpdated;

    /**
     * timestamp when password last changed
     */
    private DateTime passwordChanged;

    /**
     * target status of an in progress asynchronous status transition
     */
    private String transitioningToStatus;

    private UserProfile profile;

    private LoginCredentials credentials;

    private List<String> groupIds;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

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
     * Gets status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets status
     */
    public void setStatus(String val) {
        this.status = val;
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
     * Gets activated
     */
    public DateTime getActivated() {
        return this.activated;
    }

    /**
     * Sets activated
     */
    public void setActivated(DateTime val) {
        this.activated = val;
    }

    /**
     * Gets statusChanged
     */
    public DateTime getStatusChanged() {
        return this.statusChanged;
    }

    /**
     * Sets statusChanged
     */
    public void setStatusChanged(DateTime val) {
        this.statusChanged = val;
    }

    /**
     * Gets lastLogin
     */
    public DateTime getLastLogin() {
        return this.lastLogin;
    }

    /**
     * Sets lastLogin
     */
    public void setLastLogin(DateTime val) {
        this.lastLogin = val;
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
     * Gets passwordChanged
     */
    public DateTime getPasswordChanged() {
        return this.passwordChanged;
    }

    /**
     * Sets passwordChanged
     */
    public void setPasswordChanged(DateTime val) {
        this.passwordChanged = val;
    }

    /**
     * Gets transitioningToStatus
     */
    public String getTransitioningToStatus() {
        return this.transitioningToStatus;
    }

    /**
     * Sets transitioningToStatus
     */
    public void setTransitioningToStatus(String val) {
        this.transitioningToStatus = val;
    }

    /**
     * Gets profile
     */
    public UserProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets profile
     */
    public void setProfile(UserProfile val) {
        this.profile = val;
    }

    /**
     * Gets groupIds
     */
    public List<String> getGroupIds() {
        return this.groupIds;
    }

    /**
     * Sets groupIds
     */
    public void setGroupIds(List<String> val) {
        this.groupIds = val;
    }

    /**
     * Gets credentials
     */
    public LoginCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets credentials
     */
    public void setCredentials(LoginCredentials val) {
        this.credentials = val;
    }

    public Map<String, LinksUnion> getLinks() {
        return links;
    }

    public void setLinks(Map<String, LinksUnion> links) {
        this.links = links;
    }
}
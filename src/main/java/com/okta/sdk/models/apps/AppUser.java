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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.UserProfile;
import org.joda.time.DateTime;

import java.util.Map;

public class AppUser extends ApiObject {

    /**
     * Unique key for user.
     */
    private String id;

    /**
     * Unique external key for user.
     */
    private String externalId;

    /**
     * Timestamp when user was created.
     */
    private DateTime created;

    /**
     * Timestamp when user was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Toggles the assignment between user or group scope.
     */
    private String scope;

    /**
     * Status of app user.
     */
    private String status;

    /**
     * Timestamp when user was last updated.
     */
    private DateTime statusChanged;

    /**
     * Timestamp when app password last changed.
     */
    private DateTime passwordChanged;

    /**
     * Synchronization state for app user.
     */
    private String syncState;

    /**
     * Timestamp when last sync operation was executed.
     */
    private DateTime lastSync;

    /**
     * Credentials for assigned app.
     */
    private LoginCredentials credentials;

    /**
     * App-specific profile for the user.
     */
    private UserProfile profile;

    /**
     * HAL links for response object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

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
     * Returns the externalId.
     * @return {@link String}
     */
    public String getExternalId() {
        return this.externalId;
    }

    /**
     * Sets the externalId.
     * @param val {@link String}
     */
    public void setExternalId(String val) {
        this.externalId = val;
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
     * @param val {@link DateTime}
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
     * @param val {@link DateTime}
     */
    public void setLastUpdated(DateTime val) {
        this.lastUpdated = val;
    }

    /**
     * Returns the scopes.
     * @return {@link String}
     */
    public String getScope() {
        return this.scope;
    }

    /**
     * Sets the scope.
     * @param val {@link String}
     */
    public void setScope(String val) {
        this.scope = val;
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
     * @param val {@link String}
     */
    public void setStatus(String val) {
        this.status = val;
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
     * @param val {@link DateTime}
     */
    public void setStatusChanged(DateTime val) {
        this.statusChanged = val;
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
     * @param val {@link DateTime}
     */
    public void setPasswordChanged(DateTime val) {
        this.passwordChanged = val;
    }

    /**
     * Returns the syncState.
     * @return {@link String}
     */
    public String getSyncState() {
        return this.syncState;
    }

    /**
     * Sets the syncState.
     * @param val {@link String}
     */
    public void setSyncState(String val) {
        this.syncState = val;
    }

    /**
     * Returns the lastSync time.
     * @return {@link DateTime}
     */
    public DateTime getLastSync() {
        return this.lastSync;
    }

    /**
     * Sets the passwordChanged.
     * @param val {@link DateTime}
     */
    public void setLastSync(DateTime val) {
        this.lastSync = val;
    }

    /**
     * Returns the credentials.
     * @return {@link LoginCredentials}
     */
    public LoginCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets the credentials.
     * @param val {@link LoginCredentials}
     */
    public void setCredentials(LoginCredentials val) {
        this.credentials = val;
    }

    /**
     * Returns the profile.
     * @return {@link UserProfile}
     */
    public UserProfile getProfile() {
        return this.profile;
    }

    /**
     * Sets the profile.
     * @param val {@link UserProfile}
     */
    public void setProfile(UserProfile val) {
        this.profile = val;
    }

    /**
     * Returns the links object.
     * @return {@link Map}
     */
    public Map<String, LinksUnion> getLinks() {
        return links;
    }

    /**
     * Sets the links object.
     * @param val {@link Map}
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }
}

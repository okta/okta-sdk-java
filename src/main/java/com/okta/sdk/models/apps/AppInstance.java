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
import org.joda.time.DateTime;

import java.util.Map;

public class AppInstance extends ApiObject {

    /**
     * Public status options.
     */
    public static class Status {
        public String INACTIVE = "INACTIVE";
        public String ACTIVE = "ACTIVE";
        public String DELETED = "DELETED";
    }

    /**
     * Unique key for app.
     */
    private String id;

    /**
     * Unique key for app definition.
     */
    private String name;

    /**
     * Unique user-defined display name for app.
     */
    private String label;

    /**
     * Timestamp when app was created.
     */
    private DateTime created;

    /**
     * Timestamp when app was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Status of app.
     */
    private String status;

    /**
     * Timestamp when transition to ACTIVE status completed.
     */
    private DateTime activated;

    /**
     * Enabled app features.
     */
    private String[] features;

    /**
     * Authentication mode of app.
     */
    private String signOnMode;

    /**
     * Accessibility access for app.
     */
    private Accessibility accessibility;

    /**
     * Visibility of the app.
     */
    private Visibility visibility;

    /**
     * Credentials for the app.
     */
    private AppCredentials credentials;

    /**
     * Settings for the app.
     */
    private Settings settings;

    /**
     * HAL links object from response.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded object from response.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

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
     * Returns the name.
     * @return {@link String}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     * @param val {@link String}
     */
    public void setName(String val) {
        this.name = val;
    }

    /**
     * Returns the label.
     * @return {@link String}
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label.
     * @param val {@link String}
     */
    public void setLabel(String val) {
        this.label = val;
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
     * Returns the activated time.
     * @return {@link DateTime}
     */
    public DateTime getActivated() {
        return this.activated;
    }

    /**
     * Sets the activated time.
     * @param val {@link DateTime}
     */
    public void setActivated(DateTime val) {
        this.activated = val;
    }

    /**
     * Returns the features.
     * @return List of Strings
     */
    public String[] getFeatures() {
        return this.features;
    }

    /**
     * Sets the features.
     * @param val List of Strings
     */
    public void setFeatures(String[] val) {
        this.features = val;
    }

    /**
     * Returns the signOnMode.
     * @return {@link String}
     */
    public String getSignOnMode() {
        return this.signOnMode;
    }

    /**
     * Sets the signOnMode.
     * @param val {@link String}
     */
    public void setSignOnMode(String val) {
        this.signOnMode = val;
    }

    /**
     * Returns the accessibility.
     * @return {@link Accessibility}
     */
    public Accessibility getAccessibility() {
        return this.accessibility;
    }

    /**
     * Sets the accessibility.
     * @param val {@link Accessibility}
     */
    public void setAccessibility(Accessibility val) {
        this.accessibility = val;
    }

    /**
     * Returns the visibility.
     * @return {@link Visibility}
     */
    public Visibility getVisibility() {
        return this.visibility;
    }

    /**
     * Sets the visibility.
     * @param val {@link Visibility}
     */
    public void setVisibility(Visibility val) {
        this.visibility = val;
    }

    /**
     * Returns the credentials.
     * @return {@link AppCredentials}
     */
    public AppCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets the credentials.
     * @param val {@link AppCredentials}
     */
    public void setCredentials(AppCredentials val) {
        this.credentials = val;
    }

    /**
     * Returns the settings.
     * @return {@link Settings}
     */
    public Settings getSettings() {
        return this.settings;
    }

    /**
     * Sets the settings.
     * @param val {@link Settings}
     */
    public void setSettings(Settings val) {
        this.settings = val;
    }

    /**
     * Returns the links object.
     * @return {@link Map}
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets the links object.
     * @param val {@link Map}
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Returns the embedded object.
     * @return {@link Map}
     */
    public Map<String, Object> getEmbedded() {
        return this.embedded;
    }

    /**
     * Sets the embedded object.
     * @param val {@link Map}
     */
    public void setEmbedded(Map<String, Object> val) {
        this.embedded = val;
    }
}

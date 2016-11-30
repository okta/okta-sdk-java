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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;
import org.joda.time.DateTime;

import java.util.Map;

public class AppInstance extends ApiObject {

    public static class Status {
        public String INACTIVE = "INACTIVE";
        public String ACTIVE = "ACTIVE";
        public String DELETED = "DELETED";
    }

    /**
     * unique key for app
     */
    private String id;

    /**
     * unique key for app definition
     */
    private String name;

    /**
     * unique user-defined display name for app
     */
    private String label;

    /**
     * timestamp when app was created
     */
    private DateTime created;

    /**
     * timestamp when app was last updated
     */
    private DateTime lastUpdated;

    /**
     * status of app
     */
    private String status;

    /**
     * timestamp when transition to ACTIVE status completed
     */
    private DateTime activated;

    /**
     * enabled app features
     */
    private String[] features;

    /**
     * authentication mode of app
     */
    private String signOnMode;

    private Accessibility accessibility;

    private Visibility visibility;

    private AppCredentials credentials;

    private Settings settings;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

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
     * Gets name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name
     */
    public void setName(String val) {
        this.name = val;
    }

    /**
     * Gets label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets label
     */
    public void setLabel(String val) {
        this.label = val;
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
     * Gets features
     */
    public String[] getFeatures() {
        return this.features;
    }

    /**
     * Sets features
     */
    public void setFeatures(String[] val) {
        this.features = val;
    }

    /**
     * Gets signOnMode
     */
    public String getSignOnMode() {
        return this.signOnMode;
    }

    /**
     * Sets signOnMode
     */
    public void setSignOnMode(String val) {
        this.signOnMode = val;
    }

    /**
     * Gets accessibility
     */
    public Accessibility getAccessibility() {
        return this.accessibility;
    }

    /**
     * Sets accessibility
     */
    public void setAccessibility(Accessibility val) {
        this.accessibility = val;
    }

    /**
     * Gets visibility
     */
    public Visibility getVisibility() {
        return this.visibility;
    }

    /**
     * Sets visibility
     */
    public void setVisibility(Visibility val) {
        this.visibility = val;
    }

    /**
     * Gets credentials
     */
    public AppCredentials getCredentials() {
        return this.credentials;
    }

    /**
     * Sets credentials
     */
    public void setCredentials(AppCredentials val) {
        this.credentials = val;
    }

    /**
     * Gets settings
     */
    public Settings getSettings() {
        return this.settings;
    }

    /**
     * Sets settings
     */
    public void setSettings(Settings val) {
        this.settings = val;
    }

    /**
     * Gets links
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets links
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Gets embedded
     */
    public Map<String, Object> getEmbedded() {
        return this.embedded;
    }

    /**
     * Sets embedded
     */
    public void setEmbedded(Map<String, Object> val) {
        this.embedded = val;
    }
}
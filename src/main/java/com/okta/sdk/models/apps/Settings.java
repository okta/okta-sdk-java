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

public class Settings extends ApiObject {

    /**
     * The settings for the app.
     */
    @JsonProperty("app")
    private AppSettings appSettings;

    /**
     * Notifications for the app.
     */
    private Notifications notifications;

    /**
     * Whether to provision manually.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private Boolean manualProvisioning;

    /**
     * Return the appSettings.
     * @return {@link AppSettings}
     */
    public AppSettings getAppSettings() {
        return appSettings;
    }

    /**
     * Set the appSettings.
     * @param val {@link AppSettings}
     */
    public void setAppSettings(AppSettings val) {
        this.appSettings = val;
    }

    /**
     * Return the notifications.
     * @return {@link Notifications}
     */
    public Notifications getNotifications() {
        return notifications;
    }

    /**
     * Set the notifications.
     * @param val {@link Notifications}
     */
    public void setNotifications(Notifications val) {
        this.notifications = val;
    }

    /**
     * Return the manualProvisioning toggle.
     * @return {@link Boolean}
     */
    public Boolean getManualProvisioning() {
        return manualProvisioning;
    }

    /**
     * Set the manualProvisioning toggle.
     * @param val {@link Boolean}
     */
    public void setManualProvisioning(Boolean val) {
        this.manualProvisioning = val;
    }
}

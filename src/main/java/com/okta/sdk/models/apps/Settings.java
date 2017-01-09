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

public class Settings extends ApiObject {

    /**
     * The settings for the app
     */
    @JsonProperty("app")
    private AppSettings appSettings;

    private Notifications notifications;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private Boolean manualProvisioning;

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public Boolean getManualProvisioning() {
        return manualProvisioning;
    }

    public void setManualProvisioning(Boolean manualProvisioning) {
        this.manualProvisioning = manualProvisioning;
    }
}

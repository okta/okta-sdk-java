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

import com.okta.sdk.framework.ApiObject;

public class Accessibility extends ApiObject {

    /**
     * Enable self-service application assignment
     */
    private Boolean selfService;

    /**
     * Custom error page for this application
     */
    private String errorRedirectUrl;

    /**
     * Gets selfService
     */
    public Boolean getSelfService() {
        return this.selfService;
    }

    /**
     * Sets selfService
     */
    public void setSelfService(Boolean val) {
        this.selfService = val;
    }

    /**
     * Gets errorRedirectUrl
     */
    public String getErrorRedirectUrl() {
        return this.errorRedirectUrl;
    }

    /**
     * Sets errorRedirectUrl
     */
    public void setErrorRedirectUrl(String val) {
        this.errorRedirectUrl = val;
    }
}
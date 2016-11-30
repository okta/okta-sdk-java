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

package com.okta.sdk.models.auth;

import com.okta.sdk.framework.ApiObject;

public class OrgAnonymousInfo extends ApiObject {

    private String name;

    private String supportPhoneNumber;

    private String technicalContact;

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
     * Gets supportPhoneNumber
     */
    public String getSupportPhoneNumber() {
        return this.supportPhoneNumber;
    }

    /**
     * Sets supportPhoneNumber
     */
    public void setSupportPhoneNumber(String val) {
        this.supportPhoneNumber = val;
    }

    /**
     * Gets technicalContact
     */
    public String getTechnicalContact() {
        return this.technicalContact;
    }

    /**
     * Sets technicalContact
     */
    public void setTechnicalContact(String val) {
        this.technicalContact = val;
    }
}
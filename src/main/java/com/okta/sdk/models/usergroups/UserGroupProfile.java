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

package com.okta.sdk.models.usergroups;

import com.okta.sdk.framework.ApiObject;

public class UserGroupProfile extends ApiObject {

    /**
     * name of the group
     */
    private String name;

    /**
     * description of the group
     */
    private String description;

    /**
     * pre-windows 2000 name of the windows group
     */
    private String samAccountName;

    /**
     * the distinguished name of the windows group
     */
    private String dn;

    /**
     * fully-qualified name of the windows group
     */
    private String windowsDomainQualifiedName;

    /**
     * base-64 encoded GUID (objectGUID) of the windows group
     */
    private String externalId;

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
     * Gets description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets description
     */
    public void setDescription(String val) {
        this.description = val;
    }

    /**
     * Gets samAccountName
     */
    public String getSamAccountName() {
        return this.samAccountName;
    }

    /**
     * Sets samAccountName
     */
    public void setSamAccountName(String val) {
        this.samAccountName = val;
    }

    /**
     * Gets dn
     */
    public String getDn() {
        return this.dn;
    }

    /**
     * Sets dn
     */
    public void setDn(String val) {
        this.dn = val;
    }

    /**
     * Gets windowsDomainQualifiedName
     */
    public String getWindowsDomainQualifiedName() {
        return this.windowsDomainQualifiedName;
    }

    /**
     * Sets windowsDomainQualifiedName
     */
    public void setWindowsDomainQualifiedName(String val) {
        this.windowsDomainQualifiedName = val;
    }

    /**
     * Gets externalId
     */
    public String getExternalId() {
        return this.externalId;
    }

    /**
     * Sets externalId
     */
    public void setExternalId(String val) {
        this.externalId = val;
    }
}
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

package com.okta.sdk.models.usergroups;

import com.okta.sdk.framework.ApiObject;

public class UserGroupProfile extends ApiObject {

    /**
     * Name of the group.
     */
    private String name;

    /**
     * Description of the group.
     */
    private String description;

    /**
     * Pre-windows 2000 name of the windows group.
     */
    private String samAccountName;

    /**
     * The distinguished name of the windows group.
     */
    private String dn;

    /**
     * Fully-qualified name of the windows group.
     */
    private String windowsDomainQualifiedName;

    /**
     * Base-64 encoded GUID (objectGUID) of the windows group.
     */
    private String externalId;

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
     * Returns the description.
     * @return {@link String}
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     * @param val {@link String}
     */
    public void setDescription(String val) {
        this.description = val;
    }

    /**
     * Returns the samAccountName.
     * @return {@link String}
     */
    public String getSamAccountName() {
        return this.samAccountName;
    }

    /**
     * Sets the samAccountName.
     * @param val {@link String}
     */
    public void setSamAccountName(String val) {
        this.samAccountName = val;
    }

    /**
     * Returns the dn.
     * @return {@link String}
     */
    public String getDn() {
        return this.dn;
    }

    /**
     * Sets the dn.
     * @param val {@link String}
     */
    public void setDn(String val) {
        this.dn = val;
    }

    /**
     * Returns the windowsDomainQualifiedName.
     * @return {@link String}
     */
    public String getWindowsDomainQualifiedName() {
        return this.windowsDomainQualifiedName;
    }

    /**
     * Sets the windowsDomainQualifiedName.
     * @param val {@link String}
     */
    public void setWindowsDomainQualifiedName(String val) {
        this.windowsDomainQualifiedName = val;
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
}

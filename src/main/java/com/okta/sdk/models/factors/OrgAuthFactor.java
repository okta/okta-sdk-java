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

package com.okta.sdk.models.factors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;

import java.util.Map;

public class OrgAuthFactor extends ApiObject {

    /**
     * Unique ID of the org auth factor.
     */
    private String id;

    /**
     * Type of the factor.
     */
    private String factorType;

    /**
     * Provider of the factor.
     */
    private String provider;

    /**
     * Status of the factor.
     */
    private String status;

    /**
     * HAL links of the factor object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded of the factor object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Returns the unique ID.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the unique ID.
     * @param val {@link String}
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Returns the factorType.
     * @return {@link String}
     */
    public String getFactorType() {
        return this.factorType;
    }

    /**
     * Sets the factorType.
     * @param val {@link String}
     */
    public void setFactorType(String val) {
        this.factorType = val;
    }

    /**
     * Returns the provider.
     * @return {@link String}
     */
    public String getProvider() {
        return this.provider;
    }

    /**
     * Sets the provider.
     * @param val {@link String}
     */
    public void setProvider(String val) {
        this.provider = val;
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

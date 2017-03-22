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
import org.joda.time.DateTime;

import java.util.Map;

public class Factor extends ApiObject {

    /**
     * Unique key for factor.
     */
    private String id;

    /**
     * Type of factor.
     */
    private String factorType;

    /**
     * Factor of the provider.
     */
    private String provider;

    /**
     * Status of the factor.
     */
    private String status;

    /**
     * Timestamp when factor was created.
     */
    private DateTime created;

    /**
     * Timestamp when factor was last updated.
     */
    private DateTime lastUpdated;

    /**
     * Profile of a supported factor.
     */
    private Map<String, Object> profile;

    /**
     * HAL links of the factor.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded of the factor.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Returns the unique id.
     * @return {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the uniqu id.
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
     * Returns the profile.
     * @return {@link Map}
     */
    public Map<String, Object> getProfile() {
        return this.profile;
    }

    /**
     * Sets the profile.
     * @param val {@link Map}
     */
    public void setProfile(Map<String, Object> val) {
        this.profile = val;
    }

    /**
     * Returns the links.
     * @return {@link Map}
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets the links.
     * @param val {@link Map}
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Gets the embedded HAL.
     * @return {@link Map}
     */
    public Map<String, Object> getEmbedded() {
        return this.embedded;
    }

    /**
     * Sets the embedded HAL.
     * @param val {@link Map}
     */
    public void setEmbedded(Map<String, Object> val) {
        this.embedded = val;
    }
}

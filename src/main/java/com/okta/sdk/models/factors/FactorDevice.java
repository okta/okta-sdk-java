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

public class FactorDevice extends ApiObject {

    /**
     * Unique ID of the factor device.
     */
    private String id;

    /**
     * Status of the factor device.
     */
    private String status;

    /**
     * HAL links of the factor device.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded of the factor device.
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
     * Sets the unique id.
     * @param val {@link String}
     */
    public void setId(String val) {
        this.id = val;
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
     * Returns the links HAL.
     * @return {@link Map}
     */
    public Map<String, LinksUnion> getLinks() {
        return this.links;
    }

    /**
     * Sets the links HAL.
     * @param val {@link Map}
     */
    public void setLinks(Map<String, LinksUnion> val) {
        this.links = val;
    }

    /**
     * Returns the embedded HAL.
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

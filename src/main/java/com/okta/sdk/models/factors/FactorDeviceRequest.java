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

package com.okta.sdk.models.factors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;
import com.okta.sdk.models.links.LinksUnion;

import java.util.Map;

public class FactorDeviceRequest extends ApiObject {

    private Verification verify;

    private Map<String, Object> profile;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Gets verify
     */
    public Verification getVerify() {
        return this.verify;
    }

    /**
     * Sets verify
     */
    public void setVerify(Verification val) {
        this.verify = val;
    }

    /**
     * Gets profile
     */
    public Map<String, Object> getProfile() {
        return this.profile;
    }

    /**
     * Sets profile
     */
    public void setProfile(Map<String, Object> val) {
        this.profile = val;
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
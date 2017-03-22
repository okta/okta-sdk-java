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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.okta.sdk.framework.ApiObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactorDeviceEnrollRequest extends ApiObject {

    /**
     * Verification object of the factor device enrollment request.
     */
    private Verification verify;

    /**
     * Profile of the factor device enrollment request.
     */
    private Map<String, Object> profile;

    /**
     * HAL embedded of the factor device enrollment request.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Returns the verify object.
     * @return {@link Verification}
     */
    public Verification getVerify() {
        return this.verify;
    }

    /**
     * Sets the verify object.
     * @param val {@link Verification}
     */
    public void setVerify(Verification val) {
        this.verify = val;
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
     * Returns a list of all factors to enroll.
     * @return {@link List}
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
     public List<FactorEnrollRequest> getFactorsToEnroll() {
        return (List<FactorEnrollRequest>) embedded.get("factors");
    }

    /**
     * Sets the factors to enroll.
     * @param factorsToEnroll {@link List}
     */
    @JsonIgnore
    public void setFactorsToEnroll(List<FactorEnrollRequest> factorsToEnroll) {
        if (embedded == null) {
            embedded = new HashMap<String, Object>();
        }
        embedded.put("factors", factorsToEnroll);
    }
}

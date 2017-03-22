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

import com.okta.sdk.framework.ApiObject;

import java.util.Map;

public class FactorEnrollRequest extends ApiObject {

    /**
     * Type of factor for the factor enrollment request.
     */
    private String factorType;

    /**
     * Provider for the factor enrollment request.
     */
    private String provider;

    /**
     * Verification object for the factor enroll request
     */
    private Verification verify;

    private Map<String, Object> profile;

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
     * Returns the verification object.
     * @return {@link Verification}
     */
    public Verification getVerify() {
        return this.verify;
    }

    /**
     * Sets the verification object.
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
}

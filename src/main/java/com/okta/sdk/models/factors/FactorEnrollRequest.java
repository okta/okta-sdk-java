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

import com.okta.sdk.framework.ApiObject;

import java.util.Map;

public class FactorEnrollRequest extends ApiObject {

    private String factorType;

    private String provider;

    private Verification verify;

    private Map<String, Object> profile;

    /**
     * Gets factorType
     */
    public String getFactorType() {
        return this.factorType;
    }

    /**
     * Sets factorType
     */
    public void setFactorType(String val) {
        this.factorType = val;
    }

    /**
     * Gets provider
     */
    public String getProvider() {
        return this.provider;
    }

    /**
     * Sets provider
     */
    public void setProvider(String val) {
        this.provider = val;
    }

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
}
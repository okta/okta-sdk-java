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

public class FactorCatalogEntry extends ApiObject {

    /**
     * Type of the factor entry.
     */
    private String factorType;

    /**
     * Provider of the factor entry.
     */
    private String provider;

    /**
     * Status of the factor entry.
     */
    private String status;

    /**
     * Enrollment of the factor entry.
     */
    private String enrollment;

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
        return status;
    }

    /**
     * Sets the status.
     * @param val {@link String}
     */
    public void setStatus(String val) {
        this.status = val;
    }

    /**
     * Returns the enrollment.
     * @return {@link String}
     */
    public String getEnrollment() {
        return enrollment;
    }

    /**
     * Sets the enrollment.
     * @param val {@link String}
     */
    public void setEnrollment(String val) {
        this.enrollment = val;
    }
}

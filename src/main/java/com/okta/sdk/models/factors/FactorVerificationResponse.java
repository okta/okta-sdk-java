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

public class FactorVerificationResponse extends ApiObject {

    /**
     * Result of the factor response.
     */
    private String factorResult;

    /**
     * Current state of the factor.
     */
    private String state;

    /**
     * Result message.
     */
    private String factorResultMessage;

    /**
     * Time when factor expires.
     */
    private DateTime expiryTime;

    /**
     * HAL Links of the verification response object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_links")
    private Map<String, LinksUnion> links;

    /**
     * HAL embedded of the verification response object.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonProperty(value = "_embedded")
    private Map<String, Object> embedded;

    /**
     * Returns the factorResult.
     * @return {@link String}
     */
    public String getFactorResult() {
        return this.factorResult;
    }

    /**
     * Sets the factorResult.
     * @param val {@link String}
     */
    public void setFactorResult(String val) {
        this.factorResult = val;
    }

    /**
     * Returns the state.
     * @return {@link String}
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets the state.
     * @param val {@link String}
     */
    public void setState(String val) {
        this.state = val;
    }

    /**
     * Returns the factorResultMessage.
     * @return {@link String}
     */
    public String getFactorResultMessage() {
        return this.factorResultMessage;
    }

    /**
     * Sets the factorResultMessage.
     * @param val {@link String}
     */
    public void setFactorResultMessage(String val) {
        this.factorResultMessage = val;
    }

    /**
     * Returns the expiryTime.
     * @return {@link DateTime}
     */
    public DateTime getExpiryTime() {
        return this.expiryTime;
    }

    /**
     * Sets the expiryTime.
     * @param val {@link DateTime}
     */
    public void setExpiryTime(DateTime val) {
        this.expiryTime = val;
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

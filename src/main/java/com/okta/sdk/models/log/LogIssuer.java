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

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogIssuer extends ApiObject {

    /**
     * Unique ID of the issuer.
     */
    private final String id;

    /**
     * The type of the issuer.
     */
    private final String type;

    /**
     * Constructor for LogIssuer
     * @param id {@link String}
     * @param type {@link String}
     */
    @JsonCreator
    public LogIssuer(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type
    ) {
        this.id = id;
        this.type = type;
    }

    /**
     * Returns the id.
     * @return {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the type.
     * @return {@link String}
     */
    public String getType() {
        return type;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    /**
     * Overrides the method equals.
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogIssuer)) {
            return false;
        }
        final LogIssuer other = (LogIssuer) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.type, other.type);
    }
}

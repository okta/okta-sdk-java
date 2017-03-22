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
import com.google.common.collect.ImmutableMap;
import com.okta.sdk.framework.ApiObject;

import java.util.Map;
import java.util.Objects;

public final class LogTransaction extends ApiObject {

    /**
     * The type of the transaction.
     */
    private final String type;

    /**
     * Transaction id.
     */
    private final String id;

    /**
     * Transaction details.
     */
    private final Map<String, Object> detail;

    /**
     * Constructor for LogTransaction
     * @param type {@link String}
     * @param id {@link String}
     * @param detail {@link Map}
     */
    @JsonCreator
    public LogTransaction(
            @JsonProperty("type") String type,
            @JsonProperty("id") String id,
            @JsonProperty("detail") Map<String, Object> detail
    ) {
        this.type = type;
        this.id = id;
        if (detail != null) {
            this.detail = ImmutableMap.copyOf(detail);
        } else {
            this.detail = null;
        }
    }

    /**
     * Returns the type.
     * @return {@link String}
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the id.
     * @return {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the detail.
     * @return {@link Map}
     */
    public Map<String, Object> getDetail() {
        return detail;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, id, detail);
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
        if (obj == null || !(obj instanceof LogTransaction)) {
            return false;
        }
        final LogTransaction other = (LogTransaction) obj;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.detail, other.detail);
    }
}

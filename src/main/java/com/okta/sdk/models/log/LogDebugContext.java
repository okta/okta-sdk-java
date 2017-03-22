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

public final class LogDebugContext extends ApiObject {

    /**
     * Map of debug data keys to debug data values.
     */
    private final Map<String, Object> debugData;

    /**
     * Constructor for LogDebugContext.
     * @param debugData {@link Map}
     */
    @JsonCreator
    public LogDebugContext(
            @JsonProperty("debugData") Map<String, Object> debugData
    ) {
        if (debugData != null) {
            this.debugData = ImmutableMap.copyOf(debugData);
        } else {
            this.debugData = null;
        }

    }

    /**
     * Returns the debugData.
     * @return {@link Map}
     */
    public Map<String, Object> getDebugData() {
        return debugData;
    }

    /**
     * Hashes the object.
     * @return {@link Object}
     */
    @Override
    public int hashCode() {
        return Objects.hash(debugData);
    }

    /**
     * Overriding method for equals.
     * @param obj {@link Object}
     * @return {@link Boolean}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogDebugContext)) {
            return false;
        }
        final LogDebugContext other = (LogDebugContext) obj;
        return Objects.equals(this.debugData, other.debugData);
    }
}

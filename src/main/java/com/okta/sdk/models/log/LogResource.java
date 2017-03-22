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

public final class LogResource extends ApiObject {

    /**
     * The resource id.
     */
    private final String id;

    /**
     * The type of the resource.
     */
    private final String type;

    /**
     * Alternative id of the resource.
     */
    private final String alternateId;

    /**
     * The display name of the resource.
     */
    private final String displayName;

    /**
     * Resource details.
     */
    private final Map<String, String> detailEntry;

    /**
     * Constructor for LogResource
     * @param id {@link String}
     * @param type {@link String}
     * @param alternateId {@link String}
     * @param displayName {@link String}
     * @param detailEntry {@link Map}
     */
    @JsonCreator
    public LogResource(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("alternateId") String alternateId,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("detailEntry") Map<String, String> detailEntry
    ) {
        this.id = id;
        this.type = type;
        this.alternateId = alternateId;
        this.displayName = displayName;
        if (detailEntry != null) {
            this.detailEntry = ImmutableMap.copyOf(detailEntry);
        } else {
            this.detailEntry = null;
        }
    }

    /**
     * Returns the ID.
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
     * Returns the alternateId.
     * @return {@link String}
     */
    public String getAlternateId() {
        return alternateId;
    }

    /**
     * Returns the displayName.
     * @return {@link String}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the detailEntry.
     * @return {@link Map}
     */
    public Map<String, String> getDetailEntry() {
        return detailEntry;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, type, alternateId, displayName, detailEntry);
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
        if (obj == null || !(obj instanceof LogResource)) {
            return false;
        }
        final LogResource other = (LogResource) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.alternateId, other.alternateId)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.detailEntry, other.detailEntry);
    }
}

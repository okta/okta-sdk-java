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

package com.okta.sdk.framework;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiObject {

    /**
     * Store properties that could not be mapped to an object Model.
     */
    private Map<String, Object> unmapped = new HashMap<String, Object>();

    /**
     * Return all unmapped properties not captured by the object Model.
     * @return {@link Map}
     */
    @JsonAnyGetter
    public Map<String, Object> getUnmapped() {
        return unmapped;
    }

    /**
     * Manually set unmapped value.
     *
     * @param key {@link String}
     * @param value {@link Object}
     */
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // TODO: log a warning
        unmapped.put(key, value);
    }
}

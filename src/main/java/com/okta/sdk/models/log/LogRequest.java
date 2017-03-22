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
import com.google.common.collect.ImmutableList;
import com.okta.sdk.framework.ApiObject;

import java.util.List;
import java.util.Objects;

public final class LogRequest extends ApiObject {

    /**
     * Chain of Ip data.
     */
    private final List<LogIpAddress> ipChain;

    /**
     * Constructor for LogRequest
     * @param ipChain {@link List}
     */
    @JsonCreator
    public LogRequest(
            @JsonProperty("ipChain") List<LogIpAddress> ipChain
    ) {
        if (ipChain != null) {
            this.ipChain = ImmutableList.copyOf(ipChain);
        } else {
            this.ipChain = null;
        }
    }

    /**
     * Returns the ipChain.
     * @return {@link List}
     */
    public List<LogIpAddress> getIpChain() {
        return ipChain;
    }

    /**
     * Hashes the object.
     * @return {@link Integer}
     */
    @Override
    public int hashCode() {
        return Objects.hash(ipChain);
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
        if (obj == null || !(obj instanceof LogRequest)) {
            return false;
        }
        final LogRequest other = (LogRequest) obj;
        return Objects.equals(this.ipChain, other.ipChain);
    }
}

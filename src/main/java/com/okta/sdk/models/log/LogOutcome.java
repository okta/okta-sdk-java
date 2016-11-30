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

package com.okta.sdk.models.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okta.sdk.framework.ApiObject;

import java.util.Objects;

public final class LogOutcome extends ApiObject {

    /**
     * Result of the action
     */
    private final String result;

    /**
     * The reason for the outcome
     */
    private final String reason;

    @JsonCreator
    public LogOutcome(
            @JsonProperty("result") String result,
            @JsonProperty("reason") String reason
    ) {
        this.result = result;
        this.reason = reason;
    }

    public String getResult() {
        return result;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, reason);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LogOutcome)) {
            return false;
        }
        final LogOutcome other = (LogOutcome) obj;
        return Objects.equals(this.result, other.result)
                && Objects.equals(this.reason, other.reason);
    }
}

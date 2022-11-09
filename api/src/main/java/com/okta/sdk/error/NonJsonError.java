/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.error;

import java.util.List;
import java.util.Map;

public class NonJsonError implements Error {

    private final String message;

    public NonJsonError(String message) {
        this.message = message;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public List<ErrorCause> getCauses() {
        return null;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return null;
    }
}

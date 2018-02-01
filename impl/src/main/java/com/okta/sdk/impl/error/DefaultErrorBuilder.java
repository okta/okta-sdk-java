/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.error;

import com.okta.sdk.error.Error;
import com.okta.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.5.0
 */
public class DefaultErrorBuilder {

    private final Map<String, Object> errorProperties;

    public DefaultErrorBuilder(int status) {
        Assert.notNull(status, "status cannot be null.");
        errorProperties = new HashMap<String, Object>();
        errorProperties.put(DefaultError.STATUS.getName(), status);
    }

    public static DefaultErrorBuilder status(Integer status) {
        return new DefaultErrorBuilder(status);
    }


    public DefaultErrorBuilder code(String code) {
        this.errorProperties.put(DefaultError.CODE.getName(), code);
        return this;
    }

    public DefaultErrorBuilder message(String message) {
        this.errorProperties.put(DefaultError.MESSAGE.getName(), message);
        return this;
    }

    public DefaultErrorBuilder causes(Map<String, String> causes) {
        this.errorProperties.put(DefaultError.CAUSES.getName(), causes);
        return this;
    }

    public DefaultErrorBuilder errorId(String requestId) {
        this.errorProperties.put(DefaultError.ERROR_ID.getName(), requestId);
        return this;
    }

    public DefaultErrorBuilder headers(Map<String, String[]> headers) {
        this.errorProperties.put(DefaultError.HEADERS.getName(), headers);
        return this;
    }

    public Error build() {
        for (Object value : errorProperties.values()) {
            Assert.notNull(value);
        }
        return new DefaultError(errorProperties);
    }
}

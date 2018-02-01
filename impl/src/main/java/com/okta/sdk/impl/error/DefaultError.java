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
import com.okta.sdk.error.ErrorCause;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.IntegerProperty;
import com.okta.sdk.impl.resource.ListProperty;
import com.okta.sdk.impl.resource.MapProperty;
import com.okta.sdk.impl.resource.Property;
import com.okta.sdk.impl.resource.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @since 0.5.0
 */
public class DefaultError extends AbstractResource implements Error {

    static final long serialVersionUID = 42L;

    static final IntegerProperty STATUS = new IntegerProperty("status");
    static final StringProperty CODE = new StringProperty("errorCode");
    static final StringProperty MESSAGE = new StringProperty("errorSummary");
    static final ListProperty CAUSES = new ListProperty("errorCauses");
    static final MapProperty HEADERS = new MapProperty("errorHeaders");
    public static final StringProperty ERROR_ID = new StringProperty("errorId");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            STATUS, CODE, MESSAGE, CAUSES, ERROR_ID, HEADERS
    );

    public DefaultError(Map<String, Object> body) {
        super(null, body);
    }

    // Needed for this class to be serializable
    public DefaultError() {
        super(null, null);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public int getStatus() {
        return getInt(STATUS);
    }

    public DefaultError setStatus(int status) {
        setProperty(STATUS, status);
        return this;
    }

    @Override
    public String getCode() {
        return getString(CODE);
    }

    public DefaultError setCode(String code) {
        setProperty(CODE, code);
        return this;
    }

    @Override
    public String getMessage() {
        return getString(MESSAGE);
    }

    public DefaultError setMessage(String message) {
        setProperty(MESSAGE, message);
        return this;
    }

    @Override
    public String getId() {
       return getString(ERROR_ID);
    }

    public DefaultError setId(String requestId) {
        setProperty(ERROR_ID, requestId);
        return this;
    }

    @Override
    public List<ErrorCause> getCauses() {
        List<ErrorCause> results = new ArrayList<>();
        Object rawProp = getProperty(CAUSES.getName());
        if (rawProp instanceof List) {
            ((List<Map<String, Object>>) rawProp).forEach(causeMap ->
                    results.add(new DefaultErrorCause(getDataStore(), causeMap)));
        }
        return Collections.unmodifiableList(results);
    }

    public DefaultError setCauses(Map<String, String> causes) {
        setProperty(CAUSES, causes);
        return this;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return getMap(HEADERS);
    }

    public DefaultError setHeaders(Map<String, List<String>> headers) {
        setProperty(HEADERS, headers);
        return this;
    }
}

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
package com.okta.sdk.impl.ds;

import com.okta.sdk.impl.http.CanonicalUri;
import com.okta.sdk.impl.http.HttpHeaders;
import com.okta.sdk.resource.Resource;

import java.util.Map;

public class DefaultResourceDataRequest extends DefaultResourceMessage implements ResourceDataRequest {

    private final CanonicalUri parentUri;
    private final Class<? extends Resource> parentResourceClass;

    public DefaultResourceDataRequest(ResourceAction action, CanonicalUri uri, Class<? extends Resource> resourceClass, Map<String,Object> data) {
        this(action, uri, null, resourceClass, null, data, null);
    }

    public DefaultResourceDataRequest(ResourceAction action, CanonicalUri uri, CanonicalUri parrentUri, Class<? extends Resource> resourceClass, Class<? extends Resource> parentResourceClass, Map<String,Object> data, HttpHeaders customHeaders) {
        super(action, uri, resourceClass, data, customHeaders);
        this.parentUri = parrentUri;
        this.parentResourceClass = parentResourceClass;
    }

    @Override
    public CanonicalUri getParentUri() {
        return parentUri;
    }

    @Override
    public Class<? extends Resource> getParentResourceClass() {
        return parentResourceClass;
    }
}
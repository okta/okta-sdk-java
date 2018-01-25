/*
 * Copyright 2014 Stormpath, Inc.
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

/**
 * This class represents a request to obtain a resource and its related data.
 */
public interface ResourceDataRequest extends ResourceMessage {

    ResourceAction getAction();

    /**
     * Returns the {@code HttpHeaders HttpHeaders} instance associated with a {@link ResourceDataRequest ResourceDataRequest}
     * @return the {@link HttpHeaders HttpHeaders} instance representing the Http Headers added to a data request.
     *
     */
    HttpHeaders getHttpHeaders();

    CanonicalUri getParentUri();

    Class<? extends Resource> getParentResourceClass();
}

/*
 * Copyright 2017 Okta
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
package com.okta.sdk.resource.group;

import com.okta.commons.lang.Classes;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupApi;
import org.openapitools.client.model.Group;

public interface GroupBuilder {

    static GroupBuilder instance() {
        return Classes.newInstance("com.okta.sdk.impl.resource.DefaultGroupBuilder");
    }

    GroupBuilder setName(String name);

    GroupBuilder setDescription(String description);

    Group buildAndCreate(GroupApi client) throws ApiException;
}

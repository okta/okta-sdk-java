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
package com.okta.sdk.impl.resource;

import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.group.GroupBuilder;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupApi;
import org.openapitools.client.model.Group;
import org.openapitools.client.model.GroupProfile;

public class DefaultGroupBuilder implements GroupBuilder {

    private String name;
    private String description;

    @Override
    public GroupBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GroupBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Group buildAndCreate(GroupApi client) throws ApiException {

        Group group = new Group();
        GroupProfile groupProfile = new GroupProfile();
        groupProfile.setName(name);
        if (Strings.hasText(description)) groupProfile.setDescription(description);
        group.setProfile(groupProfile);

        return client.createGroup(group);
    }
}
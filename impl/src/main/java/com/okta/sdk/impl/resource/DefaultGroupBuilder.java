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
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.api.GroupApi;
import com.okta.sdk.resource.model.AddGroupRequest;
import com.okta.sdk.resource.model.Group;
import com.okta.sdk.resource.model.OktaUserGroupProfile;

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

        OktaUserGroupProfile oktaUserGroupProfile = new OktaUserGroupProfile();
        oktaUserGroupProfile.setName(name);
        if (Strings.hasText(description)) {
            oktaUserGroupProfile.setDescription(description);
        }

        AddGroupRequest addGroupRequest = new AddGroupRequest();
        addGroupRequest.setProfile(oktaUserGroupProfile);

        return client.addGroup(addGroupRequest);
    }
}
/*
 * Copyright 2024-Present Okta, Inc.
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
package com.okta.sdk.impl.deserializer;

import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

/**
 * Custom JSON deserializer for role assignment responses.
 * 
 * <p>The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * ListGroupAssignedRoles200ResponseInner, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.</p>
 * 
 * <p>This deserializer bypasses the polymorphic handling and directly deserializes
 * into ListGroupAssignedRoles200ResponseInner which has all the fields needed.</p>
 * 
 * @see AbstractRoleAssignmentDeserializer
 */
public class RoleAssignmentDeserializer extends AbstractRoleAssignmentDeserializer<ListGroupAssignedRoles200ResponseInner> {

    private static final long serialVersionUID = 1L;

    public RoleAssignmentDeserializer() {
        super(ListGroupAssignedRoles200ResponseInner.class);
    }

    @Override
    protected ListGroupAssignedRoles200ResponseInner createInstance() {
        return new ListGroupAssignedRoles200ResponseInner();
    }

    @Override
    protected void setAssignmentType(ListGroupAssignedRoles200ResponseInner response, RoleAssignmentType assignmentType) {
        response.setAssignmentType(assignmentType);
    }

    @Override
    protected void setStatus(ListGroupAssignedRoles200ResponseInner response, LifecycleStatus status) {
        response.setStatus(status);
    }

    @Override
    protected void setType(ListGroupAssignedRoles200ResponseInner response, RoleType type) {
        response.setType(type);
    }

    @Override
    protected void setEmbedded(ListGroupAssignedRoles200ResponseInner response, StandardRoleEmbedded embedded) {
        response.setEmbedded(embedded);
    }

    @Override
    protected void setLinks(ListGroupAssignedRoles200ResponseInner response, LinksCustomRoleResponse links) {
        response.setLinks(links);
    }
}

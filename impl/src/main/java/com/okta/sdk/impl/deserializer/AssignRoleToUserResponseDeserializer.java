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

import com.okta.sdk.resource.model.AssignRoleToUser201Response;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

/**
 * Custom JSON deserializer for AssignRoleToUser201Response.
 * 
 * <p>The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * AssignRoleToUser201Response, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.</p>
 * 
 * @see AbstractRoleAssignmentDeserializer
 */
public class AssignRoleToUserResponseDeserializer extends AbstractRoleAssignmentDeserializer<AssignRoleToUser201Response> {

    private static final long serialVersionUID = 1L;

    public AssignRoleToUserResponseDeserializer() {
        super(AssignRoleToUser201Response.class);
    }

    @Override
    protected AssignRoleToUser201Response createInstance() {
        return new AssignRoleToUser201Response();
    }

    @Override
    protected void setAssignmentType(AssignRoleToUser201Response response, RoleAssignmentType assignmentType) {
        response.setAssignmentType(assignmentType);
    }

    @Override
    protected void setStatus(AssignRoleToUser201Response response, LifecycleStatus status) {
        response.setStatus(status);
    }

    @Override
    protected void setType(AssignRoleToUser201Response response, RoleType type) {
        response.setType(type);
    }

    @Override
    protected void setEmbedded(AssignRoleToUser201Response response, StandardRoleEmbedded embedded) {
        response.setEmbedded(embedded);
    }

    @Override
    protected void setLinks(AssignRoleToUser201Response response, LinksCustomRoleResponse links) {
        response.setLinks(links);
    }
}

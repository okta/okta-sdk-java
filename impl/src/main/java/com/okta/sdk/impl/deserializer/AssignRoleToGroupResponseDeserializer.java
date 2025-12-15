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

import com.okta.sdk.resource.model.AssignRoleToGroup200Response;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

/**
 * Custom JSON deserializer for AssignRoleToGroup200Response.
 * 
 * <p>The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * AssignRoleToGroup200Response, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.</p>
 * 
 * @see AbstractRoleAssignmentDeserializer
 */
public class AssignRoleToGroupResponseDeserializer extends AbstractRoleAssignmentDeserializer<AssignRoleToGroup200Response> {

    private static final long serialVersionUID = 1L;

    public AssignRoleToGroupResponseDeserializer() {
        super(AssignRoleToGroup200Response.class);
    }

    @Override
    protected AssignRoleToGroup200Response createInstance() {
        return new AssignRoleToGroup200Response();
    }

    @Override
    protected void setAssignmentType(AssignRoleToGroup200Response response, RoleAssignmentType assignmentType) {
        response.setAssignmentType(assignmentType);
    }

    @Override
    protected void setStatus(AssignRoleToGroup200Response response, LifecycleStatus status) {
        response.setStatus(status);
    }

    @Override
    protected void setType(AssignRoleToGroup200Response response, RoleType type) {
        response.setType(type);
    }

    @Override
    protected void setEmbedded(AssignRoleToGroup200Response response, StandardRoleEmbedded embedded) {
        response.setEmbedded(embedded);
    }

    @Override
    protected void setLinks(AssignRoleToGroup200Response response, LinksCustomRoleResponse links) {
        response.setLinks(links);
    }
}

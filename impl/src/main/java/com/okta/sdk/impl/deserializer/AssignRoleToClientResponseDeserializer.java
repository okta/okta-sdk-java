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

import com.okta.sdk.resource.model.AssignRoleToClient200Response;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

/**
 * Custom JSON deserializer for AssignRoleToClient200Response.
 * 
 * <p>The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * AssignRoleToClient200Response, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.</p>
 * 
 * @see AbstractRoleAssignmentDeserializer
 */
public class AssignRoleToClientResponseDeserializer extends AbstractRoleAssignmentDeserializer<AssignRoleToClient200Response> {

    private static final long serialVersionUID = 1L;

    public AssignRoleToClientResponseDeserializer() {
        super(AssignRoleToClient200Response.class);
    }

    @Override
    protected AssignRoleToClient200Response createInstance() {
        return new AssignRoleToClient200Response();
    }

    @Override
    protected void setAssignmentType(AssignRoleToClient200Response response, RoleAssignmentType assignmentType) {
        response.setAssignmentType(assignmentType);
    }

    @Override
    protected void setStatus(AssignRoleToClient200Response response, LifecycleStatus status) {
        response.setStatus(status);
    }

    @Override
    protected void setType(AssignRoleToClient200Response response, RoleType type) {
        response.setType(type);
    }

    @Override
    protected void setEmbedded(AssignRoleToClient200Response response, StandardRoleEmbedded embedded) {
        response.setEmbedded(embedded);
    }

    @Override
    protected void setLinks(AssignRoleToClient200Response response, LinksCustomRoleResponse links) {
        response.setLinks(links);
    }
}

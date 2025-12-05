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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.LinksCustomRoleResponse;
import com.okta.sdk.resource.model.ListGroupAssignedRoles200ResponseInner;
import com.okta.sdk.resource.model.RoleAssignmentType;
import com.okta.sdk.resource.model.RoleType;
import com.okta.sdk.resource.model.StandardRoleEmbedded;

import java.io.IOException;

/**
 * Custom JSON deserializer for role assignment responses.
 * 
 * The Okta API uses the 'type' field as a discriminator for role assignments.
 * Since the generated StandardRole and CustomRole classes don't extend
 * ListGroupAssignedRoles200ResponseInner, Jackson's default polymorphic
 * deserialization fails with "not a subtype" errors.
 * 
 * This deserializer bypasses the polymorphic handling and directly deserializes
 * into ListGroupAssignedRoles200ResponseInner which has all the fields needed.
 */
public class RoleAssignmentDeserializer extends StdDeserializer<ListGroupAssignedRoles200ResponseInner> {

    private static final long serialVersionUID = 1L;

    public RoleAssignmentDeserializer() {
        this(null);
    }

    public RoleAssignmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ListGroupAssignedRoles200ResponseInner deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ListGroupAssignedRoles200ResponseInner response = new ListGroupAssignedRoles200ResponseInner();

        // Parse assignmentType
        if (node.has("assignmentType") && !node.get("assignmentType").isNull()) {
            response.setAssignmentType(RoleAssignmentType.fromValue(node.get("assignmentType").asText()));
        }

        // Parse status
        if (node.has("status") && !node.get("status").isNull()) {
            response.setStatus(LifecycleStatus.fromValue(node.get("status").asText()));
        }

        // Parse type
        if (node.has("type") && !node.get("type").isNull()) {
            response.setType(RoleType.fromValue(node.get("type").asText()));
        }

        // Parse _embedded
        if (node.has("_embedded") && !node.get("_embedded").isNull()) {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            StandardRoleEmbedded embedded = mapper.treeToValue(node.get("_embedded"), StandardRoleEmbedded.class);
            response.setEmbedded(embedded);
        }

        // Parse _links
        if (node.has("_links") && !node.get("_links").isNull()) {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            LinksCustomRoleResponse links = mapper.treeToValue(node.get("_links"), LinksCustomRoleResponse.class);
            response.setLinks(links);
        }

        return response;
    }
}

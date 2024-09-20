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
package com.okta.sdk.impl.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.model.GroupProfile;

import java.io.IOException;

public class GroupProfileSerializer extends StdSerializer<GroupProfile> {

    private static final long serialVersionUID = -7330836671206521399L;

    public GroupProfileSerializer() {
        this(null);
    }

    public GroupProfileSerializer(Class<GroupProfile> t) {
        super(t);
    }

    @Override
    public void serialize(GroupProfile groupProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        if (Strings.hasText(groupProfile.getName())) {
            jgen.writeStringField(GroupProfile.JSON_PROPERTY_NAME, groupProfile.getName());
        }

        if (Strings.hasText(groupProfile.getDescription())) {
            jgen.writeStringField(GroupProfile.JSON_PROPERTY_DESCRIPTION, groupProfile.getDescription());
        }

        jgen.writeEndObject();
    }
}

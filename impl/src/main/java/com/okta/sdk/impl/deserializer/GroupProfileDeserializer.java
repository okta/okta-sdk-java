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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.GroupProfile;

import java.io.IOException;
import java.util.Map;

public class GroupProfileDeserializer extends StdDeserializer<GroupProfile> {

    private static final long serialVersionUID = -1809919319486559586L;

    private final ObjectMapper mapper = new ObjectMapper();

    public GroupProfileDeserializer() {
        this(null);
    }

    public GroupProfileDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GroupProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        Map<String, Object> profileMap = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});

        GroupProfile groupProfile = new GroupProfile();

        for (Map.Entry<String, Object> entry : profileMap.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case GroupProfile.JSON_PROPERTY_NAME:
                    groupProfile.setName((String) value);
                    break;

                case GroupProfile.JSON_PROPERTY_DESCRIPTION:
                    groupProfile.setDescription((String) value);
                    break;

                case GroupProfile.JSON_PROPERTY_WINDOWS_DOMAIN_QUALIFIED_NAME:
                    groupProfile.setWindowsDomainQualifiedName((String) value);
                    break;

                case GroupProfile.JSON_PROPERTY_DN:
                    groupProfile.setDn((String) value);
                    break;

                case GroupProfile.JSON_PROPERTY_EXTERNAL_ID:
                    groupProfile.setExternalId((String) value);
                    break;

                case GroupProfile.JSON_PROPERTY_SAM_ACCOUNT_NAME:
                    groupProfile.setSamAccountName((String) value);
                    break;

                default:
                    break;
            }
        }

        return groupProfile;
    }
}

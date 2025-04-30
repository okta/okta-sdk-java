/*
 * Copyright 2022-Present Okta, Inc.
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
import com.okta.sdk.resource.model.OktaUserGroupProfile;

import java.io.IOException;
import java.util.Map;

public class OktaUserGroupProfileDeserializer extends StdDeserializer<OktaUserGroupProfile> {

    private static final long serialVersionUID = -6166716736969489408L;

    private final ObjectMapper mapper = new ObjectMapper();

    public OktaUserGroupProfileDeserializer() {
        this(null);
    }

    public OktaUserGroupProfileDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OktaUserGroupProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        Map<String, Object> profileMap = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});

        OktaUserGroupProfile oktaUserGroupProfile = new OktaUserGroupProfile();

        for (Map.Entry<String, Object> entry : profileMap.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case OktaUserGroupProfile.JSON_PROPERTY_NAME:
                    oktaUserGroupProfile.setName((String) value);
                    break;

                case OktaUserGroupProfile.JSON_PROPERTY_DESCRIPTION:
                    oktaUserGroupProfile.setDescription((String) value);
                    break;

                default:
                    oktaUserGroupProfile.getAdditionalProperties().put(key, value);
            }
        }

        return oktaUserGroupProfile;
    }
}

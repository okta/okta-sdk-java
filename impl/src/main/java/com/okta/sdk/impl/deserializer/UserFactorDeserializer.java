/*
 * Copyright 2023-Present Okta, Inc.
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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.okta.sdk.helper.HelperUtil;
import com.okta.sdk.resource.handler.ApiClient;
import com.okta.sdk.resource.model.UserFactor;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.io.IOException;

public class UserFactorDeserializer extends StdDeserializer<UserFactor> {

    private static final long serialVersionUID = -3329187612911501989L;

    private ObjectMapper objectMapper;

    public UserFactorDeserializer() {
        this(null);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.setDateFormat(ApiClient.buildDefaultDateFormat());
    }

    public UserFactorDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public UserFactor deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        UserFactor userFactor = objectMapper.convertValue(node, UserFactor.class);
        return objectMapper.convertValue(node, HelperUtil.getUserFactorType(userFactor));
    }
}

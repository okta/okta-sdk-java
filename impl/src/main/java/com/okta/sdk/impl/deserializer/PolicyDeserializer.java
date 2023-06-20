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
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.client.ApiClient;
import org.openapitools.client.model.*;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.io.IOException;
import java.util.Objects;

public class PolicyDeserializer extends StdDeserializer<Policy> {

    private ObjectMapper objectMapper;

    public PolicyDeserializer() {
        this(null);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.setDateFormat(ApiClient.buildDefaultDateFormat());
    }

    public PolicyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Policy deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        Policy policy = objectMapper.convertValue(node, Policy.class);

        switch (Objects.requireNonNull(policy.getType())) {
            case ACCESS_POLICY:
                return objectMapper.convertValue(node, AccessPolicy.class);
            case IDP_DISCOVERY:
                return objectMapper.convertValue(node, IdentityProviderPolicy.class);
            case MFA_ENROLL:
                return objectMapper.convertValue(node, MultifactorEnrollmentPolicy.class);
            case OAUTH_AUTHORIZATION_POLICY:
                return objectMapper.convertValue(node, AuthorizationServerPolicy.class);
            case OKTA_SIGN_ON:
                return objectMapper.convertValue(node, OktaSignOnPolicy.class);
            case PASSWORD:
                return objectMapper.convertValue(node, PasswordPolicy.class);
            case PROFILE_ENROLLMENT:
                return objectMapper.convertValue(node, ProfileEnrollmentPolicy.class);
        }

        return objectMapper.convertValue(node, Policy.class);
    }
}

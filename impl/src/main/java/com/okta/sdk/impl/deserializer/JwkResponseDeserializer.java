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
import com.okta.sdk.resource.model.ListJwk200ResponseInner;
import com.okta.sdk.resource.model.OAuthClientSecretLinks;

import java.io.IOException;

/**
 * Custom JSON deserializer for JWK responses.
 * 
 * The Okta API uses the 'use' field as a discriminator:
 * - "sig" → OAuth2ClientJsonSigningKeyResponse  
 * - "enc" → OAuth2ClientJsonEncryptionKeyResponse
 * 
 * Since the concrete classes don't extend ListJwk200ResponseInner,
 * Jackson's default polymorphic deserialization fails.
 * This deserializer bypasses the polymorphic handling and directly deserializes
 * into ListJwk200ResponseInner which has all the fields needed.
 */
public class JwkResponseDeserializer extends StdDeserializer<ListJwk200ResponseInner> {

    private static final long serialVersionUID = 1L;

    public JwkResponseDeserializer() {
        this(null);
    }

    public JwkResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ListJwk200ResponseInner deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        ListJwk200ResponseInner response = new ListJwk200ResponseInner();

        // Parse e
        if (node.has("e") && !node.get("e").isNull()) {
            response.setE(node.get("e").asText());
        }

        // Parse kty
        if (node.has("kty") && !node.get("kty").isNull()) {
            response.setKty(ListJwk200ResponseInner.KtyEnum.fromValue(node.get("kty").asText()));
        }

        // Parse n
        if (node.has("n") && !node.get("n").isNull()) {
            response.setN(node.get("n").asText());
        }

        // Parse use
        if (node.has("use") && !node.get("use").isNull()) {
            response.setUse(ListJwk200ResponseInner.UseEnum.fromValue(node.get("use").asText()));
        }

        // Parse kid
        if (node.has("kid") && !node.get("kid").isNull()) {
            response.setKid(node.get("kid").asText());
        }

        // Parse status
        if (node.has("status") && !node.get("status").isNull()) {
            response.setStatus(ListJwk200ResponseInner.StatusEnum.fromValue(node.get("status").asText()));
        }

        // Parse _links
        if (node.has("_links") && !node.get("_links").isNull()) {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            OAuthClientSecretLinks links = mapper.treeToValue(node.get("_links"), OAuthClientSecretLinks.class);
            response.setLinks(links);
        }

        return response;
    }
}

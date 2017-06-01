/*
 * Copyright 2017 Okta
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
package com.okta.sdk.impl.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.okta.sdk.impl.io.Resource;
import com.okta.sdk.lang.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONPropertiesSource implements PropertiesSource {

    private final Resource resource;

    public JSONPropertiesSource(Resource resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        this.resource = resource;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        try (InputStream in = resource.getInputStream()) {
            // InputStream is null when file is configured, but not found
            if (in != null) {
                getFlattenedMap("", new ObjectMapper().readTree(resource.getInputStream()), map);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read resource [" + resource + "]: " + e.getMessage(), e);
        }
        return map;
    }

    private void getFlattenedMap(String currentPath, JsonNode jsonNode, Map<String, String> map) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                getFlattenedMap(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                getFlattenedMap(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
    }
}

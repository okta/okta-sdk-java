/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.impl.ds;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.okta.sdk.impl.resource.AbstractResource;
import com.okta.sdk.impl.resource.ReferenceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 0.5.0
 */
public class JacksonMapMarshaller implements MapMarshaller {

    private ObjectMapper objectMapper;

    public JacksonMapMarshaller() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new AbstractResourceSerializer(AbstractResource.class));
        this.objectMapper.registerModule(simpleModule);
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isPrettyPrint() {
        return this.objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT);
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
    }

    @Override
    public void marshal(OutputStream out, Map map) {
        if (map == null) {
            throw new MarshalingException("Cannot convert null to JSON.");
        }

        try {
            this.objectMapper.writeValue(out, map);
        } catch (IOException e) {
            throw new MarshalingException("Unable to convert Map to JSON.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> unmarshal(InputStream marshalled, Map<String, String> linkMap) {
        try {
            Object resolvedObj = this.objectMapper.readValue(marshalled, Object.class);
            if (resolvedObj instanceof Map) {
                return (Map<String, Object>) resolvedObj;
            } else if (resolvedObj instanceof List) {
                List list = (List) resolvedObj;
                Map<String, Object> ret = new LinkedHashMap<>();
                ret.put("items", list);
                ret.put("nextPage", linkMap.get("next"));
                ret.put("href", "local");
                return ret;
            }
            throw new MarshalingException("Unable to convert InputStream String to Map. " +
                "Resolved Object is neither a Map or a List: " + resolvedObj.getClass());
        } catch (IOException e) {
            throw new MarshalingException("Unable to convert InputStream String to Map.", e);
        }
    }

    static class AbstractResourceSerializer extends StdSerializer<AbstractResource> {

        private static final long serialVersionUID = 42L;
        private final transient ResourceConverter resourceConverter = new DefaultResourceConverter(new ReferenceFactory());

        AbstractResourceSerializer(Class<AbstractResource> t) {
            super(t);
        }

        @Override
        public void serialize(AbstractResource resource,
                              JsonGenerator jgen,
                              SerializerProvider sp) throws IOException {
            jgen.writeObject(resourceConverter.convert(resource, false));
        }

        // method not used, but here to prevent java serialization PMD warnings
        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
        }

        // method not used, but here to prevent java serialization PMD warnings
        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
        }
    }
}
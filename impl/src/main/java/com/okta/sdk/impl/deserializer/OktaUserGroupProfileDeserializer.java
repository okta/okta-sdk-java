package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.okta.sdk.resource.model.GroupProfile;
import com.okta.sdk.resource.model.OktaUserGroupProfile;
import com.okta.sdk.resource.model.UserProfile;

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

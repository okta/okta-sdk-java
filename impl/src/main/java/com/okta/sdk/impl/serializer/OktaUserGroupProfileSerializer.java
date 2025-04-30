package com.okta.sdk.impl.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.okta.commons.lang.Strings;
import com.okta.sdk.resource.model.OktaUserGroupProfile;
import com.okta.sdk.resource.model.UserProfile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class OktaUserGroupProfileSerializer extends StdSerializer<OktaUserGroupProfile> {

    private static final long serialVersionUID = -1159312306772670042L;

    public OktaUserGroupProfileSerializer() {
        this(null);
    }

    public OktaUserGroupProfileSerializer(Class<OktaUserGroupProfile> t) {
        super(t);
    }

    @Override
    public void serialize(OktaUserGroupProfile oktaUserGroupProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        // non-nullable fields

        if (Strings.hasText(oktaUserGroupProfile.getName())) {
            jgen.writeStringField(OktaUserGroupProfile.JSON_PROPERTY_NAME, oktaUserGroupProfile.getName());
        }

        if (Strings.hasText(oktaUserGroupProfile.getDescription())) {
            jgen.writeStringField(OktaUserGroupProfile.JSON_PROPERTY_DESCRIPTION, oktaUserGroupProfile.getDescription());
        }
        Map<String, Object> additionalProperties = oktaUserGroupProfile.getAdditionalProperties();

        if (Objects.nonNull(additionalProperties) && !additionalProperties.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalProperties.entrySet()) {
                jgen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        jgen.writeEndObject();
    }
}

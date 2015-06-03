package com.okta.sdk.models.links;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class LinksUnionDeserializer extends StdDeserializer<LinksUnion> {
    public LinksUnionDeserializer() {
        super(LinksUnion.class);
    }

    @Override
    public LinksUnion deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode root = mapper.readTree(parser);
        if (root.isArray()) {
            return mapper.convertValue(root, Links.class);
        } else if (root.isObject()) {
            return mapper.convertValue(root, Link.class);
        }
        return null;
    }
}

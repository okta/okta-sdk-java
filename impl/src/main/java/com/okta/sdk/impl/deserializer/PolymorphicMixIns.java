package com.okta.sdk.impl.deserializer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.okta.sdk.resource.model.LogStream;
import com.okta.sdk.resource.model.LogStreamAws;
import com.okta.sdk.resource.model.LogStreamSplunk;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

/**
 * Mix-ins to add to polymorphic resources, so they are deserialized as the correct type.
 */
public interface PolymorphicMixIns {

    /**
     * Mix-ins to register in the ObjectMapper.
     */
    Map<Class<?>, Class<?>> MIX_INS = new HashMap<Class<?>, Class<?>>() {{
        put(LogStream.class, LogStreamMixIn.class);
    }};

    @JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, visible = true, property = "type")
    @JsonSubTypes({
            @Type(name = "aws_eventbridge", value = LogStreamAws.class),
            @Type(name = "splunk_cloud_logstreaming", value = LogStreamSplunk.class)
    })
    abstract class LogStreamMixIn {
    }

}

package com.okta.sdk.framework;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public abstract class JsonApiClient extends ApiClient {

    protected ObjectMapper objectMapper;

    public JsonApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected void initMarshaller() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule().addSerializer(DateTime.class, new CustomDateTimeSerializer()));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private class CustomDateTimeSerializer extends JsonSerializer<DateTime> {
        @Override
        public void serialize(DateTime value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            jsonGenerator.writeString(Utils.convertDateTimeToString(value));
        }
    }

    @Override
    protected  <T> T unmarshall(HttpResponse response, TypeReference<T> clazz) throws IOException {
        if (response.getEntity() == null || clazz.getType().equals(Void.class)) {
            EntityUtils.consume(response.getEntity());
            return null;
        }
        InputStream inputStream = response.getEntity().getContent();
        JsonParser parser = objectMapper.getFactory().createParser(inputStream);
        T toReturn = parser.readValueAs(clazz);
        EntityUtils.consume(response.getEntity());
        return toReturn;
    }

    @Override
    protected HttpEntity buildRequestEntity(Object object) throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);
        objectMapper.writeValue(generator, object);
        generator.close();
        writer.close();
        String string = writer.toString();
        return new StringEntity(string, "UTF-8");
    }

    @Override
    protected void setAcceptHeader(HttpUriRequest httpUriRequest) throws IOException {
        Header acceptHeader = new BasicHeader("Accept", "application/json");
        httpUriRequest.setHeader(acceptHeader);
    }

    @Override
    protected void setContentTypeHeader(HttpUriRequest httpUriRequest) throws IOException {
        Header contentTypeHeader = new BasicHeader("Content-type", "application/json");
        httpUriRequest.setHeader(contentTypeHeader);
    }
}

package com.okta.sdk.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Error;

public class ApiExceptionHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        //ensure new fields added to API errors won't cause issues
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    /**
     * Translates an {@link ApiException} to an {@link Error}
     *
     * @return the {@link Error} if present in the response body, null if not
     */
    public static Error getError(ApiException e) {
        try {
            return MAPPER.readValue(e.getResponseBody(), Error.class);
        } catch (JsonProcessingException jpe) {
            //ignore error, means the response body is not valid JSON
            return null;
        }
    }

}

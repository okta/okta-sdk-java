/*
 * Copyright 2022-Present Okta, Inc.
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
package com.okta.sdk.error;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorHandler implements ResponseErrorHandler {

    static final String ERROR_ID_PROPERTY = "errorId";
    static final String SUMMARY_PROPERTY = "errorSummary";
    static final String CAUSES_PROPERTY = "errorCauses";
    static final String HEADERS_PROPERTY = "errorHeaders";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
            httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException, ResourceException {

        final int statusCode = httpResponse.getRawStatusCode();
        final String message = new String(FileCopyUtils.copyToByteArray(httpResponse.getBody()));
        final Map<String, Object> errorMap = mapper.readValue(message, Map.class);

        Error error = new Error() {
            @Override
            public int getStatus() {
                return statusCode;
            }

            @Override
            public String getCode() {
                return String.valueOf(statusCode);
            }

            @Override
            public String getMessage() {
                return String.valueOf(errorMap.get(SUMMARY_PROPERTY));
            }

            @Override
            public String getId() {
                return String.valueOf(errorMap.get(ERROR_ID_PROPERTY));
            }

            @Override
            public List<ErrorCause> getCauses() {
                List<ErrorCause> results = new ArrayList<>();
                Object rawProp = errorMap.get(CAUSES_PROPERTY);
                if (rawProp instanceof List) {
                    ((List<Map<String, Object>>) rawProp).forEach(causeMap ->
                        results.add(new ErrorCause(String.valueOf(causeMap.get(SUMMARY_PROPERTY)))));
                }
                return Collections.unmodifiableList(results);
            }

            @Override
            public Map<String, List<String>> getHeaders() {
                Map<String, List<String>> results = new HashMap<>();
                Object rawProp = errorMap.get(HEADERS_PROPERTY);
                if (rawProp instanceof List) {
                    results.put(HEADERS_PROPERTY, (ArrayList) rawProp);
                }
                return Collections.unmodifiableMap(results);
            }
        };

        if (statusCode == 429) {
            // retry 429
            throw new RetryableException(error);
        }
        throw new ResourceException(error);
    }
}

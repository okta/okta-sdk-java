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
package com.okta.sdk.impl.openapivalidation;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class OpenApiValidationRequestInterceptor implements HttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiValidationRequestInterceptor.class);

    final OpenApiInteractionValidator validator = OpenApiInteractionValidator
        .createForSpecificationUrl("./../src/swagger/api.yaml")
        .build();

    @Override
    public void process(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext) throws IOException {
        logger.info("OpenApiValidationRequestInterceptor invoked");

        ClassicHttpRequest classicHttpRequest = (ClassicHttpRequest) httpRequest;

        SimpleRequest.Builder builder = new SimpleRequest.Builder(classicHttpRequest.getMethod(), classicHttpRequest.getRequestUri(), false);
        if (Objects.nonNull(classicHttpRequest.getEntity().getContent())) {
            String jsonRequest = IOUtils.toString(classicHttpRequest.getEntity().getContent(), Charsets.UTF_8);
            builder.withBody(jsonRequest);
        }

        final ValidationReport requestValidationReport = validator.validateRequest(builder.build());
        if (requestValidationReport.hasErrors()) {
            logger.error(SimpleValidationReportFormat.getInstance().apply(requestValidationReport));
        }
    }
}

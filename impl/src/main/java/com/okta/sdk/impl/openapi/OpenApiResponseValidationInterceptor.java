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
package com.okta.sdk.impl.openapi;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.JsonValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class OpenApiResponseValidationInterceptor implements HttpResponseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiResponseValidationInterceptor.class);

    private final OpenApiInteractionValidator validator;

    public OpenApiResponseValidationInterceptor(final String specPath) {
        validator = OpenApiInteractionValidator
            .createForSpecificationUrl(specPath)
            .build();
    }

    @Override
    public void process(HttpResponse httpResponse, EntityDetails entityDetails, HttpContext httpContext) throws IOException {

        logger.debug("OpenApiValidationResponseInterceptor invoked");

        ClassicHttpResponse classicHttpResponse = (ClassicHttpResponse) httpResponse;

        if (Objects.nonNull(classicHttpResponse.getEntity())) {

            String payload;

            if (Objects.isNull(classicHttpResponse.getEntity().getContentEncoding())) {
                payload = IOUtils.toString(classicHttpResponse.getEntity().getContent(), Charsets.UTF_8);
            } else {
                GZIPInputStream gis =
                    new GZIPInputStream(new ByteArrayInputStream(IOUtils.toByteArray(classicHttpResponse.getEntity().getContent())));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, Charsets.ISO_8859_1));
                payload = bufferedReader.lines().collect(Collectors.joining());
            }

            classicHttpResponse.setEntity(HttpEntities.create(payload));

            final Response response = SimpleResponse.Builder.ok()
                .withContentType(ContentType.APPLICATION_JSON.toString())
                .withBody(payload)
                .build();

            HttpCoreContext httpCoreContext = (HttpCoreContext) httpContext;

            ValidationReport responseValidationReport = validator.validateResponse(
                httpCoreContext.getRequest().getRequestUri(), Request.Method.valueOf(httpCoreContext.getRequest().getMethod()), response);

            if (responseValidationReport.hasErrors()) {
                logger.error(JsonValidationReportFormat.getInstance().apply(responseValidationReport));
            }
        }
    }
}

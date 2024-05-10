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
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class OpenApiValidationResponseInterceptor implements HttpResponseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiValidationResponseInterceptor.class);

    final OpenApiInteractionValidator validator = OpenApiInteractionValidator
        .createForSpecificationUrl("./../src/swagger/api.yaml")
        .build();

    @Override
    public void process(HttpResponse httpResponse, EntityDetails entityDetails, HttpContext httpContext) throws IOException {

        logger.info("OpenApiValidationResponseInterceptor invoked");

        ClassicHttpResponse classicHttpResponse = (ClassicHttpResponse) httpResponse;
        HttpCoreContext httpCoreContext = (HttpCoreContext) httpContext;

        if (Objects.nonNull(classicHttpResponse.getEntity())) {

            final Response resp = SimpleResponse.Builder.ok()
                .withContentType(ContentType.APPLICATION_JSON.toString())
                .withBody(classicHttpResponse.getEntity().getContent())
                .build();

            ValidationReport responseValidationReport = validator.validateResponse(
                httpCoreContext.getRequest().getRequestUri(), Request.Method.valueOf(httpCoreContext.getRequest().getMethod()), resp);
            if (responseValidationReport.hasErrors()) {
                logger.error(SimpleValidationReportFormat.getInstance().apply(responseValidationReport));
            }
        }
    }
}

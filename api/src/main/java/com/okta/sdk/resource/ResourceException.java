/*
 * Copyright 2014 Stormpath, Inc.
 * Modifications Copyright 2018 Okta, Inc.
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
package com.okta.sdk.resource;

import com.okta.sdk.error.Error;
import com.okta.sdk.error.ErrorCause;
import com.okta.sdk.lang.Assert;
import com.okta.sdk.lang.Collections;
import com.okta.sdk.lang.Strings;

import java.util.List;
import java.util.Map;

/**
 * A Runtime exception typically thrown when the remote server returns a non 20x response.
 * @since 0.5.0
 */
public class ResourceException extends RuntimeException implements Error {

    private final Error error;

    /**
     * Ensures the message used for the exception (i.e. exception.getMessage()) reports the {@code developerMessage}
     * returned by the Okta API Server.  The regular Okta response body {@code message} field is targeted
     * at application end-users that could very likely be non-technical.  Since an exception should be helpful to
     * developers, it is better to show a more technical message.
     * <p>
     * Added as a fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/28">Issue #28</a>.
     *
     * @param error the response Error. Cannot be null.
     * @return {@code error.getDeveloperMessage()}
     */
    private static String buildExceptionMessage(Error error) {
        Assert.notNull(error, "Error argument cannot be null.");
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ").append(error.getStatus())
          .append(", Okta ").append(error.getCode())
          .append(" (").append(error.getMessage());

        // if there is only one cause (most common) just include it, otherwise show the cause count
        int causeCount = Collections.size(error.getCauses());
        if (causeCount == 1) {
            sb.append(" - ").append(error.getCauses().get(0).getSummary());
        } else if (causeCount > 1) {
            sb.append(" - '").append(causeCount).append(" causes'");
        }
        sb.append(")");

        String errorId = error.getId();
        if (Strings.hasText(errorId)) {
            sb.append(", ErrorId ").append(errorId);
        }
        return sb.toString();
    }

    public ResourceException(Error error) {
        super(buildExceptionMessage(error));
        this.error = error;
    }

    @Override
    public int getStatus() {
        return error.getStatus();
    }

    /**
     * Get the Okta Error Code, <a href="https://developer.okta.com/reference/error_codes/">click here</a> for the
     * list of Okta error codes.
     *
     * @return the code of the error
     */
    @Override
    public String getCode() {
        return error.getCode();
    }


    @Override
    public String getId() {
        return error.getId();
    }

    @Override
    public List<ErrorCause> getCauses() {
        return error.getCauses();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return error.getHeaders();
    }

    /**
     * Returns the underlying REST {@link Error} returned from the Okta API server.
     * <p>
     * Because this class's {@link #getMessage() getMessage()} value returns a developer-friendly message to help you
     * debug when you see stack traces, you might want to acquire the underlying {@code Error} to show an end-user
     * the simpler end-user appropriate error message.  The end-user error message is non-technical in nature - as a
     * convenience, you can show this message directly to your application end-users.
     * <p>
     * For example:
     * <pre>
     * try {
     *
     *     //something that causes a ResourceException
     *
     * } catch (ResourceException re) {
     *
     *     String endUserMessage = re.getError().getMessage();
     *
     *     warningDialog.setText(endUserMessage);
     * }
     * </pre>
     *
     * @return the underlying REST {@link Error} resource representation returned from the Okta API server.
     */
    public Error getError() {
        return this.error;
    }
}

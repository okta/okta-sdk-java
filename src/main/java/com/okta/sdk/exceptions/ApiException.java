/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.exceptions;

import com.okta.sdk.framework.ErrorCause;
import com.okta.sdk.framework.ErrorResponse;

import java.io.IOException;
import java.util.List;

public class ApiException extends IOException {

    private int statusCode;
    private ErrorResponse errorResponse;

    public ApiException(int statusCode, ErrorResponse errorResponse) {
        super(String.format("ApiException - errorCode: %s | errorSummary: %s | errorId: %s | errorCauses: %s",
                errorResponse == null ? "" : errorResponse.getErrorCode(),
                errorResponse == null ? "" : errorResponse.getErrorSummary(),
                errorResponse == null ? "" : errorResponse.getErrorId(),
                errorResponse == null ? "" : printErrorCauses(errorResponse.getErrorCauses())));
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }

    private static String printErrorCauses(List<ErrorCause> errorCauses) {
        StringBuilder sb = new StringBuilder();
        if (!errorCauses.isEmpty()) {
            for (ErrorCause errorCause : errorCauses) {
                sb.append(System.getProperty("line.separator"));
                sb.append(errorCause.getErrorSummary());
            }
        }
        return sb.toString();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

}

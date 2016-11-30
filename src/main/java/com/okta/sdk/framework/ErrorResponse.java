/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.framework;

import java.util.LinkedList;
import java.util.List;

public class ErrorResponse {

    private String errorCode;
    private String errorSummary;
    private String errorLink;
    private String errorId;
    private List<ErrorCause> errorCauses;

    public ErrorResponse() {
        this.errorCauses = new LinkedList<ErrorCause>();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public String getErrorLink() {
        return errorLink;
    }

    public void setErrorLink(String errorLink) {
        this.errorLink = errorLink;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public List<ErrorCause> getErrorCauses() {
        return this.errorCauses;
    }

    public void setErrorCauses(List<ErrorCause> errorCauses) {
        this.errorCauses = errorCauses;
    }

    public void addCause(String cause) {
        ErrorCause errorCause = new ErrorCause();
        errorCause.setErrorSummary(cause);
        this.errorCauses.add(errorCause);
    }

}

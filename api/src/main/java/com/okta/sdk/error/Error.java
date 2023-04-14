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
package com.okta.sdk.error;

import java.util.List;
import java.util.Map;

/**
 * A detailed error object typically returned with a non 20x response.
 *
 * @since 0.5.0
 */
public interface Error {

    /**
     * Returns this error's HTTP status code.
     * @return the status code of this Error
     */
    int getStatus();

    /**
     * Returns the Okta specific error code.
     * @return an Okta specific error code
     */
    String getCode();

    /**
     * Returns the detail message string of this error.
     * @return message string of this error.
     */
    String getMessage();

    /**
     * Returns the error ID of this error. This maybe used when opening a support case and troubleshooting.
     * @return error ID of this error
     */
    String getId();

    /**
     * Returns the list of causes of this error. When validating a resource (for example a User) multiple validation
     * errors could occur.
     * @return A list of causes, which could be {code}null{code} or empty
     */
    List<ErrorCause> getCauses();

    /**
     * Returns the HTTP headers associated with this error response.
     * @return A list headers, which could be {code}null{code} or empty
     */
    Map<String, List<String>> getHeaders();
}

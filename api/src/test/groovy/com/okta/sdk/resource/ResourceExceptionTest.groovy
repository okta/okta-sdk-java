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
package com.okta.sdk.resource

import com.okta.sdk.error.ErrorCause
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 *
 * @since 0.5.0
 */
class ResourceExceptionTest {

    @Test
    void testDefault() {

        def error = new com.okta.sdk.error.Error() {

            int getStatus() {
                return 400
            }

            String getCode() {
                return "I2000"
            }

            String getMessage() {
                return 'foo'
            }

            @Override
            List<ErrorCause> getCauses() {
                return null
            }

            @Override
            Map<String, String[]> getHeaders() {
                return null
            }

            @Override
            String getId() {
                return  null
            }
        }

        def ex = new ResourceException(error)

        assertEquals ex.status, 400
        assertEquals ex.code, "I2000"
        assertEquals ex.message, 'HTTP 400, Okta I2000 (foo)'
        assertEquals ex.id, null
    }


}

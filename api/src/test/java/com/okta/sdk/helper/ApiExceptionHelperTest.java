/*
 * Copyright 2017-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.okta.sdk.helper;

import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.model.Error;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class ApiExceptionHelperTest {

    @Test
    public void testGetErrorWithNullResponseBody() {
        ApiException apiException = mock(ApiException.class);
        when(apiException.getResponseBody()).thenReturn(null);

        Error error = ApiExceptionHelper.getError(apiException);

        assertNull(error, "Error should be null when response body is null");
    }


    @Test
    public void testGetErrorWithInvalidJsonResponseBody() {
        ApiException apiException = mock(ApiException.class);
        String invalidJson = "This is not valid JSON";
        when(apiException.getResponseBody()).thenReturn(invalidJson);

        Error error = ApiExceptionHelper.getError(apiException);

        assertNull(error, "Error should be null for invalid JSON");
    }



    @Test
    public void testGetErrorWithEmptyJsonObject() {
        ApiException apiException = mock(ApiException.class);
        String emptyJson = "{}";
        when(apiException.getResponseBody()).thenReturn(emptyJson);

        Error error = ApiExceptionHelper.getError(apiException);

        assertNotNull(error, "Error should not be null for empty JSON object");
        assertNull(error.getErrorCode());
        assertNull(error.getErrorSummary());
    }
}

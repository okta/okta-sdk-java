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

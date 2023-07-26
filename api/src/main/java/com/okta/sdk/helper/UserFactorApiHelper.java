/*
 * Copyright 2023-Present Okta, Inc.
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
package com.okta.sdk.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.core5.http.HttpStatus;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.api.UserFactorApi;
import com.okta.sdk.resource.model.*;

import java.lang.reflect.Type;
import java.util.*;

import static com.okta.sdk.helper.HelperConstants.*;
import static com.okta.sdk.helper.HelperUtil.*;

/**
 * Helper class that enables working with sub-typed {@link UserFactor} references.
 */
public class UserFactorApiHelper<T extends UserFactor> extends UserFactorApi {

    public UserFactorApiHelper(UserFactorApi userFactorApi) {
        super(userFactorApi.getApiClient());
    }

    public UserFactorApiHelper(ApiClient apiClient) {
        super(apiClient);
    }
    public <T extends UserFactor> T activateFactorOfType(Class<T> classType,
                                                         String userId,
                                                         String factorId,
                                                         ActivateFactorRequest activateFactorRequest) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling activateFactor");
        }

        // verify the required parameter 'factorId' is set
        if (factorId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'factorId' when calling activateFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/{factorId}/lifecycle/activate"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId))
            .replaceAll("\\{" + "factorId" + "\\}", apiClient.escapeString(factorId));

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        return apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            new ArrayList<>(),
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            activateFactorRequest,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );
    }

    public <T extends UserFactor> T enrollFactorOfType(Class<T> classType, String userId, UserFactor userFactor,
                                                       Boolean updatePhone, String templateId,
                                                       Integer tokenLifetimeSeconds, Boolean activate) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling enrollFactor");
        }

        // verify the required parameter 'body' is set
        if (userFactor == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'body' when calling enrollFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId));

        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("updatePhone", updatePhone));
        localVarQueryParams.addAll(apiClient.parameterToPair("templateId", templateId));
        localVarQueryParams.addAll(apiClient.parameterToPair("tokenLifetimeSeconds", tokenLifetimeSeconds));
        localVarQueryParams.addAll(apiClient.parameterToPair("activate", activate));

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        T usrFactor = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            userFactor,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );

        return (T) getObjectMapper().convertValue(usrFactor, getUserFactorType(userFactor));
    }

    public <T extends UserFactor> T resendEnrollFactorOfType(Class<T> classType, String userId, String factorId,
                                                             UserFactor userFactor, String templateId) throws ApiException {

        ApiClient apiClient = getApiClient();

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userId' when calling resendEnrollFactor");
        }

        // verify the required parameter 'factorId' is set
        if (factorId == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'factorId' when calling resendEnrollFactor");
        }

        // verify the required parameter 'userFactor' is set
        if (userFactor == null) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "Missing the required parameter 'userFactor' when calling resendEnrollFactor");
        }

        // create path and map variables
        String localVarPath = "/api/v1/users/{userId}/factors/{factorId}/resend"
            .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId))
            .replaceAll("\\{" + "factorId" + "\\}", apiClient.escapeString(factorId));

        List<Pair> localVarQueryParams = new ArrayList<>(apiClient.parameterToPair("templateId", templateId));

        TypeReference<T> localVarReturnType = new TypeReference<T>() {
            @Override
            public Type getType() {
                return classType;
            }
        };

        T usrFactor = apiClient.invokeAPI(
            localVarPath,
            HttpMethod.POST.name(),
            localVarQueryParams,
            new ArrayList<>(),
            QUERY_STRING_JOINER.toString(),
            userFactor,
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            apiClient.selectHeaderAccept(MEDIA_TYPE),
            apiClient.selectHeaderContentType(MEDIA_TYPE),
            AUTH_NAMES,
            localVarReturnType
        );

        return (T) getObjectMapper().convertValue(usrFactor, getUserFactorType(usrFactor));
    }
}